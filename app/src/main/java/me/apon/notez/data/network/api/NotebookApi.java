package me.apon.notez.data.network.api;


import java.util.List;

import io.reactivex.Observable;
import me.apon.notez.data.model.BaseResponse;
import me.apon.notez.data.model.Notebook;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public interface NotebookApi {

    /**
     * 得到需要同步的笔记本
     * afterUsn(int, 在此usn后的笔记本是需要同步的), maxEntry(int, 最大要同步的量)
     * @param afterUsn
     * @param maxEntry
     * @return
     */
    @GET("/api/notebook/getSyncNotebooks")
    Observable<List<Notebook>> getSyncNotebooks(@Query("afterUsn")int afterUsn,@Query("maxEntry")int maxEntry);

    /**
     * 得到所有笔记本
     * @return
     */
    @GET("/api/notebook/getNotebooks")
    Observable<List<Notebook>> getNotebooks();

    /**
     * 添加笔记本
     * 参数: title(string), parentNotebookId(string, 父notebookId, 可空), seq(int) 排列
     * @param title
     * @param parentId
     * @return
     */
    @POST("/api/notebook/addNotebook")
    Observable<Notebook> addNotebook(@Query("title") String title, @Query("parentNotebookId") String parentId);

    /**
     * 修改笔记本
     * 参数: notebookId, title, parentNotebookId, seq(int), usn(int)
     * @param notebookId
     * @param title
     * @param parentId
     * @param seq
     * @param usn
     * @return
     */
    @POST("/api/notebook/updateNotebook")
    Observable<Notebook> updateNotebook(@Query("notebookId") String notebookId, @Query("title") String title,
                                  @Query("parentNotebookId") String parentId, @Query("seq") int seq, @Query("usn") int usn);

    /**
     * 删除笔记本
     * @param notebookId
     * @param usn
     * @return
     */
    @POST("/api/notebook/deleteNotebook")
    Observable<BaseResponse> deleteNotebook(@Query("notebookId") String notebookId,@Query("usn") int usn);
}
