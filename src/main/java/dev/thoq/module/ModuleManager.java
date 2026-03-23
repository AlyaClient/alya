package dev.thoq.module;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@SuppressWarnings("unused")
public final class ModuleManager {
    private final ModuleRepository repository;
    public ModuleManager() {
        this(new InMemoryModuleRepository());
    }
    public ModuleManager(ModuleRepository repository) {
        this.repository = repository;
    }
    public ModuleRepository repository() {
        return repository;
    }
    public void putAll(final Module ...modules) {
        for(final Module module : modules) {
            register(module);
        }
    }
    public void register(final Module module) {
        repository.save(module);
    }
    public void unregister(final Module module) {
        repository.remove(module);
    }
    public Optional<Module> getModule(final String name) {
        return repository.findByName(name);
    }
    public <T extends Module> Optional<T> getModule(final Class<T> clazz) {
        return repository.findByClass(clazz);
    }
    public List<Module> getModules() {
        return repository.findAll();
    }
    public List<Module> getModulesByCategory(final Category category) {
        return repository.findByCategory(category);
    }
    public List<Module> getEnabledModules() {
        return repository.findEnabled();
    }
    public void disableAll() {
        repository.findAll().forEach(module -> module.setEnabled(false));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleManager)) return false;
        ModuleManager that = (ModuleManager) o;
        return Objects.equals(repository, that.repository);
    }
    @Override
    public int hashCode() {
        return Objects.hash(repository);
    }
    @Override
    public String toString() {
        return "ModuleManager[repository=" + repository + "]";
    }
}
