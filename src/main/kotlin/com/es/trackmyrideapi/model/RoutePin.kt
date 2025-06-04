package com.es.trackmyrideapi.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "route_pins")
data class RoutePin(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val latitude: Double,
    val longitude: Double,

    val title: String,
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val route: Route
)
