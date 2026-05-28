package com.painzone.data.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedDayDao {

    @Insert
    suspend fun insert(entity: PlannedDayEntity): Long

    @Update
    suspend fun update(entity: PlannedDayEntity)

    @Query("DELETE FROM planned_day WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM planned_day WHERE id = :id")
    suspend fun getById(id: Long): PlannedDayEntity?

    @Query("SELECT * FROM planned_day WHERE training_plan_id = :planId ORDER BY order_in_plan")
    fun observeByPlanId(planId: Long): Flow<List<PlannedDayEntity>>

    @Query("SELECT MAX(order_in_plan) FROM planned_day WHERE training_plan_id = :planId")
    suspend fun maxOrderInPlan(planId: Long): Int?

    @Query("SELECT * FROM planned_day WHERE training_plan_id = :planId AND name = :name LIMIT 1")
    suspend fun findInPlanByName(planId: Long, name: String): PlannedDayEntity?
}