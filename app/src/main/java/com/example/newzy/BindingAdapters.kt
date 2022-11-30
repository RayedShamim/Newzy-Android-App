package com.example.newzy

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("newsApiStatus")
fun bindStatus(connectionErrorView: ImageView, status: NewsApiStatus?) {
    Log.v("bindingCheck","$status")
    Log.v("bind","${connectionErrorView.id}")

    when (status) {

        NewsApiStatus.DONE -> {
            connectionErrorView.visibility = View.GONE
        }

        NewsApiStatus.ERROR -> {
            connectionErrorView.visibility = View.VISIBLE
            connectionErrorView.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }

        NewsApiStatus.LOADING -> {
            connectionErrorView.visibility = View.GONE

        }

        null -> {
            connectionErrorView.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }

        else -> {}
    }
}

@BindingAdapter("apiLoadingStatus")
fun bindLoadingStatus(loadingAnimationView: ProgressBar, apiStatus: NewsApiStatus?) {
    when (apiStatus) {

        NewsApiStatus.LOADING -> {
            loadingAnimationView.visibility = View.VISIBLE
        }

        NewsApiStatus.ERROR -> {
            loadingAnimationView.visibility = View.GONE
        }

        NewsApiStatus.DONE -> {
            loadingAnimationView.visibility = View.GONE
        }

        else -> {}
    }
}