package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel implements ActionListener {

    JFrame loginFrame;

    private JTextField userName;
    private JTextField serverIP;

    private JButton connectBtn;

    private Client client = new Client();

    public LoginPanel() {}

    public static void main(String[] args) {
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.createLoginPanel();
    }

    /**
     * �Ыصn�����O
     */
    public void createLoginPanel() {
        loginFrame = new JFrame("LOGIN");
        loginFrame.setLayout(new GridLayout(3, 1));
        // �n���H����
        JPanel loginInfo = new JPanel();
        loginInfo.setLayout(new BorderLayout(2, 1));
        // �n���H���إ���
        JPanel loginInfo_left = new JPanel();
        loginInfo_left.setLayout(new GridLayout(2, 1));
        loginInfo_left.add(new JLabel(" �Τ�W :"));
        loginInfo_left.add(new JLabel("�A�Ⱦ�IP :"));
        // �n���H���إk��
        JPanel loginInfo_right = new JPanel();
        loginInfo_right.setLayout(new GridLayout(2, 1));
        userName = new JTextField("myName");
        serverIP = new JTextField("localhost");
        loginInfo_right.add(userName);
        loginInfo_right.add(serverIP);

        loginInfo.add(loginInfo_left, "West");
        loginInfo.add(loginInfo_right, "Center");
        // �n�����s
        JPanel loginBtn = new JPanel();
        connectBtn = new JButton("�n��");
        connectBtn.setActionCommand("login");
        loginBtn.add(connectBtn);
        // ��ť�n�����s
        connectBtn.addActionListener(this);

        loginFrame.getContentPane().add(loginInfo);
        loginFrame.getContentPane().add(loginBtn);
        loginFrame.setSize(350, 220);
        loginFrame.setLocation(400, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        /** �Y�I���F�n�����s*/
        if (command.equals("login")) {
            // ����g�Τ�W
            if (userName.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "�S����g�Τ�W", "���~����", JOptionPane.ERROR_MESSAGE);
            }
            // ����g�A�Ⱦ�IP�a�}
            else if (serverIP.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "�S����g�A�Ⱦ��a�}", "���~����", JOptionPane.ERROR_MESSAGE);
            } else {
                /** �Y�s���A�Ⱦ����\*/
                if (client.connect(serverIP.getText())) {
                    // �����n����
                    loginFrame.setVisible(false);
                    // �Ыةж��ɭ�
                    HallPanel hallPanel = new HallPanel();
                    hallPanel.setClient(client);
                    hallPanel.setUser(new User(userName.getText()));
                    hallPanel.createHallPanel();
                    client.setHallPanel(hallPanel);
                    // �V�A�Ⱦ��o�e�Τ�n���H��
                    String userInfo = PacketMessage.LOGIN + ":" + userName.getText();
                    client.sendMsg(userInfo);
                } else {
                    JOptionPane.showMessageDialog(null, "�n������", "���~����", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

}
