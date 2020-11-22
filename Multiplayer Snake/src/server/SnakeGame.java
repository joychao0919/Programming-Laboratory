package server;

import client.PacketMessage;
import client.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnakeGame implements ActionListener, Serializable, Cloneable {

    private transient User player1;
    private transient User player2;

    private boolean online1;
    private boolean online2;

    // Snake's Position, Length, Direction
    int[] snake1x = new int[100];
    int[] snake1y = new int[100];
    int len1;
    String direction1;

    int[] snake2x = new int[100];
    int[] snake2y = new int[100];
    int len2;
    String direction2;

    // while sanke in hole, save the snake's length temporarily
    private int tmp_len1;
    private int tmp_len2;

    // Snake's stuats in Hole
    boolean snake1EnteringHole = false;
    boolean snake1InHole = false;
    boolean snake1Waiting = false;
    boolean snake1GoingOutHole = false;
    boolean snake2EnteringHole = false;
    boolean snake2InHole = false;
    boolean snake2Waiting = false;
    boolean snake2GoingOutHole = false;

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
    int speed = 180;

    //From which hole
    boolean snake1FromHole1;
    boolean snake1FromHole2;
    boolean snake2FromHole1;
    boolean snake2FromHole2;

    //Timer
    Timer timer = new Timer(speed, this);

    public SnakeGame() {
//        initSnake();
//        timer.start();
    }

    public SnakeGame(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;
        initSnake();
    }

    public User getPlayer1() {
        return player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isFailed1() {
        return isFailed1;
    }

    public boolean isFailed2() {
        return isFailed2;
    }

    public int[] getSnake1x() {
        return snake1x;
    }

    public int[] getSnake1y() {
        return snake1y;
    }

    public int getLen1() {
        return len1;
    }

    public String getDirection1() {
        return direction1;
    }

    public int[] getSnake2x() {
        return snake2x;
    }

    public int[] getSnake2y() {
        return snake2y;
    }

    public int getLen2() {
        return len2;
    }

    public String getDirection2() {
        return direction2;
    }

    public int getFood1x() {
        return food1x;
    }

    public int getFood1y() {
        return food1y;
    }

    public int getFood2x() {
        return food2x;
    }

    public int getFood2y() {
        return food2y;
    }

    public int getHole1x() {
        return hole1x;
    }

    public int getHole1y() {
        return hole1y;
    }

    public int getHole2x() {
        return hole2x;
    }

    public int getHole2y() {
        return hole2y;
    }

    public int[] getWall1x() {
        return wall1x;
    }

    public int[] getWall1y() {
        return wall1y;
    }

    public int[] getWall2x() {
        return wall2x;
    }

    public int[] getWall2y() {
        return wall2y;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isSnake1FromHole1() {
        return snake1FromHole1;
    }

    public boolean isSnake1FromHole2() {
        return snake1FromHole2;
    }

    public boolean isSnake2FromHole1() {
        return snake2FromHole1;
    }

    public boolean isSnake2FromHole2() {
        return snake2FromHole2;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean isSnake1InHole() {
        return snake1InHole;
    }

    public boolean isSnake2InHole() {
        return snake2InHole;
    }

    public void setOnline1(boolean online1) {
        this.online1 = online1;
    }

    public void setOnline2(boolean online2) {
        this.online2 = online2;
    }

    public void setSnake1x(int[] snake1x) {
        this.snake1x = snake1x;
    }

    public void setSnake1y(int[] snake1y) {
        this.snake1y = snake1y;
    }

    public void setSnake2x(int[] snake2x) {
        this.snake2x = snake2x;
    }

    public void setSnake2y(int[] snake2y) {
        this.snake2y = snake2y;
    }

    public void setDirection1(String direction1) {
        this.direction1 = direction1;
    }

    public void setDirection2(String direction2) {
        this.direction2 = direction2;
    }

    public void setSpeed(int speed) {
        if (speed >=60 && speed <= 360) {
            this.speed = speed;
            timer.setDelay(speed);
        }
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    //Initialize the game
    public void initSnake() {
        isStarted = true;
        isFailed1 = false;
        isFailed2 = false;
        online1 = true;
        online2 = true;
        len1 = 2;
        len2 = 2;
        score1 = 0;
        score2 = 0;
        maxScore = 0;
        speed = 180;
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

        Random r = new Random();
        food1x = r.nextInt(32)*25+50;
        food1y = r.nextInt(22)*25+100;
        food2x = r.nextInt(32)*25+50;
        food2y = r.nextInt(22)*25+100;
        wall1x[0] = r.nextInt(16)*25+150;
        wall1y[0] = r.nextInt(12)*25+150;
        wall2x[0] = r.nextInt(16)*25+150;
        wall2y[0] = r.nextInt(12)*25+150;
        hole1x = r.nextInt(32)*25+50;
        hole1y = r.nextInt(22)*25+100;
        hole2x = r.nextInt(32)*25+50;
        hole2y = r.nextInt(22)*25+100;
        snake1FromHole1 = false;
        snake1FromHole2 = false;
        snake2FromHole1 = false;
        snake2FromHole2 = false;
        for(int i=0; i<wall1Len; i++) {
            wall1x[i+1] = wall1x[i]+25;
            wall1y[i] = wall1y[0];
        }
        for(int i=0; i<wall2Len; i++) {
            wall2y[i+1] = wall2y[i]+25;
            wall2x[i] = wall2x[0];
        }
        timer.start();
        holeTest();
        foodTest();
    }

    /*
     * 1.重新定鬧鐘
     * 2.蛇移動
     * 3.重畫
     * */
    int count = 0;
    @Override
    public void actionPerformed(ActionEvent e) {

        if (isFailed1 && isFailed2) {
            isStarted = false;
            timer.stop();
            return;
        }

        snakeMove();

        eatFood();

        againstWall();

        snakeIntoHole();

        //判斷遊戲失敗
        for(int i=1; i<len1; i++) {
            if(snake1x[0]==snake1x[i] && snake1y[0]==snake1y[i]) {
                isFailed1 = true;
            }
            if(snake2x[0]==snake2x[i] && snake2y[0]==snake2y[i]) {
                isFailed2 = true;
            }
            if(snake1x[0]==snake2x[i] && snake1y[0]==snake2y[i]) {
                isFailed1 = true;
            }
            if(snake2x[0]==snake1x[i] && snake2y[0]==snake1y[i]) {
                isFailed2 = true;
            }
            if(snake1x[0]==snake2x[0] && snake1y[0]==snake2y[0]) {
                isFailed1 = true;
                isFailed2 = true;
            }
        }

        sendRepaintMsg();
    }

    public void snakeMove() {
        // 移動身體1
        if(isStarted && !isFailed1 && !snake1EnteringHole && !snake1InHole) {
            for(int i=len1; i>0; i--) {
                snake1x[i] = snake1x[i-1];
                snake1y[i] = snake1y[i-1];
            }

            if(direction1.equals("R")) {
                snake1x[0] += 25;
                if(snake1x[0]>825) {
                    isFailed1 = true;
                }
            }
            else if(direction1.equals("L")) {
                snake1x[0] -= 25;
                if(snake1x[0]<50) {
                    isFailed1 = true;
                }
            }
            else if(direction1.equals("U")) {
                snake1y[0] -= 25;
                if(snake1y[0]<100) {
                    isFailed1 = true;
                }
            }
            else if(direction1.equals("D")) {
                snake1y[0] += 25;
                if(snake1y[0]>625) {
                    isFailed1 = true;
                }
            }
        }
        // 移動身體2
        if(isStarted && !isFailed2 && !snake2EnteringHole && !snake2InHole) {
            for(int i=len2; i>0; i--) {
                snake2x[i] = snake2x[i-1];
                snake2y[i] = snake2y[i-1];
            }

            if(direction2.equals("R")) {
                snake2x[0] += 25;
                if(snake2x[0]>825) {
                    isFailed2 = true;
                }
            }
            else if(direction2.equals("L")) {
                snake2x[0] -= 25;
                if(snake2x[0]<50) {
                    isFailed2 = true;
                }
            }
            else if(direction2.equals("U")) {
                snake2y[0] -= 25;
                if(snake2y[0]<100) {
                    isFailed2 = true;
                }
            }
            else if(direction2.equals("D")) {
                snake2y[0] += 25;
                if(snake2y[0]>625) {
                    isFailed2 = true;
                }
            }
        }
    }

    public void eatFood() {
        if(snake1x[0]==food1x && snake1y[0]==food1y) {
            len1++;
            score1++;
            foodNum--;
            food1x = -100;
            food1y = -100;
        }
        if(snake1x[0]==food2x && snake1y[0]==food2y) {
            len1++;
            score1++;
            foodNum--;
            food2x = -100;
            food2y = -100;
        }
        if(snake2x[0]==food1x && snake2y[0]==food1y) {
            len2++;
            score2++;
            foodNum--;
            food1x = -100;
            food1y = -100;
        }
        if(snake2x[0]==food2x && snake2y[0]==food2y) {
            len2++;
            score2++;
            foodNum--;
            food2x = -100;
            food2y = -100;
        }
        if(foodNum==0) {
            System.out.println("SnakeGame foodAgain()");
            foodNum = -1; // 置為-1，防止再次進入該判斷
            // 兩秒後生成蛋
            ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    foodAgain();
                }
            } , 2 , TimeUnit.SECONDS);
            executor.shutdown();
        }
    }

    public void againstWall() {
        for(int i=0; i<wall1Len; i++) {
            if(snake1x[0]==wall1x[i] && snake1y[0]==wall1y[i]) {
                isFailed1 = true;
            }
            if(snake2x[0]==wall1x[i] && snake2y[0]==wall1y[i]) {
                isFailed2 = true;
            }
        }

        for(int i=0; i<wall2Len; i++) {
            if(snake1x[0]==wall2x[i] && snake1y[0]==wall2y[i]) {
                isFailed1 = true;
            }
            if(snake2x[0]==wall2x[i] && snake2y[0]==wall2y[i]) {
                isFailed2 = true;
            }
        }
    }

    public void snakeIntoHole() {
        // 蛇頭剛進洞時，保存蛇身信息
        if(snake1x[0]==hole1x && snake1y[0]==hole1y && snake1FromHole2==false && !snake1EnteringHole && !snake1InHole) {
            snake1x[0] = hole2x;
            snake1y[0] = hole2y;
            snake1FromHole1 = true;
            tmp_len1 = len1;
            snake1EnteringHole = true;
        }
        else if(snake1x[0]==hole2x && snake1y[0]==hole2y && snake1FromHole1==false && !snake1EnteringHole && !snake1InHole) {
            snake1x[0] = hole1x;
            snake1y[0] = hole1y;
            snake1FromHole2 = true;
            tmp_len1 = len1;
            snake1EnteringHole = true;
        }
        else {
            snake1FromHole1 = false;
            snake1FromHole2 = false;
        }

        if(snake2x[0]==hole1x && snake2y[0]==hole1y && snake2FromHole2==false && !snake2EnteringHole && !snake2InHole) {
            snake2x[0] = hole2x;
            snake2y[0] = hole2y;
            snake2FromHole1 = true;
            tmp_len2 = len2;
            snake2EnteringHole = true;
        }
        else if(snake2x[0]==hole2x && snake2y[0]==hole2y && snake2FromHole1==false && !snake2EnteringHole && !snake2InHole) {
            snake2x[0] = hole1x;
            snake2y[0] = hole1y;
            snake2FromHole2 = true;
            tmp_len2 = len2;
            snake2EnteringHole = true;
        }
        else {
            snake2FromHole1 = false;
            snake2FromHole2 = false;
        }

        // 正在進洞
        if (snake1EnteringHole) {
            len1--;
            if (len1 == 0) {
                snake1EnteringHole = false;
                snake1InHole = true;
            }
        } else if (snake2EnteringHole) {
            len2--;
            if (len2 == 0) {
                snake2EnteringHole = false;
                snake2InHole = true;
            }
        }

        // 在洞中
        if (snake1InHole && !snake1Waiting) {
            // 1秒後出洞
            snake1Waiting = true;
            ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    snake1InHole = false;
                    snake1GoingOutHole = true;
                    snake1Waiting = false;
                }
            } , 1 , TimeUnit.SECONDS);
            executor.shutdown();
        } else if (snake2InHole && !snake2Waiting) {
            // 1秒後出洞
            snake2Waiting = true;
            ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    snake2InHole = false;
                    snake2GoingOutHole = true;
                    snake2Waiting = false;
                }
            } , 1 , TimeUnit.SECONDS);
            executor.shutdown();
        }

        // 正在出洞
        if (snake1GoingOutHole) {
            len1++;
            if (tmp_len1 == len1) {
                snake1GoingOutHole = false;
            }
        } else if (snake2GoingOutHole) {
            len2++;
            if (tmp_len2 == len2) {
                snake2GoingOutHole = false;
            }
        }
    }

    //Check if foods are on snakes' body
    public void foodTest() {
        Random r = new Random();
        for(int i=0; i<len1; i++) {
            while(food1x==snake1x[i] && food1y==snake1y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
            while(food2x==snake1x[i] && food2y==snake1y[i]) {
                food2x = r.nextInt(32)*25+50;
                food2y = r.nextInt(22)*25+100;
            }
            while(food1x==snake2x[i] && food1y==snake2y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
            while(food2x==snake2x[i] && food2y==snake2y[i]) {
                food2x = r.nextInt(32)*25+50;
                food2y = r.nextInt(22)*25+100;
            }
        }
        for(int i=0; i<wall1Len; i++) {
            while(food1x==wall1x[i] && food1y==wall1y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
            while(food2x==wall1x[i] && food2y==wall1y[i]) {
                food2x = r.nextInt(32)*25+50;
                food2y = r.nextInt(22)*25+100;
            }
        }
        for(int i=0; i<wall2Len; i++) {
            while(food1x==wall2x[i] && food1y==wall2y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
            while(food2x==wall2x[i] && food2y==wall2y[i]) {
                food2x = r.nextInt(32)*25+50;
                food2y = r.nextInt(22)*25+100;
            }
        }
        while(food1x==food2x && food1y==food2y) {
            food2x = r.nextInt(32)*25+50;
            food2y = r.nextInt(22)*25+100;
        }
        while(food1x==hole1x && food1y==hole1y) {
            food1x = r.nextInt(32)*25+50;
            food1y = r.nextInt(22)*25+100;
        }
        while(food1x==hole2x && food1y==hole2y) {
            food1x = r.nextInt(32)*25+50;
            food1y = r.nextInt(22)*25+100;
        }
        while(food2x==hole1x && food2y==hole1y) {
            food2x = r.nextInt(32)*25+50;
            food2y = r.nextInt(22)*25+100;
        }
        while(food2x==hole2x && food2y==hole2y) {
            food2x = r.nextInt(32)*25+50;
            food2y = r.nextInt(22)*25+100;
        }
    }

    //Check holes' position
    public void holeTest() {
        Random r = new Random();
        for(int i=0; i<len1; i++) {
            while(hole1x==snake1x[i] && hole1y==snake1y[i]) {
                hole1x = r.nextInt(32)*25+50;
                hole1y = r.nextInt(22)*25+100;
            }
            while(hole1x==snake2x[i] && hole1y==snake2y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
            while(hole2x==snake1x[i] && hole2y==snake1y[i]) {
                hole1x = r.nextInt(32)*25+50;
                hole1y = r.nextInt(22)*25+100;
            }
            while(hole2x==snake2x[i] && hole2y==snake2y[i]) {
                food1x = r.nextInt(32)*25+50;
                food1y = r.nextInt(22)*25+100;
            }
        }
        for(int i=0; i<wall1Len; i++) {
            while(hole1x==wall1x[i] && hole1y==wall1y[i]) {
                hole1x = r.nextInt(32)*25+50;
                hole1y = r.nextInt(22)*25+100;
            }
            while(hole2x==wall1x[i] && hole2y==wall1y[i]) {
                hole2x = r.nextInt(32)*25+50;
                hole2y = r.nextInt(22)*25+100;
            }
        }
        for(int i=0; i<wall2Len; i++) {
            while(hole1x==wall2x[i] && hole1y==wall2y[i]) {
                hole1x = r.nextInt(32)*25+50;
                hole1y = r.nextInt(22)*25+100;
            }
            while(hole2x==wall2x[i] && hole2y==wall2y[i]) {
                hole2x = r.nextInt(32)*25+50;
                hole2y = r.nextInt(22)*25+100;
            }
        }
        while(hole1x==hole2x && hole1y==hole2y) {
            hole2x = r.nextInt(32)*25+50;
            hole2y = r.nextInt(22)*25+100;
        }

    }

    public void foodAgain() {
        Random r = new Random();
        food1x = r.nextInt(32)*25+50;
        food1y = r.nextInt(22)*25+100;
        food2x = r.nextInt(32)*25+50;
        food2y = r.nextInt(22)*25+100;
        foodTest();
        foodNum = 2;
    }


    /**
     * 發送遊戲對戰信息，重繪遊戲
     * */
    public void sendRepaintMsg() {

        // 淺覆制
        SnakeGame sg = null;
        try {
            sg = (SnakeGame)this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // 深覆制數組
        sg.setSnake1x(this.snake1x.clone());
        sg.setSnake1y(this.snake1y.clone());
        sg.setSnake2x(this.snake2x.clone());
        sg.setSnake2y(this.snake2y.clone());

        try {
            // 發送遊戲消息
            if (online1) {
                player1.getDos().writeUTF(PacketMessage.MOVE_MSG + ":");
                player1.getObjos().writeObject(sg);
            }
        } catch (IOException e) {
            online1 = false;
            try {
                player1.getDos().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        try {
            // 發送遊戲消息
            if (online2) {
                player2.getDos().writeUTF(PacketMessage.MOVE_MSG + ":");
                player2.getObjos().writeObject(sg);
            }
        } catch (IOException e) {
            online2 = false;
            try {
                player2.getDos().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
