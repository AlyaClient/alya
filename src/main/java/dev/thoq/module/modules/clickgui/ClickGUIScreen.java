package dev.thoq.module.modules.clickgui;

import dev.thoq.Alya;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.*;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.render.RenderUtility;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class ClickGUIScreen extends GuiScreen {

    private static final int PANEL_WIDTH = 100;
    private static final int PANEL_HEIGHT = 18;
    private static final int MODULE_HEIGHT = 18;
    private static final int SETTING_HEIGHT = 9;
    private static final int PANEL_SPACING = 120;
    private static final int BACKGROUND_COLOR = 0xFF181A17;
    private static final int MODULE_BACKGROUND_COLOR = 0xFF232623;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BORDER_WIDTH = 1;
    private static final int DEFAULT_CATEGORY_COLOR = 0xFF666666;

    private static final Map<Category, Integer> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put(Category.COMBAT, 0xFFE64D3A);
        CATEGORY_COLORS.put(Category.MOVEMENT, 0xFF2ECD6F);
        CATEGORY_COLORS.put(Category.RENDER, 0xFF8F2DF7);
        CATEGORY_COLORS.put(Category.WORLD, 0xFF3A9DE6);
        CATEGORY_COLORS.put(Category.PLAYER, 0xFFF29D11);
        CATEGORY_COLORS.put(Category.MISC, 0xFF230057);
    }

    private final Map<Category, int[]> panelPositions = new HashMap<>();
    private final Map<Category, Boolean> expandedCategories = new HashMap<>();
    private final Map<Module, Boolean> expandedModules = new HashMap<>();
    private final Map<NumberSetting, Integer> numberSettingPositions = new HashMap<>();

    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private Category draggingCategory = null;
    private NumberSetting currentDraggedNumberSetting = null;
    private int currentDraggedSettingX = 0;

    public ClickGUIScreen() {
        int positionX = 4;
        for(final Category category : Category.values()) {
            panelPositions.put(category, new int[]{positionX, 4});
            expandedCategories.put(category, true);
            positionX += PANEL_SPACING;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);

        for(final Category category : Category.values()) {
            renderCategoryPanel(category, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderCategoryPanel(final Category category, final int mouseX, final int mouseY) {
        final int[] position = panelPositions.get(category);
        if(position == null) return;

        int panelX = position[0];
        int panelY = position[1];

        if(dragging && draggingCategory == category) {
            panelX = mouseX + dragOffsetX;
            panelY = mouseY + dragOffsetY;
            position[0] = panelX;
            position[1] = panelY;
        }

        final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);
        if(modules == null) return;

        int totalHeight = PANEL_HEIGHT;

        if(expandedCategories.get(category)) {
            for(final Module module : modules) {
                totalHeight += MODULE_HEIGHT;
                if(expandedModules.getOrDefault(module, false)) {
                    for(final Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            totalHeight += SETTING_HEIGHT;
                        }
                    }
                }
            }
        }

        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();

        RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, BACKGROUND_COLOR);

        final String categoryName = category.getDisplayName().toLowerCase();
        fontRenderer.drawString(categoryName, panelX + 4, panelY + 5, TEXT_COLOR);

        if(expandedCategories.get(category)) {
            int currentY = panelY + PANEL_HEIGHT;

            for(final Module module : modules) {
                renderModuleButton(module, panelX, currentY, category);
                currentY += MODULE_HEIGHT;

                if(expandedModules.getOrDefault(module, false)) {
                    for(final Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            renderSettingButton(setting, panelX, currentY, category);
                            currentY += SETTING_HEIGHT;
                        }
                    }
                }
            }
        }

        final int categoryColor = getCategoryColor(category);
        RenderUtility.drawRectOutline(panelX, panelY, PANEL_WIDTH, totalHeight, BORDER_WIDTH, categoryColor);

        RenderUtility.drawRect(panelX, panelY + totalHeight, PANEL_WIDTH, 2, BACKGROUND_COLOR);
    }

    private void renderModuleButton(final Module module, final int positionX, final int positionY, final Category category) {
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();

        RenderUtility.drawRect(positionX, positionY, PANEL_WIDTH, MODULE_HEIGHT, BACKGROUND_COLOR);

        final boolean extended = expandedModules.getOrDefault(module, false);
        if(!extended) {
            final int backgroundColor = module.isEnabled() ? getCategoryColor(category) : MODULE_BACKGROUND_COLOR;
            RenderUtility.drawRect(positionX + 2, positionY, PANEL_WIDTH - 4, MODULE_HEIGHT, backgroundColor);
        } else {
            RenderUtility.drawRect(positionX + 2, positionY, PANEL_WIDTH - 4, MODULE_HEIGHT, BACKGROUND_COLOR);
        }

        final String moduleName = module.getName().toLowerCase();
        final int textColor = extended ? (module.isEnabled() ? getCategoryColor(category) : TEXT_COLOR) : TEXT_COLOR;
        final int textX = positionX + PANEL_WIDTH - (int) fontRenderer.getStringWidth(moduleName) - 3;
        fontRenderer.drawString(moduleName, textX, positionY + 5, textColor);
    }

    private void renderSettingButton(final Setting<?> setting, final int positionX, final int positionY, final Category category) {
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererSmall();

        RenderUtility.drawRect(positionX, positionY, PANEL_WIDTH, SETTING_HEIGHT, BACKGROUND_COLOR);

        if(setting instanceof BooleanSetting) {
            final BooleanSetting booleanSetting = (BooleanSetting) setting;
            if(booleanSetting.isEnabled()) {
                RenderUtility.drawRect(positionX + 3, positionY, PANEL_WIDTH - 6, SETTING_HEIGHT, getCategoryColor(category));
            }
            fontRenderer.drawString(setting.getName(), positionX + 4, positionY, TEXT_COLOR);

        } else if(setting instanceof ModeSetting) {
            final ModeSetting modeSetting = (ModeSetting) setting;
            final String text = setting.getName() + " > " + modeSetting.getValue();
            fontRenderer.drawString(text, positionX + 4, positionY + 1, TEXT_COLOR);

        } else if(setting instanceof NumberSetting) {
            final NumberSetting numberSetting = (NumberSetting) setting;
            numberSettingPositions.put(numberSetting, positionX);

            RenderUtility.drawRect(positionX, positionY, PANEL_WIDTH, SETTING_HEIGHT, BACKGROUND_COLOR);

            final double value = numberSetting.getValue();
            final double minimum = numberSetting.getMin();
            final double maximum = numberSetting.getMax();
            final double percentage = (value - minimum) / (maximum - minimum);

            final int fillWidth = (int) (percentage * (PANEL_WIDTH - 6));
            RenderUtility.drawRect(positionX + 3, positionY, fillWidth, SETTING_HEIGHT, getCategoryColor(category));

            final String text = setting.getName() + ": " + Math.round(value * 100.0) / 100.0;
            fontRenderer.drawString(text, positionX + 4, positionY, TEXT_COLOR);
        }
    }

    private int getCategoryColor(final Category category) {
        return CATEGORY_COLORS.getOrDefault(category, DEFAULT_CATEGORY_COLOR);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for(final Category category : Category.values()) {
            final int[] position = panelPositions.get(category);
            if(position == null) continue;

            final int panelX = position[0];
            final int panelY = position[1];

            if(isMouseOver(mouseX, mouseY, panelX, panelY, PANEL_HEIGHT)) {
                if(mouseButton == 0) {
                    dragging = true;
                    draggingCategory = category;
                    dragOffsetX = panelX - mouseX;
                    dragOffsetY = panelY - mouseY;
                } else if(mouseButton == 1) {
                    expandedCategories.put(category, !expandedCategories.get(category));
                }
                return;
            }

            if(expandedCategories.get(category)) {
                final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);
                if(modules != null) {
                    int currentY = panelY + PANEL_HEIGHT;

                    for(final Module module : modules) {
                        if(isMouseOver(mouseX, mouseY, panelX, currentY, MODULE_HEIGHT)) {
                            if(mouseButton == 0) {
                                if(!(module instanceof ClickGUI)) {
                                    module.toggle();
                                }
                            } else if(mouseButton == 1 && !module.getSettings().isEmpty()) {
                                expandedModules.put(module, !expandedModules.getOrDefault(module, false));
                            }
                            return;
                        }
                        currentY += MODULE_HEIGHT;

                        if(expandedModules.getOrDefault(module, false)) {
                            for(final Setting<?> setting : module.getSettings()) {
                                if(setting.isVisible()) {
                                    if(isMouseOver(mouseX, mouseY, panelX, currentY, SETTING_HEIGHT)) {
                                        if(setting instanceof NumberSetting && mouseButton == 0) {
                                            currentDraggedNumberSetting = (NumberSetting) setting;
                                            currentDraggedSettingX = panelX;
                                            updateNumberSettingFromMouse(mouseX);
                                        } else {
                                            handleSettingClick(setting, mouseButton);
                                        }
                                        return;
                                    }
                                    currentY += SETTING_HEIGHT;
                                }
                            }
                        }
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void handleSettingClick(final Setting<?> setting, final int mouseButton) {
        if(setting instanceof BooleanSetting) {
            final BooleanSetting booleanSetting = (BooleanSetting) setting;
            booleanSetting.toggle();
        } else if(setting instanceof ModeSetting) {
            final ModeSetting modeSetting = (ModeSetting) setting;
            if(mouseButton == 0) {
                modeSetting.cycle();
            } else if(mouseButton == 1) {
                final String currentValue = modeSetting.getValue();
                final List<String> modes = modeSetting.getModes();
                final int currentIndex = modes.indexOf(currentValue);
                final int previousIndex = (currentIndex - 1 + modes.size()) % modes.size();
                modeSetting.setValue(modes.get(previousIndex));
            }
        }
    }

    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if(dragging && draggingCategory != null) {
            return;
        }

        if(currentDraggedNumberSetting != null) {
            updateNumberSettingFromMouse(mouseX);
            return;
        }

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        dragging = false;
        draggingCategory = null;
        currentDraggedNumberSetting = null;

        super.mouseReleased(mouseX, mouseY, state);
    }

    private void updateNumberSettingFromMouse(final int mouseX) {
        if(currentDraggedNumberSetting == null) return;

        final int settingX = currentDraggedSettingX;
        final int sliderStart = settingX + 3;
        final int sliderWidth = PANEL_WIDTH - 6;

        int relativeX = mouseX - sliderStart;
        relativeX = Math.max(0, Math.min(relativeX, sliderWidth));

        final double percentage = (double) relativeX / sliderWidth;

        final double minimum = currentDraggedNumberSetting.getMin();
        final double maximum = currentDraggedNumberSetting.getMax();
        final double increment = currentDraggedNumberSetting.getIncrement();
        double newValue = minimum + (percentage * (maximum - minimum));

        newValue = Math.round(newValue / increment) * increment;
        currentDraggedNumberSetting.setValue(newValue);
    }

    private boolean isMouseOver(final int mouseX, final int mouseY, final int positionX, final int positionY, final int height) {
        return mouseX >= positionX && mouseX <= positionX + ClickGUIScreen.PANEL_WIDTH && mouseY >= positionY && mouseY <= positionY + height;
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            Alya.getInstance().getModuleManager().getModule(ClickGUI.class)
                    .ifPresent(module -> module.setEnabled(false));
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


}
