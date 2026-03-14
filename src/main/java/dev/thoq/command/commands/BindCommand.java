package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.module.Module;
import dev.thoq.util.player.ChatUtil;

import java.util.Optional;

public final class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Bind a key to a module", "b", "keybind");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length < 1) {
            ChatUtil.sendInfo("Usage: .bind add <module> <key>");
            ChatUtil.sendInfo("Usage: .bind remove <module>");
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
            try {
                final int keyCode = Integer.parseInt(args[2]);
                module.get().setKeyCode(keyCode);
                ChatUtil.sendSuccess("Key binding updated for " + module.get().getName());
            } catch (final NumberFormatException e) {
                ChatUtil.sendError("Invalid key code: " + args[2]);
            }

        } else if (action.equals("remove") || action.equals("clear")) {
            if (args.length < 2) {
                ChatUtil.sendError("Usage: .bind remove <module>");
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
                    ChatUtil.sendRaw("  " + module.getName() + " -> key:" + module.getKeyCode());
                }
            }

        } else {
            ChatUtil.sendError("Unknown action: " + action);
            ChatUtil.sendInfo("Available actions: add, remove, list");
        }
    }
}