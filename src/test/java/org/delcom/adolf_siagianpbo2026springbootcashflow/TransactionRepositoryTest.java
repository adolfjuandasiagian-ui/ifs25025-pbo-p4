package org.delcom.adolf_siagianpbo2026springbootcashflow;

import org.delcom.adolf_siagianpbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionRepositoryTest {

    private final TransactionRepository repository = new TransactionRepository();

    @Test
    void saveRejectsDuplicateId() {
        repository.saveIncome("Gaji", BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class,
                () -> repository.save(new Transaction(1, "Duplikat", BigDecimal.ONE, TransactionType.INCOME)));
    }

    @Test
    void updateReplacesExistingTransaction() {
        repository.saveIncome("Gaji", BigDecimal.TEN);
        Transaction replacement = new Transaction(
                1, "Gaji revisi", BigDecimal.valueOf(20), TransactionType.INCOME, LocalDateTime.now());

        repository.update(replacement);

        assertEquals("Gaji revisi", repository.findById(1).orElseThrow().getDescription());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(repository.findById(1).orElseThrow().getAmount()));
    }

    @Test
    void updateRejectsUnknownId() {
        Transaction unknown = new Transaction(5, "Belum tersimpan", BigDecimal.ONE, TransactionType.EXPENSE);

        assertThrows(IllegalArgumentException.class, () -> repository.update(unknown));
    }

    @Test
    void automaticIdsContinueAfterSavingAnExplicitId() {
        repository.save(new Transaction(4, "Transaksi awal", BigDecimal.ONE, TransactionType.INCOME));

        assertEquals(5, repository.saveExpense("Makan", BigDecimal.TEN).getId());
    }
}
