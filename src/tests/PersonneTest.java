package tests;

import model.*;
import view.*;


import javax.swing.SwingUtilities;

public class PersonneTest {
    public static void main(String[] args) {
    	DatabaseSetup.createTable();
        SwingUtilities.invokeLater(() -> {
            AccueilPage accueil = new AccueilPage();
            accueil.show();

        });
    }
}

