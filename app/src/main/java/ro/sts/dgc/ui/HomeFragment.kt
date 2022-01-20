package ro.sts.dgc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ro.sts.dgc.databinding.FragmentHomeBinding
import ro.sts.dgc.navigateSafe
import ro.sts.dgc.pager.HomePageAdapter
import ro.sts.dgc.pager.HomePagerFragment

class HomeFragment : Fragment(), NavController.OnDestinationChangedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback { requireActivity().finish() }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HomePageAdapter(this, HomePagerFragment.getDescriptions().size)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
            //Some implementation
        }.attach()

        binding.header.settings.setOnClickListener {
            navigateToSettingPage()
        }
        binding.scanButton.setOnClickListener {
            navigateToCodeReaderPage()
        }
        binding.businessRulesButton.setOnClickListener {
            navigateToBusinessRulesPage()
        }
    }

    override fun onPause() {
        super.onPause()
        findNavController().removeOnDestinationChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        findNavController().addOnDestinationChangedListener(this)
    }

    private fun navigateToCodeReaderPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToCodeReaderFragment()
        navigateSafe(action)
    }

    private fun navigateToBusinessRulesPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToBusinessRulesFragment()
        navigateSafe(action)
    }

    private fun navigateToSettingPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
        navigateSafe(action)
    }

}