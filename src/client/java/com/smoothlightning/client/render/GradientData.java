package com.smoothlightning.client.render;

public class GradientData {
    private static final ThreadLocal<Float> VISUAL_HEIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Float> BASE_CAMERA_Y = new ThreadLocal<>();
    private static final ThreadLocal<Float> GRADIENT_RANGE = new ThreadLocal<>();
    private static final ThreadLocal<Float> BASE_DARKNESS = new ThreadLocal<>();
    private static final ThreadLocal<Float> SOLID_DARK_HEIGHT = new ThreadLocal<>();

    public static void setVisualHeight(float h) { VISUAL_HEIGHT.set(h); }
    public static Float getVisualHeight() { return VISUAL_HEIGHT.get(); }
    public static void clearVisualHeight() { VISUAL_HEIGHT.remove(); }

    public static void setBaseCameraY(float y) { BASE_CAMERA_Y.set(y); }
    public static Float getBaseCameraY() { return BASE_CAMERA_Y.get(); }
    public static void clearBaseCameraY() { BASE_CAMERA_Y.remove(); }

    public static void setGradientRange(float range) { GRADIENT_RANGE.set(range); }
    public static Float getGradientRange() { return GRADIENT_RANGE.get(); }
    public static void clearGradientRange() { GRADIENT_RANGE.remove(); }

    public static void setBaseDarkness(float darkness) { BASE_DARKNESS.set(darkness); }
    public static Float getBaseDarkness() { return BASE_DARKNESS.get(); }
    public static void clearBaseDarkness() { BASE_DARKNESS.remove(); }

    public static void setSolidDarkHeight(float height) { SOLID_DARK_HEIGHT.set(height); } // NUEVO
    public static Float getSolidDarkHeight() { return SOLID_DARK_HEIGHT.get(); } // NUEVO
    public static void clearSolidDarkHeight() { SOLID_DARK_HEIGHT.remove(); } // NUEVO
}