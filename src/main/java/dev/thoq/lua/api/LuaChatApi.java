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

import dev.thoq.util.player.ChatUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class LuaChatApi extends LuaTable {
    public LuaChatApi() {
        set(
                "info",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue messageValue) {
                        ChatUtil.sendInfo(messageValue.tojstring());
                        return LuaValue.NIL;
                    }
                });
        set(
                "success",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue messageValue) {
                        ChatUtil.sendSuccess(messageValue.tojstring());
                        return LuaValue.NIL;
                    }
                });
        set(
                "error",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue messageValue) {
                        ChatUtil.sendError(messageValue.tojstring());
                        return LuaValue.NIL;
                    }
                });
        set(
                "warning",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue messageValue) {
                        ChatUtil.sendWarning(messageValue.tojstring());
                        return LuaValue.NIL;
                    }
                });
        set(
                "raw",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue messageValue) {
                        ChatUtil.sendRaw(messageValue.tojstring());
                        return LuaValue.NIL;
                    }
                });
    }
}
