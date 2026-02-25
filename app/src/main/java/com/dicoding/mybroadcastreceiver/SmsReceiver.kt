package com.dicoding.mybroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

// BroadcastReceiver adalah komponen Android yang "mendengarkan" siaran (broadcast) sistem.
// Dengan meng-extend kelas ini, kita mendaftarkan diri sebagai pendengar event SMS masuk.
class SmsReceiver : BroadcastReceiver() {

    companion object {
        // TAG digunakan sebagai label pada Log agar mudah memfilter pesan debug
        // di Logcat berdasarkan nama kelas ini.
        private val TAG = SmsReceiver::class.java.simpleName
    }

    // onReceive() adalah metode wajib yang dipanggil oleh sistem Android secara otomatis
    // setiap kali ada broadcast yang cocok dengan intent-filter yang didaftarkan di Manifest.
    override fun onReceive(context: Context, intent: Intent) {
        // Memverifikasi bahwa broadcast yang diterima benar-benar SMS_RECEIVED,
        // bukan jenis broadcast lain yang mungkin terpicu secara tidak sengaja.
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            // Satu SMS panjang bisa dipecah menjadi beberapa bagian (multipart SMS),
            // sehingga getMessagesFromIntent() mengembalikan array untuk menangani semua bagiannya.
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (message in messages) {
                // originatingAddress adalah nomor pengirim SMS dalam format string mentah.
                val senderNum = message.originatingAddress
                // messageBody berisi teks isi pesan SMS.
                val body = message.messageBody

                // Mencetak info pengirim dan pesan ke Logcat untuk keperluan debugging,
                // tanpa mengganggu alur aplikasi.
                Log.d(TAG, "senderNum: $senderNum; message: $message")

                // Membuat Intent eksplisit untuk membuka SmsReceiverActivity
                // sebagai tampilan pop-up saat SMS diterima.
                val showSmsIntent = Intent(context, SmsReceiverActivity::class.java)
                // FLAG_ACTIVITY_NEW_TASK wajib digunakan karena kita menjalankan Activity
                // dari luar konteks Activity (yaitu dari BroadcastReceiver yang tidak punya back stack).
                showSmsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // Mengirimkan data nomor pengirim ke Activity tujuan melalui Intent extras,
                // agar Activity bisa menampilkan informasi yang relevan.
                showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_NO, senderNum)
                showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_MESSAGE, body)

                // Menjalankan Activity pop-up untuk menampilkan isi SMS kepada pengguna.
                context.startActivity(showSmsIntent)
            }
        }
    }
}