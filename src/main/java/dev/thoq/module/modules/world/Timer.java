package dev.thoq.module.modules.world;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.MotionEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.NumberSetting;

public class Timer extends Module {

    public final NumberSetting timer = new NumberSetting("Speed", "Multiplier", 1, 0.1, 10, 0.1);

    public Timer() {
        super("Timer", "Change the tick speed client side", Category.WORLD);

        addSetting(timer);
    }

    @EventHandler
    private void onMotion(final MotionEvent event) {
        if(event.isPre()) {
            net.minecraft.util.Timer.timerSpeed = timer.getValueAsFloat();
        }
    }

}
