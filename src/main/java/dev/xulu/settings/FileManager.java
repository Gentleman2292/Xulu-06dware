package dev.xulu.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemies;
import com.elementars.eclient.enemy.Enemy;
import com.elementars.eclient.dummy.DummyMod;
import com.elementars.eclient.font.CFontManager;
import com.elementars.eclient.friend.Friend;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.friend.Nicknames;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.guirewrite.Frame;
import com.elementars.eclient.guirewrite.HUD;
import com.elementars.eclient.guirewrite.elements.StickyNotes;
import com.elementars.eclient.guirewrite.elements.Welcome;
import com.elementars.eclient.macro.Macro;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.render.Search;
import com.elementars.eclient.module.render.Waypoints;
import com.elementars.eclient.module.render.Xray;
import com.elementars.eclient.util.FontHelper;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.NewGUI;
import com.elementars.eclient.util.NumberUtils;
import dev.xulu.newgui.Panel;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class FileManager
{
    public File Eclient;
    public File EclientSettings;
    public File EclientStorageESP;
    public File EclientCache;

    public FileManager()
    {
        this.Eclient = new File(Wrapper.getMinecraft().gameDir + File.separator + "Xulu");
        if (!this.Eclient.exists()) {
            this.Eclient.mkdirs();
        }

        this.EclientSettings = new File(Wrapper.getMinecraft().gameDir + File.separator + "Xulu" + File.separator + "Xulu Settings");
        if (!this.EclientSettings.exists()) {
            this.EclientSettings.mkdirs();
        }
        this.EclientStorageESP = new File(Wrapper.getMinecraft().gameDir + File.separator + "Xulu" + File.separator + "StorageESP Logs");
        if (!this.EclientStorageESP.exists()) {
            this.EclientStorageESP.mkdirs();
        }
    }

    public boolean appendTextFile(final String data, final String file) {
        try {
            final Path path = Paths.get(file, new String[0]);
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to write file: " + file);
            return false;
        }
        return true;
    }

    public void saveStorageESP(String name, String coords, String chests) {
        try {
            File file = new File(this.EclientStorageESP.getAbsolutePath(), name + ".txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(coords + " with " + chests + " chests");
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBinds() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Binds.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : Xulu.MODULE_MANAGER.getModules()) {
                try {
                    out.write(module.getName() + ":" + module.getKey());
                    out.write("\r\n");
                } catch(Exception e) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadBinds() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Binds.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String bind = curLine.split(":")[1];
                    int b = Integer.parseInt(bind);
                    Module m = Xulu.MODULE_MANAGER.getModuleByName(name);
                    if (m != null) {
                        m.setKey(b);
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveBinds();
        }
    }

    public void saveDummy() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Dummy.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : Xulu.MODULE_MANAGER.getModules()) {
                if (module.getCategory() == Category.DUMMY) {
                    try {
                        out.write(module.getName() + ":" + (module.getHudInfo() == null ? "null" : module.getHudInfo()));
                        out.write("\r\n");
                    } catch (Exception e) {
                        // empty
                    }
                }
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadDummy() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Dummy.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String info = curLine.split(":")[1];
                    if (info.equalsIgnoreCase("null")) {
                        Xulu.MODULE_MANAGER.getModules().add(new DummyMod(name));
                    } else {
                        Xulu.MODULE_MANAGER.getModules().add(new DummyMod(name, info));
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveDummy();
        }
    }

    public void saveFont() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Font.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write("Normal" + ":" + (CFontManager.customFont.getFont().getFontName().equalsIgnoreCase("Comfortaa Regular") ? "Comfortaa" : CFontManager.customFont.getFont().getFontName()) + ":" + CFontManager.customFont.getFont().getSize() + ":" + CFontManager.customFont.getFont().getStyle() + ":" + CFontManager.customFont.isAntiAlias() + ":" + CFontManager.customFont.isFractionalMetrics() + "\r\n");
            out.write("Xdolf" + ":" + (CFontManager.xFontRenderer.getFont().getFont().getFontName().equalsIgnoreCase("Comfortaa Regular") ? "Comfortaa" : CFontManager.xFontRenderer.getFont().getFont().getFontName()) + ":" + CFontManager.xFontRenderer.getFont().getFont().getSize() + ":" + CFontManager.xFontRenderer.getFont().getFont().getStyle() + ":" + CFontManager.xFontRenderer.isAntiAliasing() + ":NULL" + "\r\n");
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadFont() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Font.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String font = curLine.split(":")[0];
                    String name = curLine.split(":")[1];
                    String size = curLine.split(":")[2];
                    String style = curLine.split(":")[3];
                    String alias = curLine.split(":")[4];
                    String metrics = curLine.split(":")[5];
                    int si = Integer.parseInt(size);
                    int st = Integer.parseInt(style);
                    boolean al = Boolean.parseBoolean(alias);
                    boolean me = !metrics.equalsIgnoreCase("null") && Boolean.parseBoolean(metrics);
                    if (font.equalsIgnoreCase("Normal"))
                        FontHelper.setCFontRenderer(name, st, si, al, me);
                    else if (font.equalsIgnoreCase("Xdolf"))
                        FontHelper.setXdolfFontRenderer(name, st, si, al);
                    else
                        System.out.println("Invalid Font Type!");
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveFont();
        }
    }

    public void saveStickyNote() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Note.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(StickyNotes.saveText + "\r\n");
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadStickyNote() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Note.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String note = curLine.split(":")[0];
                    StickyNotes.processText(note);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveStickyNote();
        }
    }

    public void saveWelcomeMessage() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Welcome.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Welcome.text + "\r\n");
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadWelcomeMessage() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Welcome.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    Welcome.handleWelcome(curLine);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveWelcomeMessage();
        }
    }

    public void saveXray() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Xray.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Block block : Xray.getBLOCKS()) {
                try {
                    out.write(Block.REGISTRY.getNameForObject(block).getPath());
                    out.write("\r\n");
                } catch(Exception e) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            //
        }
    }

    public void loadXray() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Xray.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            Xray.getBLOCKS().clear();
            while ((line = br.readLine()) != null) {
                try {
                    Xray.getBLOCKS().add(Objects.requireNonNull(Block.getBlockFromName(line)));
                } catch (NullPointerException npe) {
                    //
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //saveXray();
        }
    }

    public void saveSearch() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Search.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Block block : Search.getBLOCKS()) {
                try {
                    out.write(Block.REGISTRY.getNameForObject(block).getPath());
                    out.write("\r\n");
                } catch(Exception e) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            //
        }
    }

    public void loadSearch() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Search.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            Search.getBLOCKS().clear();
            while ((line = br.readLine()) != null) {
                try {
                    Search.getBLOCKS().add(Objects.requireNonNull(Block.getBlockFromName(line)));
                } catch (NullPointerException npe) {
                    //
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //saveXray();
        }
    }

    public void saveHacks() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Modules.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : Xulu.MODULE_MANAGER.getModules()) {
                try {
                    if (module.isToggled() && !module.getName().matches("null") && !module.getName().equals("Log Out Spot") && !module.getName().equals("Freecam") && !module.getName().equals("Blink") && !module.getName().equals("Join/Leave msgs") && !module.getName().equals("Elytra +") && !module.getName().equals("Sound")) {
                        out.write(module.getName());
                        out.write("\r\n");
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            //
        }
    }
    public void saveFriends() {
    try {
        File file = new File(this.Eclient.getAbsolutePath(), "Friends.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        for (Friend f : Friends.getFriends()) {
            try {
                out.write(f.getUsername());
                out.write("\r\n");
            } catch(Exception e) {
                // empty
            }
        }
        out.close();
    }
    catch (Exception file) {
        // empty catch block
    }
}
    public void loadFriends() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Friends.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Friends.addFriend(line);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveFriends();
        }
    }

    public void saveNicks() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Nicknames.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Nicknames.getAliases().forEach((name, nick) -> {
                try {
                    out.write(name + ":" + nick);
                    out.write("\r\n");
                } catch(Exception e) {
                    // empty
                }
            });
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadNicks() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Nicknames.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String nick = curLine.split(":")[1];
                    Nicknames.addNickname(name, nick);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveFriends();
        }
    }
    public void saveEnemies() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Enemies.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Enemy e : Enemies.getEnemies()) {
                try {
                    out.write(e.getUsername());
                    out.write("\r\n");
                } catch(Exception ex) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadEnemies() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Enemies.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Enemies.addEnemy(line);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveEnemies();
        }
    }

    public void saveNewGui() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "OldGui.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Panel p : NewGUI.panels) {
                try {
                    out.write(p.title + ":" + p.x + ":" + p.y + ":" + p.extended);
                    out.write("\r\n");
                } catch (Exception e) {
                    // empty
                }
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadNewGui() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "OldGui.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String x = curLine.split(":")[1];
                    String y = curLine.split(":")[2];
                    String e = curLine.split(":")[3];
                    double x1 = Double.parseDouble(x);
                    double y1 = Double.parseDouble(y);
                    boolean ext = Boolean.parseBoolean(e);
                    Panel p = NewGUI.getPanelByName(name);
                    if (p != null) {
                        p.x = x1;
                        p.y = y1;
                        p.extended = ext;
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveNewGui();
        }
    }
    public void saveHUD() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "HUD.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Frame f : HUD.frames) {
                out.write(f.title + ":" + f.x + ":" + f.y);
                out.write("\r\n");
            }
            out.write(Xulu.hud.hudPanel.title + ":" + Xulu.hud.hudPanel.x + ":" + Xulu.hud.hudPanel.y);
            out.write("\r\n");
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadHUD() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "HUD.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String x = curLine.split(":")[1];
                    String y = curLine.split(":")[2];
                    double x1 = Double.parseDouble(x);
                    double y1 = Double.parseDouble(y);
                    if (name.equalsIgnoreCase(Xulu.hud.hudPanel.title)) {
                        Xulu.hud.hudPanel.x = x1;
                        Xulu.hud.hudPanel.y = y1;
                    }
                    Frame p = HUD.getframeByName(name);
                    if (p != null) {
                        p.x = x1;
                        p.y = y1;
                    }
                    Element e = ((Element)Xulu.MODULE_MANAGER.getModuleByName(name));
                    if (e != null) {
                        e.setX(x1);
                        e.setY(y1);
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveHUD();
        }
    }
    public void savePrefix() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Prefix.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.getPrefix());
            out.write("\r\n");
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadPrefix() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Prefix.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                Command.setPrefix(line);
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            savePrefix();
        }
    }
    public void saveWaypoints() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Waypoints.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Waypoints.Waypoint waypoint : Waypoints.WAYPOINTS) {
                out.write(waypoint.getName() + ":" + waypoint.getPos().x + ":" + waypoint.getPos().y + ":" + waypoint.getPos().z + ":" + waypoint.getDimension());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadWaypoints() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Waypoints.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String x = (curLine.split(":")[1]);
                    String y = (curLine.split(":")[2]);
                    String z = (curLine.split(":")[3]);
                    String dimension = (curLine.split(":")[4]);
                    int posX = Integer.parseInt(x);
                    int posY = Integer.parseInt(y);
                    int posZ = Integer.parseInt(z);
                    int dim = Integer.parseInt(dimension);
                    Waypoints.WAYPOINTS.add(new Waypoints.Waypoint(UUID.randomUUID(), name, new BlockPos(posX, posY, posZ), null, dim));
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveDrawn();
        }
    }
    public void saveDrawn() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Drawn.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Module module : Xulu.MODULE_MANAGER.getModules()) {
                out.write(module.getName() + ":" + module.isDrawn());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception file) {
            // empty catch block
        }
    }
    public void loadDrawn() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Drawn.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    boolean drawn = Boolean.parseBoolean(isOn);
                    Module m = Xulu.MODULE_MANAGER.getModuleByName(name);
                    m.setDrawn(drawn);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveDrawn();
        }
    }

    public void writeCrash(String alah) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy-HH_mm_ss");
            Date date = new Date();
            File file = new File(this.Eclient.getAbsolutePath(), "crashlog-".concat(format.format(date)).concat(".xen"));
            BufferedWriter outWrite = new BufferedWriter(new FileWriter(file));
            outWrite.write(alah);
            outWrite.close();
        }
        catch (Exception error) {
        }
    }

    public void loadHacks() {
        try {
            File file = new File(this.Eclient.getAbsolutePath(), "Modules.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                for (Module m : Xulu.MODULE_MANAGER.getModules()) {
                    try {
                        if (m.getName().equals(line)) {
                            m.initToggle(true);
                        }
                        if (m instanceof Element) {
                            ((Element) m).getFrame().pinned = true;
                        }
                    } catch(Exception e) {
                        // empty
                    }
                }
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            saveHacks();
        }
    }

    public void saveMacros() {
        //Slider
        try
        {
            File file = new File(Eclient.getAbsolutePath(), "Macros.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Macro m: Xulu.MACRO_MANAGER.getMacros())
            {
                out.write(m.getMacro() + ":" + m.getKey() + "\r\n");
            }
            out.close();
        }catch(Exception e) {}
    }
    public void loadMacros() {
        try
        {
            File file = new File(Eclient.getAbsolutePath(), "Macros.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    int key = Integer.valueOf(isOn);
                    if (!Xulu.MACRO_MANAGER.getMacros().contains(new Macro(name, key))) {
                        Xulu.MACRO_MANAGER.addMacro(name, key);
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            saveMacros();
        }
    }

    public String determineNumber(Object o) {
        if (o instanceof Integer) {
            return "INTEGER";
        }
        else if (o instanceof Float) {
            return "FLOAT";
        }
        else if (o instanceof Double) {
            return "DOUBLE";
        }
        else {
            return "INVALID";
        }
    }

    public void saveSettingsList()
    {
        //Slider
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Slider.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Value<?> i: Xulu.VALUE_MANAGER.getValues())
            {

                if(i.isNumber()) {
                    out.write(i.getName() + ":" + i.getValue().toString() +  ":" + i.getParentMod().getName() + "\r\n");
                }
            }
            out.close();
        }catch(Exception e) {}

        //Check
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Check.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Value i: Xulu.VALUE_MANAGER.getValues())
            {


                if(i.isToggle()) {
                    out.write(i.getName() + ":" + i.getValue().toString() +  ":" + i.getParentMod().getName() + "\r\n");
                }
            }
            out.close();
        }catch(Exception e) {}

        //Combo
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Combo.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Value i: Xulu.VALUE_MANAGER.getValues())
            {
                if(i.isMode()) {
                    if (((Value<String>)i).getValue().contains(":")) {
                        out.write(i.getName() + ";" + i.getValue().toString() +  ";" + i.getParentMod().getName() + "\r\n");
                    }
                    out.write(i.getName() + ":" + i.getValue().toString() +  ":" + i.getParentMod().getName() + "\r\n");
                }
                if(i.isEnum()) {
                    out.write(i.getName() + ":" + i.getValue().toString() +  ":" + i.getParentMod().getName() + "\r\n");
                }

            }
            out.close();
        }catch(Exception e) {}


        //Text Box
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "TextBox.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(Value i: Xulu.VALUE_MANAGER.getValues())
            {
                if(i.isText()) {
                    if (((Value<TextBox>)i).getValue().getText().contains(":")) {
                        out.write(i.getName() + ";" + ((Value<TextBox>)i).getValue().getText() +  ";" + i.getParentMod().getName() + "\r\n");
                    }
                    out.write(i.getName() + ":" + ((Value<TextBox>)i).getValue().getText() +  ":" + i.getParentMod().getName() + "\r\n");
                }

            }
            out.close();
        }catch(Exception e) {}

    }




    public void loadSettingsList() {

        //slider
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Slider.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    String m = curLine.split(":")[2];
                    Value mod = Xulu.VALUE_MANAGER.getValueByMod(Xulu.MODULE_MANAGER.getModuleByName(m), name);
                    Number type = 0;
                    if (mod.getValue() instanceof Double) {
                        type = NumberUtils.createDouble(isOn);
                    }
                    else if (mod.getValue() instanceof Integer) {
                        type = NumberUtils.createInteger(isOn);
                    }
                    else if (mod.getValue() instanceof Float) {
                        type = NumberUtils.createFloat(isOn);
                    }
                    else if (mod.getValue() instanceof Long) {
                        type = NumberUtils.createLong(isOn);
                    }
                    else if (mod.getValue() instanceof Short) {
                        type = NumberUtils.createShort(isOn);
                    }
                    mod.setValue(type);
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            //saveSettingsList();
        }
        //check
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Check.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                try {
                    String curLine = line.trim();
                    String name = curLine.split(":")[0];
                    String isOn = (curLine.split(":")[1]);
                    String m = curLine.split(":")[2];
                    Value mod = Xulu.VALUE_MANAGER.getValueByMod(Xulu.MODULE_MANAGER.getModuleByName(m), name);
                    mod.setValue(Boolean.parseBoolean(isOn));
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            //saveSettingsList();
        }

        //Combo
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "Combo.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                try {
                    String curLine = line.trim();
                    String name;
                    String isOn;
                    String m;
                    if (curLine.contains(";")) {
                        name = curLine.split(";")[0];
                        isOn = (curLine.split(";")[1]);
                        m = curLine.split(";")[2];
                    } else {
                        name = curLine.split(":")[0];
                        isOn = (curLine.split(":")[1]);
                        m = curLine.split(":")[2];
                    }
                    Value mod = Xulu.VALUE_MANAGER.getValueByMod(Xulu.MODULE_MANAGER.getModuleByName(m), name);
                    if (mod.isEnum()) {
                        mod.setEnumValue(isOn);
                    } else {
                        mod.setValue(isOn);
                    }
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            //saveSettingsList();
        }

        //Text Box
        try
        {
            File file = new File(EclientSettings.getAbsolutePath(), "TextBox.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null)
            {
                try {
                    String curLine = line.trim();
                    String name;
                    String isOn;
                    String m;
                    if (curLine.contains(";")) {
                        name = curLine.split(";")[0];
                        isOn = (curLine.split(";")[1]);
                        m = curLine.split(";")[2];
                    } else {
                        name = curLine.split(":")[0];
                        isOn = (curLine.split(":")[1]);
                        m = curLine.split(":")[2];
                    }
                    Value mod = Xulu.VALUE_MANAGER.getValueByMod(Xulu.MODULE_MANAGER.getModuleByName(m), name);
                    mod.setValue(new TextBox(isOn));
                } catch(Exception e) {
                    // empty
                }
            }
            br.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            //saveSettingsList();
        }

    }

}