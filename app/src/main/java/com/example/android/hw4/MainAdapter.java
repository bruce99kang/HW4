package com.example.android.hw4;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public MainAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String className = mCursor.getString(mCursor.getColumnIndex(MainContract.MainEntry.COLUMN_CLASS_NAME));
        String classDay = mCursor.getString(mCursor.getColumnIndex(MainContract.MainEntry.COLUMN_CLASS_DAY));
        String classTime = mCursor.getString(mCursor.getColumnIndex(MainContract.MainEntry.COLUMN_CLASS_TIME));

        long id = mCursor.getLong(mCursor.getColumnIndex(MainContract.MainEntry._ID));

        holder.classNameTextView.setText(className);
        holder.classDayTextView.setText(classDay);
        holder.classTimeTextView.setText(classTime);
        holder.itemView.setTag(id);
    }
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
    class MainViewHolder extends RecyclerView.ViewHolder{
        TextView classNameTextView;
        TextView classDayTextView;
        TextView classTimeTextView;
        CheckBox checkBox;

        public MainViewHolder(View itemView){
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox) ;
            classNameTextView = (TextView) itemView.findViewById(R.id.className);
            classDayTextView = (TextView)itemView.findViewById(R.id.classDay);
            classTimeTextView=(TextView)itemView.findViewById(R.id.classTime);

        }
    }
}
