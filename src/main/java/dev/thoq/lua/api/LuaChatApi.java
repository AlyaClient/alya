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
