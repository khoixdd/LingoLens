package com.example.lingolens.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `vocabulary_new` (
                `userId` TEXT NOT NULL,
                `id` TEXT NOT NULL,
                `word` TEXT NOT NULL,
                `meaning` TEXT NOT NULL,
                `pronunciation` TEXT NOT NULL,
                `partOfSpeech` TEXT NOT NULL,
                `example` TEXT NOT NULL,
                `tags` TEXT NOT NULL,
                `isFavorite` INTEGER NOT NULL,
                `masteryLevel` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `lastReviewedAt` INTEGER,
                `nextReviewAt` INTEGER,
                `correctCount` INTEGER NOT NULL,
                `wrongCount` INTEGER NOT NULL,
                `source` TEXT NOT NULL,
                PRIMARY KEY(`userId`, `id`)
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            INSERT INTO `vocabulary_new` (
                `userId`, `id`, `word`, `meaning`, `pronunciation`, `partOfSpeech`,
                `example`, `tags`, `isFavorite`, `masteryLevel`, `createdAt`,
                `lastReviewedAt`, `nextReviewAt`, `correctCount`, `wrongCount`, `source`
            )
            SELECT '', `id`, `word`, `meaning`, `pronunciation`, `partOfSpeech`,
                `example`, `tags`, `isFavorite`, `masteryLevel`, `createdAt`,
                `lastReviewedAt`, `nextReviewAt`, `correctCount`, `wrongCount`, `source`
            FROM `vocabulary`
            """.trimIndent(),
        )
        database.execSQL("DROP TABLE `vocabulary`")
        database.execSQL("ALTER TABLE `vocabulary_new` RENAME TO `vocabulary`")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `daily_word_activity` (
                `userId` TEXT NOT NULL,
                `epochDay` INTEGER NOT NULL,
                `vocabularyId` TEXT NOT NULL,
                `recordedAt` INTEGER NOT NULL,
                PRIMARY KEY(`userId`, `epochDay`, `vocabularyId`)
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `notification_settings` (
                `userId` TEXT NOT NULL,
                `dailyReminderEnabled` INTEGER NOT NULL,
                `dailyGoalReminderEnabled` INTEGER NOT NULL,
                `reviewReminderEnabled` INTEGER NOT NULL,
                `achievementNotificationsEnabled` INTEGER NOT NULL,
                `streakAlertsEnabled` INTEGER NOT NULL,
                `reminderHour` INTEGER NOT NULL,
                `reminderMinute` INTEGER NOT NULL,
                PRIMARY KEY(`userId`)
            )
            """.trimIndent(),
        )
    }
}

