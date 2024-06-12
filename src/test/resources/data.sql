DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS topic;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    time_zone VARCHAR(255),
    gmt_off_set VARCHAR(255),
    confirmed INT DEFAULT 0,--confirmed BOOLEAN DEFAULT FALSE,
    confirmation_key VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ai_model ENUM('GPT3', 'GPT4') DEFAULT 'GPT3'
);

CREATE TABLE topic (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    everyday_at VARCHAR(255) NOT NULL,
    every_nth_day INT NOT NULL,
    starting_level VARCHAR(255) NOT NULL,
    minimum_number_items INT NOT NULL,
    ending_level VARCHAR(255) NOT NULL,
    maximum_number_items INT NOT NULL,
    language VARCHAR(255) NOT NULL,
    generated INT DEFAULT 0,--generated BOOLEAN DEFAULT FALSE
    version BIGINT,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR(255),
    item_order INT,
    topic_id INT NOT NULL,
    sent INT DEFAULT 0,--sent BOOLEAN DEFAULT FALSE,
    next_at TIMESTAMP,
    content TEXT,
    version BIGINT,
    FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);
