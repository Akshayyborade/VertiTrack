package com.vertitrack.service;

import com.vertitrack.model.Lift;
import com.vertitrack.repository.LiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LiftService {
    
    private final LiftRepository liftRepository;
    
    public Lift saveLift(Lift lift) {
        return liftRepository.save(lift);
    }
    
    public Optional<Lift> findById(Long id) {
        return liftRepository.findById(id);
    }
    
    public Optional<Lift> findByLiftNumber(String liftNumber) {
        return liftRepository.findByLiftNumber(liftNumber);
    }
    
    public List<Lift> findAllLifts() {
        return liftRepository.findAll();
    }
    
    public List<Lift> findAllActiveLifts() {
        return liftRepository.findAllActiveLifts();
    }
    
    public List<Lift> findByStatus(Lift.LiftStatus status) {
        return liftRepository.findByStatus(status);
    }
    
    public List<Lift> findByLocation(String location) {
        return liftRepository.findByLocation(location);
    }
    
    public List<Lift> findByBuilding(String building) {
        return liftRepository.findByBuilding(building);
    }
    
    public List<Lift> searchLifts(String keyword) {
        return liftRepository.searchLifts(keyword);
    }
    
    public void deleteLift(Long id) {
        liftRepository.deleteById(id);
    }
    
    // AMC Related Methods
    public List<Lift> findLiftsWithAmcExpiringInDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return liftRepository.findLiftsWithAmcExpiringBetween(today, futureDate);
    }
    
    public List<Lift> findLiftsWithAmcRenewalDue() {
        return liftRepository.findLiftsWithAmcRenewalDue(LocalDate.now());
    }
    
    public List<Lift> findLiftsWithExpiredAmc() {
        return liftRepository.findLiftsWithExpiredAmc(LocalDate.now());
    }
    
    // Quarterly Payment Methods
    public List<Lift> findLiftsWithQuarterlyPaymentDueInDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return liftRepository.findLiftsWithQuarterlyPaymentDue(today, futureDate);
    }
    
    // Statistics
    public long countActiveLifts() {
        return liftRepository.countByStatus(Lift.LiftStatus.ACTIVE);
    }
    
    public long countLiftsByStatus(Lift.LiftStatus status) {
        return liftRepository.countByStatus(status);
    }
    
    // Business Logic
    public boolean isAmcExpiringSoon(Lift lift, int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate amcEndDate = lift.getAmcEndDate();
        if (amcEndDate == null) return false;
        
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, amcEndDate);
        return daysUntilExpiry >= 0 && daysUntilExpiry <= daysThreshold;
    }
    
    public boolean isAmcExpired(Lift lift) {
        LocalDate today = LocalDate.now();
        return lift.getAmcEndDate() != null && lift.getAmcEndDate().isBefore(today);
    }
}
