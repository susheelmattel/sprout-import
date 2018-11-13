/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.TimeLine;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class WeekAxisXValueFormatter implements IAxisValueFormatter {


    public WeekAxisXValueFormatter() {

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        switch ((int) value) {
            case 0:
                return "S";

            case 1:
                return "M";

            case 2:
                return "T";

            case 3:
                return "W";

            case 4:
                return "TH";

            case 5:
                return "F";

            case 6:
                return "S";

        }
        ;
        return "";
    }
}
