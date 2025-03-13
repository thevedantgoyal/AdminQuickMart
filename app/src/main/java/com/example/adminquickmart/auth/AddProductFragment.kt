package com.example.adminquickmart.auth

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminquickmart.R
import com.example.adminquickmart.activity.AdminMainActivity
import com.example.adminquickmart.adapter.selectImageAdapter
import com.example.adminquickmart.constants.constants
import com.example.adminquickmart.databinding.FragmentAddProductBinding
import com.example.adminquickmart.models.Product
import com.example.adminquickmart.utils.utils
import com.example.adminquickmart.viewModels.adminViewModel
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private val viewModel : adminViewModel by viewModels()
    private lateinit var binding : FragmentAddProductBinding
    private val imageUris : ArrayList<Uri> = arrayListOf()
    val selectImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){listOfUri->
        val fiveImages = listOfUri.take(5)

        imageUris.clear()
        imageUris.addAll(fiveImages)

        binding.rvProductImages.adapter = selectImageAdapter(imageUris)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        setStatusBarColor()

        setAutoCompleteText()

        onImageSelectClick()
        onAddBtnClicked()


        return binding.root
    }

    private fun onAddBtnClicked(){
        binding.btnAddProduct.setOnClickListener {
            utils.showDialog(requireContext() , "Uploading Images...")

            val productTitle = binding.productTitleTxt.text.toString()
            val quantity = binding.quantityTxt.text.toString()
            val unit = binding.unitTxt.text.toString()
            val price = binding.priceTxt.text.toString()
            val outOfStock = binding.noOfStockTxt.text.toString()
            val productType = binding.productstype.text.toString()
            val productCategory = binding.productCategory.text.toString()


            if(productTitle.isEmpty() || productCategory.isEmpty() || quantity.isEmpty() || unit.isEmpty() || price.isEmpty()
                || outOfStock.isEmpty() || productType.isEmpty()){
                utils.hideDialog()
                utils.showToast(requireContext() , "Please fill the Empty Fields First!!")

            }
            else if(imageUris.isEmpty()){
                utils.apply {
                    hideDialog()
                    showToast(requireContext() , "Upload the images!!")
                }
            }
            else{
                val product = Product(
                    producttitle = productTitle ,
                    productQuantity = quantity.toInt(),
                    productUnit = unit,
                    productPrice = price.toInt(),
                    productStock = outOfStock.toInt(),
                    productcategory = productCategory,
                    producttype = productType,
                    itemCount = 0,
                    adminUid = utils.getCurrentUserid(),
                    productRandomId = utils.randomId()
                )

                saveImage(product)

            }
        }
    }

    private fun saveImage(product: Product) {
        viewModel.saveImageinDB(imageUris)
        lifecycleScope.launch {
            viewModel.imageUploaded.collect{it->
                if (it){
                    utils.apply {
                        hideDialog()
                        showToast(requireContext() , "Image Saved!!")
                    }
                    getUrls(product)
                }
            }
        }
    }

    private fun getUrls(product: Product) {
        utils.showDialog(requireContext(), "Publishing Product...")
        lifecycleScope.launch {
            viewModel.dowloadUrl.collect{it->
                val urls = it
                product.productImageuris = urls
                saveProduct(product)
            }
        }
    }

    private fun saveProduct(product: Product){
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.productsaved.collect{it->
                if (it){
                    utils.hideDialog()
                    val intent = Intent(requireActivity() , AdminMainActivity::class.java)
                    startActivity(intent)
                    utils.showToast(requireContext() , "Product is Saved!")
                }
            }
        }

    }

    private fun onImageSelectClick() {
        binding.btnSelectImage.setOnClickListener {
            selectImage.launch("image/*")
        }
    }

    private  fun setAutoCompleteText(){
        val units = ArrayAdapter(requireContext() , R.layout.showlist , constants.allUnitProducts)
        val category = ArrayAdapter(requireContext() , R.layout.showlist , constants.allProductsCategory)
        val productType = ArrayAdapter(requireContext() , R.layout.showlist , constants.allProductType)

        binding.apply {
            unitTxt.setAdapter(units)
            productCategory.setAdapter(category)
            productstype.setAdapter(productType)
        }
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