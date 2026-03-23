package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;

public final class ConfigCommand extends Command {
  public ConfigCommand() {
    super("config", "Save or load configurations", "cfg", "c");
  }

  @Override
  public void execute(final String[] args) {
    if (args.length < 1) {
      ChatUtil.sendInfo("Usage: .config save <name>");
      ChatUtil.sendInfo("Usage: .config load <name>");
      ChatUtil.sendInfo("Usage: .config list");
      return;
    }
    final String action = args[0].toLowerCase();
    if (action.equals("save")) {
      if (args.length < 2) {
        ChatUtil.sendError("Usage: .config save <name>");
        return;
      }
      Alya.getInstance().getConfigManager().save(args[1]);
      ChatUtil.sendSuccess("Saved config: " + args[1]);
    } else if (action.equals("load")) {
      if (args.length < 2) {
        ChatUtil.sendError("Usage: .config load <name>");
        return;
      }
      if (!Alya.getInstance().getConfigManager().configExists(args[1])) {
        ChatUtil.sendError("Config not found: " + args[1]);
        return;
      }
      Alya.getInstance().getConfigManager().load(args[1]);
      ChatUtil.sendSuccess("Loaded config: " + args[1]);
    } else if (action.equals("list")) {
      final String[] names = Alya.getInstance().getConfigManager().getConfigNames();
      if (names.length == 0) {
        ChatUtil.sendInfo("No configs found.");
        return;
      }
      ChatUtil.sendInfo("Available configs:");
      for (final String name : names) {
        ChatUtil.sendRaw("  - " + name);
      }
    } else {
      ChatUtil.sendError("Unknown action: " + action);
      ChatUtil.sendInfo("Available actions: save, load, list");
    }
  }
}
