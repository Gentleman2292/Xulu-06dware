package com.elementars.eclient.util;

import com.elementars.eclient.util.PlayerIdentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataManager {
    private final Lock threadLock = new ReentrantLock();
    private final Lock waypointLock = new ReentrantLock();
    private final static Lock identityLock = new ReentrantLock();

    public static LinkedHashMap<String, PlayerIdentity> identityCacheMap = new LinkedHashMap<>();

    public synchronized static void savePlayerIdentity(PlayerIdentity id, boolean delete) throws IOException {
        identityLock.lock();
        try {
            File dir = new File("playeridentitycache");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File f = new File("playeridentitycache/" + id.getStringUuid() + ".mcid");
            if (f.exists() || delete) {
                f.delete();
                if (delete) return;
            }
            FileOutputStream fstream = new FileOutputStream(f);
            ObjectOutputStream stream = new ObjectOutputStream(fstream);
            stream.writeObject(id);
            stream.close();
            fstream.close();
        } finally {
            identityLock.unlock();
        }
    }

    public static PlayerIdentity getPlayerIdentity(String UUID) {
        if (identityCacheMap.containsKey(UUID)) {
            return identityCacheMap.get(UUID);
        }
        return new PlayerIdentity(UUID);
    }



    public synchronized void loadPlayerIdentities() throws IOException {
        identityLock.lock();
        try {
            File f = new File("playeridentitycache");
            if (!f.exists()) {
                return; // nothing to load :p
            }
            if (!f.isDirectory()) {
                f.delete();
                return; // nothing to load :p
            }
            List<File> files = Arrays.asList(f.listFiles());
            files.stream().filter(file -> file.getName().endsWith(".mcid")).forEach(wyptFile -> {
                try {
                    FileInputStream inputStream = new FileInputStream(wyptFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object wayptObj = objectInputStream.readObject();
                    if (wayptObj instanceof PlayerIdentity) {
                        identityCacheMap.put(((PlayerIdentity) wayptObj).getStringUuid(), (PlayerIdentity) wayptObj);
                        objectInputStream.close();
                        inputStream.close();
                        return;
                    }
                    objectInputStream.close();
                    inputStream.close();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace(); //dont rly care
                    return;
                }
            });
        } finally {
            identityLock.unlock();
        }
    }



}