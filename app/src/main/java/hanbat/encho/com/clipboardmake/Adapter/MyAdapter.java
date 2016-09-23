package hanbat.encho.com.clipboardmake.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import hanbat.encho.com.clipboardmake.DbOpenner;
import hanbat.encho.com.clipboardmake.Entity;
import hanbat.encho.com.clipboardmake.R;

/**
 * Created by USER on 2016-09-09.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> implements ViewHolder.OnItemClickListener {
    private static final String TAG = "마이어댑터";

    private Context mContext;
    private ArrayList<Entity> list = null;
    private ArrayList<Entity> filtered = null;
    private DbOpenner mOpenner;
    private boolean result;
    private AlertDialog mDialog = null;

    private SparseBooleanArray checkedItem = new SparseBooleanArray();
    private int checkMode;
    private int mCheckedPosition = INVAILD_POSITION;

    public static final int INVAILD_POSITION = -1;
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;


    public MyAdapter(Context mContext, ArrayList list, ArrayList filtered) {
        this.mContext = mContext;
        this.list = list;
        this.filtered = filtered;
        mOpenner = DbOpenner.getInstance(mContext);
        mOpenner.open();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemClick(View view, int position) {
        if (checkMode == MODE_SINGLE) {
            if (mCheckedPosition != position) {
                mCheckedPosition = position;
                notifyDataSetChanged();
            }
        } else if (checkMode == MODE_MULTI) {
            boolean oldChecked = checkedItem.get(position);
            checkedItem.put(position, !oldChecked);
            notifyDataSetChanged();
        }
    }

    public SparseBooleanArray getCheckedItemPosition() {
        return checkedItem;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(filtered.get(position).memo);

        if (checkMode == MODE_SINGLE) {
            if (position == mCheckedPosition) {
                holder.setChecked(true);
            } else {
                holder.setChecked(false);
            }
        } else if (checkMode == MODE_MULTI) {
            holder.setChecked(checkedItem.get(position));
        }

        switch (list.get(position)._id % 4) {
            case 0: {
                holder.itemView.setBackgroundResource(R.drawable.memo_yellow); // Material YELLOW
                break;
            }
            case 1: {
                holder.itemView.setBackgroundResource(R.drawable.memo_pink); // PINK
                break;
            }
            case 2: {
                holder.itemView.setBackgroundResource(R.drawable.memo_purple); // PURPLE
                break;
            }
            case 3: {
                holder.itemView.setBackgroundResource(R.drawable.memo_blue); // BLUE
                break;
            }
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result = mOpenner.deleteColumn(list.get(position)._id);
                        if (result) {
                            list.remove(filtered.get(position));
                            filtered.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "Check your list index", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDialog.dismiss();
                    }
                };

                mDialog = new AlertDialog.Builder(mContext)
                        .setTitle("메모를 삭제하시겠습니까?") //TODO String
                        .setPositiveButton("취소", cancelListener)
                        .setNegativeButton("삭제", deleteListener)
                        .create();
                mDialog.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
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


}