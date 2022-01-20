package ro.sts.dgc.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ro.sts.dgc.R
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.databinding.FragmentCodeReaderBinding
import ro.sts.dgc.navigateSafe
import ro.sts.dgc.ui.countrypicker.Country
import ro.sts.dgc.ui.model.CodeReaderViewModel
import ro.sts.dgc.util.ErrorHelper
import ro.sts.dgc.util.ErrorState

private const val STATE_IS_TORCH_ON = "STATE_IS_TORCH_ON"
private const val PERMISSION_REQUEST_CAMERA = 13

@AndroidEntryPoint
class CodeReaderFragment : Fragment(), NavController.OnDestinationChangedListener {

    private var _binding: FragmentCodeReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<CodeReaderViewModel>()

    private lateinit var beepManager: BeepManager
    private var lastText: String? = null

    private var cameraPermissionState = CameraPermissionState.REQUESTING
    private var cameraPermissionExplanationDialog: CameraPermissionExplanationDialog? = null
    private var isTorchOn: Boolean = false

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
            if (_binding == null) {
                return
            }
            if (!result.text.startsWith("HC1:")) {
                binding.invalidCode.isVisible = true
                lifecycleScope.launch {
                    delay(2000)
                    if (_binding != null) {
                        binding.invalidCode.isVisible = false
                    }
                }
                return
            }

            binding.barcodeScanner.pause()

            lastText = result.text
            beepManager.playBeepSoundAndVibrate()

