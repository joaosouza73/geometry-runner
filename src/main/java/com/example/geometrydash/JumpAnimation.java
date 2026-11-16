package com.example.geometrydash;

/** Transformações apenas visuais do cubo; não participa da física nem da hitbox. */
public final class JumpAnimation {
    private static final double PREPARE_SECONDS = 0.075;
    private static final double LAND_SECONDS = 0.18;
    private double rotation;
    private double preparationElapsed;
    private double preparationRemaining;
    private double landingElapsed = LAND_SECONDS;
    private double landingStartRotation;
    private double landingTargetRotation;
    private double groundTime;
    private boolean airborne;
    private double gravityFlipOffset;
    private double gravityFlipStart;
    private double gravityFlipTarget;
    private double gravityFlipElapsed = 0.24;
    private static final double GRAVITY_FLIP_SECONDS = 0.24;

    public void reset() {
        rotation = 0;
        preparationElapsed = 0;
        preparationRemaining = 0;
        landingElapsed = LAND_SECONDS;
        groundTime = 0;
        airborne = false;
        gravityFlipOffset = 0;
        gravityFlipStart = 0;
        gravityFlipTarget = 0;
        gravityFlipElapsed = GRAVITY_FLIP_SECONDS;
    }

    public void onJumpStarted() {
        airborne = true;
        preparationElapsed = 0;
        preparationRemaining = PREPARE_SECONDS;
        landingElapsed = LAND_SECONDS;
    }

    public void onLanding() {
        airborne = false;
        preparationRemaining = 0;
        landingElapsed = 0;
        landingStartRotation = rotation;
        double quarterTurn = Math.PI / 2.0;
        landingTargetRotation = Math.rint(rotation / quarterTurn) * quarterTurn;
    }

    public void onGravityFlip(GravityDirection direction) {
        gravityFlipStart = gravityFlipOffset;
        gravityFlipTarget = direction == GravityDirection.UP ? Math.PI : 0;
        gravityFlipElapsed = 0;
    }

    public void update(double deltaSeconds) {
        double dt = Math.max(0, Math.min(deltaSeconds, 0.05));
        groundTime += dt;
        if (gravityFlipElapsed < GRAVITY_FLIP_SECONDS) {
            gravityFlipElapsed = Math.min(GRAVITY_FLIP_SECONDS, gravityFlipElapsed + dt);
            double progress = gravityFlipElapsed / GRAVITY_FLIP_SECONDS;
            double eased = progress * progress * (3 - 2 * progress);
            gravityFlipOffset = gravityFlipStart + (gravityFlipTarget - gravityFlipStart) * eased;
        }
        if (airborne) {
            preparationElapsed += dt;
            double prepStep = Math.min(preparationRemaining, dt);
            preparationRemaining -= prepStep;
            double rotateStep = dt - prepStep;
            // Um giro completo ao longo do arco do salto; cada quadrante é de 90°.
            rotation += rotateStep * (Math.PI * 2.0 / 0.715);
        } else if (landingElapsed < LAND_SECONDS) {
            landingElapsed = Math.min(LAND_SECONDS, landingElapsed + dt);
            double t = landingElapsed / LAND_SECONDS;
            double eased = 1.0 - (1.0 - t) * (1.0 - t) * (1.0 - t);
            rotation = landingStartRotation + (landingTargetRotation - landingStartRotation) * eased;
        }
    }

    public double getRotation() { return rotation + gravityFlipOffset; }

    public double getScaleX() {
        if (airborne && preparationElapsed < PREPARE_SECONDS + 0.08) return 1.0 + 0.18 * preparationPulse();
        if (!airborne && landingElapsed < LAND_SECONDS) return 1.0 + 0.20 * landingPulse();
        return 1.0;
    }

    public double getScaleY() {
        if (airborne && preparationElapsed < PREPARE_SECONDS + 0.08) return 1.0 - 0.25 * preparationPulse();
        if (!airborne && landingElapsed < LAND_SECONDS) return 1.0 - 0.28 * landingPulse();
        return 1.0 + Math.sin(groundTime * 10.0) * 0.025;
    }

    public double getGroundBob() {
        return airborne ? 0 : Math.sin(groundTime * 10.0) * 1.2;
    }

    private double preparationPulse() {
        if (preparationElapsed <= PREPARE_SECONDS) return preparationElapsed / PREPARE_SECONDS;
        double release = Math.min(1.0, (preparationElapsed - PREPARE_SECONDS) / 0.08);
        return 1.0 - release * release * (3.0 - 2.0 * release);
    }

    private double landingPulse() {
        double progress = landingElapsed / LAND_SECONDS;
        return (1.0 - progress) * (1.0 - progress);
    }
}
