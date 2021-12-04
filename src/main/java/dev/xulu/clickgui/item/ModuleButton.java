package dev.xulu.clickgui.item;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.render.ExeterGui;
import com.elementars.eclient.util.Helper;
import dev.xulu.clickgui.Panel;
import dev.xulu.clickgui.item.properties.*;
import dev.xulu.settings.Value;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button implements Helper
{
    private final Module module;
    private List<Item> items;
    private boolean subOpen;
    
    public ModuleButton(final Module module, final Panel parent) {
        super(module.getName(), parent);
        this.items = new ArrayList<Item>();
        this.module = module;
        if (Xulu.VALUE_MANAGER.getSettingsByMod(module) != null) {
            for (Value s : Xulu.VALUE_MANAGER.getSettingsByMod(module)) {
                if (s.isToggle()) {
                    items.add(new BooleanButton(s));
                } else if (s.isNumber()) {
                    items.add(new NumberSlider(s));
                } else if (s.isMode()) {
                    items.add(new ModeButton(s));
                } else if (s.isEnum()) {
                    items.add(new EnumButton(s));
                } else if (s.isBind() && !(s.getParentMod() instanceof Element)) {
                    items.add(new BindButton(s));
                } else if (s.isText()) {
                    items.add(new TextButton(s));
                }
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            if (Xulu.VALUE_MANAGER.getValuesByMod(module) != null)
                if (ExeterGui.getCF()) {
                    Xulu.cFontRenderer.drawStringWithShadow("...", this.x + this.width - (Xulu.cFontRenderer.getStringWidth("...")) - 3, this.y + 3.0f, -1);
                } else {
                    fontRenderer.drawStringWithShadow("...", this.x + this.width - (fontRenderer.getStringWidth("...")) - 2, this.y + 4.0f, -1);
                }
            if (this.subOpen) {
                float height = 1.0f;
                for (final Item item : this.items) {
                    if (!item.property.isVisible()) continue;
                    height += 15.0f;
                    item.setLocation(this.x + 1.0f, this.y + height);
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                if (ExeterGui.getSound())
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (this.subOpen) {
                for (final Item item : this.items) {
                    if (!item.property.isVisible()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        items.forEach(item -> {
            if (item.property.isVisible()) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        });
        super.mouseReleased(mouseX, mouseY, releaseButton);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) throws IOException {
        for (Item i : items) {
            if (!i.property.isVisible()) continue;
            try {
                if (i.keyTyped(typedChar, keyCode)) return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (final Item item : this.items) {
                if (!item.property.isVisible()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }
    
    @Override
    public void toggle() {
        if (module.getName().equalsIgnoreCase("HudEditor") || !module.getCategory().equals(Category.HUD)) {
            module.toggle();
        }
    }

    @Override
    public boolean getState () {
        return module.isToggled();
    }
}
