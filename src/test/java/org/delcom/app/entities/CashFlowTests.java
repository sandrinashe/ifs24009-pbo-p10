package org.delcom.app.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CashFlowTests {
    @Test
    @DisplayName("Membuat instance dari kelas CashFlow")
    void testMembuatInstanceCashFlow() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        // CashFlow tipe INCOME
        {
            CashFlow cashFlow = new CashFlow(userId, "INCOME", 50000.0, "Uang saku", date);

            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("INCOME"));
            assert (cashFlow.getAmount().equals(50000.0));
            assert (cashFlow.getDescription().equals("Uang saku"));
            assert (cashFlow.getDate().equals(date));
        }

        // CashFlow tipe EXPENSE
        {
            CashFlow cashFlow = new CashFlow(userId, "EXPENSE", 25000.0, "Belanja", date);

            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("EXPENSE"));
            assert (cashFlow.getAmount().equals(25000.0));
            assert (cashFlow.getDescription().equals("Belanja"));
            assert (cashFlow.getDate().equals(date));
        }

        // CashFlow dengan nilai default
        {
            CashFlow cashFlow = new CashFlow();

            assert (cashFlow.getId() == null);
            assert (cashFlow.getUserId() == null);
            assert (cashFlow.getType() == null);
            assert (cashFlow.getAmount() == null);
            assert (cashFlow.getDescription() == null);
            assert (cashFlow.getDate() == null);
        }

        // CashFlow dengan setNilai
        {
            CashFlow cashFlow = new CashFlow();
            UUID generatedId = UUID.randomUUID();
            LocalDateTime setDate = LocalDateTime.now();
            
            cashFlow.setId(generatedId);
            cashFlow.setUserId(userId);
            cashFlow.setType("INCOME");
            cashFlow.setAmount(100000.0);
            cashFlow.setDescription("Hadiah");
            cashFlow.setDate(setDate);
            cashFlow.onCreate();
            cashFlow.onUpdate();

            assert (cashFlow.getId().equals(generatedId));
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("INCOME"));
            assert (cashFlow.getAmount().equals(100000.0));
            assert (cashFlow.getDescription().equals("Hadiah"));
            assert (cashFlow.getDate().equals(setDate));
            assert (cashFlow.getCreatedAt() != null);
            assert (cashFlow.getUpdatedAt() != null);
        }
    }
}