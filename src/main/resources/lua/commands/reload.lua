alya.commands.register("reload", "Reloads all Lua scripts", function(args)
    alya.chat.raw("\167eReloading Lua scripts...")
    alya.reload()
end)
