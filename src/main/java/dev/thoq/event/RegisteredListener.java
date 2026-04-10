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

package dev.thoq.event;

import java.util.Objects;

public final class RegisteredListener {
    private final Class<? extends IEvent> eventClass;
    private final IEventListener<? extends IEvent> listener;

    public RegisteredListener(
            Class<? extends IEvent> eventClass, IEventListener<? extends IEvent> listener) {
        this.eventClass = eventClass;
        this.listener = listener;
    }

    public Class<? extends IEvent> eventClass() {
        return eventClass;
    }

    public IEventListener<? extends IEvent> listener() {
        return listener;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof RegisteredListener)) return false;
        RegisteredListener that = (RegisteredListener) o;
        return Objects.equals(eventClass, that.eventClass) && Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventClass, listener);
    }

    @Override
    public String toString() {
        return "RegisteredListener[eventClass=" + eventClass + ", listener=" + listener + "]";
    }
}
