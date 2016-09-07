package com.example.testandroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;

public class DpiUtils {

	private static final int HEAD = 8;
	private static final int IHDR = 12;
	private static byte[] pHYS = {0x00, 0x00, 0x00, 0x09, 0x70, 0x48, 0x59, 0x73, 0x00, 0x00,
		0x1e, (byte)0xc2, 0x00, 0x00, 0x1e, (byte)0xc2, 0x01, 0x6e, (byte)0xd0, 0x75, 0x3e}; //png物理像素尺寸数据块

	private static final String TAG = MainActivity.class.getSimpleName();

	/** 
	 * 设置dpi 
	 * @param imageData 透明化后png数据
	 * @param filePath 要保存的图片路径
	 * 
	 */ 
	public static void changePngDpi(byte[] imageData, String filePath){
		try{
			byte[] oldData = imageData;
			//计算左块数据长度
			byte[] leftLength = new byte[4];
			leftLength[0] = oldData[HEAD];
			leftLength[1] = oldData[HEAD+1];
			leftLength[2] = oldData[HEAD+2];
			leftLength[3] = oldData[HEAD+3];

			int length = byteArrayToLength(leftLength)+HEAD+IHDR; //IHDR数据块总长度

			byte[] left = new byte[length];
			System.arraycopy(oldData, 0, left, 0, length);
			byte[] right = new byte[oldData.length - length];
			System.arraycopy(oldData, length, right, 0, oldData.length - length);

			byte[] newData = new byte[pHYS.length + oldData.length];
			System.arraycopy(left, 0, newData, 0, left.length);
			System.arraycopy(pHYS, 0, newData, length, pHYS.length);
			System.arraycopy(right, 0, newData, left.length + pHYS.length, right.length);

			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			fileOutputStream.write(newData);
			fileOutputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** 
	 * 设置dpi 
	 * @param imageData jpeg数据
	 * @param dpi 
	 * 
	 */ 
	public void changeJpegDpi(byte[] imageData, int dpi) {
		imageData[13] = 1;
		imageData[14] = (byte) (dpi >> 8);
		imageData[15] = (byte) (dpi & 0xff);
		imageData[16] = (byte) (dpi >> 8);
		imageData[17] = (byte) (dpi & 0xff);
	}

	/** 
	 * png透明化处理 
	 * @param filePath 原始png文件路径
	 * 
	 */ 
	public static byte[] toTransparent(String filePath){
		try{
			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			Bitmap oldBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
			Canvas newc = new Canvas(oldBitmap);
			newc.drawBitmap(bitmap, 0, 0, null);

			Bitmap newBitmap = transBitmapAlpha(oldBitmap);

			ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByteArray);
			byte[] imageData = imageByteArray.toByteArray();

			return imageData;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/** 
	 * png透明化处理 
	 * @param bitmap
	 * 
	 */ 
	public static byte[] toTransparent(Bitmap bitmap){
		try{
			Bitmap oldBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
			Canvas newc = new Canvas(oldBitmap);
			newc.drawBitmap(bitmap, 0, 0, null);
			Bitmap newBitmap = transBitmapAlpha(oldBitmap);
			ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByteArray);
			byte[] imageData = imageByteArray.toByteArray();

			return imageData;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static int byteArrayToLength(byte[] byteArray)
	{
		int result = 0;
		for ( int i = 0; i < byteArray.length; i++ )
		{
			result = (byteArray[i] & 0x000000FF) ^ result;
			if ( i < byteArray.length - 1 )
			{
				result <<= 8;
			}
		}
		return result;
	}

	public static Bitmap transBitmapAlpha(Bitmap localBits) {
		Bitmap tmpBits = localBits;
		int x = 0;
		int y = 0;
		for (y = tmpBits.getHeight()-1; y>=0; y--)
		{
			for (x=0; x<tmpBits.getWidth(); x++)
			{
				if (tmpBits.getPixel(x, y) == 0xFFFFFFFF)
				{
					tmpBits.setPixel(x, y, 0);
				}
			}
		}
		return tmpBits;
	}

	public static String getSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		}
		return sdDir.toString();
	}

	public void logPrint(byte[] data){
		Log.i(TAG, "data.length:" + data.length);
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<data.length; i++){
			buffer.append(String.format("%02x", data[i]) + " ");
		}
		Log.i(TAG, buffer.toString());
	}
}
