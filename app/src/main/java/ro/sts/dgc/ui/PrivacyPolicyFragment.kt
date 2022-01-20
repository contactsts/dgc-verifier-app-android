package ro.sts.dgc.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ro.sts.dgc.databinding.FragmentPrivacyPolicyBinding
import ro.sts.dgc.listen
import ro.sts.dgc.ui.data.PrivacyPolicyRepository
import ro.sts.dgc.ui.model.PrivacyPolicyViewModel

@AndroidEntryPoint
class PrivacyPolicyFragment : Fragment() {

    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PrivacyPolicyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.privacyPolicyContentLiveData.listen(this) { privacyPolicyContent ->
            with(binding.webViewPrivacyPolicy) {
                WebView.setWebContentsDebuggingEnabled(true)

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = MyWebViewClient()
                webChromeClient = WebChromeClient()

                setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if (keyCode == android.view.KeyEvent.KEYCODE_BACK &&
                        event.action == android.view.KeyEvent.ACTION_UP &&
                        canGoBack()
                    ) {
                        goBack()
                        return@OnKeyListener true
                    }
                    false
                })
                loadDataWithBaseURL(PrivacyPolicyRepository.TERMS_URL, privacyPolicyContent ?: "", "text/html", "utf-8", null)
            }
        }

        viewModel.getPrivacyPolicy()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (PrivacyPolicyRepository.TERMS_URL == url) {
                view?.loadUrl(url)
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressPrivacyPolicy.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressPrivacyPolicy.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            binding.progressPrivacyPolicy.visibility = View.GONE
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            binding.progressPrivacyPolicy.visibility = View.GONE
        }
    }
}