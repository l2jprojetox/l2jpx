-- ----------------------------
-- Table structure for olympiad_data
-- ----------------------------
DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE `olympiad_data` (
  `current_cycle` int(11) DEFAULT "1",
  `period` int(11) DEFAULT "0",
  `olympiad_end` bigint(20) DEFAULT "0",
  `olympiad_validation_end` bigint(20) DEFAULT "0",
  `next_weekly_change` bigint(20) DEFAULT "0"
) ;
