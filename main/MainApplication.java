package main;

import javax.swing.SwingUtilities;
import ui.WelcomeFrame;

public class MainApplication {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new WelcomeFrame();
        });
    }
}