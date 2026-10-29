package com.freelancehub.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
public class ServiceListing {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(nullable=false,length=2000) private String description;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal minPrice;
 @ManyToOne(optional=false) private Category category;
 @ManyToOne(optional=false) private User freelancer;
 private LocalDateTime createdAt=LocalDateTime.now();
 public ServiceListing(){}
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getMinPrice(){return minPrice;} public void setMinPrice(BigDecimal v){minPrice=v;}
 public Category getCategory(){return category;} public void setCategory(Category v){category=v;}
 public User getFreelancer(){return freelancer;} public void setFreelancer(User v){freelancer=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
