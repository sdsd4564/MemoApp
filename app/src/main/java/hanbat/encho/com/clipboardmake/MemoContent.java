package hanbat.encho.com.clipboardmake;

import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Encho on 2016-09-14.
 */
public class MemoContent extends DialogFragment{
    public static MemoContent newInstance() {
        Bundle args = new Bundle();
        MemoContent fragment = new MemoContent();
        fragment.setArguments(args);
        return fragment;
    }
}
