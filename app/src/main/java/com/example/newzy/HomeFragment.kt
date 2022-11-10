package com.example.newzy

import android.app.Fragment
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzy.database.FilterData
import com.example.newzy.databinding.HomeFragmentBinding
import java.util.*

class HomeFragment: Fragment() {

    val viewModel: NewzyViewModel by activityViewModels {
        NewzyViewModelFactory(
            (activity?.application as NewzyApplication).database.filterDataDao()
        )
    }

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.retrieveFilterData.observe(this.viewLifecycleOwner) {
            val data = it
            if(data == null) {
                viewModel.insertFilterData()
            } else {
                run(it)
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//
//    }



    fun run(filterData: FilterData) {

        val card = binding.bottomNavCard
        val cardHeight = card.height * 1.4
        val displayHeight = resources.displayMetrics.heightPixels


        val searchItem = binding.appToolBar.menu.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextFocusChangeListener { _ , hasFocus ->
            if (hasFocus) {
                // searchView expanded
                card.animate().y(displayHeight.toFloat() - cardHeight.toFloat())
                    .setDuration(0)
                    .start()
            } else {
                // searchView not expanded
                card.animate().y(displayHeight.toFloat() + cardHeight.toFloat())
                    .setDuration(0)
                    .start()
            }
        }



        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.v("new", "$query")
                var fromValue: String? = null
                if (filterData.from != null) {
                    fromValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(filterData.from)
                }
                val toValue: String? = null
                if (filterData.to != null) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(filterData.to)
                }
                if (query != null) {
                    viewModel.getSearch(query,filterData.sliderCount,viewModel.page,fromValue,toValue,filterData.sortBy,filterData.language)
                }
                pageNavigator(filterData,viewModel.page,query,fromValue,toValue)
                unCheckAll()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }
        })

        val configItem = binding.appToolBar.menu.findItem(R.id.configure)

        configItem.setOnMenuItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFilterFragment()
            this.findNavController().navigate(action)
            true
        }

        viewModel.country.observe(this.viewLifecycleOwner) {
            viewModel.getTopHeadlines("")
            binding.topHeadline.isChecked = true
        }

        binding.topHeadline.chipCornerRadius = 20F
        binding.topHeadline.setOnCheckedChangeListener { _, b ->
            val chip = binding.topHeadline
            if (b) {
                viewModel.getTopHeadlines( "")
                chip.chipCornerRadius = 20F
            } else {
                chip.chipCornerRadius = 50F
            }
        }

        binding.business.setOnCheckedChangeListener { _, b ->
            val chip = binding.business
            if (b) {
                viewModel.getTopHeadlines("business")
                chip.chipCornerRadius = 20F
            } else {
                chip.chipCornerRadius = 50F
            }
        }

        binding.Technology.setOnCheckedChangeListener { _, b ->
            val chip = binding.Technology
            if (b) {
                viewModel.getTopHeadlines("technology")
                chip.chipCornerRadius = 20F
            } else {
                chip.chipCornerRadius = 50F
            }
        }

        binding.Sports.setOnCheckedChangeListener { _, b ->
            val chip = binding.Sports
            if (b) {
                viewModel.getTopHeadlines("sports")
                chip.chipCornerRadius = 20F
            } else {
                chip.chipCornerRadius = 50F
            }
        }

        binding.Entertainment.setOnCheckedChangeListener { _, b ->
            val chip = binding.Entertainment
            if (b) {
                viewModel.getTopHeadlines("entertainment")
                chip.chipCornerRadius = 20F
            } else {
                chip.chipCornerRadius = 50F
            }
        }

        val recyclerView = binding.recyclerviewNews

        val newsAdapter = NewzyAdapter {
            val action = it.url?.let { it1 ->
                HomeFragmentDirections.actionHomeFragmentToWebpageDisplay(
                    it1
                )
            }
            if (action != null) {
                this.findNavController().navigate(action)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allArticles.observe(this.viewLifecycleOwner) { articles ->
            articles.let {
                newsAdapter.submitList(it)
            }
        }
        recyclerView.adapter = newsAdapter
    }

    fun pageNavigator(filterData: FilterData,page: Int,query: String?,fromValue: String?, toValue: String?) {
        var pageValue = page
        if (query != null) {
            binding.backButton.setOnClickListener {
                if (pageValue > 1) {
                    pageValue--
                    binding.button.text = pageValue.toString()
                    viewModel.getSearch(
                        query,
                        filterData.sliderCount,
                        pageValue,
                        fromValue,
                        toValue,
                        filterData.sortBy,
                        filterData.language
                    )
                }
            }

            binding.forwardButton.setOnClickListener {
                pageValue++
                binding.button.text = pageValue.toString()
                viewModel.getSearch(
                    query,
                    filterData.sliderCount,
                    pageValue,
                    fromValue,
                    toValue,
                    filterData.sortBy,
                    filterData.language
                )
            }

            binding.doubleBack.setOnClickListener {
                if (pageValue > 5) {
                    pageValue -= 5
                    binding.button.text = pageValue.toString()
                    viewModel.getSearch(
                        query,
                        filterData.sliderCount,
                        pageValue,
                        fromValue,
                        toValue,
                        filterData.sortBy,
                        filterData.language
                    )
                }
            }

            binding.doubleForwardButton.setOnClickListener {
                pageValue += 5
                binding.button.text = pageValue.toString()
                viewModel.getSearch(
                    query,
                    filterData.sliderCount,
                    pageValue,
                    fromValue,
                    toValue,
                    filterData.sortBy,
                    filterData.language
                )
            }
        }
    }

    fun unCheckAll() {
        binding.topHeadline.isChecked = false
        binding.business.isChecked = false
        binding.Technology.isChecked = false
        binding.Sports.isChecked = false
        binding.Entertainment.isChecked = false
    }
}