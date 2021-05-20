DROP TABLE IF EXISTS accounts,
    cards,
    account_cards,
    payments,
    transactions,
    users,
    logs,
    contracts;

/* Table of account */
CREATE TABLE `accounts` (
                            `id` 			INT PRIMARY KEY AUTO_INCREMENT,
                            `title` 		VARCHAR(255) DEFAULT '',
                            `number` 		VARCHAR(20) UNIQUE NOT NULL,
                            `currency`      VARCHAR(3) DEFAULT 'RUB',
                            `user_id` 	    INT NOT NULL,
                            `balance` 	    FLOAT DEFAULT 0.00
);


/* Cards table */
CREATE TABLE `cards` (
                         `id` 		   INT PRIMARY KEY AUTO_INCREMENT,
                         `account_id`  INT NOT NULL,
                         `type`        VARCHAR(12) NOT NULL,
                         `title` 	   VARCHAR(255) DEFAULT '',
                         `number` 	   VARCHAR(16)  UNIQUE  NOT NULL,
                         `currency`    VARCHAR(3)  DEFAULT 'RUB',
                         `limit`       FLOAT DEFAULT 100000,
                         `approved`    BOOLEAN DEFAULT false,
                         `active`      BOOLEAN DEFAULT true
);

/* payments */
CREATE TABLE transactions (
                            id 				 INT PRIMARY KEY AUTO_INCREMENT,
                            t_type 	         VARCHAR(15) NOT NULL,
                            account_from 	 VARCHAR(20) NOT NULL,
                            account_to 		 VARCHAR(20) NOT NULL,
                            amount			 FLOAT NOT NULL,
                            approved_by_id   INT NOT NULL DEFAULT 0,
                            status	         VARCHAR(20),
                            created_at       DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at		 DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
);



/* users */
CREATE TABLE `users` (
                         `id` 				 INT PRIMARY KEY AUTO_INCREMENT,
                         `login`			 VARCHAR(20) UNIQUE NOT NULL,
                         `password`		     VARCHAR(50) DEFAULT '123123',
                         `full_name` 	     VARCHAR(70) NOT NULL,
                         `phone`		     VARCHAR(20) UNIQUE NOT NULL,
                         `role`			     VARCHAR(20) NOT NULL,
);



/* logs */
CREATE TABLE `logs` (
                        `id` 				 INT PRIMARY KEY AUTO_INCREMENT,
                        `type`			     VARCHAR(20) NOT NULL,
                        `message`		 	 VARCHAR(5000) NOT NULL,
                        `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


/* contracts */
CREATE TABLE `contracts` (
                             `id` 				 INT PRIMARY KEY AUTO_INCREMENT,
                             `unique_id`         VARCHAR(40) NOT NULL,
                             `description`       VARCHAR(5000) DEFAULT NULL,
                             `user_id_from` 	 INT NOT NULL,
                             `user_id_to` 	 	 INT NOT NULL,
                             `account_from` 	 VARCHAR(20) NOT NULL,
                             `account_to` 	 	 VARCHAR(20) NOT NULL,
                             `c_status` 		 VARCHAR(20) NOT NULL,
                             `created_at`        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             `updated_at`		 TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




