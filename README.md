# MyBroadcastReceiver

Aplikasi Android sederhana untuk belajar konsep **BroadcastReceiver** — salah satu dari empat komponen utama Android.

---

## 🎯 Tujuan Proyek

Proyek ini mendemonstrasikan dua jenis BroadcastReceiver:

| Jenis | Cara Daftar | Kapan Aktif | Dipakai Untuk |
|---|---|---|---|
| **Static Receiver** | `AndroidManifest.xml` | Selalu, meski app tertutup | SMS masuk (`SmsReceiver`) |
| **Dynamic Receiver** | Kode (`registerReceiver`) | Hanya saat Activity hidup | Broadcast lokal download selesai |

---

## 🏗️ Struktur Komponen

```
app/
├── MainActivity.kt           → Layar utama: minta izin SMS & simulasi download
├── SmsReceiver.kt            → Static receiver: tangkap SMS masuk dari sistem
├── SmsReceiverActivity.kt    → Tampilkan isi SMS sebagai dialog
└── AndroidManifest.xml       → Deklarasi izin, receiver, dan activity
```

---

## 🔄 Alur Kerja

### Alur 1: Menerima SMS

```
Sistem Android
    │  (SMS masuk → broadcast SMS_RECEIVED)
    ▼
SmsReceiver.onReceive()
    │  ekstrak nomor & isi SMS dari PDU
    │  buat Intent + FLAG_ACTIVITY_NEW_TASK
    ▼
SmsReceiverActivity
    │  tampil sebagai Dialog di atas layar apapun
    │  pengguna klik "Close" → finish()
    ▼
Kembali ke layar sebelumnya
```

### Alur 2: Simulasi Download

```
Pengguna klik "Download File"
    │
    ▼
Handler.postDelayed (3 detik)
    │  simulasi proses download berjalan
    ▼
sendBroadcast(ACTION_DOWNLOAD_STATUS)
    │  broadcast dikirim hanya ke package sendiri
    ▼
downloadReceiver.onReceive()
    │
    ▼
Toast "Download Selesai"
```

---

## 🔑 Konsep Kunci

### 1. Mengapa `FLAG_ACTIVITY_NEW_TASK`?
BroadcastReceiver **tidak memiliki back stack**. Untuk memulai Activity dari receiver, Android mewajibkan flag ini agar Activity masuk ke task baru atau task yang sudah ada.

### 2. Mengapa `android:permission="BROADCAST_SMS"` di receiver?
Ini adalah **penjaga keamanan** — hanya sistem Android yang memiliki izin `BROADCAST_SMS`, sehingga tidak ada app lain yang bisa memalsukan broadcast SMS ke app kita.

### 3. Mengapa receiver dinamis di-`unregister` di `onDestroy()`?
Receiver dinamis **tidak dibersihkan otomatis** oleh sistem. Jika tidak di-unregister, objek receiver akan tetap ada di memori meski Activity sudah hancur → **memory leak**.

### 4. Mengapa `RECEIVER_NOT_EXPORTED` untuk broadcast download?
Broadcast lokal kustom (`ACTION_DOWNLOAD_STATUS`) hanya relevan di dalam app kita. Flag ini memastikan app lain tidak bisa mengirim atau menyadap broadcast tersebut.

### 5. Mengapa key Intent Extra didefinisikan di `SmsReceiverActivity`?
Karena Activity-lah yang **mengkonsumsi** data tersebut. Dengan mendefinisikan key di konsumen (bukan pengirim), kita memastikan sinkronisasi — pengirim tinggal mengacu ke konstanta yang sama.

### 6. Mengapa `getMessagesFromIntent()` mengembalikan array?
Satu SMS panjang dipecah menjadi beberapa **PDU (Protocol Data Unit)** di level jaringan. Fungsi ini merakit ulang semua bagian menjadi objek `SmsMessage` yang utuh.

---

## 🛡️ Izin yang Diperlukan

```xml
<uses-permission android:name="android.permission.RECEIVE_SMS" />
<uses-permission android:name="android.permission.READ_SMS" />
```

> ⚠️ `RECEIVE_SMS` adalah **dangerous permission** — pengguna harus menyetujuinya secara eksplisit saat runtime (Android 6.0+). Gunakan tombol "Check SMS Permission" di MainActivity untuk memintanya.

---

## ▶️ Cara Menjalankan

1. Clone / buka proyek di Android Studio.
2. Jalankan di emulator atau perangkat fisik.
3. Klik **"Check SMS Permission"** → izinkan akses SMS.
4. Untuk uji SMS: kirim SMS ke emulator via **Extended Controls → Phone**.
5. Untuk uji download broadcast: klik **"Download File"** dan tunggu 3 detik.

---

## 🧰 Tech Stack

- **Bahasa**: Kotlin
- **Min SDK**: sesuai `build.gradle.kts`
- **UI Binding**: View Binding
- **Theme**: Material 3 (DayNight)
- **Arsitektur**: Single Activity + BroadcastReceiver (komponen dasar Android)

