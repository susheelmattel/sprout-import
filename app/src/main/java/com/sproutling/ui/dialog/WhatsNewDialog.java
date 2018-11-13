package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by desaibh1 on 3/14/18.
 */

public class WhatsNewDialog extends Dialog {
    private List<String> featureList = new ArrayList<>();
    private RecyclerView mWhatsNewList;
    private WhatsNewAdapter mWhatsNewAdapter;
    private ShTextView mTvClose;

    public WhatsNewDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_whats_new);

        mWhatsNewList = (RecyclerView) findViewById(R.id.whats_new_list);
        mTvClose = (ShTextView) findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });


        featureList = Arrays.asList(context.getResources().getStringArray(R.array.whats_new_feature_list));

        mWhatsNewAdapter = new WhatsNewAdapter(featureList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mWhatsNewList.setLayoutManager(mLayoutManager);
        mWhatsNewList.setItemAnimator(new DefaultItemAnimator());
        mWhatsNewList.setAdapter(mWhatsNewAdapter);
    }

    private class WhatsNewAdapter extends RecyclerView.Adapter<WhatsNewAdapter.WhatsNewViewHolder> {
        private List<String> featureList;

        public class WhatsNewViewHolder extends RecyclerView.ViewHolder {
            public ShTextView feature_description;

            public WhatsNewViewHolder(View view) {
                super(view);
                feature_description = (ShTextView) view.findViewById(R.id.new_feature_description);
            }
        }

        public WhatsNewAdapter(List<String> featureList) {
            this.featureList = featureList;
        }

        @Override
        public WhatsNewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.whats_new_list_item, parent, false);

            return new WhatsNewViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WhatsNewViewHolder holder, int position) {
            holder.feature_description.setText(featureList.get(position));
        }

        @Override
        public int getItemCount() {
            return featureList.size();
        }
    }
}
