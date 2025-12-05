package com.mertbek.taskexplorer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mertbek.taskexplorer.data.model.TaskItem

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskItem>

    @Query("SELECT * FROM tasks WHERE task LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun searchTasks(query: String): List<TaskItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskItem>)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}