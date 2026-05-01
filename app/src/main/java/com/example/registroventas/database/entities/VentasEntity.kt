package com.example.registroventas.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ventas",
    indices = [Index(value = ["id_articulo"])],
    foreignKeys = [
        ForeignKey(
            entity = ArticuloEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_articulo"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class VentasEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grupo: Int,
    @ColumnInfo(name = "id_articulo")
    val idArticulo: String,
    val cantidad: Int,
    @ColumnInfo(name = "sub_total")
    val subTotal: Int
)