package com.example.adminquickmart.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminquickmart.R
import com.example.adminquickmart.adapter.AdapterOrders
import com.example.adminquickmart.databinding.FragmentOrderBinding
import com.example.adminquickmart.models.OrderItems
import com.example.adminquickmart.utils.utils
import com.example.adminquickmart.viewModels.adminViewModel
import kotlinx.coroutines.launch


class OrderFragment : Fragment() {
    private lateinit var binding : FragmentOrderBinding
    private val viewModel : adminViewModel by viewModels()
    private lateinit var adapter : AdapterOrders
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)


        getAllOrders()

        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.tvText.visibility = View.GONE
        lifecycleScope.launch {
            viewModel.getAllProductsForStautus().collect{orderList->
                if(orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderItems>()
                    for(orders in orderList){

                        val title = StringBuilder()

                        var totalPrice = 0

                        for(products in orders.orderList!! ){
                            val price = products.productPrice?.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productcategory}, ")
                        }

                        val orderedItems = OrderItems(
                            orderId = orders.orderId,
                            itemDate = orders.orderDate,
                            itemStatus = orders.orderStatus,
                            itemTitle = title.toString(),
                            itemPrice = totalPrice,
                            userAddress = orders.userAddress
                        )
                        orderedList.add(orderedItems)

                    }
                    adapter = AdapterOrders(requireContext() , ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapter
                    adapter.differ.submitList(orderedList)
                    binding.shimmerContainer.visibility = View.GONE
                }
                else{
                    binding.shimmerContainer.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
            }
        }

    }

    fun onOrderItemViewClicked(orderedItems: OrderItems){
        val bundle = Bundle()
        bundle.putInt("status" , orderedItems.itemStatus!!)
        bundle.putString("orderedId" , orderedItems.orderId)
        bundle.putString("userAddress" , orderedItems.userAddress)

        findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment , bundle)
    }



}