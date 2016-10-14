package hanbat.encho.com.clipboardmake.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import hanbat.encho.com.clipboardmake.DbOpener;
import hanbat.encho.com.clipboardmake.Entity;
import hanbat.encho.com.clipboardmake.MainActivity;
import hanbat.encho.com.clipboardmake.MemoContent;
import hanbat.encho.com.clipboardmake.R;

/**
 * Created by USER on 2016-09-09.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> implements ViewHolder.OnItemClickListener {
    private static final String TAG = "마이어댑터";

    private Context mContext;
    private ArrayList<Entity> list = null;
    public ArrayList<Entity> filtered = null;
    private DbOpener mOpener;
    private boolean result;
    private AlertDialog mDialog = null;

    private int checkMode;
    private int mCheckedPosition = INVAILD_POSITION;
    private MainActivity owner = null;

    public static final int INVAILD_POSITION = -1;
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    public static BookmarkedCallback mCallback = null;


    public MyAdapter(Context mContext, ArrayList list, ArrayList filtered) {
        this.mContext = mContext;
        this.list = list;
        this.filtered = filtered;
        mOpener = DbOpener.getInstance(mContext);
        owner = (MainActivity) mContext;
    }

    public static void setCallback(BookmarkedCallback callback) {
        mCallback = callback;
    }


    @Override
    public void onItemClick(View view, int position) {
        if (checkMode == MODE_SINGLE) {
            Log.d(TAG, position + "");
            MemoContent fragment = MemoContent.newInstance(filtered.get(position), filtered.get(position)._id % 4);
            FragmentTransaction transaction = owner.getSupportFragmentManager().beginTransaction();
            transaction.add(fragment, null);
            transaction.commit();
            mCheckedPosition = position;
            notifyDataSetChanged();
        } else if (checkMode == MODE_MULTI) {
            boolean oldChecked = filtered.get(position).checked;
            filtered.set(position, new Entity(filtered.get(position)._id, filtered.get(position).memo, !oldChecked, filtered.get(position).marked));
            Log.d(TAG, filtered.get(position).checked + "");
            notifyDataSetChanged();
        }
    }


    public int getCheckItemPosition() {
        if (checkMode == MODE_SINGLE) {
            return mCheckedPosition;
        } else {
            return INVAILD_POSITION;
        }
    }

    public ArrayList<Entity> getCheckedItemPositions() {
        return filtered;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(new ItemView(parent.getContext()));
        holder.setOnItemClickListener(this);
        return holder;
    }

    public void setMode(int mode) {
        if (mode == MODE_SINGLE || mode == MODE_MULTI) {
            checkMode = mode;
        } else {
            throw new IllegalArgumentException("invalid check mode");
        }
    }


    public int getMode() {
        return checkMode;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mTextView.setText(filtered.get(position).memo);

        if (filtered.get(position).marked == 1) {
            holder.remarked.setVisibility(View.VISIBLE);
        }

        if (checkMode == MODE_SINGLE) {
//            if (position == mCheckedPosition) {
//                holder.setChecked(true);
//            } else {
//                holder.setChecked(false);
//            }

        } else if (checkMode == MODE_MULTI) {
            holder.setChecked(filtered.get(position).checked);
        }

        switch (filtered.get(position)._id % 4) {
            case 0: {
                holder.mView.setBackgroundResource(R.drawable.memo_yellow); // Material YELLOW
                break;
            }
            case 1: {
                holder.mView.setBackgroundResource(R.drawable.memo_pink); // PINK
                break;
            }
            case 2: {
                holder.mView.setBackgroundResource(R.drawable.memo_purple); // PURPLE
                break;
            }
            case 3: {
                holder.mView.setBackgroundResource(R.drawable.memo_blue); // BLUE
                break;
            }
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0: { // 삭제
                                mOpener.open();
                                result = mOpener.deleteColumn(list.get(position)._id);
                                mOpener.close();
                                if (result) {
                                    list.remove(filtered.get(position));
                                    filtered.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext, mContext.getString(R.string.message_when_delete), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Check your list index", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            case 1: { // 복사
                                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData data = ClipData.newPlainText("memo", filtered.get(position).memo);
                                manager.setPrimaryClip(data);
                                Toast.makeText(mContext, R.string.message_when_copied, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 2: {
                                Intent msg = new Intent(Intent.ACTION_SEND);
                                msg.addCategory(Intent.CATEGORY_DEFAULT);
                                msg.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
                                msg.putExtra(Intent.EXTRA_TITLE, mContext.getString(R.string.app_name));
                                msg.putExtra(Intent.EXTRA_TEXT, filtered.get(position).memo);
                                msg.setType("text/plain");
                                mContext.startActivity(Intent.createChooser(msg, "메모 공유"));
                                break;
                            }
                            case 3: { // 즐겨찾기
                                int boolToInteger = filtered.get(position).marked;
                                if (boolToInteger == 0) boolToInteger = 1;
                                else boolToInteger = 0;
                                mOpener.open();
                                mOpener.updateColumn(filtered.get(position)._id, filtered.get(position).memo, boolToInteger);
                                mOpener.close();
                                mCallback.onDialogDestroied();
                                break;
                            }
                        }
                    }
                };

                final CharSequence[] items = {mContext.getString(R.string.delete), mContext.getString(R.string.copy), mContext.getString(R.string.shared), mContext.getString(R.string.bookmark)};

                mDialog = new AlertDialog.Builder(mContext)
                        .setItems(items, deleteListener)
                        .setIcon(R.drawable.ic_delete_black_24dp)
                        .create();
                mDialog.show();

                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (filtered.isEmpty()) {
            owner.findViewById(R.id.no_item).setVisibility(View.VISIBLE);
        } else {
            owner.findViewById(R.id.no_item).setVisibility(View.INVISIBLE);
        }
        return filtered.size();
    }


    /* ----- 검색 필터 적용 ----- */
    public void setFilter(ArrayList<Entity> memberModels, String text) {
        if (memberModels.size() > 0) {
            filtered.clear();
            filtered.addAll(memberModels);
        } else if (text == null && text.length() < 1) {
            filtered.clear();
            filtered.addAll(list);
        } else {
            filtered.clear();
        }
        notifyDataSetChanged();
    }

    public interface BookmarkedCallback {
        void onDialogDestroied();
    }

}
