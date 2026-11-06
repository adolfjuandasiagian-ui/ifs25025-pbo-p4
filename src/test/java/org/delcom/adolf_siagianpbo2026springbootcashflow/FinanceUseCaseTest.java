package org.delcom.adolf_siagianpbo2026springbootcashflow;

import org.delcom.adolf_siagianpbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.adolf_siagianpbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinanceUseCaseTest {

    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void balanceIsIncomeMinusExpense() throws Exception {
        useCase.addIncome("Gaji", new BigDecimal("5000000"));
        useCase.addExpense("Makan", new BigDecimal("25000"));
        assertEquals(0, new BigDecimal("4975000").compareTo(useCase.getBalance()));
    }

    @Test
    void invalidInputIsRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome(" ", BigDecimal.TEN));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", BigDecimal.ZERO));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", new BigDecimal("-5")));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", null));
    }

    @Test
    void removeUnknownIdThrows() {
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
    }

    @Test
    void searchAndSortWork() throws Exception {
        useCase.addIncome("Gaji Bulanan", new BigDecimal("100"));
        useCase.addExpense("gaji ibu", new BigDecimal("300"));
        useCase.addExpense("Kopi", new BigDecimal("50"));

        assertEquals(2, useCase.searchTransactions("GAJI").size());
        List<Transaction> sorted = useCase.sortTransactions(SortOption.AMOUNT_DESC);
        assertEquals("gaji ibu", sorted.get(0).getDescription());
        assertEquals("Gaji Bulanan", useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).getDescription());
        assertEquals(1, useCase.findTransactions("kopi", SortOption.AMOUNT_ASC).size());
        assertEquals(3, useCase.findTransactions("", null).size());
    }

    @Test
    void findAllReturnsDefensiveCopy() throws Exception {
        useCase.addIncome("A", BigDecimal.ONE);
        useCase.getAllTransactions().clear();
        assertEquals(1, useCase.getAllTransactions().size());
    }
}
