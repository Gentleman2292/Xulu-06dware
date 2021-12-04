package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class EventModelRender extends Event {

    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scaleFactor;

    public EventModelRender(ModelBase modelBaseIn, Entity entityIn, float limbSwingIn, float limbSwingAmountIn, float ageInTicksIn, float netHeadYawIn, float headPitchIn, float scaleFactorIn) {
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
