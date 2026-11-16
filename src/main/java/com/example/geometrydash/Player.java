package com.example.geometrydash;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/** Jogador com física vertical; avanço é representado pela câmera do nível. */
public final class Player {
    private static final int SIZE = 28;
    private static final double GRAVITY = 1650.0;
    private static final double JUMP_SPEED = -590.0;
    private double y;
    private double velocityY;
    private boolean grounded = true;
    private GravityDirection gravityDirection = GravityDirection.DOWN;

    public void reset(int groundY) {
        y = groundY - SIZE;
        velocityY = 0;
        grounded = true;
        gravityDirection = GravityDirection.DOWN;
    }

    public void setGravityDirection(GravityDirection direction) {
        if (direction == gravityDirection) return;
        gravityDirection = direction;
        // Preserve position and velocity through the portal; gravity carries the player to the new surface.
        grounded = false;
        if (Math.abs(velocityY) < 1.0) velocityY = 0;
    }

    public boolean jump() {
        if (grounded) {
            velocityY = gravityDirection == GravityDirection.DOWN ? JUMP_SPEED : -JUMP_SPEED;
            grounded = false;
            return true;
        }
        return false;
    }

    public boolean update(double deltaSeconds, int groundY, int ceilingY) {
        if (grounded) return false;
        velocityY += GRAVITY * gravityDirection.getSign() * deltaSeconds;
        y += velocityY * deltaSeconds;
        boolean hitSurface = gravityDirection == GravityDirection.DOWN ? y + SIZE >= groundY : y <= ceilingY;
        if (hitSurface) {
            y = gravityDirection == GravityDirection.DOWN ? groundY - SIZE : ceilingY;
            velocityY = 0;
            grounded = true;
            return true;
        }
        return false;
    }

    public Rectangle getBounds(int screenX) { return new Rectangle(screenX + 2, (int) y + 2, SIZE - 4, SIZE - 4); }
    public boolean isGrounded() { return grounded; }
    public double getY() { return y; }
    public GravityDirection getGravityDirection() { return gravityDirection; }

    public void draw(Graphics2D g, int screenX, PlayerSkin skin, JumpAnimation animation) {
        Graphics2D sprite = (Graphics2D) g.create();
        double centerX = screenX + SIZE / 2.0;
        double centerY = y + SIZE / 2.0 + animation.getGroundBob();
        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(animation.getRotation());
        transform.scale(animation.getScaleX(), animation.getScaleY());
        transform.translate(-SIZE / 2.0, -SIZE / 2.0);
        sprite.transform(transform);
        sprite.setColor(skin.getBody());
        sprite.fillRoundRect(0, 0, SIZE, SIZE, 5, 5);
        sprite.setColor(skin.getDetail());
        sprite.drawRoundRect(4, 4, SIZE - 8, SIZE - 8, 3, 3);
        sprite.fillRect(8, 8, 4, 4);
        sprite.fillRect(17, 8, 4, 4);
        sprite.dispose();
    }

    /** Desenha uma miniatura escalável da skin para o menu. */
    public static void drawPreview(Graphics2D g, int x, int y, int size, PlayerSkin skin) {
        g.setColor(new Color(skin.getGlow().getRed(), skin.getGlow().getGreen(), skin.getGlow().getBlue(), 55));
        g.fillOval(x - size / 3, y - size / 3, size * 5 / 3, size * 5 / 3);
        g.setColor(skin.getBody());
        g.fillRoundRect(x, y, size, size, size / 5, size / 5);
        g.setColor(skin.getDetail());
        g.setStroke(new java.awt.BasicStroke(Math.max(2, size / 14f)));
        g.drawRoundRect(x + size / 7, y + size / 7, size * 5 / 7, size * 5 / 7, size / 6, size / 6);
        int eye = Math.max(3, size / 9);
        g.fillRect(x + size / 4, y + size / 3, eye, eye);
        g.fillRect(x + size * 5 / 8, y + size / 3, eye, eye);
    }
}
