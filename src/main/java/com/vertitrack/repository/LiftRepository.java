package com.vertitrack.repository;

import com.vertitrack.model.Lift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LiftRepository extends JpaRepository<Lift, Long> {
    
    Optional<Lift> findByLiftNumber(String liftNumber);
    
    List<Lift> findByLocation(String location);
    
    List<Lift> findByStatus(Lift.LiftStatus status);
    
    List<Lift> findByBuilding(String building);
    
    // Find lifts with AMC expiring within days
    @Query("SELECT l FROM Lift l WHERE l.amcEndDate BETWEEN :startDate AND :endDate AND l.status = 'ACTIVE'")
    List<Lift> findLiftsWithAmcExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    // Find lifts with AMC renewal due
    @Query("SELECT l FROM Lift l WHERE l.amcRenewalDate <= :date AND l.status = 'ACTIVE'")
    List<Lift> findLiftsWithAmcRenewalDue(@Param("date") LocalDate date);
    
    // Find lifts with quarterly payment due
    @Query("SELECT l FROM Lift l WHERE " +
           "(l.quarter1PaymentDate BETWEEN :startDate AND :endDate) OR " +
           "(l.quarter2PaymentDate BETWEEN :startDate AND :endDate) OR " +
           "(l.quarter3PaymentDate BETWEEN :startDate AND :endDate) OR " +
           "(l.quarter4PaymentDate BETWEEN :startDate AND :endDate)")
    List<Lift> findLiftsWithQuarterlyPaymentDue(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
    
    // Find all active lifts
    @Query("SELECT l FROM Lift l WHERE l.status = 'ACTIVE' ORDER BY l.liftNumber")
    List<Lift> findAllActiveLifts();
    
    // Count lifts by status
    long countByStatus(Lift.LiftStatus status);
    
    // Search lifts by multiple criteria
    @Query("SELECT l FROM Lift l WHERE " +
           "LOWER(l.liftNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.building) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.contractorName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Lift> searchLifts(@Param("keyword") String keyword);
    
    // Find lifts by contractor
    List<Lift> findByContractorName(String contractorName);
    
    // Find expired AMC lifts
    @Query("SELECT l FROM Lift l WHERE l.amcEndDate < :currentDate AND l.status = 'ACTIVE'")
    List<Lift> findLiftsWithExpiredAmc(@Param("currentDate") LocalDate currentDate);
}
