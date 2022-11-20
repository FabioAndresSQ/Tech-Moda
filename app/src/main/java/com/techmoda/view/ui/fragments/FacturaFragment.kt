package com.techmoda.view.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.*
import com.techmoda.R
import com.techmoda.model.Producto
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.math.roundToInt

class FacturaFragment : Fragment() {

    private var compra : MutableList<HashMap<String, Serializable>> = mutableListOf()
    private lateinit var email: String
    private lateinit var containerFactura: LinearLayout
    private lateinit var precioFinalLbl: TextView
    private lateinit var confirmarFacturaBtn: Button
    private val db = FirebaseFirestore.getInstance()
    private val iva = 1.19
    private var total = 0

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
        val view = inflater.inflate(R.layout.fragment_factura, container, false)

        containerFactura = view.findViewById(R.id.layoutForFacturaItem)
        precioFinalLbl = view.findViewById(R.id.precioFinalLbl)
        confirmarFacturaBtn = view.findViewById(R.id.confirmarFacturaBtn)

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
        db.collection("carrito").document(email).collection("cart")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Compra error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            val productoReference = dc.document.get("producto") as DocumentReference
                            productoReference.addSnapshotListener { value, error ->
                                if (error != null){
                                    Log.e("compra error", error.message.toString())
                                }
                                val producto = value?.toObject(Producto::class.java)
                                val cantidad = dc.document.get("cantidad") as Long
                                val talla = dc.document.get("talla") as String
                                compra.add(hashMapOf("producto" to producto!!, "cantidad" to cantidad, "talla" to talla))
                            }
                        }
                    }
                }

            })

    }

    private fun setup(){
        var sumaTotal = 0
        val listaFactura = HashMap<String, HashMap<String, Serializable>>()
        for (i in compra){
            val producto = i.get("producto") as Producto
            val precio = producto.precio?.toDouble()!!
            val cantidad = i.get("cantidad") as Long
            val talla = i.get("talla") as String
            val precioUnitario = precio/iva
            val subtotal = precioUnitario*cantidad
            val valorIva = precio-precioUnitario
            val precioTotal = precio*cantidad
            val factura = getLayoutInflater().inflate(R.layout.factura_item_template, containerFactura, false)
            containerFactura.addView(factura)
            val tituloProductoFactura = factura.findViewById<TextView>(R.id.tituloProductoFactura)
            val valorUnitarioLbl = factura.findViewById<TextView>(R.id.valorUnitarioLbl)
            val valorIvaLbl = factura.findViewById<TextView>(R.id.valorIvaLbl)
            val cantidadLbl = factura.findViewById<TextView>(R.id.cantidadLbl)
            val valorSubtotalLbl = factura.findViewById<TextView>(R.id.valorSubtotalLbl)
            val precioTotalLbl = factura.findViewById<TextView>(R.id.precioTotalLbl)

            tituloProductoFactura.text = "${producto.titulo}, Talla: $talla"
            valorUnitarioLbl.text = getFormatedPrice(precioUnitario.roundToInt())
            valorIvaLbl.text = getFormatedPrice(valorIva.roundToInt())
            cantidadLbl.text = "x${cantidad}"
            valorSubtotalLbl.text = getFormatedPrice(subtotal.roundToInt())
            precioTotalLbl.text = getFormatedPrice(precioTotal.roundToInt())

            val facturaIndividual = hashMapOf<String, Serializable>(
                "producto" to "${producto.titulo}, Talla: $talla",
                "precioUnitario" to precioUnitario.roundToInt(),
                "precioIva" to valorIva.roundToInt(),
                "cantidad" to "x${cantidad}",
                "precioSubtotal" to subtotal.roundToInt(),
                "precioTotal" to precioTotal.roundToInt()
            )

            listaFactura.put(producto.id.toString(), facturaIndividual)

            sumaTotal+=precioTotal.roundToInt()

        }
        total = sumaTotal
        precioFinalLbl.text = getFormatedPrice(total)

        confirmarFacturaBtn.setOnClickListener {
            Log.d("compra confirmada", listaFactura.toString())
            if (listaFactura.size != 0){
                saveFactura(listaFactura)
                clearCarrito()
                val navController = findNavController()
                navController?.navigateUp()
                navController?.navigate(R.id.homeFragment)
            }
        }

    }

    private fun saveFactura(facturacion: HashMap<String, HashMap<String, Serializable>>){
        db.collection("factura").document(email).collection("pedidos").document().set(facturacion)
        Log.d("Compra Clear Cart", "Carrito Limpiado")
    }

    private fun clearCarrito(){
        db.collection("carrito").document(email).collection("cart").get().addOnSuccessListener {
            for (i in it){
                i.reference.delete()
            }
        }
    }

    private fun getFormatedPrice(price : Int): String{
        var orden = ""
        val precio = price.toString()
        var pos = precio.length
        if(precio.length in 4..5){
            for (i in precio){
                if (pos == 3){
                    orden += "."
                }
                orden += i
                pos--
            }
        } else if(precio.length in 6..7){
            for (i in precio){
                if (pos == 3 ){
                    orden += "."
                }
                orden += i
                pos--
            }
        }
        return "$ $orden COP"
    }
    private fun findNavController(): NavController? {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController
    }

}