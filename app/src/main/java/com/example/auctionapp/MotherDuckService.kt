package com.example.auctionapp.utils

import java.sql.Connection
import java.sql.ResultSet

class MotherDuckService {

    fun fetchNYCServiceRequests(startDate: String, endDate: String): List<Map<String, Any>> {
        val results = mutableListOf<Map<String, Any>>()
        val connection: Connection? = MotherDuckConnectionHelper.connect()

        connection?.use {
            val query = """
                SELECT created_date, agency, complaint_type, landmark, resolution_description 
                FROM sample_data.nyc.service_requests 
                WHERE created_date >= ? AND created_date <= ?
            """.trimIndent()

            val statement = it.prepareStatement(query)
            statement.setString(1, startDate)
            statement.setString(2, endDate)

            val resultSet: ResultSet = statement.executeQuery()
            while (resultSet.next()) {
                val row = mapOf(
                    "created_date" to resultSet.getString("created_date"),
                    "agency" to resultSet.getString("agency"),
                    "complaint_type" to resultSet.getString("complaint_type"),
                    "landmark" to resultSet.getString("landmark"),
                    "resolution_description" to resultSet.getString("resolution_description")
                )
                results.add(row)
            }
        }

        return results
    }
}
