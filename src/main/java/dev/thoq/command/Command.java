package dev.thoq.command;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    protected static final Minecraft MC = Minecraft.getMinecraft();
    private final String name;
    private final String description;
    private final String[] aliases;

    public Command(final String name, final String description, final String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(final String[] args);

    public List<String> getCompletions(final String[] args) {
        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }


}
