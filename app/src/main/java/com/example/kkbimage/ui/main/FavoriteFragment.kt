package com.example.kkbimage.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kkbimage.App
import com.example.kkbimage.databinding.FragmentFavoriteBinding
import com.example.kkbimage.ui.main.model.SharedPref
import com.example.kkbimage.viewmodel.FavoriteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A placeholder fragment containing a simple view.
 */
class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mAdapter: FavoriteAdapter by lazy {
        FavoriteAdapter(viewModel)
    }

    val mainJob = Job()
    val mainScope = CoroutineScope(Dispatchers.Main + mainJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(App.TAG, "MyItemFragment onCreate")
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root = binding.root

        _binding?.recyclerView?.apply {
            layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            adapter = mAdapter
        }

        context?.let {
            mainScope.launch(Dispatchers.IO){
                SharedPref.loadData(it, viewModel.items)
            }
        }

        viewModel.observeItems(viewLifecycleOwner, {
            mAdapter.notifyDataSetChanged()
        })

        return root
    }

    override fun onDestroy(){
        mainJob.cancel()
        super.onDestroy()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): FavoriteFragment {
            return FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}