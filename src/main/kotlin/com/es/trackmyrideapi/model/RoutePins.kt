package com.es.trackmyrideapi.model

import com.es.trackmyrideapi.dto.RoutePinResponseDTO
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "route_pins")
data class RoutePins(
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


fun RoutePins.toResponseDTO(): RoutePinResponseDTO {
    return RoutePinResponseDTO(
        id = this.id,
        latitude = this.latitude,
        longitude = this.longitude,
        title = this.title,
        description = this.description,
        routeId = this.route.id
    )
}