package com.peluso.walletguru.database

import androidx.room.*
import com.peluso.walletguru.model.SubmissionCell

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM submissioncell")
    fun getAllFavorites(): List<SubmissionCell>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(favorite: SubmissionCell)

    @Delete
    fun removeFavorite(favorite: SubmissionCell)

}
