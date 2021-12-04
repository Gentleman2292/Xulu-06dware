package com.elementars.eclient.command;

import com.elementars.eclient.command.commands.*;
import com.elementars.eclient.util.Helper;
import com.elementars.eclient.util.Wrapper;

import java.util.ArrayList;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<>();
    public static ArrayList<String> rcommands = new ArrayList<>();

    public void init() {
        commands.add(new AboutCommand());
        commands.add(new CreditsCommand());
        commands.add(new HelpCommand());
        commands.add(new MacroCommand());
        commands.add(new BindCommand());
        commands.add(new ToggleCommand());
        commands.add(new SetCommand());
        commands.add(new SetStringCommand());
        commands.add(new DrawnCommand());
        commands.add(new XrayCommand());
        commands.add(new SearchCommand());
        commands.add(new SaveCommand());
        commands.add(new ReloadCommand());
        commands.add(new PrefixCommand());
        commands.add(new FriendCommand());
        commands.add(new EnemyCommand());
        commands.add(new CustomFontCommand());
        commands.add(new YawCommand());
        commands.add(new LogspotCommand());
        commands.add(new WaypointCommand());
        commands.add(new AntiVoidCommand());
        commands.add(new NicknameCommand());
    }

    public static void runCommand(String message) {
        String[] args = message.split(" ");
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) continue;
            args[i] = args[i].replaceAll("<>", " ");
        }
        try {
            Wrapper.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(Command.getPrefix() + message);
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(args[0])) {
                    command.syntaxCheck(args);
                    return;
                }
            }
        } catch (Exception e) {
            Command.sendChatMessage("Error occured when running command!");
        }
        Command.sendChatMessage("Command not found. Try .help for a list of commands");
    }
    public static ArrayList<Command> getCommands() {
        return commands;
    }
}
