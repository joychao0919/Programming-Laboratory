package client;

import server.SnakeGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class SnakePanel extends JPanel implements KeyListener {

    // 客戶端
    Client client;

    //Import Image
    //Snake1
    ImageIcon up1 = new ImageIcon("up.png");
    ImageIcon down1 = new ImageIcon("down.png");
    ImageIcon right1 = new ImageIcon("right.png");
    ImageIcon left1 = new ImageIcon("left.png");
    ImageIcon body1 = new ImageIcon("body.png");

    //Snake2
    ImageIcon up2 = new ImageIcon("up2.png");
    ImageIcon down2 = new ImageIcon("down2.png");
    ImageIcon right2 = new ImageIcon("right2.png");
    ImageIcon left2 = new ImageIcon("left2.png");
    ImageIcon body2 = new ImageIcon("body2.png");

    //Title
    ImageIcon title = new ImageIcon("title.png");

    //Foods
    ImageIcon food1 = new ImageIcon("food.png");
    ImageIcon food2 = new ImageIcon("food.png");

    //Walls
    ImageIcon wall1 = new ImageIcon("wall.png");
    ImageIcon wall2 = new ImageIcon("wall.png");

    //Holes
    ImageIcon hole1 = new ImageIcon("hole.png");
    ImageIcon hole2 = new ImageIcon("hole.png");

    // Snake's Position, Length, Direction
    int[] snake1x = new int[100];
    int[] snake1y = new int[100];
    int len1;
    String direction1;

    int[] snake2x = new int[100];
    int[] snake2y = new int[100];
    int len2;
    String direction2;

    //Foods' initial position and amount
    int food1x;
    int food1y;
    int food2x;
    int food2y;

    int foodNum;

    //Holes' initial properties;
    int hole1x;
    int hole1y;
    int hole2x;
    int hole2y;

    //Wall's properties
    int[] wall1x = new int[9];
    int[] wall1y = new int[9];
    int[] wall2x = new int[9];
    int[] wall2y = new int[9];
    int wall1Len = 8;
    int wall2Len = 8;

    //Game Started Or Not
    boolean isStarted;

    //Game Failed Or Not
    boolean isFailed1;
    boolean isFailed2;

    //Score
    int score1;
    int score2;
    int maxScore;

    //Speed
    int speed;

    //From which hole
    boolean snake1FromHole1;
    boolean snake1FromHole2;
    boolean snake2FromHole1;
    boolean snake2FromHole2;

    //snake in hole
    boolean snake1InHole;
    boolean snake2InHole;

    //Panel properties
    public SnakePanel(SnakeGame snakeGame) {
        this.setFocusable(true);
        initSnake(snakeGame);
        this.addKeyListener(this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    //Initialize the game
    public void initSnake(SnakeGame snakeGame) {
        isStarted = true;
        isFailed1 = false;
        isFailed2 = false;
        len1 = 2;
        len2 = 2;
        score1 = 0;
        score2 = 0;
        speed = 170;
        foodNum = 2;
        direction1 = "L";
        direction2 = "R";
        snake1x[0] = 750;
        snake1y[0] = 600;
        snake1x[1] = 775;
        snake1y[1] = 600;
        snake2x[0] = 125;
        snake2y[0] = 125;
        snake2x[1] = 100;
        snake2y[1] = 125;
        food1x = snakeGame.getFood1x();
        food1y = snakeGame.getFood1y();
        food2x = snakeGame.getFood2x();
        food2y = snakeGame.getFood2y();
        wall1x[0] = snakeGame.getWall1x()[0];
        wall1y[0] = snakeGame.getWall1y()[0];
        wall2x[0] = snakeGame.getWall2x()[0];
        wall2y[0] = snakeGame.getWall2y()[0];
        hole1x = snakeGame.getHole1x();
        hole1y = snakeGame.getHole1y();
        hole2x = snakeGame.getHole2x();
        hole2y = snakeGame.getHole2y();
        snake1FromHole1 = false;
        snake1FromHole2 = false;
        snake2FromHole1 = false;
        snake2FromHole2 = false;
        snake1InHole = false;
        snake2InHole = false;
        for(int i=0; i<wall1Len; i++) {
            wall1x[i+1] = wall1x[i]+25;
            wall1y[i] = wall1y[0];
        }
        for(int i=0; i<wall2Len; i++) {
            wall2y[i+1] = wall2y[i]+25;
            wall2x[i] = wall2x[0];
        }
    }

    public void setSnakeGameProperites(SnakeGame sg) {

        isStarted = sg.isStarted();
        isFailed1 = sg.isFailed1();
        isFailed2 = sg.isFailed2();

        len1 = sg.getLen1();
        len2 = sg.getLen2();

        score1 = sg.getScore1();
        score2 = sg.getScore2();

        speed = sg.getSpeed();

        snake1x = sg.getSnake1x();
        snake1y = sg.getSnake1y();
        len1 = sg.getLen1();
        direction1 = sg.getDirection1();

        snake2x = sg.getSnake2x();
        snake2y = sg.getSnake2y();
        len2 = sg.getLen2();
        direction2 = sg.getDirection2();

        food1x = sg.getFood1x();
        food1y = sg.getFood1y();
        food2x = sg.getFood2x();
        food2y = sg.getFood2y();

        snake1FromHole1 = sg.isSnake1FromHole1();
        snake1FromHole2 = sg.isSnake1FromHole2();
        snake2FromHole1 = sg.isSnake2FromHole1();
        snake2FromHole2 = sg.isSnake2FromHole2();

        snake1InHole = sg.isSnake1InHole();
        snake2InHole = sg.isSnake2InHole();

        repaint();
    }

    @Override
    public void paint(Graphics g) {

        //System.out.println("user" + client.getHallPanel().getUser().getUserId()+1 + " paint():food1x=" + food1x + " food1y="+ food1y + " food2x=" + food2x + " food2y=" + food2y);
        this.setBackground(Color.black);
        g.fillRect(25, 75, 850, 600);
        title.paintIcon(this, g, 25, 11);

        //Snake1 Head
        if (len1 > 0) {
            if(direction1.equals("R")) {
                right1.paintIcon(this, g, snake1x[0], snake1y[0]);
            }
            else if(direction1.equals("L")) {
                left1.paintIcon(this, g, snake1x[0], snake1y[0]);
            }
            else if(direction1.equals("U")) {
                up1.paintIcon(this, g, snake1x[0], snake1y[0]);
            }
            else if(direction1.equals("D")) {
                down1.paintIcon(this, g, snake1x[0], snake1y[0]);
            }
        }

        //Snake2 Head
        if (len2 > 0) {
            if(direction2.equals("R")) {
                right2.paintIcon(this, g, snake2x[0], snake2y[0]);
            }
            else if(direction2.equals("L")) {
                left2.paintIcon(this, g, snake2x[0], snake2y[0]);
            }
            else if(direction2.equals("U")) {
                up2.paintIcon(this, g, snake2x[0], snake2y[0]);
            }
            else if(direction2.equals("D")) {
                down2.paintIcon(this, g, snake2x[0], snake2y[0]);
            }
        }

        //Snake1 Body
        for(int i=1; i<len1; i++) {
            body1.paintIcon(this, g, snake1x[i], snake1y[i]);
        }

        //Snake2 Body
        for(int i=1; i<len2; i++) {
            body2.paintIcon(this, g, snake2x[i], snake2y[i]);
        }

        //Draw Walls
        for(int i=0; i<wall1Len; i++) {
            wall1.paintIcon(this, g, wall1x[i], wall1y[i]);
        }

        for(int i=0; i<wall2Len; i++) {
            wall2.paintIcon(this, g, wall2x[i], wall2y[i]);
        }

        //Pause Page
        if(!isStarted) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Press Space to Start/Pause", 250, 300);
        }

        //Snake in hole Page
        if (snake1InHole) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Snake1 in Hole", 330, 300);
        }
        if (snake2InHole) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Snake2 in Hole", 330, 300);
        }

        //Draw food
        food1.paintIcon(this, g, food1x, food1y);
        food2.paintIcon(this, g, food2x, food2y);

        //Draw hole
        hole1.paintIcon(this, g, hole1x, hole1y);
        hole2.paintIcon(this, g, hole2x, hole2y);

        //Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Player1 Score: " + score1, 725, 35);
        g.drawString("Player2 Score: " + score2, 725, 55);

        //Draw speed
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 23));
        g.drawString("speed: " + (7-(speed/60)), 35, 35);

        //Fail Page
        if(isFailed1 && isFailed2) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            if(score1>score2) {
                if(score1>maxScore) {
                    maxScore = score1;
                }
                String s = String.valueOf(maxScore);
                g.drawString("Player1 Wins, Press Space to Restart", 205, 300);
                g.drawString("Highest Score:", 205, 340);
                g.drawString(s, 440, 340);
            }
            if(score1==score2) {
                if(score1>maxScore) {
                    maxScore = score1;
                }
                String s = String.valueOf(maxScore);
                g.drawString("Ties, Press Space to Restart", 240, 300);
                g.drawString("Highest Score:", 240, 340);
                g.drawString(s, 240, 340);
            }
            if(score1<score2) {
                if(score2>maxScore) {
                    maxScore = score2;
                }
                String s = String.valueOf(maxScore);
                g.drawString("Player2 Wins, Press Space to Restart", 205, 300);
                g.drawString("Highest Score:", 240, 340);
                g.drawString(s, 240, 340);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode==KeyEvent.VK_SPACE) {
            if (isFailed1 && isFailed2) {
                /** 重新開始*/
                int userId = client.getHallPanel().getUser().getUserId();
                client.sendMsg(PacketMessage.RESTART + ":");
                System.out.println("玩家" + (userId+1) + "重新開始");
            } else {
                if (isStarted) {
                    /** 暫停遊戲，向服務器發送暫停請求 */
                    int userId = client.getHallPanel().getUser().getUserId();
                    client.sendMsg(PacketMessage.PAUSE + ":" + userId);
                    System.out.println("玩家" + (userId+1) + "發出暫停請求");
                } else {
                    /** 繼續暫停的遊戲，向服務器發送繼續遊戲請求*/
                    int userId = client.getHallPanel().getUser().getUserId();
                    client.sendMsg(PacketMessage.CANCEL_PAUSE + ":" + userId);
                    System.out.println("玩家" + (userId+1) + "取消暫停");
                }
                isStarted = !isStarted;
                repaint();
            }
        } else if (keyCode==KeyEvent.VK_UP && !direction1.equals("D")) {
            direction1 = "U";
        } else if (keyCode==KeyEvent.VK_DOWN && !direction1.equals("U")) {
            direction1 = "D";
        } else if (keyCode==KeyEvent.VK_RIGHT && !direction1.equals("L")) {
            direction1 = "R";
        } else if (keyCode==KeyEvent.VK_LEFT && !direction1.equals("R")) {
            direction1 = "L";
        } else if (keyCode==KeyEvent.VK_W && !direction2.equals("D")) {
            direction2 = "U";
        } else if (keyCode==KeyEvent.VK_S && !direction2.equals("U")) {
            direction2 = "D";
        } else if (keyCode==KeyEvent.VK_D && !direction2.equals("L")) {
            direction2 = "R";
        } else if (keyCode==KeyEvent.VK_A && !direction2.equals("R")) {
            direction2 = "L";
        }

        if (keyCode==KeyEvent.VK_U) {
            client.sendMsg(PacketMessage.CHANGE_SPEED + ":" + "UP");
        } else if (keyCode==KeyEvent.VK_I) {
            client.sendMsg(PacketMessage.CHANGE_SPEED + ":" + "DOWN");
        } else {
            /** 向服務器發送蛇的方向請求*/
            int userId = client.getHallPanel().getUser().getUserId();
            if (userId == 0) {
                client.sendMsg(PacketMessage.MOVE_REQUEST + ":" + userId + ":" + direction1);
            } else if (userId == 1) {
                client.sendMsg(PacketMessage.MOVE_REQUEST + ":" + userId + ":" + direction2);
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}