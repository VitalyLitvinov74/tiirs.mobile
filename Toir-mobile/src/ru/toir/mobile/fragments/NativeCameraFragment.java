package ru.toir.mobile.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.toir.mobile.R;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.support.v4.app.Fragment;

public class NativeCameraFragment extends Fragment {

	View rootView;
	Preview mPreview;
	Button mButton;
	boolean fragmentCreated = false;
	boolean fragmentShowed = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_native_camera, container,
				false);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		fragmentCreated = true;
		if (fragmentShowed) {
			mPreview = new Preview(getActivity().getApplicationContext());
			FrameLayout frame = ((FrameLayout) rootView
					.findViewById(R.id.camera_preview));
			frame.addView(mPreview);

			mButton = (Button) rootView.findViewById(R.id.button_capture);
			mButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mPreview.mCamera.takePicture(null, null, pictureCallback);
				}
			});
		}
		return rootView;
	}

	private File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"toir");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Camera Guide", "Required media storage does not exist");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.ENGLISH).format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (fragmentCreated && !fragmentShowed) {
				mPreview = new Preview(getActivity().getApplicationContext());
				FrameLayout frame = ((FrameLayout) rootView
						.findViewById(R.id.camera_preview));
				frame.addView(mPreview);

				mButton = (Button) rootView.findViewById(R.id.button_capture);
				mButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mPreview.mCamera.takePicture(null, null,
								pictureCallback);
					}
				});
			}
			fragmentShowed = true;
		}
	}

	PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("test", "onPictureTaken - jpeg");
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
				Toast.makeText(getActivity(), "Image retrieval failed.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mPreview.mCamera.startPreview();
		}
	};

	public class Preview extends SurfaceView implements SurfaceHolder.Callback {

		public SurfaceHolder mHolder;
		public Camera mCamera;

		public Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mCamera = Camera.open();
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			mCamera.startPreview();

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

}
