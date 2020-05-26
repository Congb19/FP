package com.example.tools;

import android.util.Log;

/**
 * 定义Logger日志，因为在app手机运行时很耗电，所以开发完成后必须关日志
 * 
 * @author chen.lin
 * 
 */
public class Logger {

	public static int LOG_LEVEL = 7;
	public static final int VERBOSE = 5;
	public static final int DEBUG = 4;
	public static final int INFO = 3;
	public static final int WARN = 2;
	public static final int ERROR = 1;

	public static void v(String tag, String msg) {
		if (LOG_LEVEL > VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOG_LEVEL > DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_LEVEL > INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LOG_LEVEL > WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOG_LEVEL > ERROR) {
			Log.e(tag, msg);
		}
	}
}
