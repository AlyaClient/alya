package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public final class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Save or load configurations", "cfg", "c");
    }

    @Override
    public void execute(final String[] args) {
        if(args.length < 1) {
            ChatUtil.sendInfo("Usage: .config save <name>");
            ChatUtil.sendInfo("Usage: .config load <name>");
            ChatUtil.sendInfo("Usage: .config list");
            return;
        }

        final String action = args[0].toLowerCase();

        switch(action) {
            case "save":
                if(args.length < 2) {
                    ChatUtil.sendError("Usage: .config save <name>");
                    return;
                }
                handleSave(args[1]);
                break;

            case "load":
                if(args.length < 2) {
                    ChatUtil.sendError("Usage: .config load <name>");
                    return;
                }
                handleLoad(args[1]);
                break;

            case "list":
                handleList();
                break;

            default:
                ChatUtil.sendError("Unknown action: " + action);
                ChatUtil.sendInfo("Available actions: save, load, list");
                break;
        }
    }

    private void handleSave(final String configName) {
        Alya.getInstance().getConfigManager().save(configName);
        ChatUtil.sendSuccess("Saved config: " + configName);
    }

    private void handleLoad(final String configName) {
        if(!Alya.getInstance().getConfigManager().configExists(configName)) {
            ChatUtil.sendError("Config not found: " + configName);
            return;
        }
        Alya.getInstance().getConfigManager().load(configName);
        ChatUtil.sendSuccess("Loaded config: " + configName);
    }

    private void handleList() {
        final String[] configs = Alya.getInstance().getConfigManager().getConfigNames();
        if(configs.length == 0) {
            ChatUtil.sendInfo("No configs found.");
            return;
        }
        ChatUtil.sendInfo("Available configs:");
        for(final String config : configs) {
            ChatUtil.sendRaw("  - " + config);
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
