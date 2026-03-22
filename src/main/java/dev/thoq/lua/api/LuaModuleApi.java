package dev.thoq.lua.api;

import dev.thoq.Alya;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Optional;

public final class LuaModuleApi extends LuaTable {

    public LuaModuleApi() {
        set("register", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue nameValue, LuaValue descriptionValue, LuaValue categoryString) {
                final String categoryName = categoryString.tojstring().toUpperCase();
                Category category;
                try {
                    category = Category.valueOf(categoryName);
                } catch (IllegalArgumentException illegalArgumentException) {
                    category = Category.OTHER;
                }
                final LuaModule luaModule = new LuaModule(nameValue.tojstring(), descriptionValue.tojstring(),
                        category);
                Alya.getInstance().getModuleManager().register(luaModule);
                return luaModule.getLuaTable();
            }
        });

        set("get", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue nameValue) {
                Optional<Module> optionalModule = Alya.getInstance().getModuleManager()
                        .getModule(nameValue.tojstring());
                if (optionalModule.isPresent() && optionalModule.get() instanceof LuaModule) {
                    return ((LuaModule) optionalModule.get()).getLuaTable();
                }
                return LuaValue.NIL;
            }
        });

        set("getAll", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable resultTable = new LuaTable();
                int insertionIndex = 1;
                for (Module currentModule : Alya.getInstance().getModuleManager().getModules()) {
                    if (currentModule instanceof LuaModule) {
                        resultTable.set(insertionIndex++, ((LuaModule) currentModule).getLuaTable());
                    }
                }
                return resultTable;
            }
        });

        set("getEnabled", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable resultTable = new LuaTable();
                int insertionIndex = 1;
                for (Module currentModule : Alya.getInstance().getModuleManager().getEnabledModules()) {
                    if (currentModule instanceof LuaModule) {
                        resultTable.set(insertionIndex++, ((LuaModule) currentModule).getLuaTable());
                    }
                }
                return resultTable;
            }
        });

        set("getByCategory", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue categoryString) {
                Category category;
                try {
                    category = Category.valueOf(categoryString.tojstring().toUpperCase());
                } catch (IllegalArgumentException illegalArgumentException) {
                    return new LuaTable();
                }
                LuaTable resultTable = new LuaTable();
                int insertionIndex = 1;
                for (Module currentModule : Alya.getInstance().getModuleManager().getModulesByCategory(category)) {
                    if (currentModule instanceof LuaModule) {
                        resultTable.set(insertionIndex++, ((LuaModule) currentModule).getLuaTable());
                    }
                }
                return resultTable;
            }
        });
    }
}
