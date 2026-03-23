package dev.thoq.module;
import java.util.List;
import java.util.Optional;
public interface ModuleRepository {
    void save(final Module module);
    void remove(final Module module);
    Optional<Module> findByName(final String name);
    <T extends Module> Optional<T> findByClass(final Class<T> clazz);
    List<Module> findAll();
    List<Module> findByCategory(final Category category);
    List<Module> findEnabled();
    boolean exists(final String name);
    int count();

}
