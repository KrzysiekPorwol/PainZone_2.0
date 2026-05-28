package com.painzone.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `training_plan` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `name` TEXT NOT NULL,
                `is_active` INTEGER NOT NULL,
                `created_at` INTEGER NOT NULL
            )
            """.trimIndent(),
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `planned_day` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `training_plan_id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `order_in_plan` INTEGER NOT NULL,
                FOREIGN KEY(`training_plan_id`) REFERENCES `training_plan`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_day_training_plan_id` ON `planned_day` (`training_plan_id`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `planned_exercise` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `planned_day_id` INTEGER NOT NULL,
                `exercise_id` INTEGER NOT NULL,
                `order_in_day` INTEGER NOT NULL,
                `target_reps` TEXT NOT NULL,
                `rest_seconds` INTEGER,
                FOREIGN KEY(`planned_day_id`) REFERENCES `planned_day`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`exercise_id`) REFERENCES `exercise`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_exercise_planned_day_id` ON `planned_exercise` (`planned_day_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_exercise_exercise_id` ON `planned_exercise` (`exercise_id`)")
    }
}