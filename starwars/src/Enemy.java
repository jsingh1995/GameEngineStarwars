import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Enemy {
	BufferedImage image;
	File fImage = new File("img/enemy1.png");

	int x;
	int y;
	boolean isAlive = true;
	double theta;
	
	AffineTransform at = new AffineTransform();

	public Enemy() {
		try {
			image = ImageIO.read(fImage);
		} catch (Exception e) {
		}
		setLocation();
	}

	public BufferedImage getImage() {
		return image;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, image.getWidth() - 5, image.getHeight() - 5);
	}

	public void setLocation() {
		int i, j;
		i = (int) (Math.random() * 2 + 1);
		j = (int) (Math.random() * 2 + 1);
		if (i == 1)
			x = (int) (Math.random() * 220 + 1);
		else
			x = (int) (Math.random() * 220 + 811);
		if (j == 1)
			y = (int) (Math.random() * 115 + 1);
		else
			y = (int) (Math.random() * 115 + 496);
	}

}
