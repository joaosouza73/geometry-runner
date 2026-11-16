package com.example.geometrydash;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/** Limita partículas ativas e centraliza trilha, impactos, explosão e vitória. */
public final class ParticleSystem {
    private static final int MAX_PARTICLES = 120;
    private static final double TRAIL_INTERVAL = 0.035;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random(417);
    private double trailAccumulator;

    public void reset() {
        particles.clear();
        trailAccumulator = 0;
    }

    public void update(double deltaSeconds) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().update(deltaSeconds)) iterator.remove();
        }
    }

    public void emitTrail(double deltaSeconds, double x, double y, PlayerSkin skin) {
        trailAccumulator += Math.max(0, Math.min(deltaSeconds, 0.05));
        while (trailAccumulator >= TRAIL_INTERVAL) {
            trailAccumulator -= TRAIL_INTERVAL;
            add(new Particle(x - 4, y + random.nextDouble() * 18 - 9,
                    -28 - random.nextDouble() * 34, -24 - random.nextDouble() * 28,
                    0.34 + random.nextDouble() * 0.18, 3 + random.nextDouble() * 4,
                    skin.getParticleColor(random.nextInt(3)), skin.getParticleStyle()));
        }
    }

    public void landingBurst(double x, double y, PlayerSkin skin, GravityDirection direction) {
        burst(x, y, skin, 12, 60, direction == GravityDirection.DOWN ? 100 : -100);
    }
    public void collisionBurst(double x, double y, PlayerSkin skin) { burst(x, y, skin, 34, 190, 190); }
    public void victoryBurst(double x, double y, PlayerSkin skin) { burst(x, y, skin, 58, 220, 240); }

    private void burst(double x, double y, PlayerSkin skin, int count, double speed, double upwardBias) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double velocity = speed * (0.35 + random.nextDouble() * 0.65);
            double vx = Math.cos(angle) * velocity;
            double vy = Math.sin(angle) * velocity - upwardBias * 0.35;
            add(new Particle(x, y, vx, vy, 0.45 + random.nextDouble() * 0.35,
                    3 + random.nextDouble() * 5, skin.getParticleColor(random.nextInt(3)), skin.getParticleStyle()));
        }
    }

    private void add(Particle particle) {
        if (particles.size() < MAX_PARTICLES) particles.add(particle);
    }

    public void draw(Graphics2D g) {
        for (Particle particle : particles) particle.draw(g);
    }

    public int getActiveCount() { return particles.size(); }
}
