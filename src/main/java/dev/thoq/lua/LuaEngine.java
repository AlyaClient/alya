package dev.thoq.lua;

import dev.thoq.Alya;
import dev.thoq.gui.toast.Toast;
import dev.thoq.gui.toast.ToastManager;
import dev.thoq.lua.api.*;
import dev.thoq.util.player.ChatUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.util.*;

@SuppressWarnings({"unused", "LoggingSimilarMessage"})
public final class LuaEngine {

    private final Globals globals;
    private final List<String> loadedScripts = new ArrayList<>();
    private final Set<String> loadedExternalScripts = new LinkedHashSet<>();
    private LuaEventApi eventApi;

    public LuaEngine() {
        globals = JsePlatform.standardGlobals();
        bindApi();
    }

    private void bindApi() {
        eventApi = new LuaEventApi();

        final LuaTable alyaTable = new LuaTable();
        alyaTable.set("modules", new LuaModuleApi());
        alyaTable.set("events", eventApi);
        alyaTable.set("commands", new LuaCommandApi());
        alyaTable.set("config", new LuaConfigApi());
        alyaTable.set("chat", new LuaChatApi());
        alyaTable.set("movement", new LuaMovementApi());
        alyaTable.set("visual", new LuaRenderApi());
        alyaTable.set("timer", new LuaTimerApi());
        alyaTable.set("mc", new LuaMinecraftApi());
        alyaTable.set("combat", new LuaCombatApi());
        alyaTable.set("mathutil", new LuaMathUtilApi());
        alyaTable.set(
                "reload",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        reload();
                        return LuaValue.NIL;
                    }
                });
        alyaTable.set(
                "getName",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(Alya.getName());
                    }
                });
        alyaTable.set(
                "getVersion",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(Alya.getVersion());
                    }
                });
        globals.set("alya", alyaTable);
        globals.set(
                "loadScript",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue resourcePathValue) {
                        final String resourcePath = resourcePathValue.tojstring();
                        try {
                            final String devDir = System.getProperty("client.dev.resources");
                            InputStream inputStream = null;
                            if(devDir != null) {
                                final File devFile = new File(devDir + resourcePath);
                                if(devFile.exists()) {
                                    inputStream = new FileInputStream(devFile);
                                }
                            }
                            if(inputStream == null) {
                                inputStream = LuaEngine.class.getResourceAsStream(resourcePath);
                            }
                            if(inputStream == null) {
                                Alya.getInstance().getLogger().error("Lua script not found: {}", resourcePath);
                                return LuaValue.NIL;
                            }
                            final LuaValue chunk = globals.load(new InputStreamReader(inputStream), resourcePath);
                            return chunk.call();
                        } catch(final LuaError | FileNotFoundException luaError) {
                            Alya.getInstance()
                                    .getLogger()
                                    .error("Lua error loading {}: {}", resourcePath, luaError.getMessage());
                            return LuaValue.NIL;
                        }
                    }
                });
    }

    public void loadScript(final String resourcePath) {
        try {
            final String devDir = System.getProperty("alya.dev.resources");
            InputStream inputStream = null;
            if(devDir != null) {
                final File devFile = new File(devDir + resourcePath);
                if(devFile.exists()) {
                    inputStream = new FileInputStream(devFile);
                }
            }
            if(inputStream == null) {
                inputStream = LuaEngine.class.getResourceAsStream(resourcePath);
            }
            if(inputStream == null) {
                Alya.getInstance().getLogger().error("Lua script not found: {}", resourcePath);
                return;
            }
            final LuaValue chunk = globals.load(new InputStreamReader(inputStream), resourcePath);
            chunk.call();
            loadedScripts.add(resourcePath);
        } catch(final LuaError luaError) {
            Alya.getInstance()
                    .getLogger()
                    .error("Lua error in {}: {}", resourcePath, luaError.getMessage());
            ChatUtil.sendError("[Lua] " + resourcePath + ": " + luaError.getMessage());
        } catch(final Exception exception) {
            Alya.getInstance()
                    .getLogger()
                    .error("Error loading {}: {}", resourcePath, exception.getMessage());
        }
    }

    public void loadAll() {
        for(final String script : Alya.getInstance().getScripts()) {
            loadScript(String.format("/lua/%s", script));
        }
        loadExternalScripts();
    }

    public void loadExternalScripts() {
        final File scriptsDir = new File(
                net.minecraft.client.Minecraft.getMinecraft().mcDataDir,
                Alya.getName() + "/scripts");
        if(!scriptsDir.exists()) {
            if(!scriptsDir.mkdirs()) {
                Alya.getInstance().getLogger().error("Failed to create scripts directory");
            }
            return;
        }
        final File[] luaFiles = scriptsDir.listFiles((dir, name) -> name.endsWith(".lua"));
        if(luaFiles == null) return;
        for(final File luaFile : luaFiles) {
            if(loadedExternalScripts.contains(luaFile.getName())) {
                loadExternalScript(luaFile);
            }
        }
    }

    public void loadExternalScript(final File file) {
        try(final FileInputStream fis = new FileInputStream(file)) {
            final LuaValue chunk = globals.load(new InputStreamReader(fis), file.getName());
            chunk.call();
            loadedScripts.add("external:" + file.getName());
            loadedExternalScripts.add(file.getName());
            Alya.getInstance().getLogger().info("Loaded external script: {}", file.getName());
        } catch(final LuaError luaError) {
            Alya.getInstance().getLogger().error("Lua error in {}: {}", file.getName(), luaError.getMessage());
            ChatUtil.sendError("[Lua] " + file.getName() + ": " + luaError.getMessage());
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Error loading {}: {}", file.getName(), exception.getMessage());
        }
    }

    public void reload() {
        final Map<String, ModuleSnapshot> snapshots = new HashMap<>();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules())
                .stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .map(module -> (dev.thoq.lua.api.LuaModule) module)
                .forEach(
                        module -> {
                            final Map<String, String> settingValues = new HashMap<>();
                            module
                                    .getSettings()
                                    .forEach(setting -> settingValues.put(setting.getName(), setting.getValueAsString()));
                            snapshots.put(
                                    module.getName(),
                                    new ModuleSnapshot(module.isEnabled(), module.getKeyCode(), settingValues));
                        });
        eventApi.clearSubscriptions();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules())
                .stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .forEach(
                        module -> {
                            if(module.isEnabled()) {
                                module.setEnabled(false);
                            }
                            Alya.getInstance().getEventBus().unsubscribe(module);
                            Alya.getInstance().getModuleManager().unregister(module);
                        });
        Alya.getInstance()
                .getCommandManager()
                .getCommands()
                .removeIf(
                        command -> !(command.getClass().getName().startsWith("dev.thoq.command.commands")));
        loadedScripts.clear();
        loadAll();
        new ArrayList<>(Alya.getInstance().getModuleManager().getModules())
                .stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .map(module -> (dev.thoq.lua.api.LuaModule) module)
                .forEach(
                        module -> {
                            final ModuleSnapshot snapshot = snapshots.get(module.getName());
                            if(snapshot == null) {
                                return;
                            }
                            module.setKeyCode(snapshot.keyCode());
                            module
                                    .getSettings()
                                    .forEach(
                                            setting -> {
                                                final String savedValue = snapshot.settingValues().get(setting.getName());
                                                if(savedValue != null) {
                                                    try {
                                                        setting.setValueFromString(savedValue);
                                                    } catch(final Exception ignored) {
                                                    }
                                                }
                                            });
                            if(snapshot.enabled()) {
                                module.setEnabled(true);
                            }
                        });
    }

    public boolean isExternalScriptLoaded(final String fileName) {
        return loadedExternalScripts.contains(fileName);
    }

    public void toggleExternalScript(final String fileName) {
        final boolean wasLoaded = loadedExternalScripts.contains(fileName);
        if(wasLoaded) {
            loadedExternalScripts.remove(fileName);
        } else {
            loadedExternalScripts.add(fileName);
        }
        reload();
        final String action = wasLoaded ? "Disabled" : "Enabled";
        ToastManager.getInstance().push(Toast.Type.INFO, Toast.Side.LEFT, "Scripts", action + " script: " + fileName);
    }

    public Set<String> getLoadedExternalScripts() {
        return loadedExternalScripts;
    }

    public Globals getGlobals() {
        return globals;
    }

    public List<String> getLoadedScripts() {
        return loadedScripts;
    }


}
