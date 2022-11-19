package com.example.newzy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.newzy.database.CountryNewzy
import com.example.newzy.databinding.FilterFragmentBinding
import com.example.newzy.model.CountriesList
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class FilterFragment: androidx.fragment.app.Fragment() {

    val viewModel: NewzyViewModel by activityViewModels {
        NewzyViewModelFactory(
            (activity?.application as NewzyApplication).database.filterDataDao()
        )
    }

    private var _binding: FilterFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        slider()
        calenderFilter()
        language()
    }

    fun language() {
        val spinnerLanguage = binding.languageSelector

        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_names,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerLanguage.adapter = languageAdapter
        viewModel.retrieveFilterData.observe(this.viewLifecycleOwner) {
            val selector: String? = it.language
            val position = languageAdapter.getPosition(selector)
            spinnerLanguage.setSelection(position)
        }
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = languageAdapter.getItem(p2)
                viewModel.updateLanguage(value.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun calenderFilter() {
        var fromDate: Long? = null
        var toDate: Long? = null
        viewModel.retrieveFilterData.observe(this.viewLifecycleOwner) {
            fromDate = it.from
            toDate = it.to
        }

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        viewModel.retrieveFilterData.observe(this.viewLifecycleOwner) {
            if (it.from != null){
                binding.fromEditText.setText(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(it.from)
                )
            } else {
                binding.fromEditText.text = it.from
            }
            if (binding.fromEditText.text.isNullOrEmpty()) {
                binding.fromSelectButton.text = "Select..."
                binding.fromSelectButton.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select From")
                        .setSelection(fromDate)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build()

                    datePicker.show(this.childFragmentManager,"Tag")

                    datePicker.addOnPositiveButtonClickListener {
                        viewModel.updateFrom(it)
                    }
                }
            } else {
                binding.fromSelectButton.text = "Deselect"
                binding.fromSelectButton.setOnClickListener {
                    viewModel.updateFrom(null)
                }
            }
            //----------------------------------------------------------------
            if (it.to != null) {
                binding.to.setText(
                    SimpleDateFormat(
                "yyyy-MM-dd",
                    Locale.getDefault())
                        .format(it.to)
                )
            } else {
                binding.to.text = it.to
            }
            if (binding.to.text.isNullOrEmpty()){
                binding.toSelectButton.text = "Select..."
                binding.toSelectButton.setOnClickListener {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select To")
                        .setSelection(toDate)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build()

                    datePicker.show(this.childFragmentManager, "Tag")

                    datePicker.addOnPositiveButtonClickListener {
                        viewModel.updateTo(it)
                        binding.to.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it))
                    }
                }
            } else {
                binding.toSelectButton.text = "Deselect"
                binding.toSelectButton.setOnClickListener {
                    viewModel.updateTo(null)
                }
            }
        }
    }

    private fun slider() {
        val slider1 = binding.articlesSlider
        val newValue = viewModel.retrieveFilterData.value!!.sliderCount!!.toFloat()
        slider1.value = newValue
        slider1.addOnChangeListener { slider, value, fromUser ->
            viewModel.updateSlider(value)
        }
    }

    private fun bind() {

        binding.backButton.setOnClickListener {
            this.findNavController().navigateUp()
        }

        val spinnerCountry = binding.countrySpinner

        val countryNames = mutableListOf<String>()      //
        for (country in CountriesList.country) {        //    Creating country names list
            countryNames.add(country.name)              //
        }                                               //

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = adapter

        val oldCountry = viewModel.country.value
        val positionCountry = adapter.getPosition(oldCountry?.name)
        spinnerCountry.setSelection(positionCountry)

        spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (oldCountry != null) {
                    val newCountry = CountryNewzy(
                        id = oldCountry.id,
                        name = CountriesList.country[p2].name,
                        code = CountriesList.country[p2].code
                    )
                    viewModel.updateCountry(newCountry)
                } else {
                    val newCountry = CountryNewzy(
                        id = null,
                        name = CountriesList.country[p2].name,
                        code = CountriesList.country[p2].code
                    )
                    viewModel.insertCountry(newCountry)
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

}