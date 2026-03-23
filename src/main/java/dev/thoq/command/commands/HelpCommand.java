package dev.thoq.command.commands;
import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;
public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Displays a list of available commands", "h", "?");
    }
    @Override
    public void execute(final String[] args) {
        final String prefix = Alya.getInstance().getCommandManager().prefix();
        ChatUtil.sendRaw("\u00a7e=== Available Commands ===");
        for (final Command command : Alya.getInstance().getCommandManager().getCommands()) {
            final String[] aliases = command.getAliases();
            final String aliasText = aliases.length > 0 ? " \u00a7f(" + String.join(", ", aliases) + ")" : "";
            ChatUtil.sendRaw("\u00a7b" + prefix + command.getName() + aliasText + "\u00a7f - " + command.getDescription());
        }
    }
}
