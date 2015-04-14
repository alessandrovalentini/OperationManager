package utils;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;

public class appLanguage {
	public static final void setLanguage(Context context, String localeName){
		//Change Language
		Locale locale = new Locale(localeName);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,	context.getResources().getDisplayMetrics());	
	}
}
