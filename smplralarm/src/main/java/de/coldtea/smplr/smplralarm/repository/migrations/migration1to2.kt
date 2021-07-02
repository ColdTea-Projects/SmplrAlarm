package de.coldtea.smplr.smplralarm.repository.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE alarm_notification_table ADD COLUMN info_pairs TEXT NOT NULL DEFAULT '' ")
    }
}