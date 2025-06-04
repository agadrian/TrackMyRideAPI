package com.es.trackmyrideapi.service

import com.es.trackmyrideapi.dto.RouteImageRequestDTO
import com.es.trackmyrideapi.exceptions.NotFoundException
import com.es.trackmyrideapi.model.RouteImage
import com.es.trackmyrideapi.repository.RouteImageRepository
import com.es.trackmyrideapi.repository.RouteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class RouteImageService {

    @Autowired
    internal lateinit var cloudinaryService: CloudinaryService

    @Autowired
    internal lateinit var routeRepository: RouteRepository

    @Autowired
    internal lateinit var routeImageRepository: RouteImageRepository

    @Autowired
    internal lateinit var authService: AuthService



    /**
     * Agrega una nueva imagen a la ruta especificada.
     * Valida que el usuario autenticado sea el propietario de la ruta o un administrador.
     *
     * @param routeId ID de la ruta a la que se agregará la imagen.
     * @param request DTO con la información de la imagen.
     * @param principal Información de autenticación del usuario actual.
     * @return La entidad RouteImage creada y guardada en la base de datos.
     * @throws NotFoundException Si la ruta no existe.
     * @throws ForbiddenException Si el usuario no tiene permisos.
     */
    fun addImageToRoute(
        routeId: Long,
        request: RouteImageRequestDTO,
        principal: Jwt
    ): RouteImage {
        val route = routeRepository.findById(routeId)
            .orElseThrow { NotFoundException("Route not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        val image = RouteImage(route = route, imageUrl = request.imageUrl)
        return  routeImageRepository.save(image)
    }


    /**
     * Obtiene la lista de imágenes asociadas a una ruta.
     * Valida que el usuario autenticado sea el propietario de la ruta o un administrador.
     *
     * @param routeId ID de la ruta.
     * @param principal Información de autenticación del usuario actual.
     * @return Lista de imágenes asociadas a la ruta.
     * @throws NotFoundException Si la ruta no existe.
     * @throws ForbiddenException Si el usuario no tiene permisos.
     */
    fun getImagesForRoute(
        routeId: Long,
        principal: Jwt
    ): List<RouteImage> {
        val route = routeRepository.findById(routeId)
            .orElseThrow { NotFoundException("Route not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        return routeImageRepository.findByRouteId(routeId)
    }


    /**
     * Elimina una imagen específica de una ruta.
     * Valida que el usuario autenticado sea el propietario de la ruta o un administrador.
     * Además elimina la imagen de Cloudinary.
     *
     * @param routeId ID de la ruta.
     * @param imageId ID de la imagen a eliminar.
     * @param principal Información de autenticación del usuario actual.
     * @throws NotFoundException Si la ruta o la imagen no existen.
     * @throws RuntimeException Si ocurre un error al eliminar la imagen de Cloudinary.
     * @throws ForbiddenException Si el usuario no tiene permisos.
     */
    fun deleteImage(
        routeId: Long,
        imageId: Long,
        principal: Jwt
    ) {
        val route = routeRepository.findById(routeId)
            .orElseThrow { NotFoundException("Route not found") }

        authService.checkUserIsSelfOrAdmin(principal, route.user.uid)

        val image = routeImageRepository.findByIdAndRouteId(imageId, routeId)
            ?: throw NotFoundException("Image not found for this route")

         // Eliminar de Cloudinary
        cloudinaryService.deleteFromCloudinary(image.imageUrl)

        // Si llega aqui esta ok, eliminar de la BD
        routeImageRepository.delete(image)
    }
}