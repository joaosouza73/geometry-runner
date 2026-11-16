package com.example.geometrydash;

/** Metadados do seletor; fases futuras podem ser cadastradas sem alterar o menu. */
public final class LevelOption {
    private final int levelId;
    private final String title;
    private final String description;
    private final boolean available;

    public LevelOption(int levelId, String title, String description, boolean available) {
        this.levelId = levelId;
        this.title = title;
        this.description = description;
        this.available = available;
    }

    public String getTitle() { return title; }
    public int getLevelId() { return levelId; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return available; }

    public static LevelOption[] all() {
        return new LevelOption[]{
            new LevelOption(1, "FASE 1", "Neon Run · Disponível", true),
            new LevelOption(2, "FASE 2", "Gravity Steps · Disponível", true),
            new LevelOption(3, "FASE 3", "Em breve", false)
        };
    }
}
