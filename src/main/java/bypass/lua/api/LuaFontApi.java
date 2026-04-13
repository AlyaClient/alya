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

import bypass.Alya;
import bypass.util.font.AlyaFontRenderer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaFontApi {

    public static void bind(Globals globals) {
        globals.set(
                "getFontRenderer",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return buildTable(Alya.getInstance().getFontRenderer());
                    }
                });
        globals.set(
                "getFontRendererSmall",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return buildTable(Alya.getInstance().getFontRendererSmall());
                    }
                });
        globals.set(
                "getFontRendererMedium",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return buildTable(Alya.getInstance().getFontRendererMedium());
                    }
                });
        globals.set(
                "getFontRendererBold",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return buildTable(Alya.getInstance().getFontRendererBold());
                    }
                });
        globals.set(
                "getFontRendererTitle",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return buildTable(Alya.getInstance().getFontRendererTitle());
                    }
                });

        LuaTable alyaTable = (LuaTable) globals.get("alya");
        alyaTable.set("getFontRenderer", globals.get("getFontRenderer"));
        alyaTable.set("getFontRendererSmall", globals.get("getFontRendererSmall"));
        alyaTable.set("getFontRendererMedium", globals.get("getFontRendererMedium"));
        alyaTable.set("getFontRendererBold", globals.get("getFontRendererBold"));
        alyaTable.set("getFontRendererTitle", globals.get("getFontRendererTitle"));
    }

    private static LuaTable buildTable(AlyaFontRenderer renderer) {
        LuaTable table = new LuaTable();
        table.set(
                "drawString",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        renderer.drawString(
                                args.arg(1).tojstring(),
                                args.arg(2).tofloat(),
                                args.arg(3).tofloat(),
                                args.arg(4).toint());
                        return LuaValue.NIL;
                    }
                });
        table.set(
                "drawStringWithShadow",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        renderer.drawStringWithShadow(
                                args.arg(1).tojstring(),
                                args.arg(2).tofloat(),
                                args.arg(3).tofloat(),
                                args.arg(4).toint());
                        return LuaValue.NIL;
                    }
                });
        table.set(
                "getStringWidth",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue text) {
                        return LuaValue.valueOf((double) renderer.getStringWidth(text.tojstring()));
                    }
                });
        table.set(
                "getFontHeight",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf((double) renderer.getFontHeight());
                    }
                });
        return table;
    }
}
