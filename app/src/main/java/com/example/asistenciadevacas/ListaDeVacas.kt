package com.example.asistenciadevacas

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaDeVacas : AppCompatActivity() {

    companion object {
        var vacaAdapter: VacaAdapter? = null
        var vacas: MutableList<VacaModel> = mutableListOf()
        var isLoading = false
        var isLastPage = false
        var currentPage = 0
        const val pageSize = 10 // Número de vacas por página
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_vacas)

        //no rotate pantalla
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val listaVacas = findViewById<RecyclerView>(R.id.rvListaVaca)
        val layoutManager = LinearLayoutManager(this)
        listaVacas.layoutManager = layoutManager

        vacaAdapter = VacaAdapter(vacas)
        listaVacas.adapter = vacaAdapter

        // Cargar la primera página de datos
        loadVacas()

        // Configurar el scroll listener para paginación
        listaVacas.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                loadVacas()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        // Configuración del botón para añadir una vaca
        val btnAniadirVaca = findViewById<Button>(R.id.btnAniadirVaca)
        btnAniadirVaca.setOnClickListener {
            val intent = Intent(this, AniadirVaca::class.java)
            startActivity(intent)
        }
    }

    private fun loadVacas() {
        val conexion = ConexionDB(this)
        val nuevasVacas = conexion.getVacasPaginadas(currentPage * pageSize, pageSize)

        // Log para verificar los datos obtenidos
        Log.d("ListaDeVacas", "Nuevas Vacas: ${nuevasVacas.size}")

        if (nuevasVacas.isNotEmpty()) {
            vacas.addAll(nuevasVacas)
            vacaAdapter?.notifyDataSetChanged()
        } else {
            isLastPage = true
        }

        isLoading = false
    }

}