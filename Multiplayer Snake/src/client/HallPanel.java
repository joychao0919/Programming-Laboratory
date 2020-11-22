package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HallPanel implements ActionListener {

    private JFrame hallFrame;

    private JLabel myInfo;
    private JLabel player_1;
    private JLabel player_2;

    private JButton startBtn;

    private Client client;
    private User user;

    /**
     * 創建房間界面
     */
    public void createHallPanel() {
        hallFrame = new JFrame("遊戲房間");
        hallFrame.setLayout(new GridLayout(3, 1));
        // 當前登錄用戶信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 1));
        myInfo = new JLabel();
        myInfo.setText("當前登錄用戶：" + user.getUserName());
        infoPanel.add(myInfo);
        // 房間落座信息框
        JPanel desk = new JPanel();
        desk.setLayout(new GridLayout(1, 2));
        player_1 = new JLabel();
        player_2 = new JLabel();
        // 玩家1落座信息
        player_1.setText("玩家1未落座");
        player_1.setHorizontalAlignment(SwingConstants.CENTER);
        // 玩家2落座信息
        player_2.setText("玩家2未落座");
        player_2.setHorizontalAlignment(SwingConstants.CENTER);

        desk.add(player_1);
        desk.add(player_2);

        // 開始遊戲按鈕
        JPanel startPanel = new JPanel();
        startBtn = new JButton("開始");
        startBtn.setActionCommand("start");
        startPanel.add(startBtn);
        // 監聽登錄按鈕
        startBtn.addActionListener(this);

        hallFrame.getContentPane().add(infoPanel);
        hallFrame.getContentPane().add(desk);
        hallFrame.getContentPane().add(startPanel);

        hallFrame.setSize(350, 220);
        hallFrame.setLocation(400, 200);
        hallFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hallFrame.setVisible(true);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public JFrame getHallFrame() {
        return hallFrame;
    }

    public void setPlayer_1(String text) {
        player_1.setText(text);
    }

    public void setPlayer_2(String text) {
        player_2.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("start")) {
            System.out.println("start press");
            client.sendMsg(PacketMessage.JOIN_GAME + ":" + user.getUserName());
            startBtn.setText("已準備");
            startBtn.setEnabled(false);
        }
    }

}
