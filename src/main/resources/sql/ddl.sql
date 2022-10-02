-- ar_lending_qc.ACTIVATION_STATUS_HISTORY definition

CREATE TABLE `ACTIVATION_STATUS_HISTORY` (
                                             `activation_status_history_id` int(11) NOT NULL AUTO_INCREMENT,
                                             `ar_account_status_id` int(11) NOT NULL,
                                             `request_id_from_one_seal` varchar(100) DEFAULT NULL,
                                             `request_id_to_other` varchar(100) DEFAULT NULL,
                                             `request_action_history` tinyint(2) NOT NULL,
                                             `request_body` text,
                                             `request_response` text,
                                             `before_status` tinyint(4) DEFAULT NULL,
                                             `after_status` tinyint(4) DEFAULT NULL,
                                             PRIMARY KEY (`activation_status_history_id`),
                                             UNIQUE KEY `activation_status_history_unique_1` (`activation_status_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ar_lending_qc.AR_ACCOUNT_STATUS definition

CREATE TABLE `AR_ACCOUNT_STATUS` (
                                     `ar_account_status_id` int(11) NOT NULL AUTO_INCREMENT,
                                     `product_id` varchar(100) DEFAULT NULL,
                                     `flow_code` varchar(100) DEFAULT NULL,
                                     `distribution_channel_uid` varchar(100) NOT NULL,
                                     `session_id` varchar(100) NOT NULL,
                                     `loan_account_number` varchar(100) NOT NULL,
                                     `amount` decimal(19,2) NOT NULL,
                                     `activation_status` tinyint(4) NOT NULL,
                                     `activation_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `activation_updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `activation_expired_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `underwriter_uid` varchar(100) DEFAULT NULL,
                                     `decision` tinyint(2) DEFAULT NULL,
                                     `reason` varchar(300) DEFAULT NULL,
                                     `request_id` varchar(100) NOT NULL,
                                     PRIMARY KEY (`ar_account_status_id`),
                                     UNIQUE KEY `ar_account_status_unique_1` (`ar_account_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;