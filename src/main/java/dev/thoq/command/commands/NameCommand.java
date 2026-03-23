package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;

public final class NameCommand extends Command {
  public NameCommand() {
    super("name", "Set client name");
  }

  @Override
  public void execute(final String[] args) {
	final String name = String.join(" ", args);

    Alya.getInstance().setClientName(name);
    Alya.getInstance().getConfigManager().save();
    ChatUtil.sendInfo("Client name set to: " + name);
  }
}
