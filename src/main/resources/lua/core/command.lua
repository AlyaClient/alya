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
