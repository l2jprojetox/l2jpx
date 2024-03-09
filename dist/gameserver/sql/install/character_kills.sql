-- ----------------------------
-- Table structure for character_kills
-- ----------------------------
DROP TABLE IF EXISTS `character_kills`;
CREATE TABLE `character_kills` (
  `char_obj_id` int(11) NOT NULL,
  `char_killer_obj_id` int(11) NOT NULL,
  `kills` int(11) NOT NULL,
  PRIMARY KEY (`char_killer_obj_id`,`char_obj_id`)
) ;
