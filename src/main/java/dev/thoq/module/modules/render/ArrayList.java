package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.util.AlyaFontRenderer;
import dev.thoq.util.RenderUtility;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ArrayList extends Module {

    public ArrayList() {
        super("ArrayList", "Displays enabled modules on screen", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onRender2D(final Render2DEvent event) {
        final ScaledResolution scaledResolution = event.getScaledResolution();
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRenderer();

        final List<Module> enabledModules = Alya.getInstance().getModuleManager().getEnabledModules()
                .stream()
                .filter(m -> !(m instanceof ArrayList))
                .sorted(Comparator.comparingDouble(m -> -fontRenderer.getStringWidth(m.getName())))
                .collect(Collectors.toList());

        float y = 2;
        final int padding = 4;
        final float height = fontRenderer.getFontHeight();

        for(final Module module : enabledModules) {
            final String name = module.getName();
            final float width = fontRenderer.getStringWidth(name) + padding * 2;
            final float x = scaledResolution.getScaledWidth() - width - 2;

            final int color = getCategoryColor(module.getCategory());

            RenderUtility.drawRect((int) (x - 1), (int) (y - 1), (int) (width + 2), (int) (height + 2), 0x90000000);
            RenderUtility.drawRect(scaledResolution.getScaledWidth() - 2, (int) (y - 1), 1, (int) (height + 2), color);
            fontRenderer.drawStringWithShadow(name, x + padding - 2, y, 0xFFFFFFFF);

            y += height + 2;
        }
    }

    private int getCategoryColor(final Category category) {
        switch(category) {
            case COMBAT:
                return 0xFFFF5555;
            case MOVEMENT:
                return 0xFF55FF55;
            case RENDER:
                return 0xFF5555FF;
            case PLAYER:
                return 0xFFFFFF55;
            case WORLD:
                return 0xFF55FFFF;
            case MISC:
            default:
                return 0xFFFF55FF;
        }
    }


}
