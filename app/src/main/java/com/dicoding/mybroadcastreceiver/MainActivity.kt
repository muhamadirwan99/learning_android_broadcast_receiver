package com.dicoding.mybroadcastreceiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.mybroadcastreceiver.databinding.ActivityMainBinding

// View.OnClickListener di-implement di sini agar logika klik terpusat di override fun onClick(),
// sehingga satu handler bisa mengelola banyak tombol tanpa nested lambda yang berantakan.
class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        // String unik yang menjadi "nama" broadcast kustom kita. Nilai ini digunakan
        // sebagai pengenal agar hanya receiver yang mendaftar dengan action ini
        // yang akan menerima broadcast download selesai.
        const val ACTION_DOWNLOAD_STATUS = "download_status"
    }

    private lateinit var binding: ActivityMainBinding

    // downloadReceiver dideklarasikan di level kelas (bukan lokal) supaya bisa
    // di-unregister di onDestroy(). Jika hanya dideklarasikan lokal di onCreate(),
    // referensinya akan hilang dan kita tidak bisa membersihkannya → memory leak.
    private lateinit var downloadReceiver: BroadcastReceiver

    // registerForActivityResult adalah cara modern (menggantikan onActivityResult)
    // untuk meminta izin runtime. Didaftarkan sebelum onCreate() agar siap dipakai
    // kapan saja tanpa risiko IllegalStateException.
    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Sms receiver permission diterima", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sms receiver permission ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() membuat konten app bisa melebar sampai ke area system bar
        // (status bar & navigation bar) untuk tampilan layar penuh yang modern.
        enableEdgeToEdge()
        // View Binding di-inflate agar semua view di layout dapat diakses secara type-safe,
        // menghindari risiko ClassCastException atau NullPointerException dari findViewById().
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Listener ini memastikan konten tidak tertutup oleh system bar (status/nav bar)
        // dengan menambahkan padding yang nilainya sama dengan tinggi system bar.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnPermission.setOnClickListener(this)
        binding.btnDownload.setOnClickListener(this)

        // BroadcastReceiver dinamis (didaftarkan lewat kode, bukan Manifest) digunakan
        // untuk broadcast lokal/kustom di dalam app. Ini lebih aman karena hanya aktif
        // selama Activity hidup, tidak terus berjalan di background.
        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }
        // IntentFilter mendefinisikan "topik" broadcast apa yang mau didengarkan.
        // Tanpa filter ini, receiver tidak tahu broadcast mana yang harus ditangkap.
        val downloadIntentFilter = IntentFilter(ACTION_DOWNLOAD_STATUS)
        // RECEIVER_NOT_EXPORTED memastikan broadcast ini hanya bisa dikirim dari
        // dalam app kita sendiri — mencegah app lain memicu receiver ini (keamanan).
        ContextCompat.registerReceiver(
            this,
            downloadReceiver,
            downloadIntentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // launch() akan memunculkan dialog izin sistem ke pengguna.
            // Kita tidak bisa langsung mengakses SMS tanpa izin RECEIVE_SMS ini.
            R.id.btn_permission -> requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            R.id.btn_download -> {
                // Handler + postDelayed dipakai untuk mensimulasikan proses download
                // yang butuh waktu (3 detik). Di app nyata, ini diganti dengan
                // callback dari network request atau WorkManager.
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        // Setelah "download selesai", kirim broadcast ke seluruh
                        // komponen yang mendaftar dengan action ini agar mereka tahu.
                        val notifyFinishIntent = Intent(ACTION_DOWNLOAD_STATUS)
                        // setPackage() memastikan broadcast hanya diterima oleh app kita
                        // sendiri — praktik keamanan penting agar app lain tidak bisa
                        // menyadap atau memalsukan broadcast ini.
                        notifyFinishIntent.setPackage(packageName)
                        sendBroadcast(notifyFinishIntent)
                    },
                    3000
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Wajib unregister receiver saat Activity dihancurkan untuk mencegah memory leak.
        // Receiver dinamis tidak otomatis dibersihkan oleh sistem, berbeda dengan
        // receiver yang didaftarkan di Manifest.
        unregisterReceiver(downloadReceiver)
    }
}