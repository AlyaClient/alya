package dev.thoq.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public final class InMemoryCommandRepository implements CommandRepository {
    private final List<Command> commands = new CopyOnWriteArrayList<>();

    @Override
    public void save(final Command command) {
        if(!exists(command.getName())) {
            commands.add(command);
        }
    }

    @Override
    public void remove(final Command command) {
        commands.remove(command);
    }

    @Override
    public Optional<Command> findByName(final String name) {
        return commands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<Command> findByAlias(final String alias) {
        return commands.stream()
                .filter(command -> Arrays.stream(command.getAliases())
                        .anyMatch(aliasToFind -> aliasToFind.equalsIgnoreCase(alias)))
                .findFirst();
    }

    @Override
    public <T extends Command> Optional<T> findByClass(final Class<T> clazz) {
        return commands.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }

    @Override
    public List<Command> findAll() {
        return new ArrayList<>(commands);
    }

    @Override
    public boolean exists(final String name) {
        return commands.stream()
                .anyMatch(command -> command.getName().equalsIgnoreCase(name));
    }

    @Override
    public int count() {
        return commands.size();
    }
}
