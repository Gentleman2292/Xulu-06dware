package com.elementars.eclient.module;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.guirewrite.elements.FeatureList;
import com.elementars.eclient.util.Helper;
import dev.xulu.settings.Bind;
import dev.xulu.settings.Value;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Module implements Helper {

    protected final Logger LOGGER = LogManager.getLogger("Xulu");

    private String name, desc, displayName;
    private Category category;
    private boolean toggled;
    private boolean drawn;

    public final Value<Bind> bind = register(new Value<>("Bind", this, new Bind(Keyboard.KEY_NONE)));
    public final Value<Animation> inAnimation = new Value<>("In Animation", this, Animation.NONE, Animation.values());

    public enum Animation {
        NONE,
        ENABLE,
        DISABLE
    }

    public KeyBinding keybind;
    public static Module instance;

    public Module(String name, String desc, int key, Category category, boolean drawn) {
        this.name = name;
        this.desc = desc;
        bind.getValue().setNum(key);
        this.category = category;
        toggled = false;
        this.drawn = drawn;
        setup();
        instance = this;
    }
    public void destroy() {}
    public void onEnable() {}
    public void onDisable() {}
    public void onEnableR() {
        Xulu.EVENT_MANAGER.register(this);
        onEnable();
    }
    public void onDisableR() {
        Xulu.EVENT_MANAGER.unregister(this);
        onDisable();
    }
    public void onToggle() {}
    public void toggle() {
        toggled = !toggled;
        onToggle();
        if(toggled) {
            if (FeatureList.animation.getValue() && (FeatureList.type.getValue().equalsIgnoreCase("Enable") || FeatureList.type.getValue().equalsIgnoreCase("Both"))) {
                inAnimation.setEnumValue("ENABLE");
            }
            onEnableR();
        }else{
            if (FeatureList.animation.getValue() && (FeatureList.type.getValue().equalsIgnoreCase("Disable") || FeatureList.type.getValue().equalsIgnoreCase("Both"))) {
                inAnimation.setEnumValue("DISABLE");
            }
            onDisableR();
        }
    }

    public void initToggle(boolean enabled) {
        toggled = enabled;
        onToggle();
        if(toggled) {
            onEnableR();
        }else{
            onDisableR();
        }
    }

    public void disable() {
        if (toggled) {
            toggle();
        }
    }
    public void setDrawn(boolean in) {
        this.drawn = in;
    }
    public boolean isDrawn() {
        return this.drawn;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() { return desc; }
    public int getKey() {
        return bind.getValue().getNum();
    }
    public void setKey(int key) {
        this.bind.getValue().setNum(key);
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public boolean isToggled() {
        return toggled;
    }
    public boolean isToggledAnim() { return toggled || inAnimation.getValue() == Animation.DISABLE; }
    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setup() {}

    public void onUpdate() {

    }
    public void onRender() {}
    public void onWorldRender(RenderEvent event) {}
    public String getHudInfo() { return null; }

    public <T> Value<T> register(Value<T> s) {
        Xulu.VALUE_MANAGER.register(s);
        return s;
    }

    public void onKey(int k){
        if (bind.getValue().getNum() == k){
            toggle();
        }
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return Xulu.MODULE_MANAGER.getModule(clazz);
    }

    public static <T extends Module> T getModuleT(Class<T> clazz) {
        return Xulu.MODULE_MANAGER.getModuleT(clazz);
    }

    protected void sendDebugMessage(String text) {
        Command.sendChatMessage("&b[" + this.name + "]:&r " + text);
    }

    protected void sendRawDebugMessage(String text) {
        Command.sendRawChatMessage("&b[" + this.name + "]:&r " + text);
    }
}
