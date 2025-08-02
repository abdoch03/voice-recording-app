package com.example.voice_memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceMemoAdapter extends BaseAdapter {
    private String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    private Context context;
    private ArrayList<VoiceMemo> memos;

    public interface OnMemoActionListener {
        void onRename(VoiceMemo memo);
        void onDelete(VoiceMemo memo);
    }

    private OnMemoActionListener actionListener;

    public void setOnMemoActionListener(OnMemoActionListener listener) {
        this.actionListener = listener;
    }

    public VoiceMemoAdapter(Context context, ArrayList<VoiceMemo> memos) {
        this.context = context;
        this.memos = memos;
    }

    @Override
    public int getCount() {
        return memos.size();
    }

    @Override
    public Object getItem(int position) {
        return memos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView memoName;
        TextView memoPath;
        ImageView memoOptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_memo, parent, false);
            holder = new ViewHolder();
            holder.memoName = convertView.findViewById(R.id.memoName);
            holder.memoPath = convertView.findViewById(R.id.memoPath);
            holder.memoOptions = convertView.findViewById(R.id.memoOptions);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VoiceMemo memo = memos.get(position);
        holder.memoName.setText(memo.getName());
        holder.memoPath.setText(formatDuration(memo.getDuration()) + " • " + memo.getDate());


        holder.memoOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.memo_item_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.rename_memo) {
                    if (actionListener != null) {
                        actionListener.onRename(memo);
                    }
                    return true;
                } else if (item.getItemId() == R.id.delete_memo) {
                    if (actionListener != null) {
                        actionListener.onDelete(memo);
                    }
                    return true;
                }
                return false;
            });

            popup.show();
        });

        return convertView;
    }
    public void clear() {
        memos.clear();
    }

    public void addAll(ArrayList<VoiceMemo> newMemos) {
        memos.addAll(newMemos);
    }

}
