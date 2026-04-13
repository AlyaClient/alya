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
