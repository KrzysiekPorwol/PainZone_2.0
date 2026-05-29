package com.painzone.data.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.painzone.data.plan.relation.PlanSummaryRow
import com.painzone.data.plan.relation.PlanWithDays
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingPlanDao {

    @Insert
    suspend fun insert(entity: TrainingPlanEntity): Long

    @Update
    suspend fun update(entity: TrainingPlanEntity)

    @Query("DELETE FROM training_plan WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM training_plan WHERE id = :id")
    suspend fun getById(id: Long): TrainingPlanEntity?

    @Query("SELECT * FROM training_plan ORDER BY created_at DESC")
    fun observeAll(): Flow<List<TrainingPlanEntity>>

    @Query(
        """
        SELECT tp.*, (
            SELECT COUNT(*) FROM planned_day pd
            WHERE pd.training_plan_id = tp.id
        ) AS day_count
        FROM training_plan tp
        ORDER BY tp.created_at DESC
        """,
    )
    fun observeSummaries(): Flow<List<PlanSummaryRow>>

    @Query("SELECT * FROM training_plan WHERE is_active = 1 LIMIT 1")
    fun observeActive(): Flow<TrainingPlanEntity?>

    @Query("SELECT * FROM training_plan WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): TrainingPlanEntity?

    @Query("UPDATE training_plan SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE training_plan SET is_active = 1 WHERE id = :id")
    suspend fun activateById(id: Long)

    @Transaction
    suspend fun activateExclusive(id: Long) {
        deactivateAll()
        activateById(id)
    }

    @Transaction
    @Query("SELECT * FROM training_plan WHERE id = :id")
    suspend fun getWithDays(id: Long): PlanWithDays?

    @Transaction
    @Query("SELECT * FROM training_plan WHERE id = :id")
    fun observeWithDays(id: Long): Flow<PlanWithDays?>
}