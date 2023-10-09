package com.techmoda.view.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.iterator
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.techmoda.ProviderType
import com.techmoda.R
import com.techmoda.model.Producto
import java.io.Serializable

class DetailsFragment : Fragment() {

    private lateinit var detailedTitle: TextView
    private lateinit var detailedDescription: TextView
    private lateinit var detailedPrice: TextView
    private lateinit var detailedTags: TextView
    private lateinit var containerImages: LinearLayout
    private lateinit var producto: Producto
    private lateinit var addToCartBtn: Button
    private lateinit var itemLessBtn : Button
    private lateinit var itemPlusBtn : Button
    private lateinit var itemCantidadLbl : TextView
    private lateinit var tallasLayout : LinearLayout
    private lateinit var tallaXsBtn : Button
    private lateinit var tallaSBtn : Button
    private lateinit var tallaMBtn : Button
    private lateinit var tallaLBtn : Button
    private lateinit var tallaXlBtn : Button
    private var tallaSelected = ""
    private var cantidad = 1
    private val db = FirebaseFirestore.getInstance()

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
        addToCartBtn = view.findViewById(R.id.addToCartBtn)
        itemLessBtn = view.findViewById(R.id.itemLessBtn)
        itemPlusBtn = view.findViewById(R.id.itemPlusBtn)
        itemCantidadLbl = view.findViewById(R.id.itemCantidadLbl)
        containerImages = view.findViewById(R.id.containerImages) //LinearLayout containing Images
        tallasLayout = view.findViewById(R.id.tallasLayout) //LinearLayout containing tallas
        tallaXsBtn = view.findViewById(R.id.tallaXsBtn)
        tallaSBtn = view.findViewById(R.id.tallaSBtn)
        tallaMBtn = view.findViewById(R.id.tallaMBtn)
        tallaLBtn = view.findViewById(R.id.tallaLBtn)
        tallaXlBtn = view.findViewById(R.id.tallaXlBtn)

        tallaXsBtn.isEnabled = false
        tallaSBtn.isEnabled = false
        tallaMBtn.isEnabled = false
        tallaLBtn.isEnabled = false
        tallaXlBtn.isEnabled = false

        setup()

        return view
    }

    private fun setup(){
        itemLessBtn.visibility = View.INVISIBLE
        detailedTitle.text = producto.titulo.toString()
        detailedDescription.text = producto.descripcion.toString()
        detailedPrice.text = getFormatedPrice(producto.precio.toString())
        val imagesUrlList = producto.imagenes.toString().filter {!it.isWhitespace()}.split(",")
        val imageContainers : ArrayList<View> = ArrayList()
        //Get the number of images Links on Product and add a imageview for each link
        for (i in imagesUrlList){
            // Inflate the imageview, giving the linearlayout as the parent
            val imageView = getLayoutInflater().inflate(R.layout.image_template, containerImages, false)
            imageContainers.add(imageView)
        }
        //Delete the default ImageView on linearLayout
        containerImages.removeAllViews()
        //Add the imageviews to the linearLayout if Images
        for (i in imageContainers){
            containerImages.addView(i)
        }
        //Get the number of ImageViews in the LinearLayout and display the corresponding image with Glide
        val imagesCount = containerImages.childCount
        for (i in 0..imagesCount){
            val imageContainer = containerImages.getChildAt(i)
            if (imageContainer is ImageView){
            Firebase.storage("gs://tech-moda-13655.appspot.com/")
                .getReferenceFromUrl(imagesUrlList[i])
                .downloadUrl.addOnSuccessListener {Uri->
                    val imageUrl = Uri.toString()
                        Glide.with(requireContext()).load(imageUrl).into(imageContainer)
                    }
                }
        }
        detailedTags.text = producto.tags

        itemPlusBtn.setOnClickListener {
                itemLessBtn.visibility = View.VISIBLE
            if (cantidad < producto.disponible!!){
                cantidad++
                itemCantidadLbl.text = cantidad.toString()
            } else{
                itemPlusBtn.visibility = View.INVISIBLE
            }
        }

        itemLessBtn.setOnClickListener {
            if (cantidad > 2) {
                itemPlusBtn.visibility = View.VISIBLE
                cantidad--
            } else if (cantidad == 2){
                cantidad--
                itemLessBtn.visibility = View.INVISIBLE
                itemPlusBtn.visibility = View.VISIBLE
            }
            itemCantidadLbl.text = cantidad.toString()
        }

        addToCartBtn.setOnClickListener {

            if (tallaSelected != ""){
                val prefs = context?.getSharedPreferences(getString(R.string.cart_file), Context.MODE_PRIVATE)?.edit()
                prefs?.putString("${producto.id}", "${producto.id},$cantidad")
                Log.d("added", "${producto.id},$cantidad")
                prefs?.apply()
                val acc = context?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                val email = acc?.getString("email", null)!!
                Log.d("added", "${producto.id},$cantidad")
                prefs?.apply()

                //Save product to Carrito Db
                db.collection("carrito").document(email).collection("cart")
                    .document(producto.id.toString()).set(
                        hashMapOf("cantidad" to cantidad,
                            "talla" to tallaSelected,
                            "producto" to db.document("productos/${producto.id.toString()}"))
                    )
                Toast.makeText(context,"Añadido al Carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"Seleccione una Talla", Toast.LENGTH_SHORT).show()
            }


        }

        val tallas = producto.tallas?.split(",")

        if (tallas != null){
            for (i in tallas){
                if (i == "XS"){
                    tallaXsBtn.isEnabled = true
                }else if (i == "S"){
                    tallaSBtn.isEnabled = true
                }else if (i == "M"){
                    tallaMBtn.isEnabled = true
                }else if (i == "L"){
                    tallaLBtn.isEnabled = true
                }else if (i == "XL"){
                    tallaXlBtn.isEnabled = true
                }
            }
        }

        for (i in tallasLayout){
            i.setOnClickListener {
                setTalla(i)
            }
        }

    }

    private fun setTalla(i: View){
        for (it in tallasLayout){
            val btn = it as Button
            btn.setTextColor(resources.getColor(R.color.black))
            btn.setBackgroundResource(R.drawable.customborder)
        }
        val button = i as Button
        when (button.id){
            R.id.tallaXsBtn -> {
                i.setTextColor(resources.getColor(R.color.white))
                i.setBackgroundResource(R.drawable.customborderselected)
                tallaSelected = "Xs"
            }
            R.id.tallaSBtn -> {
                i.setTextColor(resources.getColor(R.color.white))
                i.setBackgroundResource(R.drawable.customborderselected)
                tallaSelected = "S"
            }
            R.id.tallaMBtn -> {
                i.setTextColor(resources.getColor(R.color.white))
                i.setBackgroundResource(R.drawable.customborderselected)
                tallaSelected = "M"
            }
            R.id.tallaLBtn -> {
                i.setTextColor(resources.getColor(R.color.white))
                i.setBackgroundResource(R.drawable.customborderselected)
                tallaSelected = "L"
            }
            R.id.tallaXlBtn -> {
                i.setTextColor(resources.getColor(R.color.white))
                i.setBackgroundResource(R.drawable.customborderselected)
                tallaSelected = "Xl"
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
        return "$ $orden COP"
    }

}