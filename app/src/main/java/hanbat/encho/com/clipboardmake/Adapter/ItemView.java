package hanbat.encho.com.clipboardmake.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import hanbat.encho.com.clipboardmake.R;

/**
 * Created by USER on 2016-09-23.
 */

public class ItemView extends LinearLayout implements Checkable {
    public ItemView(Context context) {
        super(context);
        init();
    }

    View checkView;

    private void init() {
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mParams.setMargins(8, 16, 8, 8);
        setLayoutParams(mParams);
        inflate(getContext(), R.layout.rcv_item, this);

        checkView = findViewById(R.id.selected);
    }

    private void drawCheck() {
        if (isChecked()) {
            checkView.setVisibility(VISIBLE);
        } else {
            checkView.setVisibility(INVISIBLE);
        }
    }

    boolean isChecked = false;

    @Override
    public void setChecked(boolean b) {
        if (isChecked != b) {
            isChecked = b;
            drawCheck();
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
