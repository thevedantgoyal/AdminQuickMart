package com.example.adminquickmart.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adminquickmart.databinding.ItemViewImageBinding
import java.util.ArrayList

class selectImageAdapter(val imageUris : ArrayList<Uri>) : RecyclerView.Adapter<selectImageAdapter.selectImageViewholder>() {
    class selectImageViewholder(val binding: ItemViewImageBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): selectImageViewholder {
        return selectImageViewholder(
            ItemViewImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    override fun onBindViewHolder(holder: selectImageViewholder, position: Int) {
        val image = imageUris[position]
        holder.binding.apply {
            rvImage.setImageURI(image)
        }

        holder.binding.rvCancelBtn.setOnClickListener {
           if(position < imageUris.size){
               imageUris.removeAt(position)
               notifyItemRemoved(position)
           }
        }
    }
}