package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "WorkerAccounts")
@AllArgsConstructor
@NoArgsConstructor
public class WorkerAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "WorkerID", nullable = false)
    private Worker worker;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "Username", nullable = false, length = 100)
    private String username;

    @Size(max = 256)
    @NotNull
    @Nationalized
    @Column(name = "PasswordHash", nullable = false, length = 256)
    private String passwordHash;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

    public WorkerAccount(Worker worker, String username, String hash) {
        this.username = username;
        this.passwordHash = hash;
        this.worker = worker;
    }

    @PrePersist
    protected void onCreate() {
        createDatetime = OffsetDateTime.now().toInstant();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDatetime = OffsetDateTime.now().toInstant();
    }
}