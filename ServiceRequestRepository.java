package com.freelancehub.repo;
import com.freelancehub.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Long>{
 List<ServiceRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
 List<ServiceRequest> findByServiceFreelancerIdOrderByCreatedAtDesc(Long freelancerId);
}
