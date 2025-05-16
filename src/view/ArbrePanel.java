package view;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;


public class ArbrePanel extends JPanel {
    private ArbreGenealogique arbre;
    private int boxWidth = 140;
    private int boxHeight = 50;
    private int verticalSpacing = 100;
    private int horizontalSpacing = 40;
    private Map<Personne, Rectangle> positions = new HashMap<>();

    public ArbrePanel(ArbreGenealogique arbre) {
        this.arbre = arbre;
        setPreferredSize(new Dimension(1000, 700));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        positions.clear();
        Set<Personne> visites = new HashSet<>();
        int startX = getWidth() / 2;
        int startY = 20;
        dessinerNoeud(g, arbre.getRacine(), startX, startY, visites);
    }

    private int dessinerNoeud(Graphics g, Personne p, int x, int y, Set<Personne> visites) {
        if (p == null || visites.contains(p)) {
            return 0;
        }
        visites.add(p);

        List<Personne> enfants = arbre.getEnfants(p);
        int totalWidth = 0;
        List<Integer> enfantsX = new ArrayList<>();
        int currentX = x;

        for (Personne enfant : enfants) {
            int childWidth = dessinerNoeud(g, enfant, currentX, y + boxHeight + verticalSpacing, visites);
            enfantsX.add(currentX);
            currentX += childWidth + horizontalSpacing;
            totalWidth += childWidth + horizontalSpacing;
        }
        if (!enfants.isEmpty()) totalWidth -= horizontalSpacing;

        if (totalWidth == 0) totalWidth = boxWidth;

        int nodeX = enfants.isEmpty() ? x : (enfantsX.get(0) + enfantsX.get(enfantsX.size() - 1)) / 2;

        Rectangle rect = new Rectangle(nodeX - boxWidth / 2, y, boxWidth, boxHeight);
        positions.put(p, rect);

        g.setColor(new Color(230, 240, 255));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(Color.BLUE.darker());
        g.drawRect(rect.x, rect.y, rect.width, rect.height);

        String texte = p.getPrenom() + " " + p.getNom();
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(texte);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height + fm.getAscent()) / 2 - 4;
        g.setColor(Color.BLACK);
        g.drawString(texte, textX, textY);

        for (Personne enfant : enfants) {
            Rectangle childRect = positions.get(enfant);
            if (childRect != null) {
                int x1 = rect.x + rect.width / 2;
                int y1 = rect.y + rect.height;
                int x2 = childRect.x + childRect.width / 2;
                int y2 = childRect.y;
                g.setColor(Color.GRAY);
                g.drawLine(x1, y1, x2, y2);
            }
        }

        return totalWidth;
    }
}
