package ro.sts.dgc.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DscCertificateDao {

    @Query("SELECT * FROM dsc_certificates")
    fun getAll(): List<DscCertificate>

    @Query("SELECT * FROM dsc_certificates WHERE kid IN (:kids)")
    fun getAllByKids(kids: Array<String>): List<DscCertificate>

    @Query("SELECT * FROM dsc_certificates WHERE kid LIKE :kid OR kidLocked LIKE :kid")
    fun getAllByKid(kid: String): List<DscCertificate>

    @Query("DELETE FROM dsc_certificates WHERE kid = :kid")
    fun deleteById(kid: String)

    @Insert
    fun insert(key: DscCertificate)

    @Delete
    fun delete(key: DscCertificate)

    @Query("DELETE FROM dsc_certificates WHERE kid NOT IN (:keyIds)")
    fun deleteAllExcept(keyIds: Array<String>)

    @Query("DELETE FROM dsc_certificates")
    fun deleteAll()

}