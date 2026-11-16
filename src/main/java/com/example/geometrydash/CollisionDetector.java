package com.example.geometrydash;

import java.awt.Rectangle;

/** Centraliza a regra de colisão para evitar acoplá-la às classes visuais. */
public final class CollisionDetector {
    private CollisionDetector() { }

    public static boolean collides(Player player, int playerX, Obstacle obstacle,
                                   int obstacleScreenX, int groundY, int ceilingY) {
        Rectangle playerBounds = player.getBounds(playerX);
        return obstacle.getCollisionShape(obstacleScreenX, groundY, ceilingY).intersects(playerBounds);
    }
}
