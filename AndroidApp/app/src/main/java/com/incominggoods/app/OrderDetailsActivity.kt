package com.incominggoods.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.incominggoods.app.api.ApiClient
import com.incominggoods.app.models.Order
import com.incominggoods.app.models.PhotoUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class OrderDetailsActivity : AppCompatActivity() {
    
    private lateinit var tvBarcode: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvSupplier: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvArrivalDate: TextView
    private lateinit var tvCompletionDate: TextView
    private lateinit var btnReportArrival: Button
    private lateinit var btnReportCompletion: Button
    private lateinit var btnCloseOrder: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var progressBar: ProgressBar
    
    private var orderId: Int = 0
    private var currentOrder: Order? = null
    
    companion object {
        private const val TAG = "OrderDetailsActivity"
        private const val CAMERA_REQUEST_CODE = 200
        private const val STORAGE_PERMISSION_CODE = 201
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Order Details"
        
        initViews()
        
        orderId = intent.getIntExtra("ORDER_ID", 0)
        val barcode = intent.getStringExtra("BARCODE") ?: ""
        
        if (orderId > 0) {
            loadOrderDetails()
        }
    }
    
    private fun initViews() {
        tvBarcode = findViewById(R.id.tvBarcode)
        tvDescription = findViewById(R.id.tvDescription)
        tvSupplier = findViewById(R.id.tvSupplier)
        tvOrderDate = findViewById(R.id.tvOrderDate)
        tvStatus = findViewById(R.id.tvStatus)
        tvArrivalDate = findViewById(R.id.tvArrivalDate)
        tvCompletionDate = findViewById(R.id.tvCompletionDate)
        btnReportArrival = findViewById(R.id.btnReportArrival)
        btnReportCompletion = findViewById(R.id.btnReportCompletion)
        btnCloseOrder = findViewById(R.id.btnCloseOrder)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        progressBar = findViewById(R.id.progressBar)
        
        btnReportArrival.setOnClickListener { reportArrival() }
        btnReportCompletion.setOnClickListener { reportCompletion() }
        btnCloseOrder.setOnClickListener { closeOrder() }
        btnTakePhoto.setOnClickListener { takePhoto() }
    }
    
    private fun loadOrderDetails() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getOrder(orderId)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        currentOrder = response.body()
                        displayOrder(currentOrder!!)
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Failed to load order details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "API error", e)
                }
            }
        }
    }
    
    private fun displayOrder(order: Order) {
        tvBarcode.text = "Barcode: ${order.barcode}"
        tvDescription.text = order.description
        tvSupplier.text = "Supplier: ${order.supplierName ?: "N/A"}"
        tvOrderDate.text = "Order Date: ${formatDate(order.orderDate)}"
        tvStatus.text = "Status: ${order.status}"
        
        if (order.arrivalDate != null) {
            tvArrivalDate.text = "Arrived: ${formatDate(order.arrivalDate)}"
            tvArrivalDate.visibility = View.VISIBLE
        } else {
            tvArrivalDate.visibility = View.GONE
        }
        
        if (order.completionDate != null) {
            tvCompletionDate.text = "Completed: ${formatDate(order.completionDate)}"
            tvCompletionDate.visibility = View.VISIBLE
        } else {
            tvCompletionDate.visibility = View.GONE
        }
        
        // Update button states
        val isClosed = order.isClosed
        btnReportArrival.isEnabled = !isClosed && order.arrivalDate == null
        btnReportCompletion.isEnabled = !isClosed && order.arrivalDate != null && order.completionDate == null
        btnCloseOrder.isEnabled = !isClosed
        btnTakePhoto.isEnabled = !isClosed
    }
    
    private fun reportArrival() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.reportArrival(orderId)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Arrival reported successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadOrderDetails()
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Failed to report arrival",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "API error", e)
                }
            }
        }
    }
    
    private fun reportCompletion() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.reportCompletion(orderId)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Completion reported successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadOrderDetails()
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Failed to report completion",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "API error", e)
                }
            }
        }
    }
    
    private fun closeOrder() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.closeOrder(orderId)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Order closed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadOrderDetails()
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Failed to close order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "API error", e)
                }
            }
        }
    }
    
    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                uploadPhoto(imageBitmap)
            }
        }
    }
    
    private fun uploadPhoto(bitmap: Bitmap) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                val base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                
                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                val photoUpload = PhotoUpload(fileName, base64String)
                
                val response = ApiClient.apiService.uploadPhoto(orderId, photoUpload)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Photo uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadOrderDetails()
                    } else {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Failed to upload photo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "API error", e)
                }
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun formatDate(dateString: String): String {
        return try {
            dateString.split("T")[0]
        } catch (e: Exception) {
            dateString
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
