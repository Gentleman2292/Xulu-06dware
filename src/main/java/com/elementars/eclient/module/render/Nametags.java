package com.elementars.eclient.module.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.util.*;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemies;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.friend.Nicknames;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.combat.PopCounter;
import com.elementars.eclient.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

/**
 * @author John200410 and Elementars
 */
public class Nametags extends Module {

    private final RenderUtils renderUtils = new RenderUtils();

    public Nametags() {
        super("NameTags", "Enhances nametags", Keyboard.KEY_NONE, Category.RENDER, true);
        INSTANCE = this;
    }

    private final Value<Boolean> outline = register(new Value<>("Outline", this, true));
    private final Value<Boolean> Orainbow = register(new Value<>("Outline Rainbow", this, false));
    private final Value<Integer> Ored = register(new Value<>("Outline Red", this, 0, 0, 255));
    private final Value<Integer> Ogreen = register(new Value<>("Outline Green", this, 0, 0, 255));
    private final Value<Integer> Oblue = register(new Value<>("Outline Blue", this, 0, 0, 255));
    private final Value<Integer> Oalpha = register(new Value<>("Outline Alpha", this, 150, 0, 255));
    private final Value<Float> Owidth = register(new Value<>("Outline Width", this, 1.5f, 0f, 3f));
    private final Value<Boolean> reversed = register(new Value<>("Reversed", this, false));
    private final Value<Boolean> reversedHand = register(new Value<>("Reversed Hand", this, false));
    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> max = register(new Value<>("Show Max Enchants", this, true));
    private final Value<Boolean> maxText = register(new Value<>("Show Max Text", this, true));
    private final Value<Boolean> health = register(new Value<>("Health", this, true));
    private final Value<Boolean> gameMode = register(new Value<>("GameMode", this, true));
    private final Value<Boolean> ping = register(new Value<>("Ping", this, true));
    private final Value<Boolean> pingColor = register(new Value<>("Ping Color", this, true));
    private final Value<Boolean> armor = register(new Value<>("Armor", this, true));
    private final Value<Boolean> durability = register(new Value<>("Durability", this, true));
    private final Value<Boolean> item = register(new Value<>("Item Name", this, true));
    private final Value<Boolean> invisibles = register(new Value<>("Invisibles", this, false));
    private final Value<Boolean> pops = register(new Value<>("Pop Count", this, true));
    private final Value<Float> scale = register(new Value<>("Scale", this, 0.05f, 0.01f, 0.09f));
    private final Value<Float> height = register(new Value<>("Height", this, 2.5f, 0.5f, 5f));
    private final Value<String> friendMode = register(new Value<>("Friend Mode", this, "Text", new ArrayList<>(
            Arrays.asList("Text", "Box")
    )));
    private final Value<Boolean> friends = register(new Value<>("Friends", this, true));
    private final Value<Boolean> enemies = register(new Value<>("Enemies", this, true));
    private final Value<Integer> red = register(new Value<>("Friend Red", this, 0, 0, 255));
    private final Value<Integer> green = register(new Value<>("Friend Green", this, 130, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Friend Blue", this, 130, 0, 255));
    private final Value<Integer> Ered = register(new Value<>("Enemy Red", this, 200, 0, 255));
    private final Value<Integer> Egreen = register(new Value<>("Enemy Green", this, 0, 0, 255));
    private final Value<Integer> Eblue = register(new Value<>("Enemy Blue", this, 0, 0, 255));

    public static Nametags INSTANCE;

    private ICamera camera = new Frustum();

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.player == null) return;
        //GlStateManager.enableTexture2D();
        //GlStateManager.disableLighting();
        //GlStateManager.disableDepth();
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)event.getPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)event.getPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)event.getPartialTicks();

        camera.setPosition(d3,  d4,  d5);

        List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
        players.sort(Comparator.comparing(entityPlayer -> mc.player.getDistance((EntityPlayer)entityPlayer)).reversed());
        for (EntityPlayer p : players) {
            NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(p.getGameProfile().getId());
            if (!camera.isBoundingBoxInFrustum(p.getEntityBoundingBox())) continue;
            if ((p != mc.getRenderViewEntity()) && (p.isEntityAlive())) {
                double pX = p.lastTickPosX + (p.posX - p.lastTickPosX) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosX;
                double pY = p.lastTickPosY + (p.posY - p.lastTickPosY) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosY;
                double pZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosZ;
                if (npi != null && getShortName(npi.getGameType().getName()).equalsIgnoreCase("SP") && !invisibles.getValue()) continue;
                if (!p.getName().startsWith("Body #")) {
                    renderNametag(p, pX, pY, pZ);
                }
            }
        }

        //GlStateManager.disableTexture2D();
        //RenderHelper.disableStandardItemLighting();
        //GlStateManager.enableLighting();
        //GlStateManager.enableDepth();
    }

    public String getShortName(String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return "S";
        }
        else if (gameType.equalsIgnoreCase("creative")) {
            return "C";
        }
        else if (gameType.equalsIgnoreCase("adventure")) {
            return "A";
        }
        else if (gameType.equalsIgnoreCase("spectator")) {
            return "SP";
        }
        else {
            return "NONE";
        }
    }

    public String getHealth(float health) {
        if (health > 18) {
            return "a";
        }
        else if (health > 16) {
            return "2";
        }
        else if (health > 12) {
            return "e";
        }
        else if (health > 8) {
            return "6";
        }
        else if (health > 5) {
            return "c";
        }
        else {
            return "4";
        }
    }

    public String getPing(float ping) {
        if (ping > 200) {
            return "c";
        }
        else if (ping > 100) {
            return "e";
        }
        else {
            return "a";
        }
    }

    boolean shownItem;

    private String getName(EntityPlayer player) {
        if (Nicknames.hasNickname(player.getName())) {
            return Nicknames.getNickname(player.getName());
        }
        return player.getName();
    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        int l4 = 0;
        shownItem = false;
        GlStateManager.pushMatrix();
        //GlStateManager.enableTexture2D();
        FontRenderer var13 = Wrapper.getMinecraft().fontRenderer;
        NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
        boolean isFriend = Friends.isFriend(player.getName()) && friends.getValue();
        boolean isEnemy = Enemies.isEnemy(player.getName()) && enemies.getValue();
        String name = ((isFriend || isEnemy) && friendMode.getValue().equalsIgnoreCase("Text")  ? Command.SECTIONSIGN() + (isFriend ? "b" : "c") : player.isSneaking() ? Command.SECTIONSIGN() + "9" : Command.SECTIONSIGN() + "r") + getName(player) + (gameMode.getValue() && npi != null ? " [" + getShortName(npi.getGameType().getName()) + "]" : "") + (ping.getValue() && npi != null ? " " + (pingColor.getValue() ? Command.SECTIONSIGN() + getPing(npi.getResponseTime()) : "") + npi.getResponseTime() + "ms" : "") + (health.getValue() ? " " + Command.SECTIONSIGN() + getHealth(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount()) : "") + (PopCounter.INSTANCE.popMap.containsKey(player) && pops.getValue() ? " " + ChatFormatting.DARK_RED +  "-" + PopCounter.INSTANCE.popMap.get(player) : "");
        name = name.replace(".0", "");
        //if (npi != null && getShortName(npi.getGameType().getName()).equalsIgnoreCase("SP") && !invisibles.getValue()) return;
        float distance = mc.player.getDistance(player);
        float var15 = ((distance / 5 <= 2 ? 2.0F : (distance / 5) * ((scale.getValue() * 10) + 1)) * 2.5f) * (scale.getValue() / 10);
        float var14 = scale.getValue() * getNametagSize(player); //0.016666668F

        GL11.glTranslated((float) x, (float) y + height.getValue() - (player.isSneaking() ? 0.4 : 0) + (distance / 5 > 2 ? distance / 12 - 0.7: 0), (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(mc.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(mc.renderManager.playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float)0);
        GL11.glScalef(-var15, -var15, var15);

        //GlStateManager.enableTexture2D();
        //GlStateManager.disableLighting();
        //lStateManager.disableDepth();

        // Disable lightning and depth test
        renderUtils.disableGlCap(GL_LIGHTING, GL_DEPTH_TEST);

        // Enable blend
        renderUtils.enableGlCap(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        int width;
        if (cf.getValue()) {
            width = Xulu.cFontRenderer.getStringWidth(name) / 2 + 1;
        } else {
            width = var13.getStringWidth(name) / 2;
        }

        int color = (isFriend || isEnemy) && friendMode.getValue().equalsIgnoreCase("Box") ? (isFriend ? new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB() : new Color(Ered.getValue(), Egreen.getValue(), Eblue.getValue()).getRGB()) : ColorUtils.Colors.BLACK;
        int outlineColor = new Color(Ored.getValue(), Ogreen.getValue(), Oblue.getValue(), Oalpha.getValue()).getRGB();
        if (Orainbow.getValue()) outlineColor = ColorUtils.changeAlpha(Xulu.rgb, Oalpha.getValue());
        Gui.drawRect(-width - 2, 10, width + 1, 20, ColorUtils.changeAlpha(color, 120));
        //if (outline.getValue()) XuluTessellator.drawRectOutline(-width - 2, 10, width + 1, 20, 0.5D, outlineColor);
        if (outline.getValue()) XuluTessellator.drawOutlineLine(-width - 2, 10, width + 1, 20, Owidth.getValue(), outlineColor);
        if (cf.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(name, -width, 10, -1);
        } else {
            mc.fontRenderer.drawStringWithShadow(name, -width, 11, -1);
        }

        //GlStateManager.enableTexture2D();
        //GlStateManager.enableLighting();
        if (armor.getValue()) {
            int xOffset = -6;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack != null) {
                    xOffset -= 8;
                    if (armourStack.getItem() != Items.AIR) ++count;
                }
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) ++count;

            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);

            ItemStack renderStack;
            if (reversedHand.getValue() ? player.getHeldItemOffhand().getItem() != Items.AIR : player.getHeldItemMainhand().getItem() != Items.AIR) {
                xOffset -= 8;
                if (reversedHand.getValue()) {
                    renderStack = player.getHeldItemOffhand().copy();
                    renderItem(player, renderStack, xOffset, -10, cacheX, false);
                } else {
                    renderStack = player.getHeldItemMainhand().copy();
                    renderItem(player, renderStack, xOffset, -10, cacheX, true);
                }
                xOffset += 16;
            } else {
                if (!reversedHand.getValue()) {
                    shownItem = true;
                }
            }
            if (reversed.getValue()) {
                for (int index = 0; index <= 3; ++index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        ItemStack renderStack1 = armourStack.copy();

                        renderItem(player, renderStack1, xOffset, -10, cacheX, false);
                        xOffset += 16;
                    }
                }
            } else {
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        ItemStack renderStack1 = armourStack.copy();

                        renderItem(player, renderStack1, xOffset, -10, cacheX, false);
                        xOffset += 16;
                    }
                }
            }

            ItemStack renderOffhand;
            if (reversedHand.getValue() ? player.getHeldItemMainhand().getItem() != Items.AIR : player.getHeldItemOffhand().getItem() != Items.AIR) {
                xOffset -= 0;
                if (reversedHand.getValue()) {
                    renderOffhand = player.getHeldItemMainhand().copy();
                    renderItem(player, renderOffhand, xOffset, -10, cacheX, true);
                } else {
                    renderOffhand = player.getHeldItemOffhand().copy();
                    renderItem(player, renderOffhand, xOffset, -10, cacheX, false);
                }
                xOffset += 8;
            }
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        } else {
            if (durability.getValue()) {
                int xOffset = -6;
                int count = 0;

                for (ItemStack armourStack : player.inventory.armorInventory) {
                    if (armourStack != null) {
                        xOffset -= 8;
                        if (armourStack.getItem() != Items.AIR) ++count;
                    }
                }
                if (player.getHeldItemOffhand().getItem() != Items.AIR) ++count;


                int cacheX = xOffset - 8;
                xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);

                ItemStack renderStack;
                /*
                if (reversedHand.getValue() ? player.getHeldItemOffhand().getItem() != Items.AIR : player.getHeldItemMainhand().getItem() != Items.AIR) {
                    xOffset -= 8;
                    if (reversedHand.getValue()) {
                        renderStack = player.getHeldItemOffhand().copy();
                        renderItem(player, renderStack, xOffset, -10, cacheX, false);
                    } else {
                        renderStack = player.getHeldItemMainhand().copy();
                        renderItem(player, renderStack, xOffset, -10, cacheX, true);
                    }
                    xOffset += 16;
                } else {
                    if (!reversedHand.getValue()) {
                        shownItem = true;
                    }
                }
                */
                if (reversed.getValue()) {
                    for (int index = 0; index <= 3; ++index) {
                        ItemStack armourStack = player.inventory.armorInventory.get(index);
                        if (armourStack != null && armourStack.getItem() != Items.AIR) {
                            ItemStack renderStack1 = armourStack.copy();

                            renderDurabilityText(player, renderStack1, xOffset, -10);
                            xOffset += 16;
                        }
                    }
                } else {
                    for (int index = 3; index >= 0; --index) {
                        ItemStack armourStack = player.inventory.armorInventory.get(index);
                        if (armourStack != null && armourStack.getItem() != Items.AIR) {
                            ItemStack renderStack1 = armourStack.copy();

                            renderDurabilityText(player, renderStack1, xOffset, -10);
                            xOffset += 16;
                        }
                    }
                }
                /*
                ItemStack renderOffhand;
                if (reversedHand.getValue() ? player.getHeldItemMainhand().getItem() != Items.AIR : player.getHeldItemOffhand().getItem() != Items.AIR) {
                    xOffset -= 0;
                    if (reversedHand.getValue()) {
                        renderOffhand = player.getHeldItemMainhand().copy();
                        renderItem(player, renderOffhand, xOffset, -10, cacheX, true);
                    } else {
                        renderOffhand = player.getHeldItemOffhand().copy();
                        renderItem(player, renderOffhand, xOffset, -10, cacheX, false);
                    }
                    xOffset += 8;
                }
                */
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
            }
        }

        // Reset caps
        renderUtils.resetCaps();

        // Reset color
        GlStateManager.resetColor();
        glColor4f(1F, 1F, 1F, 1F);

        // Pop
        glPopMatrix();

        //GlStateManager.enableDepth();
        //GlStateManager.disableLighting();
        //GlStateManager.popMatrix();
    }


    public float getNametagSize(EntityLivingBase player) {
        // return mc.thePlayer.getDistanceToEntity(player) / 4.0F <= 2.0F ? 2.0F
        // : mc.thePlayer.getDistanceToEntity(player) / 4.0F;

        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);

        return (float) twoDscale + (mc.player.getDistance(player) / (0.7f * 10));
    }

    public void drawBorderRect(float left, float top, float right, float bottom, int bcolor, int icolor, float f) {
        drawGuiRect(left + f, top + f, right - f, bottom - f, icolor);
        drawGuiRect(left, top, left + f, bottom, bcolor);
        drawGuiRect(left + f, top, right, top + f, bcolor);
        drawGuiRect(left + f, bottom - f, right, bottom, bcolor);
        drawGuiRect(right - f, top + f, right, bottom - f, bcolor);
    }

    public static void drawGuiRect(double x1, double y1, double x2, double y2, int color)
    {
        float red = (color>> 24 & 0xFF) / 255.0F;
        float green = (color>> 16 & 0xFF) / 255.0F;
        float blue = (color>> 8 & 0xFF) / 255.0F;
        float alpha = (color& 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(green, blue, alpha, red);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);

    }

    public static void fakeGuiRect(double left, double top, double right, double bottom, int color)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    // man i love doubles!
    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int internalColor,
                                        int borderColor) {
        GlStateManager.pushMatrix();
        enableGL2D();
        fakeGuiRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        fakeGuiRect(x + width, y, x1 - width, y + width, borderColor);
        fakeGuiRect(x, y, x + width, y1, borderColor);
        fakeGuiRect(x1 - width, y, x1, y1, borderColor);
        fakeGuiRect(x + width, y1 - width, x1 - width, y1, borderColor);
        disableGL2D();
        GlStateManager.popMatrix();
    }

    public void renderItem(EntityPlayer player, ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);

        GlStateManager.disableDepth();
        GlStateManager.enableDepth();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0F;
        GlStateManager.scale(1, 1, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
        if (durability.getValue()) {
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12);
        }
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(1, 1, 1);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        renderEnchantText(player, stack, x, y - 18);
        if (!shownItem && item.getValue() && showHeldItemText) {
            if (cf.getValue()) {
                Xulu.cFontRenderer.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - (Xulu.cFontRenderer.getStringWidth(stack.getDisplayName()) / 2), y - 37, -1);
            } else {
                mc.fontRenderer.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - (mc.fontRenderer.getStringWidth(stack.getDisplayName()) / 2), y - 37, -1);
            }
            shownItem = true;
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public boolean isMaxEnchants(ItemStack stack) {
        NBTTagList enchants = stack.getEnchantmentTagList();
        List<String> enchantments = new ArrayList<>();
        int count = 0;
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    enchantments.add(enc.getTranslatedName(level));
                }
            }
            if (stack.getItem() == Items.DIAMOND_HELMET) {
                int maxnum = 5;
                for (String s : enchantments) {
                    if (s.equalsIgnoreCase("Protection IV")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Respiration III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Aqua Affinity")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Unbreaking III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Mending")) {
                        count += 1;
                    }
                }
                return count >= maxnum;
            }
            else if (stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                int maxnum = 3;
                for (String s : enchantments) {
                    if (s.equalsIgnoreCase("Protection IV")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Unbreaking III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Mending")) {
                        count += 1;
                    }
                }
                return count >= maxnum;
            }
            else if (stack.getItem() == Items.DIAMOND_LEGGINGS) {
                int maxnum = 3;
                for (String s : enchantments) {
                    if (s.equalsIgnoreCase("Blast Protection IV")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Unbreaking III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Mending")) {
                        count += 1;
                    }
                }
                return count >= maxnum;
            }
            else if (stack.getItem() == Items.DIAMOND_BOOTS) {
                int maxnum = 5;
                for (String s : enchantments) {
                    if (s.equalsIgnoreCase("Protection IV")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Feather Falling IV")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Depth Strider III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Unbreaking III")) {
                        count += 1;
                    }
                    if (s.equalsIgnoreCase("Mending")) {
                        count += 1;
                    }
                }
                return count >= maxnum;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void renderDurabilityText(EntityPlayer player, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);

        GlStateManager.disableDepth();
        GlStateManager.enableDepth();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1, 1, 0.01f);
        GlStateManager.scale(1, 1, 1);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool) {
            float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
            float red = 1 - green;
            int dmg = 100 - (int) (red * 100);
            if (cf.getValue()) {
                Xulu.cFontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                        ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
            } else {
                mc.fontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                        ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(EntityPlayer player, ItemStack stack, int x, int y) {
        int encY = y;
        int yCount = y;
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool) {
            if (durability.getValue()) {
                float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
                float red = 1 - green;
                int dmg = 100 - (int) (red * 100);
                if (cf.getValue()) {
                    Xulu.cFontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                            ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                } else {
                    mc.fontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                            ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                }
            }
        }
        if (max.getValue()) {
            if (isMaxEnchants(stack)) {
                GL11.glPushMatrix();
                GL11.glScalef(1f, 1f, 0);
                if (maxText.getValue()) {
                    if (cf.getValue()) {
                        Xulu.cFontRenderer.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, ColorUtils.Colors.RED);
                    } else {
                        mc.fontRenderer.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, ColorUtils.Colors.RED);
                    }
                }
                GL11.glScalef(1f, 1f, 1);
                GL11.glPopMatrix();
                return;
            }
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    if (enc.isCurse()) continue;
                    String encName = level == 1 ? enc.getTranslatedName(level).substring(0, 3).toLowerCase() : enc.getTranslatedName(level).substring(0, 2).toLowerCase() + level;
                    //encName = encName + level;
                    encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
                    GL11.glPushMatrix();
                    GL11.glScalef(1f, 1f, 0);
                    if (cf.getValue()) {
                        Xulu.cFontRenderer.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                    } else {
                        mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                    }
                    GL11.glScalef(1f, 1f, 1);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount += 8;
                }
            }
        }
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

}