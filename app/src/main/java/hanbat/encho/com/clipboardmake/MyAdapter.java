package hanbat.encho.com.clipboardmake;

import android.content.ClipData;
import android.content.Context;
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
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "마이어댑터";

    private Context mContext;
    private ArrayList<Entity> list = null;
    private ArrayList<Entity> filtered = null;
    private DbOpenner mOpenner;
    private boolean result;

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
        holder.mTextView.setText(new StringBuilder().append(filtered.get(position).memo).append(" - ").append(position).toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                result = mOpenner.deleteColumn(list.get(position)._id);

                if (result) {
                    filtered.remove(position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Check your list index", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public void setFilter(ArrayList<Entity> memberModels, String text) {
        if (memberModels.size() > 0) {
            filtered.clear();
            filtered.addAll(memberModels);
        } else if (text != null && text.length() < 1) {
            filtered.clear();
            filtered.addAll(list);
        }
        notifyDataSetChanged();
    }
}
