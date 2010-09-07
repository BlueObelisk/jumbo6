package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HiddenGraphics {

	private static final String PNG = "png";
	private Dimension dimension;
	private BufferedImage img;
	private Graphics2D g;
	private String type;
	private Color backgroundColor;
	
	public HiddenGraphics() {
		setDefaults();
	}
	private void setDefaults() {
		this.type = PNG;
		this.setDimension(new Dimension(400, 400));
		this.setBackgroundColor(Color.WHITE);
	}
	private void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	public void setDimension(Dimension d) {
		this.dimension = d;
	}
	public Graphics2D createGraphics() {
		img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, dimension.width, dimension.height);
		return g;
	}
	
	public void setOutputType(String type) {
		this.type = type;
	}
	
	public void write(String filename) throws IOException {
		ImageIO.write(img, type, new File(filename));
	}
	public static void main(String[] args) throws IOException {
		HiddenGraphics graphics = new HiddenGraphics();
		Graphics2D g = graphics.createGraphics();
		g.setColor(Color.GREEN);
		g.fillOval(100, 170, 200, 200);
		g.fillRect(165, 25, 70, 200);
		g.fillRect(155, 25, 90, 20);
		graphics.write("image.png");
	}
	
}
