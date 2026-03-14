package dev.thoq.lua.api;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.ArrayList;
import java.util.List;

public final class LuaCommandApi extends LuaTable {

    public LuaCommandApi() {
        set("register", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs arguments) {
                String commandName = arguments.arg(1).tojstring();
                String commandDescription = arguments.arg(2).tojstring();
                LuaFunction executeFunction = arguments.arg(3) instanceof LuaFunction ? (LuaFunction) arguments.arg(3)
                        : null;
                List<String> aliasList = new ArrayList<>();
                for (int argumentIndex = 4; argumentIndex <= arguments.narg(); argumentIndex++) {
                    aliasList.add(arguments.arg(argumentIndex).tojstring());
                }

                Command command = new Command(commandName, commandDescription, aliasList.toArray(new String[0])) {
                    @Override
                    public void execute(String[] commandArgs) {
                        if (executeFunction == null)
                            return;
                        LuaTable luaArgTable = new LuaTable();
                        for (int argIndex = 0; argIndex < commandArgs.length; argIndex++) {
                            luaArgTable.set(argIndex + 1, LuaValue.valueOf(commandArgs[argIndex]));
                        }
                        try {
                            executeFunction.call(luaArgTable);
                        } catch (LuaError luaError) {
                            Alya.getInstance().getLogger().error("Lua command error in {}: {}", commandName,
                                    luaError.getMessage());
                        }
                    }
                };

                Alya.getInstance().getCommandManager().register(command);
                return LuaValue.NIL;
            }
        });

        set("getPrefix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Alya.getInstance().getCommandManager().prefix());
            }
        });

        set("getAll", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable commandListTable = new LuaTable();
                List<Command> commandList = Alya.getInstance().getCommandManager().getCommands();
                for (int commandIndex = 0; commandIndex < commandList.size(); commandIndex++) {
                    Command command = commandList.get(commandIndex);
                    LuaTable commandTable = new LuaTable();
                    commandTable.set("name", LuaValue.valueOf(command.getName()));
                    commandTable.set("description", LuaValue.valueOf(command.getDescription()));
                    LuaTable aliasTable = new LuaTable();
                    for (int aliasIndex = 0; aliasIndex < command.getAliases().length; aliasIndex++) {
                        aliasTable.set(aliasIndex + 1, LuaValue.valueOf(command.getAliases()[aliasIndex]));
                    }
                    commandTable.set("aliases", aliasTable);
                    commandListTable.set(commandIndex + 1, commandTable);
                }
                return commandListTable;
            }
        });
    }
}
