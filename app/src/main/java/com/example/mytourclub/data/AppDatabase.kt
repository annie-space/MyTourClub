package com.example.mytourclub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mytourclub.dao.*
import com.example.mytourclub.model.*
import com.example.mytourclub.until.Converters

@Database(
    entities = [
        User::class, Admin::class, Guide::class, Route::class,
        Booking::class, Rent::class, Equipment::class, RouteEquipment::class, Review::class,
        BookingRentCrossRef::class // Добавим новую таблицу
    ],
    version = 11 // Увеличьте версию базы данных
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun adminDao(): AdminDao
    abstract fun guideDao(): GuideDao
    abstract fun routeDao(): RouteDao
    abstract fun bookingDao(): BookingDao
    abstract fun rentDao(): RentDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun routeEquipmentDao(): RouteEquipmentDao
    abstract fun reviewDao(): ReviewDao
    abstract fun bookingRentCrossRefDao(): BookingRentCrossRefDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Использование разрушающей миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
