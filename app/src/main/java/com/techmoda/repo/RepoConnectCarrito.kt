package com.techmoda.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.techmoda.model.Producto
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable

class RepoConnectCarrito {
    fun getProductosCarrito(email: String): LiveData<MutableList<HashMap<String, Serializable>>> {
        val productosdata = MutableLiveData<MutableList<HashMap<String, Serializable>>>()
        val db = FirebaseFirestore.getInstance()
        val lista : MutableList<HashMap<String, Serializable>> = mutableListOf()
        db.collection("carrito").document(email).collection("cart")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
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
                                lista.add(hashMapOf("producto" to producto!!, "cantidad" to cantidad, "talla" to talla))

                                productosdata.value = lista
                                Log.d("Carrito Loaded", productosdata.toString())
                            }
                        }
                    }
                }

            })
        return productosdata
    }
}