/*
 * Decompiled with CFR 0_118.
 */
package ru.toir.mobile.multi.p6300.lib.src.main.java.android.hardware.p6300.uhf.api;

public class Gpio {
    public int com_type;
    public int gpio;
    public int gpio_level;

    public Gpio() {
    }

    public Gpio(int com_type, int gpio, int gpio_level) {
        this.com_type = com_type;
        this.gpio = gpio;
        this.gpio_level = gpio_level;
    }
}
