package com.example.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 处理窗口控件大小，形状，像素等工具
 */
public class UITools {
	/**
	 * 把像素转化为dp
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int px2dip(Context context, float px) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px * density + 0.5f);
	}
	
	public static int dip2px(Context context, float dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp / density + 0.5f);
	}

	/**
	 * 设置窗体的宽度
	 * @param context
	 */
	public static void setWinWidth(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		LayoutParams p = context.getWindow().getAttributes();
		p.width = (int) (metric.widthPixels * 0.8);
		context.getWindow().setAttributes(p);
	}

	/**
	 * 设置弹出窗口中listview的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeight(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

}
