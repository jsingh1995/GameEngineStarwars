import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyDetector implements KeyListener {

	Game game;

	public KeyDetector(Game game) {
		game.addKeyListener(this);
		this.game = game;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			game.up = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			game.down = true;
		if (e.getKeyCode() == KeyEvent.VK_D)
			game.right = true;
		if (e.getKeyCode() == KeyEvent.VK_A)
			game.left = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			game.turbo = 10;

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W)
			game.up = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			game.down = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			game.right = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			game.left = false;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			game.turbo = 4;
	}

	public void keyTyped(KeyEvent arg0) {

	}

}
