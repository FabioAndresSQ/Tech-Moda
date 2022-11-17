package com.techmoda.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.techmoda.R
import com.techmoda.model.Producto
import java.io.Serializable

class CarritoAdapter(
    private val context: Context, val productosCarrito:MutableList<HashMap<String, Serializable>>,
    private val listener : OnItemClickListener):
    RecyclerView.Adapter<CarritoAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.carrito_item, parent, false)
        val imageView: ImageView = itemView.findViewById(R.id.itemImageCarrito)
        Glide.with(context).load(R.drawable.loading_icon).into(imageView)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        Log.d("Carrito View", productosCarrito.toString())
        return productosCarrito.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto : Producto = productosCarrito[position]["producto"] as Producto
        val cantidad : Long = productosCarrito[position]["cantidad"] as Long
        val imagesList = producto.imagenes.toString().split(",")
        val imagen = imagesList[0]
        Firebase.storage("gs://tech-moda-13655.appspot.com/")
            .getReferenceFromUrl(imagen)
            .downloadUrl.addOnSuccessListener {Uri->
                val imageUrl = Uri.toString()
                Glide.with(context).load(imageUrl).into(holder.image)
            }

        holder.title.text = producto.titulo
        val formattedPrice = getFormatedPrice(producto.precio.toString(), cantidad)
        holder.price.text = formattedPrice
        holder.cantidad.text = cantidad.toString()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        val image : ImageView = view.findViewById(R.id.itemImageCarrito)
        val title : TextView = view.findViewById(R.id.itemTitleCarrito)
        val price : TextView = view.findViewById(R.id.itemPrecioCarrito)
        val cantidad : TextView = view.findViewById(R.id.itemCantidadCarrito)

        init {
            itemView.findViewById<Button>(R.id.itemLessCarritoBtn).setOnClickListener(this)
            itemView.findViewById<Button>(R.id.itemPlusCarritoBtn).setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = productosCarrito[adapterPosition]
            if (v?.id == R.id.itemLessCarritoBtn){
                listener.onItemClick(item, v.id, adapterPosition)
            } else if (v?.id == R.id.itemPlusCarritoBtn){
                listener.onItemClick(item , v.id, adapterPosition)
            }
        }
    }

    private fun getFormatedPrice(price : String, cantidad: Long): String{
        var orden = ""
        val precio = (price.toInt() * cantidad).toString()
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

    interface OnItemClickListener{
        fun onItemClick(item: HashMap<String, Serializable>, id: Int, adapterPosition: Int)
    }

}