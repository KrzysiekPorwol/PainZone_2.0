package com.painzone.data.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("UPDATE planned_exercise SET order_in_day = :order WHERE id = :id")
    suspend fun updateOrder(id: Long, order: Int)

    // Atomic: every item gets order = its index, or none does.
    @Transaction
    suspend fun reorderInDay(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id -> updateOrder(id, index) }
    }

    @Query(
        """
        SELECT COUNT(DISTINCT pd.training_plan_id)
        FROM planned_exercise pe
        JOIN planned_day pd ON pe.planned_day_id = pd.id
        WHERE pe.exercise_id = :exerciseId
        """,
    )
    suspend fun countDistinctPlansForExercise(exerciseId: Long): Int
}