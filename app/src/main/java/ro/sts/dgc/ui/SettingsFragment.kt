package ro.sts.dgc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ro.sts.dgc.BuildConfig
import ro.sts.dgc.R
import ro.sts.dgc.databinding.FragmentSettingsBinding
import ro.sts.dgc.databinding.ItemSettingLanguageBinding
import ro.sts.dgc.formatWith
import ro.sts.dgc.toLocalDateTime
import ro.sts.dgc.ui.model.SettingsViewModel
import ro.sts.dgc.util.LocaleUtil.DEFAULT_COUNTRY
import java.util.*

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()

    companion object {
        private const val LAST_UPDATE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false).apply {
            val currentLanguage = getString(R.string.language_key)

            listOf("en", "ro").forEach { language ->
                ItemSettingLanguageBinding.inflate(inflater, languageList, true).apply {
                    val locale = Locale(language, DEFAULT_COUNTRY)
                    radiobutton.text = locale.getDisplayLanguage(locale).capitalize(locale)
                    radiobutton.isChecked = locale.language == currentLanguage

                    radiobutton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            updateLanguage(language)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.version.value = "${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TIME})"
        binding.privacyAndTerms.root.setOnClickListener {
            navigateToPrivacyPolicyPage()
        }
        binding.syncPublicKeys.root.setOnClickListener {
            viewModel.syncPublicKeys()
        }

        viewModel.inProgress.observe(viewLifecycleOwner, {
            binding.privacyAndTerms.root.isClickable = it != true
            binding.syncPublicKeys.root.isClickable = it != true
            binding.progressBar.visibility = if (it == true) View.VISIBLE else View.GONE
        })
        viewModel.lastSyncLiveData.observe(viewLifecycleOwner, {
            if (it <= 0) {
                binding.syncPublicKeys.summaryView.visibility = View.GONE
            } else {
                binding.syncPublicKeys.summaryView.visibility = View.VISIBLE
                binding.syncPublicKeys.summary = getString(R.string.last_updated, it.toLocalDateTime().formatWith(LAST_UPDATE_DATE_TIME_FORMAT))
            }
        })
    }

    private fun updateLanguage(language: String) {
        viewModel.setUserLanguage(language)
        requireActivity().recreate()
    }

    private fun navigateToPrivacyPolicyPage() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyPolicyFragment();
        findNavController().navigate(action);
    }
}