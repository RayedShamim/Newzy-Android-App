package com.example.newzy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newzy.database.FilterData
import com.example.newzy.databinding.HomeFragmentBinding
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment: Fragment(){

    val viewModel: NewzyViewModel by activityViewModels {
        NewzyViewModelFactory(
            (activity?.application as NewzyApplication).database.filterDataDao()
        )
    }

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var filterData: FilterData

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

        binding.appToolBar.apply {
            setLogo(R.drawable.newzy_logo_full_2_01)
        }

        recyclerView = binding.recyclerviewNews
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

        val card = binding.bottomNavCard
        val cardHeight = card.height * 1.4
        val displayHeight = resources.displayMetrics.heightPixels
        card.animate().y(displayHeight.toFloat() + cardHeight.toFloat())
                    .setDuration(0)
                    .start()
        viewModel.page.observe(this.viewLifecycleOwner) {
            binding.button.text = it.toString()
        }

        viewModel.allArticles.observe(this.viewLifecycleOwner) { articles ->
            articles.let {
                newsAdapter.submitList(it)
            }
        }
        recyclerView.adapter = newsAdapter

        viewModel.retrieveFilterData.observe(this.viewLifecycleOwner) {
            val data = it
            if(data == null) {
                viewModel.insertFilterData()
            } else {
                filterData = it
                run()
            }
        }
    }

    private fun run() {

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
                binding.topHeadline.isChecked = true
                viewModel.updatePage(1)
                card.animate().y(displayHeight.toFloat() + cardHeight.toFloat())
                    .setDuration(0)
                    .start()
            }
        }

        searchView.queryHint = "Search News"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.v("new", "$query")
                var fromValue: String? = null
                if (filterData.from != null) {
                    fromValue = SimpleDateFormat("yyyy-MM-dd",
                        Locale.getDefault()).format(filterData.from)
                }
                val toValue: String? = null
                if (filterData.to != null) {
                    SimpleDateFormat("yyyy-MM-dd",
                        Locale.getDefault()).format(filterData.to)
                }
                if (query != null) {
                    viewModel.getSearch(query,
                        filterData.sliderCount,
                        viewModel.page.value!!,
                        fromValue,
                        toValue,
                        filterData.sortBy,
                        filterData.language)
                }
                pageNavigator(filterData,viewModel.page.value!!,query,fromValue,toValue)
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

        binding.topHeadline.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
        binding.topHeadline.setOnCheckedChangeListener { _, b ->
            val chip = binding.topHeadline
            if (b) {
                viewModel.getTopHeadlines( "")
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else if (!b) {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }

        binding.business.setOnCheckedChangeListener { _, b ->
            val chip = binding.business
            if (b) {
                viewModel.getTopHeadlines("business")
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }

        binding.Technology.setOnCheckedChangeListener { _, b ->
            val chip = binding.Technology
            if (b) {
                viewModel.getTopHeadlines("technology")
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }

        binding.Sports.setOnCheckedChangeListener { _, b ->
            val chip = binding.Sports
            if (b) {
                viewModel.getTopHeadlines("sports")
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }

        binding.Entertainment.setOnCheckedChangeListener { _, b ->
            val chip = binding.Entertainment
            if (b) {
                viewModel.getTopHeadlines("entertainment")
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }
    }

    suspend fun scrollToTop() {
        delay(1000)
        recyclerView.smoothScrollToPosition(0)
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

//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.top_app_bar,menu)
//        Log.v("checking", "..................")
//    }
//
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        val card = binding.bottomNavCard
//        val cardHeight = card.height * 1.4
//        val displayHeight = resources.displayMetrics.heightPixels
//
////        val searchviewItem =
////        val searchView = searchviewItem.actionView as SearchView
//
//        return when (menuItem.itemId) {
//
//            R.id.search_bar -> {
//                val searchView = menuItem.actionView as SearchView
//                searchView.setOnQueryTextFocusChangeListener { _ , hasFocus ->
//                    if (hasFocus) {
//                        // searchView expanded
//                        card.animate().y(displayHeight.toFloat() - cardHeight.toFloat())
//                            .setDuration(0)
//                            .start()
//                    } else {
//                        // searchView not expanded
//                        card.animate().y(displayHeight.toFloat() + cardHeight.toFloat())
//                            .setDuration(0)
//                            .start()
//                    }
//                }
//
//                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String?): Boolean {
//                        Log.v("new", "$query")
//                        var fromValue: String? = null
//                        if (filterData.from != null) {
//                            fromValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(filterData.from)
//                        }
//                        val toValue: String? = null
//                        if (filterData.to != null) {
//                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(filterData.to)
//                        }
//                        if (query != null) {
//                            viewModel.getSearch(query,filterData.sliderCount,viewModel.page,fromValue,toValue,filterData.sortBy,filterData.language)
//                        }
//                        pageNavigator(filterData,viewModel.page,query,fromValue,toValue)
//                        unCheckAll()
//                        return false
//                    }
//
//                    override fun onQueryTextChange(query: String?): Boolean {
//                        return false
//                    }
//                })
//
//                true
//            }
//
//            R.id.configure -> {
//                val action = HomeFragmentDirections.actionHomeFragmentToFilterFragment()
//                this.findNavController().navigate(action)
//                true
//            }
//
//            else -> false
//        }
//    }
}