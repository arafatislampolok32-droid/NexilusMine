package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.ChannelPostEntity
import com.example.data.model.MissionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NexilusDao {
    // Users
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun observeUser(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY totalEarned DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY totalEarned DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users ORDER BY referralCount DESC, referralEarnings DESC LIMIT 50")
    fun observeTopReferrers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY totalEarned DESC, balance DESC LIMIT 50")
    fun observeTopEarners(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE deviceHash = :deviceHash")
    suspend fun countUsersByDeviceHash(deviceHash: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Transactions
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC LIMIT 200")
    fun observeAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND meta LIKE '%' || :cycleId || '%' LIMIT 1")
    suspend fun findTransactionByCycleId(userId: String, cycleId: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txs: List<TransactionEntity>)

    // Withdrawals
    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC")
    fun observeAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE wdId = :wdId LIMIT 1")
    suspend fun getWithdrawalById(wdId: String): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWithdrawal(withdrawal: WithdrawalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWithdrawals(withdrawals: List<WithdrawalEntity>)

    // Missions
    @Query("SELECT * FROM missions WHERE status = 'ACTIVE' ORDER BY reward DESC")
    fun observeActiveMissions(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions ORDER BY reward DESC")
    fun observeAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMission(mission: MissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMissions(missions: List<MissionEntity>)

    @Query("DELETE FROM missions WHERE missionId = :missionId")
    suspend fun deleteMission(missionId: String)

    // Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: AppSettingsEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY ts DESC LIMIT 250")
    fun observeAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Channel Posts (@NexilusPayLogs + Bot DMs)
    @Query("SELECT * FROM channel_posts ORDER BY createdAt DESC LIMIT 100")
    fun observeChannelPosts(): Flow<List<ChannelPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelPost(post: ChannelPostEntity)
}

@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        WithdrawalEntity::class,
        MissionEntity::class,
        AuditLogEntity::class,
        ChannelPostEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NexilusDatabase : RoomDatabase() {
    abstract fun nexilusDao(): NexilusDao

    companion object {
        @Volatile
        private var INSTANCE: NexilusDatabase? = null

        fun getInstance(context: Context): NexilusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexilusDatabase::class.java,
                    "nexilus_mine_prod.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
