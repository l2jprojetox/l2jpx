-- ---------------------------
-- Table structure for character_recipes
-- ---------------------------
DROP TABLE IF EXISTS `character_recipes`;
CREATE TABLE `character_recipes` (
  char_id int(11) NOT NULL default 0,
  id int(11) NOT NULL default 0,
  PRIMARY KEY  (id,char_id)
) ;