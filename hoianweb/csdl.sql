-- ---------------------------------
-- CREATE DATABASE
-- ---------------------------------
CREATE DATABASE IF NOT EXISTS `hoian_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hoian_db`;

-- ---------------------------------
-- TABLE: admin (was a_admin)
-- ---------------------------------
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------
-- TABLE: category (was the_loai)
-- ---------------------------------
CREATE TABLE `category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------
-- TABLE: location (was dia_diem)
-- ---------------------------------
CREATE TABLE `location` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `slug` VARCHAR(255) NOT NULL,
  `longitude` DECIMAL(11, 8) NOT NULL,
  `latitude` DECIMAL(10, 8) NOT NULL,
  `description` TEXT,
  `category_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug_UNIQUE` (`slug`),
  KEY `fk_location_category_idx` (`category_id`),
  CONSTRAINT `fk_location_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------
-- TABLE: image (was hinh_anh)
-- ---------------------------------
CREATE TABLE `image` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `image_url` VARCHAR(255) NOT NULL,
  `location_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_image_location_idx` (`location_id`),
  CONSTRAINT `fk_image_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------
-- INSERT SAMPLE DATA
-- ---------------------------------

-- 1. Admin Account (user: admin, pass: admin123)
INSERT INTO `admin` (`username`, `password`) VALUES
('admin', '$2a$12$85zi9qU0u5Hpal5YjQ/iX.dfUPqtGZPRTX47kK5n4K8Ve.ASZqPBu');

-- 2. Categories
INSERT INTO `category` (`name`) VALUES
('Historical Site'),
('Local Cuisine'),
('Cultural Venue');

-- 3. Locations
INSERT INTO `location` (`name`, `slug`, `longitude`, `latitude`, `description`, `category_id`) VALUES
('Japanese Covered Bridge', 'chua-cau', 108.337041, 15.877232, 'The Japanese Covered Bridge is a characteristic architectural icon of Hoi An. This bridge was built by Japanese merchants around the 17th century.', 1),
('Fukian Assembly Hall', 'hoi-quan-phuc-kien', 108.339247, 15.877864, 'The Fukian Assembly Hall is one of the largest and most beautiful assembly halls in Hoi An, built by the Fujian Chinese community to worship Thien Hau Holy Mother.', 3),
('Banh Mi Phuong', 'banh-mi-phuong', 108.339794, 15.880626, 'World-famous, Banh Mi Phuong was praised by the late chef Anthony Bourdain as "the best banh mi in the world". This is a must-visit stop for food lovers.', 2),
('Tan Ky Old House', 'nha-co-tan-ky', 108.337222, 15.876798, 'As the first old house recognized as a National Historical - Cultural Monument in Hoi An, Tan Ky bears a strong architectural style combining Vietnamese - Chinese - Japanese influences.', 1);

-- 4. Images
INSERT INTO `image` (`image_url`, `location_id`) VALUES
('/images/chua-cau-1.jpg', 1),
('/images/chua-cau-2.jpg', 1),
('/images/hoi-quan-phuc-kien-1.jpg', 2),
('/images/banh-mi-phuong-1.jpg', 3),
('/images/nha-co-tan-ky-1.jpg', 4),
('/images/nha-co-tan-ky-2.jpg', 4);