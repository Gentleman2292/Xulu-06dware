package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public class EventItemRender extends Event {

    public Entity entity;
    public ICamera camera;
    public float n;

    public EventItemRender(Entity entity, ICamera camera, float n) {
        this.entity = entity;
        this.camera = camera;
        this.n = n;
    }
}
