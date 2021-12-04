package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;

/**
 * @author Elementars
 * @since 8/2/2020 - 9:22 AM
 */
public class EventRenderEntity extends Event {

    RenderLivingBase renderer;
    EntityLivingBase entity;
    ModelBase model;
    double x;
    double y;
    double z;
    float entityYaw;
    float partialTicks;

    public EventRenderEntity(@Nullable RenderLivingBase renderer, EntityLivingBase entity, @Nullable ModelBase model, double x, double y, double z, float entityYaw, float partialTicks) {
        this.renderer = renderer;
        this.entity = entity;
        this.model = model;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
    }

    public RenderLivingBase getRenderer() {
        return renderer;
    }

    public double getZ() {
        return z;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ModelBase getModel() {
        return model;
    }

    public float getEntityYaw() {
        return entityYaw;
    }

    @Override
    public float getPartialTicks() {
        return partialTicks;
    }
}
