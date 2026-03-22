local Module = {}
Module.__index = Module

function Module.new(name, description, category, keyCode)
    local moduleTable = alya.modules.register(name, description, category or "OTHER")
    if keyCode then
        moduleTable.setKeyCode(keyCode)
    end
    local instance = setmetatable({}, Module)
    instance._moduleTable = moduleTable
    instance._name = name
    instance._description = description
    instance._category = category or "OTHER"
    return instance
end

function Module:getName()
    return self._moduleTable.getName()
end

function Module:getDescription()
    return self._moduleTable.getDescription()
end

function Module:getCategory()
    return self._moduleTable.getCategory()
end

function Module:isEnabled()
    return self._moduleTable.isEnabled()
end

function Module:toggle()
    self._moduleTable.toggle()
end

function Module:enable()
    self._moduleTable.enable()
end

function Module:disable()
    self._moduleTable.disable()
end

function Module:getKeyCode()
    return self._moduleTable.getKeyCode()
end

function Module:setKeyCode(keyCode)
    self._moduleTable.setKeyCode(keyCode)
end

function Module:onEnable(callback)
    self._moduleTable.onEnable(callback)
end

function Module:onDisable(callback)
    self._moduleTable.onDisable(callback)
end

function Module:addBooleanSetting(name, description, default)
    return self._moduleTable.addBooleanSetting(name, description, default)
end

function Module:addNumberSetting(name, description, default, min, max, increment)
    return self._moduleTable.addNumberSetting(name, description, default, min, max, increment or 0.1)
end

function Module:addModeSetting(name, description, default, ...)
    return self._moduleTable.addModeSetting(name, description, default, ...)
end

function Module:getSetting(name)
    return self._moduleTable.getSetting(name)
end

function Module:onEvent(eventName, callback)
    if not self._moduleTable.isEnabled() then return end
    alya.events.on(eventName, function(event)
        if self._moduleTable.isEnabled() then
            callback(event)
        end
    end)
end

return Module
