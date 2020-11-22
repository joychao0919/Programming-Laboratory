package server;

import client.PacketMessage;
import client.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * �A�Ⱦ��A�B�z�Ȥ�ݡB�A�Ⱦ��������q�H
 */
public class Server {

    // �Τ�C��
    private static List<User> userList = new ArrayList();
    // ��e�w�n�����Τ�ƶq
    private static int userNumber = 0;

    // �w�ǳƶ}�l�C�����Τ�
    private List<User> userReadyList = new ArrayList<User>();

    // �A�Ⱥݳg�Y�D�C��
    private SnakeGame snakeGame;

    public Server() {}

    public static void main(String[] args) {
        Server server = new Server();
        server.waitConnect();
    }

    /**
     * ���ݨӦ۫Ȥ�ݪ��s���ШD
     * �̦h������ӫȤ�ݪ��s��
     */
    public void waitConnect() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            /** �Y�Ȥ�ݻP�A�Ⱦ��s�����\�A�h�}�Ҥ@�ӥΤ�u�{�B�z�Ȥ�ݻP�A�Ⱦ������������q�H*/
            while (userNumber < 2) {
                // ���ݳs��
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                User user = new User(dis, dos);
                user.setUserId(userNumber);
                userList.add(user);
                userNumber++;
                // �}�ҥΤ�u�{
                UserThread userThread = new UserThread(user);
                userThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �Τ�u�{�A�B�z�Ȥ�ݻP�A�Ⱦ������������q�H
     */
    class UserThread extends Thread {

        private User user;
        private DataInputStream dis;
        private DataOutputStream dos;

        public UserThread(User user) {
            super();
            this.user = user;
            dis = user.getDis();
            dos = user.getDos();
        }

        public void run() {

            String recivedMsg; // �������H��
            int pos; //�H�������I��m
            String command; //PocketMessage�ت��H���R�O�s��

            /** ���ݱ����o�e���ӥΤ�u�{���H���A�óB�z�ӫH��*/
            try {
                while (true) {
                    recivedMsg = dis.readUTF();
                    pos = recivedMsg.indexOf(":");
                    command = recivedMsg.substring(0, pos);
                    // �n���H��
                    if (command.equals(PacketMessage.LOGIN)) {
                        handleLoginMsg(recivedMsg.substring(pos+1));
                    }
                    // �[�J�C���H��
                    else if (command.equals(PacketMessage.JOIN_GAME)) {
                        handleJoinGameMsg(recivedMsg.substring(pos+1));
                    }
                    // �B�z��V��D�����ʤ�V�ШD
                    else if (command.equals(PacketMessage.MOVE_REQUEST)) {
                        handleMoveRequestMsg(recivedMsg.substring(pos+1));
                    }
                    // �B�z�Ȱ��ШD
                    else if (command.equals(PacketMessage.PAUSE)) {
                        handlePauseMsg(recivedMsg.substring(pos+1));
                    }
                    // �Ȱ������ШD
                    else if (command.equals(PacketMessage.CANCEL_PAUSE)) {
                        handleCancelPauseMsg(recivedMsg.substring(pos+1));
                    }
                    // ���s�}�l�C���ШD
                    else if (command.equals(PacketMessage.RESTART)) {
                        handleRestartMsg();
                    }
                    // �ո`�t�׽ШD
                    else if (command.equals(PacketMessage.CHANGE_SPEED)) {
                        handleChangeSpeedMsg(recivedMsg.substring(pos+1));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * ���Y�ӫȤ�ݵo�e�H��
         * @param sbdos
         * @param msg
         */
        private void sendMsgToSomebody(DataOutputStream sbdos, String msg) {
            try {
                sbdos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * �o�e�������Ҧ��Ȥ��
         * @param msg
         */
        private void sendMsgToAllClient(String msg) {
            try {
                for (User u : userList) {
                    u.getDos().writeUTF(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * �o�e�ǦC�ƫ᪺��H���Ҧ��Ȥ��
         * @param obj
         */
        private void sendObjectToAllClient(Object obj) {
            try {
                for (User u : userList) {
                    u.getObjos().writeObject(obj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * �Τ�n����i�J�ж��A�d�ݩж��H���]���O�_�w�ǳƶ}�l�C���^
         * @param userName
         */
        private synchronized void handleLoginMsg(String userName) {
            user.setUserName(userName);
            userList.get(userNumber-1).setUserName(userName);
            // �d�ݨ�L�ΤḨ�y�H��
            for (User u : userReadyList) {
                try {
                    // �V���Τ�o�e��L�Τ᪺���y�H��
                    dos.writeUTF(PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId()+1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * �B�z�Τ�ǳƶ}�l�C��������
         * @param userName
         */
        private void handleJoinGameMsg(String userName) {
            System.out.println("handleJoinGameMsg�G" + userName);
            int num = userReadyList.size();
            // �Ĥ@�ӥΤ�ǳƹC���A�h�q���Ҧ��Ȥ����ܥΤḨ�y�H��
            if (num == 0) {
                for (User u : userList) {
                    if (u.getUserName().equals(userName)) {
                        userReadyList.add(u);
                        String updateHallMsg = PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId();
                        sendMsgToAllClient(updateHallMsg);
                    }
                }
            }
            // �ĤG�ӥΤ�ǳƹC���A�h�q���Ҧ��Ȥ�ݶ}�l�C��
            else if (num == 1) {
                for (User u : userList) {
                    if (u.getUserName().equals(userName)) {
                        System.out.println("server handleJoinGameMsg num=1");
                        userReadyList.add(u);
                        String updateHallMsg = PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId();
                        sendMsgToAllClient(updateHallMsg);
                        // ��l�ƹC���ƾ�,�N��ӥΤ᪺��J��X�y�j�w��C����, �õo�e���Ҧ��Ȥ��
                        snakeGame = new SnakeGame(userList.get(0), userList.get(1));
                        String startGameMsg = PacketMessage.START_GAME + ":";
                        sendMsgToAllClient(startGameMsg);
                        sendObjectToAllClient(snakeGame);
                    }
                }
            } else {
                try {
                    user.getDos().writeUTF(PacketMessage.ERROR + ":" + "���b�C�����A�L�k�i�J");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * �B�z���U��V��ɡA�D�����ʤ�V�ШD
         * @param directionMsg
         */
        private void handleMoveRequestMsg(String directionMsg) {
            StringTokenizer st = new StringTokenizer(directionMsg, ":");
            int userId = Integer.parseInt(st.nextToken());
            String direction = st.nextToken();
            /** �P�_����ШD�Ӧۭ��Ӫ��a */
            if (userId == 0) {
                snakeGame.setDirection1(direction);
            } else if (userId == 1) {
                snakeGame.setDirection2(direction);
            }
        }

        /**
         * �B�z�Ȥ�ݪ��Ȱ��ШD
         * @param userId
         */
        private void handlePauseMsg(String userId) {
            snakeGame.setStarted(false);
            int id = Integer.parseInt(userId);
            // ��o�Ȱ�����L�Τ�
            if (id == 0) {
                sendMsgToSomebody(snakeGame.getPlayer2().getDos(), PacketMessage.PAUSE + ":");
            } else if (id == 1) {
                sendMsgToSomebody(snakeGame.getPlayer1().getDos(), PacketMessage.PAUSE + ":");
            }
        }

        /**
         * �B�z�Ȥ�ݪ��Ȱ������ШD
         * @param userId
         */
        private void handleCancelPauseMsg(String userId) {
            snakeGame.setStarted(true);
            snakeGame.getTimer().start();
            int id = Integer.parseInt(userId);
            // ��o�Ȱ������H������L�Τ�
            if (id == 0) {
                sendMsgToSomebody(snakeGame.getPlayer2().getDos(), PacketMessage.CANCEL_PAUSE + ":");
            } else if (id == 1) {
                sendMsgToSomebody(snakeGame.getPlayer1().getDos(), PacketMessage.CANCEL_PAUSE + ":");
            }
        }

        /**
         * �B�z���s�}�l�C���ШD
         */
        private void handleRestartMsg() {
            snakeGame = new SnakeGame(userList.get(0), userList.get(1));
            sendMsgToAllClient(PacketMessage.RESTART + ":");
            sendObjectToAllClient(snakeGame);
        }

        /**
         * �B�z�ոѳt�׽ШD
         * @param adjust
         */
        private void handleChangeSpeedMsg(String adjust) {
            if (adjust.equals("UP")) {
                snakeGame.setSpeed(snakeGame.getSpeed() - 60);
            } else if (adjust.equals("DOWN")) {
                snakeGame.setSpeed(snakeGame.getSpeed() + 60);
            }
        }
    }


}
