package ro.sts.dgc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dsc_certificates")
data class DscCertificate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kid: String,
    val key: String,
    val kidLocked: String?
)