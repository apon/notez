package me.apon.notez.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

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

    @Query("select id _id , * from notes where userId = :userid and title like '%' || :keyword || '%'")
    Cursor searchByTitle(String userid, String keyword);

    @Query("select * from notes where noteId = :serverId")
    Note getByServerId(String serverId);

    @Query("select * from notes where noteId = :serverId")
    Single<Note> getByServerIdX(String serverId);

    @Query("select * from notes where userId = :userId and notebookId = :bookId order by updatedTimeInMills desc")
    Single<List<Note>> getByBookIdX(String userId,String bookId);

    @Query("select * from notes where id = :localId")
    Single<Note> getByLocalId(long localId);

    @Query("select * from notes where userId = :userId order by updatedTimeInMills desc" )
    Single<List<Note>> getAllNotesX(String userId);

    @Query("select * from notes where userId = :userId and isTrash = 'true'")
    Single<List<Note>> getAllDirtyNotes(String userId);

    @Query("delete from notes where userId = :userId")
    void deleteAll(String userId);
}
