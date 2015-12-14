/**
 * 
 */
package ru.toir.mobile.bluetooth;

/**
 * @author Dmitriy Logachov
 * 
 */
public interface ICommunicatorListener {

	public void onMessage(byte[] message);
}
