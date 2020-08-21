package ru.toir.mobile.multi.rfid;

public class RfidDriverMsg {
    public static final int TYPE_TAG = 0;
    public static final int TYPE_LOGIN = 1;

    private int type;
    private String tagId;
    private String login;
    private String password;

    public static RfidDriverMsg tagMsg() {
        RfidDriverMsg msg = new RfidDriverMsg();
        msg.setType(TYPE_TAG);
        return msg;
    }

    public static RfidDriverMsg tagMsg(String tagId) {
        RfidDriverMsg msg = RfidDriverMsg.tagMsg();
        msg.setType(TYPE_TAG);
        msg.setTagId(tagId);
        return msg;
    }

    public static RfidDriverMsg loginMsg() {
        RfidDriverMsg msg = new RfidDriverMsg();
        msg.setType(TYPE_LOGIN);
        return msg;
    }

    public static RfidDriverMsg loginMsg(String login, String password) {
        RfidDriverMsg msg = RfidDriverMsg.loginMsg();
        msg.setType(TYPE_LOGIN);
        msg.setLogin(login);
        msg.setPassword(password);
        return msg;
    }

    public boolean isTag() {
        return type == TYPE_TAG;
    }

    public boolean isLogin() {
        return type == TYPE_LOGIN;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTagId() {
        return type == TYPE_TAG ? tagId.substring(4) : tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
