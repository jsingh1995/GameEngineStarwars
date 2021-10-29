import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel implements Runnable {

	Font font = new Font(Font.MONOSPACED, Font.BOLD, 32);
	KeyDetector keys;
	MouseDetector mouse;

	JFrame frame = new JFrame("starwars");
	volatile boolean play;
	boolean inited = false;
	boolean up = false, down = false, right = false, left = false;
	boolean gameOver;
	boolean resultPanel;

	BufferedImage backGround;
	BufferedImage opacity;
	ResultPanel result;

	private long diff, start = System.currentTimeMillis();
	double theta;
	Thread thread;
	int turbo = 4;
	int numberOfEnemies = 12;
	int speedOfEnemies = 3;
	int speedOfBullets = 20;
	int numberOfHearts = 3;
	int remainingHearts = numberOfHearts;
	int numberOfBullets = 25;
	int countEnemy = numberOfEnemies;
	int timeForBullet = 1;
	int seconds = 90;

	Player player;
	HealthBar[] hearts;
	Enemy[] enemy;
	Bullet[] bullets;

	public Game() {
		setSize(1080, 660);
		frame.setSize(1080, 680);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		setFocusable(true);
		frame.setLocationRelativeTo(null);
		setBackground(Color.BLACK);
		frame.add(this);
		File background = new File("img/background.jpg");
		try {
			backGround = (ImageIO.read(background)).getSubimage(0, 0, 1080, 660);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File opacityF = new File("img/opacity.png");
		try {
			opacity = (ImageIO.read(opacityF)).getSubimage(0, 0, 1080, 660);
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setVisible(true);
	}

	public void init() {
		keys = new KeyDetector(this);
		mouse = new MouseDetector(this);

		gameOver = true;
		play = false;
		resultPanel = false;

		result = new ResultPanel(this);

		player = new Player();

		remainingHearts = numberOfHearts;
		hearts = new HealthBar[numberOfHearts];
		for (int i = 0; i < numberOfHearts; i++) {
			hearts[i] = new HealthBar();
			hearts[i].x = 40 * i;
		}

		enemy = new Enemy[numberOfEnemies];
		for (int i = 0; i < numberOfEnemies; i++) {
			enemy[i] = new Enemy();
		}

		thread = new Thread(this);

		bullets = new Bullet[numberOfBullets];
		for (int i = 0; i < numberOfBullets; i++) {
			bullets[i] = new Bullet(210, 200, 0);
		}

		inited = true;
	}

	public void run() {
		init();
		while (true) {

			main();

			while (!gameOver) {
				play();
			}

			result();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (inited) {
			if (!gameOver) {
				g2d.drawImage(backGround, 0, 0, null); // BackGround

				for (int i = 0; i < numberOfHearts; i++)
					g2d.drawImage(hearts[i].getImage(), hearts[i].x, 620, null);

				// Calculate The angle
				theta = Math.atan2(mouse.getY() - (player.y + 40), mouse.getX() - (player.x + 50)) + Math.PI / 2;

				for (int i = 0; i < numberOfBullets; i++) {
					if (bullets[i].fired) {
						g2d.drawImage(bullets[i].getImage(), bullets[i].x, bullets[i].y, null);
					}
				}

				player.draw(g2d, theta);

				for (int v = 0; v < numberOfEnemies; v++) {
					if (enemy[v].isAlive) {
						g2d.drawImage(enemy[v].getImage(), enemy[v].x, enemy[v].y, null);
					}
				}

			} else if (resultPanel) {
				result.draw(g2d);
			}
		}

	}

	public static void main(String[] args) {
		new Thread(new Game()).start();
	}

	public void sleep(int fps) {
		if (fps > 0) {
			diff = System.currentTimeMillis() - start;
			long targetDelay = 1000 / fps;
			if (diff < targetDelay) {
				try {
					Thread.sleep(targetDelay - diff);
				} catch (InterruptedException e) {
				}
			}
			start = System.currentTimeMillis();
		}
	}

	public void addBullet(int x) {
		if (x == 1) {
			for (int i = 0; i < numberOfBullets; i++) {
				if (!bullets[i].fired) {
					if (i % 2 == 0)
						bullets[i].x = player.x + 70;
					else
					bullets[i].x = player.x + 30;
					bullets[i].y = player.y + 40;
					bullets[i].theta = theta;
					bullets[i].sin = Math.sin(bullets[i].theta - Math.PI / 2);
					bullets[i].cos = Math.cos(bullets[i].theta - Math.PI / 2);
					bullets[i].fired = true;
					break;
				}
			}
		}
		}



	public void play() {

		while (play) {
			if (player.isAlive) {
				if (up && player.y > 0)
					player.setY(player.y - turbo);
				if (down && player.y < 580)
					player.setY(player.y + turbo);
				if (right && player.x < 980)
					player.setX(player.x + turbo);
				if (left && player.x > 0)
					player.setX(player.x - turbo);

				for (int i = 0; i < numberOfEnemies; i++) {
					enemy[i].theta = Math.atan2(enemy[i].y - (player.y + 40), enemy[i].x - (player.x + 50));

					enemy[i].y = enemy[i].y - (int) (speedOfEnemies * Math.sin(enemy[i].theta));

					enemy[i].x = enemy[i].x - (int) (speedOfEnemies * Math.cos(enemy[i].theta));

					if (enemy[i].isAlive && remainingHearts != 0
							&& enemy[i].getBounds().intersects(player.getBounds())) {
						enemy[i].isAlive = false;
						countEnemy--;
						hearts[--remainingHearts].setImage();
					}
				}


				for (int i = 0; i < numberOfBullets; i++) {
					if (bullets[i].fired) {
						for (int v = 0; v < numberOfEnemies; v++) {
							if (enemy[v].isAlive) {
								if (enemy[v].getBounds().intersects(bullets[i].getBounds())) {
									enemy[v].isAlive = false;
									countEnemy--;
									bullets[i].fired = false;
								}
							}
						}

						if (bullets[i].x < -100 || bullets[i].x > 1180 || bullets[i].y < -100 || bullets[i].y > 760)
							bullets[i].fired = false;
						else {
							bullets[i].y = bullets[i].y + (int) (speedOfBullets * bullets[i].sin);

							bullets[i].x = bullets[i].x + (int) (speedOfBullets * bullets[i].cos);
						}
					}
				}

				if (remainingHearts == 0) {
					result.won = false;
					play = false;
					gameOver = true;
					resultPanel = true;
				}
				if (countEnemy == 0) {
					result.won = true;
					play = false;
					gameOver = true;
					resultPanel = true;
				}

				repaint();
				sleep(60);
				timeForBullet++;
			}
		}
	}

	public void main() {

		remainingHearts = 3;
		seconds = 90;
		countEnemy = numberOfEnemies;
		reset();
		play = true;
		gameOver = false;
		repaint();
		sleep(60);
	}

	public void result() {
		result.painted = false;
		mouse.pClicked.x = 0;
		mouse.pClicked.y = 0;
		while (resultPanel) {

			for (int i = 0; i < 2; i++) {
				if (result.rectangle[i].contains(mouse.pMoved))
					result.activated[i] = true;
				else
					result.activated[i] = false;
			}

			if (result.rectangle[1].contains(mouse.pClicked))
				System.exit(0);
			if (result.rectangle[0].contains(mouse.pClicked)) {
				mouse.pClicked.x = 0;
				mouse.pClicked.y = 0;
				resultPanel = false;
			}

			repaint();
			sleep(60);
			result.painted = true;

		}
	}

	public void reset() {

		for (int i = 0; i < numberOfHearts; i++) {
			hearts[i].setHealth();
		}

		for (int i = 0; i < numberOfEnemies; i++) {
			enemy[i].isAlive = true;
			enemy[i].setLocation();
		}

		for (int i = 0; i < numberOfBullets; i++) {
			bullets[i].fired = false;
		}
		player.x = 1080 / 2 - 50;
		player.y = 340 - 40;

	}
}
