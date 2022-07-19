///*
// * Copyright 2019, The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
package ai.comake.petping.data.db.badge

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "badge")
data class Badge(

    @PrimaryKey()
    @ColumnInfo(name = "badgeSeq")
    var badgeSeq: Int = 0,

    @ColumnInfo(name = "newMissionId")
    var newMissionId: Int? = null,

    @ColumnInfo(name = "newSaveRewardId")
    var newSaveRewardId: Int? = null,

    @ColumnInfo(name = "newNoticeId")
    var newNoticeId: Int? = null,

    @ColumnInfo(name = "newReplyId")
    var newReplyId: Int? = null,

    @ColumnInfo(name = "androidNewAppVersion")
    var androidNewAppVersion: String?
) : Parcelable