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
