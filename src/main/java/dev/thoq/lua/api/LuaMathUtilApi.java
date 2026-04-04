package dev.thoq.lua.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.security.SecureRandom;

public final class LuaMathUtilApi extends LuaTable {

    final SecureRandom secureRandom = new SecureRandom();

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
        set("makeRandom", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(secureRandom.nextDouble());
            }
        });
    }


}
