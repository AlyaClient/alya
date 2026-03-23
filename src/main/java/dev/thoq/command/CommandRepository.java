package dev.thoq.command;
import java.util.List;
import java.util.Optional;
public interface CommandRepository {
    void save(final Command command);
    void remove(final Command command);
    Optional<Command> findByName(final String name);
    Optional<Command> findByAlias(final String alias);
    <T extends Command> Optional<T> findByClass(final Class<T> clazz);
    List<Command> findAll();
    boolean exists(final String name);
    int count();
}
