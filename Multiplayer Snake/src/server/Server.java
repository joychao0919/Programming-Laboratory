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
 * 服務器，處理客戶端、服務器之間的通信
 */
public class Server {

    // 用戶列表
    private static List<User> userList = new ArrayList();
    // 當前已登錄的用戶數量
    private static int userNumber = 0;

    // 已準備開始遊戲的用戶
    private List<User> userReadyList = new ArrayList<User>();

    // 服務端貪吃蛇遊戲
    private SnakeGame snakeGame;

    public Server() {}

    public static void main(String[] args) {
        Server server = new Server();
        server.waitConnect();
    }

    /**
     * 等待來自客戶端的連接請求
     * 最多接受兩個客戶端的連接
     */
    public void waitConnect() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            /** 若客戶端與服務器連接成功，則開啟一個用戶線程處理客戶端與服務器之間的消息通信*/
            while (userNumber < 2) {
                // 等待連接
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                User user = new User(dis, dos);
                user.setUserId(userNumber);
                userList.add(user);
                userNumber++;
                // 開啟用戶線程
                UserThread userThread = new UserThread(user);
                userThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用戶線程，處理客戶端與服務器之間的消息通信
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

            String recivedMsg; // 接收的信息
            int pos; //信息分割點位置
            String command; //PocketMessage裏的信息命令編號

            /** 等待接收發送給該用戶線程的信息，並處理該信息*/
            try {
                while (true) {
                    recivedMsg = dis.readUTF();
                    pos = recivedMsg.indexOf(":");
                    command = recivedMsg.substring(0, pos);
                    // 登錄信息
                    if (command.equals(PacketMessage.LOGIN)) {
                        handleLoginMsg(recivedMsg.substring(pos+1));
                    }
                    // 加入遊戲信息
                    else if (command.equals(PacketMessage.JOIN_GAME)) {
                        handleJoinGameMsg(recivedMsg.substring(pos+1));
                    }
                    // 處理方向鍵蛇的移動方向請求
                    else if (command.equals(PacketMessage.MOVE_REQUEST)) {
                        handleMoveRequestMsg(recivedMsg.substring(pos+1));
                    }
                    // 處理暫停請求
                    else if (command.equals(PacketMessage.PAUSE)) {
                        handlePauseMsg(recivedMsg.substring(pos+1));
                    }
                    // 暫停取消請求
                    else if (command.equals(PacketMessage.CANCEL_PAUSE)) {
                        handleCancelPauseMsg(recivedMsg.substring(pos+1));
                    }
                    // 重新開始遊戲請求
                    else if (command.equals(PacketMessage.RESTART)) {
                        handleRestartMsg();
                    }
                    // 調節速度請求
                    else if (command.equals(PacketMessage.CHANGE_SPEED)) {
                        handleChangeSpeedMsg(recivedMsg.substring(pos+1));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 給某個客戶端發送信息
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
         * 發送消息給所有客戶端
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
         * 發送序列化後的對象給所有客戶端
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
         * 用戶登陸後進入房間，查看房間信息（對方是否已準備開始遊戲）
         * @param userName
         */
        private synchronized void handleLoginMsg(String userName) {
            user.setUserName(userName);
            userList.get(userNumber-1).setUserName(userName);
            // 查看其他用戶落座信息
            for (User u : userReadyList) {
                try {
                    // 向本用戶發送其他用戶的落座信息
                    dos.writeUTF(PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId()+1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 處理用戶準備開始遊戲的消息
         * @param userName
         */
        private void handleJoinGameMsg(String userName) {
            System.out.println("handleJoinGameMsg：" + userName);
            int num = userReadyList.size();
            // 第一個用戶準備遊戲，則通知所有客戶端顯示用戶落座信息
            if (num == 0) {
                for (User u : userList) {
                    if (u.getUserName().equals(userName)) {
                        userReadyList.add(u);
                        String updateHallMsg = PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId();
                        sendMsgToAllClient(updateHallMsg);
                    }
                }
            }
            // 第二個用戶準備遊戲，則通知所有客戶端開始遊戲
            else if (num == 1) {
                for (User u : userList) {
                    if (u.getUserName().equals(userName)) {
                        System.out.println("server handleJoinGameMsg num=1");
                        userReadyList.add(u);
                        String updateHallMsg = PacketMessage.UPDATE_HALL + ":" + u.getUserName() + ":" + u.getUserId();
                        sendMsgToAllClient(updateHallMsg);
                        // 初始化遊戲數據,將兩個用戶的輸入輸出流綁定到遊戲中, 並發送給所有客戶端
                        snakeGame = new SnakeGame(userList.get(0), userList.get(1));
                        String startGameMsg = PacketMessage.START_GAME + ":";
                        sendMsgToAllClient(startGameMsg);
                        sendObjectToAllClient(snakeGame);
                    }
                }
            } else {
                try {
                    user.getDos().writeUTF(PacketMessage.ERROR + ":" + "正在遊戲中，無法進入");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 處理按下方向鍵時，蛇的移動方向請求
         * @param directionMsg
         */
        private void handleMoveRequestMsg(String directionMsg) {
            StringTokenizer st = new StringTokenizer(directionMsg, ":");
            int userId = Integer.parseInt(st.nextToken());
            String direction = st.nextToken();
            /** 判斷按鍵請求來自哪個玩家 */
            if (userId == 0) {
                snakeGame.setDirection1(direction);
            } else if (userId == 1) {
                snakeGame.setDirection2(direction);
            }
        }

        /**
         * 處理客戶端的暫停請求
         * @param userId
         */
        private void handlePauseMsg(String userId) {
            snakeGame.setStarted(false);
            int id = Integer.parseInt(userId);
            // 轉發暫停給其他用戶
            if (id == 0) {
                sendMsgToSomebody(snakeGame.getPlayer2().getDos(), PacketMessage.PAUSE + ":");
            } else if (id == 1) {
                sendMsgToSomebody(snakeGame.getPlayer1().getDos(), PacketMessage.PAUSE + ":");
            }
        }

        /**
         * 處理客戶端的暫停取消請求
         * @param userId
         */
        private void handleCancelPauseMsg(String userId) {
            snakeGame.setStarted(true);
            snakeGame.getTimer().start();
            int id = Integer.parseInt(userId);
            // 轉發暫停取消信息給其他用戶
            if (id == 0) {
                sendMsgToSomebody(snakeGame.getPlayer2().getDos(), PacketMessage.CANCEL_PAUSE + ":");
            } else if (id == 1) {
                sendMsgToSomebody(snakeGame.getPlayer1().getDos(), PacketMessage.CANCEL_PAUSE + ":");
            }
        }

        /**
         * 處理重新開始遊戲請求
         */
        private void handleRestartMsg() {
            snakeGame = new SnakeGame(userList.get(0), userList.get(1));
            sendMsgToAllClient(PacketMessage.RESTART + ":");
            sendObjectToAllClient(snakeGame);
        }

        /**
         * 處理調解速度請求
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
