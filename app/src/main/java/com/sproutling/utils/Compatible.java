/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import static android.os.Build.VERSION;

/**
 * Created by moi0312 on 2016/12/26.
 */

public class Compatible {

	public static int getColor(Context context, int colorId) {
		Resources res = context.getResources();
		if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			Resources.Theme theme = context.getTheme();
			return res.getColor(colorId, theme);
		} else {
			return res.getColor(colorId);
		}
	}
}
