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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class LuaRegexApi extends LuaTable {

    public LuaRegexApi() {
        set("match", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue patternValue, LuaValue textValue) {
                try {
                    Pattern pattern = Pattern.compile(patternValue.tojstring());
                    Matcher matcher = pattern.matcher(textValue.tojstring());
                    return LuaValue.valueOf(matcher.find());
                } catch (PatternSyntaxException e) {
                    return LuaValue.NIL;
                }
            }
        });

        set("find", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue patternValue, LuaValue textValue) {
                try {
                    Pattern pattern = Pattern.compile(patternValue.tojstring());
                    Matcher matcher = pattern.matcher(textValue.tojstring());
                    if (matcher.find()) {
                        LuaTable result = new LuaTable();
                        result.set(1, LuaValue.valueOf(matcher.start()));
                        result.set(2, LuaValue.valueOf(matcher.end()));
                        result.set(3, LuaValue.valueOf(matcher.group()));
                        return result;
                    }
                    return LuaValue.NIL;
                } catch (PatternSyntaxException e) {
                    return LuaValue.NIL;
                }
            }
        });

        set("replace", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue patternValue, LuaValue textValue, LuaValue replacementValue) {
                try {
                    Pattern pattern = Pattern.compile(patternValue.tojstring());
                    String result = pattern.matcher(textValue.tojstring()).replaceAll(replacementValue.tojstring());
                    return LuaValue.valueOf(result);
                } catch (PatternSyntaxException e) {
                    return LuaValue.NIL;
                }
            }
        });

        set("findAll", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue patternValue, LuaValue textValue) {
                try {
                    Pattern pattern = Pattern.compile(patternValue.tojstring());
                    Matcher matcher = pattern.matcher(textValue.tojstring());
                    LuaTable results = new LuaTable();
                    int index = 1;
                    while (matcher.find()) {
                        LuaTable match = new LuaTable();
                        match.set(1, LuaValue.valueOf(matcher.start()));
                        match.set(2, LuaValue.valueOf(matcher.end()));
                        match.set(3, LuaValue.valueOf(matcher.group()));
                        results.set(index++, match);
                    }
                    return results;
                } catch (PatternSyntaxException e) {
                    return new LuaTable();
                }
            }
        });

        set("censor", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue patternValue, LuaValue textValue) {
                try {
                    Pattern pattern = Pattern.compile(patternValue.tojstring());
                    Matcher matcher = pattern.matcher(textValue.tojstring());
                    StringBuilder stringBuilder = new StringBuilder();
                    while (matcher.find()) {
                        matcher.appendReplacement(stringBuilder, "*".repeat(matcher.group().length()));
                    }
                    matcher.appendTail(stringBuilder);
                    return LuaValue.valueOf(stringBuilder.toString());
                } catch (PatternSyntaxException e) {
                    return LuaValue.NIL;
                }
            }
        });
    }
}
