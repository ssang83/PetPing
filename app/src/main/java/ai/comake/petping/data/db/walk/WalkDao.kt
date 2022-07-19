/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.comake.petping.data.db.walk

import androidx.room.*

@Dao
interface WalkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(walk: Walk)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(walk: Walk)

    @Query("SELECT * FROM walk WHERE walk_id = :key")
    suspend fun select(key: String): Walk?

    @Query("SELECT * FROM walk ORDER BY walk_id DESC LIMIT 1")
    suspend fun selectAll(): List<Walk>

    @Query("DELETE FROM walk WHERE walk_id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM walk")
    suspend fun deleteAll()
}