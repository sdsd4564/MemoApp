package hanbat.encho.com.clipboardmake;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Encho on 2016-09-14.
 */
public class MemoContent extends DialogFragment {
    public static DeleteCallback mCallback = null;

    public static MemoContent newInstance(Entity content, int memoColor) {
        Bundle args = new Bundle();
        MemoContent fragment = new MemoContent();
        args.putString("content", content.memo);
        args.putInt("id", content._id);
        args.putInt("color", memoColor);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setCallback(DeleteCallback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_frame, container, false);
        TextView displayText = (TextView) view.findViewById(R.id.content_text);
        LinearLayout memoFrame = (LinearLayout) view.findViewById(R.id.memo_dialog);
        ImageView closeMemo = (ImageView) view.findViewById(R.id.close_memo);
        ImageView deleteMemo = (ImageView) view.findViewById(R.id.delete_memo);
        ImageView copyMemo = (ImageView) view.findViewById(R.id.copy_memo);

        /* ----- 닫기 버튼 ----- */
        closeMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(MemoContent.this).commit();
            }
        });

        /* ----- 삭제 버튼 ----- */
        deleteMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbOpenner mOpenner = DbOpenner.getInstance(getContext());
                mOpenner.open();
                mOpenner.deleteColumn(getArguments().getInt("id"));
                mOpenner.close();
                getActivity().getSupportFragmentManager().beginTransaction().remove(MemoContent.this).commit();
                mCallback.onFragmentDestroy();
            }
        });
        /* ----- 복사 버튼 ----- */
        copyMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("memo", getArguments().getString("content"));
                manager.setPrimaryClip(data);
                Toast.makeText(getActivity(), R.string.message_when_copied, Toast.LENGTH_SHORT).show();
            }
        });

        int memoColor = getArguments().getInt("color");
        switch (memoColor) {
            case 0: {
                memoFrame.setBackgroundResource(R.drawable.memo_yellow); // Material YELLOW
                break;
            }
            case 1: {
                memoFrame.setBackgroundResource(R.drawable.memo_pink); // Material YELLOW
                break;
            }
            case 2: {
                memoFrame.setBackgroundResource(R.drawable.memo_purple); // Material YELLOW
                break;
            }
            case 3: {
                memoFrame.setBackgroundResource(R.drawable.memo_blue); // Material YELLOW
                break;
            }
        }
        displayText.setText(getArguments().getString("content"));
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FrameLayout root = new FrameLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    public interface DeleteCallback {
        void onFragmentDestroy();
    }
}
