package client;


import server.SnakeGame;

import javax.swing.*;
import javax.xml.bind.SchemaOutputResolver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * 客戶端，負責信息的收發及處理
 * 多線程，可同時在一個電腦上打開多個客戶端
 */
public class Client extends Thread {

    private Socket socket;
    private DataInputStream dis;
    private ObjectInputStream objis;
    private DataOutputStream dos;

    private HallPanel hallPanel;

    private SnakePanel snake;

    public Client() {}

    public void setHallPanel(HallPanel hallPanel) {
        this.hallPanel = hallPanel;
    }

    public HallPanel getHallPanel() {
        return hallPanel;
    }

    /**
     * 通過服務器IP與服務器連接
     * @param serverIP
     * @return boolean
     */
    public boolean connect(String serverIP) {
        Socket socket = null;
        try {
            socket = new Socket(serverIP, 8888);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            objis = new ObjectInputStream(dis);
            // 開啟一個客戶端線程
            this.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 客戶端線程，接收並處理服務器發來的消息
     * 向服務器發送消息
     */
    public void run() {
        while (true) {
            try {
                String recivedMsg = dis.readUTF();
                int pos;
                String command;
                pos = recivedMsg.indexOf(":");
                command = recivedMsg.substring(0, pos);

                if (command.equals(PacketMessage.UPDATE_HALL)) {
                    handleUpdateHallMsg(recivedMsg.substring(pos + 1));
                } else if (command.equals(PacketMessage.START_GAME)) {
                    handleStartGameMsg(recivedMsg.substring(pos + 1));
                } else if (command.equals(PacketMessage.MOVE_MSG)) {
                    handleMoveMsg();
                } else if (command.equals(PacketMessage.PAUSE)) {
                    handlePauseMsg();
                } else if (command.equals(PacketMessage.CANCEL_PAUSE)) {
                    handleCancelPauseMsg();
                } else if (command.equals(PacketMessage.RESTART)) {
                    handleRestartMsg();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向服務器發送信息
     */
    public void sendMsg(String msg) {
        System.out.println("client sendMsg:" + msg);
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 處理並設置遊戲房間的用戶信息
     * @param hallInfo
     */
    private void handleUpdateHallMsg(String hallInfo) {
        if (hallPanel == null) return;
        StringTokenizer st = new StringTokenizer(hallInfo, ":");
        String userName = st.nextToken();
        String playerNumber = st.nextToken();
        // 設置用戶的落座信息
        int num = Integer.parseInt(playerNumber);
        if (num == 0) {
            hallPanel.setPlayer_1("玩家1：" + userName + "已落座");
        } else {
            hallPanel.setPlayer_2("玩家2：" + userName + "已落座");
        }
        // 判斷用戶名，設置本客戶端的用戶id
        if (hallPanel.getUser().getUserName().equals(userName)) {
            hallPanel.getUser().setUserId(num);
        }
    }

    /**
     * 開始遊戲
     * @param startGameMsg
     */
    private void handleStartGameMsg(String startGameMsg) {
        // 關閉房間界面
        hallPanel.getHallFrame().setVisible(false);
        // 創建遊戲界面
        JFrame f = new JFrame("玩家" + (hallPanel.getUser().getUserId() + 1) + ":" + hallPanel.getUser().getUserName());
        f.setBounds(400, 200, 900, 720);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 從服務器獲取遊戲的初始化數據
        SnakeGame snakeGame = null;
        try {
            snakeGame = (SnakeGame) objis.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 創建遊戲並初始化遊戲數據
        if (snakeGame != null) {
            snake = new SnakePanel(snakeGame);
            snake.setClient(this);
        }

        f.add(snake);
        f.setVisible(true);
    }

    /**
     * 處理服務器發來的遊戲移動等信息
     */
    private void handleMoveMsg() {
        try {
            SnakeGame snakeGame = (SnakeGame)objis.readObject();
            snake.setSnakeGameProperites(snakeGame);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 處理服務器轉發過來的暫停信息
     */
    private void handlePauseMsg() {
        snake.setStarted(false);
        snake.repaint();
    }

    /**
     * 處理服務器轉發過來的暫停取消信息
     */
    private void handleCancelPauseMsg() {
        snake.setStarted(true);
        snake.repaint();
    }

    /**
     * 處理重新開始遊戲請求
     */
    private void handleRestartMsg() {
        try {
            SnakeGame snakeGame = (SnakeGame) objis.readObject();
            snake.initSnake(snakeGame);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
