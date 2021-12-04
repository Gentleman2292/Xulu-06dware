package com.elementars.eclient.guirewrite;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

public class Element extends Module {

    Frame frame;
    protected double x;
    protected double y;

    protected double width;
    protected double height;

    public Element(String name) {
        super(name, "NONE", Keyboard.KEY_NONE, Category.HUD, false);
    }

    public void registerFrame() {
        this.frame = HUD.getframeByName(getName());
        this.width = frame.width;
        this.height = frame.height;
    }

    @Override
    public void onUpdate() {
        if (this.frame != null) {
            if (frame.width != width) frame.width = width;
            if (frame.height != height) frame.height = height;
        }
        super.onUpdate();
    }

    public void onMiddleClick() {}

    public Frame getFrame() {
        return frame;
    }

    @Override
    public void onEnable() {
        if (this.frame != null && !this.frame.pinned) {
            this.frame.pinned = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.frame != null && this.frame.pinned) {
            this.frame.pinned = false;
        }
        super.onDisable();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
