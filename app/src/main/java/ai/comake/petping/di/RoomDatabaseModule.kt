package ai.comake.petping.di

import ai.comake.petping.data.db.ALL_MIGRATIONS
import ai.comake.petping.data.db.Database
import ai.comake.petping.data.db.badge.BadgeDao
import ai.comake.petping.data.db.walk.WalkDao
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): Database {
        return Room.databaseBuilder(
            appContext,
            Database::class.java,
            "PetPing.db"
        )
            .addMigrations(*ALL_MIGRATIONS)
//            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWalkDao(database : Database)  : WalkDao {
        return database.getWalkDao()
    }

    @Provides
    fun provideBadgeDaos(database : Database)  : BadgeDao {
        return database.getBadgeDao()
    }
}