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
import android.view.MenuItem;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apon.notez.R;
import me.apon.notez.app.BaseActivity;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Response;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/16.
 */

public class NotePreviewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.web_view)
    WebView webView;

    private String noteServerId;

    private NoteViewModel noteViewModel;

    public static void start(Context context,String noteServerId) {
        Intent starter = new Intent(context, NotePreviewActivity.class);
        starter.putExtra("noteid",noteServerId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview_act);
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
        noteServerId = getIntent().getStringExtra("noteid");
        noteViewModel.getNoteAndContent(noteServerId);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void observeLiveData(){
        noteViewModel.noteAndContentResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(@Nullable Response response) {
                noteAndContentResponse(response);
            }
        });
    }

    private void noteAndContentResponse(Response response){
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                Note note = (Note) response.data;
                webView.loadData(note.getContent(),"text/html;charset=UTF-8", null);
                break;
            case ERROR:
                Throwable e = response.error;
                e.printStackTrace();
                break;
        }
    }

}
