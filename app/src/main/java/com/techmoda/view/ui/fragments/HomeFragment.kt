package com.techmoda.view.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techmoda.R
import com.techmoda.model.Producto
import com.techmoda.view.adapter.ProductoAdapter
import com.techmoda.view.ui.activities.HomeActivity
import com.techmoda.viewmodel.ProductoViewModel
import java.io.Serializable


class HomeFragment : Fragment(), ProductoAdapter.OnItemClickListener{
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var productoList: ArrayList<Producto>
    private lateinit var itemAdapter: ProductoAdapter
    private val viewModel by lazy { ViewModelProvider(this).get(ProductoViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false) //save inflater to view

        myRecyclerView = view.findViewById(R.id.homeRecyclerView)
        myRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
        }

        productoList = arrayListOf()


        getLiveData()
        return view
    }

    private fun getLiveData(){
        viewModel.productosData().observe(viewLifecycleOwner, Observer {
            itemAdapter = ProductoAdapter(this.requireContext(), it, this)
            myRecyclerView.adapter = itemAdapter
            itemAdapter.notifyDataSetChanged()
        })
    }

    private fun findNavController(): NavController? {
        val navHostFragment = (requireActivity() as? HomeActivity)?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController
    }

    override fun onItemClick(item: Producto, adapterPosition : Int) { //Check on item clicked passing item and item position
        val launchDetails = Bundle()
        val nav = findNavController()
        Log.i("Launching Product", item.titulo.toString())
        launchDetails.putSerializable("selected_product" , item as Serializable) //Save item on Intend
        nav?.navigate(R.id.detailsFragment, launchDetails)
    }

    //Error Alert
    private fun showErrorAlert(msg:String){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }


}