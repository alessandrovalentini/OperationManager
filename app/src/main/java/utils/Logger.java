package utils;

import android.util.Log;

public class Logger {

	public static final void verbose(Object o ,String msg){
		String tag = ""+o.getClass();
		Log.v(tag + " - LOGGER:", msg);
	}

	public static final void debug(Object o ,String msg){
		String tag = ""+o.getClass();
		Log.d(tag + " - LOGGER:", msg);
	}
	public static final void info(Object o ,String msg){
		String tag = ""+o.getClass();
		Log.i(tag + " - LOGGER:", msg);
	}

	public static final void warn(Object o ,String msg){
		String tag = ""+o.getClass();
		Log.w(tag + " - LOGGER:", msg);
	}

	public static final void error(Object o ,String msg){
		String tag = ""+o.getClass();
		Log.e(tag + " - LOGGER:", msg);
	}
}
