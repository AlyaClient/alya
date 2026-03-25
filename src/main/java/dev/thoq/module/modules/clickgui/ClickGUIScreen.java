package dev.thoq.module.modules.clickgui;

import dev.thoq.Alya;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.module.setting.ModeSetting;
import dev.thoq.module.setting.NumberSetting;
import dev.thoq.module.setting.Setting;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.render.RenderUtility;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class ClickGUIScreen extends GuiScreen {
    private static final int PANEL_WIDTH = 100;
    private static final int PANEL_HEIGHT = 16;
    private static final int MODULE_HEIGHT = 14;
    private static final int SETTING_HEIGHT = 11;
    private static final int SETTING_GROUP_PADDING = 2;
    private static final int PANEL_SPACING = 115;
    private static final int SETTING_INDENT = 6;
    private static final int BACKGROUND_COLOR = 0xFF181A17;
    private static final int MODULE_BACKGROUND_COLOR = 0xFF232623;
    private static final int SETTING_BACKGROUND_COLOR = 0xFF111311;
    private static final int HOVER_TINT = 0x20FFFFFF;
    private static final int TEXT_COLOR = 0xFFCCCCCC;
    private static final float BORDER_WIDTH = 0.5f;
    private static final int DEFAULT_CATEGORY_COLOR = 0xFF666666;

    private static final AlyaFontRenderer fontMedium = Alya.getInstance().getFontRendererSmall();
    private static final AlyaFontRenderer fontSmall = Alya.getInstance().getFontRendererTiny();
    private static final Map<Category, Integer> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put(Category.COMBAT, 0xFFE74C3C);
        CATEGORY_COLORS.put(Category.MOVEMENT, 0xFF2ECC71);
        CATEGORY_COLORS.put(Category.PLAYER, 0xFF8E44AD);
        CATEGORY_COLORS.put(Category.VISUAL, 0xFF3700CE);
        CATEGORY_COLORS.put(Category.EXPLOIT, 0xFF4697DB);
        CATEGORY_COLORS.put(Category.OTHER, 0xFFF39C12);
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
    private boolean draggingSecondNub = false;

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
        final List<Module> modules =
                Alya.getInstance().getModuleManager().getModulesByCategory(category);
        if(modules == null) return;
        int totalHeight = PANEL_HEIGHT;
        if(expandedCategories.get(category)) {
            for(final Module module : modules) {
                totalHeight += MODULE_HEIGHT;
                if(expandedModules.getOrDefault(module, false)) {
                    boolean hasVisibleSettings = false;
                    for(final Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            totalHeight += SETTING_HEIGHT;
                            hasVisibleSettings = true;
                        }
                    }
                    if(hasVisibleSettings) {
                        totalHeight += SETTING_GROUP_PADDING * 2;
                    }
                }
            }
            totalHeight += 1;
        }
        RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, BACKGROUND_COLOR);
        final String categoryName = category.getDisplayName().toLowerCase();
        fontMedium.drawString(categoryName, panelX + 4, panelY + 5, TEXT_COLOR);

        final int catColor = getCategoryColor(category);
        final float catR = (catColor >> 16 & 0xFF) / 255.0F;
        final float catG = (catColor >> 8 & 0xFF) / 255.0F;
        final float catB = (catColor & 0xFF) / 255.0F;

        final net.minecraft.util.ResourceLocation categoryIcon =
                new net.minecraft.util.ResourceLocation(
                        "Alya/Icons/Categories/" + categoryName + "_good.png");
        net.minecraft.client.renderer.GlStateManager.color(catR, catG, catB, 1.0F);
        RenderUtility.drawImage(categoryIcon, panelX + PANEL_WIDTH - 12, panelY + 5, 6, 6);

        final String eyeIconName = expandedCategories.get(category) ? "eye_open.png" : "eye_close.png";
        final net.minecraft.util.ResourceLocation eyeIcon =
                new net.minecraft.util.ResourceLocation("Alya/Icons/" + eyeIconName);
        net.minecraft.client.renderer.GlStateManager.color(catR, catG, catB, 1.0F);
        RenderUtility.drawImage(eyeIcon, panelX + PANEL_WIDTH - 20, panelY + 5, 6, 6);

        if(expandedCategories.get(category)) {
            int currentY = panelY + PANEL_HEIGHT;
            for(final Module module : modules) {
                renderModuleButton(module, panelX, currentY, category, mouseX, mouseY);
                currentY += MODULE_HEIGHT;
                if(expandedModules.getOrDefault(module, false)) {
                    RenderUtility.drawRect(
                            panelX + 1,
                            currentY,
                            PANEL_WIDTH - 2,
                            SETTING_GROUP_PADDING,
                            SETTING_BACKGROUND_COLOR);
                    currentY += SETTING_GROUP_PADDING;
                    for(final Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            renderSettingButton(setting, panelX, currentY, category, mouseX, mouseY);
                            currentY += SETTING_HEIGHT;
                        }
                    }
                    RenderUtility.drawRect(
                            panelX + 1,
                            currentY,
                            PANEL_WIDTH - 2,
                            SETTING_GROUP_PADDING,
                            SETTING_BACKGROUND_COLOR);
                    currentY += SETTING_GROUP_PADDING;
                }
            }
        }
        final int categoryColor = getCategoryColor(category);
        RenderUtility.drawRectOutline(
                panelX, panelY, PANEL_WIDTH, totalHeight, categoryColor, BORDER_WIDTH);
    }

    private void renderModuleButton(
            final Module module,
            final int positionX,
            final int positionY,
            final Category category,
            final int mouseX,
            final int mouseY) {
        final boolean extended = expandedModules.getOrDefault(module, false);
        final boolean hasSettings = !module.getSettings().isEmpty();
        if(!extended) {
            final int backgroundColor =
                    module.isEnabled() ? getCategoryColor(category) : MODULE_BACKGROUND_COLOR;
            RenderUtility.drawRect(
                    positionX + 1, positionY, PANEL_WIDTH - 2, MODULE_HEIGHT, backgroundColor);
        } else {
            RenderUtility.drawRect(
                    positionX + 1, positionY, PANEL_WIDTH - 2, MODULE_HEIGHT, BACKGROUND_COLOR);
        }
        if(isMouseOver(mouseX, mouseY, positionX, positionY, MODULE_HEIGHT)) {
            RenderUtility.drawRect(positionX + 1, positionY, PANEL_WIDTH - 2, MODULE_HEIGHT, HOVER_TINT);
        }
        final String moduleName = module.getName().toLowerCase().replace(" ", "");
        final int textColor =
                extended ? (module.isEnabled() ? getCategoryColor(category) : TEXT_COLOR) : TEXT_COLOR;
        fontMedium.drawString(moduleName, positionX + 4, positionY + 5, textColor);
    }

    private void renderSettingButton(
            final Setting<?> setting,
            final int positionX,
            final int positionY,
            final Category category,
            final int mouseX,
            final int mouseY) {
        final int settingX = positionX + SETTING_INDENT;
        final int settingWidth = PANEL_WIDTH - SETTING_INDENT * 2;
        RenderUtility.drawRect(
                positionX + 1, positionY, PANEL_WIDTH - 2, SETTING_HEIGHT, SETTING_BACKGROUND_COLOR);
        final int textX = settingX + 3;
        final int settingRight = positionX + PANEL_WIDTH - SETTING_INDENT;
        if(setting instanceof BooleanSetting) {
            final BooleanSetting booleanSetting = (BooleanSetting) setting;
            if(booleanSetting.isEnabled()) {
                RenderUtility.drawRect(
                        settingX, positionY, settingWidth, SETTING_HEIGHT, getCategoryColor(category));
            }
            if(isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT)) {
                RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, HOVER_TINT);
            }
            fontSmall.drawString(setting.getName(), textX, positionY + 3, TEXT_COLOR);
        } else if(setting instanceof ModeSetting) {
            final ModeSetting modeSetting = (ModeSetting) setting;
            fontSmall.drawString(setting.getName(), textX, positionY + 3, TEXT_COLOR);
            final String modeValue = modeSetting.getValue();
            final float modeWidth = fontSmall.getStringWidth(modeValue);
            fontSmall.drawString(modeValue, settingRight - modeWidth - 2, positionY + 3, TEXT_COLOR);
            if(isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT)) {
                RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, HOVER_TINT);
            }
        } else if(setting instanceof NumberSetting) {
            final NumberSetting numberSetting = (NumberSetting) setting;
            numberSettingPositions.put(numberSetting, positionX);
            final double value = numberSetting.getValue();
            final double minimum = numberSetting.getMin();
            final double maximum = numberSetting.getMax();
            final int sliderWidth = settingWidth;
            final int categoryColor = getCategoryColor(category);
            final int nubWidth = 4;
            final int nubColor = lightenColor(categoryColor, 0.4f);
            if(numberSetting.isRangeEnabled()) {
                final double secondValue = numberSetting.getSecondValue();
                final double loVal = Math.min(value, secondValue);
                final double hiVal = Math.max(value, secondValue);
                final double loPercentage = (loVal - minimum) / (maximum - minimum);
                final double hiPercentage = (hiVal - minimum) / (maximum - minimum);
                final int fillStart = (int) (loPercentage * sliderWidth);
                final int fillEnd = (int) (hiPercentage * sliderWidth);
                RenderUtility.drawRect(
                        settingX + fillStart, positionY, fillEnd - fillStart, SETTING_HEIGHT, categoryColor);
                final int nub1X =
                        settingX + (int) (((value - minimum) / (maximum - minimum)) * sliderWidth) - 2;
                final int nub2X =
                        settingX + (int) (((secondValue - minimum) / (maximum - minimum)) * sliderWidth) - 2;
                RenderUtility.drawRect(
                        Math.max(settingX, nub1X), positionY, nubWidth, SETTING_HEIGHT, nubColor);
                RenderUtility.drawRect(
                        Math.max(settingX, nub2X), positionY, nubWidth, SETTING_HEIGHT, nubColor);
                final String displayValue =
                        Math.round(loVal * 100.0) / 100.0 + "-" + Math.round(hiVal * 100.0) / 100.0;
                fontSmall.drawString(setting.getName(), textX, positionY + 3, TEXT_COLOR);
                final float valueWidth = fontSmall.getStringWidth(displayValue);
                fontSmall.drawString(
                        displayValue, settingRight - valueWidth - 2, positionY + 3, TEXT_COLOR);
            } else {
                final double percentage = (value - minimum) / (maximum - minimum);
                final int fillWidth = (int) (percentage * sliderWidth);
                RenderUtility.drawRect(settingX, positionY, fillWidth, SETTING_HEIGHT, categoryColor);
                final int nubX = settingX + fillWidth - 2;
                RenderUtility.drawRect(
                        Math.max(settingX, nubX), positionY, nubWidth, SETTING_HEIGHT, nubColor);
                final String displayValue = String.valueOf(Math.round(value * 100.0) / 100.0);
                fontSmall.drawString(setting.getName(), textX, positionY + 3, TEXT_COLOR);
                final float valueWidth = fontSmall.getStringWidth(displayValue);
                fontSmall.drawString(
                        displayValue, settingRight - valueWidth - 2, positionY + 3, TEXT_COLOR);
            }
            if(isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT)) {
                RenderUtility.drawRect(settingX, positionY, sliderWidth, SETTING_HEIGHT, HOVER_TINT);
            }
        }
    }

    private int getCategoryColor(final Category category) {
        return CATEGORY_COLORS.getOrDefault(category, DEFAULT_CATEGORY_COLOR);
    }

    private int lightenColor(final int color, final float amount) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * amount));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * amount));
        int b = Math.min(255, (int) ((color & 0xFF) + (255 - (color & 0xFF)) * amount));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton)
            throws IOException {
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
                final List<Module> modules =
                        Alya.getInstance().getModuleManager().getModulesByCategory(category);
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
                            currentY += SETTING_GROUP_PADDING;
                            for(final Setting<?> setting : module.getSettings()) {
                                if(setting.isVisible()) {
                                    if(isMouseOver(mouseX, mouseY, panelX, currentY, SETTING_HEIGHT)) {
                                        if(setting instanceof NumberSetting && mouseButton == 0) {
                                            currentDraggedNumberSetting = (NumberSetting) setting;
                                            currentDraggedSettingX = panelX;
                                            if(currentDraggedNumberSetting.isRangeEnabled()) {
                                                final int sliderStart = panelX + SETTING_INDENT;
                                                final int sliderWidth = PANEL_WIDTH - SETTING_INDENT * 2;
                                                final double pct =
                                                        (double) Math.max(0, Math.min(mouseX - sliderStart, sliderWidth))
                                                                / sliderWidth;
                                                final double mouseVal =
                                                        currentDraggedNumberSetting.getMin()
                                                                + pct
                                                                * (currentDraggedNumberSetting.getMax()
                                                                - currentDraggedNumberSetting.getMin());
                                                final double distFirst =
                                                        Math.abs(mouseVal - currentDraggedNumberSetting.getValue());
                                                final double distSecond =
                                                        Math.abs(mouseVal - currentDraggedNumberSetting.getSecondValue());
                                                draggingSecondNub = distSecond < distFirst;
                                            } else {
                                                draggingSecondNub = false;
                                            }
                                            updateNumberSettingFromMouse(mouseX);
                                        } else {
                                            handleSettingClick(setting, mouseButton);
                                        }
                                        return;
                                    }
                                    currentY += SETTING_HEIGHT;
                                }
                            }
                            currentY += SETTING_GROUP_PADDING;
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
    protected void mouseClickMove(
            final int mouseX,
            final int mouseY,
            final int clickedMouseButton,
            final long timeSinceLastClick) {
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
        final int settingX = currentDraggedSettingX + SETTING_INDENT;
        final int sliderStart = settingX;
        final int sliderWidth = PANEL_WIDTH - SETTING_INDENT * 2;
        int relativeX = mouseX - sliderStart;
        relativeX = Math.max(0, Math.min(relativeX, sliderWidth));
        final double percentage = (double) relativeX / sliderWidth;
        final double minimum = currentDraggedNumberSetting.getMin();
        final double maximum = currentDraggedNumberSetting.getMax();
        final double increment = currentDraggedNumberSetting.getIncrement();
        double newValue = minimum + (percentage * (maximum - minimum));
        newValue = Math.round(newValue / increment) * increment;
        if(draggingSecondNub) {
            currentDraggedNumberSetting.setSecondValue(newValue);
        } else {
            currentDraggedNumberSetting.setValue(newValue);
        }
    }

    private boolean isMouseOver(
            final int mouseX,
            final int mouseY,
            final int positionX,
            final int positionY,
            final int height) {
        return mouseX >= positionX
                && mouseX <= positionX + ClickGUIScreen.PANEL_WIDTH
                && mouseY >= positionY
                && mouseY <= positionY + height;
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            Alya.getInstance()
                    .getModuleManager()
                    .getModule(ClickGUI.class)
                    .ifPresent(module -> module.setEnabled(false));
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static Map<Category, Integer> getCategoryColors() {
        return CATEGORY_COLORS;
    }
}
