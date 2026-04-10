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

visual = {
    drawRect = function(x, y, width, height, color)
        alya.visual.drawRect(x, y, width, height, color)
    end,
    drawRectOutline = function(x, y, width, height, color, borderWidth)
        alya.visual.drawRectOutline(x, y, width, height, color, borderWidth)
    end,
    drawRoundedRect = function(x, y, width, height, radius, color)
        alya.visual.drawRoundedRect(x, y, width, height, radius, color)
    end,
    drawGradientRect = function(x, y, width, height, colorLeft, colorRight)
        alya.visual.drawGradientRect(x, y, width, height, colorLeft, colorRight)
    end,
    drawVerticalGradientRect = function(x, y, width, height, colorTop, colorBottom)
        alya.visual.drawVerticalGradientRect(x, y, width, height, colorTop, colorBottom)
    end,
    drawLine = function(x1, y1, x2, y2, lineWidth, color)
        alya.visual.drawLine(x1, y1, x2, y2, lineWidth, color)
    end,
    drawCircle = function(x, y, radius, color)
        alya.visual.drawCircle(x, y, radius, color)
    end,
}
