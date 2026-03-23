local moduleTable = alya.modules.register("ESP", "Draws boxes around players through walls", "VISUAL")
local settingRed = moduleTable.addNumberSetting("Red", "", 255, 0, 255, 1)
local settingGreen = moduleTable.addNumberSetting("Green", "", 80,  0, 255, 1)
local settingBlue = moduleTable.addNumberSetting("Blue", "", 80,  0, 255, 1)
local settingAlpha = moduleTable.addNumberSetting("Alpha", "", 255, 0, 255, 1)
local lineW = moduleTable.addNumberSetting("Line Width", "", 1.5, 0.5, 4.0, 0.5)
local showHealth = moduleTable.addBooleanSetting("Health Color", "", true)
local showHitbox = moduleTable.addBooleanSetting("Hitbox Expansion", "", false)
local showName = moduleTable.addBooleanSetting("Show Name", "", true)
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "", true)
local namePositions = {}
alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end
    namePositions = {}
    local pt      = alya.mc.getPartialTicks()
    local players = alya.combat.getAllPlayers()
    for _, p in ipairs(players) do
        if p.isInvisible then goto continue end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(p.name) then goto continue end
        local ix = p.lastX + (p.x - p.lastX) * pt
        local iy = p.lastY + (p.y - p.lastY) * pt
        local iz = p.lastZ + (p.z - p.lastZ) * pt
        local r = math.floor(settingRed.getValue())
        local g = math.floor(settingGreen.getValue())
        local b = math.floor(settingBlue.getValue())
        local a = math.floor(settingAlpha.getValue())
        if showHealth.isEnabled() then
            local hpFrac = math.max(0, math.min(1, p.health / p.maxHealth))
            r = math.floor((1 - hpFrac) * 255)
            g = math.floor(hpFrac * 255)
            b = 0
        end
        local color = alya.render.toARGB(a, r, g, b)
        
        local espWidth = p.width
        if showHitbox.isEnabled() then
            local combatModule = alya.modules.get("HitBoxes")
            if combatModule and combatModule.isEnabled() then
                local expansion = combatModule.getSetting("Expansion")
                if expansion then
                    espWidth = espWidth + (expansion.getValue() * 2.0)
                end
            end
        end

        alya.render.drawBox3D(ix, iy, iz, espWidth, p.height, color, lineW.getValue())
        if showName.isEnabled() then
            local screen = alya.render.worldToScreen(ix, iy + p.height + 0.2, iz)
            if screen ~= nil then
                namePositions[#namePositions + 1] = {
                    name   = p.name,
                    sx     = screen.x,
                    sy     = screen.y,
                }
            end
        end
        ::continue::
    end
end)
alya.events.on("render2d", function(event)
    if not moduleTable.isEnabled() then return end
    if not showName.isEnabled() then return end
    local font = alya.getFontRendererMedium and alya.getFontRendererMedium() or nil
    if not font then return end
    for _, entry in ipairs(namePositions) do
        local w = font.getStringWidth(entry.name)
        font.drawStringWithShadow(entry.name, entry.sx - w * 0.5, entry.sy, 0xFFFFFFFF)
    end
end)
moduleTable.onDisable(function()
    namePositions = {}
end)
