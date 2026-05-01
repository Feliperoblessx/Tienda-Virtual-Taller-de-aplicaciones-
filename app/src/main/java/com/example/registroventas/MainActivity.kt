package com.example.registroventas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.registroventas.database.AppDatabase
import com.example.registroventas.database.dao.ArticuloDao
import com.example.registroventas.database.dao.VentaDao
import com.example.registroventas.database.entities.ArticuloEntity
import com.example.registroventas.database.entities.VentasEntity
import com.example.registroventas.database.model.VentaDetalle
import com.example.registroventas.ui.theme.RegistroVentasTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getInstance(applicationContext)
        setContent {
            RegistroVentasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        articuloDao = database.articuloDao(),
                        ventaDao = database.ventaDao(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(
    articuloDao: ArticuloDao,
    ventaDao: VentaDao,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio", modifier = modifier) {
        composable("inicio") {
            PantallaPrincipal(navController = navController)
        }
        composable("consultar_factura") {
            PantallaConsultarFactura(navController = navController, ventaDao = ventaDao)
        }
        composable("registrar_venta") {
            PantallaRegistrarVenta(
                navController = navController,
                articuloDao = articuloDao,
                ventaDao = ventaDao
            )
        }
        composable("registrar_articulos") {
            PantallaRegistrarArticulos(navController = navController, articuloDao = articuloDao)
        }
    }
}

@Composable
private fun PantallaPrincipal(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar ventas")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("consultar_factura") }, modifier = Modifier.fillMaxWidth()) {
            Text("Consultar factura")
        }
        Button(onClick = { navController.navigate("registrar_venta") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar venta")
        }
        Button(onClick = { navController.navigate("registrar_articulos") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar articulos")
        }
    }
}

@Composable
private fun PantallaConsultarFactura(
    navController: NavHostController,
    ventaDao: VentaDao
) {
    val scope = rememberCoroutineScope()
    var grupos by remember { mutableStateOf<List<Int>>(emptyList()) }
    var grupoSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var detalles by remember { mutableStateOf<List<VentaDetalle>>(emptyList()) }
    var mostrarMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ventaDao.getGruposConVentas().collectLatest { lista ->
            grupos = lista
            if (grupoSeleccionado == null && lista.isNotEmpty()) {
                grupoSeleccionado = lista.first()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Consultar factura")
        Text("Grupo")
        Button(onClick = { mostrarMenu = true }, modifier = Modifier.fillMaxWidth()) {
            Text(grupoSeleccionado?.toString() ?: "Seleccionar grupo")
        }
        DropdownMenu(expanded = mostrarMenu, onDismissRequest = { mostrarMenu = false }) {
            grupos.forEach { grupo ->
                DropdownMenuItem(
                    text = { Text(grupo.toString()) },
                    onClick = {
                        grupoSeleccionado = grupo
                        mostrarMenu = false
                    }
                )
            }
        }

        Button(
            onClick = {
                val grupo = grupoSeleccionado ?: return@Button
                scope.launch {
                    detalles = ventaDao.getVentasDetalleByGrupo(grupo).first()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        TablaFacturas(detalles = detalles)

        Text("TOTAL FACTURA")
        Text(detalles.sumOf { it.subTotal }.toString())

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Composable
private fun TablaFacturas(detalles: List<VentaDetalle>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Producto")
        Text("Desc")
        Text("P.Unit")
        Text("Cant")
        Text("Sub")
    }
    detalles.forEach { venta ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(venta.nombre)
            Text(venta.descripcion)
            Text(venta.precioUnitario.toString())
            Text(venta.cantidad.toString())
            Text(venta.subTotal.toString())
        }
    }
}

@Composable
private fun PantallaRegistrarVenta(
    navController: NavHostController,
    articuloDao: ArticuloDao,
    ventaDao: VentaDao
) {
    val scope = rememberCoroutineScope()
    var articulos by remember { mutableStateOf<List<ArticuloEntity>>(emptyList()) }
    var articuloSeleccionado by remember { mutableStateOf<ArticuloEntity?>(null) }
    var grupo by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var mostrarArticulos by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        articuloDao.getArticulos().collectLatest { lista ->
            articulos = lista
            if (articuloSeleccionado == null && lista.isNotEmpty()) {
                articuloSeleccionado = lista.first()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar ventas")
        Text("Articulo")
        Button(onClick = { mostrarArticulos = true }, modifier = Modifier.fillMaxWidth()) {
            Text(articuloSeleccionado?.nombre ?: "Seleccionar articulo")
        }
        DropdownMenu(expanded = mostrarArticulos, onDismissRequest = { mostrarArticulos = false }) {
            articulos.forEach { articulo ->
                DropdownMenuItem(
                    text = { Text("${articulo.nombre} - ${articulo.precioUnitario}") },
                    onClick = {
                        articuloSeleccionado = articulo
                        mostrarArticulos = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = grupo,
            onValueChange = { grupo = it.filter(Char::isDigit) },
            label = { Text("Grupo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it.filter(Char::isDigit) },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val articulo = articuloSeleccionado ?: return@Button
                val grupoInt = grupo.toIntOrNull() ?: return@Button
                val cantidadInt = cantidad.toIntOrNull() ?: return@Button
                if (cantidadInt <= 0) return@Button
                val subTotal = articulo.precioUnitario * cantidadInt

                scope.launch {
                    ventaDao.insertVenta(
                        VentasEntity(
                            grupo = grupoInt,
                            idArticulo = articulo.id,
                            cantidad = cantidadInt,
                            subTotal = subTotal
                        )
                    )
                    grupo = ""
                    cantidad = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar")
        }
    }
}

@Composable
private fun PantallaRegistrarArticulos(
    navController: NavHostController,
    articuloDao: ArticuloDao
) {
    val scope = rememberCoroutineScope()
    var id by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precioUnitario by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar articulos")
        OutlinedTextField(
            value = id,
            onValueChange = { id = it.trim() },
            label = { Text("Id") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precioUnitario,
            onValueChange = { precioUnitario = it.filter(Char::isDigit) },
            label = { Text("Precio unitario") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val precio = precioUnitario.toIntOrNull() ?: return@Button
                if (id.isBlank() || nombre.isBlank() || descripcion.isBlank()) return@Button
                scope.launch {
                    articuloDao.upsertArticulo(
                        ArticuloEntity(
                            id = id,
                            nombre = nombre,
                            descripcion = descripcion,
                            precioUnitario = precio
                        )
                    )
                    id = ""
                    nombre = ""
                    descripcion = ""
                    precioUnitario = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar")
        }
    }
}