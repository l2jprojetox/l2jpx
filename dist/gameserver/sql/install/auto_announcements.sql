-- --------------------------------------
-- Table structure for auto_announcements
-- --------------------------------------
DROP TABLE IF EXISTS `auto_announcements`;
CREATE TABLE `auto_announcements` (
  `id` int(11) NOT NULL auto_increment,
  `announcement` varchar(255) NOT NULL,
  `delay` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ;

