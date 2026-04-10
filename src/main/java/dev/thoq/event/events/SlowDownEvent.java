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

package dev.thoq.event.events;

import dev.thoq.event.ICancelable;
import dev.thoq.event.IEvent;

@SuppressWarnings("unused")
public final class SlowDownEvent implements IEvent, ICancelable {


    private final String reason;
    private boolean cancelled = false;

    public SlowDownEvent(final String reason) {
        this.reason = normalizeReason(reason);
    }

    private static String normalizeReason(final String raw) {
        if(raw == null) {
            return "unknown";
        }
        return switch(raw.toLowerCase()) {
            case "eat" -> "eat";
            case "drink" -> "drink";
            case "block" -> "block";
            case "bow" -> "bow";
            default -> "unknown";
        };
    }

    public String getType() {
        return "slowdown";
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }


}
