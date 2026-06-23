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

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_session` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `planned_day_id` INTEGER,
                `plan_name_snapshot` TEXT NOT NULL,
                `day_name_snapshot` TEXT NOT NULL,
                `started_at` INTEGER NOT NULL,
                `finished_at` INTEGER,
                FOREIGN KEY(`planned_day_id`) REFERENCES `planned_day`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_session_planned_day_id` ON `workout_session` (`planned_day_id`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `session_exercise_snapshot` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `session_id` INTEGER NOT NULL,
                `exercise_id` INTEGER NOT NULL,
                `exercise_name_snapshot` TEXT NOT NULL,
                `muscle_group_snapshot` TEXT NOT NULL,
                `order_in_session` INTEGER NOT NULL,
                `planned_target_reps` TEXT NOT NULL,
                `planned_rest_seconds` INTEGER,
                FOREIGN KEY(`session_id`) REFERENCES `workout_session`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`exercise_id`) REFERENCES `exercise`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercise_snapshot_session_id` ON `session_exercise_snapshot` (`session_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercise_snapshot_exercise_id` ON `session_exercise_snapshot` (`exercise_id`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `logged_set` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `session_exercise_snapshot_id` INTEGER NOT NULL,
                `order_in_exercise` INTEGER NOT NULL,
                `reps` INTEGER NOT NULL,
                `weight` REAL NOT NULL,
                `rpe` INTEGER,
                `completed_at` INTEGER NOT NULL,
                FOREIGN KEY(`session_exercise_snapshot_id`) REFERENCES `session_exercise_snapshot`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_logged_set_session_exercise_snapshot_id` ON `logged_set` (`session_exercise_snapshot_id`)")
    }
}

// Rest Timer (M3.7): persist the actual rest before each set instead of deriving it (ADR-0008).
val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `logged_set` ADD COLUMN `rest_before_seconds` INTEGER")
    }
}