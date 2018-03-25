package me.apon.notez.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import me.apon.notez.data.model.Notebook;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */
@Dao
public interface NoteBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(Notebook notebook);

    @Query("select * from notebooks where NotebookId = :id limit 1")
    Notebook getByServerId(String id);

    @Query("select * from notebooks where userId = :userid")
    Single<List<Notebook>> getAllNoteBook(String userid);
}
