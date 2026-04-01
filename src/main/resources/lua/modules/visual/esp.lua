local moduleTable = alya.modules.register("ESP", "Draws boxes around players through walls", "VISUAL")
local settingRed = moduleTable.addNumberSetting("Red", "", 255, 0, 255, 1)
local settingGreen = moduleTable.addNumberSetting("Green", "", 80, 0, 255, 1)
local settingBlue = moduleTable.addNumberSetting("Blue", "", 80, 0, 255, 1)
local settingAlpha = moduleTable.addNumberSetting("Alpha", "", 255, 0, 255, 1)
local lineWidth = moduleTable.addNumberSetting("Line Width", "", 1.5, 0.5, 4.0, 0.5)
local showHealth = moduleTable.addBooleanSetting("Health Color", "", true)
local showHitbox = moduleTable.addBooleanSetting("Hitbox Expansion", "", false)
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "", true)

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end
    local pt = alya.mc.getPartialTicks()
    local players = alya.combat.getAllPlayers()
    for _, player in ipairs(players) do
        if player.isInvisible then goto continue end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(player.name) then goto continue end
        local ix = player.lastX + (player.x - player.lastX) * pt
        local iy = player.lastY + (player.y - player.lastY) * pt
        local iz = player.lastZ + (player.z - player.lastZ) * pt
        local red = math.floor(settingRed.getValue())
        local green = math.floor(settingGreen.getValue())
        local blue = math.floor(settingBlue.getValue())
        local alpha = math.floor(settingAlpha.getValue())
        if showHealth.isEnabled() then
            local healthFraction = math.max(0, math.min(1, player.health / player.maxHealth))
            red = math.floor((1 - healthFraction) * 255)
            green = math.floor(healthFraction * 255)
            blue = 0
        end
        local color = alya.render.toARGB(alpha, red, green, blue)
        local espWidth = player.width
        if showHitbox.isEnabled() then
            local hitboxesModule = alya.modules.get("HitBoxes")
            if hitboxesModule and hitboxesModule.isEnabled() then
                local expansion = hitboxesModule.getSetting("Expansion")
                if expansion then
                    espWidth = espWidth + (expansion.getValue() * 2.0)
                end
            end
        end
        alya.render.drawBox3D(ix, iy, iz, espWidth, player.height, color, lineWidth.getValue())
        ::continue::
    end
end)
