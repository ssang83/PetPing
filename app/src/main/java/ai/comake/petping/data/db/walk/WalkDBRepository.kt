package ai.comake.petping.google.database.room.walk

import ai.comake.petping.data.db.walk.Walk
import ai.comake.petping.data.db.walk.WalkDao
import javax.inject.Inject

class WalkDBRepository @Inject constructor(private val waklDao: WalkDao) {
    suspend fun insert(walk: Walk) = waklDao.insert(walk)
    suspend fun update(walk: Walk) = waklDao.update(walk)
    suspend fun delete(walk_id: Int) = waklDao.delete(walk_id)
    suspend fun deleteAll() = waklDao.deleteAll()
    suspend fun select(walk_id: String) = waklDao.select(walk_id)
    suspend fun selectAll() = waklDao.selectAll()
}

