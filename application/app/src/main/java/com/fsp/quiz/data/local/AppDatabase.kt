package com.fsp.quiz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fsp.quiz.data.question.Question
import com.fsp.quiz.data.question.QuestionDao
import com.fsp.quiz.data.quiz.Quiz
import com.fsp.quiz.data.quiz.QuizDao

@Database(
    entities = [Quiz::class, Question::class], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room
                .databaseBuilder(context.applicationContext, AppDatabase::class.java, "quizapp.db")
                .build()
                .also { INSTANCE = it }
        }
    }
}
