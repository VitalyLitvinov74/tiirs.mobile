package ru.toir.mobile.multi.c5lib.lib.src.main.java.android.hardware.uhf.magic;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 28.12.16.
 */

public class UHFCommandResult {
    public int result;
    public String data;

    public UHFCommandResult() {
    }

    public UHFCommandResult(int result) {
        this.result = result;
        this.data = null;
    }

    public UHFCommandResult(int result, String data) {
        this.result = result;
        this.data = data;
    }

}
