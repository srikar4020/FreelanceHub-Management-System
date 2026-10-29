package com.freelancehub.controller;
import com.freelancehub.model.*;
import com.freelancehub.repo.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController @RequestMapping("/api")
public class MarketplaceController{
 private final UserRepository users; private final CategoryRepository categories; private final ServiceListingRepository services;
 private final ServiceRequestRepository requests; private final ServicePlanRepository plans;
 public MarketplaceController(UserRepository u,CategoryRepository c,ServiceListingRepository s,ServiceRequestRepository r,ServicePlanRepository p){users=u;categories=c;services=s;requests=r;plans=p;}
 record CategoryInput(String name,String description){}
 record ServiceInput(String title,String description,BigDecimal minPrice,Long categoryId,Long freelancerId){}
 record RequestInput(Long serviceId,Long customerId,String requirements){}
 record PlanInput(Long freelancerId,String details,BigDecimal price){}
 record DecisionInput(Long customerId,String decision,String message){}
 private ResponseEntity<Map<String,String>> error(HttpStatus status,String msg){return ResponseEntity.status(status).body(Map.of("error",msg));}
 @GetMapping("/categories") public List<Category> categories(){return categories.findAll();}
 @PostMapping("/categories") public ResponseEntity<?> addCategory(@RequestBody CategoryInput in,@RequestParam Long adminId){
  Optional<User>a=users.findById(adminId);if(a.isEmpty()||a.get().getRole()!=User.Role.ADMIN)return error(HttpStatus.FORBIDDEN,"Admin access required");
  if(in.name()==null||in.name().isBlank())return error(HttpStatus.BAD_REQUEST,"Category name is required");
  Category c=new Category();c.setName(in.name().trim());c.setDescription(in.description()==null?"":in.description().trim());
  try{return ResponseEntity.status(201).body(categories.save(c));}catch(Exception e){return error(HttpStatus.CONFLICT,"Category already exists");}
 }
 @DeleteMapping("/categories/{id}") public ResponseEntity<?> deleteCategory(@PathVariable Long id,@RequestParam Long adminId){
  if(users.findById(adminId).filter(u->u.getRole()==User.Role.ADMIN).isEmpty())return error(HttpStatus.FORBIDDEN,"Admin access required");
  if(!categories.existsById(id))return ResponseEntity.notFound().build();
  if(services.findAll().stream().anyMatch(s->s.getCategory().getId().equals(id)))return error(HttpStatus.CONFLICT,"Category is used by a service");
  categories.deleteById(id);return ResponseEntity.noContent().build();
 }
 @GetMapping("/services") public List<ServiceListing> services(@RequestParam(required=false) String q){
  List<ServiceListing> all=services.findAll();if(q==null||q.isBlank())return all;
  String term=q.toLowerCase();return all.stream().filter(s->s.getTitle().toLowerCase().contains(term)||s.getDescription().toLowerCase().contains(term)||s.getCategory().getName().toLowerCase().contains(term)).toList();
 }
 @PostMapping("/services") public ResponseEntity<?> addService(@RequestBody ServiceInput in){
  if(in.title()==null||in.title().isBlank()||in.description()==null||in.description().isBlank()||in.minPrice()==null||in.minPrice().compareTo(BigDecimal.ZERO)<=0)return error(HttpStatus.BAD_REQUEST,"Enter title, description and a positive minimum price");
  Optional<User> f=users.findById(in.freelancerId());Optional<Category> c=categories.findById(in.categoryId());
  if(f.isEmpty()||f.get().getRole()!=User.Role.FREELANCER)return error(HttpStatus.FORBIDDEN,"Freelancer account required");
  if(c.isEmpty())return error(HttpStatus.BAD_REQUEST,"Choose an existing service category");
  ServiceListing s=new ServiceListing();s.setTitle(in.title().trim());s.setDescription(in.description().trim());s.setMinPrice(in.minPrice());s.setCategory(c.get());s.setFreelancer(f.get());
  return ResponseEntity.status(201).body(services.save(s));
 }
 @DeleteMapping("/services/{id}") public ResponseEntity<?> deleteService(@PathVariable Long id,@RequestParam Long userId){
  Optional<ServiceListing>s=services.findById(id);if(s.isEmpty())return ResponseEntity.notFound().build();
  Optional<User>u=users.findById(userId);if(u.isEmpty()||(u.get().getRole()!=User.Role.ADMIN&&!s.get().getFreelancer().getId().equals(userId)))return error(HttpStatus.FORBIDDEN,"Only the owner or admin can delete this service");
  services.delete(s.get());return ResponseEntity.noContent().build();
 }
 @GetMapping("/requests/mine") public List<ServiceRequest> mine(@RequestParam Long userId){
  Optional<User>u=users.findById(userId);if(u.isEmpty())return List.of();
  return u.get().getRole()==User.Role.FREELANCER?requests.findByServiceFreelancerIdOrderByCreatedAtDesc(userId):requests.findByCustomerIdOrderByCreatedAtDesc(userId);
 }
 @GetMapping("/requests") public ResponseEntity<?> allRequests(@RequestParam Long adminId){
  if(users.findById(adminId).filter(u->u.getRole()==User.Role.ADMIN).isEmpty())return error(HttpStatus.FORBIDDEN,"Admin access required");
  return ResponseEntity.ok(requests.findAll());
 }
 @PostMapping("/requests") public ResponseEntity<?> createRequest(@RequestBody RequestInput in){
  Optional<ServiceListing>s=services.findById(in.serviceId());Optional<User>c=users.findById(in.customerId());
  if(s.isEmpty()||c.isEmpty())return error(HttpStatus.BAD_REQUEST,"Service or customer not found");
  if(c.get().getRole()!=User.Role.CUSTOMER)return error(HttpStatus.FORBIDDEN,"Customer account required");
  if(in.requirements()==null||in.requirements().isBlank())return error(HttpStatus.BAD_REQUEST,"Please describe your requirements");
  ServiceRequest r=new ServiceRequest();r.setService(s.get());r.setCustomer(c.get());r.setRequirements(in.requirements().trim());
  return ResponseEntity.status(201).body(requests.save(r));
 }
 @GetMapping("/requests/{id}/plans") public ResponseEntity<?> getPlans(@PathVariable Long id,@RequestParam Long userId){
  Optional<ServiceRequest>r=requests.findById(id);Optional<User>u=users.findById(userId);
  if(r.isEmpty()||u.isEmpty())return ResponseEntity.notFound().build();
  boolean owner=r.get().getCustomer().getId().equals(userId)||r.get().getService().getFreelancer().getId().equals(userId)||u.get().getRole()==User.Role.ADMIN;
  if(!owner)return error(HttpStatus.FORBIDDEN,"You cannot view this request");
  return ResponseEntity.ok(plans.findByServiceRequestIdOrderByCreatedAtDesc(id));
 }
 @PostMapping("/requests/{id}/plans") public ResponseEntity<?> addPlan(@PathVariable Long id,@RequestBody PlanInput in){
  Optional<ServiceRequest>r=requests.findById(id);if(r.isEmpty())return ResponseEntity.notFound().build();
  if(!r.get().getService().getFreelancer().getId().equals(in.freelancerId()))return error(HttpStatus.FORBIDDEN,"Only this service's freelancer can send a plan");
  if(in.details()==null||in.details().isBlank()||in.price()==null||in.price().compareTo(BigDecimal.ZERO)<=0)return error(HttpStatus.BAD_REQUEST,"Plan details and a positive price are required");
  ServicePlan p=new ServicePlan();p.setServiceRequest(r.get());p.setDetails(in.details().trim());p.setPrice(in.price());p.setStatus(ServicePlan.Status.PENDING);
  r.get().setStatus(ServiceRequest.Status.PLAN_SENT);requests.save(r.get());
  return ResponseEntity.status(201).body(plans.save(p));
 }
 @PutMapping("/plans/{id}/decision") public ResponseEntity<?> decide(@PathVariable Long id,@RequestBody DecisionInput in){
  Optional<ServicePlan>p=plans.findById(id);if(p.isEmpty())return ResponseEntity.notFound().build();
  ServicePlan plan=p.get();ServiceRequest r=plan.getServiceRequest();
  if(!r.getCustomer().getId().equals(in.customerId()))return error(HttpStatus.FORBIDDEN,"Only the requesting customer can respond");
  if(!"ACCEPT".equalsIgnoreCase(in.decision())&&!"REJECT".equalsIgnoreCase(in.decision()))return error(HttpStatus.BAD_REQUEST,"Decision must be ACCEPT or REJECT");
  plan.setStatus("ACCEPT".equalsIgnoreCase(in.decision())?ServicePlan.Status.ACCEPTED:ServicePlan.Status.REJECTED);
  plan.setCustomerMessage(in.message()==null?"":in.message().trim());plans.save(plan);
  r.setStatus("ACCEPT".equalsIgnoreCase(in.decision())?ServiceRequest.Status.ACCEPTED:ServiceRequest.Status.REJECTED);r.setCustomerMessage(plan.getCustomerMessage());requests.save(r);
  return ResponseEntity.ok(plan);
 }
 @GetMapping("/admin/summary") public ResponseEntity<?> summary(@RequestParam Long adminId){
  if(users.findById(adminId).filter(u->u.getRole()==User.Role.ADMIN).isEmpty())return error(HttpStatus.FORBIDDEN,"Admin access required");
  return ResponseEntity.ok(Map.of("users",users.count(),"freelancers",users.findAll().stream().filter(u->u.getRole()==User.Role.FREELANCER).count(),"customers",users.findAll().stream().filter(u->u.getRole()==User.Role.CUSTOMER).count(),"categories",categories.count(),"services",services.count(),"requests",requests.count(),"plans",plans.count()));
 }
}
