local moduleTable = alya.modules.register("Chams", "Highlights player models through walls", "VISUAL")
local red = moduleTable.addNumberSetting("Red", "Red channel", 255, 0, 255, 1)
local green = moduleTable.addNumberSetting("Green", "Green channel", 0, 0, 255, 1)
local blue = moduleTable.addNumberSetting("Blue", "Blue channel", 255, 0, 255, 1)
local alpha = moduleTable.addNumberSetting("Alpha", "Opacity", 180, 0, 255, 1)
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "Skip friends", true)

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end

    local players = alya.combat.getAllPlayers()
    local colorRed = red.getValue() / 255.0
    local colorGreen = green.getValue() / 255.0
    local colorBlue = blue.getValue() / 255.0
    local colorAlpha = alpha.getValue() / 255.0

    for _, player in ipairs(players) do
        if player.isInvisible then goto continue_loop end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(player.name) then goto continue_loop end
        alya.render.renderEntityChams(player.id, colorRed, colorGreen, colorBlue, colorAlpha)
        ::continue_loop::
    end
end)
