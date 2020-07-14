package ru.toir.mobile.multi.rfid;

import ru.toir.mobile.multi.utils.DataUtils;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 23.11.16.
 */

public class TagStructure {

    public static final int OFFSET_UUID = 0;
    public static final int SIZE_UUID = 16;

    public static final int OFFSET_TASKID = OFFSET_UUID + SIZE_UUID;
    public static final int SIZE_TASKID = 4;

    public static final int OFFSET_TASKTYPEID = OFFSET_TASKID + SIZE_TASKID;
    public static final int SIZE_TASKTYPEID = 4;

    public static final int OFFSET_START = OFFSET_TASKTYPEID + SIZE_TASKTYPEID;
    public static final int SIZE_START = 4;

    public static final int OFFSET_END = OFFSET_START + SIZE_START;
    public static final int SIZE_END = 4;

    public static final int OFFSET_STATUS = OFFSET_END + SIZE_END;
    public static final int SIZE_STATUS = 4;

    public static final int OFFSET_VERDICTID = OFFSET_STATUS + SIZE_STATUS;
    public static final int SIZE_VERDICTID = 4;

    public static final int OFFSET_USERID = OFFSET_VERDICTID + SIZE_VERDICTID;
    public static final int SIZE_USERID = 4;

    public static final int OFFSET_EQUIPMENTSTATUSID = OFFSET_USERID + SIZE_USERID;
    public static final int SIZE_EQUIPMENTSTATUSID = 4;

    public static final int OFFSET_PHONE = OFFSET_EQUIPMENTSTATUSID + SIZE_EQUIPMENTSTATUSID;
    public static final int SIZE_PHONE = 12;

    public static final int OFFSET_CONTROLCODE = OFFSET_PHONE + SIZE_PHONE;
    public static final int SIZE_CONTROLCODE = 4;

    // uuid оборудования?
    public String uuid = "00000000-0000-0000-0000-000000000000";
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
    public String phone = "+70000000000";
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

        if (!copyHexBytes(String.format("%08x", taskTypeId), SIZE_TASKTYPEID, data, OFFSET_TASKTYPEID)) {
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
        if (value == null) {
            return false;
        }

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
        byte[] tmpUuid = new byte[TagStructure.SIZE_UUID];
        System.arraycopy(data, TagStructure.OFFSET_UUID, tmpUuid, 0, TagStructure.SIZE_UUID);
        uuid = DataUtils.StringToUUID(DataUtils.toHexString(tmpUuid)).toUpperCase();
        byte[] tmpInt = new byte[4];
        System.arraycopy(data, TagStructure.OFFSET_TASKID, tmpInt, 0, TagStructure.SIZE_TASKID);
        taskId = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_TASKTYPEID, tmpInt, 0, TagStructure.SIZE_TASKTYPEID);
        taskTypeId = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_START, tmpInt, 0, TagStructure.SIZE_START);
        start = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_END, tmpInt, 0, TagStructure.SIZE_END);
        end = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_STATUS, tmpInt, 0, TagStructure.SIZE_STATUS);
        status = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_VERDICTID, tmpInt, 0, TagStructure.SIZE_VERDICTID);
        verdictId = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_USERID, tmpInt, 0, TagStructure.SIZE_USERID);
        userId = getInt(tmpInt);
        System.arraycopy(data, TagStructure.OFFSET_EQUIPMENTSTATUSID, tmpInt, 0, TagStructure.SIZE_EQUIPMENTSTATUSID);
        equipmentStatusId = getInt(tmpInt);
        byte[] tmpPhone = new byte[TagStructure.SIZE_PHONE];
        System.arraycopy(data, TagStructure.OFFSET_PHONE, tmpPhone, 0, TagStructure.SIZE_PHONE);
        StringBuilder phoneBuilder = new StringBuilder();
        for (byte tmpByte: tmpPhone) {
            phoneBuilder.append((char) tmpByte);
        }

        phone = phoneBuilder.toString();

        System.arraycopy(data, TagStructure.OFFSET_CONTROLCODE, tmpInt, 0, TagStructure.SIZE_CONTROLCODE);
        controlCode = getInt(tmpInt);

        return true;
    }

    private int getInt(byte[] data) {
        return (int)data[0] << 24 | (int)data[1] << 16 | (int)data[2] << 8 | (int)data[3];
    }
}
