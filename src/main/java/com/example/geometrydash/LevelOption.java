package com.example.geometrydash;

/** Catálogo de disponibilidade e metadados exibidos pelo seletor de fases. */
public final class LevelOption {
    private static final LevelOption[] OPTIONS = {
        new LevelOption(1, "FASE 1", "Neon Run · Disponível", true),
        new LevelOption(2, "FASE 2", "Gravity Steps · Disponível", true),
        new LevelOption(3, "FASE 3", "Em breve", false)
    };

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
        return OPTIONS.clone();
    }

    public static boolean isAvailable(int levelId) {
        for (LevelOption option : OPTIONS) {
            if (option.levelId == levelId) return option.available;
        }
        return false;
    }
}
