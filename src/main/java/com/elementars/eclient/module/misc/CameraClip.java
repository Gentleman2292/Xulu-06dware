package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 11/12/2017.
 * @see com.elementars.eclient.mixin.mixins.MixinEntityRenderer#rayTraceBlocks(WorldClient, Vec3d, Vec3d)
 */
public class CameraClip extends Module {
    public CameraClip() {
        super("CameraClip", "Allows camera to clip in blocks", Keyboard.KEY_NONE, Category.MISC, true);
    }
}
