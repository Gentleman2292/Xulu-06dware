package com.elementars.eclient.module.player;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

/**
 * Author Seth
 * 5/2/2019 @ 4:39 AM.
 */
public final class ItemSpoof extends Module {

    private boolean send;
    private Entity entity;

    public BlockPos position;
    public EnumFacing placedBlockDirection;
    public EnumHand hand;
    public float facingX;
    public float facingY;
    public float facingZ;

    public ItemSpoof() {
        super("ItemSpoof", "Allows you to display a different item server-side(Use the top left slot in your inventory)", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    @EventTarget
    public void sendPacket(EventSendPacket event) {
        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            if (send) {
                send = false;
                return;
            }
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            this.position = packet.getPos();
            this.placedBlockDirection = packet.getDirection();
            this.hand = packet.getHand();
            this.facingX = packet.getFacingX();
            this.facingY = packet.getFacingY();
            this.facingZ = packet.getFacingZ();

            if(this.position != null) {
                event.setCancelled(true);
                final Minecraft mc = Minecraft.getMinecraft();
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                this.send = true;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.position, this.placedBlockDirection, this.hand, this.facingX, this.facingY, this.facingZ));
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
        }
        if(event.getPacket() instanceof CPacketPlayerTryUseItem) {
            if (send) {
                send = false;
                return;
            }
            final CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem) event.getPacket();
            this.hand = packet.getHand();

            if(this.hand != null) {
                event.setCancelled(true);
                final Minecraft mc = Minecraft.getMinecraft();
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                this.send = true;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(this.hand));
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
        }
        if (event.getPacket() instanceof CPacketUseEntity) {
            if (send) {
                send = false;
                return;
            }
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                final Minecraft mc = Minecraft.getMinecraft();

                this.entity = packet.getEntityFromWorld(mc.world);

                if(this.entity != null) {
                    event.setCancelled(true);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                    this.send = true;
                    mc.player.connection.sendPacket(new CPacketUseEntity(this.entity));
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                }
            }
        }
    }
}