package com.elementars.eclient.guirewrite;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.render.NewGui;
import dev.xulu.newgui.Panel;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.ValueManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

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
public class HUD extends GuiScreen {
   public static ArrayList<Frame> frames;
   public static ArrayList<Frame> rframes;
   public Panel hudPanel;
    private ModuleButton mb = null;
   public ValueManager setmgr;
   
   /*
    * Konstrukor sollte nur einmal aufgerufen werden => in der MainMethode des eigenen Codes
    * hier Client.startClient()
    * das GUI wird dann so ge  ffnet: 
    * 		mc.displayGuiScreen(Client.HUD);
    * 		this.setToggled(false);
    * das Module wird sofort wieder beendet damit
    * n  chstes mal nicht 2mal der z.B. 'RSHIFT' Knopf gedr  ckt
    * werden muss
    */

    public static ArrayList<Frame> getFrames() {
        return frames;
    }

    public static Frame getframeByName(String in) {
        for (Frame f : getFrames()) {
            if (f.title.equalsIgnoreCase(in)) {
                return f;
           }
       }
        return null;
    }

    public static void registerElements() {
        for (Module m : Xulu.MODULE_MANAGER.getModules()) {
            if (m instanceof com.elementars.eclient.guirewrite.Element) {
                ((com.elementars.eclient.guirewrite.Element) m).registerFrame();
            }
        }
    }

    public void refreshPanel() {
        hudPanel = new Panel("Elements", 10, 10, 100, 13, true, Xulu.newGUI) {
            @Override
            public void setup() {
                for (Module m : Xulu.MODULE_MANAGER.getModules()) {
                    if (!m.getCategory().equals(Category.HUD) && !(m instanceof com.elementars.eclient.guirewrite.Element)) {
                        continue;
                    }
                    this.Elements.add(new ModuleButton(m, this));
                }
            }
        };
    }

