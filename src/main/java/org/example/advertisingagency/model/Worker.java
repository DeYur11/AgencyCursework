package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "Workers", schema = "dbo")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WorkerID", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "Surname", nullable = false, length = 100)
    private String surname;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Email", nullable = false, length = 150)
    private String email;

    @Size(max = 128)
    @Nationalized
    @Column(name = "PhoneNumber", length = 128)
    private String phoneNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PositionID", nullable = false)
    private Position position;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OfficeID", nullable = false)
    private Office office;

    @NotNull
    @Column(name = "IsReviewer", nullable = false)
    private Boolean isReviewer = false;

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