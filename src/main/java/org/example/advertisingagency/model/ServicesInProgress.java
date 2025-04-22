package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class ServicesInProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ServiceInProgressID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StatusID")
    private ServiceInProgressStatus statusID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectServiceID")
    private ProjectService projectServiceID;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

}