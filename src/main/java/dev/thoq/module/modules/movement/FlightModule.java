package dev.thoq.module.modules.movement;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.PlayerMoveEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.NumberSetting;
import dev.thoq.util.MovementUtil;

public final class FlightModule extends Module {

    private final NumberSetting speed = new NumberSetting("Speed", "How fast", 1D, 0.1, 10.0);

    public FlightModule() {
        super("Flight", "Airplane", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
        if(MC.gameSettings.keyBindJump.isKeyDown()) {
            MC.thePlayer.motionY += speed.getValue() / 2D;
        } else if(MC.gameSettings.keyBindSneak.isKeyDown()) {
            MC.thePlayer.motionY -= speed.getValue() / 2D;
        } else {
            MC.thePlayer.motionY = 0;
        }

        MovementUtil.setSpeed(speed.getValue());
    }


}
