package com.example.auctionapp.utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.statements
import java.sql.resultsset

object MotherDuckConnectionHelper {

    private const val MOTHERDUCK_URL = "jdbc:duckdb:md:TestDB?motherduck_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im9sczEyMDIzQGJ5dWkuZWR1Iiwic2Vzc2lvbiI6Im9sczEyMDIzLmJ5dWkuZWR1IiwicGF0IjoiTmpYbGd3ZXlXMUJjaXY2dEpMQUl6b1ozenV2RVhPNHVoYi00YTc4VUtKTSIsInVzZXJJZCI6IjY3ZDYzMWRmLTgzMjktNDhjYi04ZjRhLTk4MWI4YTI3ODVhMiIsImlzcyI6Im1kX3BhdCIsInJlYWRPbmx5IjpmYWxzZSwidG9rZW5UeXBlIjoicmVhZF93cml0ZSIsImlhdCI6MTczMzYwNzk4NX0.cesuoNv2A4wTfW7j7z2grEutIkVnLyRgrv7VYzP_RBE"
    private const val USERNAME = "paddle number"
    private const val PASSWORD = "amount"

    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(MOTHERDUCK_URL, USERNAME, PASSWORD)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }
}
