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

local moduleTable = alya.modules.register("Nuker", "Break blocks around you", "PLAYER")

local range = moduleTable.addNumberSetting("Range", "Maximum distance to break blocks", 6, 1, 10, 0.5)
local delay = moduleTable.addNumberSetting("Delay", "Delay between break attempts (ms)", 50, 0, 500, 10)
local verticalMode = moduleTable.addModeSetting("Vertical", "Vertical range mode", "Both", "Down", "Up", "Both")
local horizontalMode = moduleTable.addModeSetting("Horizontal", "Horizontal range mode", "Circle", "Circle", "Square")
local nukeIt = moduleTable.addBooleanSetting("NukeIt", "Break all blocks at once", false)
local ignoreAir = moduleTable.addBooleanSetting("IgnoreAir", "Skip air blocks in calculation", true)

local timer = alya.timer.create()
local currentTarget = nil

local function isBlockValid(x, y, z)
    if alya.mc.isPlayerNull() or alya.mc.isWorldNull() then return false end

    if ignoreAir.isEnabled() and alya.mc.isBlockAir(x, y, z) then
        return false
    end

    return true
end

local function getBlocksInRange()
    local blocks = {}
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local rangeVal = range.getValue()
    local vert = verticalMode.getValue()
    local horiz = horizontalMode.getValue()

    local minY, maxY
    if vert == "Down" then
        minY = math.floor(playerY) - 1
        maxY = math.floor(playerY)
    elseif vert == "Up" then
        minY = math.floor(playerY)
        maxY = math.floor(playerY) + 1
    else
        minY = math.floor(playerY) - 1
        maxY = math.floor(playerY) + 1
    end

    for x = math.floor(playerX) - rangeVal, math.floor(playerX) + rangeVal do
        for z = math.floor(playerZ) - rangeVal, math.floor(playerZ) + rangeVal do
            for y = minY, maxY do
                local dx = x - math.floor(playerX)
                local dz = z - math.floor(playerZ)

                local inRange
                if horiz == "Circle" then
                    inRange = math.sqrt(dx * dx + dz * dz) <= rangeVal
                else
                    inRange = true
                end

                if inRange and isBlockValid(x, y, z) then
                    local dist = math.sqrt(
                        (x - playerX) * (x - playerX) +
                        (y - playerY) * (y - playerY) +
                        (z - playerZ) * (z - playerZ)
                    )
                    table.insert(blocks, { x = x, y = y, z = z, dist = dist })
                end
            end
        end
    end

    table.sort(blocks, function(a, b) return a.dist < b.dist end)
    return blocks
end

local function breakBlock(x, y, z)
    alya.mc.breakBlock(0, x, y, z, 1)
    alya.mc.breakBlock(1, x, y, z, 1)
end

moduleTable.onEnable(function()
    timer.reset()
    currentTarget = nil
end)

moduleTable.onDisable(function()
    currentTarget = nil
end)

alya.events.on("tick", function(event)
    if not moduleTable.isEnabled() then return end
    if alya.mc.isPlayerNull() or alya.mc.isWorldNull() then return end

    if not timer.hasElapsed(delay.getValueAsInt()) then
        return
    end
    timer.reset()

    local blocks = getBlocksInRange()

    if #blocks == 0 then
        currentTarget = nil
        return
    end

    if nukeIt.isEnabled() then
        for _, block in ipairs(blocks) do
            alya.mc.breakBlock(0, block.x, block.y, block.z, 1)
            alya.mc.breakBlock(1, block.x, block.y, block.z, 1)
        end
    else
        local target = blocks[1]
        if not currentTarget or currentTarget.x ~= target.x or currentTarget.y ~= target.y or currentTarget.z ~= target.z then
            alya.mc.breakBlock(0, target.x, target.y, target.z, 1)
            currentTarget = target
        else
            alya.mc.breakBlock(1, target.x, target.y, target.z, 1)
        end
    end
end)
