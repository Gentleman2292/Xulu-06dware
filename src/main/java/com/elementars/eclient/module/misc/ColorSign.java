package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.GuiScreenEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by 086 on 8/04/2018.
 */
public class ColorSign extends Module {

    public ColorSign() {
        super("ColorSign", "Allows writing with colors on signs", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @EventTarget
    private void onGui(GuiScreenEvent.Displayed event) {
        if (event.getScreen() instanceof GuiEditSign && isToggled()) {
            event.setScreen(new KamiGuiEditSign(((GuiEditSign) event.getScreen()).tileSign));
        }
    }

//    @EventHandler
//    public Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
//        if (event.getPacket() instanceof CPacketUpdateSign) {
//            String[] lines = ((CPacketUpdateSign) event.getPacket()).lines;
//            for (int i = 0; i < 4; i++) {
//                lines[i] = lines[i].replace(Command.SECTIONSIGN() + "", Command.SECTIONSIGN() + Command.SECTIONSIGN() + "rr");
//            }
//        }
//    });

    private class KamiGuiEditSign extends GuiEditSign {


        public KamiGuiEditSign(TileEntitySign teSign) {
            super(teSign);
        }

        @Override
        public void initGui() {
            super.initGui();
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                this.tileSign.signText[this.editLine] = new TextComponentString(tileSign.signText[this.editLine].getFormattedText().replaceAll("(" + Command.SECTIONSIGN() + ")(.)", "$1$1$2$2"));
            }
            super.actionPerformed(button);
        }
        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            String s = ((TextComponentString) tileSign.signText[this.editLine]).getText();
            s = s.replace("&", Command.SECTIONSIGN() + "");
            tileSign.signText[this.editLine] = new TextComponentString(s);
        }

    }
}
