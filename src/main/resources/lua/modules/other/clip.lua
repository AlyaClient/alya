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


local function parseDistance(args, usage)
    local distance = tonumber(args[1])
    if not distance then
        alya.chat.error("Usage: " .. usage)
        return nil
    end
    return distance
end

alya.commands.register("vclip", "Teleports you vertically by the given blocks", function(args)
    local distance = parseDistance(args, ".vclip <blocks>")
    if not distance then return end
    local mc = alya.mc
    mc.teleport(mc.getPlayerX(), mc.getPlayerY() + distance, mc.getPlayerZ())
    alya.chat.success("Clipped " .. distance .. " blocks vertically")
end)

alya.commands.register("hclip", "Teleports you forward by the given blocks", function(args)
    local distance = parseDistance(args, ".hclip <blocks>")
    if not distance then return end
    local mc = alya.mc
    local yaw = math.rad(mc.getYaw())
    local x = mc.getPlayerX() - math.sin(yaw) * distance
    local z = mc.getPlayerZ() + math.cos(yaw) * distance
    mc.teleport(x, mc.getPlayerY(), z)
    alya.chat.success("Clipped " .. distance .. " blocks horizontally")
end)
