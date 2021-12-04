package com.elementars.eclient.module.render;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import dev.xulu.settings.Value;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Chat extends Module {
    public final Value<Boolean> customFont = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> ncb = register(new Value<>("No Chat Background", this, false));
    public static Value<Boolean> nochatshadow;
    private final Value<Boolean> namehighlight = register(new Value<>("Name Highlight", this, false));
    private final Value<String> namemode = register(new Value<>("Highlight Mode", this, "Highlight", new ArrayList<>(
            Arrays.asList("Highlight", "Hide")
    )));
    private final Value<String> playername = register(new Value<>("Player Tag", this, "<Player>", new String[]{
            "<Player>", "[Player]:", "Player:", "Player ->"
    }));
    private final Value<String> playerColor = register(new Value<>("Player Color", this, "White", ColorTextUtils.colors));
    private final Value<Boolean> timestamps = register(new Value<>("Time Stamps", this, false));
    private final Value<Boolean> mode = register(new Value<>("24 Hour Time", this, false));
    private final Value<String> bracketmode = register(new Value<>("Bracket Type", this, "<>", new ArrayList<>(
            Arrays.asList("()", "<>", "[]", "{}")
    )));
    private final Value<String> color = register(new Value<>("Color", this, "LightGray", ColorTextUtils.colors));

    public static Chat INSTANCE;

    public Chat() {
        super("Chat", "Tampers with chat", Keyboard.KEY_NONE, Category.RENDER, true);
        nochatshadow = register(new Value<>("No Chat Shadow", this, false));
        INSTANCE = this;
    }

    public static TextComponentString componentStringOld;

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO) {
                if (tryProcessChat(packet.getChatComponent().getFormattedText(), packet.getChatComponent().getUnformattedText())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean tryProcessChat(String message, String unformatted) {
        String out = message;
        String[] parts = out.split(" ");
        String[] partsUnformatted = unformatted.split(" ");
        parts[0] = partsUnformatted[0];
        if (parts[0].startsWith("<") && parts[0].endsWith(">")) {
            parts[0] = parts[0].replaceAll("<", "");
            parts[0] = parts[0].replaceAll(">", "");
            parts[0] = Command.SECTIONSIGN() + ColorTextUtils.getColor(playerColor.getValue()).substring(1) + parts[0] + Command.SECTIONSIGN() + "r";
            if (playername.getValue().equalsIgnoreCase("<Player>")) {
                String temp;
                temp = "<" + parts[0] + ">";
                for (int i = 1; i < parts.length; i++) {
                    temp += " " + parts[i];
                }
                message = temp;
            } else if (playername.getValue().equalsIgnoreCase("[Player]:")) {
                String temp;
                temp = "[" + parts[0] + "]:";
                for (int i = 1; i < parts.length; i++) {
                    temp += " " + parts[i];
                }
                message = temp;
            } else if (playername.getValue().equalsIgnoreCase("Player:")) {
                String temp;
                temp = parts[0] + ":";
                for (int i = 1; i < parts.length; i++) {
                    temp += " " + parts[i];
                }
                message = temp;
            } else if (playername.getValue().equalsIgnoreCase("Player ->")) {
                String temp;
                temp = parts[0] + " ->";
                for (int i = 1; i < parts.length; i++) {
                    temp += " " + parts[i];
                }
                message = temp;
            } else {
                String temp;
                temp = "<" + parts[0] + ">";
                for (int i = 1; i < parts.length; i++) {
                    temp += " " + parts[i];
                }
                message = temp;
            }
        }
        out = message;
        if (this.timestamps.getValue()) {
            String date = "";
            if (this.mode.getValue()) {
                date = new SimpleDateFormat("k:mm").format(new Date());
            }else{
                date = new SimpleDateFormat("h:mm a").format(new Date());
            }
            if (this.bracketmode.getValue().equalsIgnoreCase("<>")) {
                out = "\247" + ColorTextUtils.getColor(color.getValue()).substring(1) + "<" + date + ">\247r " + message;
            }
            else if (this.bracketmode.getValue().equalsIgnoreCase("()")) {
                out = "\247" + ColorTextUtils.getColor(color.getValue()).substring(1) + "(" + date + ")\247r " + message;
            }
            else if (this.bracketmode.getValue().equalsIgnoreCase("[]")) {
                out = "\247" + ColorTextUtils.getColor(color.getValue()).substring(1) + "[" + date + "]\247r " + message;
            }
            else if (this.bracketmode.getValue().equalsIgnoreCase("{}")) {
                out = "\247" + ColorTextUtils.getColor(color.getValue()).substring(1) + "{" + date + "}\247r " + message;
            }

        }
        if (this.namehighlight.getValue()) {
            if (mc.player == null) return false;
            if (this.namemode.getValue().equalsIgnoreCase("Hide")) {
                out = out.replace(mc.player.getName(), "HIDDEN");
            } else {
                out = out.replace(mc.player.getName(), "\247b" + mc.player.getName() + "\247r");
            }
        }
        Command.sendRawChatMessage(out);
        return true;
    }
}
