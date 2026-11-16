package com.example.geometrydash;

/** Seleciona e carrega mapas; a simulação da partida usa sempre o nível ativo. */
public final class LevelManager {
    private Level activeLevel = new Level(1);
    private int selectedLevelId = 1;

    public void selectLevel(int levelId) {
        if (!LevelOption.isAvailable(levelId)) throw new IllegalArgumentException("Fase indisponível: " + levelId);
        selectedLevelId = levelId;
    }

    public Level loadSelectedLevel() {
        activeLevel = new Level(selectedLevelId);
        return activeLevel;
    }

    public Level getActiveLevel() { return activeLevel; }
    public int getSelectedLevelId() { return selectedLevelId; }
}
