DROP TABLE IF EXISTS `clan_subpledges`;
CREATE TABLE `clan_subpledges` (
  clan_id INT NOT NULL default '0',
  sub_pledge_id INT NOT NULL default '0',
  name varchar(45),
  leader_name varchar(35),
  PRIMARY KEY  (`clan_id`,`sub_pledge_id`)
);
