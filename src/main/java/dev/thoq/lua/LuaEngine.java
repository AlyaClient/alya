package dev.thoq.lua;

import dev.thoq.Alya;
import dev.thoq.lua.api.LuaChatApi;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
            final InputStream inputStream = LuaEngine.class.getResourceAsStream(resourcePath);
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
        }
    }

    public void loadAll() {
        for (final String script : Alya.getInstance().getScripts()) {
            loadScript(String.format("/lua/%s", script));
        }
    }

    public void reload() {
        eventApi.clearSubscriptions();
        new java.util.ArrayList<>(Alya.getInstance().getModuleManager().getModules()).stream()
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
    }

    public Globals getGlobals() {
        return globals;
    }

    public List<String> getLoadedScripts() {
        return loadedScripts;
    }
}
