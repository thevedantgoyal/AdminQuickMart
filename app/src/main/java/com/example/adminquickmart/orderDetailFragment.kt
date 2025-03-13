package com.example.adminquickmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminquickmart.adapter.AdapterCartProducts
import com.example.adminquickmart.databinding.FragmentOrderDetailBinding
import com.example.adminquickmart.utils.utils
import com.example.adminquickmart.viewModels.adminViewModel
import kotlinx.coroutines.launch


class orderDetailFragment : Fragment() {
    private lateinit var binding : FragmentOrderDetailBinding
    private lateinit var adapter : AdapterCartProducts
    private  val viewModel : adminViewModel by viewModels()
    private  var status = 0
    private  var currentStatus = 0
    private var orderId = ""
    private var address = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        getValues()
        settingStatus(status)
        getOrderedProduct()
        onBackBtnClicked()
        onChangeBtnClicked()
        return binding.root
    }

    private fun onChangeBtnClicked() {
        binding.changeStatusBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menuReceived -> {
                        currentStatus = 1
                        if(currentStatus > status){
                            status = 1
                            settingStatus(1)
                            viewModel.updateOrderStatus(orderId , 1)
                        }
                        else{
                            utils.showToast(requireContext() , "Order is Already Received...")
                        }
                        true
                    }
                    R.id.menuDispatched->{
                        currentStatus = 2
                        if(currentStatus > status){
                            status = 2
                            settingStatus(2)
                            viewModel.updateOrderStatus(orderId , 2)
                        }
                        else{
                            utils.showToast(requireContext() , "Order is Already Dispatched...")
                        }
                        true
                    }
                    R.id.menuDelivered->{
                        currentStatus = 3
                        if(currentStatus > status){
                            status = 3
                            settingStatus(3)
                            viewModel.updateOrderStatus(orderId , 3)
                        }
                        true
                    }
                    else -> {false}
                }
            }
        }
    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderedId").toString()
        address = bundle.getString("userAddress").toString()
        binding.tvUserAddress.text = address
    }
    private fun getOrderedProduct() {
        lifecycleScope.launch {
            viewModel.getOrderedProducts(orderId).collect{cartList->
                adapter = AdapterCartProducts()
                binding.rvOrderDetail.adapter = adapter
                adapter.differ.submitList(cartList)
            }
        }
    }

    private fun settingStatus(status : Int) {
        when(status){
            0->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            1->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            2->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            3->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv4.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
        }
    }

    private fun onBackBtnClicked() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }

}