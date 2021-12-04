package com.elementars.eclient.util;

/**
 * @author Elementars
 * @since 6/23/2020 - 8:52 PM
 */
public enum GLSLShaders {
    AWESOME("/shaders/awesome.fsh"),
    BLUEGRID("/shaders/bluegrid.fsh"),
    BLUENEBULA("/shaders/bluenebula.fsh"),
    BLUEVORTEX("/shaders/bluevortex.fsh"),
    CAVE("/shaders/cave.fsh"),
    CITY("/shaders/city.fsh"),
    CLOUDS("/shaders/clouds.fsh"),
    DJ("/shaders/dj.fsh"),
    ICYFIRE("/shaders/icyfire.fsh"),
    JUPITER("/shaders/jupiter.fsh"),
    MATRIX("/shaders/matrix.fsh"),
    MATRIXRED("/shaders/matrixred.fsh"),
    MINECRAFT("/shaders/minecraft.fsh"),
    PINWHEEL("/shaders/pinwheel.fsh"),
    PURPLEGRID("/shaders/purplegrid.fsh"),
    PURPLEMIST("/shaders/purplemist.fsh"),
    REDGLOW("/shaders/redglow.fsh"),
    SKY("/shaders/sky.fsh"),
    SNAKE("/shaders/snake.fsh"),
    SPACE("/shaders/space.fsh"),
    SPACE2("/shaders/space2.fsh"),
    STORM("/shaders/storm.fsh"),
    SUNSETWAVES("/shaders/sunsetwaves.fsh"),
    TRIANGLE("/shaders/triangle.fsh"),
    WAIFU("/shaders/waifu.fsh"),
    XULU("/shaders/xulu.fsh");

    String s;

    GLSLShaders(String s) {
        this.s = s;
    }

    public String get() {
        return this.s;
    }
}
