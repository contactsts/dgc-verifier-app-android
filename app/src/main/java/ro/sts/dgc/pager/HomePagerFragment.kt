package ro.sts.dgc.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ro.sts.dgc.R
import ro.sts.dgc.databinding.FragmentHomePagerBinding

class HomePagerFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "ARG_POSITION"

        fun getInstance(position: Int) = HomePagerFragment().apply {
            arguments = bundleOf(ARG_POSITION to position)
        }

        fun getDescriptions() = listOf(
            R.string.verifier_homescreen_pager_description_1,
            R.string.verifier_homescreen_pager_description_2
        )
    }

    private var _binding: FragmentHomePagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)
        val homescreenPagerDescriptions: List<Int> = getDescriptions()
        val homescreenImages = getImageAssets()
        with(binding) {
            image.setImageResource(homescreenImages[position])
            description.setText(homescreenPagerDescriptions[position])

            root.post {
                // It seems that the viewpager has an issue with wrap_content and multiline text views. That's why we have to
                // request a layout on the description again after the full view is laid out, to ensure the whole text is visible.
                description.requestLayout()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getImageAssets() = listOf(
        R.drawable.ic_illu_home_1,
        R.drawable.ic_illu_home_2
    )
}