package dev.thoq.lua.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class LuaMathUtilApi extends LuaTable {

    public LuaMathUtilApi() {
        set("isBetween", new ThreeArgFunction() {
            @Override
            public LuaValue call(final LuaValue arg1, final LuaValue arg2, final LuaValue arg3) {
                final double value = arg1.todouble();
                final double min = arg2.todouble();
                final double max = arg3.todouble();
                return LuaValue.valueOf(value >= min && value <= max);
            }
        });
    }


}
