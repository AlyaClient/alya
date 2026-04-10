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

package dev.thoq.lua.api;

import dev.thoq.Alya;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaConfigApi extends LuaTable {
    public LuaConfigApi() {
        set(
                "save",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue configNameValue) {
                        if(configNameValue.isnil()) {
                            Alya.getInstance().getConfigManager().save();
                        } else {
                            Alya.getInstance().getConfigManager().save(configNameValue.tojstring());
                        }
                        return LuaValue.NIL;
                    }
                });
        set(
                "load",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue configNameValue) {
                        if(configNameValue.isnil()) {
                            Alya.getInstance().getConfigManager().load();
                        } else {
                            Alya.getInstance().getConfigManager().load(configNameValue.tojstring());
                        }
                        return LuaValue.NIL;
                    }
                });
        set(
                "exists",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue configNameValue) {
                        return LuaValue.valueOf(
                                Alya.getInstance().getConfigManager().configExists(configNameValue.tojstring()));
                    }
                });
        set(
                "getNames",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        String[] configNames = Alya.getInstance().getConfigManager().getConfigNames();
                        LuaTable namesTable = new LuaTable();
                        for(int nameIndex = 0; nameIndex < configNames.length; nameIndex++) {
                            namesTable.set(nameIndex + 1, LuaValue.valueOf(configNames[nameIndex]));
                        }
                        return namesTable;
                    }
                });
    }
}
