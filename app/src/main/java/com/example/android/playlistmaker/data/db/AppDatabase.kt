package com.example.android.playlistmaker.data.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.playlistmaker.data.db.dao.FavouriteTrackDao
import com.example.android.playlistmaker.data.db.entity.FavouriteTrackEntity

@Database(version = 1, entities = [FavouriteTrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun favouriteTrackDao(): FavouriteTrackDao

}