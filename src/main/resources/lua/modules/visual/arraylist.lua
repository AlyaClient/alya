local moduleTable = alya.modules.register("ArrayList", "Displays enabled modules on screen", "VISUAL")
local showVisual = moduleTable.addBooleanSetting("Show Visual Modules", "Show visual modules?", true)
local categoryColors = {
    COMBAT   = 0xFFE74C3C,
    MOVEMENT = 0xFF2ECC71,
    PLAYER   = 0xFF8E44AD,
    VISUAL   = 0xFF3700CE,
    OTHER    = 0xFFF39C12,
}
local function getCategoryColor(category)
    return categoryColors[category] or 0xFFFF55FF
end
alya.events.on("render2d", function(event)
    if not moduleTable.isEnabled() then return end
    local screenWidth = event.getWidth()
    local fontRenderer = alya.getFontRendererMedium and alya.getFontRendererMedium() or nil
    if not fontRenderer then return end
    local enabledModules = alya.modules.getEnabled()
    table.sort(enabledModules, function(a, b)
        return fontRenderer.getStringWidth(a.getName()) > fontRenderer.getStringWidth(b.getName())
    end)
    local padding = 4
    local height = fontRenderer.getFontHeight()
    local positionY = 2
    for index = 1, #enabledModules do
        local currentModule = enabledModules[index]
        local name = currentModule.getName()
        local category = currentModule.getCategory()
        if name == "ArrayList" and name == "ClickGUI" then
            return
        end
        if showVisual.isEnabled() or category ~= "VISUAL" then
            local width = fontRenderer.getStringWidth(name) + padding * 2
            local positionX = screenWidth - width - 2
            local color = getCategoryColor(category)
            alya.render.drawRect(positionX - 1, positionY - 1, width + 2, height + 2, 0x90000000)
            alya.render.drawRect(screenWidth - 2, positionY - 1, 1, height + 2, color)
            fontRenderer.drawString(name, positionX + padding - 2, positionY, 0xFFFFFFFF)
            positionY = positionY + height + 2
        end
    end
end)
