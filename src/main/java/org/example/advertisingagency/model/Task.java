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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskID", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private Instant startDate;

    @Column(name = "EndDate")
    private Instant endDate;

    @Column(name = "Deadline")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ServiceInProgressID")
    private ServicesInProgress serviceInProgressID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedWorkerId")
    private Worker assignedWorker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskStatus")
    private TaskStatus taskStatus;

    @Column(name = "Priority", columnDefinition = "tinyint")
    private Short priority;

    @Column(name = "\"Value\"", precision = 10, scale = 2)
    private BigDecimal value;

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