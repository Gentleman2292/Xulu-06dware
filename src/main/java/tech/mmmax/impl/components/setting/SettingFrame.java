package tech.mmmax.impl.components.setting;

import com.elementars.eclient.module.Module;
import tech.mmmax.api.component.custom.AbstractFrameComponent;

import java.util.ArrayList;

public class SettingFrame extends AbstractFrameComponent {

    public ArrayList<AbstractSettingComponent> settingComponents;

    public SettingFrame(int x, int y, Module module) {
        super(x, y, 120, 100);
        updateAnchor = true;
        draggable = true;
    }

 //   public int getSettings(Module module){

 //   }


}
