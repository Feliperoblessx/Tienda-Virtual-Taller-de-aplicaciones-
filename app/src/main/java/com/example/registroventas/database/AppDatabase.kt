package com.example.registroventas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.registroventas.database.dao.ArticuloDao
import com.example.registroventas.database.dao.VentaDao
import com.example.registroventas.database.entities.ArticuloEntity
import com.example.registroventas.database.entities.VentasEntity

@Database(
    entities = [ArticuloEntity::class, VentasEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articuloDao(): ArticuloDao
    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
