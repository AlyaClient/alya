package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.ChatUtility;

import java.util.ArrayList;
import java.util.List;

public final class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Save or load configurations", "cfg", "c");
    }

    @Override
    public void execute(final String[] args) {
        if(args.length < 1) {
            ChatUtility.sendInfo("Usage: .config save <name>");
            ChatUtility.sendInfo("Usage: .config load <name>");
            ChatUtility.sendInfo("Usage: .config list");
            return;
        }

        final String action = args[0].toLowerCase();

        switch(action) {
            case "save":
                if(args.length < 2) {
                    ChatUtility.sendError("Usage: .config save <name>");
                    return;
                }
                handleSave(args[1]);
                break;

            case "load":
                if(args.length < 2) {
                    ChatUtility.sendError("Usage: .config load <name>");
                    return;
                }
                handleLoad(args[1]);
                break;

            case "list":
                handleList();
                break;

            default:
                ChatUtility.sendError("Unknown action: " + action);
                ChatUtility.sendInfo("Available actions: save, load, list");
                break;
        }
    }

    private void handleSave(final String configName) {
        Alya.getInstance().getConfigManager().save(configName);
        ChatUtility.sendSuccess("Saved config: " + configName);
    }

    private void handleLoad(final String configName) {
        if(!Alya.getInstance().getConfigManager().configExists(configName)) {
            ChatUtility.sendError("Config not found: " + configName);
            return;
        }
        Alya.getInstance().getConfigManager().load(configName);
        ChatUtility.sendSuccess("Loaded config: " + configName);
    }

    private void handleList() {
        final String[] configs = Alya.getInstance().getConfigManager().getConfigNames();
        if(configs.length == 0) {
            ChatUtility.sendInfo("No configs found.");
            return;
        }
        ChatUtility.sendInfo("Available configs:");
        for(final String config : configs) {
            ChatUtility.sendRaw("  - " + config);
        }
    }

    @Override
    public List<String> getCompletions(final String[] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            final String partial = args[0].toLowerCase();
            for(final String action : new String[]{"save", "load", "list"}) {
                if(action.startsWith(partial)) {
                    completions.add(action);
                }
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("load")) {
            final String partial = args[1].toLowerCase();
            for(final String config : Alya.getInstance().getConfigManager().getConfigNames()) {
                if(config.toLowerCase().startsWith(partial)) {
                    completions.add(config);
                }
            }
        }

        return completions;
    }


}
