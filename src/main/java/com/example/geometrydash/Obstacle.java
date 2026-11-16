package com.example.geometrydash;

import java.awt.Graphics2D;
import java.awt.Shape;

/** Abstração comum: subclasses desenham e definem colisão de cada obstáculo. */
public abstract class Obstacle {
    private final double x;
    private final int width;
    private final int height;
    private final GravityDirection surfaceDirection;

    protected Obstacle(double x, int width, int height) {
        this(x, width, height, GravityDirection.DOWN);
    }

    protected Obstacle(double x, int width, int height, GravityDirection surfaceDirection) {
        this.x = x;
        this.width = width;
        this.height = height;
        this.surfaceDirection = surfaceDirection;
    }

    public final double getX() { return x; }
    public final int getWidth() { return width; }
    public final int getHeight() { return height; }
    public final GravityDirection getSurfaceDirection() { return surfaceDirection; }
    public abstract void draw(Graphics2D g, int screenX, int groundY, int ceilingY);
    public abstract Shape getCollisionShape(int screenX, int groundY, int ceilingY);
}
