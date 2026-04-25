package com.example.registroventas.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos")
data class ArticuloEntity(

    @PrimaryKey
    val id: String,

    val nombre: String,

    val descripcion: String,

    val precioUnitario: Int
)
