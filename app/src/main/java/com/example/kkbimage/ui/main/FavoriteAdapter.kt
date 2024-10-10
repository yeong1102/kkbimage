package com.example.kkbimage.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.kkbimage.App
import com.example.kkbimage.R
import com.example.kkbimage.databinding.ItemFavoriteBinding
import com.example.kkbimage.viewmodel.FavoriteViewModel

class FavoriteAdapter(
    private val viewModel: FavoriteViewModel
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private lateinit var binding: ItemFavoriteBinding

    class ViewHolder(
        private val binding: ItemFavoriteBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(vm: FavoriteViewModel, position: Int) {
            binding.apply {

                try{
                    var item = vm.getItem(position)

                    val layoutParams =
                        itemView.getLayoutParams() as StaggeredGridLayoutManager.LayoutParams

                    binding.setVariable(BR.item, item)

                    Glide
                        .with(itemView.context)
                        .load(item?.thumbnail)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.thumbnail)

                    binding.constraintLayout.setOnClickListener{
                        item?.let { it1 -> vm.clickItem(it1) }
                    }

                }catch (t: Throwable){
                    Log.d(App.TAG, "t;;;;;;;;;;; "+t.message)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_favorite,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, position)
    }

    override fun getItemCount(): Int {
        return viewModel.getItemCount()
    }

}