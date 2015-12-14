/**
 * 
 */
package ru.toir.mobile.bluetooth;

/**
 * @author Dmitriy Logachov
 *
 */
public interface ICommunicator {

	public void startCommunication();
	public void stopCommunication();
	public void write(byte[] command);
}
