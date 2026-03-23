package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.module.Module;
import dev.thoq.util.player.ChatUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.lwjgl.input.Keyboard;

public final class BindCommand extends Command {
  public BindCommand() {
    super("bind", "Bind a key to a module", "b", "keybind");
  }

  @Override
  public void execute(final String[] args) {
    if (args.length < 1) {
      ChatUtil.sendInfo("Usage: .bind add <module> <key>");
      ChatUtil.sendInfo("Usage: .bind del <module>");
      ChatUtil.sendInfo("Usage: .bind list");
      return;
    }
    final String action = args[0].toLowerCase();
    if (action.equals("add") || action.equals("set")) {
      if (args.length < 3) {
        ChatUtil.sendError("Usage: .bind add <module> <key>");
        return;
      }
      final Optional<Module> module = Alya.getInstance().getModuleManager().getModule(args[1]);
      if (!module.isPresent()) {
        ChatUtil.sendError("Module not found: " + args[1]);
        return;
      }
      final int keyCode = resolveKey(args[2]);
      if (keyCode == Keyboard.KEY_NONE) {
        ChatUtil.sendError(
            "Invalid key: '" + args[2] + "'. Use a key name (e.g. R, F, HOME) or numeric keycode.");
        return;
      }
      module.get().setKeyCode(keyCode);
      ChatUtil.sendSuccess(
          "Bound " + module.get().getName() + " to " + Keyboard.getKeyName(keyCode));
    } else if (action.equals("del") || action.equals("clear")) {
      if (args.length < 2) {
        ChatUtil.sendError("Usage: .bind del <module>");
        return;
      }
      final Optional<Module> module = Alya.getInstance().getModuleManager().getModule(args[1]);
      if (!module.isPresent()) {
        ChatUtil.sendError("Module not found: " + args[1]);
        return;
      }
      module.get().setKeyCode(0);
      ChatUtil.sendSuccess("Removed keybind from " + module.get().getName());
    } else if (action.equals("list")) {
      ChatUtil.sendInfo("Module Keybinds:");
      for (final Module module : Alya.getInstance().getModuleManager().getModules()) {
        if (module.getKeyCode() != 0) {
          ChatUtil.sendRaw(
              "  " + module.getName() + " -> " + Keyboard.getKeyName(module.getKeyCode()));
        }
      }
    } else {
      ChatUtil.sendError("Unknown action: " + action);
      ChatUtil.sendInfo("Available actions: add, del, list");
    }
  }

  @Override
  public List<String> getCompletions(final String[] args) {
    List<String> completions = new ArrayList<>();
    if (args.length == 1) {
      for (String action : new String[] {"add", "del", "list", "clear"}) {
        if (action.startsWith(args[0].toLowerCase())) completions.add(action);
      }
    } else if (args.length == 2
        && (args[0].equalsIgnoreCase("add")
            || args[0].equalsIgnoreCase("set")
            || args[0].equalsIgnoreCase("del")
            || args[0].equalsIgnoreCase("clear"))) {
      final String prefix = args[1].toLowerCase();
      completions =
          Alya.getInstance().getModuleManager().getModules().stream()
              .map(Module::getName)
              .filter(name -> name.toLowerCase().startsWith(prefix))
              .collect(Collectors.toList());
    }
    return completions;
  }

  private int resolveKey(final String input) {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException ignored) {
    }
    final int key = Keyboard.getKeyIndex(input.toUpperCase());
    if (key != Keyboard.KEY_NONE) return key;
    return Keyboard.KEY_NONE;
  }
}
