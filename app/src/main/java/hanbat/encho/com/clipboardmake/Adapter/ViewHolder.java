package hanbat.encho.com.clipboardmake.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import hanbat.encho.com.clipboardmake.R;

/**
 * Created by USER on 2016-09-23.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;
    View mView;
    public View checkedView;
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    OnItemClickListener mClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener = listener;
    }

    public ViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mTextView = (TextView) itemView.findViewById(R.id.item_content_text);
        checkedView = itemView.findViewById(R.id.selected);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        });
    }
    public void setChecked(boolean checked) {
        if (mView instanceof Checkable){
            ((Checkable) mView).setChecked(checked);
        }
    }
}
