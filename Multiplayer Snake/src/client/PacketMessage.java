package client;

import org.omg.CORBA.PUBLIC_MEMBER;

public interface PacketMessage {

    public String LOGIN = "00";
    public String HALL_INFO = "01";
    public String JOIN_GAME = "02";
    public String START_GAME = "03";
    public String UPDATE_HALL = "04";
    public String MOVE_MSG = "05";
    public String MOVE_REQUEST = "06";
    public String RESTART = "07";
    public String PAUSE = "08";
    public String CANCEL_PAUSE = "09";
    public String CHANGE_SPEED = "10";
    public String ERROR = "12";
}
