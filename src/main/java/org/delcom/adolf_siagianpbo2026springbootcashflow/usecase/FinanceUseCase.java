package org.delcom.adolf_siagianpbo2026springbootcashflow.usecase;

import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception domain.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambah pemasukan setelah validasi. */
    public Transaction addIncome(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return transactionRepository.saveIncome(description.trim(), amount);
    }

    /** Menambah pengeluaran setelah validasi. */
    public Transaction addExpense(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return transactionRepository.saveExpense(description.trim(), amount);
    }

    /** Menghapus transaksi berdasarkan ID. */
    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID transaksi tidak valid");
        }
        if (!transactionRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi berdasarkan keterangan (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return transactionRepository.findAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return transactionRepository.findBy(
                t -> t.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword));
    }

    /** Mengurutkan semua transaksi sesuai {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option) {
        return sort(transactionRepository.findAll(), option);
    }

    /** Mencari lalu mengurutkan; keyword kosong = semua data, option null = tanpa urutan khusus. */
    public List<Transaction> findTransactions(String keyword, SortOption option) {
        return sort(searchTransactions(keyword), option);
    }

    /** Total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return sum(transactionRepository.findAll().stream().filter(Transaction::isIncome));
    }

    /** Total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return sum(transactionRepository.findAll().stream().filter(Transaction::isExpense));
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private List<Transaction> sort(List<Transaction> transactions, SortOption option) {
        if (option == null) {
            return transactions;
        }
        return transactions.stream().sorted(option.comparator()).toList();
    }

    private BigDecimal sum(Stream<Transaction> transactions) {
        return transactions.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validate(String description, BigDecimal amount) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (amount == null) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
    }
}
