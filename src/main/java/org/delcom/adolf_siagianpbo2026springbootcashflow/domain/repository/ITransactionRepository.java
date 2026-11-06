package org.delcom.adolf_siagianpbo2026springbootcashflow.domain.repository;

import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;

/**
 * Port spesifik Transaction yang memperluas generic {@link IRepository}.
 * Menyediakan pembuatan transaksi baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /** Menyimpan transaksi pemasukan baru. */
    Transaction saveIncome(String description, BigDecimal amount);

    /** Menyimpan transaksi pengeluaran baru. */
    Transaction saveExpense(String description, BigDecimal amount);
}
