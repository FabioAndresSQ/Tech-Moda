package com.techmoda.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.techmoda.R
import com.techmoda.model.Producto
import java.io.File

class ProductoAdapter (
    private val context: Context, val itemsProductos :ArrayList<Producto>,
    private val listener : OnItemClickListener):
    RecyclerView.Adapter<ProductoAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.producto_item, parent, false)
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        Glide.with(context).load(R.drawable.loading_icon).into(imageView)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemsProductos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto : Producto = itemsProductos[position]
        Firebase.storage("gs://tech-moda-13655.appspot.com/")
            .getReferenceFromUrl(producto.imagenes.toString())
            .downloadUrl.addOnSuccessListener {Uri->
                val imageUrl = Uri.toString()
                Glide.with(context).load(imageUrl).into(holder.image)
            }

        holder.title.text = producto.titulo
        val formattedPrice = getFormatedPrice(producto.precio.toString())
        holder.price.text = "$ $formattedPrice COP"
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        val image : ImageView = view.findViewById(R.id.itemImage)
        val title : TextView = view.findViewById(R.id.itemTitle)
        val price : TextView = view.findViewById(R.id.itemPrecio)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = itemsProductos[adapterPosition]
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(item, adapterPosition)
            }
        }
    }

    private fun getFormatedPrice(price : String): String{
        var orden = ""
        var pos = price.length
        if(price.length > 3){
            for (i in price){
                if (pos == 3 || pos == 6){
                    orden += "."
                }
                orden += i
                pos--
            }
        }
        return orden
    }

    interface OnItemClickListener{
        fun onItemClick(item: Producto, adapterPosition: Int)
    }

}