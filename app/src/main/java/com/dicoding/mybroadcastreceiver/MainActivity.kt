package com.dicoding.mybroadcastreceiver

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.mybroadcastreceiver.databinding.ActivityMainBinding

// View.OnClickListener di-implement di sini agar logika klik terpusat di override fun onClick(),
// sehingga satu handler bisa mengelola banyak tombol tanpa nested lambda yang berantakan.
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() membuat tampilan meluas ke area sistem (status bar & nav bar)
        // untuk memberikan pengalaman visual yang lebih modern dan imersif.
        enableEdgeToEdge()
        // View Binding di-inflate agar semua view di layout dapat diakses secara type-safe,
        // menghindari risiko ClassCastException atau NullPointerException dari findViewById().
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Padding disesuaikan dengan tinggi system bars (status bar & navigation bar)
        // supaya konten tidak tertimpa elemen UI milik sistem setelah edge-to-edge aktif.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnPermission.setOnClickListener(this)
    }

    // registerForActivityResult() adalah cara modern (menggantikan onActivityResult yang deprecated)
    // untuk meminta izin runtime. Callback ini dipanggil sistem setelah pengguna merespons dialog izin.
    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan → BroadcastReceiver sudah bisa menerima SMS masuk.
            Toast.makeText(this, "Sms receiver permission diterima", Toast.LENGTH_SHORT).show()
        } else {
            // Izin ditolak → SmsReceiver tidak akan dipanggil sistem saat ada SMS masuk.
            Toast.makeText(this, "Sms receiver permission ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Saat tombol ditekan, sistem akan menampilkan dialog izin runtime kepada pengguna.
            // Tanpa RECEIVE_SMS, broadcast SMS_RECEIVED tidak akan diteruskan ke SmsReceiver kita.
            R.id.btn_permission -> requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }

    }
}