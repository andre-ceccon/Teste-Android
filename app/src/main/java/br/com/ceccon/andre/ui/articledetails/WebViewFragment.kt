package br.com.ceccon.andre.ui.articledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import br.com.ceccon.andre.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentWebViewBinding? = null

    private val args: WebViewFragmentArgs by lazy {
        WebViewFragmentArgs.fromBundle(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(args.url)
        }
    }
}