package com.proiectcolectiv.eventmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestName;
    private String guestEmail;

    @Column(unique = true)
    private String token; // pentru linkul unic

    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;

    private Integer numberOfGuests;

    private LocalDateTime sentAt = LocalDateTime.now();


}
