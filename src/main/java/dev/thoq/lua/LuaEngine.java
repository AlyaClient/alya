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

    public LuaEngine() {
        globals = JsePlatform.standardGlobals();
        bindApi();
    }

    private void bindApi() {
        LuaTable alyaTable = new LuaTable();
        alyaTable.set("modules", new LuaModuleApi());
        alyaTable.set("events", new LuaEventApi());
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
        }
    }

    public void loadAll() {
        //todo: make more elegant idk
        loadScript("/lua/util/movement.lua");
        loadScript("/lua/util/chat.lua");
        loadScript("/lua/util/timer.lua");
        loadScript("/lua/util/render.lua");
        loadScript("/lua/core/setting.lua");
        loadScript("/lua/core/module.lua");
        loadScript("/lua/core/submodule.lua");
        loadScript("/lua/core/command.lua");
        loadScript("/lua/modules/render/fullbright.lua");
        loadScript("/lua/modules/render/arraylist.lua");
        loadScript("/lua/modules/render/ambience.lua");
        loadScript("/lua/modules/movement/flight.lua");
        loadScript("/lua/modules/movement/speed.lua");
        loadScript("/lua/modules/player/sprint.lua");
        loadScript("/lua/modules/player/nojumpdelay.lua");
        loadScript("/lua/modules/player/norightclickdelay.lua");
        loadScript("/lua/modules/misc/disabler.lua");
        loadScript("/lua/modules/world/timer.lua");
        loadScript("/lua/commands/help.lua");
        loadScript("/lua/commands/bind.lua");
        loadScript("/lua/commands/config.lua");
        loadScript("/lua/commands/reload.lua");
    }

    public void reload() {
        new java.util.ArrayList<>(Alya.getInstance().getModuleManager().getModules()).stream()
                .filter(module -> module instanceof dev.thoq.lua.api.LuaModule)
                .forEach(module -> {
                    if (module.isEnabled()) {
                        module.setEnabled(false);
                    }
                    Alya.getInstance().getEventBus().unsubscribe(module);
                });
        Alya.getInstance().getModuleManager().getModules()
                .removeIf(module -> module instanceof dev.thoq.lua.api.LuaModule);
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