            navigateToVerificationPage(result.text)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCodeReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isTorchOn = savedInstanceState?.getBoolean(STATE_IS_TORCH_ON, isTorchOn) ?: isTorchOn
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.AZTEC, BarcodeFormat.QR_CODE)
        binding.barcodeScanner.decoderFactory = DefaultDecoderFactory(formats)
        binding.barcodeScanner.decodeContinuous(callback)
        beepManager = BeepManager(requireActivity())

        setUpCountryPicker()
        setupFlashButton()
        setupUseNationalRulesButton()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (binding != null && binding.barcodeScanner != null) {
            if (isVisibleToUser) {
                binding.barcodeScanner.resume()
            } else {
                binding.barcodeScanner.pauseAndWait()
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id == R.id.codeReaderFragment) {
            binding.barcodeScanner.resume()
            lastText = ""
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pauseAndWait()
        findNavController().removeOnDestinationChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        findNavController().addOnDestinationChangedListener(this)
        lastText = ""

        // Check permission in onResume to automatically handle the user returning from the system settings.
        // Be careful to avoid popup loops, since our fragment is resumed whenever the user returns from the dialog!
        checkCameraPermission()

        setFlashAndButtonStyle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_IS_TORCH_ON, isTorchOn)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            val isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

            cameraPermissionState = if (isGranted) CameraPermissionState.GRANTED else CameraPermissionState.DENIED
            refreshView()
        }
    }

    private fun checkCameraPermission() {
        val isGranted = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            cameraPermissionState = CameraPermissionState.GRANTED
        }
        // Do not request the permission again if the last time we tried the user denied it.
        // I.e. don't show the popup but the error view
        else if (cameraPermissionState != CameraPermissionState.DENIED) {
            cameraPermissionState = CameraPermissionState.REQUESTING
        }
        refreshView()
    }

    private fun refreshView() {
        when (cameraPermissionState) {
            CameraPermissionState.GRANTED -> {
                binding.errorView.isVisible = false
                binding.flashButton.isVisible = true
                setBackendSynchronizationObserver()
            }
            CameraPermissionState.REQUESTING -> {
                binding.errorView.isVisible = false
                binding.flashButton.isVisible = false
                showCameraPermissionExplanationDialog()
            }
            CameraPermissionState.CANCELLED, CameraPermissionState.DENIED -> {
                binding.errorView.isVisible = true
                binding.flashButton.isVisible = false
                ErrorHelper.updateErrorView(binding.errorView, ErrorState.CAMERA_ACCESS_DENIED, null, context)
            }
        }
    }

    private fun showCameraPermissionExplanationDialog() {
        if (cameraPermissionExplanationDialog?.isShowing == true) {
            return
        }

        cameraPermissionExplanationDialog = CameraPermissionExplanationDialog(requireContext()).apply {
            setOnCancelListener {
                cameraPermissionState = CameraPermissionState.CANCELLED
                refreshView()
            }
            setGrantCameraAccessClickListener {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
            setOnDismissListener {
                cameraPermissionExplanationDialog = null
            }
            show()
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlashUnit(): Boolean {
        return requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun setupFlashButton() {
        if (hasFlashUnit()) {
            binding.flashButton.isVisible = true
            setFlashAndButtonStyle()
        } else {
            binding.flashButton.isVisible = false
        }

        binding.flashButton.setOnClickListener {
            isTorchOn = !binding.flashButton.isSelected
            setFlashAndButtonStyle()
        }
    }

    private fun setFlashAndButtonStyle() {
        binding.barcodeScanner.setTorch(isTorchOn)
        val drawableId = if (isTorchOn) R.drawable.ic_light_on_black else R.drawable.ic_light_off
        binding.flashButton.isSelected = isTorchOn
        binding.flashButton.setImageResource(drawableId)
    }

    private fun setupUseNationalRulesButton() {
        viewModel.useNationalRules.observe(viewLifecycleOwner, {
            binding.explanationBusinessRules.text =
                if (it) getString(R.string.verifier_qr_scanner_validate_with_national_rules) else getString(R.string.verifier_qr_scanner_validate_with_country_rules)
            binding.countryPicker.isVisible = !it

            val drawableId = if (it) R.drawable.ic_national_rules_on_black else R.drawable.ic_national_rules_off
            binding.nationalRulesButton.isSelected = it
            binding.nationalRulesButton.setImageResource(drawableId)
        })

        binding.nationalRulesButton.setOnClickListener {
            viewModel.setUseNationalRules(!viewModel.useNationalRules.value!!)
        }
    }

    private fun setUpCountryPicker() {
        viewModel.countries.observe(viewLifecycleOwner, { pair ->
            if (pair.first.isEmpty() || pair.second == null) {
                View.GONE
            } else {
                binding.countryPicker.setAllowedCountries(pair.first)
                if (pair.second!!.isNotBlank()) {
                    binding.countryPicker.setCountry(Country.countries.getValue(pair.second!!))
                }

                binding.countryPicker.onSelectedCountryChanged = {
                    viewModel.selectCountry(it.countryCode)
                }

                View.VISIBLE
            }.apply {
                binding.countryPicker.visibility = if (viewModel.useNationalRules.value == true) View.GONE else this
            }
        })
    }

    private fun setBackendSynchronizationObserver() {
        viewModel.lastSyncLiveData.observe(viewLifecycleOwner, {
            if (it == -1L) {
                binding.errorView.isVisible = true
                ErrorHelper.updateErrorView(binding.errorView, ErrorState.NO_BACKEND_SYNCHRONIZATION, {
                    navigateToSettingPage()
                }, context)
            } else {
                binding.errorView.isVisible = false
            }
        })
    }

    private fun navigateToVerificationPage(text: String) {
        val countryCode = if (viewModel.useNationalRules.value == true) PreferencesImpl.DEFAULT_COUNTRY_ISO_CODE else binding.countryPicker.selectedCountry.countryCode
        val action = CodeReaderFragmentDirections.actionCodeReaderFragmentToCodeVerificationDialogFragment(text, countryCode, viewModel.useNationalRules.value == true)
        navigateSafe(action)
    }

    private fun navigateToSettingPage() {
        val action = CodeReaderFragmentDirections.actionCodeReaderFragmentToSettingsFragment()
        navigateSafe(action)
    }

    enum class CameraPermissionState {
        GRANTED,
        REQUESTING,
        CANCELLED,
        DENIED,
    }

}