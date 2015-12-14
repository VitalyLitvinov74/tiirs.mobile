/**
 * 
 */
package ru.toir.mobile.bluetooth;

import android.bluetooth.BluetoothSocket;

/**
 * @author Dmitriy Logachov
 *
 */
public interface ICommunicatorService {

	public ICommunicator createCommunicator(BluetoothSocket socket);
}
