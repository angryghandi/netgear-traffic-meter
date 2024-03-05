# netgear-traffic-meter

'''
CREATE TABLE `traffic_measure` (
`id` int unsigned NOT NULL AUTO_INCREMENT,
`type_id` int unsigned NOT NULL,
`ts` datetime NOT NULL,
`upload` double NOT NULL,
`upload_average` double DEFAULT NULL,
`download` double NOT NULL,
`download_average` double DEFAULT NULL,
`total` double NOT NULL,
`total_average` double DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `fk_type_id` (`type_id`),
CONSTRAINT `fk_type_id` FOREIGN KEY (`type_id`) REFERENCES `traffic_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
'''

CREATE TABLE `traffic_type` (
`id` int unsigned NOT NULL AUTO_INCREMENT,
`name` varchar(16) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `traffic_type` VALUES
    (1,'TODAY'),
    (2,'YESTERDAY')
    (5,'LAST_MONTH'),
    (4,'THIS_MONTH'),
    (3,'THIS_WEEK');