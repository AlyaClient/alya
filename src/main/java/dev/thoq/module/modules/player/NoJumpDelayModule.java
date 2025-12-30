package dev.thoq.module.modules.player;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.MotionEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;

public final class NoJumpDelayModule extends Module {

    public NoJumpDelayModule() {
        super("NoJumpDelay", "Removes delay between jumps", Category.PLAYER);
    }

    @EventHandler
    private void onMotion(final MotionEvent event) {
        if(MC.thePlayer.onGround) {
            MC.thePlayer.jumpTicks = 0;
        }
    }


}
