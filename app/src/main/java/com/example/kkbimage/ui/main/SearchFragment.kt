package com.example.kkbimage.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kkbimage.App
import com.example.kkbimage.R
import com.example.kkbimage.databinding.FragmentSearchBinding
import com.example.kkbimage.ui.main.model.SharedPref
import com.example.kkbimage.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A placeholder fragment containing a simple view.
 */

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mAdapter: SearchAdapter by lazy {
        SearchAdapter(viewModel, resources.getDimension(R.dimen.separator_height).toInt(), resources.getDimension(R.dimen.fab_margin))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(App.TAG, "SearchFragment onCreate")
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root

        _binding?.recyclerView?.apply {
            layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            adapter = mAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        Log.d(App.TAG, "Last!")
                        viewModel.searchWithScroll(mAdapter)
                    }
                }
            })
        }

        _binding?.let{
            it.btSearch.setOnClickListener(View.OnClickListener {
                hideKeyboard()
                Log.d(App.TAG, "onClick btSearch")
                Log.d(App.TAG, "_binding: "+_binding)
                _binding?.let {
                    searchAction(_binding!!.etSearch)
                }
            })
            it.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    searchAction(it.etSearch)
                    return@OnEditorActionListener true
                }
                false
            })
        } ?: kotlin.run {
            Log.d(App.TAG, "_binding.run: "+_binding)
        }

        viewModel.observeResponse(viewLifecycleOwner, mAdapter)
        //viewModel.search("Chat GPT")

        return root
    }

    fun search(word: String){
        viewModel.resetSearchResult(mAdapter)
        viewModel.search(word)
    }

    private fun hideKeyboard() {
        try {
            val view = requireActivity().currentFocus
            if (view != null) {
                val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }catch (t: Throwable){}
    }


    private fun searchAction(etSearch: EditText) {
        try{
            Log.d(App.TAG, "searchAction: ")
            Log.d(App.TAG, "dataSetReady: "+SharedPref.dataSetReady)
            if(SharedPref.dataSetReady){
                search(etSearch.text.toString())
            }else{
                Toast.makeText(context, "dataSet is not Ready. Please wait...", Toast.LENGTH_SHORT).show()
            }
        }catch (t: Throwable){}
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
        fun newInstance(sectionNumber: Int): SearchFragment {
            return SearchFragment().apply {
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