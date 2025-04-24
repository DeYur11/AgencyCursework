package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "ProjectServices")
public class ProjectService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectServicesID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServiceID")
    private Service serviceID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectID")
    private Project projectID;

    @Column(name = "Amount", columnDefinition = "tinyint")
    private Short amount;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

    @PrePersist
    protected void onCreate() {
        createDatetime = OffsetDateTime.now().toInstant();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDatetime = OffsetDateTime.now().toInstant();
    }
}