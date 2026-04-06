package dev.thoq.lua;

import java.util.ArrayList;
import java.util.List;

public final class ScriptsUtil {

    private final List<String> rawScripts;

    public ScriptsUtil() {
        this.rawScripts = new ArrayList<>();
    }

    public String[] putAll(final Script ...scripts) {
        for(final Script script : scripts) {
            this.rawScripts.add(String.format("modules/%s/%s.lua", script.category().toString().toLowerCase(), script.name()));
        }
        return this.rawScripts.toArray(new String[0]);
    }


}
