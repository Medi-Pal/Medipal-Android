package com.example.medipal.data

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Utility class for database operations and troubleshooting
 */
object DatabaseHelper {
    private const val TAG = "DatabaseHelper"

    /**
     * Delete the database file completely to fix corruption issues
     * This is an extreme solution and should only be used in case of severe corruption
     * @param context Application context
     * @return true if successful, false otherwise
     */
    fun deleteDatabase(context: Context): Boolean {
        return try {
            val databaseFile = context.getDatabasePath("app_database")
            
            Log.d(TAG, "Attempting to delete database file: ${databaseFile.absolutePath}")
            
            if (databaseFile.exists()) {
                val deleted = databaseFile.delete()
                
                // Also try to delete related files (journal, shm, wal)
                val journalFile = File(databaseFile.parent, "${databaseFile.name}-journal")
                if (journalFile.exists()) {
                    journalFile.delete()
                }
                
                val shmFile = File(databaseFile.parent, "${databaseFile.name}-shm")
                if (shmFile.exists()) {
                    shmFile.delete()
                }
                
                val walFile = File(databaseFile.parent, "${databaseFile.name}-wal")
                if (walFile.exists()) {
                    walFile.delete()
                }
                
                Log.d(TAG, "Database deletion ${if (deleted) "successful" else "failed"}")
                deleted
            } else {
                Log.d(TAG, "Database file does not exist")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting database: ${e.message}", e)
            false
        }
    }
    
    /**
     * Log database file info for debugging
     * @param context Application context
     */
    fun logDatabaseInfo(context: Context) {
        try {
            val databaseFile = context.getDatabasePath("app_database")
            Log.d(TAG, "Database file exists: ${databaseFile.exists()}")
            if (databaseFile.exists()) {
                Log.d(TAG, "Database file size: ${databaseFile.length()} bytes")
                Log.d(TAG, "Database last modified: ${databaseFile.lastModified()}")
            }
            
            val journalFile = File(databaseFile.parent, "${databaseFile.name}-journal")
            Log.d(TAG, "Journal file exists: ${journalFile.exists()}")
            
            val shmFile = File(databaseFile.parent, "${databaseFile.name}-shm")
            Log.d(TAG, "SHM file exists: ${shmFile.exists()}")
            
            val walFile = File(databaseFile.parent, "${databaseFile.name}-wal")
            Log.d(TAG, "WAL file exists: ${walFile.exists()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking database files: ${e.message}", e)
        }
    }
} 