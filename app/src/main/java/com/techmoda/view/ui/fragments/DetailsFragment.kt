package com.techmoda.view.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.iterator
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.techmoda.R
import com.techmoda.model.Producto

class DetailsFragment : Fragment() {

    private lateinit var detailedTitle: TextView
    private lateinit var detailedDescription: TextView
    private lateinit var detailedPrice: TextView
    private lateinit var detailedTags: TextView
    private lateinit var containerImages: LinearLayout
    private lateinit var producto: Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val args =this.arguments
        producto = args?.getSerializable("selected_product") as Producto

        detailedTitle = view.findViewById(R.id.detailedTitle)
        detailedDescription = view.findViewById(R.id.detailedDescription)
        detailedPrice = view.findViewById(R.id.detailedPrice)
        detailedTags = view.findViewById(R.id.detailedTags)
        containerImages = view.findViewById(R.id.containerImages)

        setup()

        return view
    }

    private fun setup(){
        detailedTitle.text = producto.titulo.toString()
        detailedDescription.text = producto.descripcion.toString()
        detailedPrice.text = getFormatedPrice(producto.precio.toString())
        val imagesUrlList = producto.imagenes.toString().filter {!it.isWhitespace()}.split(",")
        val imageContainers : ArrayList<View> = ArrayList()
        // Inflate the imageview, giving the linearlayout as the parent)
        for (i in imagesUrlList){
            val imageView = getLayoutInflater().inflate(R.layout.image_template, containerImages, false)
            imageContainers.add(imageView)
            Log.e("IMAGE", "IMAGE URL -> $i")
        }
        containerImages.removeAllViews()
        for (i in imageContainers){
            containerImages.addView(i)
        }

        val imagesCount = containerImages.childCount
        for (i in 0..imagesCount){
            val imageContainer = containerImages.getChildAt(i)
            if (imageContainer is ImageView){
            Firebase.storage("gs://tech-moda-13655.appspot.com/")
                .getReferenceFromUrl(imagesUrlList[i])
                .downloadUrl.addOnSuccessListener {Uri->
                    val imageUrl = Uri.toString()
                        Glide.with(context).load(imageUrl).into(imageContainer)
                    }
                }
        }
        detailedTags.text = producto.tags
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
        return "$ $orden COP"
    }

    private fun setImage(imageUrl:String, index: Int){

    }

}