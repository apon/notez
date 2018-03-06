package me.apon.notez.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;
import me.apon.notez.data.model.Note;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/5.
 */
@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(Note note);

    @Query("select * from notes where userId = :userid and title like :keyword")
    Single<List<Note>> searchByTitle(String userid,String keyword);

    @Query("select * from notes where noteId = :serverId")
    Single<Note> getByServerId(String serverId);

    @Query("select * from notes where id = :localId")
    Single<Note> getByLocalId(long localId);

    @Query("select * from notes where userId = :userId")
    Single<List<Note>> getAllNotes(String userId);

    @Query("select * from notes where userId = :userId and isTrash = 'true'")
    Single<List<Note>> getAllDirtyNotes(String userId);

    @Query("delete from notes where userId = :userId")
    void deleteAll(String userId);
}
