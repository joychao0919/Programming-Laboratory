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
 * �Ȥ�ݡA�t�d�H�������o�γB�z
 * �h�u�{�A�i�P�ɦb�@�ӹq���W���}�h�ӫȤ��
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
     * �q�L�A�Ⱦ�IP�P�A�Ⱦ��s��
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
            // �}�Ҥ@�ӫȤ�ݽu�{
            this.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * �Ȥ�ݽu�{�A�����óB�z�A�Ⱦ��o�Ӫ�����
     * �V�A�Ⱦ��o�e����
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
     * �V�A�Ⱦ��o�e�H��
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
     * �B�z�ó]�m�C���ж����Τ�H��
     * @param hallInfo
     */
    private void handleUpdateHallMsg(String hallInfo) {
        if (hallPanel == null) return;
        StringTokenizer st = new StringTokenizer(hallInfo, ":");
        String userName = st.nextToken();
        String playerNumber = st.nextToken();
        // �]�m�Τ᪺���y�H��
        int num = Integer.parseInt(playerNumber);
        if (num == 0) {
            hallPanel.setPlayer_1("���a1�G" + userName + "�w���y");
        } else {
            hallPanel.setPlayer_2("���a2�G" + userName + "�w���y");
        }
        // �P�_�Τ�W�A�]�m���Ȥ�ݪ��Τ�id
        if (hallPanel.getUser().getUserName().equals(userName)) {
            hallPanel.getUser().setUserId(num);
        }
    }

    /**
     * �}�l�C��
     * @param startGameMsg
     */
    private void handleStartGameMsg(String startGameMsg) {
        // �����ж��ɭ�
        hallPanel.getHallFrame().setVisible(false);
        // �ЫعC���ɭ�
        JFrame f = new JFrame("���a" + (hallPanel.getUser().getUserId() + 1) + ":" + hallPanel.getUser().getUserName());
        f.setBounds(400, 200, 900, 720);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // �q�A�Ⱦ�����C������l�Ƽƾ�
        SnakeGame snakeGame = null;
        try {
            snakeGame = (SnakeGame) objis.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // �ЫعC���ê�l�ƹC���ƾ�
        if (snakeGame != null) {
            snake = new SnakePanel(snakeGame);
            snake.setClient(this);
        }

        f.add(snake);
        f.setVisible(true);
    }

    /**
     * �B�z�A�Ⱦ��o�Ӫ��C�����ʵ��H��
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
     * �B�z�A�Ⱦ���o�L�Ӫ��Ȱ��H��
     */
    private void handlePauseMsg() {
        snake.setStarted(false);
        snake.repaint();
    }

    /**
     * �B�z�A�Ⱦ���o�L�Ӫ��Ȱ������H��
     */
    private void handleCancelPauseMsg() {
        snake.setStarted(true);
        snake.repaint();
    }

    /**
     * �B�z���s�}�l�C���ШD
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
