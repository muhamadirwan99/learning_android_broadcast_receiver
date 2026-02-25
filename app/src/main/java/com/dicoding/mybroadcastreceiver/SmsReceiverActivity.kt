package com.dicoding.mybroadcastreceiver

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.mybroadcastreceiver.databinding.ActivitySmsReceiverBinding

class SmsReceiverActivity : AppCompatActivity() {

    companion object {
        // Konstanta key untuk Intent extras — didefinisikan di sini (bukan di SmsReceiver)
        // karena Activity inilah yang "memiliki" data tersebut dan yang paling tahu
        // key apa yang dibutuhkan. SmsReceiver tinggal mengacu ke konstanta ini.
        const val EXTRA_SMS_NO = "extra_sms_no"
        const val EXTRA_SMS_MESSAGE = "extra_sms_MESSAGE"
    }

    private lateinit var binding: ActivitySmsReceiverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySmsReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Padding dinamis ini penting agar konten tidak tertimpa status bar atau
        // navigation bar, terutama di perangkat dengan notch atau gesture navigation.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengganti judul default Activity di title bar agar pengguna tahu konteks
        // Activity ini — menampilkan pesan masuk, bukan halaman utama app.
        title = getString(R.string.incoming_message)

        // finish() menutup Activity ini dan kembali ke layar sebelumnya (atau home
        // jika tidak ada back stack), memberi pengguna kendali untuk menutup notif SMS.
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Ambil data yang "dititipkan" SmsReceiver via Intent extras.
        // getStringExtra() aman dipakai karena kita sendiri yang mengirim data ini
        // dan sudah memastikan key-nya cocok dengan konstanta di atas.
        val senderNo = intent.getStringExtra(EXTRA_SMS_NO)
        val senderMessage = intent.getStringExtra(EXTRA_SMS_MESSAGE)

        // Format string dari resources dipakai agar teks bisa dilokalisasi ke bahasa lain
        // dengan mudah — cukup ubah strings.xml tanpa menyentuh kode Kotlin ini.
        binding.tvFrom.text = getString(R.string.from, senderNo)
        binding.tvMessage.text = senderMessage
    }
}