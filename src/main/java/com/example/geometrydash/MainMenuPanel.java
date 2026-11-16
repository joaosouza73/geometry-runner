package com.example.geometrydash;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Menu principal com catálogo de fases, skins e pré-visualização. */
public final class MainMenuPanel extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 450;
    private final Game game;
    private final Runnable onPlay;
    private final LevelOption[] levels = LevelOption.all();
    private int selectedLevel;

    public MainMenuPanel(Game game, Runnable onPlay) {
        this.game = game;
        this.onPlay = onPlay;
        setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "play");
        getActionMap().put("play", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { startIfAvailable(); }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });
    }

    private void handleClick(int x, int y) {
        // Linhas do catálogo: fases indisponíveis são mostradas, mas não selecionadas.
        for (int i = 0; i < levels.length; i++) {
            int top = 154 + i * 66;
            if (inside(x, y, 64, top, 310, 54) && levels[i].isAvailable()) {
                selectedLevel = i;
                repaint();
                return;
            }
        }
        PlayerSkin[] skins = PlayerSkin.values();
        for (int i = 0; i < skins.length; i++) {
            int left = 414 + i * 145;
            if (inside(x, y, left, 154, 132, 86)) {
                game.setSelectedSkin(skins[i]);
                repaint();
                return;
            }
        }
        if (inside(x, y, 340, 382, 220, 52)) startIfAvailable();
    }

    private boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x <= left + width && y >= top && y <= top + height;
    }

    private void startIfAvailable() {
        if (levels[selectedLevel].isAvailable()) {
            game.selectLevel(levels[selectedLevel].getLevelId());
            onPlay.run();
        }
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, new Color(23, 30, 65), 0, HEIGHT, new Color(8, 12, 30)));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        drawGrid(g);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.setColor(new Color(92, 241, 219));
        center(g, "GEOMETRY RUNNER", 58);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(176, 195, 229));
        center(g, "ESCOLHA SUA FASE E PERSONALIZE SEU CUBO", 82);

        section(g, "SELECIONE A FASE", 64, 122);
        for (int i = 0; i < levels.length; i++) drawLevel(g, i, 64, 154 + i * 66, 310, 54);

        section(g, "ESCOLHA SUA SKIN", 414, 122);
        PlayerSkin[] skins = PlayerSkin.values();
        for (int i = 0; i < skins.length; i++) drawSkinCard(g, skins[i], i, 414 + i * 145, 154);
        drawPreview(g);
        drawPlayButton(g);
        g.dispose();
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(83, 111, 173, 38));
        for (int x = 0; x < WIDTH; x += 40) g.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += 40) g.drawLine(0, y, WIDTH, y);
        g.setColor(new Color(64, 240, 218, 30));
        g.fillOval(745, -95, 230, 230);
        g.setColor(new Color(255, 75, 125, 24));
        g.fillOval(-100, 310, 210, 210);
    }

    private void section(Graphics2D g, String text, int x, int y) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(new Color(198, 211, 239));
        g.drawString(text, x, y);
    }

    private void drawLevel(Graphics2D g, int index, int x, int y, int w, int h) {
        LevelOption level = levels[index];
        boolean chosen = index == selectedLevel;
        g.setColor(level.isAvailable() ? (chosen ? new Color(26, 94, 112) : new Color(28, 39, 72)) : new Color(24, 28, 48));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setStroke(new BasicStroke(chosen ? 2.5f : 1.2f));
        g.setColor(chosen ? new Color(91, 235, 212) : new Color(81, 101, 146));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(level.isAvailable() ? Color.WHITE : new Color(124, 132, 157));
        g.drawString(level.getTitle(), x + 16, y + 23);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(level.isAvailable() ? new Color(129, 247, 224) : new Color(133, 139, 159));
        g.drawString(level.isAvailable() ? "◆  " + level.getDescription() : "BLOQUEADA  ·  " + level.getDescription(), x + 16, y + 42);
    }

    private void drawSkinCard(Graphics2D g, PlayerSkin skin, int index, int x, int y) {
        boolean selected = game.getSelectedSkin() == skin;
        g.setColor(selected ? new Color(32, 53, 80) : new Color(25, 32, 58));
        g.fillRoundRect(x, y, 132, 86, 12, 12);
        g.setStroke(new BasicStroke(selected ? 2.5f : 1f));
        g.setColor(selected ? skin.getGlow() : new Color(75, 91, 129));
        g.drawRoundRect(x, y, 132, 86, 12, 12);
        Player.drawPreview(g, x + 10, y + 24, 36, skin);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        g.drawString(skin.getDisplayName(), x + 52, y + 39);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(selected ? skin.getGlow() : new Color(155, 171, 207));
        g.drawString(selected ? "SELECIONADA" : "Selecionar", x + 52, y + 57);
    }

    private void drawPreview(Graphics2D g) {
        int x = 414, y = 257, w = 422, h = 105;
        PlayerSkin skin = game.getSelectedSkin();
        g.setColor(new Color(19, 27, 52, 230));
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setStroke(new BasicStroke(1.2f));
        g.setColor(new Color(skin.getGlow().getRed(), skin.getGlow().getGreen(), skin.getGlow().getBlue(), 150));
        g.drawRoundRect(x, y, w, h, 16, 16);
        Player.drawPreview(g, x + 30, y + 20, 64, skin);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString(skin.getDisplayName(), x + 125, y + 42);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(167, 184, 218));
        g.drawString("Pré-visualização do personagem", x + 125, y + 64);
        g.drawString("A skin altera somente o visual do cubo", x + 125, y + 82);
    }

    private void drawPlayButton(Graphics2D g) {
        g.setPaint(new GradientPaint(340, 382, new Color(74, 237, 207), 560, 434, new Color(50, 189, 228)));
        g.fillRoundRect(340, 382, 220, 52, 16, 16);
        g.setColor(new Color(224, 255, 249));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(340, 382, 220, 52, 16, 16);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(new Color(11, 31, 48));
        center(g, "▶  JOGAR", 416);
    }

    private void center(Graphics2D g, String text, int y) {
        g.drawString(text, (WIDTH - g.getFontMetrics().stringWidth(text)) / 2, y);
    }
}
