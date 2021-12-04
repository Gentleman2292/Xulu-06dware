package com.elementars.eclient.event.events;

import com.elementars.eclient.event.EclientEvent;
import com.elementars.eclient.event.Event;
import com.elementars.eclient.util.Wrapper;

public class EventPreMotionUpdates extends Event {
    private boolean cancel;
    public float yaw;
    public float pitch;
    public double y;

    public EventPreMotionUpdates(float yaw, float pitch, double y)
    {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
    }

    public void setMotion(double motionX, double motionY, double motionZ)
    {
        Wrapper.getMinecraft().player.motionX = motionX;
        Wrapper.getMinecraft().player.motionY = motionY;
        Wrapper.getMinecraft().player.motionZ = motionZ;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public double getY()
    {
        return this.y;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setCancelled(boolean state)
    {
        this.cancel = state;
    }
}
