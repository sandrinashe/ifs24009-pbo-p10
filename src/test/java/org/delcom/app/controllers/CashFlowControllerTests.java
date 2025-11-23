package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CashFlowControllerTests {
    @Test
    @DisplayName("Pengujian untuk controller CashFlow")
    void testCashFlowController() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId, "INCOME", 50000.0, "Uang saku", date);
        cashFlow.setId(cashFlowId);

        // Membuat mock ServiceRepository
        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);

        // Atur perilaku mock
        when(cashFlowService.createCashFlow(any(UUID.class), any(String.class), any(Double.class), 
                any(String.class), any(LocalDateTime.class))).thenReturn(cashFlow);

        // Membuat instance controller
        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        assert (cashFlowController != null);

        cashFlowController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Menguji method createCashFlow
        {
            cashFlowController.authContext.setAuthUser(authUser);
            
            // Data tidak valid - Type
            {
                // Type Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, null, 50000.0, "Deskripsi valid", date);
                var result1 = cashFlowController.createCashFlow(invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Type Kosong
                CashFlow invalidCashFlow2 = new CashFlow(userId, "", 50000.0, "Deskripsi valid", date);
                var result2 = cashFlowController.createCashFlow(invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));

                // Type Invalid (bukan INCOME atau EXPENSE)
                CashFlow invalidCashFlow3 = new CashFlow(userId, "INVALID", 50000.0, "Deskripsi valid", date);
                var result3 = cashFlowController.createCashFlow(invalidCashFlow3);
                assert (result3 != null);
                assert (result3.getStatusCode().is4xxClientError());
                assert (result3.getBody().getStatus().equals("fail"));
                
                // ✅ Type Valid = "EXPENSE" (untuk cover branch yang missed)
                CashFlow validTypeExpense = new CashFlow(userId, "EXPENSE", 30000.0, "Belanja bulanan", date);
                var result4 = cashFlowController.createCashFlow(validTypeExpense);
                assert (result4 != null);
                assert (result4.getBody().getStatus().equals("success"));
            }

            // Data tidak valid - Amount
            {
                // Amount Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, "INCOME", null, "Deskripsi valid", date);
                var result1 = cashFlowController.createCashFlow(invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Amount = 0
                CashFlow invalidCashFlow2 = new CashFlow(userId, "INCOME", 0.0, "Deskripsi valid", date);
                var result2 = cashFlowController.createCashFlow(invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));

                // Amount < 0
                CashFlow invalidCashFlow3 = new CashFlow(userId, "INCOME", -1000.0, "Deskripsi valid", date);
                var result3 = cashFlowController.createCashFlow(invalidCashFlow3);
                assert (result3 != null);
                assert (result3.getStatusCode().is4xxClientError());
                assert (result3.getBody().getStatus().equals("fail"));
            }

            // Data tidak valid - Description
            {
                // Description Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, "INCOME", 50000.0, null, date);
                var result1 = cashFlowController.createCashFlow(invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Description Kosong
                CashFlow invalidCashFlow2 = new CashFlow(userId, "INCOME", 50000.0, "", date);
                var result2 = cashFlowController.createCashFlow(invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));
            }

            // Data tidak valid - Date
            {
                // Date Null
                CashFlow invalidCashFlow = new CashFlow(userId, "INCOME", 50000.0, "Deskripsi valid", null);
                var result = cashFlowController.createCashFlow(invalidCashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Tidak terautentikasi untuk menambahkan cashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil menambahkan cashFlow
            {
                cashFlowController.authContext.setAuthUser(authUser);
                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getAllCashFlows
        {
            // Tidak terautentikasi untuk getAllCashFlows
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllCashFlows dengan search null
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<CashFlow> dummyResponse = List.of(cashFlow);
                when(cashFlowService.getAllCashFlows(any(UUID.class), any(String.class))).thenReturn(dummyResponse);
                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getCashFlowById
        {
            // Tidak terautentikasi untuk getCashFlowById
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji getCashFlowById dengan ID yang ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(cashFlow);
                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("cashflow").getId().equals(cashFlowId));
            }

            // Menguji getCashFlowById dengan ID yang tidak ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(null);
                var result = cashFlowController.getCashFlowById(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method updateCashFlow
        {
            cashFlowController.authContext.setAuthUser(authUser);
            
            // Data tidak valid - Type
            {
                // Type Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, null, 50000.0, "Deskripsi valid", date);
                var result1 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Type Kosong
                CashFlow invalidCashFlow2 = new CashFlow(userId, "", 50000.0, "Deskripsi valid", date);
                var result2 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));

                // Type Invalid
                CashFlow invalidCashFlow3 = new CashFlow(userId, "INVALID", 50000.0, "Deskripsi valid", date);
                var result3 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow3);
                assert (result3 != null);
                assert (result3.getStatusCode().is4xxClientError());
                assert (result3.getBody().getStatus().equals("fail"));
                
                // ✅ Type Valid = "INCOME" (untuk cover branch yang missed)
                CashFlow validTypeIncome = new CashFlow(userId, "INCOME", 100000.0, "Gaji", date);
                validTypeIncome.setId(cashFlowId);
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), 
                        any(Double.class), any(String.class), any(LocalDateTime.class)))
                        .thenReturn(validTypeIncome);
                var result4 = cashFlowController.updateCashFlow(cashFlowId, validTypeIncome);
                assert (result4 != null);
                assert (result4.getBody().getStatus().equals("success"));
            }

            // Data tidak valid - Amount
            {
                // Amount Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, "INCOME", null, "Deskripsi valid", date);
                var result1 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Amount = 0
                CashFlow invalidCashFlow2 = new CashFlow(userId, "INCOME", 0.0, "Deskripsi valid", date);
                var result2 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));
            }

            // Data tidak valid - Description
            {
                // Description Null
                CashFlow invalidCashFlow1 = new CashFlow(userId, "INCOME", 50000.0, null, date);
                var result1 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow1);
                assert (result1 != null);
                assert (result1.getStatusCode().is4xxClientError());
                assert (result1.getBody().getStatus().equals("fail"));

                // Description Kosong
                CashFlow invalidCashFlow2 = new CashFlow(userId, "INCOME", 50000.0, "", date);
                var result2 = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow2);
                assert (result2 != null);
                assert (result2.getStatusCode().is4xxClientError());
                assert (result2.getBody().getStatus().equals("fail"));
            }

            // Data tidak valid - Date
            {
                // Date Null
                CashFlow invalidCashFlow = new CashFlow(userId, "INCOME", 50000.0, "Deskripsi valid", null);
                var result = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Tidak terautentikasi untuk updateCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Memperbarui cashFlow dengan ID tidak ada
            {
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), 
                        any(Double.class), any(String.class), any(LocalDateTime.class)))
                        .thenReturn(null);
                CashFlow updatedCashFlow = new CashFlow(userId, "EXPENSE", 75000.0, "Bayar tagihan", date);
                updatedCashFlow.setId(nonexistentCashFlowId);

                var result = cashFlowController.updateCashFlow(nonexistentCashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Memperbarui cashFlow dengan ID ada
            {
                CashFlow updatedCashFlow = new CashFlow(userId, "EXPENSE", 75000.0, "Bayar tagihan", date);
                updatedCashFlow.setId(cashFlowId);
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), 
                        any(Double.class), any(String.class), any(LocalDateTime.class)))
                        .thenReturn(updatedCashFlow);

                var result = cashFlowController.updateCashFlow(cashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method deleteCashFlow
        {
            // Tidak terautentikasi untuk deleteCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji deleteCashFlow dengan ID yang tidak ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(false);
                var result = cashFlowController.deleteCashFlow(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji deleteCashFlow dengan ID yang ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(true);
                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }
    }
}