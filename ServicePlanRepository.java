package com.freelancehub.repo;
import com.freelancehub.model.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ServicePlanRepository extends JpaRepository<ServicePlan,Long>{
 List<ServicePlan> findByServiceRequestIdOrderByCreatedAtDesc(Long requestId);
}
