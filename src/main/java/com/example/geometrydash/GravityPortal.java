package com.example.geometrydash;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/** Portal de troca de gravidade com acionamento único a cada tentativa. */
public final class GravityPortal {
    private final double x;
    private final GravityDirection targetDirection;
    private boolean triggered;

    public GravityPortal(double x, GravityDirection targetDirection) {
        this.x = x;
        this.targetDirection = targetDirection;
    }

    public double getX() { return x; }
    public GravityDirection getTargetDirection() { return targetDirection; }
    public boolean isTriggered() { return triggered; }
    public void reset() { triggered = false; }
    public void trigger() { triggered = true; }
    public boolean hasReachedPlayer(double cameraX, int playerX) {
        return !triggered && cameraX >= x - playerX;
    }

    public void draw(Graphics2D g, int screenX) {
        Color color = targetDirection == GravityDirection.UP
                ? new Color(92, 119, 255) : new Color(255, 154, 75);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
        g.fillOval(screenX - 25, 124, 50, 190);
        g.setColor(color);
        g.setStroke(new BasicStroke(4));
        g.drawOval(screenX - 17, 135, 34, 168);
        g.setStroke(new BasicStroke(2));
        g.drawOval(screenX - 10, 143, 20, 152);
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        g.drawString(targetDirection == GravityDirection.UP ? "INVERTE" : "NORMAL", screenX - 22, 119);
    }
}
