package me.apon.notez.features.note.adapter;


import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import me.apon.notez.R;
import me.apon.notez.data.model.Note;
import me.apon.notez.utils.TimeUtils;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/13.
 */

public class NoteListAdapter extends BaseQuickAdapter<Note,BaseViewHolder> {

    public NoteListAdapter() {
        super(R.layout.note_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, Note item) {
        TextView tvTitle = helper.getView(R.id.tv_title);
        TextView tvTime = helper.getView(R.id.tv_time);
        //TextView tvBook = helper.getView(R.id.tv_book);
        String title = TextUtils.isEmpty(item.getTitle())?"未命名":item.getTitle();
        tvTitle.setText(title);
        tvTime.setText(TimeUtils.toDateFormat(item.getUpdatedTimeInMills()));
        //tvBook.setText(item.getNotebookId());
    }
}
