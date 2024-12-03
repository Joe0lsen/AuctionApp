package com.example.auctionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.FileProvider
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ExcelActivity : AppCompatActivity() {

    private val excelFileName = "auction_data.xlsx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excel)

        val recyclerView = findViewById<RecyclerView>(R.id.bidsRecyclerView)
        val shareButton = findViewById<Button>(R.id.shareButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val bidList = loadBidsFromExcel()
        recyclerView.adapter = BidsAdapter(bidList)

        shareButton.setOnClickListener {
            shareExcelFile()
        }
    }

    private fun loadBidsFromExcel(): List<BidEntry> {
        val bidList = mutableListOf<BidEntry>()
        try {
            val file = File(filesDir, excelFileName)
            if (!file.exists()) createInitialExcelFile(file)

            val inputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet.drop(1)) {
                val name = row.getCell(0)?.toString() ?: ""
                val paddleNumber = row.getCell(1)?.toString() ?: ""
                val bidAmount = row.getCell(2)?.toString()?.toDoubleOrNull()
                bidList.add(BidEntry(name, paddleNumber, bidAmount))
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bidList
    }

    private fun createInitialExcelFile(file: File) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Auction Data")
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Name")
        headerRow.createCell(1).setCellValue("Paddle Number")
        headerRow.createCell(2).setCellValue("Bid Amount")

        val outputStream = FileOutputStream(file)
        workbook.write(outputStream)
        outputStream.close()
    }

    private fun shareExcelFile() {
        val file = File(filesDir, excelFileName)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Excel File"))
        } else {
            Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show()
        }
    }
}
