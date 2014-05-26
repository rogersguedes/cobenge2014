/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.marcelosilveira.gerenciadorDeMemoria.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JImagePanel extends JPanel {
	private BufferedImage image;
	// private BufferedImage image = null;
	private FillType fillType = FillType.RESIZE;

        public JImagePanel()
        {
            
        }

	public JImagePanel(URL url) throws IOException, URISyntaxException {
		setImage(url);
	}

	public void setImage(URL url) throws IOException, URISyntaxException {
		this.image = ImageIO.read(new File(url.toURI()));
	}

	public BufferedImage getImage() {
		return image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		fillType.drawImage(this, g2d, image);
		g2d.dispose();
	}

	/**
	 * Returns the way this image fills itself.
	 * 
	 * @return The fill type.
	 */
	public FillType getFillType() {
		return fillType;
	}

	/**
	 * Changes the fill type.
	 * 
	 * @param fillType
	 *            The new fill type
	 * @throws IllegalArgumentException
	 *             If the fill type is null.
	 */
	public void setFillType(FillType fillType) {
		if (fillType == null)
			throw new IllegalArgumentException("Invalid fill type!");

		this.fillType = fillType;
		invalidate();
	}

	public static enum FillType {
		/**
		 * Make the image size equal to the panel size, by resizing it.
		 */
		RESIZE {
			@Override
			public void drawImage(JPanel panel, Graphics2D g2d,
					BufferedImage image) {
				g2d.drawImage(image, 0, 0, panel.getWidth(), panel.getHeight(),
						null);
			}
		},

		/**
		 * Centers the image on the panel.
		 */
		CENTER {
			@Override
			public void drawImage(JPanel panel, Graphics2D g2d,
					BufferedImage image) {
				int left = (panel.getWidth() - image.getWidth()) / 2;
				int top = (panel.getHeight() - image.getHeight()) / 2;
				g2d.drawImage(image, left, top, null);
			}

		},
		/**
		 * Makes several copies of the image in the panel, putting them side by
		 * side.
		 */
		SIDE_BY_SIDE {
			@Override
			public void drawImage(JPanel panel, Graphics2D g2d,
					BufferedImage image) {
				Paint p = new TexturePaint(image, new Rectangle2D.Float(0, 0,
						image.getWidth(), image.getHeight()));
				g2d.setPaint(p);
				g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			}
		};

		public abstract void drawImage(JPanel panel, Graphics2D g2d,
				BufferedImage image);
	}
}