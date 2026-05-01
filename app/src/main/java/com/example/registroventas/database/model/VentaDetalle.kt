package com.example.registroventas.database.model

import androidx.room.ColumnInfo

data class VentaDetalle(
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "descripcion")
    val descripcion: String,
    @ColumnInfo(name = "precio_unitario")
    val precioUnitario: Int,
    @ColumnInfo(name = "cantidad")
    val cantidad: Int,
    @ColumnInfo(name = "sub_total")
    val subTotal: Int
)
