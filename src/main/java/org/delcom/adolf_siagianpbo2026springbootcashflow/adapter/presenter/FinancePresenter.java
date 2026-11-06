package org.delcom.adolf_siagianpbo2026springbootcashflow.adapter.presenter;

import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter yang memformat data menjadi teks siap tampil
 * (uang, tipe, tanggal, dan pesan notifikasi).
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat uang: 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    /** Label tipe transaksi. */
    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Label opsi pengurutan (aman terhadap nilai null). */
    public String formatSortOption(SortOption option) {
        if (option == null) {
            return "Urutkan...";
        }
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Satu baris transaksi: [id] keterangan | Rp x | [Tipe]. */
    public String format(Transaction transaction) {
        return String.format("[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatMoney(transaction.getAmount()),
                formatType(transaction.getType()));
    }

    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    private String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        if (transactions.isEmpty()) {
            return header + "\n" + emptyMessage;
        }
        return header + "\n" + transactions.stream()
                .map(this::format)
                .collect(Collectors.joining("\n"));
    }

    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, "Hasil Pencarian: \"" + keyword + "\"",
                "- Transaksi tidak ditemukan!");
    }

    public String showSortedTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    public String showAddSuccess(Transaction transaction) {
        String label = transaction.isIncome() ? "pemasukan" : "pengeluaran";
        return "Berhasil menambah " + label + ": " + format(transaction);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showInvalidAmount() {
        return "[!] Jumlah harus berupa angka yang valid!";
    }

    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    public String showInvalidChoice() {
        return "[!] Pilihan tidak dimengerti.";
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}