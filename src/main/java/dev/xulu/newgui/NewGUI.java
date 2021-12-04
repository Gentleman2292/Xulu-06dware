package dev.xulu.newgui;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Wrapper;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.elements.menu.ElementSlider;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.ValueManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//Deine Imports

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class NewGUI extends GuiScreen {
   public static ArrayList<dev.xulu.newgui.Panel> panels;
   public static ArrayList<dev.xulu.newgui.Panel> rpanels;
   private ModuleButton mb = null;
   public ValueManager setmgr;

   /*
    * Konstrukor sollte nur einmal aufgerufen werden => in der MainMethode des eigenen Codes
    * hier Client.startClient()
    * das GUI wird dann so ge  ffnet:
    * 		mc.displayGuiScreen(Client.clickgui);
    * 		this.setToggled(false);
    * das Module wird sofort wieder beendet damit
    * n  chstes mal nicht 2mal der z.B. 'RSHIFT' Knopf gedr  ckt
    * werden muss
    */

    public static ArrayList<dev.xulu.newgui.Panel> getPanels() {
        return panels;
    }

    public static dev.xulu.newgui.Panel getPanelByName(String in) {
        for (dev.xulu.newgui.Panel p : getPanels()) {
            if (p.title.equalsIgnoreCase(in)) {
                return p;
           }
       }
        return null;
    }

    public NewGUI() {
       setmgr = Xulu.VALUE_MANAGER;

       panels = new ArrayList<>();
       double pwidth = 100;
       double pheight = 13;
       double px = 10;
       double py = 10;
       double pyplus = pheight + 10;

       /*
        * Zum Sortieren der Panels einfach die Reihenfolge im Enum   ndern ;)
        */
       for (Category c : Category.values()) {
           if (c == Category.HIDDEN || c == Category.HUD) continue;
           boolean isEmpty = true;
           for (Module m : Xulu.MODULE_MANAGER.getModules()) {
               if (!m.getCategory().equals(c)) {
                   continue;
               }
               isEmpty = false;
           }
           if (isEmpty) continue;
           String title = Character.toUpperCase(c.name().toLowerCase().charAt(0)) + c.name().toLowerCase().substring(1);
           NewGUI.panels.add(new dev.xulu.newgui.Panel(title, px, py, pwidth, pheight, false, this) {
                       @Override
                       public void setup() {
                           for (Module m : Xulu.MODULE_MANAGER.getModules()) {
                               if (!m.getCategory().equals(c)) {
                                   continue;
                               }
                               this.Elements.add(new ModuleButton(m, this));
                           }
                       }
           });
           py += pyplus;
       }

       /*
        * Wieso nicht einfach
        * 		rpanels = panels;
        * 		Collections.reverse(rpanels);
        * Ganz eifach:
        * 		durch diese Zuweisung wird rpanels einfach nur eine Weiterleitung
        * 		zu panels, was mit 'Collections.reverse(rpanels);' nicht ganz
        * 		funktionieren w  rde. Und da die Elemente nur 'r  berkopiert' werden
        * 		gibt es keine Probleme ;)
        */
       rpanels = new ArrayList<dev.xulu.newgui.Panel>();
       for (dev.xulu.newgui.Panel p : panels) {
           if (p.Elements.isEmpty()) { continue; }
           rpanels.add(p);
       }
       Collections.reverse(rpanels);

   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
       /*
        * Panels und damit auch Buttons rendern.
        * panels wird NUR hier im Code verwendet, da das
        * zuletzt gerenderte Panel ganz oben ist
        * Auch wenn es manchmal egal w  re ob panels/rpanels
        * benutzt wird habe ich mich einfach mal dazu entschieden,
        * einfach weil es einfacher ist nur einmal panels zu benutzen
        */
       for (dev.xulu.newgui.Panel p : rpanels) {
           p.drawScreen(mouseX, mouseY, partialTicks);
           if (p.extended && p.visible && p.Elements != null) {
               for (ModuleButton b : p.Elements) {
                   if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
                       double off = b.height + 1;
                       Color temp = ColorUtil.getClickGUIColor().darker();
                       if (OldGui.rainbowgui.getValue()) {
                           temp = (new Color(Xulu.rgb)).darker();
                       }
                       int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();

                       for (Element e : b.menuelements) {
                           if (!e.set.isVisible()) continue;
                           e.offset = off;
                           e.update();
                           e.drawScreen(mouseX, mouseY, partialTicks);
                           off += e.height;
                       }
                   }
               }
           }
       }


       /*															*/ ScaledResolution s = new ScaledResolution(mc);

       mb = null;
       /*
        *   berpr  fen ob ein Button listening == true hat, wenn
        * ja, dann soll nicht mehr gesucht werden, nicht dass
        * 1+ auf listening steht...
        */
       listen:
       for (dev.xulu.newgui.Panel p : rpanels) {
           if (p != null && p.visible && p.extended && p.Elements != null
                   && p.Elements.size() > 0) {
               for (ModuleButton e : p.Elements) {
                   if (e.listening) {
                       mb = e;
                       break listen;
                   }
               }
           }
       }

       /*
        * Settings rendern. Da Settings   ber alles gerendert werden soll,
        * abgesehen vom ListeningOverlay werden die Elements von hier aus
        * fast am Schluss gerendert
        */
       /*
       for (Panel panel : rpanels) {
           if (panel.extended && panel.visible && panel.Elements != null) {
               for (ModuleButton b : panel.Elements) {
                   if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
                       double off = b.height + 1;
                       Color temp = ColorUtil.getClickGUIColor().darker();
                       if (OldGui.rainbowgui.getValue()) {
                           temp = (new Color(Xulu.rgb)).darker();
                       }
                       int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();

                       for (Element e : b.menuelements) {
                           e.offset = off;
                           e.update();
                           e.drawScreen(mouseX, mouseY, partialTicks);
                           off += e.height;
                       }
                   }
               }
           }

       }
       */

       /*
        * Wenn mb != null ist => ein Button listening == true
        * dann wird das Overlay gerendert mit ein paar Informationen.
        */
       if(mb != null){
           drawRect(0, 0, this.width, this.height, 0x88101010);
           GL11.glPushMatrix();
           GL11.glTranslatef(s.getScaledWidth() / 2, s.getScaledHeight() / 2, 0.0F);
           GL11.glScalef(4.0F, 4.0F, 0F);
           FontUtil.drawTotalCenteredStringWithShadow("Listening...", 0, -10, 0xffffffff);
           GL11.glScalef(0.5F, 0.5F, 0F);
           FontUtil.drawTotalCenteredStringWithShadow("Press 'ESCAPE' to unbind " + mb.mod.getName() + (mb.mod.getKey() > -1 ? " (" + Keyboard.getKeyName(mb.mod.getKey())+ ")" : ""), 0, 0, 0xffffffff);
           GL11.glScalef(0.25F, 0.25F, 0F);
           FontUtil.drawTotalCenteredStringWithShadow("by HeroCode", 0, 20, 0xffffffff);
           GL11.glPopMatrix();
       }

       /*
        * Nicht ben  tigt, aber es ist so einfach sauberer ;)
        * Und ohne diesen call k  nnen keine GUIButtons/andere Elemente
        * gerendert werden
        */
       for (dev.xulu.newgui.Panel p : panels) {
           if (!p.extended) continue;
           for (ModuleButton moduleButton : p.Elements) {
               if (moduleButton.mod instanceof com.elementars.eclient.guirewrite.Element) continue;
               if (moduleButton.isHovered(mouseX, mouseY)) {
                   if (OldGui.customfont.getValue()) {
                       Gui.drawRect((mouseX + 6), mouseY + 6, (mouseX + Xulu.cFontRenderer.getStringWidth(moduleButton.mod.getDesc()) + 11), (int)(mouseY + Xulu.cFontRenderer.getHeight() + 10), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, OldGui.bgAlpha.getValue()));
                       if (OldGui.outline.getValue()) XuluTessellator.drawRectOutline((mouseX + 6), mouseY + 6, (mouseX + Xulu.cFontRenderer.getStringWidth(moduleButton.mod.getDesc()) + 11), (int)(mouseY + Xulu.cFontRenderer.getHeight() + 10), 1,  ColorUtils.changeAlpha(ColorUtil.getClickGUIColor().getRGB(), 225));
                       Xulu.cFontRenderer.drawStringWithShadow(moduleButton.mod.getDesc(), mouseX + 8, mouseY + 7, ColorUtils.Colors.WHITE);
                   } else {
                       Gui.drawRect((mouseX + 6), mouseY + 6, (mouseX + Wrapper.getMinecraft().fontRenderer.getStringWidth(moduleButton.mod.getDesc()) + 11), (mouseY + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT + 10), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, OldGui.bgAlpha.getValue()));
                       if (OldGui.outline.getValue()) XuluTessellator.drawRectOutline((mouseX + 6), mouseY + 6, (mouseX + Wrapper.getMinecraft().fontRenderer.getStringWidth(moduleButton.mod.getDesc()) + 11), (mouseY + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT + 10), 1, ColorUtils.changeAlpha(ColorUtil.getClickGUIColor().getRGB(), 225));
                       Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(moduleButton.mod.getDesc(), mouseX + 9, mouseY + 9, ColorUtils.Colors.WHITE);
                   }
               }
           }
       }
       super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
       /*
        * Damit man nicht nochmal den Listeningmode aktivieren kann,
        * wenn er schon aktiviert ist
        */
       if(mb != null)return;

       /*
        * Ben  tigt damit auch mit Elements interagiert werden kann
        * besonders zu beachten ist dabei, dass zum einen rpanels aufgerufen
        * wird welche eine Eigenst  ndige Kopie von panels ist, genauer oben erkl  rt
        * Also rpanels damit zuerst das panel 'untersucht' wird, dass als letztes
        * gerendert wurde => Ganz oben ist!
        * sodass der Nutzer nicht mit dem Unteren interagiern kann, weil er es wohl
        * nicht will. Und damit nicht einfach mit Panels  anstatt Elements interagiert wird
        * werden hier nur die Settings untersucht. Und wenn wirklich interagiert wurde, dann
        * endet diese Methode hier.
        * Das ist auch in anderen Loops zu beobachten
        */
       for (dev.xulu.newgui.Panel panel : rpanels) {
           if (panel.extended && panel.visible && panel.Elements != null) {
               for (ModuleButton b : panel.Elements) {
                   if (b.extended) {
                       for (Element e : b.menuelements) {
                           if (!e.set.isVisible()) continue;
                           if (e.mouseClicked(mouseX, mouseY, mouseButton))
                               return;
                       }
                   }
               }
           }
       }

       /*
        * Ben  tigt damit mit ModuleButtons interagiert werden kann
        * und Panels 'gegriffen' werden k  nnen
        */
       for (dev.xulu.newgui.Panel p : rpanels) {
           if (p.mouseClicked(mouseX, mouseY, mouseButton))
               return;
       }

       /*
        * Nicht ben  tigt, aber es ist so einfach sauberer ;)
        */
       try {
           super.mouseClicked(mouseX, mouseY, mouseButton);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
       /*
        * Damit man nicht nochmal den Listeningmode aktivieren kann,
        * wenn er schon aktiviert ist
        */
       if(mb != null)return;

       /*
        * Eigentlich nur f  r die Slider ben  tigt, aber
        * durch diesen Call erf  hrt jedes Element, wenn
        * z.B. Rechtsklick losgelassen wurde
        */
       for (dev.xulu.newgui.Panel panel : rpanels) {
           if (panel.extended && panel.visible && panel.Elements != null) {
               for (ModuleButton b : panel.Elements) {
                   if (b.extended) {
                       for (Element e : b.menuelements) {
                           if (!e.set.isVisible()) continue;
                           e.mouseReleased(mouseX, mouseY, state);
                       }
                   }
               }
           }
       }

       /*
        * Ben  tigt damit Slider auch losgelassen werden k  nnen und nicht
        * immer an der Maus 'festkleben' :>
        */
       for (dev.xulu.newgui.Panel p : rpanels) {
           p.mouseReleased(mouseX, mouseY, state);
       }

       /*
        * Nicht ben  tigt, aber es ist so einfach sauberer ;)
        */
       super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) {
       /*
        * Ben  tigt f  r die Keybindfunktion
        */
       for (dev.xulu.newgui.Panel p : rpanels) {
           if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
               for (ModuleButton e : p.Elements) {
                   try {
                       if (e.keyTyped(typedChar, keyCode))return;
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   }
               }
           }
       }

       /*
        * keyTyped in GuiScreen MUSS aufgerufen werden, damit
        * man mit z.B. ESCAPE aus dem GUI gehen kann
        */
       try {
           super.keyTyped(typedChar, keyCode);
       } catch (IOException e2) {
           e2.printStackTrace();
       }
   }

   public void handleMouseInput() throws IOException {
       int scrollAmount = 5;
       if (Mouse.getEventDWheel() > 0) {
           for (dev.xulu.newgui.Panel p : rpanels) {
               p.y += scrollAmount;
           }
       }
       if (Mouse.getEventDWheel() < 0) {
           for (dev.xulu.newgui.Panel p : rpanels) {
               p.y -= scrollAmount;
           }
       }

       super.handleMouseInput();
   }

   @Override
   public void initGui() {
       /*
        * Start blur
        */
       if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer && OldGui.blur.getValue()) {
           if (mc.entityRenderer.getShaderGroup() != null) {
               mc.entityRenderer.getShaderGroup().deleteShaderGroup();
           }
           mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
       }
   }

   @Override
   public void onGuiClosed() {
       /*
        * End blur
        */
       if (mc.entityRenderer.getShaderGroup() != null) {
           mc.entityRenderer.getShaderGroup().deleteShaderGroup();
       }
       /*
        * Sliderfix
        */
       for (dev.xulu.newgui.Panel panel : NewGUI.rpanels) {
           if (panel.extended && panel.visible && panel.Elements != null) {
               for (ModuleButton b : panel.Elements) {
                   if (b.extended) {
                       for (Element e : b.menuelements) {
                           if(e instanceof ElementSlider){
                               ((ElementSlider)e).dragging = false;
                           }
                       }
                   }
               }
           }
       }
   }

   public void closeAllSettings() {
       for (Panel p : rpanels) {
           if (p != null && p.visible && p.extended && p.Elements != null
                   && p.Elements.size() > 0) {
               for (ModuleButton e : p.Elements) {
                   e.extended = false;
               }
           }
       }
   }
}
