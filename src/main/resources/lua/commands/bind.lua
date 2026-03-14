alya.commands.register("bind", "Bind a key to a module", function(args)
    if #args < 1 then
        alya.chat.info("Usage: .bind add <module> <key>")
        alya.chat.info("Usage: .bind remove <module>")
        alya.chat.info("Usage: .bind list")
        return
    end

    local action = string.lower(args[1])

    if action == "add" or action == "set" then
        if #args < 3 then
            alya.chat.error("Usage: .bind add <module> <key>")
            return
        end
        local targetModule = alya.modules.get(args[2])
        if not targetModule then
            alya.chat.error("Module not found: " .. args[2])
            return
        end
        alya.chat.success("Key binding updated for " .. targetModule.getName())

    elseif action == "remove" or action == "clear" then
        if #args < 2 then
            alya.chat.error("Usage: .bind remove <module>")
            return
        end
        local targetModule = alya.modules.get(args[2])
        if not targetModule then
            alya.chat.error("Module not found: " .. args[2])
            return
        end
        targetModule.setKeyCode(0)
        alya.chat.success("Removed keybind from " .. targetModule.getName())

    elseif action == "list" then
        alya.chat.info("Module Keybinds:")
        local allModules = alya.modules.getAll()
        for index = 1, #allModules do
            local currentModule = allModules[index]
            if currentModule.getKeyCode() ~= 0 then
                alya.chat.raw("  " .. currentModule.getName() .. " -> key:" .. currentModule.getKeyCode())
            end
        end

    else
        alya.chat.error("Unknown action: " .. action)
        alya.chat.info("Available actions: add, remove, list")
    end
end, "b", "keybind")
