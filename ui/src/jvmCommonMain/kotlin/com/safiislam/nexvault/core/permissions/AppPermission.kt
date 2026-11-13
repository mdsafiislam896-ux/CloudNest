package com.safiislam.nexvault.core.permissions

import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.permission_all_files_rationale
import com.safiislam.nexvault.resources.permission_all_files_title
import com.safiislam.nexvault.resources.permission_battery_rationale
import com.safiislam.nexvault.resources.permission_battery_title
import com.safiislam.nexvault.resources.permission_notifications_rationale
import com.safiislam.nexvault.resources.permission_notifications_title
import com.safiislam.nexvault.resources.permission_photos_rationale
import com.safiislam.nexvault.resources.permission_photos_title
import com.safiislam.nexvault.resources.permission_videos_rationale
import com.safiislam.nexvault.resources.permission_videos_title
import org.jetbrains.compose.resources.StringResource

/** [critical] marks the permissions automatic backup cannot work without. */
enum class AppPermission(
    val titleRes: StringResource,
    val rationaleRes: StringResource,
    val critical: Boolean
) {
    MEDIA_IMAGES(
        titleRes = Res.string.permission_photos_title,
        rationaleRes = Res.string.permission_photos_rationale,
        critical = true
    ),
    MEDIA_VIDEO(
        titleRes = Res.string.permission_videos_title,
        rationaleRes = Res.string.permission_videos_rationale,
        critical = true
    ),
    NOTIFICATIONS(
        titleRes = Res.string.permission_notifications_title,
        rationaleRes = Res.string.permission_notifications_rationale,
        critical = false
    ),
    ALL_FILES(
        titleRes = Res.string.permission_all_files_title,
        rationaleRes = Res.string.permission_all_files_rationale,
        critical = true
    ),
    UNRESTRICTED_BATTERY(
        titleRes = Res.string.permission_battery_title,
        rationaleRes = Res.string.permission_battery_rationale,
        critical = false
    );

    val isSpecialAccess: Boolean get() = this == ALL_FILES || this == UNRESTRICTED_BATTERY
}
