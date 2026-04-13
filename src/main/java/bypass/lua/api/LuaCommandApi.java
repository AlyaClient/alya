/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.lua.api;

import bypass.Alya;
import bypass.command.Command;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.ArrayList;
import java.util.List;

public final class LuaCommandApi extends LuaTable {
    public LuaCommandApi() {
        set(
                "register",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs arguments) {
                        String commandName = arguments.arg(1).tojstring();
                        String commandDescription = arguments.arg(2).tojstring();
                        LuaFunction executeFunction =
                                arguments.arg(3) instanceof LuaFunction ? (LuaFunction) arguments.arg(3) : null;
                        List<String> aliasList = new ArrayList<>();
                        for(int argumentIndex = 4; argumentIndex <= arguments.narg(); argumentIndex++) {
                            aliasList.add(arguments.arg(argumentIndex).tojstring());
                        }
                        Command command =
                                new Command(commandName, commandDescription, aliasList.toArray(new String[0])) {
                                    @Override
                                    public void execute(String[] commandArgs) {
                                        if(executeFunction == null) return;
                                        LuaTable luaArgTable = new LuaTable();
                                        for(int argIndex = 0; argIndex < commandArgs.length; argIndex++) {
                                            luaArgTable.set(argIndex + 1, LuaValue.valueOf(commandArgs[argIndex]));
                                        }
                                        try {
                                            executeFunction.call(luaArgTable);
                                        } catch(LuaError luaError) {
                                            Alya.getInstance()
                                                    .getLogger()
                                                    .error("Lua command error in {}: {}", commandName, luaError.getMessage());
                                        }
                                    }
                                };
                        Alya.getInstance().getCommandManager().register(command);
                        return LuaValue.NIL;
                    }
                });
        set(
                "getPrefix",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(Alya.getInstance().getCommandManager().prefix());
                    }
                });
        set(
                "getAll",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        LuaTable commandListTable = new LuaTable();
                        List<Command> commandList = Alya.getInstance().getCommandManager().getCommands();
                        for(int commandIndex = 0; commandIndex < commandList.size(); commandIndex++) {
                            Command command = commandList.get(commandIndex);
                            LuaTable commandTable = new LuaTable();
                            commandTable.set("name", LuaValue.valueOf(command.getName()));
                            commandTable.set("description", LuaValue.valueOf(command.getDescription()));
                            LuaTable aliasTable = new LuaTable();
                            for(int aliasIndex = 0; aliasIndex < command.getAliases().length; aliasIndex++) {
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
