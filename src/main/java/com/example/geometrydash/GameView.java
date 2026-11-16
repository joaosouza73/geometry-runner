package com.example.geometrydash;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/** Desenha e recebe controles da partida, sem gerenciar navegação do menu. */
public final class GameView extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 450;
    private static final int PLAYER_X = 150;
    private static final Rectangle2D RETRY_BUTTON = new Rectangle2D.Double(238, 265, 190, 48);
    private static final Rectangle2D MENU_BUTTON = new Rectangle2D.Double(472, 265, 190, 48);
    private final Game game;
    private final Runnable onReturnToMenu;
    private final Timer timer;
    private long lastUpdateNanos;

    public GameView(Game game, Runnable onReturnToMenu) {
        this.game = game;
        this.onReturnToMenu = onReturnToMenu;
        setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setDoubleBuffered(true);
        bindKeys();
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });
        timer = new Timer(16, this::tick);
        timer.start();
    }

    private void bindKeys() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "jumpOrRetry");
        getActionMap().put("jumpOrRetry", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (isFinished()) game.retry();
                else game.jump();
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "retry");
        getActionMap().put("retry", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { if (isFinished()) game.retry(); }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "menu");
        getActionMap().put("menu", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onReturnToMenu.run(); }
        });
    }

    private boolean isFinished() {
        return game.getState() == GameState.GAME_OVER || game.getState() == GameState.VICTORY;
    }

    private void handleClick(int x, int y) {
        if (isFinished()) {
            if (RETRY_BUTTON.contains(x, y)) game.retry();
            else if (MENU_BUTTON.contains(x, y)) onReturnToMenu.run();
        } else {
            game.jump();
        }
        requestFocusInWindow();
        repaint();
    }

    private void tick(ActionEvent event) {
        long now = System.nanoTime();
        if (lastUpdateNanos != 0) {
            double dt = Math.min((now - lastUpdateNanos) / 1_000_000_000.0, 0.05);
            game.update(dt);
        }
        lastUpdateNanos = now;
        repaint();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g);
        drawWorld(g);
        drawProgress(g);
        if (isFinished()) drawEndOverlay(g);
        g.dispose();
    }

    private void drawBackground(Graphics2D g) {
        boolean inverted = game.getGravityDirection() == GravityDirection.UP;
        Color top = inverted ? new Color(36, 31, 84) : new Color(24, 31, 67);
        Color bottom = inverted ? new Color(15, 20, 53) : new Color(11, 15, 35);
        g.setPaint(new GradientPaint(0, 0, top, 0, HEIGHT, bottom));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(66, 91, 148, 45));
        int offset = (int) (game.getLevel().getCameraX() * 0.22) % 60;
        for (int x = -offset; x < WIDTH; x += 60) g.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += 60) g.drawLine(0, y, WIDTH, y);
    }

    private void drawWorld(Graphics2D g) {
        int groundY = game.getGroundY();
        g.setColor(new Color(19, 25, 48));
        g.fillRect(0, groundY, WIDTH, HEIGHT - groundY);
        g.setColor(new Color(95, 235, 215));
        g.setStroke(new BasicStroke(3));
        g.drawLine(0, groundY, WIDTH, groundY);
        if (game.getGravityDirection() == GravityDirection.UP) {
            g.setColor(new Color(158, 132, 255));
            g.drawLine(0, game.getCeilingY(), WIDTH, game.getCeilingY());
        }
        for (Obstacle obstacle : game.getLevel().getObstacles()) {
            int screenX = (int) Math.round(obstacle.getX() - game.getLevel().getCameraX());
            if (screenX > -60 && screenX < WIDTH + 60) {
                obstacle.draw(g, screenX, groundY, game.getCeilingY());
            }
        }
        for (GravityPortal portal : game.getLevel().getPortals()) {
            int screenX = (int) Math.round(portal.getX() - game.getLevel().getCameraX());
            if (screenX > -80 && screenX < WIDTH + 80) portal.draw(g, screenX);
        }
        if (!isFinished()) game.getParticleSystem().draw(g);
        game.getPlayer().draw(g, PLAYER_X, game.getSelectedSkin(), game.getJumpAnimation());
        double finishX = game.getLevel().getLength() - game.getLevel().getCameraX() + PLAYER_X;
        if (finishX > -10 && finishX < WIDTH + 10) {
            g.setColor(new Color(255, 226, 118));
            g.setStroke(new BasicStroke(5));
            g.drawLine((int) finishX, groundY - 95, (int) finishX, groundY);
            g.fillRect((int) finishX, groundY - 95, 44, 22);
            g.setColor(new Color(38, 44, 78));
            g.drawString("FIM", (int) finishX + 8, groundY - 79);
        }
    }

    private void drawProgress(Graphics2D g) {
        int x = 24, y = 22, w = 250, h = 12;
        g.setColor(new Color(255, 255, 255, 45));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(new Color(91, 235, 212));
        g.fillRoundRect(x, y, (int) (w * game.getProgress()), h, 10, 10);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(Color.WHITE);
        g.drawString("FASE " + game.getSelectedLevelId() + "   " + Math.round(game.getProgress() * 100) + "%", x, y + 32);
        if (game.getGravityDirection() == GravityDirection.UP) {
            g.setColor(new Color(200, 188, 255));
            g.drawString("GRAVIDADE INVERTIDA", 300, 34);
        }
    }

    private void drawEndOverlay(Graphics2D g) {
        g.setColor(new Color(7, 10, 24, 210));
        g.fill(new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
        game.getParticleSystem().draw(g);
        boolean victory = game.getState() == GameState.VICTORY;
        g.setFont(new Font("SansSerif", Font.BOLD, 38));
        g.setColor(victory ? new Color(91, 235, 212) : new Color(255, 112, 132));
        center(g, victory ? "FASE " + game.getSelectedLevelId() + " CONCLUÍDA!" : "FIM DE JOGO", 190);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        center(g, victory ? "Você chegou ao final!" : "Tente novamente ou volte ao lobby.", 225);
        drawButton(g, RETRY_BUTTON, "JOGAR NOVAMENTE", new Color(46, 190, 175));
        drawButton(g, MENU_BUTTON, "VOLTAR AO LOBBY", new Color(65, 91, 148));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(186, 199, 224));
        center(g, "ESPAÇO reinicia  ·  ESC volta ao lobby  ·  R tenta novamente", 343);
    }

    private void drawButton(Graphics2D g, Rectangle2D bounds, String text, Color color) {
        g.setColor(color);
        g.fillRoundRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(), 12, 12);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight(), 12, 12);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        int x = (int) (bounds.getCenterX() - g.getFontMetrics().stringWidth(text) / 2.0);
        g.drawString(text, x, (int) bounds.getCenterY() + 5);
    }

    private void center(Graphics2D g, String text, int y) {
        g.drawString(text, (WIDTH - g.getFontMetrics().stringWidth(text)) / 2, y);
    }
}
