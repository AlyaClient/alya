package dev.thoq.module.modules.player;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.UpdateEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.NumberSetting;

public final class NoRightClickDelayModule extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", "Delay in ticks", 1, 0, 4, 1);

    public NoRightClickDelayModule() {
        super("NoRightClickDelay", "Removes right click delay", Category.PLAYER);

        addSetting(delay);
    }

    @EventHandler
    private void onUpdateEvent(final UpdateEvent event) {
        if(MC.thePlayer.ticksExisted % (delay.getValueAsInt() + 1) == 0) {
            MC.rightClickDelayTimer = 0;
        }
    }


}
