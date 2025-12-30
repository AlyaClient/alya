package dev.thoq.module.modules.render;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.UpdateEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;

public class FullBrightModule extends Module {

    public FullBrightModule() {
        super("FullBright", "Brightens up the world client side", Category.RENDER);
    }

    @EventHandler
    private void onUpdateEvent(final UpdateEvent event) {
        MC.gameSettings.gammaSetting = 100;
    }


}
