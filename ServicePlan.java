package com.freelancehub.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
public class ServicePlan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private ServiceRequest serviceRequest;
 @Column(nullable=false,length=2000) private String details;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status=Status.PENDING;
 private String customerMessage;
 private LocalDateTime createdAt=LocalDateTime.now();
 public enum Status{PENDING,ACCEPTED,REJECTED}
 public ServicePlan(){}
 public Long getId(){return id;} public ServiceRequest getServiceRequest(){return serviceRequest;} public void setServiceRequest(ServiceRequest v){serviceRequest=v;}
 public String getDetails(){return details;} public void setDetails(String v){details=v;}
 public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
 public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
 public String getCustomerMessage(){return customerMessage;} public void setCustomerMessage(String v){customerMessage=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
