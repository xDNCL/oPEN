package edu.mit.blocks.workspace;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JLayeredPane;

/**
 * This class serves as the zoomable JComponent and RBParent of the page
 * that wraps it.
 */
public class PageJComponent extends JLayeredPane implements RBParent {

    private static final long serialVersionUID = 83982193213L;
    private static final Integer BLOCK_LAYER = new Integer(1);
    private static final Integer HIGHLIGHT_LAYER = new Integer(0);
    private static final int IMAGE_WIDTH = 60;
    private Image image = null;
    private boolean fullview = true;

    public void setFullView(boolean isFullView) {
        this.fullview = isFullView;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    /**
     * renders this JComponent
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //paint page
        super.paintComponent(g);
        //set label color
        if (this.getBackground().getBlue() + this.getBackground().getGreen() + this.getBackground().getRed() > 400) {
            g.setColor(Color.DARK_GRAY);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }

        //paint label at correct position
        if (fullview) {
            int xpos = (int) (this.getWidth() * 0.5 - g.getFontMetrics().getStringBounds(this.getName(), g).getCenterX());
            g.drawString(this.getName(), xpos, getHeight() / 2);
            g.drawString(this.getName(), xpos, getHeight() / 4);
            g.drawString(this.getName(), xpos, getHeight() * 3 / 4);


            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.33F));
            int imageX = (int) (this.getWidth() / 2 - IMAGE_WIDTH / 2 * Page.zoom);
            int imageWidth = (int) (IMAGE_WIDTH * Page.zoom);
            g.drawImage(this.getImage(), imageX, getHeight() / 2 + 5, imageWidth, imageWidth, null);
            g.drawImage(this.getImage(), imageX, getHeight() / 4 + 5, imageWidth, imageWidth, null);
            g.drawImage(this.getImage(), imageX, getHeight() * 3 / 4 + 5, imageWidth, imageWidth, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }

    }

    //////////////////////////////////
    //RBParent implemented methods	//
    //////////////////////////////////
    /** @overrides RBParent.addToBlockLayer() */
    @Override
    public void addToBlockLayer(Component c) {
        this.add(c, BLOCK_LAYER);

    }

    /** @overrides RBParent.addToHighlightLayer() */
    @Override
    public void addToHighlightLayer(Component c) {
        this.add(c, HIGHLIGHT_LAYER);
    }
}
