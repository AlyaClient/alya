package dev.thoq.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.thoq.Alya;
import dev.thoq.module.Module;
import dev.thoq.module.setting.Setting;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public final class ConfigManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final File configDir;

  public ConfigManager() {
    this.configDir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName() + "/configs");
    if (!configDir.exists()) {
      if (!configDir.mkdirs()) {
        Alya.getInstance()
            .getLogger()
            .error("Failed to create config directory: {}", configDir.getAbsolutePath());
      }
    }
  }

  public void save() {
    save("default");
  }

  public void save(String configName) {
    final File configFile = new File(configDir, configName + ".json");
    final JsonObject root = new JsonObject();
    final JsonObject modules = new JsonObject();
    for (final Module module : Alya.getInstance().getModuleManager().getModules()) {
      JsonObject moduleData = new JsonObject();
      moduleData.addProperty("enabled", module.isEnabled());
      moduleData.addProperty("keyCode", module.getKeyCode());
      if (module.hasSettings()) {
        final JsonObject settingsData = new JsonObject();
        for (final Setting<?> setting : module.getSettings()) {
          settingsData.addProperty(setting.getName(), setting.getValueAsString());
        }
        moduleData.add("settings", settingsData);
      }
      modules.add(module.getName(), moduleData);
    }
    root.add("modules", modules);
    root.addProperty("clientName", Alya.getName());
    try (final FileWriter writer = new FileWriter(configFile)) {
      GSON.toJson(root, writer);
      Alya.getInstance().getLogger().info("Saved config: {}", configName);
    } catch (final IOException ioException) {
      Alya.getInstance().getLogger().error("Failed to save config: {}", configName, ioException);
    }
  }

  public void load() {
    load("default");
  }

  public void load(final String configName) {
    final File configFile = new File(configDir, configName + ".json");
    if (!configFile.exists()) {
      Alya.getInstance().getLogger().info("Config file does not exist: {}", configName);
      return;
    }
    try (final FileReader reader = new FileReader(configFile)) {
      final JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
      if (root.has("clientName")) {
        Alya.getInstance().setName(root.get("clientName").getAsString());
      }
      if (root.has("modules")) {
        final JsonObject modules = root.getAsJsonObject("modules");
        for (final Map.Entry<String, JsonElement> entry : modules.entrySet()) {
          final String moduleName = entry.getKey();
          final JsonObject moduleData = entry.getValue().getAsJsonObject();
          Alya.getInstance()
              .getModuleManager()
              .getModule(moduleName)
              .ifPresent(
                  module -> {
                    if (moduleData.has("enabled")) {
                      module.setEnabled(moduleData.get("enabled").getAsBoolean());
                    }
                    if (moduleData.has("keyCode")) {
                      module.setKeyCode(moduleData.get("keyCode").getAsInt());
                    }
                    if (moduleData.has("settings")) {
                      JsonObject settingsData = moduleData.getAsJsonObject("settings");
                      for (final Map.Entry<String, JsonElement> settingEntry :
                          settingsData.entrySet()) {
                        module
                            .getSetting(settingEntry.getKey())
                            .ifPresent(
                                setting ->
                                    setting.setValueFromString(
                                        settingEntry.getValue().getAsString()));
                      }
                    }
                  });
        }
      }
      Alya.getInstance().getLogger().info("Loaded config: {}", configName);
    } catch (final IOException ioException) {
      Alya.getInstance().getLogger().error("Failed to load config: {}", configName, ioException);
    }
  }

  public boolean configExists(final String configName) {
    return new File(configDir, configName + ".json").exists();
  }

  public String[] getConfigNames() {
    final File[] files = configDir.listFiles((dir, name) -> name.endsWith(".json"));
    if (files == null) {
      return new String[0];
    }
    final String[] names = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      names[i] = files[i].getName().replace(".json", "");
    }
    return names;
  }

  public File getConfigDir() {
    return configDir;
  }
}
