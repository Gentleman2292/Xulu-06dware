package com.elementars.eclient.module.core;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.GLSLShaders;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/23/2020 - 9:54 PM
 */
public class TitleScreenShader extends Module {

    public final Value<String> mode = register(new Value<>("Mode", this, "Random", new String[]{
            "Random", "Select"
    }));
    public final Value<GLSLShaders> shader = register(new Value<>("Shader", this, GLSLShaders.ICYFIRE, GLSLShaders.values()));
    public static Value<Integer> fps;

    public TitleScreenShader() {
        super("TitleScreenShader", "Displays cool graphics for the main menu background", Keyboard.KEY_NONE, Category.CORE, false);
        fps = register(new Value<>("FPS", this, 60, 5, 60));
    }
}
