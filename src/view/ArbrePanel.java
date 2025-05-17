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

    private ArbreGenealogique arbre;

    private int boxWidth = 140;
    private int boxHeight = 50;
    private int verticalSpacing = 100;
    private int horizontalSpacing = 40;

    private Map<Personne, javafx.geometry.Rectangle2D> positions = new HashMap<>();

    private Canvas canvas;

    public ArbrePanel(ArbreGenealogique arbre) {
        this.arbre = arbre;
        this.setPrefSize(1000, 700);

        canvas = new Canvas(getPrefWidth(), getPrefHeight());
        this.getChildren().add(canvas);

        // Redessiner quand la taille change
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

        // Fond blanc
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        positions.clear();

        Set<Personne> visites = new HashSet<>();
        int startX = (int) width / 2;
        int startY = 20;

        dessinerNoeud(gc, arbre.getRacine(), startX, startY, visites);
    }

    private int dessinerNoeud(GraphicsContext gc, Personne p, int x, int y, Set<Personne> visites) {
        if (p == null || visites.contains(p)) {
            return 0;
        }
        visites.add(p);

        List<Personne> enfants = arbre.getEnfants(p);
        int totalWidth = 0;
        List<Integer> enfantsX = new ArrayList<>();
        int currentX = x;

        for (Personne enfant : enfants) {
            int childWidth = dessinerNoeud(gc, enfant, currentX, y + boxHeight + verticalSpacing, visites);
            enfantsX.add(currentX);
            currentX += childWidth + horizontalSpacing;
            totalWidth += childWidth + horizontalSpacing;
        }
        if (!enfants.isEmpty()) totalWidth -= horizontalSpacing;

        if (totalWidth == 0) totalWidth = boxWidth;

        int nodeX = enfants.isEmpty() ? x : (enfantsX.get(0) + enfantsX.get(enfantsX.size() - 1)) / 2;

        javafx.geometry.Rectangle2D rect = new javafx.geometry.Rectangle2D(nodeX - boxWidth / 2, y, boxWidth, boxHeight);
        positions.put(p, rect);

        // Dessin du rectangle avec fond bleu clair
        gc.setFill(Color.rgb(230, 240, 255));
        gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());

        gc.setStroke(Color.DARKBLUE);
        gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());

        // Texte centré
        String texte = p.getPrenom() + " " + p.getNom();
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("System", 14));

        // Calcul largeur texte approximatif
        Text text = new Text(texte);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        double textX = rect.getMinX() + (rect.getWidth() - textWidth) / 2;
        double textY = rect.getMinY() + (rect.getHeight() + 14) / 2 - 4; // 14 = font size approx

        gc.fillText(texte, textX, textY);

        // Dessiner les lignes vers enfants
        for (Personne enfant : enfants) {
            javafx.geometry.Rectangle2D childRect = positions.get(enfant);
            if (childRect != null) {
                double x1 = rect.getMinX() + rect.getWidth() / 2;
                double y1 = rect.getMinY() + rect.getHeight();
                double x2 = childRect.getMinX() + childRect.getWidth() / 2;
                double y2 = childRect.getMinY();

                gc.setStroke(Color.GRAY);
                gc.strokeLine(x1, y1, x2, y2);
            }
        }

        return totalWidth;
    }
}
