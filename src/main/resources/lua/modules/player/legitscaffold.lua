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

local moduleTable =
	alya.modules.register("LegitScaffold", "Larp as someone with skill", "PLAYER")
local edgeOnly = moduleTable.addBooleanSetting("Edge Only", "Only sneak on block edges", false)
local pitchCheck = moduleTable.addBooleanSetting("Pitch Check", "Only sneak when looking down (~45 deg)", false)
local blockOnly = moduleTable.addBooleanSetting("Block Only", "Only run while holding a block", false)
local delay = moduleTable.addNumberSetting("Delay", "Delay after block placed before unsneaking (ms)", 100, 0, 500, 10)

delay.setRangeEnabled(true)
delay.setSecondValue(200)
local timer = alya.timer.create()
local currentDuration = 0
local sneaking = false
local placed = false
local wasAboveVoid = false

local function nextDuration()
	currentDuration = delay.getRandomValueAsInt()
	timer.reset()
end

alya.events.on("motion", function(event)
	if not moduleTable.isEnabled() then
		return
	end
	if not event.isPre() then
		return
	end
    if blockOnly.isEnabled() and not alya.mc.isHoldingBlock() then
        return
    end

	local aboveVoid = alya.mc.isAboveVoid() and alya.mc.isOnGround() and not alya.mc.isOnLadder()
    if sneaking then
        if not placed and not aboveVoid then
            placed = true
            nextDuration()
        end

        if placed and timer.hasElapsed(currentDuration) then
            alya.mc.setSneakPressed(false)
            sneaking = false
            placed = false
            wasAboveVoid = true
        end
        return
    end

    if wasAboveVoid and aboveVoid then
        return
    end
    wasAboveVoid = false

	local shouldSneak = aboveVoid
    if shouldSneak and edgeOnly.isEnabled() then
        shouldSneak = alya.mc.isOnEdge()
    end

    if shouldSneak and pitchCheck.isEnabled() then
        local pitch = alya.mc.getCameraPitch()
        local yaw = alya.mc.getCameraYaw()

        local pitchOk = alya.mathutil.isBetween(pitch, 45, 90)
        local yawOk = alya.mathutil.isBetween(yaw, yaw - 110, yaw + 110)

        shouldSneak = pitchOk and yawOk
    end

	if shouldSneak then
		sneaking = true
		placed = false
		alya.mc.setSneakPressed(true)
	end
end)

moduleTable.onDisable(function()
	if sneaking then
		alya.mc.setSneakPressed(false)
		sneaking = false
		placed = false
	end
	wasAboveVoid = false
end)
