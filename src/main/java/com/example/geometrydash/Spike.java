package com.example.geometrydash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

/** Espinho triangular. */
public final class Spike extends Obstacle {
    public Spike(double x) { this(x, GravityDirection.DOWN); }
    public Spike(double x, GravityDirection direction) { super(x, 34, 38, direction); }

    @Override public void draw(Graphics2D g, int screenX, int groundY, int ceilingY) {
        Polygon triangle = triangle(screenX, surfaceY(groundY, ceilingY));
        g.setColor(new Color(255, 86, 112));
        g.fillPolygon(triangle);
        g.setColor(new Color(255, 190, 198));
        int tipY = surfaceY(groundY, ceilingY) + (getSurfaceDirection() == GravityDirection.DOWN ? -37 : 37);
        int detailY = surfaceY(groundY, ceilingY) + (getSurfaceDirection() == GravityDirection.DOWN ? -17 : 17);
        g.drawLine(screenX + 17, tipY, screenX + 8, detailY);
    }

    @Override public Shape getCollisionShape(int screenX, int groundY, int ceilingY) {
        return triangle(screenX + 4, surfaceY(groundY, ceilingY));
    }

    private int surfaceY(int groundY, int ceilingY) {
        return getSurfaceDirection() == GravityDirection.DOWN ? groundY : ceilingY;
    }

    private Polygon triangle(int x, int surfaceY) {
        int tipOffset = getSurfaceDirection() == GravityDirection.DOWN ? -38 : 38;
        return new Polygon(new int[]{x, x + 17, x + 34},
                new int[]{surfaceY, surfaceY + tipOffset, surfaceY}, 3);
    }
}
