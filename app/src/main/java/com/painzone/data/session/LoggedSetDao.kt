package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedSetDao {

    @Insert
    suspend fun insert(entity: LoggedSetEntity): Long

    @Update
    suspend fun update(entity: LoggedSetEntity)

    @Query("DELETE FROM logged_set WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM logged_set WHERE id = :id")
    suspend fun getById(id: Long): LoggedSetEntity?

    @Query("SELECT * FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId ORDER BY order_in_exercise")
    fun observeBySnapshotId(snapshotId: Long): Flow<List<LoggedSetEntity>>

    @Query("SELECT * FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId ORDER BY order_in_exercise")
    suspend fun getBySnapshotId(snapshotId: Long): List<LoggedSetEntity>

    @Query("SELECT MAX(order_in_exercise) FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId")
    suspend fun maxOrderInSnapshot(snapshotId: Long): Int?

    @Query("UPDATE logged_set SET order_in_exercise = :order WHERE id = :id")
    suspend fun updateOrder(id: Long, order: Int)

    // Re-sequence after a delete so order stays 1..n contiguous within the exercise.
    @Transaction
    suspend fun resequence(snapshotId: Long) {
        getBySnapshotId(snapshotId).forEachIndexed { index, set -> updateOrder(set.id, index + 1) }
    }
}
