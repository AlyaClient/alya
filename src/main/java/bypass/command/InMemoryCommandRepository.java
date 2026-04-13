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

package bypass.command;

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
                .filter(
                        command ->
                                Arrays.stream(command.getAliases())
                                        .anyMatch(aliasToFind -> aliasToFind.equalsIgnoreCase(alias)))
                .findFirst();
    }

    @Override
    public <T extends Command> Optional<T> findByClass(final Class<T> clazz) {
        return commands.stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
    }

    @Override
    public List<Command> findAll() {
        return new ArrayList<>(commands);
    }

    @Override
    public boolean exists(final String name) {
        return commands.stream().anyMatch(command -> command.getName().equalsIgnoreCase(name));
    }

    @Override
    public int count() {
        return commands.size();
    }
}
