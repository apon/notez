package me.apon.notez.network.api;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.apon.notez.data.model.Note;
import me.apon.notez.data.model.UpdateRe;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public interface NoteApi {
    /**
     * 获取需要同步的笔记
       参数: afterUsn(int, 在此usn后的笔记是需要同步的), maxEntry(int, 最大要同步的量)
     * @param afterUsn
     * @param maxEntry
     * @return
     */
    @GET("/api/note/getSyncNotes")
    Observable<List<Note>> getSyncNotes(@Query("afterUsn") int afterUsn, @Query("maxEntry") int maxEntry);

    /**
     * 获得某笔记本下的笔记(无内容)
       参数: notebookId
     * @param notebookId
     * @return
     */
    @GET("/api/note/getNotes")
    Observable<List<Note>> getNotes(@Query("notebookId") String notebookId);

    /**
     *获得笔记与内容
        参数: noteId
     * @param noteId
     * @return
     */
    @GET("/api/note/getNoteAndContent")
    Observable<Note> getNoteAndContent(@Query("noteId") String noteId);

    /**
     * 添加笔记
         参数: (注意首字大写)
         NotebookId string 必传
         Title string 必传
         Tags []string 可选
         Content string 必传
         Abstract string 可选, 当是markdown笔记时必须传
         IsMarkdown bool 可选
         CreatedTime string 可选, 如2012-12-01 12:32:11
         UpdatedTime string 可选, 如2012-12-01 12:32:11
         Files []type.NoteFiles 数组  可选
     * @param body
     * @param files
     * @return
     */
    @Multipart
    @POST("/api/note/addNote")
    Observable<Note> add(@PartMap Map<String, RequestBody> body, @Part List<MultipartBody.Part> files);

    /**
     * 更新笔记
     * 参数: (注意首字大写)
         NoteId string 必传
         Usn int 必传
         NotebookId string 可选
         Title string 可选
         Tags []string 可选
         Content string 可选
         Abstract string 可选, 当是markdown笔记时必须传
         IsMarkdown bool 可选
         IsTrash bool 是否是trash 可选
         UpdatedTime string 可选, 如2012-12-01 12:32:11
         Files []type.NoteFiles 数组  可选
     * @param body
     * @param files
     * @return
     */
    @Multipart
    @POST("/api/note/updateNote")
    Observable<Note> update(@PartMap Map<String, RequestBody> body, @Part List<MultipartBody.Part> files);

    /**
     *彻底删除笔记
        参数: noteId, usn
     * @param noteId
     * @param usn
     * @return
     */
    @POST("/api/note/deleteTrash")
    Observable<UpdateRe> delete(@Query("noteId") String noteId, @Query("usn") int usn);
}
