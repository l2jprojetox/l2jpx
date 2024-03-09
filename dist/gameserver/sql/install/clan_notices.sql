-- ----------------------------
-- Table structure for clan_notices
-- ----------------------------
DROP TABLE IF EXISTS `clan_notices`;
CREATE TABLE `clan_notices` (
  `clan_id` int(11) NOT NULL,
  `notice` varchar(512) NOT NULL,
  `enabled` varchar(5) NOT NULL,
  PRIMARY KEY (`clan_id`)
) ;
