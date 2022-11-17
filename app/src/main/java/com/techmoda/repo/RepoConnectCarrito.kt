package com.techmoda.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
        db.collection("carrito").document(email).collection("cart")
            .get().addOnSuccessListener {
                val lista : MutableList<HashMap<String, Serializable>> = mutableListOf()
                if (it != null){
                    for (i in it){
                        val product : DocumentReference = i.get("producto") as DocumentReference
                        product.get().addOnSuccessListener {
                            val productoFirestore = it.toObject(Producto::class.java)
                            val cantidad = i.get("cantidad") as Long
                            lista.add(hashMapOf("producto" to productoFirestore!!, "cantidad" to cantidad))
                        }
                    }
                }
                runBlocking {
                    launch {  }
                    delay(500L)
                }
                productosdata.value = lista
                Log.d("Carrito Loaded", productosdata.toString())
            }
        return productosdata
    }
}