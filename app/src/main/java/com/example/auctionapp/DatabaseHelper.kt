
package com.example.auctionapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AuctionDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_BIDS = "Bids"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_ITEM_NUMBER = "ItemNumber"
        private const val COLUMN_PADDLE_NUMBER = "PaddleNumber"
        private const val COLUMN_BID_AMOUNT = "BidAmount"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_BIDS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ITEM_NUMBER TEXT, " +
                "$COLUMN_PADDLE_NUMBER TEXT, " +
                "$COLUMN_BID_AMOUNT REAL)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BIDS")
        onCreate(db)
    }

    fun insertBid(itemNumber: String, paddleNumber: String, bidAmount: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NUMBER, itemNumber)
        values.put(COLUMN_PADDLE_NUMBER, paddleNumber)
        values.put(COLUMN_BID_AMOUNT, bidAmount)

        val result = db.insert(TABLE_BIDS, null, values)
        db.close()
        return result != -1L
    }
}
