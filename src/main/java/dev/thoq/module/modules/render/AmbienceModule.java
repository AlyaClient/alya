package dev.thoq.module.modules.render;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.TimeUpdateEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.NumberSetting;

public class AmbienceModule extends Module {

    private final NumberSetting time = new NumberSetting("Time", "Time of day", 18.0, 0.0, 24.0, 1);
    private long originalTime;

    public AmbienceModule() {
        super("Ambience", "Sets the time of day client side", Category.RENDER);

        initializeSettings(time);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(MC.thePlayer == null) {
            return;
        }
        originalTime = MC.theWorld.getWorldTime();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(MC.thePlayer == null) {
            return;
        }
        MC.theWorld.setWorldTime(originalTime);
    }

    @EventHandler
    private void onTimeUpdate(final TimeUpdateEvent event) {
        event.cancel();

        final long mcTime = time.getValueAsInt() * 1_000L;
        MC.theWorld.setWorldTime(mcTime);
    }


}
