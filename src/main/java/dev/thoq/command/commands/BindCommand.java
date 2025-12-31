package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.module.Module;
import dev.thoq.util.player.ChatUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Bind a key to a module", "b", "keybind");
    }

    @Override
    public void execute(final String[] args) {
        if(args.length < 1) {
            ChatUtil.sendInfo("Usage: .bind add <module> <key>");
            ChatUtil.sendInfo("Usage: .bind remove <module>");
            ChatUtil.sendInfo("Usage: .bind list");
            return;
        }

        final String action = args[0].toLowerCase();

        switch(action) {
            case "add":
            case "set":
                if(args.length < 3) {
                    ChatUtil.sendError("Usage: .bind add <module> <key>");
                    return;
                }
                handleAdd(args[1], args[2]);
                break;

            case "remove":
            case "clear":
                if(args.length < 2) {
                    ChatUtil.sendError("Usage: .bind remove <module>");
                    return;
                }
                handleRemove(args[1]);
                break;

            case "list":
                handleList();
                break;

            default:
                ChatUtil.sendError("Unknown action: " + action);
                ChatUtil.sendInfo("Available actions: add, remove, list");
                break;
        }
    }

    private void handleAdd(final String moduleName, final String keyName) {
        Optional<Module> moduleOpt = Alya.getInstance().getModuleManager().getModule(moduleName);

        if(!moduleOpt.isPresent()) {
            ChatUtil.sendError("Module not found: " + moduleName);
            return;
        }

        final Module module = moduleOpt.get();
        final int keyCode = Keyboard.getKeyIndex(keyName.toUpperCase());

        if(keyCode == Keyboard.KEY_NONE) {
            ChatUtil.sendError("Invalid key: " + keyName);
            return;
        }

        module.setKeyCode(keyCode);
        ChatUtil.sendSuccess("Bound " + module.getName() + " to " + Keyboard.getKeyName(keyCode));
    }

    private void handleRemove(final String moduleName) {
        final Optional<Module> moduleOpt = Alya.getInstance().getModuleManager().getModule(moduleName);

        if(!moduleOpt.isPresent()) {
            ChatUtil.sendError("Module not found: " + moduleName);
            return;
        }

        final Module module = moduleOpt.get();
        module.setKeyCode(Keyboard.KEY_NONE);
        ChatUtil.sendSuccess("Removed keybind from " + module.getName());
    }

    private void handleList() {
        ChatUtil.sendInfo("Module Keybinds:");
        for(final Module module : Alya.getInstance().getModuleManager().getModules()) {
            if(module.getKeyCode() != Keyboard.KEY_NONE) {
                ChatUtil.sendRaw("  " + module.getName() + " -> " + Keyboard.getKeyName(module.getKeyCode()));
            }
        }
    }

    @Override
    public List<String> getCompletions(final String[] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            final String partial = args[0].toLowerCase();
            for(final String action : new String[]{"add", "remove", "list"}) {
                if(action.startsWith(partial)) {
                    completions.add(action);
                }
            }
        } else if(args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")
                || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("clear"))) {
            String partial = args[1].toLowerCase();
            for(final Module module : Alya.getInstance().getModuleManager().getModules()) {
                if(module.getName().toLowerCase().startsWith(partial)) {
                    completions.add(module.getName());
                }
            }
        }

        return completions;
    }


}
