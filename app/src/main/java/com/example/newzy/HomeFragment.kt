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
import com.example.newzy.database.CountryNewzy
import com.example.newzy.database.FilterData
import com.example.newzy.databinding.HomeFragmentBinding
import com.example.newzy.model.CountriesList
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
    private lateinit var country: CountryNewzy

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

        //Observing the class property country. This will be used to display top headlines based on this variable.
        viewModel.country.observe(this.viewLifecycleOwner) {
            if (it == null) {
                val newCountry = CountriesList.country[50]
                val entryCountry = CountryNewzy(null,newCountry.name,newCountry.code)
                viewModel.insertCountry(entryCountry)
            } else {
                country = it
                val searchItem = binding.appToolBar.menu.findItem(R.id.search_bar)
                val searchView = searchItem?.actionView as SearchView
                val card = binding.bottomNavCard

                if (card.height.toFloat() != 0f) {
                    viewModel.cardheight = card.height.toFloat()
                }
                val cardUp = resources.displayMetrics.heightPixels.toFloat() - viewModel.cardheight
                val cardDown = resources.displayMetrics.heightPixels.toFloat() + viewModel.cardheight

                if (cardUp != resources.displayMetrics.heightPixels.toFloat()) {
                    viewModel.cardUpValue = cardUp
                    viewModel.cardDownValue = cardDown
                }

                if (!searchView.isIconified) {
                    card.animate().y(viewModel.cardUpValue)
                        .setDuration(0)
                        .start()
                } else {
                    viewModel.getTopHeadlines("",it)
                    card.animate().y(viewModel.cardDownValue)
                        .setDuration(0)
                        .start()
                }
            }
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

        viewModel.page.observe(this.viewLifecycleOwner) {
            binding.button.text = it.toString()
        }
        //Updates the recyclerView with new articles.
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
        val searchItem = binding.appToolBar.menu.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView
        val card = binding.bottomNavCard

        viewModel.searchQuery.observe(this.viewLifecycleOwner) { query ->
            val from1 = filterData.from?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(it)
            }
            val to1 = filterData.to?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(it)
            }
            query?.let {
                viewModel.getSearch(it,
                    filterData.sliderCount,
                    viewModel.page.value!!,
                    from1,
                    to1,
                    filterData.sortBy,
                    filterData.language)
            }
            pageNavigator(
                filterData,
                viewModel.page.value!!,
                query,
                from1,
                to1
            )
        }

        val displayHeight = resources.displayMetrics.heightPixels
        Log.v("it","$displayHeight")//.....................................................
        Log.v("it1","${card.y}")

        searchView.setOnQueryTextFocusChangeListener { _ , hasFocus ->
            if (hasFocus) {
                // searchView expanded
                if (card.y != viewModel.cardUpValue){
                    card.animate().y(
                        viewModel.cardUpValue
                    )
                        .setDuration(0)
                        .start()
                    Log.v("it2","${card.y}")
                }
            } else {
                // searchView not expanded
                viewModel.getTopHeadlines("",country)
                binding.topHeadline.isChecked = true
                Log.v("it is the culprit","")
                card.animate().y(
                    viewModel.cardDownValue
                )
                    .setDuration(0)
                    .start()
            }
        }


        searchView.queryHint = "Search News"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.v("new", "$query")
                viewModel.updateSearchQuery(query)
                viewModel.updatePage(1)
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

        //..........................................................................................

        binding.topHeadline.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
        binding.topHeadline.setOnCheckedChangeListener { _, b ->
            val chip = binding.topHeadline
            if (b) {
                viewModel.getTopHeadlines( "",country)
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
                viewModel.getTopHeadlines("business",country)
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
                viewModel.getTopHeadlines("technology",country)
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
                viewModel.getTopHeadlines("sports",country)
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
                viewModel.getTopHeadlines("entertainment",country)
                lifecycleScope.launch {
                    scrollToTop()
                }
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(20F)
            } else {
                chip.shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(50F)
            }
        }
    }

    private suspend fun scrollToTop() {
        delay(1000)
        recyclerView.smoothScrollToPosition(0)
    }

    private fun pageNavigator(filterData: FilterData, page: Int, query: String?, fromValue: String?, toValue: String?) {
        var pageValue = page
        if (query != null) {
            binding.backButton.setOnClickListener {
                if (pageValue > 1) {
                    pageValue--
                    viewModel.updatePage(pageValue)
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
                if (pageValue < 5){
                    pageValue++
                    viewModel.updatePage(pageValue)
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
                Log.v("search result","${viewModel.allArticles.value}")
            }

            binding.doubleBack.setOnClickListener {
                if (pageValue > 1) {
                    val difference = pageValue - 1
                    pageValue -= difference
                    viewModel.updatePage(pageValue)
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
                if (pageValue < 5) {
                    val difference = 5 - pageValue
                    pageValue += difference
                    viewModel.updatePage(pageValue)
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
    }

    fun unCheckAll() {
        binding.topHeadline.isChecked = false
        binding.business.isChecked = false
        binding.Technology.isChecked = false
        binding.Sports.isChecked = false
        binding.Entertainment.isChecked = false
    }
}