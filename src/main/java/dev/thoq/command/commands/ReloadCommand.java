package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;

public final class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", "Reloads all Lua scripts");
    }

    @Override
    public void execute(final String[] args) {
        ChatUtil.sendRaw("§eReloading Lua scripts...");
        Alya.getInstance().getLuaEngine().reload();
    }


}
