package me.apon.notez.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.data.database.dao.NoteBookDao;
import me.apon.notez.data.database.dao.NoteDao;
import me.apon.notez.data.database.dao.TagDao;
import me.apon.notez.data.model.Account;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.Notebook;
import me.apon.notez.data.model.Tag;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/5.
 */
@Database(entities = {Account.class, Tag.class, Notebook.class, Note.class}, version = 2)

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;


    public abstract AccountDao accountDao();

    public abstract TagDao tagDao();

    public abstract NoteBookDao noteBookDao();

    public abstract NoteDao noteDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "notezdatabase.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}


