package com.painzone.data.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedExerciseDao {

    @Insert
    suspend fun insert(entity: PlannedExerciseEntity): Long

    @Update
    suspend fun update(entity: PlannedExerciseEntity)

    @Query("DELETE FROM planned_exercise WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM planned_exercise WHERE id = :id")
    suspend fun getById(id: Long): PlannedExerciseEntity?

    @Query("SELECT * FROM planned_exercise WHERE planned_day_id = :dayId ORDER BY order_in_day")
    fun observeByDayId(dayId: Long): Flow<List<PlannedExerciseEntity>>

    @Query("SELECT MAX(order_in_day) FROM planned_exercise WHERE planned_day_id = :dayId")
    suspend fun maxOrderInDay(dayId: Long): Int?
}