package com.elementars.eclient.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.LinkedHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerIdentity implements Serializable {
    private String displayName;
    private String stringUuid;
    private Long lastUpdated;

    public PlayerIdentity(String stringUuid) {
        String formattedUuid = stringUuid.replace("-", "");
        this.stringUuid = stringUuid;
        this.displayName = "Loading...";
        new Thread(() -> {
            this.displayName = getName(formattedUuid);
            DataManager.identityCacheMap.put(this.getStringUuid(), this);
            try {
                DataManager.savePlayerIdentity(this, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        this.lastUpdated = System.currentTimeMillis();
    }

    private static String getName(String UUID) {
        LinkedHashMap<String, Long> nameHistories = new LinkedHashMap<>();
        try {
            URL e = new URL("https://api.mojang.com/user/profiles/" + UUID.replace("-", "") + "/names");
            URLConnection connection = e.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonb.append(line + "\n");
            }
            String formattedjson = jsonb.toString();
            reader.close();

            JsonArray array = new JsonParser().parse(formattedjson).getAsJsonArray();
            JsonObject obj = array.get(array.size() - 1).getAsJsonObject();
            String nameform = obj.get("name").getAsString();
            try {
                obj.get("changedToAt");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(obj.get("changedToAt").getAsLong());
                long changedAt = obj.get("changedToAt").getAsLong();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                //nameform = nameform + " @ " + getDateFormat(mMonth + 1, mDay, mYear);
                nameHistories.put(nameform, changedAt);
                return nameform;
            } catch (Exception ee) {
                return nameform;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("fuck");
        }
        return UUID;
    }

    private static String getDateFormat(int mm, int dd, int yyyy) {
        return mm + "/" + dd + "/" + yyyy;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getStringUuid() {
        return this.stringUuid;
    }

    /**
     * run this on a seperate thread pls so u dont kill the game
     */
    public void updateDisplayName() {
        new Thread(() -> {
            PlayerIdentity identity = new PlayerIdentity(this.stringUuid);
            this.displayName = identity.getDisplayName();
            this.lastUpdated = System.currentTimeMillis();
            identity = null;
        }).start();
    }

    public boolean shouldUpdate() {
        return System.currentTimeMillis() - this.lastUpdated >= 6.048e+8 /* one week */;
    }
}