package com.example.geometrydash;

import java.awt.Color;

/** Aparências selecionáveis; nenhum atributo de física é definido por uma skin. */
public enum PlayerSkin {
    CLASSIC_BLUE("Classic Blue", new Color(67, 153, 255), Color.WHITE, new Color(117, 204, 255),
            new Color(66, 159, 255), new Color(151, 220, 255), new Color(91, 112, 255), ParticleStyle.SQUARE),
    FIRE_RED("Fire Red", new Color(244, 67, 74), new Color(255, 204, 67), new Color(255, 137, 48),
            new Color(255, 71, 48), new Color(255, 173, 50), new Color(255, 226, 87), ParticleStyle.EMBER),
    NEON_GREEN("Neon Green", new Color(99, 255, 91), new Color(23, 92, 48), new Color(161, 255, 104),
            new Color(58, 255, 102), new Color(146, 255, 74), new Color(31, 195, 126), ParticleStyle.DIAMOND);

    private final String displayName;
    private final Color body;
    private final Color detail;
    private final Color glow;
    private final Color[] particleColors;
    private final ParticleStyle particleStyle;

    PlayerSkin(String displayName, Color body, Color detail, Color glow,
               Color particleOne, Color particleTwo, Color particleThree, ParticleStyle particleStyle) {
        this.displayName = displayName;
        this.body = body;
        this.detail = detail;
        this.glow = glow;
        this.particleColors = new Color[]{particleOne, particleTwo, particleThree};
        this.particleStyle = particleStyle;
    }

    public String getDisplayName() { return displayName; }
    public Color getBody() { return body; }
    public Color getDetail() { return detail; }
    public Color getGlow() { return glow; }
    public Color getParticleColor(int index) { return particleColors[Math.floorMod(index, particleColors.length)]; }
    public ParticleStyle getParticleStyle() { return particleStyle; }
}
