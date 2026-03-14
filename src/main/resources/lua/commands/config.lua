alya.commands.register("config", "Save or load configurations", function(args)
    if #args < 1 then
        alya.chat.info("Usage: .config save <name>")
        alya.chat.info("Usage: .config load <name>")
        alya.chat.info("Usage: .config list")
        return
    end

    local action = string.lower(args[1])

    if action == "save" then
        if #args < 2 then
            alya.chat.error("Usage: .config save <name>")
            return
        end
        alya.config.save(args[2])
        alya.chat.success("Saved config: " .. args[2])

    elseif action == "load" then
        if #args < 2 then
            alya.chat.error("Usage: .config load <name>")
            return
        end
        if not alya.config.exists(args[2]) then
            alya.chat.error("Config not found: " .. args[2])
            return
        end
        alya.config.load(args[2])
        alya.chat.success("Loaded config: " .. args[2])

    elseif action == "list" then
        local configNames = alya.config.getNames()
        if #configNames == 0 then
            alya.chat.info("No configs found.")
            return
        end
        alya.chat.info("Available configs:")
        for index = 1, #configNames do
            alya.chat.raw("  - " .. configNames[index])
        end

    else
        alya.chat.error("Unknown action: " .. action)
        alya.chat.info("Available actions: save, load, list")
    end
end, "cfg", "c")
