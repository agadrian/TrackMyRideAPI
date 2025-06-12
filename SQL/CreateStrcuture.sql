-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-06-2025 a las 19:41:54
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `profile_images`;
DROP TABLE IF EXISTS `refresh_token`;
DROP TABLE IF EXISTS `route_image`;
DROP TABLE IF EXISTS `route_pins`;
DROP TABLE IF EXISTS `routes`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `vehicles`;

SET FOREIGN_KEY_CHECKS = 1;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `trackmyridedb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `profile_images`
--

DROP TABLE IF EXISTS `profile_images`;
CREATE TABLE `profile_images` (
  `id` bigint(20) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `uploaded_at` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_uid` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `routes`
--

DROP TABLE IF EXISTS `routes`;
CREATE TABLE `routes` (
  `id` bigint(20) NOT NULL,
  `avg_speed` double NOT NULL,
  `compressed_path` mediumtext DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `distance_km` double NOT NULL,
  `efficiency` double DEFAULT NULL,
  `end_point` varchar(255) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `fuel_consumed` double DEFAULT NULL,
  `max_speed` double NOT NULL,
  `moving_time_sec` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pace` double DEFAULT NULL,
  `start_point` varchar(255) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `vehicle_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `route_image`
--

DROP TABLE IF EXISTS `route_image`;
CREATE TABLE `route_image` (
  `id` bigint(20) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `route_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `route_pins`
--

DROP TABLE IF EXISTS `route_pins`;
CREATE TABLE `route_pins` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `route_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `uid` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_premium` bit(1) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE `vehicles` (
  `id` bigint(20) NOT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `efficiency` double DEFAULT NULL,
  `fuel_type` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tank_capacity` double DEFAULT NULL,
  `type` enum('BIKE','CAR','MOTORCYCLE') DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `profile_images`
--
ALTER TABLE `profile_images`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKeywhtcxxpr9vetbl05wig5flc` (`user_id`);

--
-- Indices de la tabla `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKejwgljrgxcapy8f0o07fy1y5c` (`user_uid`);

--
-- Indices de la tabla `routes`
--
ALTER TABLE `routes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKwj7ayv49yvobipy3wtvltxj9` (`user_id`),
  ADD KEY `FK8dskpd2ky1n4ca1g2xp5mfqpt` (`vehicle_id`);

--
-- Indices de la tabla `route_image`
--
ALTER TABLE `route_image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKhlas6hot20j4cds8y0hglluw7` (`route_id`);

--
-- Indices de la tabla `route_pins`
--
ALTER TABLE `route_pins`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1w1xr8vjumplpeg51mh24mke3` (`route_id`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`uid`);

--
-- Indices de la tabla `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKtqdmt4nfrq7sh3gthrx4xat6p` (`user_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `profile_images`
--
ALTER TABLE `profile_images`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `refresh_token`
--
ALTER TABLE `refresh_token`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `routes`
--
ALTER TABLE `routes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `route_image`
--
ALTER TABLE `route_image`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `route_pins`
--
ALTER TABLE `route_pins`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `profile_images`
--
ALTER TABLE `profile_images`
  ADD CONSTRAINT `FKipod71ngolgi6vreyy8w25f41` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`);

--
-- Filtros para la tabla `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD CONSTRAINT `FKejwgljrgxcapy8f0o07fy1y5c` FOREIGN KEY (`user_uid`) REFERENCES `users` (`uid`);

--
-- Filtros para la tabla `routes`
--
ALTER TABLE `routes`
  ADD CONSTRAINT `FK8dskpd2ky1n4ca1g2xp5mfqpt` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `FKwj7ayv49yvobipy3wtvltxj9` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `route_image`
--
ALTER TABLE `route_image`
  ADD CONSTRAINT `FKhlas6hot20j4cds8y0hglluw7` FOREIGN KEY (`route_id`) REFERENCES `routes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `route_pins`
--
ALTER TABLE `route_pins`
  ADD CONSTRAINT `FK1w1xr8vjumplpeg51mh24mke3` FOREIGN KEY (`route_id`) REFERENCES `routes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `FKtqdmt4nfrq7sh3gthrx4xat6p` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
