package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EventDrinkPotion extends Event {
    EntityLivingBase entityLivingBase;
    ItemStack stack;

    public EventDrinkPotion(EntityLivingBase entityLivingBase, ItemStack itemStack) {
        this.entityLivingBase = entityLivingBase;
        this.stack = itemStack;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }

    public ItemStack getStack() {
        return stack;
    }
}
