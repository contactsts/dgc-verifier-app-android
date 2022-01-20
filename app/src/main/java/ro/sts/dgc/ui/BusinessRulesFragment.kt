package ro.sts.dgc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.databinding.FragmentBusinessRulesBinding
import ro.sts.dgc.ui.countrypicker.Country
import ro.sts.dgc.ui.model.BusinessRulesViewModel
import ro.sts.dgc.ui.model.RuleModel
import ro.sts.dgc.ui.model.adapter.RuleListAdapter
import ro.sts.dgc.ui.model.toRuleModel

@AndroidEntryPoint
class BusinessRulesFragment : Fragment() {

    private var _binding: FragmentBusinessRulesBinding? = null
    private val binding get() = _binding!!

    private lateinit var ruleListAdapter: RuleListAdapter

    private val viewModel by viewModels<BusinessRulesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ruleListAdapter = RuleListAdapter(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBusinessRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerViewRulesList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRulesList.adapter = ruleListAdapter

        viewModel.rules.observe(viewLifecycleOwner, { rules ->
            val list = mutableListOf<RuleModel>()
            rules.forEach {
                list.add(it.toRuleModel())
            }
            ruleListAdapter.update(list)
        })

        viewModel.inProgress.observe(viewLifecycleOwner, {
            binding.progressContainer.visibility = if (it == true) View.VISIBLE else View.GONE
        })

        setUpCountryPicker()
    }

    private fun setUpCountryPicker() {
        viewModel.countries.observe(viewLifecycleOwner, { countryList ->
            binding.countryPicker.setAllowedCountries(countryList)

            binding.countryPicker.onSelectedCountryChanged = { country ->
                viewModel.selectCountry(country)
            }
        })

        val ro = Country.countries.get(PreferencesImpl.DEFAULT_COUNTRY_ISO_CODE)!!
        binding.countryPicker.setCountry(ro)
        viewModel.selectCountry(ro)
    }
}