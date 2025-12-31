package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.modules.clickgui.ClickGUI;
import dev.thoq.module.modules.clickgui.ClickGUIScreen;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.render.RenderUtility;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ArrayListModule extends Module {

    private final BooleanSetting showVisual = new BooleanSetting("Show Visual Modules", "Show visual modules?", true);

    public ArrayListModule() {
        super("ArrayList", "Displays enabled modules on screen", Category.RENDER);

        addSetting(showVisual);
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
        final ScaledResolution scaledResolution = event.scaledResolution();
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();
        final boolean showVisual = this.showVisual.getValue();

        final List<Module> enabledModules = Alya.getInstance().getModuleManager().getEnabledModules()
                .stream()
                .filter(m -> !(m instanceof ArrayListModule))
                .filter(m -> !(m instanceof ClickGUI))
                .filter(m -> showVisual || !(m.getCategory() == Category.RENDER))
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
            fontRenderer.drawString(name, x + padding - 2, y, 0xFFFFFFFF);

            y += height + 2;
        }
    }

    private int getCategoryColor(final Category category) {
        return ClickGUIScreen
                .getCategoryColors()
                .getOrDefault(category, 0xFFFF55FF);
    }


}
