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
