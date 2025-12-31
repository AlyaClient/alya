package dev.thoq.module.modules.player;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.PlayerInputEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.movement.MovementUtil;

public class SprintModule extends Module {

    private final BooleanSetting omniSprint = new BooleanSetting("OmniSprint", "Sprint in all directions", false);

    public SprintModule() {
        super("Sprint", "Auto-Sprint", Category.PLAYER);

        initializeSettings(omniSprint);
    }

    @EventHandler
    private void onPlayerInput(final PlayerInputEvent event) {
        if(!MovementUtil.isMoving()) {
            return;
        }

        if(omniSprint.isEnabled()) {
            MC.thePlayer.setSprinting(true);
        } else {
            if(MC.gameSettings.keyBindForward.isKeyDown() && !MC.gameSettings.keyBindBack.isKeyDown()) {
                MC.thePlayer.setSprinting(true);
            }
        }
    }

}
