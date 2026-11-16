package com.example.geometrydash;

/** Verificações executáveis sem bibliotecas externas: java ...GameModelChecks. */
public final class GameModelChecks {
    private GameModelChecks() { }

    public static void main(String[] args) {
        checksPhaseCatalogAndManager();
        checksLevelDataAndImmutability();
        checksPlayerJumpAndGravity();
        checksSkinsAndGameFlow();
        checksObstacleCollisions();
        System.out.println("Todas as verificações do modelo passaram.");
    }

    private static void checksPhaseCatalogAndManager() {
        check(LevelOption.isAvailable(1), "Fase 1 deve estar disponível");
        check(LevelOption.isAvailable(2), "Fase 2 deve estar disponível");
        check(!LevelOption.isAvailable(3), "Fase 3 deve permanecer bloqueada");
        check(!LevelOption.isAvailable(99), "ID desconhecido não deve estar disponível");

        LevelManager manager = new LevelManager();
        manager.selectLevel(2);
        check(manager.getSelectedLevelId() == 2, "gerenciador deve selecionar Fase 2");
        expectThrows(IllegalArgumentException.class, () -> manager.selectLevel(3),
                "gerenciador deve rejeitar a Fase 3 bloqueada");
    }

    private static void checksLevelDataAndImmutability() {
        Level first = new Level(1);
        Level second = new Level(2);
        check(first.getLength() == 6500.0, "comprimento da Fase 1");
        check(second.getLength() == 11300.0, "comprimento da Fase 2");
        check(first.getObstacles().size() == 20, "quantidade de obstáculos da Fase 1");
        check(second.getPortals().size() == 2, "Fase 2 deve ter dois portais");
        expectThrows(UnsupportedOperationException.class,
                () -> first.getObstacles().add(new Block(1)),
                "lista de obstáculos não deve ser modificável externamente");
        first.update(first.getLength() / first.getSpeed() + 0.001);
        check(first.isComplete(), "fase deve concluir ao percorrer seu comprimento");
    }

    private static void checksPlayerJumpAndGravity() {
        Player player = new Player();
        player.reset(360);
        check(player.jump(), "jogador apoiado deve conseguir pular");
        check(!player.jump(), "jogador no ar não deve iniciar um segundo salto");
        double beforeJump = player.getY();
        player.update(0.05, 360, 68);
        check(player.getY() < beforeJump, "salto sob gravidade normal deve subir");

        player.reset(360);
        player.setGravityDirection(GravityDirection.UP);
        double beforeFlip = player.getY();
        player.update(0.05, 360, 68);
        check(player.getY() < beforeFlip, "gravidade invertida deve levar o jogador ao teto");
    }

    private static void checksSkinsAndGameFlow() {
        PlayerSkin[] skins = PlayerSkin.values();
        check(skins.length == 3, "jogo deve oferecer exatamente três skins");
        check(skins[0] == PlayerSkin.CLASSIC_BLUE
                        && skins[1] == PlayerSkin.FIRE_RED
                        && skins[2] == PlayerSkin.NEON_GREEN,
                "catálogo de skins deve manter as três opções esperadas");

        Game game = new Game();
        game.setSelectedSkin(PlayerSkin.FIRE_RED);
        game.selectLevel(2);
        game.start();
        check(game.getState() == GameState.PLAYING && game.getLevel().getId() == 2,
                "iniciar deve carregar a fase selecionada");
        game.returnToMenu();
        check(game.getState() == GameState.MENU && game.getSelectedSkin() == PlayerSkin.FIRE_RED,
                "voltar ao menu deve preservar a skin");
        game.retry();
        check(game.getState() == GameState.PLAYING && game.getLevel().getId() == 2
                        && game.getSelectedSkin() == PlayerSkin.FIRE_RED,
                "reiniciar deve manter fase e skin selecionadas");
    }

    private static void checksObstacleCollisions() {
        Player player = new Player();
        player.reset(360);
        check(CollisionDetector.collides(player, 150, new Block(0), 150, 360, 68),
                "jogador apoiado deve colidir com bloco no chão");
        check(CollisionDetector.collides(player, 150, new Spike(0), 150, 360, 68),
                "jogador apoiado deve colidir com espinho no chão");

        player.setGravityDirection(GravityDirection.UP);
        for (int i = 0; i < 100 && !player.isGrounded(); i++) player.update(0.01, 360, 68);
        check(player.isGrounded() && player.getY() == 68,
                "gravidade invertida deve apoiar o jogador no teto");
        check(CollisionDetector.collides(player, 150,
                        new Block(0, 40, 42, GravityDirection.UP), 150, 360, 68),
                "jogador no teto deve colidir com bloco invertido");
        check(CollisionDetector.collides(player, 150,
                        new Spike(0, GravityDirection.UP), 150, 360, 68),
                "jogador no teto deve colidir com espinho invertido");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("Falhou: " + description);
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable action, String description) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (type.isInstance(thrown)) return;
            throw new AssertionError(description + "; exceção recebida: " + thrown, thrown);
        }
        throw new AssertionError(description + "; nenhuma exceção foi lançada");
    }
}
