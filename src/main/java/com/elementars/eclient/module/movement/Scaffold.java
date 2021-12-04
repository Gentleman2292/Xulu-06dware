package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.BlockInteractionHelper;
import dev.xulu.settings.Value;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 7/30/2020 - 12:32 PM
 * yes this is sb27 I don't really care
 */
public class Scaffold extends Module {

    private BlockPos block;
    private EnumFacing side;
    private boolean rotated;

    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;

    private final Value<Integer> delay = register(new Value<>("Delay", this, 0, 0, 20));

    public Scaffold() {
        super("Scaffold", "Automatically places blocks below you", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    int delayT;

    @Override
    public void onUpdate() {
        if (delayT > 0) delayT--;
    }

    @EventTarget
    public void onMove(PlayerMoveEvent event) {
        if (event.getEventState() == Event.State.PRE) {
            rotated = false;
            block = null;
            side = null;

            BlockPos pos = new BlockPos(mc.player.getPositionVector()).add(0, -1, 0);
            if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
                setBlockAndFacing(pos);

                if (block != null) {
                    float[] facing = BlockInteractionHelper.getDirectionToBlock(block.getX(), block.getY(), block.getZ(), side);

                    float yaw = facing[0];
                    float pitch = Math.min(90, facing[1] + 9);

                    rotated = true;
                    this.yaw = yaw;
                    this.pitch = pitch;
                    isSpoofingAngles = true;
                }
            }
        }
        if (event.getEventState() == Event.State.POST) {
            if (block != null) {
                if (delayT == 0) {
                    if (mc.player.getHeldItemMainhand() != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
                        if (mc.playerController.processRightClickBlock(mc.player, mc.world, block, side, new Vec3d(block.getX(), block.getY(), block.getZ()), EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS) {
                            delayT = delay.getValue();
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.motionX = 0;
                            mc.player.motionZ = 0;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onSend(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) event.getPacket()).yaw = (float) yaw;
                ((CPacketPlayer) event.getPacket()).pitch = (float) pitch;
            }
        }
    }

    private void setBlockAndFacing(BlockPos pos) {
        if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.AIR) {
            this.block = pos.add(0, -1, 0);
            side = EnumFacing.UP;
        } else if (mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR) {
            this.block = pos.add(-1, 0, 0);
            side = EnumFacing.EAST;
        } else if (mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.AIR) {
            this.block = pos.add(1, 0, 0);
            side = EnumFacing.WEST;
        } else if (mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.AIR) {
            this.block = pos.add(0, 0, -1);
            side = EnumFacing.SOUTH;
        } else if (mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.AIR) {
            this.block = pos.add(0, 0, 1);
            side = EnumFacing.NORTH;
        } else {
            block = null;
            side = null;
        }
    }
}
