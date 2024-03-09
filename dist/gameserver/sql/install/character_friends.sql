-- ---------------------------- 
-- Table structure for character_friends
-- ---------------------------- 
DROP TABLE IF EXISTS `character_friends`;
CREATE TABLE `character_friends` (
  `char_id` INT NOT NULL default 0,
  `friend_id` INT(11) NOT NULL DEFAULT 0,
  `friend_name` VARCHAR(35) NOT NULL DEFAULT '',
  `not_blocked` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY  (`char_id`,`friend_name`) 
);
