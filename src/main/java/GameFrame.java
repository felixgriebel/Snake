import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameFrame extends JFrame {
    //TODO 1: add icon
    //TODO 2: add lost screen


    public static Color headColor = new Color(0, 79, 21);
    public static Color tailColor = Color.green;
    public static Color appleColor = Color.RED;
    public static Color backColor = Color.BLACK;

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int SPEED = 3;
    public static final int BODY_SIZE = 15;
    private static final int ADDITION_PER_APPLE = 15;
    private static final int LOST_SCREEN_SIZE = WIDTH / 15;

    private int direction = 3;
    private Thread th;
    public boolean end = false;
    private boolean finalEnd = false;


    private int changeCounter = 0;

    private JPanel apple;

    private boolean addBody = false;

    private boolean darkMode = true;


    private final BodyElement innerPanel;
    private List<BodyElement> tail = new ArrayList<>();
    private Point lastPos = new Point(WIDTH / 4, HEIGHT / 4);
    private final JPanel panel;

    public GameFrame() throws HeadlessException {
        super("SNAKE");
        this.setLayout(new GridLayout(1, 1));
        this.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.pack();


        URL iconURL = getClass().getResource("snake_icon.png");
// iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());

        panel = new JPanel();
        panel.setBackground(backColor);
        panel.setLayout(null);
        this.add(panel);


        innerPanel = new BodyElement();
        innerPanel.setBackground(headColor);
        innerPanel.setLocation(WIDTH / 4, HEIGHT / 4);
        panel.add(innerPanel);


        apple = new JPanel();
        apple.setBackground(appleColor);
        apple.setSize(BODY_SIZE, BODY_SIZE);
        apple.setLocation(WIDTH / 2, HEIGHT / 2);
        panel.add(apple);

        th = new Thread(new RunnableUpdater(this));

        for (int i = 0; i < 20; i++) addElement();

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!finalEnd) {
                    if (!th.isAlive()) {
                        end = false;
                        th = new Thread(new RunnableUpdater(getThis()));
                        th.start();
                    }

                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        end = true;
                        th = new Thread(new RunnableUpdater(getThis()));
                    }

                    if (changeCounter >= BODY_SIZE) {
                        if (e.getKeyCode() == KeyEvent.VK_W) {
                            if (direction != 2) {
                                direction = 0;
                                changeCounter = 0;
                            }
                        }

                        if (e.getKeyCode() == KeyEvent.VK_A) {
                            if (direction != 3) {
                                direction = 1;
                                changeCounter = 0;
                            }
                        }


                        if (e.getKeyCode() == KeyEvent.VK_S) {
                            if (direction != 0) {
                                direction = 2;
                                changeCounter = 0;
                            }
                        }

                        if (e.getKeyCode() == KeyEvent.VK_D) {
                            if (direction != 1) {
                                direction = 3;
                                changeCounter = 0;
                            }
                        }
                        if (e.getKeyCode() == KeyEvent.VK_L) {
                            if (darkMode) {
                                backColor = Color.GRAY;
                                panel.setBackground(backColor);

                                headColor = new Color(50, 50, 252);
                                tailColor = Color.BLUE;
                                appleColor = new Color(244, 228, 2);
                                innerPanel.setBackground(headColor);
                                for (BodyElement bodyElement : tail) {
                                    bodyElement.setBackground(tailColor);
                                }
                                apple.setBackground(appleColor);
                                darkMode = false;
                            } else {
                                backColor = Color.BLACK;
                                panel.setBackground(backColor);
                                headColor = new Color(0, 79, 21);
                                tailColor = Color.GREEN;
                                appleColor = Color.RED;
                                innerPanel.setBackground(headColor);
                                for (BodyElement bodyElement : tail) {
                                    bodyElement.setBackground(tailColor);
                                }
                                apple.setBackground(appleColor);
                                darkMode = true;
                            }
                        }
                    }
                }


            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void move() {
        changeCounter += SPEED;

        lastPos = innerPanel.getLocation();
        switch (direction) {
            case 0 -> {
                int tempY = innerPanel.getLocation().y - SPEED;

                if (tempY < 0) tempY += HEIGHT;
                innerPanel.setLocation(innerPanel.getLocation().x, tempY);
            }
            case 1 -> {
                int tempX = innerPanel.getLocation().x - SPEED;

                if (tempX < 0) tempX += WIDTH;
                innerPanel.setLocation(tempX, innerPanel.getLocation().y);
            }
            case 2 -> {
                innerPanel.setLocation(innerPanel.getLocation().x, (innerPanel.getLocation().y + SPEED) % HEIGHT);
            }
            case 3 -> {
                innerPanel.setLocation((innerPanel.getLocation().x + SPEED) % WIDTH, innerPanel.getLocation().y);
            }


        }

        for (BodyElement bodyElement : tail) {
            Point temp = bodyElement.getLocation();
            bodyElement.setLocation(lastPos);
            lastPos = temp;
        }


        if (addBody) {
            for (int i = 0; i < ADDITION_PER_APPLE; i++) {
                addElement();
            }
            addBody = false;
        }
    }

    public void checkforEnd() throws InterruptedException {
        for (int i = 0; i < tail.size(); i++) {
            //A
            if (direction == 0 || direction == 1) {
                if (innerPanel.getLocation().x > tail.get(i).getLocation().x && innerPanel.getLocation().y > tail.get(i).getLocation().y && innerPanel.getLocation().x < (tail.get(i).getLocation().x + BODY_SIZE) && innerPanel.getLocation().y < (tail.get(i).getLocation().y + BODY_SIZE)) {
                    end = true;
                    finalEnd = true;
                    break;
                }
            }
            if (direction == 0 || direction == 3) {
                if ((innerPanel.getLocation().x + BODY_SIZE) > tail.get(i).getLocation().x && innerPanel.getLocation().y > tail.get(i).getLocation().y && (innerPanel.getLocation().x + BODY_SIZE) < (tail.get(i).getLocation().x + BODY_SIZE) && innerPanel.getLocation().y < (tail.get(i).getLocation().y + BODY_SIZE)) {
                    end = true;
                    finalEnd = true;
                    break;
                }
            }
            if (direction == 1 || direction == 2) {
                if (innerPanel.getLocation().x > tail.get(i).getLocation().x && (innerPanel.getLocation().y + BODY_SIZE) > tail.get(i).getLocation().y && innerPanel.getLocation().x < (tail.get(i).getLocation().x + BODY_SIZE) && (innerPanel.getLocation().y + BODY_SIZE) < (tail.get(i).getLocation().y + BODY_SIZE)) {
                    end = true;
                    finalEnd = true;
                    break;
                }
            }
            if (direction == 2 || direction == 3) {
                if ((innerPanel.getLocation().x + BODY_SIZE) > tail.get(i).getLocation().x && (innerPanel.getLocation().y + BODY_SIZE) > tail.get(i).getLocation().y && (innerPanel.getLocation().x + BODY_SIZE) < (tail.get(i).getLocation().x + BODY_SIZE) && (innerPanel.getLocation().y + BODY_SIZE) < (tail.get(i).getLocation().y + BODY_SIZE)) {
                    end = true;
                    finalEnd = true;
                    break;
                }
            }
        }
        if (finalEnd) {

            reset();
        }

    }

    public void addElement() {
        BodyElement elem = new BodyElement();
        elem.setSize(BODY_SIZE, BODY_SIZE);
        elem.setLocation(lastPos);
        panel.add(elem);
        tail.add(elem);
    }

    public GameFrame getThis() {
        return this;
    }

    public void reset() throws InterruptedException {
        for(int i=0;i<8;i++){
            innerPanel.setBackground(Color.RED);
            TimeUnit.MILLISECONDS.sleep(250);
            innerPanel.setBackground(headColor);
            TimeUnit.MILLISECONDS.sleep(250);
        }
        this.dispose();
        new GameFrame();
    }

    public void spawnApple() {
        apple.setBackground(appleColor);
        int tempX = ((int) (Math.random() * (WIDTH - BODY_SIZE)));
        int tempY = ((int) (Math.random() * (HEIGHT - BODY_SIZE)));
        apple.setLocation(tempX, tempY);
        panel.add(apple);

        panel.repaint();
    }

    public synchronized void appleEaten() {
        Thread applethread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spawnApple();

        });
        if (apple.getBackground().equals(appleColor)) {
            synchronized (this) {


                if (innerPanel.getLocation().x >= apple.getLocation().x && innerPanel.getLocation().y >= apple.getLocation().y && innerPanel.getLocation().x <= (apple.getLocation().x + BODY_SIZE) && innerPanel.getLocation().y <= (apple.getLocation().y + BODY_SIZE) && (direction == 0 || direction == 1)) {
                    apple.setBackground(backColor);
                    panel.remove(apple);
                    panel.repaint();
                    addBody = true;
                    applethread.start();
                } else if ((innerPanel.getLocation().x + BODY_SIZE) >= apple.getLocation().x && innerPanel.getLocation().y >= apple.getLocation().y && (innerPanel.getLocation().x + BODY_SIZE) <= (apple.getLocation().x + BODY_SIZE) && innerPanel.getLocation().y <= (apple.getLocation().y + BODY_SIZE) && (direction == 0 || direction == 3)) {
                    apple.setBackground(backColor);
                    panel.remove(apple);
                    panel.repaint();
                    addBody = true;
                    applethread.start();
                } else if (innerPanel.getLocation().x >= apple.getLocation().x && (innerPanel.getLocation().y + BODY_SIZE) >= apple.getLocation().y && innerPanel.getLocation().x <= (apple.getLocation().x + BODY_SIZE) && (innerPanel.getLocation().y + BODY_SIZE) <= (apple.getLocation().y + BODY_SIZE) && (direction == 1 || direction == 2)) {
                    apple.setBackground(backColor);
                    panel.remove(apple);
                    panel.repaint();
                    addBody = true;
                    applethread.start();
                } else if ((innerPanel.getLocation().x + BODY_SIZE) >= apple.getLocation().x && (innerPanel.getLocation().y + BODY_SIZE) >= apple.getLocation().y && (innerPanel.getLocation().x + BODY_SIZE) <= (apple.getLocation().x + BODY_SIZE) && (innerPanel.getLocation().y + BODY_SIZE) <= (apple.getLocation().y + BODY_SIZE) && (direction == 2 || direction == 3)) {
                    apple.setBackground(backColor);
                    panel.remove(apple);
                    panel.repaint();
                    addBody = true;
                    applethread.start();
                }


            }
        }
    }
}