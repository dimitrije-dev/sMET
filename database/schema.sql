CREATE DATABASE IF NOT EXISTS iMetDatabase;
USE iMetDatabase;

CREATE TABLE IF NOT EXISTS `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    gitLink VARCHAR(255) NULL,
    instaLink VARCHAR(255) NULL,
    bio TEXT NULL
);

CREATE TABLE IF NOT EXISTS `post` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_content TEXT NOT NULL,
    user_id INT NOT NULL,
    post_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES `user` (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS followers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    follower_id INT NOT NULL,
    CONSTRAINT uq_user_follower UNIQUE (user_id, follower_id),
    CONSTRAINT fk_follower_user FOREIGN KEY (user_id) REFERENCES `user` (id) ON DELETE CASCADE,
    CONSTRAINT fk_followed_user FOREIGN KEY (follower_id) REFERENCES `user` (id) ON DELETE CASCADE
);
