local moduleTable = alya.modules.register("Knockback", "Buffers attacks during hurt invincibility then releases them for burst knockback", "COMBAT")
local hurtTimeThreshold = moduleTable.addNumberSetting("HurtTime", "Release buffered attacks when target hurtTime reaches this value", 0, 0, 10, 1)

local bufferedAttacksByTarget = {}
local currentTargetId = nil

alya.events.on("packetsend", function(event)
    if not moduleTable.isEnabled() then return end
    if event.getPacketClass() ~= "C02PacketUseEntity" then return end
    if event.getUseAction() ~= "ATTACK" then return end

    local entityId = event.getAttackedEntityId()
    if entityId < 0 then return end

    local target = alya.combat.getEntityById(entityId)
    if target == nil then return end

    currentTargetId = entityId

    if target.hurtTime > hurtTimeThreshold.getValue() then
        event.cancel()
        local existing = bufferedAttacksByTarget[entityId] or 0
        bufferedAttacksByTarget[entityId] = existing + 1
    end
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if currentTargetId == nil then return end

    local bufferedCount = bufferedAttacksByTarget[currentTargetId] or 0
    if bufferedCount == 0 then return end

    local target = alya.combat.getEntityById(currentTargetId)
    if target == nil then
        bufferedAttacksByTarget[currentTargetId] = nil
        currentTargetId = nil
        return
    end

    if target.hurtTime <= hurtTimeThreshold.getValue() then
        for i = 1, bufferedCount do
            alya.combat.attackEntity(currentTargetId)
        end
        bufferedAttacksByTarget[currentTargetId] = 0
    end
end)

moduleTable.onDisable(function()
    bufferedAttacksByTarget = {}
    currentTargetId = nil
end)
