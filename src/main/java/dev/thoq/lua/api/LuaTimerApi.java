package dev.thoq.lua.api;

import dev.thoq.util.misc.TimerUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaTimerApi extends LuaTable {
    public LuaTimerApi() {
        set(
                "create",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        final TimerUtil timerUtil = new TimerUtil();
                        final LuaTable timerTable = new LuaTable();
                        timerTable.set(
                                "reset",
                                new ZeroArgFunction() {
                                    @Override
                                    public LuaValue call() {
                                        timerUtil.reset();
                                        return LuaValue.NIL;
                                    }
                                });
                        timerTable.set(
                                "getTime",
                                new ZeroArgFunction() {
                                    @Override
                                    public LuaValue call() {
                                        return LuaValue.valueOf(timerUtil.getTime());
                                    }
                                });
                        timerTable.set(
                                "hasElapsed",
                                new OneArgFunction() {
                                    @Override
                                    public LuaValue call(LuaValue milliseconds) {
                                        return LuaValue.valueOf(timerUtil.hasTimeElapsed(milliseconds.tolong()));
                                    }
                                });
                        timerTable.set(
                                "hasElapsedAndReset",
                                new TwoArgFunction() {
                                    @Override
                                    public LuaValue call(LuaValue milliseconds, LuaValue shouldReset) {
                                        return LuaValue.valueOf(
                                                timerUtil.hasTimeElapsed(milliseconds.tolong(), shouldReset.toboolean()));
                                    }
                                });
                        return timerTable;
                    }
                });
    }
}
