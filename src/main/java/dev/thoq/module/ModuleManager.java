package dev.thoq.module;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class ModuleManager {
    private final ModuleRepository repository;

    public ModuleManager() {
        this.repository = new InMemoryModuleRepository();
    }

    public ModuleManager(final ModuleRepository repository) {
        this.repository = repository;
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

    public ModuleRepository getRepository() {
        return repository;
    }
}
