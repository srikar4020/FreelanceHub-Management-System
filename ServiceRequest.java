package com.freelancehub.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="service_requests")
public class ServiceRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private ServiceListing service;
 @ManyToOne(optional=false) private User customer;
 @Column(nullable=false,length=2000) private String requirements;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status=Status.REQUESTED;
 private String customerMessage;
 private LocalDateTime createdAt=LocalDateTime.now();
 public enum Status{REQUESTED,PLAN_SENT,ACCEPTED,REJECTED}
 public ServiceRequest(){}
 public Long getId(){return id;} public ServiceListing getService(){return service;} public void setService(ServiceListing v){service=v;}
 public User getCustomer(){return customer;} public void setCustomer(User v){customer=v;}
 public String getRequirements(){return requirements;} public void setRequirements(String v){requirements=v;}
 public Status getStatus(){return status;} public void setStatus(Status v){status=v;}
 public String getCustomerMessage(){return customerMessage;} public void setCustomerMessage(String v){customerMessage=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
