/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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

    public void putAll(final Module... modules) {
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
        if(this == o) return true;
        if(!(o instanceof ModuleManager)) return false;
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
