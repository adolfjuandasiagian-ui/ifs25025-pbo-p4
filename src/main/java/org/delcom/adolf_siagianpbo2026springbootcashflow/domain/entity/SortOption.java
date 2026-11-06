package org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi.
 * Setiap opsi membawa comparator-nya sendiri sehingga aturan pengurutan
 * terpusat di domain, bukan tersebar di view atau use case.
 */
public enum SortOption {
    /** Jumlah dari yang terkecil. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)),

    /** Jumlah dari yang terbesar. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparing((Transaction t) -> !t.isIncome())),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparing((Transaction t) -> !t.isExpense()));

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
