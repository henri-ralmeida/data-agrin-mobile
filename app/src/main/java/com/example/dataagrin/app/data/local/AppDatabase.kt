package com.example.dataagrin.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.model.Task

@Database(
    entities = [Task::class, TaskRegistry::class, WeatherCache::class, HourlyWeatherCache::class], 
    version = 8, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskRegistryDao(): TaskRegistryDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migração de v4 para v5: Adiciona coluna isModified na tabela activities
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE activities ADD COLUMN isModified INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migração de v5 para v6: Adiciona coluna isDeleted na tabela activities
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE activities ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migração de v6 para v7: Remove colunas remoteId e lastSyncedAt da tabela tasks
        // Como SQLite não suporta DROP COLUMN em versões antigas, recriamos a tabela
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Criar nova tabela com schema correto (sem remoteId e lastSyncedAt)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tasks_new (
                        id INTEGER PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        area TEXT NOT NULL,
                        scheduledTime TEXT NOT NULL,
                        endTime TEXT NOT NULL,
                        observations TEXT NOT NULL,
                        status TEXT NOT NULL,
                        syncStatus TEXT NOT NULL DEFAULT 'LOCAL',
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                
                // Copiar dados da tabela antiga para a nova
                database.execSQL("""
                    INSERT INTO tasks_new (id, name, area, scheduledTime, endTime, observations, status, syncStatus, createdAt, updatedAt)
                    SELECT id, name, area, scheduledTime, endTime, observations, status, 
                           COALESCE(syncStatus, 'LOCAL'), 
                           COALESCE(createdAt, 0), 
                           COALESCE(updatedAt, 0) 
                    FROM tasks
                """.trimIndent())
                
                // Remover tabela antiga
                database.execSQL("DROP TABLE tasks")
                
                // Renomear nova tabela
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }

        // Migração de v7 para v8: Corrige migração anterior que estava incompleta
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adicionar colunas que faltavam na migração anterior
                try {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'LOCAL'")
                } catch (e: Exception) { /* Coluna já existe */ }
                try {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) { /* Coluna já existe */ }
                try {
                    database.execSQL("ALTER TABLE tasks ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) { /* Coluna já existe */ }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "data_agrin_database"
                ).addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
