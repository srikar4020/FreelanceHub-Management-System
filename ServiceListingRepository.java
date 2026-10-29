package com.freelancehub.repo;
import com.freelancehub.model.ServiceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ServiceListingRepository extends JpaRepository<ServiceListing,Long>{
 List<ServiceListing> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);
}
