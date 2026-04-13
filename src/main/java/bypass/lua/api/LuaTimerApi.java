/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.lua.api;

import bypass.util.misc.TimerUtil;
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
