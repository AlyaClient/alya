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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import dev.thoq.gui.toast.Toast;
import dev.thoq.gui.toast.ToastManager;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "SameParameterValue"})
public final class ClickGUIScreen extends GuiScreen {

    private static final int PANEL_WIDTH = 110;
    private static final int PANEL_HEIGHT = 20;
    private static final int MODULE_HEIGHT = 16;
    private static final int SETTING_HEIGHT = 10;
    private static final int SETTING_GROUP_PADDING = 2;
    private static final int PANEL_SPACING = 130;
    private static final int SETTING_INDENT = 6;
    private static final int BACKGROUND_COLOR = 0xFF181A17;
    private static final int MODULE_BACKGROUND_COLOR = 0xFF232623;
    private static final int SETTING_BACKGROUND_COLOR = 0xFF111311;
    private static final float HOVER_DIM = 0.6F;
    private static final int TEXT_COLOR = 0xFFCCCCCC;
    private static final float BORDER_WIDTH = 0.7F;
    private static final int DEFAULT_CATEGORY_COLOR = 0x20444444;
    private static final int ICON_SIZE = 7;
    private static final int BOTTOM_ICON_SIZE = 7;

    private static final AlyaFontRenderer font = new AlyaFontRenderer("client/fonts/Lato-Bold.ttf", 7.5F);
    private static final AlyaFontRenderer settingsFont = new AlyaFontRenderer("client/fonts/Lato-Bold.ttf", 6.5F);
    private static final Map<Category, Integer> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put(Category.COMBAT, 0xFFE74C3C);
        CATEGORY_COLORS.put(Category.MOVEMENT, 0xFF219150);
        CATEGORY_COLORS.put(Category.PLAYER, 0xFF8E44AD);
        CATEGORY_COLORS.put(Category.VISUAL, 0xFF3700CE);
        CATEGORY_COLORS.put(Category.EXPLOIT, 0xFF4697DB);
        CATEGORY_COLORS.put(Category.OTHER, 0xFFF39C12);
        CATEGORY_COLORS.put(Category.SCRIPTS, 0xFFFADB5F);
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
    private boolean scriptsExpanded = false;
    private boolean configsExpanded = false;

    private float getScale() {
        float userScale = Alya.getInstance()
                .getModuleManager()
                .getModule(ClickGUI.class)
                .map(module -> module.scale.getValue().floatValue())
                .orElse(1.0f);
        
        float maxScaleX = (float) width / (Category.values().length * PANEL_SPACING + 4);
        
        int maxHeight = 0;
        for(final Category category : Category.values()) {
            final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);
            if(modules == null) {
                continue;
            }
            int totalHeight = PANEL_HEIGHT;
            if(expandedCategories.getOrDefault(category, false)) {
                totalHeight += calculateExpandedHeight(modules) + 1;
            }
            if(totalHeight > maxHeight) {
                maxHeight = totalHeight;
            }
        }
        float maxScaleY = maxHeight == 0 ? userScale : (float) height / (maxHeight + 8);
        
