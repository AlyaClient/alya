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

import dev.thoq.util.movement.MovementUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaMovementApi extends LuaTable {
    public LuaMovementApi() {
        set(
                "isMoving",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.isMoving());
                    }
                });
        set(
                "jump",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        MovementUtil.jump();
                        return LuaValue.NIL;
                    }
                });
        set(
                "getMoveYaw",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.getMoveYaw());
                    }
                });
        set(
                "getMoveSpeed",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.getMoveSpeed());
                    }
                });
        set(
                "getDirection",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.getDirection());
                    }
                });
        set(
                "getSpeed",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.getSpeed());
                    }
                });
        set(
                "getAllowedHDistNCP",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(MovementUtil.getAllowedHDistNCP());
                    }
                });
        set(
                "setSpeed",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue speedValue) {
                        MovementUtil.setSpeed(speedValue.todouble());
                        return LuaValue.NIL;
                    }
                });
        set(
                "setSpeedStrafe",
                new TwoArgFunction() {
                    @Override
                    public LuaValue call(LuaValue speedValue, LuaValue strafePercentage) {
                        MovementUtil.setSpeed(speedValue.todouble(), strafePercentage.todouble());
                        return LuaValue.NIL;
                    }
                });
        set("SPRINT_SPEED", LuaValue.valueOf(MovementUtil.SPRINT_SPEED));
        set("WALK_SPEED", LuaValue.valueOf(MovementUtil.WALK_SPEED));
    }
}
