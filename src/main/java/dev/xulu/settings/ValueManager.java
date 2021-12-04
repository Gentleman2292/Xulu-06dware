package dev.xulu.settings;

import java.util.ArrayList;
import java.util.List;

//Deine Imports
import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.ArrayHelper;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ValueList;

/**
 * @author Elementars
 */
public class ValueManager {

    private ArrayList<Value<?>> values;

    public ValueManager(){
        this.values = new ArrayList<>();
    }

    public <T> Value<T> register(Value<T> in){
        this.values.add(in);
        return in;
    }

    public ArrayList<Value<?>> getValues(){
        return this.values;
    }

    public ArrayList<Value<?>> getSettingsByMod(Module mod){
        ArrayList<Value<?>> out = new ArrayList<>();
        for(Value<?> s : getValues()){
            if(s.getParentMod().equals(mod)){
                out.add(s);
            }
        }
        if(out.isEmpty()){
            return null;
        }
        return out;
    }

    public ArrayList<Value<?>> getValuesByMod(Module mod){
        ValueList out = new ValueList();
        for(Value<?> s : getValues()){
            if(s.getParentMod().equals(mod)){
                out.add(s);
            }
        }
        if(out.isEmpty()){
            return null;
        }
        return out;
    }


    public <T> Value<T> getValueByMod(Module mod, String name){
        for(Value s : getValues()){
            if(s.getParentMod().equals(mod) && s.getName().equalsIgnoreCase(name)){
                return s;
            }
        }
        return null;
    }

    public <T> Value<T> getValueByName(String name){
        for(Value<?> set : getValues()){
            if(set.getName().equalsIgnoreCase(name)){
                return (Value<T>) set;
            }
        }
        System.err.println("["+ Xulu.name + "] Error Setting NOT found: '" + name +"'!");
        return null;
    }

    public <T> Value<T> getValueT(String name, Class<? extends Module> modClazz){
        for(Value<?> set : getValues()){
            if(set.getName().equalsIgnoreCase(name) && set.getParentMod().equals(Xulu.MODULE_MANAGER.getModule(modClazz))){
                return (Value<T>) set;
            }
        }
        System.err.println("["+ Xulu.name + "] Error Setting NOT found: '" + name +"'!");
        return null;
    }

}