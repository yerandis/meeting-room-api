package com.yerandis.meetingRoom.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120, unique = true)
    private String name;

    @Positive
    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Room() {}

    public Room(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getId()          { return id; }
    public String getName()      { return name; }
    public Integer getCapacity() { return capacity; }
    public Instant getCreatedAt(){ return createdAt; }
}
