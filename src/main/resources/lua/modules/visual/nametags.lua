local moduleTable = alya.modules.register("Nametags", "Draws player names above their heads", "VISUAL")
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "", true)

local namePositions = {}

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end
    namePositions = {}
    local pt = alya.mc.getPartialTicks()
    local players = alya.combat.getAllPlayers()
    for _, player in ipairs(players) do
        if player.isInvisible then goto continue end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(player.name) then goto continue end
        local ix = player.lastX + (player.x - player.lastX) * pt
        local iy = player.lastY + (player.y - player.lastY) * pt
        local iz = player.lastZ + (player.z - player.lastZ) * pt
        local screen = alya.render.worldToScreen(ix, iy + player.height + 0.2, iz)
        if screen ~= nil then
            namePositions[#namePositions + 1] = {
                name = player.name,
                screenX = screen.x,
                screenY = screen.y,
            }
        end
        ::continue::
    end
end)

alya.events.on("render2d", function(event)
    if not moduleTable.isEnabled() then return end
    local font = alya.getFontRendererMedium and alya.getFontRendererMedium() or nil
    if not font then return end
    for _, entry in ipairs(namePositions) do
        local width = font.getStringWidth(entry.name)
        font.drawStringWithShadow(entry.name, entry.screenX - width * 0.5, entry.screenY, 0xFFFFFFFF)
    end
end)

moduleTable.onDisable(function()
    namePositions = {}
end)
