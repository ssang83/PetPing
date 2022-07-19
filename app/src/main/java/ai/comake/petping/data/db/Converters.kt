package ai.comake.petping.data.db

import ai.comake.petping.data.vo.WalkPath
import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun pathListToJson(value: List<WalkPath>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToPathList(value: String) = Gson().fromJson(value, Array<WalkPath>::class.java).toList()

    @TypeConverter
    fun listToJson(value: List<Int>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Int>::class.java).toList()

    @TypeConverter
    fun listStringToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}