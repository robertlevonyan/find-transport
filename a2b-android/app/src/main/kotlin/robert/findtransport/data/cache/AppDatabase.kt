package robert.findtransport.data.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import robert.findtransport.data.entity.*
import robert.findtransport.utils.DATABASE_NAME

@Database(
  entities = [
    History::class,
    Stop::class,
    StopLocation::class,
    Transport::class,
    TransportStopJoin::class,
//    TransportRoute::class,
  ],
  version = 27,
  exportSchema = true
)
@TypeConverters(TransportRouteTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun stopsDao(): StopsDao

  abstract fun transportsDao(): TransportsDao

  abstract fun historyDao(): HistoryDao

  companion object {
    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase =
      instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }

    private fun buildDatabase(context: Context): AppDatabase =
      Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()
  }
}
