package com.vertitrack.service;

import com.vertitrack.model.Lift;
import com.vertitrack.model.ServiceRecord;
import com.vertitrack.repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceRecordService {
    
    private final ServiceRecordRepository serviceRecordRepository;
    
    public ServiceRecord saveServiceRecord(ServiceRecord serviceRecord) {
        // Calculate total cost if not set
        if (serviceRecord.getTotalCost() == null) {
            double laborCost = serviceRecord.getLaborCost() != null ? serviceRecord.getLaborCost() : 0.0;
            double partsCost = serviceRecord.getPartsCost() != null ? serviceRecord.getPartsCost() : 0.0;
            serviceRecord.setTotalCost(laborCost + partsCost);
        }
        return serviceRecordRepository.save(serviceRecord);
    }
    
    public Optional<ServiceRecord> findById(Long id) {
        return serviceRecordRepository.findById(id);
    }
    
    public List<ServiceRecord> findAllServiceRecords() {
        return serviceRecordRepository.findAll();
    }
    
    public List<ServiceRecord> findByLift(Lift lift) {
        return serviceRecordRepository.findByLiftOrderByServiceDateDesc(lift);
    }
    
    public List<ServiceRecord> findByLiftId(Long liftId) {
        return serviceRecordRepository.findByLiftIdOrderByServiceDateDesc(liftId);
    }
    
    public List<ServiceRecord> findByServiceType(ServiceRecord.ServiceType serviceType) {
        return serviceRecordRepository.findByServiceType(serviceType);
    }
    
    public List<ServiceRecord> findByStatus(ServiceRecord.ServiceStatus status) {
        return serviceRecordRepository.findByStatus(status);
    }
    
    public List<ServiceRecord> findServiceRecordsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return serviceRecordRepository.findServiceRecordsBetweenDates(startDate, endDate);
    }
    
    public List<ServiceRecord> findByLiftIdAndDateRange(Long liftId, LocalDate startDate, LocalDate endDate) {
        return serviceRecordRepository.findByLiftIdAndServiceDateBetween(liftId, startDate, endDate);
    }
    
    // AMC Servicing Records for individual lift - Whole Year
    public List<ServiceRecord> findAmcServicingRecordsByLiftAndYear(Long liftId, int year) {
        return serviceRecordRepository.findAmcServicingRecordsByLiftAndYear(liftId, year);
    }
    
    // AMC Repairing Records for individual lift - Whole Year
    public List<ServiceRecord> findAmcRepairingRecordsByLiftAndYear(Long liftId, int year) {
        return serviceRecordRepository.findAmcRepairingRecordsByLiftAndYear(liftId, year);
    }
    
    // Get total service cost for a lift in a year
    public Double getTotalServiceCostByLiftAndYear(Long liftId, int year) {
        return serviceRecordRepository.getTotalServiceCostByLiftAndYear(liftId, year);
    }
    
    // Find upcoming service due
    public List<ServiceRecord> findUpcomingServiceDue(int daysAhead) {
        LocalDate futureDate = LocalDate.now().plusDays(daysAhead);
        return serviceRecordRepository.findUpcomingServiceDue(futureDate);
    }
    
    // Find overdue services
    public List<ServiceRecord> findOverdueServices() {
        return serviceRecordRepository.findOverdueServices(LocalDate.now());
    }
    
    // Get last service record for a lift
    public ServiceRecord getLastServiceRecordByLiftId(Long liftId) {
        return serviceRecordRepository.findLastServiceRecordByLiftId(liftId);
    }
    
    public List<ServiceRecord> searchServiceRecords(String keyword) {
        return serviceRecordRepository.searchServiceRecords(keyword);
    }
    
    public void deleteServiceRecord(Long id) {
        serviceRecordRepository.deleteById(id);
    }
    
    // Business Logic
    public boolean isServiceOverdue(ServiceRecord serviceRecord) {
        if (serviceRecord.getNextServiceDate() == null) return false;
        return serviceRecord.getNextServiceDate().isBefore(LocalDate.now());
    }
}
