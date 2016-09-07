package com.example.testandroid;

import java.io.File;

import com.centerm.testandroid.test.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.main);

		String beforeFilePath = DpiUtils.getSDPath() + File.separator + "imagebeforepng.png";
		String afterFilePath = DpiUtils.getSDPath() + File.separator + "imageafterpng.png";

		//先透明再调整dpi
		byte[] imageData = DpiUtils.toTransparent(beforeFilePath);
		DpiUtils.changePngDpi(imageData, afterFilePath);
	}
}
