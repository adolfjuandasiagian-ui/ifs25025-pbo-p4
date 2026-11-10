package org.delcom.adolf_siagianpbo2026springbootcashflow;

import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.adolf_siagianpbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @Test
    void rejectsInvalidDomainState() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(0, "Valid", BigDecimal.ONE, TransactionType.INCOME));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1, "  ", BigDecimal.ONE, TransactionType.INCOME));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1, "Valid", BigDecimal.ZERO, TransactionType.INCOME));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1, "Valid", BigDecimal.ONE, null));
    }
}
