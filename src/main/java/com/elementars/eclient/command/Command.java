package com.elementars.eclient.command;

import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.Helper;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command implements Helper {
    protected String name;
    protected String description;
    protected String[] syntax;

    public Command(String name, String description, String[] syntax) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        prefix = ".";
    }

    private static String prefix;

    private static String[] getBrackets(String type) {
        if (type.equalsIgnoreCase("[]")) {
            return new String[]{"[", "]"};
        } else if (type.equalsIgnoreCase("<>")) {
            return new String[]{"<", ">"};
        } else if (type.equalsIgnoreCase("()")) {
            return new String[]{"(", ")"};
        } else if (type.equalsIgnoreCase("{}")) {
            return new String[]{"{", "}"};
        } else if (type.equalsIgnoreCase("-==-")) {
            return new String[]{"-=", "=-"};
        } else {
            return new String[]{"[", "]"};
        }
    }

    public static void sendChatMessage(String message){
        sendRawChatMessage(ColorTextUtils.getColor(Global.command2.getValue()) + getBrackets(Global.command3.getValue())[0] + ColorTextUtils.getColor(Global.command1.getValue()) + "Xulu" + ColorTextUtils.getColor(Global.command2.getValue()) + getBrackets(Global.command3.getValue())[1] + " &r" + message);
    }

    public static void sendStringChatMessage(String[] messages) {
        sendChatMessage("");
        for (String s : messages) sendRawChatMessage(s);
    }

    public static void sendRawChatMessage(String message){
        try {
            Wrapper.getPlayer().sendMessage(new Command.ChatMessage(message));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(String[] args) {}

    public void syntaxCheck(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        execute(args);
    }

    public static void setPrefix(String in) {
        prefix = in;
    }

    public static String getPrefix() {
        return prefix;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getSyntax() {
        return syntax;
    }

    public void showSyntax(String command) {
        Command.sendChatMessage("Options for " + command);
        if (syntax.length == 0) {
            Command.sendChatMessage("No options for this command.");
            return;
        }
        for (String arg : syntax) {
            Command.sendChatMessage(" - " + arg);
        }
    }

    public static class ChatMessage extends TextComponentBase {

        String text;

        public ChatMessage(String text) {

            Pattern p = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher m = p.matcher(text);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                String replacement = "\u00A7" + m.group().substring(1);
                m.appendReplacement(sb, replacement);
            }

            m.appendTail(sb);

            this.text = sb.toString();
        }

        public String getUnformattedComponentText() {
            return text;
        }

        @Override
        public ITextComponent createCopy() {
            return new Command.ChatMessage(text);
        }

    }

    public static char SECTIONSIGN() {
        return '\u00A7';
    }
}
