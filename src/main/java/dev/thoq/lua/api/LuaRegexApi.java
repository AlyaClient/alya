package dev.thoq.lua.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
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
                    StringBuffer sb = new StringBuffer();
                    while (matcher.find()) {
                        matcher.appendReplacement(sb, "*".repeat(matcher.group().length()));
                    }
                    matcher.appendTail(sb);
                    return LuaValue.valueOf(sb.toString());
                } catch (PatternSyntaxException e) {
                    return LuaValue.NIL;
                }
            }
        });
    }
}
