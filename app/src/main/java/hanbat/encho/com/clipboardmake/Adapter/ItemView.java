package hanbat.encho.com.clipboardmake.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import hanbat.encho.com.clipboardmake.R;

/**
 * Created by USER on 2016-09-23.
 */

public class ItemView extends CardView implements Checkable {
    public ItemView(Context context) {
        super(context);
        init();
    }

    View checkView;

    private void init() {
        ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
