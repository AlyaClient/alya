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

package bypass.event.events;

import bypass.event.IEvent;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Objects;

public record Render2DEvent(ScaledResolution scaledResolution) implements IEvent {

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Render2DEvent)) return false;
        Render2DEvent that = (Render2DEvent) o;
        return Objects.equals(scaledResolution, that.scaledResolution);
    }

    @Override
    public String toString() {
        return "Render2DEvent[scaledResolution=" + scaledResolution + "]";
    }
}
