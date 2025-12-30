package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public final class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Displays a list of available commands", "h", "?");
    }

    @Override
    public void execute(final String[] args) {
        if(MC.thePlayer == null) {
            return;
        }

        MC.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Available Commands ==="));

        for(final Command command : Alya.getInstance().getCommandManager().getCommands()) {
            final String aliases = command.getAliases().length > 0
                    ? EnumChatFormatting.GRAY + " (" + String.join(", ", command.getAliases()) + ")"
                    : "";
            MC.thePlayer.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.AQUA + "." + command.getName() + aliases +
                            EnumChatFormatting.WHITE + " - " + command.getDescription()
            ));
        }
    }


}
