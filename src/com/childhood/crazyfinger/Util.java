package com.childhood.crazyfinger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class Util {
	// Context mContext;
	static Util instance;

	private Util() {
	}

	public static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}

	/**
	 * Copy assets files
	 * 
	 * @param context
	 */
	public void copyAssetsFileToSDcard(Context context) {
		try {
			InputStream inputStream;

			File romsDirectory = new File(Constant.ICON_PATH);

			if (!romsDirectory.isDirectory()) {
				romsDirectory.mkdirs();
			}

			// String outFileName = outDirName + "/" +
			// Constant.SDCARD_ROM_ZIP_NEOGEO;
			File file = new File(Constant.ICON_PATH + "crazy_finger.png");
			if (!file.exists()) {
				OutputStream outputStream = new FileOutputStream(file);
				inputStream = context.getAssets().open("crazy_finger.png");
				if (inputStream != null) {
					byte[] buffer = new byte[1024];
					int length;
					while ((length = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, length);
					}

					outputStream.flush();
					inputStream.close();
					outputStream.close();
				}
			}
		} catch (Exception e) {
			Log.v("DEBUG", "Error");
			e.printStackTrace();
		}
	}
}
