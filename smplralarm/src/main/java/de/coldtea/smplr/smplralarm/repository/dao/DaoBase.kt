package de.coldtea.smplr.smplralarm.repository.dao

import androidx.room.*

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
internal interface DaoBase<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)

}

@Transaction
internal suspend inline fun <reified T> DaoBase<T>.insertOrUpdate(item: T) {
    if (insert(item) != -1L) return
    update(item)
}