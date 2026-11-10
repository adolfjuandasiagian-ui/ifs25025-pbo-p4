package org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain — tidak bergantung pada layer lain.
 */
public class Transaction {
    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Keterangan transaksi. */
    private final String description;

    /** Jumlah uang; selalu bernilai positif, arah ditentukan oleh {@link #type}. */
    private final BigDecimal amount;

    /** Jenis transaksi: pemasukan atau pengeluaran. */
    private final TransactionType type;

    /** Waktu transaksi dicatat. */
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID transaksi harus lebih besar dari 0");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Keterangan tidak boleh kosong");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih besar dari 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("Jenis transaksi wajib diisi");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Waktu transaksi wajib diisi");
        }
        this.id = id;
        this.description = description.trim();
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    /** Dampak transaksi terhadap saldo: positif untuk pemasukan, negatif untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }
}
