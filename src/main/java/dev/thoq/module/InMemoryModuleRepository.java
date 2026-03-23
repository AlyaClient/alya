package dev.thoq.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class InMemoryModuleRepository implements ModuleRepository {
  private final List<Module> modules = new CopyOnWriteArrayList<>();

  @Override
  public void save(final Module module) {
    if (!exists(module.getName())) {
      modules.add(module);
    }
  }

  @Override
  public void remove(final Module module) {
    if (module.isEnabled()) {
      module.setEnabled(false);
    }
    modules.remove(module);
  }

  @Override
  public Optional<Module> findByName(final String name) {
    return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst();
  }

  @Override
  public <T extends Module> Optional<T> findByClass(final Class<T> clazz) {
    return modules.stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
  }

  @Override
  public List<Module> findAll() {
    return new ArrayList<>(modules);
  }

  @Override
  public List<Module> findByCategory(final Category category) {
    return modules.stream()
        .filter(module -> module.getCategory() == category)
        .collect(Collectors.toList());
  }

  @Override
  public List<Module> findEnabled() {
    return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
  }

  @Override
  public boolean exists(final String name) {
    return modules.stream().anyMatch(module -> module.getName().equalsIgnoreCase(name));
  }

  @Override
  public int count() {
    return modules.size();
  }
}
