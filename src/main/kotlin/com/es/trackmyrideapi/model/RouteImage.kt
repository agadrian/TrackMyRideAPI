package com.es.trackmyrideapi.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(name = "route_image")
data class RouteImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val route: Route,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now()
)