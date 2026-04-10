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

movement = {
    isMoving = function() return alya.movement.isMoving() end,
    jump = function() alya.movement.jump() end,
    getMoveYaw = function() return alya.movement.getMoveYaw() end,
    getMoveSpeed = function() return alya.movement.getMoveSpeed() end,
    getDirection = function() return alya.movement.getDirection() end,
    getSpeed = function() return alya.movement.getSpeed() end,
    getAllowedHDistNCP = function() return alya.movement.getAllowedHDistNCP() end,
    setSpeed = function(speed) alya.movement.setSpeed(speed) end,
    setSpeedStrafe = function(speed, strafe) alya.movement.setSpeedStrafe(speed, strafe) end,
    SPRINT_SPEED = alya.movement.SPRINT_SPEED,
    WALK_SPEED = alya.movement.WALK_SPEED,
}
