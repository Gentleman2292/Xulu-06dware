package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

/**
 * @author Elementars
 * @since 8/25/2020 - 4:35 PM
 */
public class EventModelPlayerRender extends Event {

    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scaleFactor;

    public EventModelPlayerRender(ModelBase modelBaseIn, Entity entityIn, float limbSwingIn, float limbSwingAmountIn, float ageInTicksIn, float netHeadYawIn, float headPitchIn, float scaleFactorIn) {
        modelBase = modelBaseIn;
        entity = entityIn;
        limbSwing = limbSwingIn;
        limbSwingAmount = limbSwingAmountIn;
        ageInTicks = ageInTicksIn;
        netHeadYaw = netHeadYawIn;
        headPitch = headPitchIn;
        scaleFactor = scaleFactorIn;
    }
}
