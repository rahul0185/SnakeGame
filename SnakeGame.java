import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int BOX_SIZE = 15;
    private static final int INIT_LENGTH = 3;
    private static final int GAME_SPEED = 100;

    private LinkedList<Point> snake;
    private Point food;
    private char direction;
    private boolean gameOver;
    private boolean growing;

    private Timer timer;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        initGame();

        timer = new Timer(GAME_SPEED, this);
        timer.start();
    }

    private void initGame() {
        snake = new LinkedList<>();
        for (int i = 0; i < INIT_LENGTH; i++) {
            snake.add(new Point(300 - i * BOX_SIZE, 300));  // Initial position of the snake
        }

        direction = 'R';

        generateFood();

        gameOver = false;
        growing = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.YELLOW);
        for (Point p : snake) {
            g.fillRect(p.x, p.y, BOX_SIZE, BOX_SIZE);
        }

        g.setColor(Color.GREEN);
        g.fillRect(food.x, food.y, BOX_SIZE, BOX_SIZE);

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER!", WIDTH / 3, HEIGHT / 2);

            int response = JOptionPane.showConfirmDialog(this, "Game Over! Do you want to play again?",
                    "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                initGame();
                repaint();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }

        moveSnake();
        checkCollisions();
        checkFoodCollision();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            return;
        }

        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP && direction != 'D') {
            direction = 'U';
        } else if (keyCode == KeyEvent.VK_DOWN && direction != 'U') {
            direction = 'D';
        } else if (keyCode == KeyEvent.VK_LEFT && direction != 'R') {
            direction = 'L';
        } else if (keyCode == KeyEvent.VK_RIGHT && direction != 'L') {
            direction = 'R';
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = null;

        switch (direction) {
            case 'U':
                newHead = new Point(head.x, head.y - BOX_SIZE);
                break;
            case 'D':
                newHead = new Point(head.x, head.y + BOX_SIZE);
                break;
            case 'L':
                newHead = new Point(head.x - BOX_SIZE, head.y);
                break;
            case 'R':
                newHead = new Point(head.x + BOX_SIZE, head.y);
                break;
        }

        snake.addFirst(newHead);

        if (growing) {
            growing = false;
        } else {
            snake.removeLast();
        }
    }

    private void checkCollisions() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameOver = true;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                break;
            }
        }
    }

    private void checkFoodCollision() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            growing = true;
            generateFood();
        }
    }

    // Updated food generation logic to avoid food appearing on snake's body
    private void generateFood() {
        Random rand = new Random();
        boolean validPosition = false;
        int x = 0, y = 0;

        // Keep generating a new food position until it's not on the snake's body
        while (!validPosition) {
            x = (rand.nextInt(WIDTH / BOX_SIZE)) * BOX_SIZE;
            y = (rand.nextInt(HEIGHT / BOX_SIZE)) * BOX_SIZE;

            validPosition = true;

            // Check if the food position is already occupied by the snake
            for (Point p : snake) {
                if (p.x == x && p.y == y) {
                    validPosition = false;  // The position is occupied, try again
                    break;
                }
            }
        }

        // Once a valid position is found, set the food position
        food = new Point(x, y);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);
    }
}
