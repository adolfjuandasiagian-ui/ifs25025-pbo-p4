# adolf_siagian-pbo2026-springboot-cashflow

Aplikasi web pencatat keuangan pribadi (Cashflow) berbasis **Spring Boot**, **Maven**, dan **Vaadin**, dengan arsitektur **Clean Architecture**. Data disimpan **in-memory** sehingga reset saat aplikasi di-restart.

## Fitur

- Tambah pemasukan dan pengeluaran
- Cari transaksi berdasarkan keterangan
- Urutkan transaksi melalui empat `SortOption` domain: jumlah terkecil/terbesar dan pemasukan/pengeluaran terlebih dahulu
- Lihat saldo (total pemasukan − total pengeluaran)
- Hapus transaksi dengan konfirmasi
- Validasi keterangan dan jumlah
- Aksesibilitas: bahasa halaman Indonesia, landmark utama, status koneksi untuk pembaca layar, dan tata letak responsif

Aturan pengurutan berada di `domain/entity/SortOption`; Grid hanya menampilkan data dan tidak mendefinisikan comparator sendiri.

## Struktur Layer

```text
src/main/java/org/delcom/adolf_siagianpbo2026springbootcashflow/
├── domain/       # entity, exception, repository (port)
├── usecase/      # FinanceUseCase
├── adapter/      # InMemoryRepository, TransactionRepository, FinancePresenter
├── framework/    # FinanceView (Vaadin)
├── config/       # AppConfig (composition root)
├── DotEnv.java
└── App.java
```

## Menjalankan (Development)

```cmd
mvnw.cmd spring-boot:run
```

Buka http://localhost:8080 (port diatur lewat `APP_PORT` di file `.env`).

## Build JAR (Production)

```cmd
mvnw.cmd clean package -Pproduction
java -jar target\adolf_siagian-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
```

Flag `-Pproduction` wajib, jika tidak aplikasi gagal start dengan error `vaadin-dev-server not found`.

## Deploy ke Render

Deploy memakai `Dockerfile`. Build dijalankan dengan `-Pproduction`, dan port dibaca dari variabel `PORT` yang disediakan Render.