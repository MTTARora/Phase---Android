package com.rora.phase.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.rora.phase.R;

import carbon.widget.Button;

public class FilterDialog extends Dialog {

    public Activity activity;

    public FilterDialog(Activity a) {
        super(a);
        this.activity = a;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_filter);

        findViewById(R.id.apply_filter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