    public HUD() {
       setmgr = Xulu.VALUE_MANAGER;

       frames = new ArrayList<>();
       double pwidth = 80;
       double pheight = 15;
       double px = 10;
       double py = 10;
       double pyplus = pheight + 10;
       /*
        * Zum Sortieren der frames einfach die Reihenfolge im Enum   ndern ;)
        */
       HUD.frames.add(new Frame("PvPInfo", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Totems", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Obsidian", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Crystals", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Gapples", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("InvPreview", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("TextRadar", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("FeatureList", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Player", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Welcome", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("OldName", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("TheGoons", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Potions", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("StickyNotes", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Exp", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("HoleHud", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Info", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Armor", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("CraftingPreview", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("GodInfo", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Watermark", 2, 2, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Logo", px, py, pwidth, pheight, false, this));
       HUD.frames.add(new Frame("Target", px, py, pwidth, pheight, false, this));
       
       /*
        * Wieso nicht einfach
        * 		rframes = frames;
        * 		Collections.reverse(rframes);
        * Ganz eifach:
        * 		durch diese Zuweisung wird rframes einfach nur eine Weiterleitung
        * 		zu frames, was mit 'Collections.reverse(rframes);' nicht ganz 
        * 		funktionieren w  rde. Und da die Elemente nur 'r  berkopiert' werden
        * 		gibt es keine Probleme ;)
        */
       rframes = new ArrayList<Frame>();
       for (Frame f : frames) {
           rframes.add(f);
       }
       Collections.reverse(rframes);
       hudPanel = new Panel("Elements", px, py, 100, 13, true, Xulu.newGUI) {
           @Override
           public void setup() {
               for (Module m : Xulu.MODULE_MANAGER.getModules()) {
                   if (!m.getCategory().equals(Category.HUD) && !(m instanceof com.elementars.eclient.guirewrite.Element)) {
                       continue;
                   }
                   System.out.println("[HUD] We adding a modulebutton");
                   this.Elements.add(new ModuleButton(m, this));
               }
           }
       };
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
       /*
        * frames und damit auch Buttons rendern.
        * frames wird NUR hier im Code verwendet, da das
        * zuletzt gerenderte frame ganz oben ist 
        * Auch wenn es manchmal egal w  re ob frames/rframes
        * benutzt wird habe ich mich einfach mal dazu entschieden,
        * einfach weil es einfacher ist nur einmal frames zu benutzen
        */

       hudPanel.drawScreen(mouseX, mouseY, partialTicks);
       if (hudPanel.extended && hudPanel.visible && hudPanel.Elements != null) {
           for (ModuleButton b : hudPanel.Elements) {
               if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
                   double off = b.height + 1;
                   Color temp = ColorUtil.getClickGUIColor().darker();
                   if (NewGui.rainbowgui.getValue()) {
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

       mb = null;
       /*
        *   berpr  fen ob ein Button listening == true hat, wenn
        * ja, dann soll nicht mehr gesucht werden, nicht dass
        * 1+ auf listening steht...
        */
       listen:
       if (hudPanel != null && hudPanel.visible && hudPanel.extended && hudPanel.Elements != null
               && hudPanel.Elements.size() > 0) {
           for (ModuleButton e : hudPanel.Elements) {
               if (e.listening) {
                   mb = e;
                   break listen;
               }
           }
       }
       for (Frame f : frames) {
           f.drawScreen(mouseX, mouseY, partialTicks);
       }


       /*															*/ ScaledResolution s = new ScaledResolution(mc);
       
       /*
        *   berpr  fen ob ein Button listening == true hat, wenn
        * ja, dann soll nicht mehr gesucht werden, nicht dass 
        * 1+ auf listening steht...
        */
       /*
       listen:
       for (frame p : frames) {
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
       */
       
       /*
        * Settings rendern. Da Settings   ber alles gerendert werden soll,
        * abgesehen vom ListeningOverlay werden die Elements von hier aus
        * fast am Schluss gerendert
        */
       
       /*
        * Wenn mb != null ist => ein Button listening == true
        * dann wird das Overlay gerendert mit ein paar Informationen.
        */
       
       /*
        * Nicht ben  tigt, aber es ist so einfach sauberer ;)
        * Und ohne diesen call k  nnen keine GUIButtons/andere Elemente
        * gerendert werden
        */
       super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
       /*
        * Damit man nicht nochmal den Listeningmode aktivieren kann,
        * wenn er schon aktiviert ist
        */
       
       /*
        * Ben  tigt damit auch mit Elements interagiert werden kann
        * besonders zu beachten ist dabei, dass zum einen rframes aufgerufen
        * wird welche eine Eigenst  ndige Kopie von frames ist, genauer oben erkl  rt
        * Also rframes damit zuerst das frame 'untersucht' wird, dass als letztes
        * gerendert wurde => Ganz oben ist!
        * sodass der Nutzer nicht mit dem Unteren interagiern kann, weil er es wohl
        * nicht will. Und damit nicht einfach mit frames  anstatt Elements interagiert wird
        * werden hier nur die Settings untersucht. Und wenn wirklich interagiert wurde, dann
        * endet diese Methode hier.
        * Das ist auch in anderen Loops zu beobachten
        */

       if (hudPanel.extended && hudPanel.visible && hudPanel.Elements != null) {
           for (ModuleButton b : hudPanel.Elements) {
               if (b.extended) {
                   for (Element e : b.menuelements) {
                       if (e.mouseClicked(mouseX, mouseY, mouseButton))
                           return;
                   }
               }
           }
       }

       /*
        * Ben  tigt damit mit ModuleButtons interagiert werden kann
        * und Panels 'gegriffen' werden k  nnen
        */
       if (hudPanel.mouseClicked(mouseX, mouseY, mouseButton))
           return;
       for (Frame f : frames) {
           if (f.mouseClicked(mouseX, mouseY, mouseButton))
               return;
       }


       /*
        * Ben  tigt damit mit ModuleButtons interagiert werden kann
        * und frames 'gegriffen' werden k  nnen
        */
       
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
       
       /*
        * Eigentlich nur f  r die Slider ben  tigt, aber
        * durch diesen Call erf  hrt jedes Element, wenn
        * z.B. Rechtsklick losgelassen wurde
        */
       
       /*
        * Ben  tigt damit Slider auch losgelassen werden k  nnen und nicht
        * immer an der Maus 'festkleben' :>
        */

       if (hudPanel.extended && hudPanel.visible && hudPanel.Elements != null) {
           for (ModuleButton b : hudPanel.Elements) {
               if (b.extended) {
                   for (Element e : b.menuelements) {
                       e.mouseReleased(mouseX, mouseY, state);
                   }
               }
           }
       }

       /*
        * Ben  tigt damit Slider auch losgelassen werden k  nnen und nicht
        * immer an der Maus 'festkleben' :>
        */
       hudPanel.mouseReleased(mouseX, mouseY, state);
       for (Frame p : rframes) {
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
           for (Frame p : rframes) {
               p.y += scrollAmount;
           }
           hudPanel.y += scrollAmount;
       }
       if (Mouse.getEventDWheel() < 0) {
           for (Frame p : rframes) {
               p.y -= scrollAmount;
           }
           hudPanel.y -= scrollAmount;
       }

       super.handleMouseInput();
   }


}
