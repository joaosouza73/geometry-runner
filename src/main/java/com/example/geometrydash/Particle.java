package com.example.geometrydash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

/** Partícula independente, com vida curta e desvanecimento gradual. */
public final class Particle {
    private double x;
    private double y;
    private final double velocityX;
    private double velocityY;
    private final double lifetime;
    private final double initialSize;
    private final Color color;
    private final ParticleStyle style;
    private double age;

    public Particle(double x, double y, double velocityX, double velocityY,
                    double lifetime, double size, Color color, ParticleStyle style) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.lifetime = lifetime;
        this.initialSize = size;
        this.color = color;
        this.style = style;
    }

    public boolean update(double deltaSeconds) {
        double dt = Math.max(0, Math.min(deltaSeconds, 0.05));
        age += dt;
        x += velocityX * dt;
        y += velocityY * dt;
        velocityY += 45.0 * dt;
        return age < lifetime;
    }

    public void draw(Graphics2D g) {
        double lifeRatio = Math.max(0, 1.0 - age / lifetime);
        int size = Math.max(1, (int) Math.ceil(initialSize * lifeRatio));
        int alpha = (int) (color.getAlpha() * lifeRatio);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        int drawX = (int) Math.round(x - size / 2.0);
        int drawY = (int) Math.round(y - size / 2.0);
        switch (style) {
            case EMBER -> g.fillOval(drawX, drawY, size, size);
            case DIAMOND -> {
                Polygon diamond = new Polygon(
                        new int[]{drawX + size / 2, drawX + size, drawX + size / 2, drawX},
                        new int[]{drawY, drawY + size / 2, drawY + size, drawY + size / 2}, 4);
                g.fillPolygon(diamond);
            }
            case SQUARE -> g.fillRect(drawX, drawY, size, size);
        }
    }
}
