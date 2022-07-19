package ai.comake.petping.data.db.badge

import javax.inject.Inject

class BadgeRepository @Inject constructor(private val badgeDao: BadgeDao) {
    suspend fun insert(badge: Badge) = badgeDao.insert(badge)
    suspend fun update(badge: Badge) = badgeDao.update(badge)
    suspend fun delete(badgeSeq: Int) = badgeDao.delete(badgeSeq)
    suspend fun deleteAll() = badgeDao.deleteAll()
    suspend fun select(badgeSeq: Int) = badgeDao.select(badgeSeq)
    suspend fun selectAll() = badgeDao.selectAll()
}

