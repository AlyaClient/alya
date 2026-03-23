package dev.thoq.lua.api;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import dev.thoq.util.render.RenderUtility;
public final class LuaRenderApi extends LuaTable {
    public LuaRenderApi() {
        set("drawRect", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawRect(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).toint());
                return LuaValue.NIL;
            }
        });
        set("drawRectAbsolute", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawRectAbsolute(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).toint());
                return LuaValue.NIL;
            }
        });
        set("drawRectOutline", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawRectOutline(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).toint(),
                        arguments.arg(6).tofloat());
                return LuaValue.NIL;
            }
        });
        set("drawRoundedRect", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawRoundedRect(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).tofloat(),
                        arguments.arg(6).toint());
                return LuaValue.NIL;
            }
        });
        set("drawArc", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawArc(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).toint(),
                        arguments.arg(5).toint(),
                        arguments.arg(6).toint());
                return LuaValue.NIL;
            }
        });
        set("drawCircle", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawCircle(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).toint());
                return LuaValue.NIL;
            }
        });
        set("drawHorizontalGradient", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawHorizontalGradient(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).toint(),
                        arguments.arg(6).toint());
                return LuaValue.NIL;
            }
        });
        set("drawVerticalGradient", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawVerticalGradient(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).toint(),
                        arguments.arg(6).toint());
                return LuaValue.NIL;
            }
        });
        set("drawLine", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawLine(
                        arguments.arg(1).tofloat(),
                        arguments.arg(2).tofloat(),
                        arguments.arg(3).tofloat(),
                        arguments.arg(4).tofloat(),
                        arguments.arg(5).tofloat(),
                        arguments.arg(6).toint());
                return LuaValue.NIL;
            }
        });
        set("worldToScreen", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                float[] result = RenderUtility.worldToScreen(
                        arguments.arg(1).todouble(),
                        arguments.arg(2).todouble(),
                        arguments.arg(3).todouble());
                if (result == null) return LuaValue.NIL;
                LuaTable t = new LuaTable();
                t.set("x", LuaValue.valueOf((double) result[0]));
                t.set("y", LuaValue.valueOf((double) result[1]));
                return t;
            }
        });
        set("drawBox3D", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawBox3D(
                        arguments.arg(1).todouble(),
                        arguments.arg(2).todouble(),
                        arguments.arg(3).todouble(),
                        arguments.arg(4).todouble(),
                        arguments.arg(5).todouble(),
                        arguments.arg(6).toint(),
                        arguments.arg(7).tofloat()); 
                return LuaValue.NIL;
            }
        });
        set("drawCircle3D", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                RenderUtility.drawCircle3D(
                        arguments.arg(1).todouble(),
                        arguments.arg(2).todouble(),
                        arguments.arg(3).todouble(),
                        arguments.arg(4).todouble(),
                        arguments.arg(5).toint(),
                        arguments.arg(6).toint(),
                        arguments.arg(7).tofloat());
                return LuaValue.NIL;
            }
        });
        set("toARGB", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                return LuaValue.valueOf(RenderUtility.toARGB(
                        arguments.arg(1).toint(),
                        arguments.arg(2).toint(),
                        arguments.arg(3).toint(),
                        arguments.arg(4).toint()));
            }
        });
        set("toRGB", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                return LuaValue.valueOf(RenderUtility.toRGB(
                        arguments.arg(1).toint(),
                        arguments.arg(2).toint(),
                        arguments.arg(3).toint()));
            }
        });
        set("withAlpha", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                return LuaValue.valueOf(RenderUtility.withAlpha(
                        arguments.arg(1).toint(),
                        arguments.arg(2).toint()));
            }
        });
    }
}
