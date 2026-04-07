local moduleTable = alya.modules.register("Hitboxes", "Expands entity hitboxes", "COMBAT")
local expansion = moduleTable.addNumberSetting("Expansion", "", 0.1, 0.1, 1, 0.01)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    alya.mc.setHitboxExpansion(expansion.getValue())
end)

moduleTable.onDisable(function()
    alya.mc.resetHitboxExpansion()
end)
