package dev.thoq.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class CommandManager {

    private final CommandRepository repository;
    private final String prefix;

    public CommandManager() {
        this.repository = new InMemoryCommandRepository();
        this.prefix = ".";
    }

    public CommandManager(final CommandRepository repository, final String prefix) {
        this.repository = repository;
        this.prefix = prefix;
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

    public CommandRepository getRepository() {
        return repository;
    }

    public String getPrefix() {
        return prefix;
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
        final String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

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
            final String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
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


}
