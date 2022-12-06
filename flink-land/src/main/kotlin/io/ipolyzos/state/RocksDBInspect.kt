package io.ipolyzos.state

import org.rocksdb.*
import java.io.File

fun main() {
    RocksDB.loadLibrary()

    val cfOptions: ColumnFamilyOptions = ColumnFamilyOptions()
        .optimizeUniversalStyleCompaction()

    val cfDescriptors: List<ColumnFamilyDescriptor> = listOf(
        ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOptions),
        ColumnFamilyDescriptor("events".toByteArray(), cfOptions),
        ColumnFamilyDescriptor("products".toByteArray(), cfOptions),
        ColumnFamilyDescriptor("users".toByteArray(), cfOptions),
        ColumnFamilyDescriptor("_timer_state/event_user-timers".toByteArray(), cfOptions),
        ColumnFamilyDescriptor("_timer_state/processing_user-timers".toByteArray(), cfOptions)
    )

    File(System.getProperty("user.dir") + "/state/rocksdb/")
        .listFiles()
        ?.forEach { dir ->
            // a list which will hold the handles for the column families once the db is opened
            val columnFamilyHandleList: List<ColumnFamilyHandle> = mutableListOf<ColumnFamilyHandle>()
            val dbPath = "$dir/db/"
            val options = DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true)

            val db = RocksDB
                .open(options, dbPath, cfDescriptors, columnFamilyHandleList)

            println("Processing state of operator: ${dbPath.split("/").filter { it.startsWith("job") }.first()}}")
            columnFamilyHandleList.forEach { columnFamilyHandle ->
                var count = 0
                val iterator = db.newIterator(columnFamilyHandle)
                iterator.seekToFirst()

                while (iterator.isValid) {
                    count += 1
                    iterator.next()
                }
                if (count > 0) {
                    println("\tColumn Family '${String(columnFamilyHandle.name)}' has $count entries.")
                }
            }
        }
}
