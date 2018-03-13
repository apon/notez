package me.apon.notez.data.network.api;

import java.util.List;

import io.reactivex.Observable;
import me.apon.notez.data.model.Tag;
import me.apon.notez.data.model.UpdateRe;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public interface TagApi {

    /**
     *获取需要同步的标签
        参数: afterUsn(int, 在此usn后的标签是需要同步的), maxEntry(int, 最大要同步的量)
     * @param afterUsn
     * @param maxEntry
     * @return
     */
    @GET("/api/tag/getSyncTags")
    Observable<List<Tag>> getSyncTags(@Query("afterUsn")int afterUsn, @Query("maxEntry")int maxEntry);

    /**
     * 添加标签
        参数: tag(string)
     * @param tag
     * @return
     */
    @POST("/api/tag/addTag ")
    Observable<Tag> addTag(@Query("tag")String tag);

    /**
     * 删除标签
        参数: tag(string),usn(int)
     * @param tag
     * @param usn
     * @return
     */
    @POST("/api/tag/deleteTag")
    Observable<UpdateRe> deleteTag(@Query("tag")String tag,@Query("usn")int usn);
}
