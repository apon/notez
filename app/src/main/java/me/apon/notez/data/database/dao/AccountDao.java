package me.apon.notez.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import io.reactivex.Single;
import me.apon.notez.data.model.Account;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/5.
 */
@Dao
public interface AccountDao {

    @Query("select * from accounts where Email = :email limit 1")
    Account getAccount(String email);

    @Query("select * from accounts where Email = :email limit 1")
    Single<Account> getAccountX(String email);

    @Query("select * from accounts where Token != '' limit 1")
    Account getCurrent();

    @Query("select * from accounts where Token != '' limit 1")
    Single<Account> getCurrentX();

    @Query("select * from accounts where id = :id limit 1")
    Single<Account> getAccountById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAccount(Account account);

    @Query("delete from accounts where Token = :token")
    void deleteAccout(String token);

}
