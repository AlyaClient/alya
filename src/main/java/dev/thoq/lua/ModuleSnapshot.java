package dev.thoq.lua;

import java.util.Map;

public record ModuleSnapshot(boolean enabled, int keyCode, Map<String, String> settingValues) {
}

