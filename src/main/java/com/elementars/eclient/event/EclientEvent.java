package com.elementars.eclient.event;

import com.elementars.eclient.util.Wrapper;

/**
 * Created by 086 on 16/11/2017.
 */
public class EclientEvent{

    private Era era = Era.PRE;
    private final float partialTicks;

    public EclientEvent() {
        partialTicks = Wrapper.getMinecraft().getRenderPartialTicks();
    }

    public Era getEra() {
        return era;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public enum Era {
        PRE, PERI, POST
    }

}