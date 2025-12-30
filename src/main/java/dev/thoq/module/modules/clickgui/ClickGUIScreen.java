package dev.thoq.module.modules.clickgui;

import dev.thoq.Alya;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.module.setting.NumberSetting;
import dev.thoq.module.setting.Setting;
import dev.thoq.module.setting.StringSetting;
import dev.thoq.util.AlyaFontRenderer;
import dev.thoq.util.RenderUtility;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClickGUIScreen extends GuiScreen {

    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEADER_HEIGHT = 22;
    private static final int MODULE_HEIGHT = 20;
    private static final int SETTING_HEIGHT = 18;
    private static final int PANEL_PADDING = 8;
    private static final int PANEL_SPACING = 12;
    private static final int PANEL_BACKGROUND = 0xE0181818;
    private static final int PANEL_HEADER_BACKGROUND = 0xFF101010;
    private static final int MODULE_BACKGROUND = 0xC0202020;
    private static final int SETTING_BACKGROUND = 0xC0151515;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_COLOR_DISABLED = 0xFFAAAAAA;

    private static final Map<Category, Integer> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put(Category.COMBAT, 0xFFE53935);
        CATEGORY_COLORS.put(Category.MOVEMENT, 0xFF43A047);
        CATEGORY_COLORS.put(Category.RENDER, 0xFF1E88E5);
        CATEGORY_COLORS.put(Category.PLAYER, 0xFFFFB300);
        CATEGORY_COLORS.put(Category.WORLD, 0xFF8E24AA);
        CATEGORY_COLORS.put(Category.MISC, 0xFF757575);
    }

    private final Map<Category, int[]> panelPositions = new HashMap<>();
    private Category draggingPanel = null;
    private int dragOffsetX, dragOffsetY;
    private final Map<Category, Boolean> expandedPanels = new HashMap<>();
    private final Map<Module, Boolean> expandedModuleSettings = new HashMap<>();
    private StringSetting editingStringSetting = null;
    private String stringEditBuffer = "";
    private NumberSetting draggingNumberSetting = null;
    private int draggingSliderX = 0;
    private int draggingSliderWidth = 0;

    public ClickGUIScreen() {
        int startX = 10;
        int startY = 20;
        int index = 0;

        for(final Category category : Category.values()) {
            panelPositions.put(category, new int[]{startX + (PANEL_WIDTH + PANEL_SPACING) * index, startY});
            expandedPanels.put(category, true);
            index++;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererSmall();

        for(final Category category : Category.values()) {
            final int[] pos = panelPositions.get(category);
            final int panelX = pos[0];
            final int panelY = pos[1];
            final boolean expanded = expandedPanels.get(category);

            final int categoryColor = CATEGORY_COLORS.getOrDefault(category, 0xFF757575);

            RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, PANEL_HEADER_HEIGHT, PANEL_HEADER_BACKGROUND);
            RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, 2, categoryColor);

            final String categoryName = category.getDisplayName();
            final int textWidth = (int) fontRenderer.getStringWidth(categoryName);
            fontRenderer.drawStringWithShadow(categoryName, panelX + (PANEL_WIDTH - textWidth) / 2f, panelY + 6, TEXT_COLOR);

            final String indicator = expanded ? "-" : "+";
            fontRenderer.drawStringWithShadow(indicator, panelX + PANEL_WIDTH - 10, panelY + 5, TEXT_COLOR);

            if(expanded) {
                final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);

                int bodyHeight = PANEL_PADDING * 2;
                for(final Module module : modules) {
                    bodyHeight += MODULE_HEIGHT;
                    if(expandedModuleSettings.getOrDefault(module, false) && !module.getSettings().isEmpty()) {
                        bodyHeight += module.getSettings().size() * SETTING_HEIGHT;
                    }
                }

                RenderUtility.drawRect(panelX, panelY + PANEL_HEADER_HEIGHT, PANEL_WIDTH, bodyHeight, PANEL_BACKGROUND);

                int moduleY = panelY + PANEL_HEADER_HEIGHT + PANEL_PADDING;
                for(final Module module : modules) {
                    boolean isEnabled = module.isEnabled();
                    boolean isHovered = isMouseOver(mouseX, mouseY, panelX + 2, moduleY, PANEL_WIDTH - 4, MODULE_HEIGHT);
                    boolean hasSettings = !module.getSettings().isEmpty();
                    boolean settingsExpanded = expandedModuleSettings.getOrDefault(module, false);

                    int moduleBgColor;
                    if(isEnabled) {
                        moduleBgColor = RenderUtility.withAlpha(categoryColor, 180);
                    } else if(isHovered) {
                        moduleBgColor = 0x80404040;
                    } else {
                        moduleBgColor = MODULE_BACKGROUND;
                    }

                    RenderUtility.drawRect(panelX + 2, moduleY, PANEL_WIDTH - 4, MODULE_HEIGHT - 1, moduleBgColor);

                    final String moduleName = module.getName();
                    final int moduleTextWidth = (int) fontRenderer.getStringWidth(moduleName);
                    final int textColor = isEnabled ? TEXT_COLOR : TEXT_COLOR_DISABLED;
                    fontRenderer.drawStringWithShadow(moduleName, panelX + PANEL_WIDTH - moduleTextWidth - 8, moduleY + 4, textColor);

                    moduleY += MODULE_HEIGHT;

                    if(settingsExpanded && hasSettings) {
                        for(final Setting<?> setting : module.getSettings()) {
                            final boolean settingHovered = isMouseOver(mouseX, mouseY, panelX + 6, moduleY, PANEL_WIDTH - 12, SETTING_HEIGHT - 1);

                            if(setting instanceof BooleanSetting) {
                                final BooleanSetting boolSetting = (BooleanSetting) setting;
                                int settingBgColor;
                                if(boolSetting.isEnabled()) {
                                    settingBgColor = RenderUtility.withAlpha(categoryColor, 140);
                                } else if(settingHovered) {
                                    settingBgColor = 0x80303030;
                                } else {
                                    settingBgColor = SETTING_BACKGROUND;
                                }
                                RenderUtility.drawRect(panelX + 6, moduleY, PANEL_WIDTH - 12, SETTING_HEIGHT - 1, settingBgColor);

                                final String settingName = setting.getName();
                                final int settingTextWidth = (int) fontRenderer.getStringWidth(settingName);
                                fontRenderer.drawStringWithShadow(settingName, panelX + PANEL_WIDTH - settingTextWidth - 10, moduleY + 3,
                                        boolSetting.isEnabled() ? TEXT_COLOR : TEXT_COLOR_DISABLED);
                            } else if(setting instanceof NumberSetting) {
                                final NumberSetting numSetting = (NumberSetting) setting;
                                final int sliderWidth = PANEL_WIDTH - 12;
                                final int sliderX = panelX + 6;

                                final int settingBgColor = settingHovered ? 0x80303030 : SETTING_BACKGROUND;
                                RenderUtility.drawRect(sliderX, moduleY, sliderWidth, SETTING_HEIGHT - 1, settingBgColor);

                                final double percent = (numSetting.getValue() - numSetting.getMin()) / (numSetting.getMax() - numSetting.getMin());
                                final int filledWidth = (int) (sliderWidth * percent);
                                RenderUtility.drawRect(sliderX, moduleY, filledWidth, SETTING_HEIGHT - 1, RenderUtility.withAlpha(categoryColor, 120));

                                final String settingText = setting.getName() + ": " + String.format("%.1f", numSetting.getValue());
                                final int numSettingTextWidth = (int) fontRenderer.getStringWidth(settingText);
                                fontRenderer.drawStringWithShadow(settingText, panelX + PANEL_WIDTH - numSettingTextWidth - 10, moduleY + 3, TEXT_COLOR);
                            } else if(setting instanceof StringSetting) {
                                final StringSetting strSetting = (StringSetting) setting;
                                final boolean isEditing = editingStringSetting == strSetting;
                                final int settingBgColor = isEditing ? 0x80505050 : (settingHovered ? 0x80303030 : SETTING_BACKGROUND);
                                RenderUtility.drawRect(panelX + 6, moduleY, PANEL_WIDTH - 12, SETTING_HEIGHT - 1, settingBgColor);

                                final String displayValue = isEditing ? stringEditBuffer + "_" : strSetting.getValue();
                                final String settingText = setting.getName() + ": " + displayValue;
                                final int strSettingTextWidth = (int) fontRenderer.getStringWidth(settingText);
                                fontRenderer.drawStringWithShadow(settingText, panelX + PANEL_WIDTH - strSettingTextWidth - 10, moduleY + 3,
                                        isEditing ? TEXT_COLOR : TEXT_COLOR_DISABLED);
                            }

                            moduleY += SETTING_HEIGHT;
                        }
                    }
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for(final Category category : Category.values()) {
            final int[] pos = panelPositions.get(category);
            final int panelX = pos[0];
            final int panelY = pos[1];
            final boolean expanded = expandedPanels.get(category);

            if(mouseButton == 0) {
                if(isMouseOver(mouseX, mouseY, panelX, panelY, PANEL_WIDTH, PANEL_HEADER_HEIGHT)) {
                    if(isMouseOver(mouseX, mouseY, panelX + PANEL_WIDTH - 15, panelY, 15, PANEL_HEADER_HEIGHT)) {
                        expandedPanels.put(category, !expanded);
                    } else {
                        draggingPanel = category;
                        dragOffsetX = mouseX - panelX;
                        dragOffsetY = mouseY - panelY;
                    }
                    return;
                }
            }

            if(expanded) {
                final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);
                int moduleY = panelY + PANEL_HEADER_HEIGHT + PANEL_PADDING;

                for(final Module module : modules) {
                    boolean hasSettings = !module.getSettings().isEmpty();
                    boolean settingsExpanded = expandedModuleSettings.getOrDefault(module, false);

                    if(isMouseOver(mouseX, mouseY, panelX + 2, moduleY, PANEL_WIDTH - 4, MODULE_HEIGHT)) {
                        if(mouseButton == 0) {
                            if(!(module instanceof ClickGUI)) {
                                module.toggle();
                            }
                        } else if(mouseButton == 1) {

                            if(hasSettings) {
                                expandedModuleSettings.put(module, !settingsExpanded);
                            }
                        }
                        return;
                    }
                    moduleY += MODULE_HEIGHT;

                    if(settingsExpanded && hasSettings) {
                        for(final Setting<?> setting : module.getSettings()) {
                            if(isMouseOver(mouseX, mouseY, panelX + 6, moduleY, PANEL_WIDTH - 12, SETTING_HEIGHT - 1)) {
                                if(mouseButton == 0) {
                                    if(setting instanceof BooleanSetting) {
                                        ((BooleanSetting) setting).toggle();
                                    } else if(setting instanceof NumberSetting) {
                                        draggingNumberSetting = (NumberSetting) setting;
                                        draggingSliderX = panelX + 6;
                                        draggingSliderWidth = PANEL_WIDTH - 12;

                                        updateSliderValue(mouseX);
                                    } else if(setting instanceof StringSetting) {
                                        final StringSetting strSetting = (StringSetting) setting;
                                        if(editingStringSetting != strSetting) {
                                            editingStringSetting = strSetting;
                                            stringEditBuffer = strSetting.getValue();
                                        }
                                    }
                                }
                                return;
                            }
                            moduleY += SETTING_HEIGHT;
                        }
                    }
                }
            }
        }

        if(editingStringSetting != null && mouseButton == 0) {
            editingStringSetting.setValue(stringEditBuffer);
            editingStringSetting = null;
            stringEditBuffer = "";
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void updateSliderValue(final int mouseX) {
        if(draggingNumberSetting != null) {
            double percent = (double) (mouseX - draggingSliderX) / draggingSliderWidth;
            percent = Math.max(0, Math.min(1, percent));
            double value = draggingNumberSetting.getMin() + percent * (draggingNumberSetting.getMax() - draggingNumberSetting.getMin());

            double increment = draggingNumberSetting.getIncrement();
            value = Math.round(value / increment) * increment;
            draggingNumberSetting.setValue(value);
        }
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        draggingPanel = null;
        draggingNumberSetting = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if(draggingPanel != null && clickedMouseButton == 0) {
            final int[] pos = panelPositions.get(draggingPanel);
            pos[0] = mouseX - dragOffsetX;
            pos[1] = mouseY - dragOffsetY;
        }

        if(draggingNumberSetting != null && clickedMouseButton == 0) {
            updateSliderValue(mouseX);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {

        if(editingStringSetting != null) {
            if(keyCode == Keyboard.KEY_ESCAPE) {
                editingStringSetting = null;
                stringEditBuffer = "";
                return;
            } else if(keyCode == Keyboard.KEY_RETURN) {
                editingStringSetting.setValue(stringEditBuffer);
                editingStringSetting = null;
                stringEditBuffer = "";
                return;
            } else if(keyCode == Keyboard.KEY_BACK) {
                if(!stringEditBuffer.isEmpty()) {
                    stringEditBuffer = stringEditBuffer.substring(0, stringEditBuffer.length() - 1);
                }
                return;
            } else if(typedChar >= 32 && typedChar < 127) {
                stringEditBuffer += typedChar;
                return;
            }
        }

        if(keyCode == 1) {
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

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    private boolean isMouseOver(final int mouseX, final int mouseY, final int x, final int y, final int width, final int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }


}
