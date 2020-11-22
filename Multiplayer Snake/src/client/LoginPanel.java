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
     * 創建登錄面板
     */
    public void createLoginPanel() {
        loginFrame = new JFrame("LOGIN");
        loginFrame.setLayout(new GridLayout(3, 1));
        // 登錄信息框
        JPanel loginInfo = new JPanel();
        loginInfo.setLayout(new BorderLayout(2, 1));
        // 登錄信息框左側
        JPanel loginInfo_left = new JPanel();
        loginInfo_left.setLayout(new GridLayout(2, 1));
        loginInfo_left.add(new JLabel(" 用戶名 :"));
        loginInfo_left.add(new JLabel("服務器IP :"));
        // 登錄信息框右側
        JPanel loginInfo_right = new JPanel();
        loginInfo_right.setLayout(new GridLayout(2, 1));
        userName = new JTextField("myName");
        serverIP = new JTextField("localhost");
        loginInfo_right.add(userName);
        loginInfo_right.add(serverIP);

        loginInfo.add(loginInfo_left, "West");
        loginInfo.add(loginInfo_right, "Center");
        // 登錄按鈕
        JPanel loginBtn = new JPanel();
        connectBtn = new JButton("登錄");
        connectBtn.setActionCommand("login");
        loginBtn.add(connectBtn);
        // 監聽登錄按鈕
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
        /** 若點擊了登錄按鈕*/
        if (command.equals("login")) {
            // 未填寫用戶名
            if (userName.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "沒有填寫用戶名", "錯誤提示", JOptionPane.ERROR_MESSAGE);
            }
            // 未填寫服務器IP地址
            else if (serverIP.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "沒有填寫服務器地址", "錯誤提示", JOptionPane.ERROR_MESSAGE);
            } else {
                /** 若連接服務器成功*/
                if (client.connect(serverIP.getText())) {
                    // 關閉登錄框
                    loginFrame.setVisible(false);
                    // 創建房間界面
                    HallPanel hallPanel = new HallPanel();
                    hallPanel.setClient(client);
                    hallPanel.setUser(new User(userName.getText()));
                    hallPanel.createHallPanel();
                    client.setHallPanel(hallPanel);
                    // 向服務器發送用戶登錄信息
                    String userInfo = PacketMessage.LOGIN + ":" + userName.getText();
                    client.sendMsg(userInfo);
                } else {
                    JOptionPane.showMessageDialog(null, "登錄失敗", "錯誤提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

}
