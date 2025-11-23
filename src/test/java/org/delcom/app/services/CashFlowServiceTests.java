package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CashFlowServiceTests {
    @Test
    @DisplayName("Pengujian untuk service CashFlow")
    void testCashFlowService() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId, "INCOME", 50000.0, "Uang saku", date);
        cashFlow.setId(cashFlowId);

        // Membuat mock CashFlowRepository
        // Buat mock
        CashFlowRepository cashFlowRepository = Mockito.mock(CashFlowRepository.class);

        // Atur perilaku mock
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);
        when(cashFlowRepository.findByKeyword(userId, "saku")).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findAllByUserId(userId)).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, nonexistentCashFlowId)).thenReturn(java.util.Optional.empty());
        when(cashFlowRepository.existsById(cashFlowId)).thenReturn(true);
        when(cashFlowRepository.existsById(nonexistentCashFlowId)).thenReturn(false);
        doNothing().when(cashFlowRepository).deleteById(any(UUID.class));

        // Membuat instance service
        CashFlowService cashFlowService = new CashFlowService(cashFlowRepository);
        assert (cashFlowService != null);

        // Menguji create cashFlow
        {
            CashFlow createdCashFlow = cashFlowService.createCashFlow(userId, cashFlow.getType(), 
                    cashFlow.getAmount(), cashFlow.getDescription(), cashFlow.getDate());
            assert (createdCashFlow != null);
            assert (createdCashFlow.getId().equals(cashFlowId));
            assert (createdCashFlow.getType().equals(cashFlow.getType()));
            assert (createdCashFlow.getAmount().equals(cashFlow.getAmount()));
            assert (createdCashFlow.getDescription().equals(cashFlow.getDescription()));
            assert (createdCashFlow.getDate().equals(cashFlow.getDate()));
        }

        // Menguji getAllCashFlows
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId, null);
            assert (cashFlows.size() == 1);
        }

        // Menguji getAllCashFlows dengan pencarian
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId, "saku");
            assert (cashFlows.size() == 1);

            cashFlows = cashFlowService.getAllCashFlows(userId, "     ");
            assert (cashFlows.size() == 1);
        }

        // Menguji getCashFlowById
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, cashFlowId);
            assert (fetchedCashFlow != null);
            assert (fetchedCashFlow.getId().equals(cashFlowId));
            assert (fetchedCashFlow.getType().equals(cashFlow.getType()));
            assert (fetchedCashFlow.getAmount().equals(cashFlow.getAmount()));
            assert (fetchedCashFlow.getDescription().equals(cashFlow.getDescription()));
        }

        // Menguji getCashFlowById dengan ID yang tidak ada
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, nonexistentCashFlowId);
            assert (fetchedCashFlow == null);
        }

        // Menguji updateCashFlow
        {
            String updatedType = "EXPENSE";
            Double updatedAmount = 75000.0;
            String updatedDescription = "Bayar tagihan listrik";
            LocalDateTime updatedDate = LocalDateTime.now();

            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, cashFlowId, updatedType, 
                    updatedAmount, updatedDescription, updatedDate);
            assert (updatedCashFlow != null);
            assert (updatedCashFlow.getType().equals(updatedType));
            assert (updatedCashFlow.getAmount().equals(updatedAmount));
            assert (updatedCashFlow.getDescription().equals(updatedDescription));
            assert (updatedCashFlow.getDate().equals(updatedDate));
        }

        // Menguji update CashFlow dengan ID yang tidak ada
        {
            String updatedType = "EXPENSE";
            Double updatedAmount = 75000.0;
            String updatedDescription = "Bayar tagihan listrik";
            LocalDateTime updatedDate = LocalDateTime.now();

            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, nonexistentCashFlowId, 
                    updatedType, updatedAmount, updatedDescription, updatedDate);
            assert (updatedCashFlow == null);
        }

        // Menguji deleteCashFlow
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, cashFlowId);
            assert (deleted == true);
        }

        // Menguji deleteCashFlow dengan ID yang tidak ada
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, nonexistentCashFlowId);
            assert (deleted == false);
        }
    }
}