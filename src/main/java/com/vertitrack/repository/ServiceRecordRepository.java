package com.vertitrack.repository;

import com.vertitrack.model.ServiceRecord;
import com.vertitrack.model.Lift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    
    // Find all service records for a specific lift
    List<ServiceRecord> findByLiftOrderByServiceDateDesc(Lift lift);
    
    // Find service records by lift ID
    List<ServiceRecord> findByLiftIdOrderByServiceDateDesc(Long liftId);
    
    // Find by service type
    List<ServiceRecord> findByServiceType(ServiceRecord.ServiceType serviceType);
    
    // Find by status
    List<ServiceRecord> findByStatus(ServiceRecord.ServiceStatus status);
    
    // Find service records between dates
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.serviceDate BETWEEN :startDate AND :endDate ORDER BY sr.serviceDate DESC")
    List<ServiceRecord> findServiceRecordsBetweenDates(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
    
    // Find service records for a lift between dates
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.lift.id = :liftId AND sr.serviceDate BETWEEN :startDate AND :endDate ORDER BY sr.serviceDate DESC")
    List<ServiceRecord> findByLiftIdAndServiceDateBetween(@Param("liftId") Long liftId, 
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
    
    // Find upcoming service due
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.nextServiceDate <= :date AND sr.status = 'COMPLETED' ORDER BY sr.nextServiceDate")
    List<ServiceRecord> findUpcomingServiceDue(@Param("date") LocalDate date);
    
    // Find overdue services
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.nextServiceDate < :currentDate AND sr.status = 'COMPLETED' ORDER BY sr.nextServiceDate")
    List<ServiceRecord> findOverdueServices(@Param("currentDate") LocalDate currentDate);
    
    // Find AMC servicing records for a lift (whole year)
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.lift.id = :liftId AND sr.serviceType = 'AMC_SERVICING' AND YEAR(sr.serviceDate) = :year ORDER BY sr.serviceDate")
    List<ServiceRecord> findAmcServicingRecordsByLiftAndYear(@Param("liftId") Long liftId, @Param("year") int year);
    
    // Find AMC repairing records for a lift (whole year)
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.lift.id = :liftId AND sr.serviceType = 'AMC_REPAIR' AND YEAR(sr.serviceDate) = :year ORDER BY sr.serviceDate")
    List<ServiceRecord> findAmcRepairingRecordsByLiftAndYear(@Param("liftId") Long liftId, @Param("year") int year);
    
    // Get total cost for a lift in a year
    @Query("SELECT COALESCE(SUM(sr.totalCost), 0) FROM ServiceRecord sr WHERE sr.lift.id = :liftId AND YEAR(sr.serviceDate) = :year")
    Double getTotalServiceCostByLiftAndYear(@Param("liftId") Long liftId, @Param("year") int year);
    
    // Get service count by type for a lift
    @Query("SELECT COUNT(sr) FROM ServiceRecord sr WHERE sr.lift.id = :liftId AND sr.serviceType = :serviceType")
    long countByLiftIdAndServiceType(@Param("liftId") Long liftId, @Param("serviceType") ServiceRecord.ServiceType serviceType);
    
    // Get last service record for a lift
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.lift.id = :liftId ORDER BY sr.serviceDate DESC LIMIT 1")
    ServiceRecord findLastServiceRecordByLiftId(@Param("liftId") Long liftId);
    
    // Search service records
    @Query("SELECT sr FROM ServiceRecord sr WHERE " +
           "LOWER(sr.performedBy) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sr.workDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sr.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ServiceRecord> searchServiceRecords(@Param("keyword") String keyword);
}
