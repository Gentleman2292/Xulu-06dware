package com.elementars.eclient.module;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.guirewrite.HudEditor;
import com.elementars.eclient.guirewrite.elements.*;
import com.elementars.eclient.module.combat.*;
import com.elementars.eclient.module.core.CustomFont;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.module.core.TitleScreenShader;
import com.elementars.eclient.module.misc.*;
import com.elementars.eclient.module.movement.*;
import com.elementars.eclient.module.player.*;
import com.elementars.eclient.module.render.*;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.XuluTessellator;
import com.elementars.eclient.util.Wrapper;
import me.memeszz.aurora.module.modules.movement.FastSwim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModuleManager {
    private static ArrayList<Module> modules = new ArrayList<Module>();

    public void init() {
        //Core
        modules.add(new Global());
        modules.add(new CustomFont());
        modules.add(new TitleScreenShader());

       //Combat
        modules.add(new AntiChainPop());
        modules.add(new Aura());
        modules.add(new AutoArmor());
        modules.add(new AutoCrystal());
        modules.add(new AutoEz());
        //modules.add(new AutoLog());
        modules.add(new AutoFeetBlock());
        modules.add(new AutoRepair());
        modules.add(new AutoTotem());
        modules.add(new AutoTrap());
        modules.add(new AutoWeb());
        modules.add(new BreakAlert());
        modules.add(new CityBlocker());
        modules.add(new Criticals());
        modules.add(new DurabilityAlert());
        modules.add(new EXPFast());
        modules.add(new FastBow());
        modules.add(new FuckedDetect());
        modules.add(new HoleBlocker());
        modules.add(new HoleFill());
        modules.add(new MiddleClickPearl());
        modules.add(new Offhand());
        modules.add(new PearlAlert());
        modules.add(new PopCounter());
        modules.add(new SelfWeb());
        modules.add(new Sharp32kDetect());
        modules.add(new StrengthDetect());
        modules.add(new Surround());

        //Misc
        modules.add(new Announcer());
        modules.add(new AntiDeathScreen());
        modules.add(new AntiSound());
        modules.add(new AntiSpam());
        modules.add(new Australia());
        modules.add(new AutoQMain());
        modules.add(new AutoWither());
        modules.add(new Avoid());
        modules.add(new CameraClip());
        modules.add(new ColorSign());
        modules.add(new CoordLogger());
        modules.add(new CrashExploit());
        modules.add(new CustomChat());
        modules.add(new DonkeyAlert());
        modules.add(new FakePlayer());
        modules.add(new FakeVanilla());
        modules.add(new FovSlider());
        modules.add(new HopperNuker());
        modules.add(new LiquidInteract());
        modules.add(new MCF());
        modules.add(new MobOwner());
        modules.add(new NoEntityTrace());
        modules.add(new NoPacketKick());
        modules.add(new PortalChat());
        modules.add(new SkinFlicker());
        modules.add(new Time());
        modules.add(new Timer());
        modules.add(new VisualRange());

        //Movement
        modules.add(new ElytraFly());
        modules.add(new FastSwim());
        modules.add(new Flight());
        modules.add(new ForgeFly());
        modules.add(new GuiMove());
        modules.add(new HoleTP());
        modules.add(new Jesus());
        modules.add(new LongJump());
        modules.add(new NoSlowDown());
        //modules.add(new PacketFly());
        //modules.add(new Scaffold());
        modules.add(new Sprint());
        modules.add(new Step());
        modules.add(new Strafe());
        modules.add(new Velocity());

        //Player
        modules.add(new AntiVoid());
        modules.add(new AutoReplenish());
        modules.add(new AutoWalk());
        modules.add(new Blink());
        modules.add(new FastFall());
        modules.add(new Freecam());
        modules.add(new ItemSpoof());
        modules.add(new Multitask());
        modules.add(new NoBreakAnimation());
        modules.add(new PacketSwing());
        modules.add(new SelfLogoutSpot());
        modules.add(new Speedmine());
        modules.add(new TpsSync());
        modules.add(new XCarry());

        //Render
        modules.add(new AntiFog());
        modules.add(new Arrows());
        modules.add(new BlockHighlight());
        modules.add(new BossStack());
        modules.add(new BreakESP());
        modules.add(new Cape());
        modules.add(new Chams());
        modules.add(new Chat());
        modules.add(new ChunkFinder());
        modules.add(new Compass());
        modules.add(new EnchantColor());
        modules.add(new OldGui());
        modules.add(new NewGui());
        modules.add(new ESP());
        modules.add(new ExeterGui());
        modules.add(new ExtraTab());
        modules.add(new FullBright());
        modules.add(new HellenKeller());
        modules.add(new HoleESP());
        modules.add(new ImageESP());
        modules.add(new ItemESP());
        modules.add(new LogoutSpots());
        modules.add(new Nametags());
        modules.add(new NoHurtCam());
        modules.add(new NoRender());
        modules.add(new OffhandSwing());
        modules.add(new OutlineESP());
        modules.add(new Pathfind());
        modules.add(new Search());
        modules.add(new SecretShaders());
        modules.add(new PvPInfo());
        modules.add(new ShulkerPreview());
        modules.add(new Skeleton());
        modules.add(new StorageESP());
        modules.add(new ToolTips());
        modules.add(new Tracers());
        modules.add(new Trajectories());
        modules.add(new ViewmodelChanger());
        modules.add(new VoidESP());
        modules.add(new Waypoints());
        modules.add(new Xray());

        //HUD
        modules.add(new HudEditor());
        modules.add(new Totems());
        modules.add(new Obsidian());
        modules.add(new Crystals());
        modules.add(new Gapples());
        modules.add(new Exp());
        modules.add(new InvPreview());
        modules.add(new CraftingPreview());
        modules.add(new TextRadar());
        modules.add(new FeatureList());
        modules.add(new Player());
        modules.add(new Welcome());
        modules.add(new OldName());
        modules.add(new TheGoons());
        modules.add(new Potions());
        modules.add(new StickyNotes());
        modules.add(new HoleHud());
        modules.add(new Info());
        modules.add(new Armor());
        modules.add(new GodInfo());
        modules.add(new Watermark());
        modules.add(new Logo());
        modules.add(new Target());
    }

    public static void onUpdate() {
        Xulu.MODULE_MANAGER.getModules().stream().filter(Module::isToggled).forEach(Module::onUpdate);
        OldGui.resetGui();
        Xulu.TASK_SCHEDULER.onUpdate();
    }

    public static void onRender() {
        Xulu.MODULE_MANAGER.getModules().stream().filter(Module::isToggled).forEach(Module::onRender);
    }

    public static void onKey() {
        if (Keyboard.getEventKeyState()) {
            Xulu.MODULE_MANAGER.getModules().forEach((m) -> m.onKey(Keyboard.getEventKey()));
        }
    }

    public static void onWorldRender(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().profiler.startSection("eclient");

        Minecraft.getMinecraft().profiler.startSection("setup");
//        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();

        GlStateManager.glLineWidth(1f);
        Vec3d renderPos = EntityUtil.getInterpolatedPos(Wrapper.getPlayer(), event.getPartialTicks());

        RenderEvent e = new RenderEvent(XuluTessellator.INSTANCE, renderPos);
        e.resetTranslation();
        Minecraft.getMinecraft().profiler.endSection();

        Xulu.MODULE_MANAGER.getModules().stream().filter(Module::isToggled).forEach(module -> {
            Minecraft.getMinecraft().profiler.startSection(module.getName());
            module.onWorldRender(e);
            Minecraft.getMinecraft().profiler.endSection();
        });

        Minecraft.getMinecraft().profiler.startSection("release");
        GlStateManager.glLineWidth(1f);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
//        GlStateManager.popMatrix();
        XuluTessellator.releaseGL();
        Minecraft.getMinecraft().profiler.endSection();

        Minecraft.getMinecraft().profiler.endSection();
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    @Deprecated
    public Module getModuleByName(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Module getModule(Class<? extends Module> clazz) {
        return modules.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleT(Class<T> clazz) {
        return modules.stream().filter(module -> module.getClass() == clazz).map(module -> (T) module).findFirst().orElse(null);
    }

    public static boolean isModuleEnabled(String name) {
        Module m = modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (m != null) {
            return m.isToggled();
        }
        return false;
    }
}
