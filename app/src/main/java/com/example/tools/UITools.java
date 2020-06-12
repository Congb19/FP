package com.example.tools;

import android.content.Context;
import android.util.Log;

// dp适配工具
public class UITools {
	public static int px2dip(Context context, float px) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5f);
	}
	public static int dip2px(Context context, float dp) {
		float density = context.getResources().getDisplayMetrics().density;
		Log.i("DP TO PX, ", "dp: " + dp + ", destiny: "+ density + ", px: "+(dp / density + 0.5f));
		return (int) (dp * density + 0.5f);
	}
}
