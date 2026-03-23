package dev.thoq.lua.api;
import dev.thoq.Alya;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
public final class LuaConfigApi extends LuaTable {
    public LuaConfigApi() {
        set("save", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue configNameValue) {
                if (configNameValue.isnil()) {
                    Alya.getInstance().getConfigManager().save();
                } else {
                    Alya.getInstance().getConfigManager().save(configNameValue.tojstring());
                }
                return LuaValue.NIL;
            }
        });
        set("load", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue configNameValue) {
                if (configNameValue.isnil()) {
                    Alya.getInstance().getConfigManager().load();
                } else {
                    Alya.getInstance().getConfigManager().load(configNameValue.tojstring());
                }
                return LuaValue.NIL;
            }
        });
        set("exists", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue configNameValue) {
                return LuaValue
                        .valueOf(Alya.getInstance().getConfigManager().configExists(configNameValue.tojstring()));
            }
        });
        set("getNames", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                String[] configNames = Alya.getInstance().getConfigManager().getConfigNames();
                LuaTable namesTable = new LuaTable();
                for (int nameIndex = 0; nameIndex < configNames.length; nameIndex++) {
                    namesTable.set(nameIndex + 1, LuaValue.valueOf(configNames[nameIndex]));
                }
                return namesTable;
            }
        });
    }
}
