local moduleTable = alya.modules.register("Knockback", "Buffers attacks during hurt invincibility then releases them for burst knockback", "COMBAT")
local hurtTimeThreshold = moduleTable.addNumberSetting("HurtTime", "Release buffered attacks when target hurtTime reaches this value", 0, 0, 10, 1)
local packetWTap = moduleTable.addBooleanSetting("Packet WTap", "Resets sprint packets for every hit to maximize knockback", true)

local bufferedAttacksByTarget = {}
local shouldRestartSprint = false

alya.events.on("packetsend", function(event)
    if not moduleTable.isEnabled() then return end
    if event.getPacketClass() ~= "C02PacketUseEntity" then return end
    if event.getUseAction() ~= "ATTACK" then return end

    local entityIdentifier = event.getAttackedEntityId()
    local targetEntity = alya.combat.getEntityById(entityIdentifier)

    if targetEntity == nil then return end

    if targetEntity.hurtTime > hurtTimeThreshold.getValue() then
        event.cancel()
        bufferedAttacksByTarget[entityIdentifier] = (bufferedAttacksByTarget[entityIdentifier] or 0) + 1
    end
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end

    if event.isPre() then
        for entityIdentifier, bufferedCount in pairs(bufferedAttacksByTarget) do
            if bufferedCount > 0 then
                local targetEntity = alya.combat.getEntityById(entityIdentifier)

                if targetEntity == nil or targetEntity.isDead then
                    bufferedAttacksByTarget[entityIdentifier] = nil
                elseif targetEntity.hurtTime <= hurtTimeThreshold.getValue() then

                    if packetWTap.getValue() then
                        alya.mc.sendSprintPacket(false)
                    end

                    alya.combat.attackEntity(entityIdentifier)

                    if packetWTap.getValue() then
                        shouldRestartSprint = true
                    end

                    bufferedAttacksByTarget[entityIdentifier] = bufferedCount - 1
                    break
                end
            end
        end
    else
        if shouldRestartSprint then
            alya.mc.sendSprintPacket(true)
            shouldRestartSprint = false
        end
    end
end)

moduleTable.onDisable(function()
    bufferedAttacksByTarget = {}
    shouldRestartSprint = false
end)
