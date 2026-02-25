# MyBroadcastReceiver

Aplikasi Android yang mendemonstrasikan penggunaan **BroadcastReceiver** untuk mendeteksi dan menampilkan SMS masuk secara real-time sebagai dialog pop-up.

---

## Fitur

- Meminta izin runtime `RECEIVE_SMS` kepada pengguna
- Mendengarkan broadcast sistem `SMS_RECEIVED` secara otomatis
- Menampilkan nomor pengirim dan isi pesan SMS dalam dialog pop-up
- Menerapkan keamanan broadcast dengan `android:permission="BROADCAST_SMS"`

---

## Arsitektur & Alur Kerja

```
Sistem Android (SMS masuk)
        │
        ▼ broadcast: android.provider.Telephony.SMS_RECEIVED
  ┌─────────────┐
  │ SmsReceiver │  ← BroadcastReceiver (komponen pasif, selalu aktif di background)
  └──────┬──────┘
         │ startActivity() + Intent extras (nomor & pesan)
         ▼
┌──────────────────────┐
│ SmsReceiverActivity  │  ← Dialog pop-up yang menampilkan isi SMS
└──────────────────────┘

Pengguna membuka app
        │
        ▼
  ┌──────────────┐
  │ MainActivity │  ← Meminta izin RECEIVE_SMS via runtime permission
  └──────────────┘
```

---

## Struktur Proyek

```
app/src/main/
├── AndroidManifest.xml          # Deklarasi komponen, izin, dan keamanan broadcast
├── java/com/dicoding/mybroadcastreceiver/
│   ├── MainActivity.kt          # Layar utama; meminta izin RECEIVE_SMS
│   ├── SmsReceiver.kt           # BroadcastReceiver; menangkap SMS masuk dari sistem
│   └── SmsReceiverActivity.kt   # Dialog pop-up; menampilkan nomor & isi SMS
└── res/
    ├── layout/
    │   ├── activity_main.xml           # Layout layar utama (tombol izin)
    │   └── activity_sms_receiver.xml  # Layout dialog SMS (pengirim + pesan + tombol tutup)
    └── values/
        └── themes.xml           # Tema app & tema Dialog untuk SmsReceiverActivity
```

---

## Penjelasan Komponen Utama

### `SmsReceiver.kt` — BroadcastReceiver

| Konsep | Penjelasan |
|--------|-----------|
| `BroadcastReceiver` | Komponen Android yang "mendengarkan" event sistem tanpa perlu UI aktif |
| `onReceive()` | Dipanggil otomatis oleh sistem saat broadcast `SMS_RECEIVED` terpicu |
| `getMessagesFromIntent()` | Mengurai data SMS dari Intent; mengembalikan array karena SMS panjang bisa dipecah (multipart) |
| `FLAG_ACTIVITY_NEW_TASK` | Wajib diset saat menjalankan Activity dari luar konteks Activity (BroadcastReceiver tidak punya back stack) |

### `SmsReceiverActivity.kt` — Dialog Pop-up

| Konsep | Penjelasan |
|--------|-----------|
| `android:theme="Dialog"` | Activity ini tampil sebagai dialog, bukan layar penuh, karena hanya bersifat notifikasi sementara |
| `android:exported="false"` | Mencegah Activity ini dibuka oleh aplikasi lain; hanya boleh dipanggil dari internal app |
| `EXTRA_SMS_NO / EXTRA_SMS_MESSAGE` | Konstanta sebagai "kontrak kunci" antara pengirim (SmsReceiver) dan penerima data (Activity) |
| `finish()` | Menutup hanya Activity ini, bukan keluar dari seluruh app |

### `MainActivity.kt` — Layar Utama

| Konsep | Penjelasan |
|--------|-----------|
| `registerForActivityResult()` | API modern untuk meminta izin runtime; menggantikan `onActivityResult()` yang deprecated |
| `View.OnClickListener` | Di-implement di kelas agar satu fungsi `onClick()` mengelola semua tombol secara terpusat |
| `RECEIVE_SMS` | Izin runtime yang **wajib** diberikan pengguna agar broadcast SMS bisa diterima app |

### `AndroidManifest.xml` — Konfigurasi & Keamanan

| Atribut | Penjelasan |
|---------|-----------|
| `android:permission="BROADCAST_SMS"` | Memproteksi SmsReceiver agar hanya sistem (bukan app jahat) yang bisa mengirim broadcast ke dalamnya |
| `uses-feature android:required="false"` | Agar app tetap bisa diinstal di perangkat tanpa fitur telepon (misal: tablet WiFi-only) |
| `READ_SMS` | Diperlukan jika membaca SMS dari Content Provider (kotak masuk), bukan hanya dari broadcast |

---

## Izin yang Diperlukan

| Izin | Tipe | Kegunaan |
|------|------|----------|
| `RECEIVE_SMS` | Dangerous (runtime) | Menerima broadcast saat ada SMS masuk |
| `READ_SMS` | Dangerous (runtime) | Membaca isi kotak masuk SMS |

> **Catatan:** Kedua izin ini termasuk kategori **dangerous permission** di Android, sehingga harus diminta secara eksplisit kepada pengguna saat runtime (bukan hanya deklarasi di Manifest).

---

## Cara Menjalankan

1. Clone atau buka proyek ini di Android Studio
2. Hubungkan perangkat fisik atau jalankan emulator yang mendukung SMS
3. Build & jalankan aplikasi
4. Tekan tombol **"Check SMS Permission"** dan berikan izin yang diminta
5. Kirim SMS ke nomor perangkat → dialog pop-up akan muncul otomatis

> **Tips:** Gunakan **Extended Controls** di emulator Android Studio (ikon titik tiga → Phone) untuk mengirim SMS simulasi ke emulator tanpa nomor fisik.

---

## Konsep Android yang Dipelajari

- ✅ **BroadcastReceiver** — mendengarkan event sistem secara pasif
- ✅ **Runtime Permission** — meminta izin sensitif saat aplikasi berjalan
- ✅ **Intent & Intent Extras** — komunikasi antar komponen dengan data tambahan
- ✅ **Activity sebagai Dialog** — menampilkan Activity dengan tema dialog
- ✅ **View Binding** — akses view secara type-safe tanpa `findViewById()`
- ✅ **Edge-to-Edge UI** — tampilan modern yang meluas ke area status bar & navigation bar
- ✅ **Keamanan Broadcast** — menggunakan `android:permission` untuk memproteksi receiver

---

## Referensi

- [Android Developers — BroadcastReceiver](https://developer.android.com/guide/components/broadcasts)
- [Android Developers — Request App Permissions](https://developer.android.com/training/permissions/requesting)
- [Android Developers — Telephony.Sms.Intents](https://developer.android.com/reference/android/provider/Telephony.Sms.Intents)

