package com.example.geometrydash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

/** Bloco sólido, mais alto que um espinho. */
public final class Block extends Obstacle {
    public Block(double x) { super(x, 40, 42); }
    public Block(double x, int width, int height, GravityDirection direction) {
        super(x, width, height, direction);
    }

    @Override public void draw(Graphics2D g, int screenX, int groundY, int ceilingY) {
        int y = getSurfaceDirection() == GravityDirection.DOWN ? groundY - getHeight() : ceilingY;
        g.setColor(new Color(255, 170, 64));
        g.fillRoundRect(screenX, y, getWidth(), getHeight(), 5, 5);
        g.setColor(new Color(255, 224, 160));
        g.drawRoundRect(screenX + 4, y + 4, getWidth() - 8, getHeight() - 8, 4, 4);
    }

    @Override public Shape getCollisionShape(int screenX, int groundY, int ceilingY) {
        int y = getSurfaceDirection() == GravityDirection.DOWN ? groundY - getHeight() : ceilingY;
        return new Rectangle(screenX + 2, y + 2,
                getWidth() - 4, getHeight() - 2);
    }
}
