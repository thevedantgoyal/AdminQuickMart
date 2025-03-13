package com.example.adminquickmart.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminquickmart.R
import com.example.adminquickmart.activity.AuthMainActivity
import com.example.adminquickmart.adapter.CategoryAdapter
import com.example.adminquickmart.adapter.ProductAdapter
import com.example.adminquickmart.constants.constants
import com.example.adminquickmart.databinding.EditProductLayoutBinding
import com.example.adminquickmart.databinding.FragmentHomeBinding
import com.example.adminquickmart.models.Category
import com.example.adminquickmart.models.Product
import com.example.adminquickmart.utils.utils
import com.example.adminquickmart.viewModels.adminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    val viewModel : adminViewModel by viewModels()
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapterProduct : ProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)



        setStatusBarColor()
        setCategories()
        searchProduct()
        onLogOutBtnClicked()
        getAlltheProducts("All")
        return binding.root
    }

    private fun onLogOutBtnClicked() {
        binding.tbHome.setOnMenuItemClickListener(){
            when(it.itemId){
                R.id.menuLogout->{
                   logOutUser()
                    true
                }

                else -> {false}
            }

        }
    }

    private fun logOutUser() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        builder.setTitle("Log Out")
            .setIcon(R.drawable.logout)
            .setMessage("Are you Want to Logout")
            .setPositiveButton("Yes"){_,_->
                viewModel.LogOutUser()
                val intent = Intent(requireContext() , AuthMainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

    private fun searchProduct(){
        binding.searchEdit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.getFilter().filter(query)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
    private fun getAlltheProducts(categoryTitle: String) {
        binding.shimmerContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts(categoryTitle).collect{it

                if(it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = ProductAdapter(::onEditBtnClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerContainer.visibility = View.GONE
            }
        }
    }
    private fun onEditBtnClicked(product: Product){
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductTitle.setText(product.producttitle)
            etQuantityTxt.setText(product.productQuantity.toString())
            etNoOfStockTxt.setText(product.productcategory)
            etProductstype.setText(product.producttype)
            etNoOfStockTxt.setText(product.productStock.toString())
            etPriceTxt.setText(product.productPrice.toString())
            etUnitTxt.setText(product.productUnit)
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        editProduct.btnEditProduct.setOnClickListener {
            editProduct.etProductTitle.isEnabled = true
            editProduct.etProductstype.isEnabled = true
            editProduct.etQuantityTxt.isEnabled = true
            editProduct.etNoOfStockTxt.isEnabled = true
            editProduct.etPriceTxt.isEnabled = true
            editProduct.etUnitTxt.isEnabled = true
            editProduct.etProductCategory.isEnabled = true
        }
        setAutoCompleteText(editProduct)

        editProduct.btnSaveProduct.setOnClickListener {

            lifecycleScope.launch {
                product.producttitle = editProduct.etProductTitle.text.toString()
                product.producttype = editProduct.etProductstype.text.toString()
                product.productQuantity = editProduct.etQuantityTxt.text.toString().toInt()
                product.productUnit = editProduct.etUnitTxt.text.toString()
                product.productStock = editProduct.etNoOfStockTxt.text.toString().toInt()
                product.productPrice = editProduct.etPriceTxt.text.toString().toInt()
                product.productcategory = editProduct.etProductCategory.text.toString()
                viewModel.savingUpdateProduct(product)
            }
            utils.showToast(requireContext(), "Saved Changes!")
            alertDialog.dismiss()

        }
    }

    private fun setAutoCompleteText(editProduct: EditProductLayoutBinding) {
        val units = ArrayAdapter(requireContext() , R.layout.showlist , constants.allUnitProducts)
        val category = ArrayAdapter(requireContext() , R.layout.showlist , constants.allProductsCategory)
        val productType = ArrayAdapter(requireContext() , R.layout.showlist , constants.allProductType)

        editProduct.apply {
            etUnitTxt.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductstype.setAdapter(productType)
        }
    }
    private fun setCategories() {
        val categroyList = ArrayList<Category>()
        for(i in 0 until constants.allProductsCategoryIcon.size){
            categroyList.add(Category(constants.allProductsCategory[i] , constants.allProductsCategoryIcon[i]))
        }

        binding.rvCategories.adapter = CategoryAdapter(categroyList , ::onCategoryClicked)
    }
   private fun onCategoryClicked(category: Category){
       getAlltheProducts(category.categoryTitle)
    }
    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}