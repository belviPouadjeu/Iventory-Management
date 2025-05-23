package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //@CreatedDate
  @CreationTimestamp
  @Column(name = "creationDate", nullable = false, updatable = false)
  private LocalDateTime creationDate;
  //private Instant creationDate;

  //@LastModifiedDate
  @UpdateTimestamp
  @Column(name = "lastModifiedDate")
  private LocalDateTime lastModifiedDate;
  //private Instant lastModifiedDate;


  // ✅ Constructeur vide explicite (optionnel)
  public AbstractEntity() {
  }
}
