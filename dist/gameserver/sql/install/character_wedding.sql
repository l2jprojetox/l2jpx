-- 
-- Table structure for table `character_wedding`
-- 
DROP TABLE IF EXISTS `character_wedding`;
CREATE TABLE `character_wedding` (
  `id` int(11) NOT NULL auto_increment,
  `player1Id` int(11) NOT NULL default '0',
  `player2Id` int(11) NOT NULL default '0',
  `married` varchar(5) default NULL,
  `affianceDate` bigint(20) default '0',
  `weddingDate` bigint(20) default '0',
  `coupleType` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ;
