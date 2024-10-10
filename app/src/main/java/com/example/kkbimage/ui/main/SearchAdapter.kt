package com.example.kkbimage.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.kkbimage.App
import com.example.kkbimage.R
import com.example.kkbimage.databinding.ItemSearchBinding
import com.example.kkbimage.viewmodel.SearchViewModel


class SearchAdapter(
    private val viewModel: SearchViewModel, val separator_height: Int, val textSize: Float
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var binding: ItemSearchBinding

    class ViewHolder(
        private val binding: ItemSearchBinding,
        val separator_height: Int, val textSize: Float): RecyclerView.ViewHolder(binding.root){

        fun bind(vm: SearchViewModel, position: Int) {
            binding.apply {

                try{
                    var item = vm.getItem(position)

                    if (item.separator) {
                        val layoutParams =
                            itemView.getLayoutParams() as StaggeredGridLayoutManager.LayoutParams
                        layoutParams.isFullSpan = true

                        binding.constraintLayout.layoutParams.height = separator_height
                        binding.thumbnail.visibility = View.GONE
                        binding.text.textSize = textSize

                        binding.setVariable(BR.item, item)
                    } else {
                        val layoutParams =
                            itemView.getLayoutParams() as StaggeredGridLayoutManager.LayoutParams
                        layoutParams.isFullSpan = false

                        binding.setVariable(BR.item, item)
                        binding.executePendingBindings()

                        Glide
                            .with(itemView.context)
                            .load(item.thumbnail)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(binding.thumbnail)

                        binding.constraintLayout.setOnClickListener{
                            try{
                                item.setHeartVisibility(!item.getHeartVisibility())
                            }catch (t: Throwable){}
                        }
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
            R.layout.item_search,
            parent,
            false
        )

        return ViewHolder(binding, separator_height, textSize)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, position)
    }

    override fun getItemCount(): Int {
        return viewModel.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewModel.getItem(position).separator) 1 else 0
    }

}