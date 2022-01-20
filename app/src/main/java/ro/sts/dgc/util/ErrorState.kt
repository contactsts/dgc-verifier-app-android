package ro.sts.dgc.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ro.sts.dgc.R

enum class ErrorState(
    @field:StringRes @param:StringRes val titleResId: Int,
    @field:StringRes @param:StringRes val textResId: Int,
    @field:StringRes @param:StringRes val actionResId: Int,
    @field:DrawableRes @param:DrawableRes val imageResId: Int
) {
    NETWORK(
        R.string.error_network_title,
        R.string.error_network_text,
        R.string.error_action_retry,
        R.drawable.ic_error_triangle
    ),
    CAMERA_ACCESS_DENIED(
        R.string.error_camera_permission_title,
        R.string.error_camera_permission_text,
        R.string.error_action_change_settings,
        R.drawable.ic_camera_off
    ),
    NO_VALID_QR_CODE(
        R.string.error_title,
        R.string.error_invalid_qr_text,
        R.string.error_action_ok,
        R.drawable.ic_error_triangle
    ),
    NO_BACKEND_SYNCHRONIZATION(
        R.string.error_title,
        R.string.error_backend_synchronization_text,
        R.string.error_action_settings,
        R.drawable.ic_error_triangle
    );
}