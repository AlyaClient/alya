package dev.thoq.lua;
import dev.thoq.Alya;
import dev.thoq.lua.api.LuaChatApi;
import dev.thoq.lua.api.LuaCombatApi;
import dev.thoq.lua.api.LuaCommandApi;
import dev.thoq.lua.api.LuaConfigApi;
import dev.thoq.lua.api.LuaEventApi;
import dev.thoq.lua.api.LuaMinecraftApi;
import dev.thoq.lua.api.LuaModuleApi;
import dev.thoq.lua.api.LuaMovementApi;
import dev.thoq.lua.api.LuaRenderApi;
import dev.thoq.lua.api.LuaTimerApi;
import dev.thoq.util.player.ChatUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public final class LuaEngine {
    private final Globals globals;
    private final List<String> loadedScripts = new ArrayList<>();
    private LuaEventApi eventApi;
    public LuaEngine() {
        globals = JsePlatform.standardGlobals();
        bindApi();
    }
    private void bindApi() {
        LuaTable alyaTable = new LuaTable();
        alyaTable.set("modules", new LuaModuleApi());
        eventApi = new LuaEventApi();
        alyaTable.set("events", eventApi);
        alyaTable.set("commands", new LuaCommandApi());
        alyaTable.set("config", new LuaConfigApi());
        alyaTable.set("chat", new LuaChatApi());
        alyaTable.set("movement", new LuaMovementApi());
        alyaTable.set("render", new LuaRenderApi());
        alyaTable.set("timer", new LuaTimerApi());
        alyaTable.set("mc", new LuaMinecraftApi());
        alyaTable.set("combat", new LuaCombatApi());
        alyaTable.set("reload", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                reload();
                return LuaValue.NIL;
            }
        });
        alyaTable.set("getName", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Alya.getName());
            }
        });
        alyaTable.set("getVersion", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Alya.getVersion());
            }
        });
        globals.set("alya", alyaTable);
        globals.set("loadScript", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue resourcePathValue) {
                final String resourcePath = resourcePathValue.tojstring();
                try {
                    final InputStream inputStream = LuaEngine.class.getResourceAsStream(resourcePath);
                    if (inputStream == null) {
                        Alya.getInstance().getLogger().error("Lua script not found: {}", resourcePath);
                        return LuaValue.NIL;
                    }
                    final LuaValue chunk = globals.load(new InputStreamReader(inputStream), resourcePath);
                    return chunk.call();
                } catch (final LuaError luaError) {
                    Alya.getInstance().getLogger().error("Lua error loading {}: {}", resourcePath,
                            luaError.getMessage());
                    return LuaValue.NIL;
                }
            }
        });
    }
    public void loadScript(final String resourcePath) {
        try {
            final String devDir = System.getProperty("alya.dev.resources");
            InputStream inputStream = null;
            if (devDir != null) {
                final File devFile = new File(devDir + resourcePath);
                if (devFile.exists()) {
                    inputStream = new FileInputStream(devFile);
                }
            }
            if (inputStream == null) {
                inputStream = LuaEngine.class.getResourceAsStream(resourcePath);
            }
            if (inputStream == null) {
                Alya.getInstance().getLogger().error("Lua script not found: {}", resourcePath);
                return;
            }
            final LuaValue chunk = globals.load(new InputStreamReader(inputStream), resourcePath);
            chunk.call();
            loadedScripts.add(resourcePath);
        } catch (final LuaError luaError) {
            Alya.getInstance().getLogger().error("Lua error in {}: {}", resourcePath, luaError.getMessage());
            ChatUtil.sendError("[Lua] " + resourcePath + ": " + luaError.getMessage());
        } catch (final Exception exception) {
            Alya.getInstance().getLogger().error("Error loading {}: {}", resourcePath, exception.getMessage());
        }
    }
    public void loadAll() {
        for (final String script : Alya.getInstance().getScripts()) {
            loadScript(String.format("/lua/%s", script));
        }
    }
    public void reload() {
        final Map<String, ModuleSnapshot> snapshots = new HashMap<>();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules()).stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .map(module -> (dev.thoq.lua.api.LuaModule) module)
                .forEach(module -> {
                    final Map<String, String> settingValues = new HashMap<>();
                    module.getSettings().forEach(s -> settingValues.put(s.getName(), s.getValueAsString()));
                    snapshots.put(module.getName(), new ModuleSnapshot(module.isEnabled(), module.getKeyCode(), settingValues));
                });
        eventApi.clearSubscriptions();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules()).stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .forEach(module -> {
                    if (module.isEnabled()) {
                        module.setEnabled(false);
                    }
                    Alya.getInstance().getEventBus().unsubscribe(module);
                    Alya.getInstance().getModuleManager().unregister(module);
                });
        Alya.getInstance().getCommandManager().getCommands().removeIf(
                command -> !(command.getClass().getName().startsWith("dev.thoq.command.commands")));
        loadedScripts.clear();
        loadAll();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules()).stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .map(module -> (dev.thoq.lua.api.LuaModule) module)
                .forEach(module -> {
                    final ModuleSnapshot snapshot = snapshots.get(module.getName());
                    if (snapshot == null) return;
                    module.setKeyCode(snapshot.keyCode);
                    module.getSettings().forEach(setting -> {
                        final String savedValue = snapshot.settingValues.get(setting.getName());
                        if (savedValue != null) {
                            try {
                                setting.setValueFromString(savedValue);
                            } catch (final Exception ignored) {
                            }
                        }
                    });
                    if (snapshot.enabled) {
                        module.setEnabled(true);
                    }
                });
    }
    private static final class ModuleSnapshot {
        final boolean enabled;
        final int keyCode;
        final Map<String, String> settingValues;
        ModuleSnapshot(final boolean enabled, final int keyCode, final Map<String, String> settingValues) {
            this.enabled = enabled;
            this.keyCode = keyCode;
            this.settingValues = settingValues;
        }
    }
    public Globals getGlobals() {
        return globals;
    }
    public List<String> getLoadedScripts() {
        return loadedScripts;
    }
}
