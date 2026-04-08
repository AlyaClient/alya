local moduleTable = alya.modules.register("NoRotate", "Prevents server from setting your rotation", "PLAYER")

local pitch = moduleTable.addBooleanSetting("Pitch", "Lock pitch", true)
local yaw = moduleTable.addBooleanSetting("Yaw", "Lock yaw", true)

local savedYaw = 0
local savedPitch = 0

moduleTable.onEnable(function()
    savedYaw = alya.mc.getYaw()
    savedPitch = alya.mc.getPitch()
end)

moduleTable.onDisable(function()
end)

alya.events.on("packetsend", function(event)
    if not moduleTable.isEnabled() then return end
end)

alya.events.on("packetreceive", function(event)
    if not moduleTable.isEnabled() then return end
    local packetClass = event.getPacketClass()
    
    if packetClass:find("S08") or packetClass:find("PlayerPosLook") then
        if yaw.isEnabled() then
            alya.mc.setCameraYaw(savedYaw)
            alya.combat.setClientRotation(savedYaw, savedPitch)
        end
        if pitch.isEnabled() then
            alya.mc.setCameraPitch(savedPitch)
        end
    end
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    
    if yaw.isEnabled() then
        savedYaw = alya.mc.getYaw()
    end
    if pitch.isEnabled() then
        savedPitch = alya.mc.getPitch()
    end
end)