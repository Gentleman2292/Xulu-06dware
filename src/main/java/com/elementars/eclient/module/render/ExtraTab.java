package com.elementars.eclient.module.render;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemies;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import dev.xulu.settings.Value;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 8/04/2018.
 */
public class ExtraTab extends Module {

    public final Value<Integer> tabSize = register(new Value<>("Players", this, 80, 1, 1000));
    public final Value<String> color = register(new Value<>("Friend Color", this, "LightGreen", ColorTextUtils.colors));
    public final Value<String> ecolor = register(new Value<>("Enemy Color", this, "LightRed", ColorTextUtils.colors));

    public static ExtraTab INSTANCE;

    public ExtraTab() {
        super("ExtraTab", "Expands tab menu", Keyboard.KEY_NONE, Category.RENDER, true);
        ExtraTab.INSTANCE = this;
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (Friends.isFriend(dname)) return String.format("%s" + ColorTextUtils.getColor(ExtraTab.INSTANCE.color.getValue()).substring(1) + "%s", Command.SECTIONSIGN(), dname);
        if (Enemies.isEnemy(dname)) return String.format("%s" + ColorTextUtils.getColor(ExtraTab.INSTANCE.ecolor.getValue()).substring(1) + "%s", Command.SECTIONSIGN(), dname);
        return dname;
    }
}
