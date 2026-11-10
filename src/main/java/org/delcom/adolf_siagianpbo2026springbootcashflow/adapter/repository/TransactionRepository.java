package org.delcom.adolf_siagianpbo2026springbootcashflow.adapter.repository;

import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;

/**
 * Implementasi repository transaksi berbasis generic {@link InMemoryRepository}.
 * Data disimpan di memori dan hilang saat aplikasi di-restart.
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {
    /** Penghitung ID otomatis. */
    private int idCounter = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction save(Transaction transaction) {
        Transaction saved = super.save(transaction);
        idCounter = Math.max(idCounter, transaction.getId());
        return saved;
    }

    @Override
    public Transaction saveIncome(String description, BigDecimal amount) {
        return save(new Transaction(nextId(), description, amount, TransactionType.INCOME));
    }

    @Override
    public Transaction saveExpense(String description, BigDecimal amount) {
        return save(new Transaction(nextId(), description, amount, TransactionType.EXPENSE));
    }

    private int nextId() {
        return ++idCounter;
    }
}
