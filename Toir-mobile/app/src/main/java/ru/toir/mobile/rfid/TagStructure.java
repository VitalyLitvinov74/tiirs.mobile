package ru.toir.mobile.rfid;

import ru.toir.mobile.utils.DataUtils;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 23.11.16.
 */

public class TagStructure {

    private static final int OFFSET_UUID = 0;
    private static final int SIZE_UUID = 16;

    private static final int OFFSET_TASKID = OFFSET_UUID + SIZE_UUID;
    private static final int SIZE_TASKID = 4;

    private static final int OFFSET_TASKTYPEID = OFFSET_TASKID + SIZE_TASKID;
    private static final int SIZE_TASKTYPEID = 4;

    private static final int OFFSET_START = OFFSET_TASKTYPEID + SIZE_TASKTYPEID;
    private static final int SIZE_START = 4;

    private static final int OFFSET_END = OFFSET_START + SIZE_START;
    private static final int SIZE_END = 4;

    private static final int OFFSET_STATUS = OFFSET_END + SIZE_END;
    private static final int SIZE_STATUS = 4;

    private static final int OFFSET_VERDICTID = OFFSET_STATUS + SIZE_STATUS;
    private static final int SIZE_VERDICTID = 4;

    private static final int OFFSET_USERID = OFFSET_VERDICTID + SIZE_VERDICTID;
    private static final int SIZE_USERID = 4;

    private static final int OFFSET_EQUIPMENTSTATUSID = OFFSET_USERID + SIZE_USERID;
    private static final int SIZE_EQUIPMENTSTATUSID = 4;

    private static final int OFFSET_PHONE = OFFSET_EQUIPMENTSTATUSID + SIZE_EQUIPMENTSTATUSID;
    private static final int SIZE_PHONE = 12;

    private static final int OFFSET_CONTROLCODE = OFFSET_PHONE + SIZE_PHONE;
    private static final int SIZE_CONTROLCODE = 4;

    // uuid оборудования?
    public String uuid;
    // ид задачи
    public int taskId;
    // ид типа задачи
    public int taskTypeId;
    // дата начала выполнения
    public int start;
    // дата окончания выполнения
    public int end;
    // cтатус
    public int status;
    // заключение по выполнению задачи
    public int verdictId;
    // ид пользователя выполнявшего задачу
    public int userId;
    // статус оборудования
    public int equipmentStatusId;
    // телефон пользователя выполнявшего задачу
    public String phone;
    // "секретный" код
    public int controlCode;

    public byte[] getBinary() {
        byte[] data = new byte[64];


        if (!copyHexBytes(uuid.replace("-", ""), SIZE_UUID, data, OFFSET_UUID)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", taskId), SIZE_TASKID, data, OFFSET_TASKID)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", taskTypeId), SIZE_TASKTYPEID, data, OFFSET_TASKID)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", start), SIZE_START, data, OFFSET_START)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", end), SIZE_END, data, OFFSET_END)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", status), SIZE_STATUS, data, OFFSET_STATUS)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", verdictId), SIZE_VERDICTID, data, OFFSET_VERDICTID)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", userId), SIZE_USERID, data, OFFSET_USERID)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", equipmentStatusId), SIZE_EQUIPMENTSTATUSID, data, OFFSET_EQUIPMENTSTATUSID)) {
            return null;
        }

        if (!copyBytes(phone, SIZE_PHONE, data, OFFSET_PHONE)) {
            return null;
        }

        if (!copyHexBytes(String.format("%08x", controlCode), SIZE_CONTROLCODE, data, OFFSET_CONTROLCODE)) {
            return null;
        }

        return data;
    }

    /**
     *
     * @param value     String
     * @param size      int
     * @param target    byte[]
     * @param offset    int
     * @return boolean
     */
    private boolean copyBytes(String value, int size, byte[] target, int offset) {
        byte[] tmpValue = value.getBytes();
        if (tmpValue.length < size) {
            return false;
        } else {
            System.arraycopy(tmpValue, 0, target, offset, size);
            return true;
        }
    }

    /**
     *
     * @param value     String
     * @param size      int
     * @param target    byte[]
     * @param offset    int
     * @return boolean
     */
    private boolean copyHexBytes(String value, int size, byte[] target, int offset) {
        byte[] tmpValue = DataUtils.hexStringTobyte(value);
        if (tmpValue == null || tmpValue.length < size) {
            return false;
        } else {
            System.arraycopy(tmpValue, 0, target, offset, size);
            return true;
        }
    }

    public boolean parse(byte[] data) {
        return false;
    }
}
