package com.example.medipal.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun QrScanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)

    scanner.startScan()
        .addOnSuccessListener { barcode->
            Toast.makeText(context, barcode.rawValue, Toast.LENGTH_SHORT).show()
        }
        .addOnCanceledListener {
            Toast.makeText(context, "Qr Scanning Cancelled", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener{ e ->
            e.printStackTrace()
            Toast.makeText(context, "Qr Scanning Failed ${e.message}", Toast.LENGTH_SHORT).show()
        }
}