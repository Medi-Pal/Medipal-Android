package com.example.medipal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medipal.data.dao.EmergencyContactDao
import com.example.medipal.data.dao.PrescriptionDao
import com.example.medipal.data.model.EmergencyContact
import com.example.medipal.data.model.Prescription
import com.example.medipal.data.model.PrescriptionConverters

@Database(
    entities = [
        EmergencyContact::class,
        User::class,
        Prescription::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(PrescriptionConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun userDao(): UserDao
    abstract fun prescriptionDao(): PrescriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
