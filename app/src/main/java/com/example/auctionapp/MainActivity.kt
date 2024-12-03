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
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

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
        val viewBidsButton = dialog.findViewById<Button>(R.id.viewBidsButton)

        // Submit bid logic
        submitButton.setOnClickListener {
            val paddleNumber = paddleNumberInput.text.toString()
            val bidAmount = bidAmountInput.text.toString()

            if (paddleNumber.isNotEmpty() && bidAmount.isNotEmpty()) {
                val success = dbHelper.insertBid(itemNumber, paddleNumber, bidAmount.toDouble())
                if (success) {
                    Toast.makeText(this, "Submit Successful!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // View existing bids
        viewBidsButton.setOnClickListener {
            val bids = dbHelper.getBidsForItem(itemNumber)
            if (bids.isNotEmpty()) {
                val message = bids.joinToString("\n") { "Paddle: ${it.first}, Bid: ${it.second}" }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No bids for this item", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_clear_bids -> {
                clearBids()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearBids() {
        qrCodeResult?.let {
            val success = dbHelper.deleteBidsForItem(it)
            if (success) {
                Toast.makeText(this, "All bids for $it cleared", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to clear bids", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
