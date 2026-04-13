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

import bypass.util.render.RenderUtility;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class LuaRenderApi extends LuaTable {
    public LuaRenderApi() {
        set(
                "drawRect",
                new VarArgFunction() {
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
        set(
                "drawRectAbsolute",
                new VarArgFunction() {
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
        set(
                "drawRectOutline",
                new VarArgFunction() {
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
        set(
                "drawArc",
                new VarArgFunction() {
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
        set(
                "drawCircle",
                new VarArgFunction() {
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
        set(
                "drawHorizontalGradient",
                new VarArgFunction() {
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
        set(
                "drawVerticalGradient",
                new VarArgFunction() {
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
        set(
                "drawLine",
                new VarArgFunction() {
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
        set(
                "worldToScreen",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        float[] result =
                                RenderUtility.worldToScreen(
                                        arguments.arg(1).todouble(),
                                        arguments.arg(2).todouble(),
                                        arguments.arg(3).todouble());
                        if(result == null) {
                            return LuaValue.NIL;
                        }
                        LuaTable t = new LuaTable();
                        t.set("x", LuaValue.valueOf(result[0]));
                        t.set("y", LuaValue.valueOf(result[1]));
                        return t;
                    }
                });
        set(
                "drawBox3D",
                new VarArgFunction() {
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
        set(
                "drawCircle3D",
                new VarArgFunction() {
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
        set(
                "renderEntityChams",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        RenderUtility.renderEntityChams(
                                arguments.arg(1).toint(),
                                arguments.arg(2).tofloat(),
                                arguments.arg(3).tofloat(),
                                arguments.arg(4).tofloat(),
                                arguments.arg(5).tofloat());
                        return LuaValue.NIL;
                    }
                });
        set(
                "renderEntityOutline",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        RenderUtility.renderEntityOutline(
                                arguments.arg(1).toint(),
                                arguments.arg(2).tofloat(),
                                arguments.arg(3).tofloat(),
                                arguments.arg(4).tofloat(),
                                arguments.arg(5).tofloat(),
                                arguments.arg(6).tofloat());
                        return LuaValue.NIL;
                    }
                });
        set(
                "toARGB",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        return LuaValue.valueOf(
                                RenderUtility.toARGB(
                                        arguments.arg(1).toint(),
                                        arguments.arg(2).toint(),
                                        arguments.arg(3).toint(),
                                        arguments.arg(4).toint()));
                    }
                });
        set(
                "toRGB",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        return LuaValue.valueOf(
                                RenderUtility.toRGB(
                                        arguments.arg(1).toint(), arguments.arg(2).toint(), arguments.arg(3).toint()));
                    }
                });
        set(
                "withAlpha",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        return LuaValue.valueOf(
                                RenderUtility.withAlpha(arguments.arg(1).toint(), arguments.arg(2).toint()));
                    }
                });
    }
}
