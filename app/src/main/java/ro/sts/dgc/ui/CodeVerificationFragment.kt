package ro.sts.dgc.ui

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ro.sts.dgc.*
import ro.sts.dgc.cwt.DecisionService
import ro.sts.dgc.cwt.VerificationDecision
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.databinding.FragmentCodeVerificationBinding
import ro.sts.dgc.model.ContentType
import ro.sts.dgc.model.VerificationResult
import ro.sts.dgc.ui.countrypicker.Country
import ro.sts.dgc.ui.model.*
import ro.sts.dgc.ui.model.adapter.CertificateListAdapter
import ro.sts.dgc.ui.model.adapter.RuleValidationResultListAdapter

@AndroidEntryPoint
class CodeVerificationDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TOP_MARGIN = 50
    }

    private val args by navArgs<CodeVerificationDialogFragmentArgs>()
    private val viewModel by viewModels<CodeVerificationViewModel>()

    private var _binding: FragmentCodeVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var certificateListAdapter: CertificateListAdapter
    private lateinit var ruleValidationResultListAdapter: RuleValidationResultListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        certificateListAdapter = CertificateListAdapter(layoutInflater)
        ruleValidationResultListAdapter = RuleValidationResultListAdapter(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCodeVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewHeight()

        dialog.expand()

        binding.recyclerViewCertList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCertList.adapter = certificateListAdapter

        binding.recyclerViewRulesList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRulesList.adapter = ruleValidationResultListAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.staticViewPager.currentItem = 0
                    }
                    1 -> {
                        binding.staticViewPager.currentItem = 1
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.staticViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
        binding.countryBusinessRulesTitle.text = if (args.useNationalRules) getString(R.string.business_rules_domestic) else
            Country.countries.getOrDefault(args.countryIsoCode, Country.countries[PreferencesImpl.DEFAULT_COUNTRY_ISO_CODE])?.name?.let { getString(it) } ?: ""

        binding.actionBtn.setOnClickListener { dismiss() }

        viewModel.verificationResult.observe(viewLifecycleOwner, { verificationResult ->
            if (verificationResult == null) {
                dismiss()
            } else {
                val verificationDecision = DecisionService().decide(verificationResult)

                setCertStatusUI(verificationDecision, verificationResult)
                setCertStatusError(verificationDecision)
                setCertDataVisibility(verificationDecision)

                setCertTechnicalData(verificationResult)
            }
        })
        viewModel.certificate.observe(viewLifecycleOwner, { certificate ->
            if (certificate != null) {
                toggleButton(certificate)
                showUserData(certificate)

                val list = getCertificateListData(certificate)
                certificateListAdapter.update(list)
            }
        })
        viewModel.rulesValidationResults.observe(viewLifecycleOwner, { ruleValidationResults ->
            val list = mutableListOf<RuleValidationResultModel>()
            ruleValidationResults.forEach {
                list.add(it.toRuleValidationResultModel())
            }
            ruleValidationResultListAdapter.update(list)
        })


        viewModel.inProgress.observe(viewLifecycleOwner, {
            binding.progressContainer.isVisible = it
        })

        viewModel.init(args.qrCodeText, args.countryIsoCode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewHeight() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val params = binding.content.layoutParams as FrameLayout.LayoutParams
        params.height = height - TOP_MARGIN.dpToPx()
    }

    private fun setCertStatusUI(verificationDecision: VerificationDecision, verificationResult: VerificationResult) {
        val text: String
        val imageId: Int
        val statusColor: ColorStateList
        val actionBtnText: String

        binding.testCertificateLegalNotice.isVisible = false

        when (verificationDecision.result) {
            VerificationDecision.Result.GOOD -> {
                text = getString(R.string.cert_valid)
                imageId = R.drawable.ic_hcert_valid
                statusColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.hcert_good))
                actionBtnText = getString(R.string.done)

                if (args.useNationalRules && verificationResult.contentType.contains(ContentType.TEST)) {
                    binding.testCertificateLegalNotice.isVisible = true
                }
            }
            VerificationDecision.Result.FAIL -> {
                text = getString(R.string.cert_invalid)
                imageId = R.drawable.ic_hcert_invalid
                statusColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.hcert_fail))
                actionBtnText = getString(R.string.retry)
            }
        }

        binding.status.text = text
        binding.certStatusIcon.setImageResource(imageId)
        binding.verificationStatusBg.backgroundTintList = statusColor
        binding.actionBtn.isVisible = true
        binding.actionBtn.backgroundTintList = statusColor
        binding.actionBtn.text = actionBtnText
    }

    private fun setCertStatusError(verificationDecision: VerificationDecision) {
        if (verificationDecision.result == VerificationDecision.Result.FAIL) {
            binding.reasonForCertificateInvalidity.visibility = View.VISIBLE

            when (verificationDecision.reason) {
                VerificationDecision.Reason.COSE_VERIFICATION_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_valid, "CS")
                }
                VerificationDecision.Reason.COSE_VERIFICATION_FAILED_NO_DSC -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_verifiable, "NODSC")
                }
                VerificationDecision.Reason.BASE45_DECODE_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_content_schema_not_valid, "BS45")
                }
                VerificationDecision.Reason.CWT_DECODE_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_content_schema_not_valid, "CWT")
                }
                VerificationDecision.Reason.CBOR_DECODE_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_content_schema_not_valid, "CBR")
                }
                VerificationDecision.Reason.CONTEXT_IDENTIFIER_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_content_schema_not_valid, "CTX")
                }
                VerificationDecision.Reason.ISSUED_AT_BEFORE_DSC_VALID_FROM -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_valid, "IBDVF")
                }
                VerificationDecision.Reason.ISSUED_AT_AFTER_NOW -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_valid, "IAN")
                }
                VerificationDecision.Reason.EXPIRATION_AT_AFTER_DSC_VALID_UNTIL -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_valid, "EADVU")
                }
                VerificationDecision.Reason.EXPIRATION_AT_BEFORE_NOW -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_expired)
                }
                VerificationDecision.Reason.CERTIFICATE_ENTRY_TYPE_INVALID -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_cryptographic_signature_not_valid, "CETOID")
                }
                VerificationDecision.Reason.CERTIFICATE_VACCINATION_ENTRY_DATE_IN_FUTURE -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_vaccination_entry_date_in_future)
                }
                VerificationDecision.Reason.CERTIFICATE_TEST_ENTRY_RESULT_POSITIVE -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_test_entry_result_positive)
                }
                VerificationDecision.Reason.CERTIFICATE_TEST_ENTRY_TEST_DATE_IN_FUTURE -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_test_entry_test_date_in_future)
                }
                VerificationDecision.Reason.CERTIFICATE_RECOVERY_ENTRY_NOT_VALID_SO_FAR -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_recovery_entry_not_valid_so_far)
                }
                VerificationDecision.Reason.CERTIFICATE_RECOVERY_ENTRY_NOT_VALID_ANYMORE -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_recovery_entry_not_valid_anymore)
                }
                VerificationDecision.Reason.RULES_VALIDATION_FAILED -> {
                    binding.reasonForCertificateInvalidity.text = getString(R.string.certificate_rules_validation_failed)
                }
                else -> {

                }
            }
        } else {
            binding.reasonForCertificateInvalidity.visibility = View.GONE
        }
    }

    private fun setCertDataVisibility(verificationDecision: VerificationDecision) {
        val visibility = if (verificationDecision.result == VerificationDecision.Result.GOOD) View.GONE else View.VISIBLE
        binding.reasonForCertificateInvalidity.visibility = visibility
    }

    private fun setCertTechnicalData(verificationResult: VerificationResult) {
        binding.issuerCountryValue.text = verificationResult.issuer.orEmpty()
        binding.issuedAtValue.text = verificationResult.issuedAt?.toString().orEmpty()
        binding.expiresAtValue.text = verificationResult.expirationTime?.toString().orEmpty()

        binding.dscValidFromValue.text = verificationResult.certificateValidFrom?.toString().orEmpty()
        binding.dscValidToValue.text = verificationResult.certificateValidUntil?.toString().orEmpty()
    }

    private fun getCertificateListData(certificate: CertificateModel): List<CertificateData> {
        val list = mutableListOf<CertificateData>()
        list.addAll(certificate.vaccinations ?: emptyList())
        list.addAll(certificate.tests ?: emptyList())
        list.addAll(certificate.recoveryStatements ?: emptyList())

        return list
    }

    private fun showUserData(certificate: CertificateModel) {
        binding.personFullName.text = getString(R.string.person_full_name_placeholder, certificate.person.givenName, certificate.person.familyName)
        binding.familyNameTransliterated.text = certificate.person.familyNameTransliterated
        binding.givenNameTransliterated.text = certificate.person.givenNameTransliterated
        if (certificate.dateOfBirth == null) {
            binding.dateOfBirth.text = certificate.dateOfBirthString?.parseFromTo(YEAR_MONTH, FORMATTED_YEAR_MONTH) ?: certificate.dateOfBirthString.orEmpty()
        } else {
            binding.dateOfBirth.text = certificate.dateOfBirthString?.parseFromTo(YEAR_MONTH_DAY, FORMATTED_YEAR_MONTH_DAY) ?: ""
        }

        if (certificate.vaccinations != null && certificate.vaccinations.isNotEmpty()) {
            binding.certificateIdValue.text = certificate.vaccinations[0].certificateIdentifier
        }
        if (certificate.tests != null && certificate.tests.isNotEmpty()) {
            binding.certificateIdValue.text = certificate.tests[0].certificateIdentifier
        }
        if (certificate.recoveryStatements != null && certificate.recoveryStatements.isNotEmpty()) {
            binding.certificateIdValue.text = certificate.recoveryStatements[0].certificateIdentifier
        }
        binding.schemaVersionValue.text = certificate.schemaVersion
    }

    private fun toggleButton(certificate: CertificateModel) {
        when {
            certificate.vaccinations?.isNotEmpty() == true -> enableToggleBtn(binding.vacToggle)
            certificate.recoveryStatements?.isNotEmpty() == true -> enableToggleBtn(binding.recToggle)
            certificate.tests?.isNotEmpty() == true -> enableToggleBtn(binding.testToggle)
        }
    }

    private fun enableToggleBtn(button: MaterialButton) {
        button.toggle()
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }
}

fun Dialog?.expand() {
    this?.let { dialog ->
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let {
                val bottomSheetBehavior = BottomSheetBehavior.from(it)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.peekHeight = it.height
                it.setBackgroundResource(android.R.color.transparent)
            }
        }
    }
}