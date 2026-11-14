package net.space.developer.meetingscheduler.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomJpaRepository extends JpaRepository<RoomJpaEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
}
