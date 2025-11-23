package org.delcom.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashFlowService {
    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public CashFlow createCashFlow(UUID userId, String type, Double amount, String description, LocalDateTime date) {
        CashFlow cashFlow = new CashFlow(userId, type, amount, description, date);
        return cashFlowRepository.save(cashFlow);
    }

    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return cashFlowRepository.findByKeyword(userId, search);
        }
        return cashFlowRepository.findAllByUserId(userId);
    }

    public CashFlow getCashFlowById(UUID userId, UUID id) {
        return cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    @Transactional
    public CashFlow updateCashFlow(UUID userId, UUID id, String type, Double amount, String description, LocalDateTime date) {
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow != null) {
            cashFlow.setType(type);
            cashFlow.setAmount(amount);
            cashFlow.setDescription(description);
            cashFlow.setDate(date);
            return cashFlowRepository.save(cashFlow);
        }
        return null;
    }

    @Transactional
    public boolean deleteCashFlow(UUID userId, UUID id) {
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow == null) {
            return false;
        }

        cashFlowRepository.deleteById(id);
        return true;
    }
}