package com.example.registroventas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.registroventas.database.entities.VentasEntity
import com.example.registroventas.database.model.VentaDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenta(venta: VentasEntity)

    @Query("SELECT DISTINCT grupo FROM ventas ORDER BY grupo ASC")
    fun getGruposConVentas(): Flow<List<Int>>

    @Query(
        """
        SELECT a.nombre, a.descripcion, a.precioUnitario AS precio_unitario, v.cantidad, v.sub_total
        FROM ventas v
        INNER JOIN articulos a ON v.id_articulo = a.id
        WHERE v.grupo = :grupo
        ORDER BY v.id DESC
        """
    )
    fun getVentasDetalleByGrupo(grupo: Int): Flow<List<VentaDetalle>>
}
