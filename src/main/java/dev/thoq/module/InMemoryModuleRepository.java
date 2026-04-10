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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class InMemoryModuleRepository implements ModuleRepository {
    private final List<Module> modules = new CopyOnWriteArrayList<>();

    @Override
    public void save(final Module module) {
        if(!exists(module.getName())) {
            modules.add(module);
        }
    }

    @Override
    public void remove(final Module module) {
        if(module.isEnabled()) {
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
