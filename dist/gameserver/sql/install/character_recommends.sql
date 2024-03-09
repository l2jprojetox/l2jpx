-- ---------------------------- 
-- Table structure for character_recommends 
-- ----------------------------
DROP TABLE IF EXISTS `character_recommends`;
CREATE TABLE `character_recommends` ( 
 char_id INT NOT NULL default 0, 
 target_id INT(11) NOT NULL DEFAULT 0, 
 PRIMARY KEY (char_id,target_id) 
); 
