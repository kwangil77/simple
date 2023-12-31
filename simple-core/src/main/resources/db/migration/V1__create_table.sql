USE `simple`;

CREATE TABLE IF NOT EXISTS `users` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL,
  creator VARCHAR(20) NOT NULL,
  createdDate DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  lastModifier VARCHAR(20) NOT NULL,
  lastModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);