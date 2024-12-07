package com.example.auctionapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.auctionapp.databinding.ActivityMainBinding
import com.example.auctionapp.utils.MotherDuckConnectionHelper // Import helper
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var qrCodeResult: String
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding and toolbar
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Setup navigation controller
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Initialize the database helper
        dbHelper = DatabaseHelper(this)

        // Floating action button for QR code scanning
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Scanning QR Code...", Snackbar.LENGTH_SHORT).show()
            initiateQRCodeScan()
        }
    }

    private fun initiateQRCodeScan() {
        val qrScan = IntentIntegrator(this)
        qrScan.setPrompt("Scan QR Code")
        qrScan.setBeepEnabled(true)
        qrScan.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            qrCodeResult = result.contents
            showBidEntryPopup(qrCodeResult)
        } else {
            Toast.makeText(this, "No QR Code Found", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showBidEntryPopup(itemNumber: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.bid_entry_popup)

        val paddleNumberInput = dialog.findViewById<EditText>(R.id.paddleNumberInput)
        val bidAmountInput = dialog.findViewById<EditText>(R.id.bidAmountInput)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val paddleNumber = paddleNumberInput.text.toString()
            val bidAmount = bidAmountInput.text.toString()

            if (paddleNumber.isNotEmpty() && bidAmount.isNotEmpty()) {
                val success = dbHelper.insertBid(itemNumber, paddleNumber, bidAmount.toDouble())
                if (success) {
                    Toast.makeText(this, "Submit Successful!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    // Sync the bid to MotherDuck
                    CoroutineScope(Dispatchers.IO).launch {
                        val syncSuccess = syncBidToMotherDuck(itemNumber, paddleNumber, bidAmount.toDouble())
                        withContext(Dispatchers.Main) {
                            if (syncSuccess) {
                                Toast.makeText(this@MainActivity, "Synced with MotherDuck!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Failed to Sync with MotherDuck!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private suspend fun syncBidToMotherDuck(itemNumber: String, paddleNumber: String, bidAmount: Double): Boolean {
        var connection: Connection? = null
        return try {
            connection = MotherDuckConnectionHelper.connect()
            connection?.use {
                val query = """
                    INSERT INTO Bids (ItemNumber, PaddleNumber, BidAmount)
                    VALUES (?, ?, ?)
                """.trimIndent()
                val preparedStatement = it.prepareStatement(query)
                preparedStatement.setString(1, itemNumber)
                preparedStatement.setString(2, paddleNumber)
                preparedStatement.setDouble(3, bidAmount)
                preparedStatement.executeUpdate() > 0
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            connection?.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
