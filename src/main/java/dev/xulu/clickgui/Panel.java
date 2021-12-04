package dev.xulu.clickgui;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.module.render.ExeterGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Helper;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.clickgui.item.Button;
import dev.xulu.clickgui.item.Item;
import dev.xulu.newgui.util.ColorUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

public abstract class Panel implements Labeled, Helper {
    private final String label;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    public boolean drag;
    private final ArrayList<Item> items;
    
    public Panel(final String label, final int x, final int y, final boolean open) {
        this.items = new ArrayList<Item>();
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }
    
    public abstract void setupItems();

    public int rgb;

    public int updateRainbow(int IN) {
        float hue2 = Color.RGBtoHSB(new Color(IN).getRed(), new Color(IN).getGreen(), new Color(IN).getBlue(), null)[0];
        hue2 += ExeterGui.getSpeed() / 1000f;
        if (hue2 > 1) hue2 -= 1;
        return Color.HSBtoRGB(hue2, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        rgb = Xulu.rgb;
        this.drag(mouseX, mouseY);
        final float totalItemHeight = this.open ? (this.getTotalItemHeight() - 2.0f) : 0.0f;
        XuluTessellator.drawRectGradient((float)this.x, this.y - 1.5f, (float)(this.x + this.width), (float)(this.y + this.height - 6), /*-7829368*/ ColorUtils.changeAlpha(ExeterGui.getRainbow() ? rgb : ColorUtil.getClickGUIColor().getRGB(), 225), -6710887);
        if (this.open) {
            RenderMethods.drawRect((float)this.x, this.y + 12.5f, (float)(this.x + this.width), this.open ? (this.y + this.height + totalItemHeight) : ((float)(this.y + this.height - 1)), 1996488704);
        }
        if (ExeterGui.getCF()) {
            Xulu.cFontRenderer.drawStringWithShadow(this.getLabel(), this.x + 3.0f, this.y + 1.0f, -1);
        } else {
            fontRenderer.drawStringWithShadow(this.getLabel(), this.x + 3.0f, this.y + 2.0f, -1);
        }
        if (this.open) {
            float y = this.getY() + this.getHeight() - 3.0f;
            for (final Item item : this.getItems()) {
                rgb = updateRainbow(rgb);
                item.setLocation(this.x + 2.0f, y);
                item.setWidth(this.getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5f;
            }
        }
    }
    
    private void drag(final int mouseX, final int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            ClickGui.getClickGui().getPanels().forEach(panel -> {
                if (panel.drag) {
                    panel.drag = false;
                }
                return;
            });
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            if (ExeterGui.getSound())
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    public void addButton(final Button button) {
        this.items.add(button);
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }
    
    @Override
    public final String getLabel() {
        return this.label;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public boolean getOpen() {
        return this.open;
    }
    
    public final ArrayList<Item> getItems() {
        return this.items;
    }
    
    private boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }
    
    private float getTotalItemHeight() {
        float height = 0.0f;
        for (final Item item : this.getItems()) {
            height += item.getHeight() + 1.5f;
        }
        return height;
    }
    
    public void setX(final int dragX) {
        this.x = dragX;
    }
    
    public void setY(final int dragY) {
        this.y = dragY;
    }
}
