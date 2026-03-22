local moduleTable = alya.modules.register("FullBright", "Brightens up the world client side", "VISUAL")

alya.events.on("update", function(event)
    if moduleTable.isEnabled() then
        alya.mc.setGamma(100)
    end
end)
