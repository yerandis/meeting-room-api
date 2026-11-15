package com.yerandis.meetingRoom.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String responsible;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Reservation() {}

    public Reservation(Room room, String responsible, Instant startTime, Instant endTime) {
        this.room = room;
        this.responsible = responsible;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
        this.status = ReservationStatus.ACTIVE;
    }

    public UUID getId()                  { return id; }
    public Room getRoom()                { return room; }
    public String getResponsible()       { return responsible; }
    public Instant getStartTime()        { return startTime; }
    public Instant getEndTime()          { return endTime; }
    public ReservationStatus getStatus() { return status; }
    public Instant getCreatedAt()        { return createdAt; }

    public void setStatus(ReservationStatus status) { this.status = status; }
}
