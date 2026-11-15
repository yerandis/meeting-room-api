package com.yerandis.meetingRoom.specification;

import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.Room;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ReservationSpecifications {

    private ReservationSpecifications() {}

    public static Specification<Reservation> filter(
            String search, UUID roomId, Instant startTime, Instant endTime) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Reservation, Room> room = root.join("room");

            if (search != null) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("responsible")), like),
                        cb.like(cb.lower(room.get("name")), like)));
            }
            if (roomId != null) {
                predicates.add(cb.equal(room.get("id"), roomId));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThan(root.get("endTime"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThan(root.get("startTime"), endTime));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
