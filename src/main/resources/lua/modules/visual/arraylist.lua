--[[
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
--]]

local moduleTable = alya.modules.register("ArrayList", "Displays enabled modules on screen", "VISUAL")
local showVisual = moduleTable.addBooleanSetting("Show Visual Modules", "Show visual modules?", true)
local waveColor = moduleTable.addBooleanSetting("Wave Color", "Enable wave effect?", true)

local function getWaveFactor(index)
    if not waveColor.isEnabled() then return 1.0 end
    return (math.sin(os.clock() * 4 - (index * 0.4)) + 1) / 2 * 0.5 + 0.5
end

local function applyWave(hex, index)
    local factor = getWaveFactor(index)
    local a = bit32.extract(hex, 24, 8)
    local r = math.floor(bit32.extract(hex, 16, 8) * factor)
    local g = math.floor(bit32.extract(hex, 8, 8) * factor)
    local b = math.floor(bit32.extract(hex, 0, 8) * factor)
    return bit32.bor(bit32.lshift(a, 24), bit32.lshift(r, 16), bit32.lshift(g, 8), b)
end

alya.events.on("render2d", function(event)
    if not moduleTable.isEnabled() then return end

    local screenWidth = event.getWidth()
    local fontRenderer = alya.getFontRendererMedium and alya.getFontRendererMedium() or nil
    if not fontRenderer then return end

    local enabledModules = alya.modules.getEnabled()

    table.sort(enabledModules, function(a, b)
        return fontRenderer.getStringWidth(string.lower(a.getName())) >
            fontRenderer.getStringWidth(string.lower(b.getName()))
    end)

    local padding = 4
    local height = fontRenderer.getFontHeight()
    local positionY = 2
    local baseAccent = alya.getAccent()

    for index = 1, #enabledModules do
        local currentModule = enabledModules[index]
        local name = string.lower(currentModule.getName())
        local category = currentModule.getCategory()

        if name ~= "arraylist" and name ~= "clickgui" then
            if showVisual.isEnabled() or category ~= "VISUAL" then
                local width = fontRenderer.getStringWidth(name) + padding * 2
                local positionX = screenWidth - width
                local modColor = applyWave(baseAccent, index)

                alya.visual.drawRect(positionX - 1, positionY - 2, width + 2, height + 2, 0x90000000)
                fontRenderer.drawString(name, positionX + padding - 2, positionY - 1, modColor)

                positionY = positionY + height + 2
            end
        end
    end
end)
