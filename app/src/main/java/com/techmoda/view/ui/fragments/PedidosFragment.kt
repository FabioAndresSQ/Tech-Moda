package com.techmoda.view.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.*
import com.techmoda.R
import com.techmoda.model.Producto
import kotlinx.coroutines.*
import java.io.Serializable

class PedidosFragment : Fragment() {

    private lateinit var pedidosLayoutContainer : LinearLayout
    private lateinit var email: String
    private var pedidosRetrieved = mutableListOf<Map.Entry<String, Any>>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsEmail = context?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        email = prefsEmail?.getString("email", null)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pedidos, container, false)

        pedidosLayoutContainer = view.findViewById(R.id.pedidosLayoutContainer)

        GlobalScope.launch {
            suspend {
                getData()
                delay(500)
                withContext(Dispatchers.Main) {
                    setup()
                }
            }.invoke()
        }
        return view
    }

    private fun getData(){
        val pedidosList = mutableListOf<Map.Entry<String, Any>>()
        db.collection("factura").document(email).collection("pedidos")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Compra error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            val pedidos = dc.document.data
                            pedidos.forEach { entry ->
                                pedidosRetrieved.add(entry)
                            }
                            }
                        }
                    }

            })
    }

    private fun setup(){
        Log.d("pedidos Setup", pedidosRetrieved.toString())
        pedidosRetrieved.forEach {
            val factura = it.value as HashMap<*, *>
            val precioUnitario = factura["precioUnitario"]
            val precioIva = factura["precioIva"]
            val cantidad = factura["cantidad"]
            val precioSubtotal = factura["precioSubtotal"]
            val precioTotal = factura["precioTotal"]
            val producto = factura["producto"]

            val pedidoView = getLayoutInflater().inflate(R.layout.pedidos_item_template, pedidosLayoutContainer, false)
            pedidosLayoutContainer.addView(pedidoView)
            val precioUnitarioView = pedidoView.findViewById<TextView>(R.id.valorUnitarioLblPedido)
            val precioIvaView = pedidoView.findViewById<TextView>(R.id.valorIvaLblPedido)
            val cantidadView = pedidoView.findViewById<TextView>(R.id.cantidadLblPedido)
            val precioSubtotalView = pedidoView.findViewById<TextView>(R.id.valorSubtotalLblPedido)
            val precioTotalView = pedidoView.findViewById<TextView>(R.id.precioTotalLblPedido)
            val productoView = pedidoView.findViewById<TextView>(R.id.tituloProductoPedido)
            val layoutItemPedido = pedidoView.findViewById<LinearLayout>(R.id.layoutPedidoItems)

            precioUnitarioView.text = precioUnitario.toString()
            precioIvaView.text = precioIva.toString()
            cantidadView.text = cantidad.toString()
            precioSubtotalView.text = precioSubtotal.toString()
            precioTotalView.text = precioTotal.toString()
            productoView.text = producto.toString()
            //layoutItemPedido

            Log.d("pedidos", factura["precioUnitario"].toString())
            Log.d("pedidos for each", it.value.toString())
        }
    }

}