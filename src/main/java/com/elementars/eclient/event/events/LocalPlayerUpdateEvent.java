package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.entity.EntityLivingBase;

public class LocalPlayerUpdateEvent extends Event {

    EntityLivingBase entityLivingBase;

    public LocalPlayerUpdateEvent(EntityLivingBase entity) {
        this.entityLivingBase = entity;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }
}
