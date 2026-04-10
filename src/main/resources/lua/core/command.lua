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

local Command = {}
Command.__index = Command
function Command.new(name, description, executeCallback, ...)
    local aliases = {...}
    local instance = setmetatable({}, Command)
    instance._name = name
    instance._description = description
    instance._aliases = aliases
    instance._executeCallback = executeCallback
    alya.commands.register(name, description, executeCallback, table.unpack(aliases))
    return instance
end
function Command:getName()
    return self._name
end
function Command:getDescription()
    return self._description
end
function Command:getAliases()
    return self._aliases
end
return Command
