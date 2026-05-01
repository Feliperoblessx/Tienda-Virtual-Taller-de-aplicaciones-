package com.example.registroventas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.registroventas.database.entities.ArticuloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticuloDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticulo(articulo: ArticuloEntity)

    @Query("SELECT * FROM articulos ORDER BY nombre ASC")
    fun getArticulos(): Flow<List<ArticuloEntity>>

    @Query("SELECT * FROM articulos WHERE id = :id LIMIT 1")
    suspend fun getArticuloById(id: String): ArticuloEntity?
}
