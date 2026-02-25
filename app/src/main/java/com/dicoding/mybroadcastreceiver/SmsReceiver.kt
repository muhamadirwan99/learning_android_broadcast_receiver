package com.dicoding.mybroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

// BroadcastReceiver adalah komponen Android yang "mendengarkan" event sistem atau app lain.
// Kelas ini khusus menangkap event ketika SMS masuk ke perangkat.
class SmsReceiver : BroadcastReceiver() {

    companion object {
        // TAG dipakai untuk menandai log agar mudah difilter di Logcat saat debugging.
        // Menggunakan simpleName kelas supaya tag selalu sinkron dengan nama kelas.
        private val TAG = SmsReceiver::class.java.simpleName
    }

    // onReceive() adalah entry point wajib BroadcastReceiver — sistem akan memanggil
    // fungsi ini secara otomatis setiap kali ada broadcast yang cocok dengan filter.
    override fun onReceive(context: Context, intent: Intent) {
        // Pengecekan action diperlukan karena satu receiver bisa menerima banyak jenis
        // broadcast. Kita hanya mau proses SMS, bukan event lain yang mungkin lewat.
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            // Satu SMS panjang bisa dipecah menjadi beberapa PDU (bagian pesan).
            // getMessagesFromIntent() merakit ulang semua bagian PDU dari intent menjadi
            // array SmsMessage yang siap dibaca.
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            // Loop per pesan karena satu intent bisa membawa lebih dari satu SmsMessage
            // (misalnya SMS panjang yang dipecah atau SMS masuk bersamaan).
            for (message in messages) {
                // originatingAddress adalah nomor pengirim — perlu disimpan agar bisa
                // ditampilkan ke pengguna sebagai info "dari siapa" SMS itu datang.
                val senderNum = message.originatingAddress
                // messageBody adalah isi teks SMS yang sebenarnya ingin ditampilkan.
                val body = message.messageBody

                // Log debug berguna untuk verifikasi data SMS saat pengembangan,
                // tanpa harus membuka UI terlebih dahulu.
                Log.d(TAG, "senderNum: $senderNum; message: $message")

                // Buat Intent untuk membuka SmsReceiverActivity yang akan menampilkan SMS.
                // Kita perlu explicit intent (menyebutkan kelas tujuan) agar hanya
                // activity milik app kita yang menerima data ini.
                val showSmsIntent = Intent(context, SmsReceiverActivity::class.java)
                // FLAG_ACTIVITY_NEW_TASK wajib diset karena kita memulai Activity dari luar
                // Activity (dari BroadcastReceiver yang tidak punya back stack sendiri).
                showSmsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // putExtra dipakai untuk "menitipkan" data (nomor & isi SMS) ke Activity
                // tujuan, karena Intent adalah satu-satunya cara aman untuk transfer data
                // antar komponen Android.
                showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_NO, senderNum)
                showSmsIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_MESSAGE, body)

                // Jalankan Activity dari context receiver. Karena receiver tidak memiliki
                // UI sendiri, kita "mendelegasikan" tampilan ke Activity khusus.
                context.startActivity(showSmsIntent)
            }
        }
    }
}