package ru.toir.mobile.serial;

public interface LooperBuffer{
	void add(byte[] buffer);
	byte[] getFullPacket();
}
