package dev.thoq.command;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class CommandManager {
    private final CommandRepository repository;
    private final String prefix;

    public CommandManager() {
        this(new InMemoryCommandRepository(), ".");
    }

    public CommandManager(CommandRepository repository, String prefix) {
        this.repository = repository;
        this.prefix = prefix;
    }

    public CommandRepository repository() {
        return repository;
    }

    public String prefix() {
        return prefix;
    }

    public void putAll(final Command... commands) {
        for(final Command command : commands) {
            register(command);
        }
    }

    public void register(final Command command) {
        repository.save(command);
    }

    public void unregister(final Command command) {
        repository.remove(command);
    }

    public Optional<Command> getCommand(final String name) {
        return repository.findByName(name);
    }

    public <T extends Command> Optional<T> getCommand(Class<T> clazz) {
        return repository.findByClass(clazz);
    }

    public List<Command> getCommands() {
        return repository.findAll();
    }

    public boolean executeCommand(final String message) {
        if(!message.startsWith(prefix)) {
            return false;
        }
        final String commandLine = message.substring(prefix.length()).trim();
        if(commandLine.isEmpty()) {
            return false;
        }
        final String[] parts = commandLine.split("\\s+");
        final String commandName = parts[0];
        final String[] args =
                parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        Optional<Command> command = repository.findByName(commandName);
        if(!command.isPresent()) {
            command = repository.findByAlias(commandName);
        }
        if(command.isPresent()) {
            command.get().execute(args);
            return true;
        }
        return false;
    }

    public List<String> getCompletions(final String input) {
        if(!input.startsWith(prefix)) {
            return new ArrayList<>();
        }
        final String commandLine = input.substring(prefix.length());
        if(!commandLine.contains(" ")) {
            return getCommandCompletions(commandLine);
        }
        final String[] parts = commandLine.split("\\s+", -1);
        final String commandName = parts[0];
        Optional<Command> command = repository.findByName(commandName);
        if(!command.isPresent()) {
            command = repository.findByAlias(commandName);
        }
        if(command.isPresent()) {
            final String[] args =
                    parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
            return command.get().getCompletions(args);
        }
        return new ArrayList<>();
    }

    public List<String> getCommandCompletions(final String partial) {
        final String lowerPartial = partial.toLowerCase();
        final List<String> completions = new ArrayList<>();
        for(final Command command : repository.findAll()) {
            if(command.getName().toLowerCase().startsWith(lowerPartial)) {
                completions.add(prefix + command.getName());
            }
            for(final String alias : command.getAliases()) {
                if(alias.toLowerCase().startsWith(lowerPartial)) {
                    completions.add(prefix + alias);
                }
            }
        }
        return completions.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CommandManager)) return false;
        CommandManager that = (CommandManager) o;
        return Objects.equals(repository, that.repository) && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository, prefix);
    }

    @Override
    public String toString() {
        return "CommandManager[repository=" + repository + ", prefix=" + prefix + "]";
    }
}
