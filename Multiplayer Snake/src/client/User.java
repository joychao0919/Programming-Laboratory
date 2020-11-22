package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class User {

    private int userId;
    private String userName;

    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectOutputStream objos;

    public User() {}

    public User(DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        try {
            objos = new ObjectOutputStream(this.dos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public ObjectOutputStream getObjos() {
        return objos;
    }

    public void setObjos(ObjectOutputStream objos) {
        this.objos = objos;
    }

}
