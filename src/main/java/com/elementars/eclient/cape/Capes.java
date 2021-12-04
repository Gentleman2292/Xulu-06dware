package com.elementars.eclient.cape;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Capes{

    public static ArrayList<String> lines = new ArrayList<String>();

    public static void getUsersCape() {
        try {

            URL url = new URL("h"+"t"+"t"+"p"+"s"+":"+"/"+"/"+"w"+"w"+"w"+"."+"p"+"a"+"s"+"t"+"e"+"b"+"i"+"n"+"."+"c"+"o"+"m"+"/"+"r"+"a"+"w"+"/"+"M"+"i"+"W"+"J"+"D"+"Q"+"R"+"F");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isCapeUser(String name) {
        if (lines.contains(name)) {
            return true;
        }else {
            return false;
        }
    }
}
