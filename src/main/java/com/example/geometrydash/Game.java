package com.example.geometrydash;

/** Coordena estado, jogador, fase e as regras de atualização da partida. */
public final class Game {
    private static final int PLAYER_X = 150;
    private static final int GROUND_Y = 360;
    private static final int CEILING_Y = 68;
    private final Player player = new Player();
    private final LevelManager levelManager = new LevelManager();
    private final JumpAnimation jumpAnimation = new JumpAnimation();
    private final ParticleSystem particleSystem = new ParticleSystem();
    private GameState state = GameState.MENU;
    private PlayerSkin selectedSkin = PlayerSkin.CLASSIC_BLUE;

    public Game() { player.reset(GROUND_Y); }
    public GameState getState() { return state; }
    public Player getPlayer() { return player; }
    public Level getLevel() { return levelManager.getActiveLevel(); }
    public LevelManager getLevelManager() { return levelManager; }
    public JumpAnimation getJumpAnimation() { return jumpAnimation; }
    public ParticleSystem getParticleSystem() { return particleSystem; }
    public int getPlayerX() { return PLAYER_X; }
    public int getGroundY() { return GROUND_Y; }
    public int getCeilingY() { return CEILING_Y; }
    public int getSelectedLevelId() { return levelManager.getSelectedLevelId(); }
    public GravityDirection getGravityDirection() { return player.getGravityDirection(); }
    public PlayerSkin getSelectedSkin() { return selectedSkin; }
    public void setSelectedSkin(PlayerSkin skin) { selectedSkin = skin; }

    public void start() {
        levelManager.loadSelectedLevel();
        player.reset(GROUND_Y);
        jumpAnimation.reset();
        particleSystem.reset();
        state = GameState.PLAYING;
    }

    public void returnToMenu() { state = GameState.MENU; }

    public void selectLevel(int levelId) { levelManager.selectLevel(levelId); }

    public void retry() { start(); }

    public void jump() {
        if (state == GameState.PLAYING && player.jump()) jumpAnimation.onJumpStarted();
    }

    public void update(double deltaSeconds) {
        particleSystem.update(deltaSeconds);
        jumpAnimation.update(deltaSeconds);
        if (state != GameState.PLAYING) return;
        boolean landed = player.update(deltaSeconds, GROUND_Y, CEILING_Y);
        if (landed) {
            jumpAnimation.onLanding();
            int surfaceY = player.getGravityDirection() == GravityDirection.DOWN ? GROUND_Y : CEILING_Y;
            particleSystem.landingBurst(PLAYER_X + 14, surfaceY, selectedSkin, player.getGravityDirection());
        }
        Level level = getLevel();
        level.update(deltaSeconds);
        particleSystem.emitTrail(deltaSeconds, PLAYER_X - 3, player.getY() + 14, selectedSkin);
        for (GravityPortal portal : level.getPortals()) {
            if (portal.hasReachedPlayer(level.getCameraX(), PLAYER_X + 14)) {
                portal.trigger();
                player.setGravityDirection(portal.getTargetDirection());
                jumpAnimation.onGravityFlip(portal.getTargetDirection());
            }
        }
        for (Obstacle obstacle : level.getObstacles()) {
            int obstacleX = (int) Math.round(obstacle.getX() - level.getCameraX());
            if (obstacleX + obstacle.getWidth() >= PLAYER_X - 2 && obstacleX <= PLAYER_X + 30
                    && CollisionDetector.collides(player, PLAYER_X, obstacle, obstacleX, GROUND_Y, CEILING_Y)) {
                particleSystem.collisionBurst(PLAYER_X + 14, player.getY() + 14, selectedSkin);
                state = GameState.GAME_OVER;
                return;
            }
        }
        if (level.isComplete()) {
            state = GameState.VICTORY;
            particleSystem.victoryBurst(PLAYER_X + 14, player.getY() + 14, selectedSkin);
        }
    }

    public double getProgress() { return Math.min(1.0, getLevel().getCameraX() / getLevel().getLength()); }
}
