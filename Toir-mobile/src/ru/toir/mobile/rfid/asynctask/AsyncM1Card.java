package ru.toir.mobile.rfid.asynctask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import ru.toir.mobile.serial.M1CardAPI;
import ru.toir.mobile.serial.M1CardAPI.Result;

public class AsyncM1Card extends Handler {

	private static final int READ_CARD_NUM = 1;

	private static final int WRITE_AT_POSITION_DATA = 2;

	private static final int READ_AT_POSITION_DATA = 3;

	private static final String POSITION_KEY = "position";
	private static final String NUM = "num";
	private static final String KEY_TYPE_KEY = "keyType";
	private static final String PASSWORD_KEY = "password";
	private static final String DATA_KEY = "data";

	private Handler mWorkerThreadHandler;

	private M1CardAPI reader;

	public AsyncM1Card(Looper looper) {
		mWorkerThreadHandler = createHandler(looper);
		reader = new M1CardAPI();
	}

	protected Handler createHandler(Looper looper) {
		return new WorkerHandler(looper);
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_CARD_NUM:
				Result result = reader.readCardNum();
				AsyncM1Card.this.obtainMessage(READ_CARD_NUM, result)
						.sendToTarget();
				break;
			case WRITE_AT_POSITION_DATA:
				Result writeAtPositionResult = write(msg);
				AsyncM1Card.this.obtainMessage(WRITE_AT_POSITION_DATA,
						writeAtPositionResult).sendToTarget();
				break;
			case READ_AT_POSITION_DATA:
				Result readAtPositionResult = read(msg);
				AsyncM1Card.this.obtainMessage(READ_AT_POSITION_DATA,
						readAtPositionResult).sendToTarget();
				break;
			default:
				break;
			}
		}
	}

	private OnReadCardNumListener onReadCardNumListener;

	private OnReadAtPositionListener onReadAtPositionListener;

	private OnWriteAtPositionListener onWriteAtPositionListener;

	public void setOnReadCardNumListener(
			OnReadCardNumListener onReadCardNumListener) {
		this.onReadCardNumListener = onReadCardNumListener;
	}

	public void setOnReadAtPositionListener(
			OnReadAtPositionListener onReadAtPositionListener) {
		this.onReadAtPositionListener = onReadAtPositionListener;
	}

	public void setOnWriteAtPositionListener(
			OnWriteAtPositionListener onWriteAtPositionListener) {
		this.onWriteAtPositionListener = onWriteAtPositionListener;
	}

	public interface OnReadCardNumListener {
		public void onReadCardNumSuccess(String num);

		public void onReadCardNumFail(int comfirmationCode);
	}

	public interface OnReadAtPositionListener {
		/**
		 * 
		 * @param num
		 * @param data
		 */
		public void onReadAtPositionSuccess(String cardNum, byte[][] data);

		/**
		 * И·ИПВл 1: іЙ№¦ 2ЈєС°їЁК§°Ь 3ЈєСйЦ¤К§°Ь 4:РґїЁК§°Ь 5Јєі¬К± 6ЈєЖдЛьТміЈ
		 * 
		 * @param comfirmationCode
		 */
		public void onReadAtPositionFail(int comfirmationCode);
	}

	public interface OnWriteAtPositionListener {
		public void onWriteAtPositionSuccess(String num);

		/**
		 * И·ИПВл 1: іЙ№¦ 2ЈєС°їЁК§°Ь 3ЈєСйЦ¤К§°Ь 4:РґїЁК§°Ь 5Јєі¬К± 6ЈєЖдЛьТміЈ
		 * 
		 * @param comfirmationCode
		 */
		public void onWriteAtPositionFail(int comfirmationCode);
	}

	public void readCardNum() {
		mWorkerThreadHandler.obtainMessage(READ_CARD_NUM).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case READ_CARD_NUM:
			Result numResult = (Result) msg.obj;
			if (onReadCardNumListener != null) {
				if (numResult != null
						&& numResult.confirmationCode == Result.SUCCESS) {
					onReadCardNumListener.onReadCardNumSuccess(numResult.num);
				} else {
					onReadCardNumListener
							.onReadCardNumFail(numResult.confirmationCode);
				}
			}
			break;
		case WRITE_AT_POSITION_DATA:
			if (onWriteAtPositionListener != null) {
				Result result = (Result) msg.obj;
				if (result != null && result.confirmationCode == Result.SUCCESS) {
					onWriteAtPositionListener
							.onWriteAtPositionSuccess(result.num);
				} else {
					onWriteAtPositionListener
							.onWriteAtPositionFail(result.confirmationCode);
				}
			}
			break;
		case READ_AT_POSITION_DATA:
			Result readPositionResult = (Result) msg.obj;
			byte[][] readPositionData = (byte[][]) readPositionResult.resultInfo;
			if (onReadAtPositionListener != null) {
				if (readPositionData != null && !dataIsNull(readPositionData)) {
					onReadAtPositionListener.onReadAtPositionSuccess(
							readPositionResult.num, readPositionData);
				} else {
					onReadAtPositionListener
							.onReadAtPositionFail(readPositionResult.confirmationCode);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * ПтЦё¶ЁµДїйєЕРґИлКэѕЭЈ¬і¤¶ИОЄ16ЧЦЅЪ Write data to the specified block, length is 16 bytes
	 * args should be data[i].length == num.
	 * @param position
	 *            РґИлКэѕЭµДїйєЕ Write data block number
	 * @param password
	 *            ГЬВлїЙТФОЄnullЈ¬ТІїЙТФОЄі¤¶И6ЧЦЅЪµДГЬВл Password can be null, or length of 6
	 *            bytes of password
	 * @param keyType
	 *            ГЬВлАаРНЈєГЬВлA»тГЬВлB Password: A password or password B
	 * @param data
	 *            РґИлµДКэѕЭІ»ДЬОЄїХЈ¬dataµДі¤¶ИОЄ16ЧЦЅЪЈ¬ТтТ»ёцїйЦ»ДЬґж·Е16ЧЦЅЪµДКэѕЭ,ЅЁТйІ»Чг16ЧЦЅЪ УГ0І№Жл Write
	 *            data can not be empty, the length of the data of 16 bytes,
	 *            because of a piece of only 16 bytes of data, suggest that less
	 *            than 16 bytes 0 is lacking
	 * @return
	 */
	public void write(int position, int num, int keyType, byte[] password, byte[][] data) {
		Message msg = mWorkerThreadHandler
				.obtainMessage(WRITE_AT_POSITION_DATA);
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putInt(NUM, num);
		bundle.putInt(KEY_TYPE_KEY, keyType);
		bundle.putByteArray(PASSWORD_KEY, password);
		msg.obj = data;
		msg.setData(bundle);
		msg.sendToTarget();
	}

	/**
	 * 
	 * @param position
	 *            РґИлКэѕЭµДїйєЕ Write data block number
	 * @param num : the number of block
	 * @param password
	 *            ГЬВлїЙТФОЄnullЈ¬ТІїЙТФОЄі¤¶И6ЧЦЅЪµДГЬВл Password can be null, or length of 6
	 *            bytes of code
	 */
	public void read(int position, int num, int keyType, byte[] password) {
		Message msg = mWorkerThreadHandler.obtainMessage(READ_AT_POSITION_DATA);
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putInt(NUM, num);
		bundle.putInt(KEY_TYPE_KEY, keyType);
		bundle.putByteArray(PASSWORD_KEY, password);
		msg.setData(bundle);
		msg.sendToTarget();

	}

	private Result write(Message msg) {
		Bundle writeBundle = msg.getData();
		int position = writeBundle.getInt(POSITION_KEY);
		int num = writeBundle.getInt(NUM);
		int keyType = writeBundle.getInt(KEY_TYPE_KEY);
		byte[] password = writeBundle.getByteArray(PASSWORD_KEY);
		byte[][] data = (byte[][])msg.obj;
		Result result = null;
		int time = 0;
		// С°їЁИэґО»тСйЦ¤3ґО¶јІ»НЁ№э·µ»Ш
		while (time < 3) {
			result = reader.readCardNum();
			if (result.confirmationCode == Result.FIND_FAIL) {
				// return result;
				time++;
				continue;
			} else if (result.confirmationCode == Result.TIME_OUT) {
				return result;
			}

			if (!reader.validatePassword(position, keyType, password)) {
				result.confirmationCode = Result.VALIDATE_FAIL;
				// return result;
				time++;
				continue;
			} else {
				break;
			}
		}
		if (result.confirmationCode == Result.FIND_FAIL
				|| result.confirmationCode == Result.VALIDATE_FAIL) {
			return result;
		}

		boolean writeResult = reader.write(data, position,num);
		reader.turnOff();
		if (writeResult) {
			result.confirmationCode = Result.SUCCESS;
		} else {
			result.confirmationCode = Result.WRITE_FAIL;
		}
		return result;
	}

	private Result read(Message msg) {
		Bundle readBundle = msg.getData();
		int position = readBundle.getInt(POSITION_KEY);
		int num = readBundle.getInt(NUM);
		int keyType = readBundle.getInt(KEY_TYPE_KEY);
		byte[] password = readBundle.getByteArray(PASSWORD_KEY);
		Result result = null;
		int time = 0;
		// С°їЁИэґО»тСйЦ¤3ґО¶јІ»НЁ№э·µ»Ш
		while (time < 3) {
			result = reader.readCardNum();
			if (result.confirmationCode == Result.FIND_FAIL) {
				// return result;
				time++;
				continue;
			} else if (result.confirmationCode == Result.TIME_OUT) {
				return result;
			}

			if (!reader.validatePassword(position, keyType, password)) {
				result.confirmationCode = Result.VALIDATE_FAIL;
				// return result;
				time++;
				continue;
			} else {
				break;
			}
		}
		if (result.confirmationCode == Result.FIND_FAIL
				|| result.confirmationCode == Result.VALIDATE_FAIL) {
			return result;
		}
		byte[][] data = reader.read(position, num);
		reader.turnOff();
		if (!dataIsNull(data)) {
			result.confirmationCode = Result.SUCCESS;
			result.resultInfo = data;
		} else {
			result.confirmationCode = Result.READ_FAIL;
			result.resultInfo = data;
		}
		return result;
	}

	private boolean dataIsNull(byte[][] data) {
		if (data == null) {
			return true;
		}

		for (int i = 0; i < data.length; i++) {
			if (data[i] == null) {
				return true;
			}
		}

		return false;
	}
}
