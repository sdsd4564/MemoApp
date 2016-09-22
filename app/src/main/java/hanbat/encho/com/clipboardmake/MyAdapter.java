package hanbat.encho.com.clipboardmake;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by USER on 2016-09-09.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
          {
    private static final String TAG = "마이어댑터";

    private Context mContext;
    private ArrayList<Entity> list = null;
    private ArrayList<Entity> filtered = null;
    private DbOpenner mOpenner;
    private boolean result;
    private AlertDialog mDialog = null;

    public MyAdapter(Context mContext, ArrayList list, ArrayList filtered) {
        this.mContext = mContext;
        this.list = list;
        this.filtered = filtered;
        mOpenner = DbOpenner.getInstance(mContext);
        mOpenner.open();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_content_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(filtered.get(position).memo);

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
