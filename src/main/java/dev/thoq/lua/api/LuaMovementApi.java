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
            return LuaValue.valueOf((double) MovementUtil.getMoveYaw());
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
            return LuaValue.valueOf((double) MovementUtil.getSpeed());
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
    set("SPRINT_SPEED", LuaValue.valueOf((double) MovementUtil.SPRINT_SPEED));
    set("WALK_SPEED", LuaValue.valueOf((double) MovementUtil.WALK_SPEED));
  }
}
