package dev.thoq.module.modules.movement.flight;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.PlayerMoveEvent;
import dev.thoq.module.Submodule;
import dev.thoq.module.modules.movement.FlightModule;
import dev.thoq.util.MovementUtil;

public final class StaticFlightMode extends Submodule {

    public StaticFlightMode(FlightModule parent) {
        super("Static", parent);
    }

    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
        MC.thePlayer.motionY = 0;
        MovementUtil.setSpeed(0);
    }


}
