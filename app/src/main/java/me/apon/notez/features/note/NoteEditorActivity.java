package me.apon.notez.features.note;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.richeditor.RichEditor;
import me.apon.notez.R;
import me.apon.notez.app.BaseActivity;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/4.
 */

public class NoteEditorActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.rich_editor)
    RichEditor richEditor;
    @BindView(R.id.et_title)
    EditText etTitle;

    private NoteViewModel noteViewModel;
    private List<Notebook> notebookList;
    private Note note;
    private String noteId;

    public static void start(Context context) {
        Intent starter = new Intent(context, NoteEditorActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }

    public static void start(Context context, String noteid) {
        Intent starter = new Intent(context, NoteEditorActivity.class);
        starter.putExtra("noteid", noteid);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor_act);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        observeLiveData();
        initView();
    }

    private void initView() {
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("内容");
        noteId = getIntent().getStringExtra("noteid");
        if (TextUtils.isEmpty(noteId)) {//新增
            note = new Note();
            note.setMarkdown(false);
            note.setDirty(false);
            note.setBlog(false);
            note.setTrash(false);
            note.setTitle("未命名");
        } else {//编辑
            noteViewModel.getNoteByServerId(noteId);
        }

        noteViewModel.getNoteBooks();
    }

    private void observeLiveData() {
        noteViewModel.saveNoteResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                saveNoteResponse(response);
            }
        });
        noteViewModel.noteBooksResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteBooksResponse(response);
            }
        });
        noteViewModel.noteResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteResponse(response);
            }
        });
    }

    private void saveNoteResponse(Response response) {
        switch (response.status) {
            case LOADING:
                showLoadingDialog("正在保存笔记...");
                break;
            case SUCCESS:
                dismissLoadingDialog();
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case ERROR:
                dismissLoadingDialog();
                Throwable e = response.error;
                e.printStackTrace();
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void noteBooksResponse(Response response) {
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                notebookList = (List<Notebook>) response.data;
                if (notebookList != null && notebookList.size() > 0 && note != null&&TextUtils.isEmpty(noteId)) {
                    Notebook notebook = notebookList.get(0);
                    note.setNotebookId(notebook.getNotebookId());
                }
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    private void noteResponse(Response response) {
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                note = (Note) response.data;
                etTitle.setText(note.getTitle());
                richEditor.setHtml(note.getContent());
                //richEditor.loadData(note.getContent(), "text/html;charset=UTF-8", null);
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_save) {
            saveNote();
        }
        if (id == R.id.action_settings) {
            noteSetting();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        long updateTime = System.currentTimeMillis();
        note.setUpdatedTimeInMills(updateTime);
        String content = richEditor.getHtml();
        note.setContent(content);
        String title = etTitle.getText().toString();
        note.setTitle(TextUtils.isEmpty(title)?"未命名":title);
        if (TextUtils.isEmpty(noteId)) {//新建
            note.setCreatedTimeInMills(updateTime);
            noteViewModel.saveNote(note);
        } else {//更新
            noteViewModel.updateNote(note);
        }
    }

    private void noteSetting() {
        if (notebookList == null || notebookList.size() <= 0) {
            return;
        }
        int position = 0;
        final List<String> list = new ArrayList();
        Log.d("editor","note noteBookid: "+note.getNotebookId());
        for (int i=0;i<notebookList.size();i++) {
            Notebook notebook = notebookList.get(i);
            list.add(notebook.getTitle());
            Log.d("editor","noteBookid: "+notebook.getNotebookId()+" title： "+notebook.getTitle());
            if (notebook.getNotebookId().equals(note.getNotebookId())){
                position = i;
            }
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("笔记本")
                .items(list)
                .itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Notebook notebook = notebookList.get(which);
                        note.setNotebookId(notebook.getNotebookId());
                        return true;
                    }
                })
                .positiveText("选择")
                .build();
        dialog.show();
    }

    @OnClick({R.id.action_undo, R.id.action_redo, R.id.action_bold, R.id.action_strikethrough, R.id.action_underline, R.id.action_heading1, R.id.action_heading2, R.id.action_heading3, R.id.action_heading4, R.id.action_heading5, R.id.action_heading6, R.id.action_insert_bullets, R.id.action_insert_numbers, R.id.action_insert_checkbox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_undo:
                richEditor.undo();
                break;
            case R.id.action_redo:
                richEditor.redo();
                break;
            case R.id.action_bold:
                richEditor.setBold();
                break;
            case R.id.action_strikethrough:
                richEditor.setStrikeThrough();
                break;
            case R.id.action_underline:
                richEditor.setUnderline();
                break;
            case R.id.action_heading1:
                richEditor.setHeading(1);
                break;
            case R.id.action_heading2:
                richEditor.setHeading(2);
                break;
            case R.id.action_heading3:
                richEditor.setHeading(3);
                break;
            case R.id.action_heading4:
                richEditor.setHeading(4);
                break;
            case R.id.action_heading5:
                richEditor.setHeading(5);
                break;
            case R.id.action_heading6:
                richEditor.setHeading(6);
                break;
            case R.id.action_insert_bullets:
                richEditor.setBullets();
                break;
            case R.id.action_insert_numbers:
                richEditor.setNumbers();
                break;
            case R.id.action_insert_checkbox:
                richEditor.insertTodo();
                break;
        }
    }
}
