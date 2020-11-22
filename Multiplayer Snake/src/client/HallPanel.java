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
     * �Ыةж��ɭ�
     */
    public void createHallPanel() {
        hallFrame = new JFrame("�C���ж�");
        hallFrame.setLayout(new GridLayout(3, 1));
        // ��e�n���Τ�H��
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 1));
        myInfo = new JLabel();
        myInfo.setText("��e�n���Τ�G" + user.getUserName());
        infoPanel.add(myInfo);
        // �ж����y�H����
        JPanel desk = new JPanel();
        desk.setLayout(new GridLayout(1, 2));
        player_1 = new JLabel();
        player_2 = new JLabel();
        // ���a1���y�H��
        player_1.setText("���a1�����y");
        player_1.setHorizontalAlignment(SwingConstants.CENTER);
        // ���a2���y�H��
        player_2.setText("���a2�����y");
        player_2.setHorizontalAlignment(SwingConstants.CENTER);

        desk.add(player_1);
        desk.add(player_2);

        // �}�l�C�����s
        JPanel startPanel = new JPanel();
        startBtn = new JButton("�}�l");
        startBtn.setActionCommand("start");
        startPanel.add(startBtn);
        // ��ť�n�����s
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
            startBtn.setText("�w�ǳ�");
            startBtn.setEnabled(false);
        }
    }

}
