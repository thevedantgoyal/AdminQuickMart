package com.example.adminquickmart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminquickmart.databinding.ItemProductDetailsBinding
import com.example.adminquickmart.filterable.FilterableProducts
import com.example.adminquickmart.models.Product

class ProductAdapter(val onEditBtnClicked: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder>() , Filterable {
    class ProductAdapterViewHolder(val binding: ItemProductDetailsBinding) : ViewHolder(binding.root)

    val diffUtil = object :DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterViewHolder {
        return ProductAdapterViewHolder(ItemProductDetailsBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductAdapterViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()

            val productImage = product.productImageuris

            for (i in 0 until productImage?.size!!){
                imageList.add(SlideModel(product.productImageuris!![i].toString()))
            }
            imageSlider.setImageList(imageList)

            tvProductTitle.text = product.producttitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text = quantity
            tvProductPrice.text = "₹" + product.productPrice

        }
        holder.itemView.setOnClickListener{
            onEditBtnClicked(product)
        }


    }

    val filter : FilterableProducts? = null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if(filter == null) return FilterableProducts(this , originalList)
        return filter
    }

}