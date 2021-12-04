package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;

public class EventThrow extends Event {
    private Entity thrower;
    private EntityThrowable entity;
    private float rotation;

    public EventThrow(Entity entityLivingBase, EntityThrowable entityThrowable, float rotation) {
        this.thrower = entityLivingBase;
        this.entity = entityThrowable;
        this.rotation = rotation;
    }

    public Entity getThrower() {
        return thrower;
    }

    public EntityThrowable getEntity() {
        return entity;
    }

    public float getRotation() {
        return rotation;
    }
}
