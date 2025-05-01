package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "Projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectID", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @NotNull
    @Column(name = "RegistrationDate", nullable = false)
    private LocalDate registrationDate;


    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "EstimateCost", precision = 10, scale = 2)
    private BigDecimal estimateCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StatusID")
    private ProjectStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TypeID", nullable = false)
    private ProjectType projectType;

    @Column(name = "PaymentDeadline")
    private LocalDate paymentDeadline;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ClientID", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ManagerID")
    private Worker manager;

    @Size(max = 1024)
    @Column(name = "Description", length = 1024)
    private String description;

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