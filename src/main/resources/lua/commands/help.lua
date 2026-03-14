alya.commands.register("help", "Displays a list of available commands", function(args)
    alya.chat.raw("\167e=== Available Commands ===")
    local commands = alya.commands.getAll()
    for index = 1, #commands do
        local command = commands[index]
        local aliasText = ""
        if #command.aliases > 0 then
            aliasText = "\167f (" .. table.concat(command.aliases, ", ") .. ")"
        end
        alya.chat.raw("\167b" .. alya.commands.getPrefix() .. command.name .. aliasText .. "\167f - " .. command.description)
    end
end, "h", "?")
