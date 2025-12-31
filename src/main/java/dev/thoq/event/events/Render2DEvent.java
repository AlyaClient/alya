package dev.thoq.event.events;

import dev.thoq.event.IEvent;
import net.minecraft.client.gui.ScaledResolution;

public record Render2DEvent(ScaledResolution scaledResolution) implements IEvent {


}
