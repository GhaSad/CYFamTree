package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.*;

import java.util.*;

public class ArbrePanel extends Pane {

    private final ArbreGenealogique arbre;

    private final int boxWidth = 140;
    private final int boxHeight = 50;
    private final int verticalSpacing = 100;
    private final int horizontalSpacing = 40;

    private final Map<Personne, javafx.geometry.Rectangle2D> positions = new HashMap<>();

    private final Canvas canvas;

    public ArbrePanel(ArbreGenealogique arbre) {
        this.arbre = arbre;
        this.setPrefSize(1000, 700);

        canvas = new Canvas(getPrefWidth(), getPrefHeight());
        this.getChildren().add(canvas);

        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        redraw();
    }

    private void redraw() {
        double width = getWidth() > 0 ? getWidth() : getPrefWidth();
        double height = getHeight() > 0 ? getHeight() : getPrefHeight();

        canvas.setWidth(width);
        canvas.setHeight(height);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        positions.clear();
        Set<Personne> visites = new HashSet<>();

        Personne racine = arbre.getVraieRacine();
        int centerX = (int) width / 2;
        int startY = 100;

        dessinerNoeudRecursif(gc, racine, centerX, startY, visites);
    }

    private void dessinerNoeudRecursif(GraphicsContext gc, Personne p, int x, int y, Set<Personne> visites) {
        if (p == null || visites.contains(p)) return;
        visites.add(p);

        javafx.geometry.Rectangle2D rect = new javafx.geometry.Rectangle2D(x - boxWidth / 2, y, boxWidth, boxHeight);
        positions.put(p, rect);

        // Dessiner la boîte
        gc.setFill(Color.rgb(230, 240, 255));
        gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
        gc.setStroke(Color.DARKBLUE);
        gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());

        // Dessiner le nom
        String texte = p.getPrenom() + " " + p.getNom();
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("System", 14));
        Text text = new Text(texte);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        gc.fillText(texte, rect.getMinX() + (boxWidth - textWidth) / 2, rect.getMinY() + 30);

        // Dessiner les parents
        Noeud noeud = arbre.getNoeudParPersonne(p);
        if (noeud != null) {
            List<Noeud> parents = noeud.getParents();
            int totalWidth = parents.size() * (boxWidth + horizontalSpacing);
            int startX = x - totalWidth / 2;

            for (int i = 0; i < parents.size(); i++) {
                Personne parent = parents.get(i).getPersonne();
                int parentX = startX + i * (boxWidth + horizontalSpacing);
                int parentY = y - verticalSpacing - boxHeight;

                dessinerNoeudRecursif(gc, parent, parentX + boxWidth / 2, parentY, visites);

                javafx.geometry.Rectangle2D parentRect = positions.get(parent);
                if (parentRect != null) {
                    gc.setStroke(Color.GRAY);
                    gc.strokeLine(
                            parentRect.getMinX() + boxWidth / 2,
                            parentRect.getMinY() + boxHeight,
                            rect.getMinX() + boxWidth / 2,
                            rect.getMinY()
                    );
                }
            }
        }

        // Dessiner les enfants
        List<Personne> enfants = arbre.getEnfants(p);
        int totalWidth = enfants.size() * (boxWidth + horizontalSpacing);
        int startX = x - totalWidth / 2;

        for (int i = 0; i < enfants.size(); i++) {
            Personne enfant = enfants.get(i);
            int enfantX = startX + i * (boxWidth + horizontalSpacing);
            int enfantY = y + boxHeight + verticalSpacing;

            dessinerNoeudRecursif(gc, enfant, enfantX + boxWidth / 2, enfantY, visites);

            javafx.geometry.Rectangle2D enfantRect = positions.get(enfant);
            if (enfantRect != null) {
                gc.setStroke(Color.GRAY);
                gc.strokeLine(
                        rect.getMinX() + boxWidth / 2,
                        rect.getMinY() + boxHeight,
                        enfantRect.getMinX() + boxWidth / 2,
                        enfantRect.getMinY()
                );
            }
        }
    }
}
