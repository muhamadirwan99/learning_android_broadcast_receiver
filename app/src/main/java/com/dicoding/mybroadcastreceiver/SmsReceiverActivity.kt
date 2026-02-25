package com.dicoding.mybroadcastreceiver

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.mybroadcastreceiver.databinding.ActivitySmsReceiverBinding

class SmsReceiverActivity : AppCompatActivity() {

    companion object {
        // Konstanta ini berfungsi sebagai "kunci" yang disepakati antara SmsReceiver (pengirim)
        // dan SmsReceiverActivity (penerima) agar pengambilan data dari Intent tidak salah kunci.
        const val EXTRA_SMS_NO = "extra_sms_no"
        const val EXTRA_SMS_MESSAGE = "extra_sms_MESSAGE"
    }

    private lateinit var binding: ActivitySmsReceiverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() membuat konten tampilan meluas hingga ke area sistem (status bar & navigation bar),
        // memberikan tampilan yang lebih imersif dan modern.
        enableEdgeToEdge()
        // View Binding di-inflate di sini agar kita bisa mengakses semua view di layout
        // secara type-safe tanpa perlu findViewById() yang rawan NullPointerException.
        binding = ActivitySmsReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // WindowInsetsListener dipasang agar konten tidak tertutup oleh status bar atau navigation bar
        // setelah enableEdgeToEdge() diterapkan — ini adalah penyesuaian padding yang wajib dilakukan.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengubah judul Activity di title bar agar pengguna tahu konteks tampilan ini.
        title = getString(R.string.incoming_message)

        // Menutup Activity ini (bukan keluar dari app) saat tombol Close ditekan,
        // karena Activity ini hanya bersifat pop-up informatif, bukan layar utama.
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Mengambil data yang dikirimkan oleh SmsReceiver melalui Intent extras.
        // Kunci yang digunakan harus sama persis dengan yang dipakai saat putExtra() dipanggil.
        val senderNo = intent.getStringExtra(EXTRA_SMS_NO)
        val senderMessage = intent.getStringExtra(EXTRA_SMS_MESSAGE)

        // Menampilkan nomor pengirim menggunakan string resource berformat (@string/from)
        // agar teks prefix ("Dari: ") bisa dilokalisasi ke berbagai bahasa.
        binding.tvFrom.text = getString(R.string.from, senderNo)
        // Menampilkan isi pesan SMS langsung tanpa format tambahan.
        binding.tvMessage.text = senderMessage
    }
}