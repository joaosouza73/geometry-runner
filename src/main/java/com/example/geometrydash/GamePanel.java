package com.example.geometrydash;

import javax.swing.JPanel;
import java.awt.CardLayout;

/** Navegação entre telas; o menu e a partida mantêm responsabilidades separadas. */
public final class GamePanel extends JPanel {
    private final Game game = new Game();
    private final CardLayout cards = new CardLayout();
    private final MainMenuPanel menu;
    private final GameView gameView;

    public GamePanel() {
        setLayout(cards);
        menu = new MainMenuPanel(game, this::playSelectedLevel);
        gameView = new GameView(game, this::returnToMenu);
        add(menu, "menu");
        add(gameView, "game");
        cards.show(this, "menu");
    }

    private void playSelectedLevel() {
        game.start();
        cards.show(this, "game");
        gameView.requestFocusInWindow();
    }

    private void returnToMenu() {
        game.returnToMenu();
        cards.show(this, "menu");
        menu.requestFocusInWindow();
    }
}
