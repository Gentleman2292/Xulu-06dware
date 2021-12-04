package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorUtils;
import dev.xulu.settings.Value;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public class Compass extends Module {

    public final Value<Boolean> axis = register(new Value<>("Axis", this, false));
    public final Value<Integer> scale = register(new Value<>("Scale", this, 3, 1, 10));
    public final Value<Float> position = register(new Value<>("Y Position", this, 8f, 0f, 10f));
    public final Value<Integer> xposition = register(new Value<>("X Position", this, 0, -500, 500));

    private static final double HALF_PI = Math.PI / 2;

    private enum Direction {
        N("-Z"),
        W("-X"),
        S("+Z"),
        E("+X");

        private String alternate;

        Direction(String alternate) {
            this.alternate = alternate;
        }

        public String getAlternate() {
            return alternate;
        }
    }

    public Compass() {
        super("Compass", "Credit to fr1kin", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onRender() {
        GlStateManager.pushMatrix();
        final double centerX = mc.displayWidth / 4 + xposition.getValue();
        //final double centerY = mc.displayHeight * 0.8;
        final double centerY = (mc.displayHeight /2 ) * (position.getValue()/10);

        for (Direction dir : Direction.values()) {
            double rad = getPosOnCompass(dir);
            mc.fontRenderer.drawStringWithShadow((axis.getValue() ? dir.getAlternate() : dir.name()), (float) (centerX + getX(rad)), (float) (centerY + getY(rad)), dir == Direction.N ? ColorUtils.Colors.RED : ColorUtils.Colors.WHITE);
        }
        GlStateManager.popMatrix();
    }

    private double getX(double rad) {
        return Math.sin(rad) * (scale.getValue() * 10);
    }

    private double getY(double rad) {
        final double epicPitch = MathHelper
                .clamp(mc.player.rotationPitch + 30f, -90f, 90f);
        final double pitchRadians = Math.toRadians(epicPitch); // player pitch
        return Math.cos(rad) * Math.sin(pitchRadians) * (scale.getValue() * 10);
    }

    // return the position on the circle in radians
    private static double getPosOnCompass(Direction dir) {
        double yaw =
                Math.toRadians(
                        MathHelper.wrapDegrees(mc.player.rotationYaw)); // player yaw
        int index = dir.ordinal();
        return yaw + (index * HALF_PI);
    }
}
