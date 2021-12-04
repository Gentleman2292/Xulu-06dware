package com.elementars.eclient.macro;

import java.util.ArrayList;

public class MacroManager {
    private ArrayList<Macro> macros = new ArrayList<>();
    public void addMacro(String msg, int key){
        macros.add(new Macro(msg, key));
    }
    public void delMacro(int key){
        macros.stream().filter(macro -> macro.getKey() == key).forEach(macro -> macros.remove(macro));
    }
    public ArrayList<Macro> getMacros() {
        return macros;
    }
    public void runMacros(int key) {
        macros.stream().filter(macro -> macro.getKey() == key).forEach(Macro::runMacro);
    }
}
