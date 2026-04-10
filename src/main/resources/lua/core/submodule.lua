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

local Submodule = {}
Submodule.__index = Submodule
function Submodule.new(name, parentModule)
    local instance = setmetatable({}, Submodule)
    instance._name = name
    instance._parent = parentModule
    instance._enabled = false
    instance._onEnableCallback = nil
    instance._onDisableCallback = nil
    instance._eventHandlers = {}
    return instance
end
function Submodule:getName()
    return self._name
end
function Submodule:getParent()
    return self._parent
end
function Submodule:isEnabled()
    return self._enabled
end
function Submodule:setEnabled(enabled)
    if self._enabled == enabled then return end
    self._enabled = enabled
    if enabled and self._onEnableCallback then
        self._onEnableCallback()
    elseif not enabled and self._onDisableCallback then
        self._onDisableCallback()
    end
end
function Submodule:onEnable(callback)
    self._onEnableCallback = callback
end
function Submodule:onDisable(callback)
    self._onDisableCallback = callback
end
function Submodule:onEvent(eventName, callback)
    alya.events.on(eventName, function(event)
        if self._enabled then
            callback(event)
        end
    end)
end
return Submodule
