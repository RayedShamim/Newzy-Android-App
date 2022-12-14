package com.example.newzy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newzy.databinding.WebpageDisplayBinding

class WebpageDisplay: Fragment() {

    private var _binding: WebpageDisplayBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: WebpageDisplayArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebpageDisplayBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webpageDisplay = binding.webView

        val url = navigationArgs.url

        webpageDisplay.loadUrl(url)

    }

}