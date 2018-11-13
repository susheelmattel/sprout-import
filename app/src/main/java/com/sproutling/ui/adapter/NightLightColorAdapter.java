package com.sproutling.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.object.NightLightColor;
import com.wx.wheelview.adapter.BaseWheelAdapter;

/**
 * Created by subram13 on 2/22/18.
 */

public class NightLightColorAdapter extends BaseWheelAdapter<NightLightColor> {
    private Context mContext;

    public NightLightColorAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.night_light_color_list_item, parent, false);
        TextView colorName = rowView.findViewById(R.id.colorName);
        colorName.setText(((NightLightColor) getItem(position)).getColor());
        colorName.setCompoundDrawablesWithIntrinsicBounds(((NightLightColor) getItem(position)).getImgRes(), 0, 0, 0);
        return rowView;
    }

}