        return Math.min(userScale, Math.min(maxScaleX, maxScaleY));
    }

    public ClickGUIScreen() {
        int positionX = 4;
        for(final Category category : Category.values()) {
            panelPositions.put(category, new int[]{positionX, 4});
            expandedCategories.put(category, false);
            positionX += PANEL_SPACING;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);
        final float scale = getScale();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0f);
        final int scaledMouseX = (int) (mouseX / scale);
        final int scaledMouseY = (int) (mouseY / scale);
        for(final Category category : Category.values()) {
            renderCategoryPanel(category, scaledMouseX, scaledMouseY);
        }
        GlStateManager.popMatrix();
        renderBottomBar(mouseX, mouseY);
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
            totalHeight += calculateExpandedHeight(modules) + 1;
        }
        renderPanelHeader(category, panelX, panelY);
        if(expandedCategories.get(category)) {
            renderExpandedModules(modules, panelX, panelY + PANEL_HEIGHT, category, mouseX, mouseY);
        }
        RenderUtility.drawRectOutline(panelX, panelY, PANEL_WIDTH, totalHeight, getCategoryColor(category), BORDER_WIDTH);
    }

    private void renderPanelHeader(final Category category, final int panelX, final int panelY) {
        RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, BACKGROUND_COLOR);
        final String categoryName = category.getDisplayName().toLowerCase();
        font.drawString(categoryName, panelX + 4, panelY + 5, TEXT_COLOR);
        final int categoryColor = getCategoryColor(category);
        final float r = (categoryColor >> 16 & 0xFF) / 255.0F;
        final float g = (categoryColor >> 8 & 0xFF) / 255.0F;
        final float b = (categoryColor & 0xFF) / 255.0F;
        final int iconY = panelY + (PANEL_HEIGHT - ICON_SIZE) / 2;
        GlStateManager.color(r, g, b, 1.0F);
        RenderUtility.drawImage(
                new ResourceLocation("client/icons/categories/" + categoryName + "_good.png"),
                panelX + PANEL_WIDTH - ICON_SIZE - 4, iconY, ICON_SIZE, ICON_SIZE);
        final boolean expanded = expandedCategories.get(category);
        if(expanded) {
            GlStateManager.color(r, g, b, 1.0F);
        } else {
            GlStateManager.color(46 / 255.0F, 46 / 255.0F, 46 / 255.0F, 1.0F);
        }
        final String eyeIconName = expanded ? "eye_open.png" : "eye_close.png";
        RenderUtility.drawImage(
                new ResourceLocation("client/icons/" + eyeIconName),
                panelX + PANEL_WIDTH - (ICON_SIZE * 2) - 6, iconY, ICON_SIZE, ICON_SIZE);
    }

    private int calculateExpandedHeight(final List<Module> modules) {
        int height = 0;
        for(final Module module : modules) {
            height += MODULE_HEIGHT;
            if(expandedModules.getOrDefault(module, false)) {
                height += calculateModuleSettingsHeight(module);
            }
        }
        return height;
    }

    private int calculateModuleSettingsHeight(final Module module) {
        int visibleCount = 0;
        for(final Setting<?> setting : module.getSettings()) {
            if(setting.isVisible()) visibleCount++;
        }
        if(visibleCount == 0) return 0;
        return visibleCount * SETTING_HEIGHT + SETTING_GROUP_PADDING * 2;
    }

    private int countVisibleSettings(final Module module) {
        int count = 0;
        for(final Setting<?> setting : module.getSettings()) {
            if(setting.isVisible()) count++;
        }
        return count;
    }

    private void renderExpandedModules(
            final List<Module> modules,
            final int panelX,
            final int startY,
            final Category category,
            final int mouseX,
            final int mouseY) {
        int currentY = startY;
        for(final Module module : modules) {
            renderModuleButton(module, panelX, currentY, category, mouseX, mouseY);
            currentY += MODULE_HEIGHT;
            if(expandedModules.getOrDefault(module, false)) {
                currentY = renderModuleSettings(module, panelX, currentY, category, mouseX, mouseY);
            }
        }
    }

    private int renderModuleSettings(
            final Module module,
            final int panelX,
            final int startY,
            final Category category,
            final int mouseX,
            final int mouseY) {
        int currentY = startY;
        RenderUtility.drawRect(panelX + 1, currentY, PANEL_WIDTH - 2, SETTING_GROUP_PADDING, SETTING_BACKGROUND_COLOR);
        currentY += SETTING_GROUP_PADDING;
        for(final Setting<?> setting : module.getSettings()) {
            if(!setting.isVisible()) continue;
            renderSettingButton(setting, panelX, currentY, category, mouseX, mouseY);
            currentY += SETTING_HEIGHT;
        }
        RenderUtility.drawRect(panelX + 1, currentY, PANEL_WIDTH - 2, SETTING_GROUP_PADDING, SETTING_BACKGROUND_COLOR);
        currentY += SETTING_GROUP_PADDING;
        return currentY;
    }

    private void renderModuleButton(
            final Module module,
            final int positionX,
            final int positionY,
            final Category category,
            final int mouseX,
            final int mouseY) {
        final boolean extended = expandedModules.getOrDefault(module, false);
        final boolean hovered = isMouseOver(mouseX, mouseY, positionX, positionY, MODULE_HEIGHT);
        if(!extended) {
            int backgroundColor = module.isEnabled() ? getCategoryColor(category) : MODULE_BACKGROUND_COLOR;
            if(hovered) backgroundColor = dimColor(backgroundColor, HOVER_DIM);
            RenderUtility.drawRect(positionX + 1, positionY, PANEL_WIDTH - 2, MODULE_HEIGHT, backgroundColor);
        } else {
            RenderUtility.drawRect(positionX + 1, positionY, PANEL_WIDTH - 2, MODULE_HEIGHT, BACKGROUND_COLOR);
        }
        final String moduleName = module.getName().toLowerCase().replace(" ", "");
        final int textColor =
                extended ? (module.isEnabled() ? getCategoryColor(category) : TEXT_COLOR) : TEXT_COLOR;
        font.drawString(moduleName, positionX + 4, positionY + 5, textColor);
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
        if(setting instanceof BooleanSetting booleanSetting) {
            final boolean boolHovered = isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT);
            if(booleanSetting.isEnabled()) {
                int color = getCategoryColor(category);
                if(boolHovered) color = dimColor(color, HOVER_DIM);
                RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, color);
            } else if(boolHovered) {
                RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, dimColor(SETTING_BACKGROUND_COLOR, HOVER_DIM));
            }
            settingsFont.drawString(setting.getName(), textX, positionY + 1, TEXT_COLOR);
        } else if(setting instanceof ModeSetting modeSetting) {
            settingsFont.drawString(setting.getName(), textX, positionY + 1, TEXT_COLOR);
            final String modeValue = modeSetting.getValue();
            final float modeWidth = settingsFont.getStringWidth(modeValue);
            settingsFont.drawString(modeValue, settingRight - modeWidth - 2, positionY + 1, TEXT_COLOR);
            if(isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT)) {
                RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, dimColor(SETTING_BACKGROUND_COLOR, HOVER_DIM));
            }
        } else if(setting instanceof NumberSetting numberSetting) {
            renderNumberSetting(numberSetting, setting, positionX, positionY, settingX, settingWidth, settingRight, textX, category, mouseX, mouseY);
        }
    }

    private void renderNumberSetting(
            final NumberSetting numberSetting,
            final Setting<?> setting,
            final int positionX,
            final int positionY,
            final int settingX,
            final int settingWidth,
            final int settingRight,
            final int textX,
            final Category category,
            final int mouseX,
            final int mouseY) {
        numberSettingPositions.put(numberSetting, positionX);
        final double value = numberSetting.getValue();
        final double minimum = numberSetting.getMin();
        final double maximum = numberSetting.getMax();
        final int categoryColor = getCategoryColor(category);
        final int nubWidth = 4;
        final int nubColor = lightenColor(categoryColor, 0.4f);
        if(numberSetting.isRangeEnabled()) {
            renderRangeNumberSetting(numberSetting, setting, positionY, settingX, settingWidth, settingRight, textX, value, minimum, maximum, categoryColor, nubWidth, nubColor);
        } else {
            renderSingleNumberSetting(setting, positionY, settingX, settingWidth, settingRight, textX, value, minimum, maximum, categoryColor, nubWidth, nubColor);
        }
        if(isMouseOver(mouseX, mouseY, positionX, positionY, SETTING_HEIGHT)) {
            RenderUtility.drawRect(settingX, positionY, settingWidth, SETTING_HEIGHT, dimColor(SETTING_BACKGROUND_COLOR, HOVER_DIM));
        }
    }

    private void renderRangeNumberSetting(
            final NumberSetting numberSetting,
            final Setting<?> setting,
            final int positionY,
            final int settingX,
            final int settingWidth,
            final int settingRight,
            final int textX,
            final double value,
            final double minimum,
            final double maximum,
            final int categoryColor,
            final int nubWidth,
            final int nubColor) {
        final double secondValue = numberSetting.getSecondValue();
        final double loVal = Math.min(value, secondValue);
        final double hiVal = Math.max(value, secondValue);
        final double loPercentage = (loVal - minimum) / (maximum - minimum);
        final double hiPercentage = (hiVal - minimum) / (maximum - minimum);
        final int fillStart = (int) (loPercentage * settingWidth);
        final int fillEnd = (int) (hiPercentage * settingWidth);
        RenderUtility.drawRect(settingX + fillStart, positionY, fillEnd - fillStart, SETTING_HEIGHT, categoryColor);
        final int nub1X = settingX + (int) (((value - minimum) / (maximum - minimum)) * settingWidth) - 2;
        final int nub2X = settingX + (int) (((secondValue - minimum) / (maximum - minimum)) * settingWidth) - 2;
        RenderUtility.drawRect(Math.max(settingX, nub1X), positionY, nubWidth, SETTING_HEIGHT, nubColor);
        RenderUtility.drawRect(Math.max(settingX, nub2X), positionY, nubWidth, SETTING_HEIGHT, nubColor);
        final String displayValue = Math.round(loVal * 100.0) / 100.0 + "-" + Math.round(hiVal * 100.0) / 100.0;
        settingsFont.drawString(setting.getName(), textX, positionY + 1, TEXT_COLOR);
        final float valueWidth = settingsFont.getStringWidth(displayValue);
        settingsFont.drawString(displayValue, settingRight - valueWidth - 2, positionY + 1, TEXT_COLOR);
    }

    private void renderSingleNumberSetting(
            final Setting<?> setting,
            final int positionY,
            final int settingX,
            final int settingWidth,
            final int settingRight,
            final int textX,
            final double value,
            final double minimum,
            final double maximum,
            final int categoryColor,
            final int nubWidth,
            final int nubColor) {
        final double percentage = (value - minimum) / (maximum - minimum);
        final int fillWidth = (int) (percentage * settingWidth);
        RenderUtility.drawRect(settingX, positionY, fillWidth, SETTING_HEIGHT, categoryColor);
        final int nubX = settingX + fillWidth - 2;
        RenderUtility.drawRect(Math.max(settingX, nubX), positionY, nubWidth, SETTING_HEIGHT, nubColor);
        final String displayValue = String.valueOf(Math.round(value * 100.0) / 100.0);
        settingsFont.drawString(setting.getName(), textX, positionY + 1, TEXT_COLOR);
        final float valueWidth = settingsFont.getStringWidth(displayValue);
        settingsFont.drawString(displayValue, settingRight - valueWidth - 2, positionY + 1, TEXT_COLOR);
    }

    private void renderBottomBar(final int mouseX, final int mouseY) {
        final int configsPanelX = width - PANEL_WIDTH;
        final int scriptsPanelX = configsPanelX - PANEL_WIDTH;
        final int panelY = height - PANEL_HEIGHT;

        renderBottomPanel("scripts", scriptsPanelX, panelY, scriptsExpanded, getScriptEntries(), new String[0],
                "reload.png", "folder.png", "lua.png", mouseX, mouseY, true);
        renderBottomPanel("configs", configsPanelX, panelY, configsExpanded, getConfigEntries(), getConfigDates(),
                null, "folder.png", "options.png", mouseX, mouseY, false);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void renderBottomPanel(
            final String title,
            final int panelX,
            final int panelY,
            final boolean expanded,
            final String[] entries,
            final String[] entryDates,
            final String actionIcon,
            final String folderIcon,
            final String typeIcon,
            final int mouseX,
            final int mouseY,
            final boolean isScripts) {
        RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, BACKGROUND_COLOR);
        font.drawString(title, panelX + 4, panelY + 5, TEXT_COLOR);

        int iconX = panelX + PANEL_WIDTH - BOTTOM_ICON_SIZE - 3;
        final int iconY = panelY + (PANEL_HEIGHT - BOTTOM_ICON_SIZE) / 2;

        GlStateManager.color(0.6F, 0.6F, 0.6F, 1.0F);
        RenderUtility.drawImage(new ResourceLocation("client/icons/" + typeIcon), iconX, iconY, BOTTOM_ICON_SIZE, BOTTOM_ICON_SIZE);
        iconX -= BOTTOM_ICON_SIZE + 2;

        final boolean folderHovered = mouseX >= iconX && mouseX <= iconX + BOTTOM_ICON_SIZE
                && mouseY >= iconY && mouseY <= iconY + BOTTOM_ICON_SIZE;
        GlStateManager.color(folderHovered ? 1.0F : 0.6F, folderHovered ? 1.0F : 0.6F, folderHovered ? 1.0F : 0.6F, 1.0F);
        RenderUtility.drawImage(new ResourceLocation("client/icons/" + folderIcon), iconX, iconY, BOTTOM_ICON_SIZE, BOTTOM_ICON_SIZE);
        iconX -= BOTTOM_ICON_SIZE + 2;

        if(actionIcon != null) {
            final boolean actionHovered = mouseX >= iconX && mouseX <= iconX + BOTTOM_ICON_SIZE
                    && mouseY >= iconY && mouseY <= iconY + BOTTOM_ICON_SIZE;
            GlStateManager.color(actionHovered ? 1.0F : 0.6F, actionHovered ? 1.0F : 0.6F, actionHovered ? 1.0F : 0.6F, 1.0F);
            RenderUtility.drawImage(new ResourceLocation("client/icons/" + actionIcon), iconX, iconY, BOTTOM_ICON_SIZE, BOTTOM_ICON_SIZE);
        }

        int totalHeight = PANEL_HEIGHT;
        if(expanded && entries.length > 0) {
            final int entryTotalHeight = entries.length * MODULE_HEIGHT;
            totalHeight += entryTotalHeight;
            int entryY = panelY - entryTotalHeight;
            for(int i = 0; i < entries.length; i++) {
                final String entry = entries[i];
                final boolean hovered = mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                        && mouseY >= entryY && mouseY <= entryY + MODULE_HEIGHT;

                if(isScripts) {
                    final boolean loaded = Alya.getInstance().getLuaEngine().isExternalScriptLoaded(entry);
                    int bgColor = loaded ? 0xFF808080 : MODULE_BACKGROUND_COLOR;
                    if(hovered) bgColor = dimColor(bgColor, HOVER_DIM);
                    RenderUtility.drawRect(panelX, entryY, PANEL_WIDTH, MODULE_HEIGHT, bgColor);
                } else {
                    int bgColor = MODULE_BACKGROUND_COLOR;
                    if(hovered) bgColor = dimColor(bgColor, HOVER_DIM);
                    RenderUtility.drawRect(panelX, entryY, PANEL_WIDTH, MODULE_HEIGHT, bgColor);
                }

                font.drawString(entry, panelX + 4, entryY + 5, TEXT_COLOR);

                if(!isScripts && i < entryDates.length && entryDates[i] != null) {
                    final float dateWidth = settingsFont.getStringWidth(entryDates[i]);
                    settingsFont.drawString(entryDates[i], panelX + PANEL_WIDTH - dateWidth - 4, entryY + 5, 0xFF888888);
                }

                entryY += MODULE_HEIGHT;
            }
        }
        final int outlineY = expanded && entries.length > 0 ? panelY - (totalHeight - PANEL_HEIGHT) : panelY;
        RenderUtility.drawRectOutline(panelX, outlineY, PANEL_WIDTH, totalHeight, 0xFF808080, BORDER_WIDTH);
    }

    private String[] getScriptEntries() {
        final File scriptsDir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName() + "/scripts");
        if(!scriptsDir.exists()) return new String[0];
        final File[] files = scriptsDir.listFiles((_, name) -> name.endsWith(".lua"));
        if(files == null) return new String[0];
        final String[] names = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    private String[] getConfigEntries() {
        final File configDir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName() + "/configs");
        if(!configDir.exists()) return new String[0];
        final File[] files = configDir.listFiles((_, name) -> name.endsWith(".json"));
        if(files == null) return new String[0];
        final String[] names = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            names[i] = files[i].getName().replace(".json", "");
        }
        return names;
    }

    private String[] getConfigDates() {
        final File configDir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName() + "/configs");
        if(!configDir.exists()) return new String[0];
        final File[] files = configDir.listFiles((_, name) -> name.endsWith(".json"));
        if(files == null) return new String[0];
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final String[] dates = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            dates[i] = dateFormat.format(new Date(files[i].lastModified()));
        }
        return dates;
    }

    private boolean handleBottomBarClick(final int mouseX, final int mouseY, final int mouseButton) {
        final int configsPanelX = width - PANEL_WIDTH;
        final int scriptsPanelX = configsPanelX - PANEL_WIDTH;
        final int panelY = height - PANEL_HEIGHT;

        if(mouseX >= scriptsPanelX && mouseX <= scriptsPanelX + PANEL_WIDTH
                && mouseY >= panelY && mouseY <= panelY + PANEL_HEIGHT) {
            if(mouseButton == 1) {
                scriptsExpanded = !scriptsExpanded;
                return true;
            }
            if(mouseButton == 0) {
                return handleBottomPanelIconClick(mouseX, scriptsPanelX, true);
            }
        }

        if(mouseX >= configsPanelX && mouseX <= configsPanelX + PANEL_WIDTH
                && mouseY >= panelY && mouseY <= panelY + PANEL_HEIGHT) {
            if(mouseButton == 1) {
                configsExpanded = !configsExpanded;
                return true;
            }
            if(mouseButton == 0) {
                return handleBottomPanelIconClick(mouseX, configsPanelX, false);
            }
        }

        if(scriptsExpanded) {
            final String[] entries = getScriptEntries();
            if(handleBottomEntryClick(entries, scriptsPanelX, panelY, mouseX, mouseY, mouseButton, true)) return true;
        }

        if(configsExpanded) {
            final String[] entries = getConfigEntries();
            return handleBottomEntryClick(entries, configsPanelX, panelY, mouseX, mouseY, mouseButton, false);
        }

        return false;
    }

    private boolean handleBottomPanelIconClick(final int mouseX, final int panelX, final boolean isScripts) {
        int iconX = panelX + PANEL_WIDTH - BOTTOM_ICON_SIZE - 3;

        iconX -= BOTTOM_ICON_SIZE + 2;

        if(mouseX >= iconX && mouseX <= iconX + BOTTOM_ICON_SIZE) {
            try {
                final String subDir = isScripts ? "scripts" : "configs";
                final File dir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName() + "/" + subDir);
                Desktop.getDesktop().open(dir);
            } catch(final IOException ignored) {}
            return true;
        }
        iconX -= BOTTOM_ICON_SIZE + 2;

        if(isScripts && mouseX >= iconX && mouseX <= iconX + BOTTOM_ICON_SIZE) {
            Alya.getInstance().getLuaEngine().reload();
            ToastManager.getInstance().push(Toast.Type.INFO, Toast.Side.LEFT, "Scripts", "Reloaded scripts");
            return true;
        }

        return false;
    }

    private boolean handleBottomEntryClick(
            final String[] entries, final int panelX, final int panelY,
            final int mouseX, final int mouseY, final int mouseButton, final boolean isScripts) {
        final int entryTotalHeight = entries.length * MODULE_HEIGHT;
        int entryY = panelY - entryTotalHeight;
        for(final String entry : entries) {
            if(mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                    && mouseY >= entryY && mouseY <= entryY + MODULE_HEIGHT) {
                if(mouseButton == 0) {
                    if(isScripts) {
                        Alya.getInstance().getLuaEngine().toggleExternalScript(entry);
                    } else {
                        Alya.getInstance().getConfigManager().load(entry);
                        Alya.getInstance().getModuleManager().getModule(ClickGUI.class)
                                .ifPresent(m -> m.setEnabled(true));
                        mc.displayGuiScreen(this);
                        ToastManager.getInstance().push(Toast.Type.INFO, Toast.Side.LEFT, "Config", "Loaded config: " + entry);
                    }
                }
                return true;
            }
            entryY += MODULE_HEIGHT;
        }
        return false;
    }

    private int getCategoryColor(final Category category) {
        return CATEGORY_COLORS.getOrDefault(category, DEFAULT_CATEGORY_COLOR);
    }

    private int dimColor(final int color, final float factor) {
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    @SuppressWarnings("SameParameterValue")
    private int lightenColor(final int color, final float amount) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * amount));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * amount));
        int b = Math.min(255, (int) ((color & 0xFF) + (255 - (color & 0xFF)) * amount));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton)
            throws IOException {
        final float scale = getScale();
        final int scaledMouseX = (int) (mouseX / scale);
        final int scaledMouseY = (int) (mouseY / scale);

        if(handleBottomBarClick(mouseX, mouseY, mouseButton)) return;
        for(final Category category : Category.values()) {
            if(handleCategoryClick(category, scaledMouseX, scaledMouseY, mouseButton)) return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean handleCategoryClick(
            final Category category, final int mouseX, final int mouseY, final int mouseButton) {
        final int[] position = panelPositions.get(category);
        if(position == null) return false;
        final int panelX = position[0];
        final int panelY = position[1];
        if(isMouseOver(mouseX, mouseY, panelX, panelY, PANEL_HEIGHT)) {
            handlePanelHeaderClick(category, panelX, panelY, mouseX, mouseY, mouseButton);
            return true;
        }
        if(!expandedCategories.get(category)) return false;
        return handleExpandedModulesClick(category, panelX, panelY, mouseX, mouseY, mouseButton);
    }

    private void handlePanelHeaderClick(
            final Category category,
            final int panelX,
            final int panelY,
            final int mouseX,
            final int mouseY,
            final int mouseButton) {
        if(mouseButton == 0) {
            dragging = true;
            draggingCategory = category;
            dragOffsetX = panelX - mouseX;
            dragOffsetY = panelY - mouseY;
        } else if(mouseButton == 1) {
            expandedCategories.put(category, !expandedCategories.get(category));
        }
    }

    private boolean handleExpandedModulesClick(
            final Category category,
            final int panelX,
            final int panelY,
            final int mouseX,
            final int mouseY,
            final int mouseButton) {
        final List<Module> modules = Alya.getInstance().getModuleManager().getModulesByCategory(category);
        if(modules == null) return false;
        int currentY = panelY + PANEL_HEIGHT;
        for(final Module module : modules) {
            if(isMouseOver(mouseX, mouseY, panelX, currentY, MODULE_HEIGHT)) {
                handleModuleClick(module, mouseButton);
                return true;
            }
            currentY += MODULE_HEIGHT;
            if(expandedModules.getOrDefault(module, false)) {
                currentY += SETTING_GROUP_PADDING;
                if(tryHandleSettingClick(module, panelX, currentY, mouseX, mouseY, mouseButton)) return true;
                currentY += countVisibleSettings(module) * SETTING_HEIGHT + SETTING_GROUP_PADDING;
            }
        }
        return false;
    }

    private void handleModuleClick(final Module module, final int mouseButton) {
        if(mouseButton == 0) {
            if(!(module instanceof ClickGUI)) module.toggle();
        } else if(mouseButton == 1 && countVisibleSettings(module) > 0) {
            expandedModules.put(module, !expandedModules.getOrDefault(module, false));
        }
    }

    private boolean tryHandleSettingClick(
            final Module module,
            final int panelX,
            final int startY,
            final int mouseX,
            final int mouseY,
            final int mouseButton) {
        int currentY = startY;
        for(final Setting<?> setting : module.getSettings()) {
            if(!setting.isVisible()) continue;
            if(isMouseOver(mouseX, mouseY, panelX, currentY, SETTING_HEIGHT)) {
                handleSettingInteraction(setting, panelX, mouseX, mouseButton);
                return true;
            }
            currentY += SETTING_HEIGHT;
        }
        return false;
    }

    private void handleSettingInteraction(
            final Setting<?> setting, final int panelX, final int mouseX, final int mouseButton) {
        if(setting instanceof NumberSetting && mouseButton == 0) {
            currentDraggedNumberSetting = (NumberSetting) setting;
            currentDraggedSettingX = panelX;
            if(currentDraggedNumberSetting.isRangeEnabled()) {
                final double mouseVal = getMouseVal(mouseX, panelX);
                final double distFirst = Math.abs(mouseVal - currentDraggedNumberSetting.getValue());
                final double distSecond = Math.abs(mouseVal - currentDraggedNumberSetting.getSecondValue());
                draggingSecondNub = distSecond < distFirst;
            } else {
                draggingSecondNub = false;
            }
            updateNumberSettingFromMouse(mouseX);
        } else {
            handleSettingClick(setting, mouseButton);
        }
    }

    private double getMouseVal(int mouseX, int panelX) {
        final int sliderStart = panelX + SETTING_INDENT;
        final int sliderWidth = PANEL_WIDTH - SETTING_INDENT * 2;
        final double pct =
                (double) Math.clamp(mouseX - sliderStart, 0, sliderWidth)
                        / sliderWidth;
        return currentDraggedNumberSetting.getMin()
                + pct
                * (currentDraggedNumberSetting.getMax()
                - currentDraggedNumberSetting.getMin());
    }

    private void handleSettingClick(final Setting<?> setting, final int mouseButton) {
        if(setting instanceof BooleanSetting booleanSetting) {
            booleanSetting.toggle();
        } else if(setting instanceof ModeSetting modeSetting) {
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
        final float scale = getScale();
        final int scaledMouseX = (int) (mouseX / scale);
        
        if(dragging && draggingCategory != null) {
            return;
        }
        if(currentDraggedNumberSetting != null) {
            updateNumberSettingFromMouse(scaledMouseX);
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
        if(currentDraggedNumberSetting == null) {
            return;
        }
        final int sliderStart = currentDraggedSettingX + SETTING_INDENT;
        final int sliderWidth = PANEL_WIDTH - SETTING_INDENT * 2;
        double newValue = getNewValue(mouseX, sliderStart, sliderWidth);
        if(draggingSecondNub) {
            currentDraggedNumberSetting.setSecondValue(newValue);
        } else {
            currentDraggedNumberSetting.setValue(newValue);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private double getNewValue(int mouseX, int sliderStart, int sliderWidth) {
        int relativeX = mouseX - sliderStart;
        relativeX = Math.clamp(relativeX, 0, sliderWidth);
        final double percentage = (double) relativeX / sliderWidth;
        final double minimum = currentDraggedNumberSetting.getMin();
        final double maximum = currentDraggedNumberSetting.getMax();
        final double increment = currentDraggedNumberSetting.getIncrement();
        double newValue = minimum + (percentage * (maximum - minimum));
        newValue = Math.round(newValue / increment) * increment;
        return newValue;
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

    @SuppressWarnings("unused")
    public static Map<Category, Integer> getCategoryColors() {
        return CATEGORY_COLORS;
    }


}
