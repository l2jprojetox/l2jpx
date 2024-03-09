package net.l2jpx.gameserver.network;

/**
 * @author L2JFrozen dev
 * @author ReynalDev
 */
public enum SystemMessageId
{
	/**
	 * ID: 0<br>
	 * Message: You have been disconnected from the server.
	 */
	YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER(0),

	/**
	 * ID: 1<br>
	 * Message: The server will be coming down in $s1 second(s). Please find a safe place to log out.
	 */
	THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS(1),
	
	/**
	 * ID: 2<br>
	 * Message: $s1 does not exist.
	 */
	S1_DOES_NOT_EXIST(2),
	
	/**
	 * ID: 3<br>
	 * Message: $s1 is not currently logged in.
	 */
	S1_IS_NOT_CURRENTLY_LOGGED_IN(3),
	
	/**
	 * ID: 4<br>
	 * Message: You cannot ask yourself to apply to a clan.
	 */
	YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN(4),
	
	/**
	 * ID: 5<br>
	 * Message: $s1 already exists.
	 */
	S1_ALREADY_EXISTS(5),
	
	/**
	 * ID: 6<br>
	 * Message: $s1 does not exist.
	 */
	S1_DOES_NOT_EXIST_2(6),
	
	/**
	 * ID: 7<br>
	 * Message: You are already a member of $s1.
	 */
	YOU_ARE_ALREADY_A_MEMBER_OF_S1(7),
	
	/**
	 * ID: 8<br>
	 * Message: You are working with another clan.
	 */
	YOU_ARE_WORKING_WITH_ANOTHER_CLAN(8),
	
	/**
	 * ID: 9<br>
	 * Message: $s1 is not a clan leader.
	 */
	S1_IS_NOT_A_CLAN_LEADER(9),
	
	/**
	 * ID: 10<br>
	 * Message: $s1 is working with another clan.
	 */
	S1_WORKING_WITH_ANOTHER_CLAN(10),
	
	/**
	 * ID: 11<br>
	 * Message: There are no applicants for this clan.
	 */
	THERE_ARE_NO_APPLICANTS_FOR_THIS_CLAN(11),
	
	/**
	 * ID: 12<br>
	 * Message: Applicant information is incorrect.
	 */
	APPLICANT_INFORMATION_IS_INCORRECT(12),
	
	/**
	 * ID: 13<br>
	 * Message: Unable to disperse: your clan has requested to participate in a castle siege.
	 */
	CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE(13),
	
	/**
	 * ID: 14<br>
	 * Message: Unable to disperse: your clan owns one or more castles or hideouts.
	 */
	UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS(14),
	
	/**
	 * ID: 15<br>
	 * Message: You are in siege.
	 */
	YOU_ARE_IN_SIEGE(15),

	/**
	 * ID: 16<br>
	 * Message: You are not in siege.
	 */
	YOU_ARE_NOT_IN_SIEGE(16),

	/**
	 * ID: 17<br>
	 * Message: The castle siege has begun.
	 */
	THE_CASTLE_SIEGE_HAS_BEGUN(17),
	
	/**
	 * ID: 18<br>
	 * Message: The castle siege has ended.
	 */
	THE_CASTLE_SIEGE_HAS_ENDED(18),
	
	/**
	 * ID: 19<br>
	 * Message: There is a new Lord of the castle!
	 */
	THERE_IS_A_NEW_LORD_OF_THE_CASTLE(19),
	
	/**
	 * ID: 20<br>
	 * Message: The gate is being opened.
	 */
	THE_GATE_IS_BEING_OPENED(20),
	
	/**
	 * ID: 21<br>
	 * Message: The gate is being destroyed.
	 */
	THE_GATE_IS_BEING_DESTROYED(21),
	
	/**
	 * ID: 22<br>
	 * Message: Your target is out of range.
	 */
	TARGET_TOO_FAR(22),
	
	/**
	 * ID: 23<br>
	 * Message: Not enough HP.
	 */
	NOT_ENOUGH_HP(23),
	
	/**
	 * ID: 24<br>
	 * Message: Not enough MP.
	 */
	NOT_ENOUGH_MP(24),
	
	/**
	 * ID: 25<br>
	 * Message: Rejuvenating HP.
	 */
	REJUVENATING_HP(25),
	
	/**
	 * ID: 26<br>
	 * Message: Rejuvenating MP.
	 */
	REJUVENATING_MP(26),
	
	/**
	 * ID: 27<br>
	 * Message: Your casting has been interrupted.
	 */
	YOU_CASTING_HAS_BEEN_INTERRUPTED(27),
	
	/**
	 * ID: 28<br>
	 * Message: You have obtained $s1 adena.
	 */
	YOU_HAVE_OBTAINED_S1_ADENA(28),
	
	/**
	 * ID: 29<br>
	 * Message: You have obtained $s2 $s1.
	 */
	YOU_HAVE_OBTAINED_S2_S1(29),
	
	/**
	 * ID: 30<br>
	 * Message: You have obtained $s1.
	 */
	YOU_HAVE_OBTAINED_S1(30),
	
	/**
	 * ID: 31<br>
	 * Message: You cannot move while sitting.
	 */
	YOU_CANNOT_MOVE_WHILE_SITTING(31),
	
	/**
	 * ID: 32<br>
	 * Message: You are unable to engage in combat. Please go to the nearest restart point.
	 */
	YOU_ARE_UNABLE_TO_ENGAGE_IN_COMBAT_PLEASE_GO_TO_THE_NEAREST_RESTART_POINT(32),
	
	/**
	 * ID: 33<br>
	 * Message: You cannot move while casting.
	 */
	YOU_CANNOT_MOVE_WHILE_CASTING(33),
	
	/**
	 * ID: 34<br>
	 * Message: Welcome to the World of Lineage II.
	 */
	WELCOME_TO_THE_WORLD_OF_LINEAGE_II(34),
	
	/**
	 * ID: 35<br>
	 * Message: You hit for $s1 damage.
	 */
	YOU_HIT_FOR_S1_DAMAGE(35),
	
	/**
	 * ID: 36<br>
	 * Message: $s1 hit you for $s2 damage.
	 */
	S1_HIT_YOU_FOR_S2_DAMAGE(36),
	
	/**
	 * ID: 37<br>
	 * Message: $s1 hit you for $s2 damage.
	 */
	S1_HIT_YOU_FOR_S2_DAMAGE_2(37),
	
	/**
	 * ID: 38<br>
	 * Message: The TGS2002 event begins!
	 */
	THE_TGS2002_EVENT_BEGINS(38),
	
	/**
	 * ID: 39<br>
	 * Message: The TGS2002 event is over. Thank you very much.
	 */
	THE_TGS2002_EVENT_IS_OVER_THANK_YOU_VERY_MUCH(39),
	
	/**
	 * ID: 40<br>
	 * Message: This is the TGS demo: the character will immediately be restored.
	 */
	THIS_IS_THE_TGS_DEMO_THE_CHARACTER_WILL_IMMEDIATELY_BE_RESTORED(40),
	
	/**
	 * ID: 41<br>
	 * Message: You carefully nock an arrow!
	 */
	YOU_CAREFULLY_NOCK_AN_ARROW(41),
	
	/**
	 * ID: 42<br>
	 * Message: You have avoided $s1's attack.
	 */
	YOU_HAVE_AVOIDED_S1S_ATTACK(42),
	
	/**
	 * ID: 43<br>
	 * Message: You have missed.
	 */
	YOU_HAVE_MISSED(43),
	
	/**
	 * ID: 44<br>
	 * Message: Critical hit!
	 */
	CRITICAL_HIT(44),
	
	/**
	 * ID: 45<br>
	 * Message: You have earned $s1 experience.
	 */
	YOU_HAVE_EARNED_S1_EXPERIENCE(45),
	
	/**
	 * ID: 46<br>
	 * Message: You use $s1.
	 */
	YOU_USE_S1(46),
	
	/**
	 * ID: 47<br>
	 * Message: You begin to use a(n) $s1.
	 */
	YOU_BEGIN_TO_USE_A_S1(47),
	
	/**
	 * ID: 48<br>
	 * Message: $s1 is not available at this time: being prepared for reuse.
	 */
	S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE(48),
	
	/**
	 * ID: 49<br>
	 * Message: You have equipped your $s1.
	 */
	YOU_HAVE_EQUIPPED_YOUR_S1(49),
	
	/**
	 * ID: 50<br>
	 * Message: Your target cannot be found.
	 */
	YOU_TARGET_CANNOT_BE_FOUND(50),
	
	/**
	 * ID: 51<br>
	 * Message: You cannot use this on yourself.
	 */
	YOU_CANNOT_USE_THIS_ON_YOURSELF(51),
	
	/**
	 * ID: 52<br>
	 * Message: You have earned $s1 adena.
	 */
	YOU_HAVE_EARNED_S1_ADENA(52),
	
	/**
	 * ID: 53<br>
	 * Message: You have earned $s2 $s1(s).
	 */
	YOU_HAVE_EARNED_S2_S1S(53),
	
	/**
	 * ID: 54<br>
	 * Message: You have earned $s1.
	 */
	YOU_HAVE_EARNED_S1(54),
	
	/**
	 * ID: 55<br>
	 * Message: You have failed to pick up $s1 adena.
	 */
	YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA(55),
	
	/**
	 * ID: 56<br>
	 * Message: You have failed to pick up $s1.
	 */
	YOU_HAVE_FAILED_TO_PICK_UP_S1(56),
	
	/**
	 * ID: 57<br>
	 * Message: You have failed to pick up $s2 $s1(s).
	 */
	YOU_HAVE_FAILED_TO_PICK_UP_S2_S1S(57),
	
	/**
	 * ID: 58<br>
	 * Message: You have failed to earn $s1 adena.
	 */
	YOU_HAVE_FAILED_TO_EARN_S1_ADENA(58),
	
	/**
	 * ID: 59<br>
	 * Message: You have failed to earn $s1.
	 */
	YOU_HAVE_FAILED_TO_EARN_S1(59),
	
	/**
	 * ID: 60<br>
	 * Message: You have failed to earn $s2 $s1(s).
	 */
	YOU_HAVE_FAILED_TO_EARN_S2_S1(60),

	/**
	 * ID: 61<br>
	 * Message: Nothing happened.
	 */
	NOTHING_HAPPENED(61),
	
	/**
	 * ID: 62<br>
	 * Message: Your $s1 has been successfully enchanted.
	 */
	YOU_S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED(62),
	
	/**
	 * ID: 63<br>
	 * Message: Your +$S1 $S2 has been successfully enchanted.
	 */
	YOUR_PLUS_S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED(63),
	
	/**
	 * ID: 64<br>
	 * Message: The enchantment has failed! Your $s1 has been crystallized.
	 */
	THE_ENCHANTMENT_HAS_FAILED_YOU_S1_HAS_BEEN_CRYSTALLIZED(64),
	
	/**
	 * ID: 65<br>
	 * Message: The enchantment has failed! Your +$s1 $s2 has been crystallized.
	 */
	THE_ENCHANTMENT_HAS_FAILED_YOU_PLUS_S1_S2_HAS_BEEN_CRYSTALLIZED(65),
	
	/**
	 * ID: 66<br>
	 * Message: $s1 has invited you to his/her party. Do you accept the invitation?
	 */
	S1_HAS_INVITED_YOU_TO_HIS_HER_PARTY_DO_YOU_ACCEPT_THE_INVITATION(66),
	
	/**
	 * ID: 67<br>
	 * Message: $s1 has invited you to the join the clan, $s2. Do you wish to join?
	 */
	S1_HAS_INVITED_YOU_TO_JOIN_THE_CLAN_S2_DO_YOU_WISH_TO_JOIN(67),
	
	/**
	 * ID: 68<br>
	 * Message: Would you like to withdraw from the $s1 clan? If you leave, you will have to wait at least a day before joining another clan.
	 */
	WOULD_YOU_LIKE_TO_WITHDRAW_FROM_THE_S1_CLAN_IF_YOU_LEAVE_YOU_WILL_HAVE_TO_WAIT_AT(68),
	
	/**
	 * ID: 69<br>
	 * Message: Would you like to dismiss $s1 from the clan? If you do so, you will have to wait at least a day before accepting a new member.
	 */
	WOULD_YOU_LIKE_TO_DISMISS_S1_FROM_THE_CLAN_IF_YOU_DO_SO_YOU_WILL_HAVE_TO_WAIT_AT_(69),
	
	/**
	 * ID: 70<br>
	 * Message: Do you wish to disperse the clan, $s1?
	 */
	DO_YOU_WISH_TO_DISPERSE_THE_CLAN_S1(70),
	
	/**
	 * ID: 71<br>
	 * Message: How many of your $s1(s) do you wish to discard?
	 */
	HOW_MANY_OF_YOUR_S1_DO_YOU_WISH_TO_DISCARD(71),
	
	/**
	 * ID: 72<br>
	 * Message: How many of your $s1(s) do you wish to move?
	 */
	HOW_MANY_OF_YOUR_S1_DO_YOU_WISH_TO_MOVE(72),
	
	/**
	 * ID: 73<br>
	 * Message: How many of your $s1(s) do you wish to destroy?
	 */
	HOW_MANY_OF_YOUR_S1_DO_YOU_WISH_TO_DESTROY(73),
		
	/**
	 * ID: 74<br>
	 * Message: Do you wish to destroy your $s1?
	 */
	DO_YOU_WISH_TO_DESTROY_YOUR_S1(74),
	
	/**
	 * ID: 75<br>
	 * Message: ID does not exist.
	 */
	ID_DOES_NOT_EXIST(75),
	
	/**
	 * ID: 76<br>
	 * Message: Incorrect password.
	 */
	INCORRECT_PASSWORD(76),
	
	/**
	 * ID: 77<br>
	 * Message: You cannot create another character. Please delete the existing character and try again.
	 */
	YOU_CANNOT_CREATE_ANOTHER_CHARACTER_PLEASE_DELETE_THE_EXISTING_CHARACTER_AND_TRY_(77),
	
	/**
	 * ID: 78<br>
	 * Message: Do you wish to delete $s1?
	 */
	DO_YOU_WISH_TO_DELETE_S1(78),
	
	/**
	 * ID: 79<br>
	 * Message: This name already exists.
	 */
	THIS_NAME_ALREADY_EXISTS(79),
	
	/**
	 * ID: 80<br>
	 * Message: Your title cannot exceed 16 characters in length. Please try again.
	 */
	YOUR_TITLE_CANNNOT_EXEED_16_CHARACTERS_IN_LENGTH_PLEASE_TRY_AGAIN(80),
	
	/**
	 * ID: 81<br>
	 * Message: Please select your race.
	 */
	PLEASE_SELECT_YOUR_RACE(81),
	
	/**
	 * ID: 82<br>
	 * Message: Please select your occupation.
	 */
	PLEASE_SELECT_YOUR_OCCUPATION(82),
	
	/**
	 * ID: 83<br>
	 * Message: Please select your gender.
	 */
	PLEASE_SELECT_YOUR_GENDER(83),
	
	/**
	 * ID: 84<br>
	 * Message: You may not attack in a peaceful zone.
	 */
	YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE(84),
	
	/**
	 * ID: 85<br>
	 * Message: You may not attack this target in a peaceful zone.
	 */
	YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_PEACEFUL_ZONE(85),
	
	/**
	 * ID: 86<br>
	 * Message: Please enter your ID.
	 */
	PLEASE_ENTER_YOUR_ID(86),
	
	/**
	 * ID: 87<br>
	 * Message: Please enter your password.
	 */
	PLEASE_ENTER_YOUR_PASSWORD(87),
	
	/**
	 * ID: 88<br>
	 * Message: Your protocol version is different, please restart your client and run a full check.
	 */
	YOUR_PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_RESTART_YOUR_CLIENT_AND_RUN_A_FULL_CHEC(88),
	
	/**
	 * ID: 89<br>
	 * Message: Your protocol version is different, please continue.
	 */
	YOUR_PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_CONTINUE(89),
	
	/**
	 * ID: 90<br>
	 * Message: You are unable to connect to the server.
	 */
	YOU_ARE_UNABLE_TO_CONNECT_TO_THE_SERVER(90),
	
	/**
	 * ID: 91<br>
	 * Message: Please select your hairstyle.
	 */
	PLEASE_SELECT_YOUR_HAIRSTYLE(91),
	
	/**
	 * ID: 92<br>
	 * Message: $s1 has worn off.
	 */
	S1_HAS_WORN_OFF(92),
	
	/**
	 * ID: 93<br>
	 * Message: You do not have enough SP for this.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_SP_FOR_THIS(93),
	
	/**
	 * ID: 94<br>
	 * Message: 2003 - 2007 Copyright NCsoft Corporation. All Rights Reserved.
	 */
	N_2003_2007_COPYRIGHT_NCSOFT_CORPORATION_ALL_RIGHTS_RESERVED(94),
	
	/**
	 * ID: 95<br>
	 * Message: You have earned $s1 experience and $s2 SP.
	 */
	YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP(95),
	
	/**
	 * ID: 96<br>
	 * Message: Your level has increased!
	 */
	YOUR_LEVEL_HAS_INCREASED(96),
	
	/**
	 * ID: 97<br>
	 * Message: This item cannot be moved.
	 */
	THIS_ITEM_CANNOT_BE_MOVED(97),
	
	/**
	 * ID: 98<br>
	 * Message: This item cannot be discarded.
	 */
	THIS_ITEM_CANNOT_BE_DISCARDED(98),
	
	/**
	 * ID: 99<br>
	 * Message: This item cannot be traded or sold.
	 */
	THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD(99),
	
	/**
	 * ID: 100<br>
	 * Message: $s1 has requested a trade. Do you wish to continue?
	 */
	S1_HAS_REQUESTED_A_TRADE_DO_YOU_WISH_TO_CONTINUE(100),
	
	/**
	 * ID: 101<br>
	 * Message: You cannot exit while in combat.
	 */
	YOU_CANNOT_EXIT_WHILE_IN_COMBAT(101),
	
	/**
	 * ID: 102<br>
	 * Message: You cannot restart while in combat.
	 */
	YOU_CANNOT_RESTART_WHILE_IN_COMBAT(102),
	
	/**
	 * ID: 103<br>
	 * Message: This ID is currently logged in.
	 */
	THIS_ID_IS_CURRENTLY_LOGGED_IN(103),
	
	/**
	 * ID: 104<br>
	 * Message: You may not equip items while casting or performing a skill.
	 */
	YOU_MAY_NOT_EQUIP_ITEMS_WHILE_CASTING_OR_PERFORMING_A_SKILL(104),
	
	/**
	 * ID: 105<br>
	 * Message: You have invited $s1 to your party.
	 */
	YOU_HAVE_INVITED_S1_TO_YOUR_PARTY(105),
	
	/**
	 * ID: 106<br>
	 * Message: You have joined $s1's party.
	 */
	YOU_HAVE_JOINED_S1S_PARTY(106),
	
	/**
	 * ID: 107<br>
	 * Message: $s1 has joined the party.
	 */
	S1_HAS_JOINED_THE_PARTY(107),
	
	/**
	 * ID: 108<br>
	 * Message: $s1 has left the party.
	 */
	S1_HAS_LEFT_THE_PARTY(108),
	
	/**
	 * ID: 109<br>
	 * Message: Invalid target.
	 */
	INVALID_TARGET(109),
	
	/**
	 * ID: 110<br>
	 * Message: The effects of $s1 flow through you.
	 */
	THE_EFFECTS_OF_S1_FLOW_THROUGH_YOU(110),
	
	/**
	 * ID: 111<br>
	 * Message: Your shield defense has succeeded.
	 */
	YOUR_SHIELD_DEFENSE_HAS_SUCCEDED(111),
	
	/**
	 * ID: 112<br>
	 * Message: You have run out of arrows.
	 */
	YOU_HAVE_RUN_OUT_OF_ARROWS(112),
	
	/**
	 * ID: 113<br>
	 * Message: $s1 cannot be used due to unsuitable terms.
	 */
	S1_CANNOT_BE_USED_TO_UNSUITABLE_TERMS(113),
	
	/**
	 * ID: 114<br>
	 * Message: You have entered the shadow of the Mother Tree.
	 */
	YOU_HAVE_ENTERED_THE_SHADOW_OF_THE_MOTHER_TREE(114),
	
	/**
	 * ID: 115<br>
	 * Message: You have left the shadow of the Mother Tree.
	 */
	YOU_HAVE_LEFT_THE_SHADOW_OF_THE_MOTHER_TREE(115),
	
	/**
	 * ID: 116<br>
	 * Message: You have entered a peaceful zone.
	 */
	YOU_HAVE_ENTERED_A_PEACEFUL_ZONE(116),
	
	/**
	 * ID: 117<br>
	 * Message: You have left the peaceful zone.
	 */
	YOU_HAVE_LEFT_THE_PEACEFUL_ZONE(117),
	
	/**
	 * ID: 118<br>
	 * Message: You have requested a trade with $s1.
	 */
	YOU_HAVE_REQUESTED_A_TRADE_WITH_S1(118),
	
	/**
	 * ID: 119<br>
	 * Message: $s1 has denied your request to trade.
	 */
	S1_HAS_DENIED_YOUR_REQUEST_TO_TRADE(119),
	
	/**
	 * ID: 120<br>
	 * Message: You begin trading with $s1.
	 */
	YOU_BEGIN_TRADING_WITH_S1(120),
	
	/**
	 * ID: 121<br>
	 * Message: $s1 has confirmed the trade.
	 */
	S1_HAS_CONFIRMED_THE_TRADE(121),
	
	/**
	 * ID: 122<br>
	 * Message: You may no longer adjust items in the trade because the trade has been confirmed.
	 */
	YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED(122),
	
	/**
	 * ID: 123<br>
	 * Message: Your trade is successful.
	 */
	YOUR_TRADE_IS_SUCCESSFUL(123),
	
	/**
	 * ID: 124<br>
	 * Message: $s1 has canceled the trade.
	 */
	S1_HAS_CANCELED_THE_TRADE(124),
	
	/**
	 * ID: 125<br>
	 * Message: Do you wish to exit the game?
	 */
	DO_YOU_WISH_TO_EXIT_THE_GAME(125),
	
	/**
	 * ID: 126<br>
	 * Message: Do you wish to exit to the character select screen?
	 */
	DO_YOU_WISH_TO_EXIT_TO_THE_CHARACTER_SELECT_SCREEN(126),
	
	/**
	 * ID: 127<br>
	 * Message: You have been disconnected from the server. Please login again.
	 */
	YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN(127),
	
	/**
	 * ID: 128<br>
	 * Message: Your character creation has failed.
	 */
	YOUR_CHARACTER_CREATION_HAS_FAILED(128),
	
	/**
	 * ID: 129<br>
	 * Message: Your inventory is full.
	 */
	YOUR_INVENTORY_IS_FULL(129),
	
	/**
	 * ID: 130<br>
	 * Message: Your warehouse is full.
	 */
	YOUR_WAREHOUSE_IS_FULL(130),
	
	/**
	 * ID: 131<br>
	 * Message: $s1 has logged in.
	 */
	S1_HAS_LOGGED_IN(131),
	
	/**
	 * ID: 132<br>
	 * Message: $s1 has been added to your friends list.
	 */
	S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST(132),
	
	/**
	 * ID: 133<br>
	 * Message: $s1 has been removed from your friends list.
	 */
	S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST(133),
	
	/**
	 * ID: 134<br>
	 * Message: Please check your friends list again.
	 */
	PLEACE_CHECK_YOUR_FRIEND_LIST_AGAIN(134),
	
	/**
	 * ID: 135<br>
	 * Message: $s1 did not reply to your invitation; your invite has been canceled.
	 */
	S1_DID_NOT_REPLY_TO_YOUR_INVITATION_YOUR_INVITE_HAS_BEEN_CANCELED(135),
	
	/**
	 * ID: 136<br>
	 * Message: You have not replied to $s1's invitation; the offer has been canceled.
	 */
	YOU_HAVVE_NOT_REPLIED_TO_S1S_INVITATION_THE_OFFER_HAS_BEEN_CANCELED(136),
	
	/**
	 * ID: 137<br>
	 * Message: There are no more items in the shortcut.
	 */
	THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT(137),
	
	/**
	 * ID: 138<br>
	 * Message: Designate shortcut.
	 */
	DESIGNATE_SHORTCUT(138),
	
	/**
	 * ID: 139<br>
	 * Message: $s1 has resisted your $s2.
	 */
	S1_HAS_RESISTED_YOUR_S2(139),
	
	/**
	 * ID: 140<br>
	 * Message: Your skill was removed due to a lack of MP.
	 */
	YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP(140),
	
	/**
	 * ID: 141<br>
	 * Message: Once the trade is confirmed, the item cannot be moved again.
	 */
	ONCE_THE_TRADE_IS_CONFIRMED_THE_ITEM_CANNOT_BE_MOVED_AGAIN(141),
	
	/**
	 * ID: 142<br>
	 * Message: You are already trading with someone.
	 */
	YOU_ARE_ALREADY_TRADING_WITH_SOMEONE(142),
	
	/**
	 * ID: 143<br>
	 * Message: $s1 is already trading with another person. Please try again later.
	 */
	S1_IS_ALREADY_TRADING_WITH_ANOTHER_PERSON_PLEASE_TRY_AGAIN_LATER(143),
	
	/**
	 * ID: 144<br>
	 * Message: That is the incorrect target.
	 */
	THAT_IS_THE_INCORRECT_TARGET(144),
	
	/**
	 * ID: 145<br>
	 * Message: That player is not online.
	 */
	THAT_PLAYER_IS_NOT_ONLINE(145),
	
	/**
	 * ID: 146<br>
	 * Message: Chatting is now permitted.
	 */
	CHATTING_IS_NOW_PERMITTED(146),
	
	/**
	 * ID: 147<br>
	 * Message: Chatting is currently prohibited.
	 */
	CHATTING_IS_CURRENTLY_PROHIBITED(147),
	
	/**
	 * ID: 148<br>
	 * Message: You cannot use quest items.
	 */
	YOU_CANNOT_USE_QUEST_ITEMS(148),
	
	/**
	 * ID: 149<br>
	 * Message: You cannot pick up or use items while trading.
	 */
	YOU_CANNOT_PICK_OR_USE_ITEMS_WHILE_TRADING(149),
	
	/**
	 * ID: 150<br>
	 * Message: You cannot discard or destroy an item while trading at a private store.
	 */
	YOU_CANNOT_DISCARD_OR_DESTROY_AN_ITEM_WHILE_TRADING_AT_A_PRIVATE_STORE(150),
	
	/**
	 * ID: 151<br>
	 * Message: That is too far from you to discard.
	 */
	THAT_IS_TOO_FAR_FROM_YOU_TO_DISCARD(151),
	
	/**
	 * ID: 152<br>
	 * Message: You have invited the wrong target.
	 */
	YOU_HAVE_INVITED_THE_WRONG_TARGET(152),
	
	/**
	 * ID: 153<br>
	 * Message: $s1 is busy. Please try again later.
	 */
	S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER(153),
	
	/**
	 * ID: 154<br>
	 * Message: Only the leader can give out invitations.
	 */
	ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS(154),
	
	/**
	 * ID: 155<br>
	 * Message: The party is full.
	 */
	THE_PARTY_IS_FULL(155),
	
	/**
	 * ID: 156<br>
	 * Message: Drain was only 50 percent successful.
	 */
	DRAIN_WAS_ONLY_50_PERCENT_SUCCESSFUL(156),
	
	/**
	 * ID: 157<br>
	 * Message: You resisted $s1's drain.
	 */
	YOU_RESISTED_S1S_DRAIN(157),
	
	/**
	 * ID: 158<br>
	 * Message: Your attack has failed.
	 */
	YOUR_ATTACK_HAS_FAILED(158),
	
	/**
	 * ID: 159<br>
	 * Message: You have resisted $s1's magic.
	 */
	YOU_HAVE_RESISTED_S1S_MAGIC(159),
	
	/**
	 * ID: 160<br>
	 * Message: $s1 is a member of another party and cannot be invited.
	 */
	S1_IS_ALREADY_IN_PARTY(160),
	
	/**
	 * ID: 161<br>
	 * Message: That player is not currently online.
	 */
	THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE(161),
	
	/**
	 * ID: 162<br>
	 * Message: Warehouse is too far.
	 */
	WAREHOUSE_IS_TOO_FAR(162),
	
	/**
	 * ID: 163<br>
	 * Message: You cannot destroy it because the number is incorrect.
	 */
	YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT(163),
	
	/**
	 * ID: 164<br>
	 * Message: Waiting for another reply.
	 */
	WAITING_FOR_ANOTHER_REPLY(164),
	
	/**
	 * ID: 165<br>
	 * Message: You cannot add yourself to your own friend list.
	 */
	YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST(165),
	
	/**
	 * ID: 166<br>
	 * Message: Friend list is not ready yet. Please register again later.
	 */
	FRIEND_LIST_NOT_READY_YET_PLEASE_REGISTER_AGAIN_LATER(166),
	
	/**
	 * ID: 167<br>
	 * Message: $s1 is already on your friend list.
	 */
	S1_IS_ALREADY_ON_YOUR_FRIEND_LIST(167),
	
	/**
	 * ID: 168<br>
	 * Message: $s1 has requested to become friends.
	 */
	S1_HAS_REQUESTED_TO_BECOME_FRIENDS(168),
	
	/**
	 * ID: 169<br>
	 * Message: Accept friendship 0/1 (1 to accept, 0 to deny)
	 */
	ACCEPT_FRIENDSHIP_0_1_1_TO_ACCEPT_0_TO_DENY(169),
	
	/**
	 * ID: 170<br>
	 * Message: The user who requested to become friends is not found in the game.
	 */
	THE_USER_WHO_YOU_REQUESTED_TO_BECOME_FRIEDS_IS_NOT_FOUND_IN_THE_GAME(170),
	
	/**
	 * ID: 171<br>
	 * Message: $s1 is not on your friend list.
	 */
	S1_IS_NOT_ON_YOUR_FRIENDS_LIST(171),
	
	/**
	 * ID: 172<br>
	 * Message: You lack the funds needed to pay for this transaction.
	 */
	YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION(172),
	
	/**
	 * ID: 173<br>
	 * Message: You lack the funds needed to pay for this transaction.
	 */
	YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION_2(173),
	
	/**
	 * ID: 174<br>
	 * Message: That person's inventory is full.
	 */
	THAT_PERSONS_INVENTORY_IS_FULL(174),
	
	/**
	 * ID: 175<br>
	 * Message: That skill has been de-activated as HP was fully recovered.
	 */
	THAT_SKILL_HAS_BEEN_DE_ACTIVATED_AS_HP_WAS_FULLY_RECOVERED(175),
	
	/**
	 * ID: 176<br>
	 * Message: That person is in message refusal mode.
	 */
	THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE(176),
	
	/**
	 * ID: 177<br>
	 * Message: Message refusal mode.
	 */
	MESSAGE_REFUSAL_MODE(177),
	
	/**
	 * ID: 178<br>
	 * Message: Message acceptance mode.
	 */
	MESSAGE_ACCEPTANCE_MODE(178),
	
	/**
	 * ID: 179<br>
	 * Message: You cannot discard those items here.
	 */
	YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE(179),
	
	/**
	 * ID: 180<br>
	 * Message: You have $s1 day(s) left until deletion.  Do you wish to cancel this action?
	 */
	YOU_HAVE_S1_DAY_LEFT_UNTIL_DELETION_DO_YOU_WISH_TO_CANCEL_THIS_ACTION(180),
	
	/**
	 * ID: 181<br>
	 * Message: Cannot see target.
	 */
	CANNOT_SEE_TARGET(181),
	
	/**
	 * ID: 182<br>
	 * Message: Do you want to quit the current quest?
	 */
	DO_YOU_WANT_TO_QUIT_THE_CURRENT_QUEST(182),
	
	/**
	 * ID: 183<br>
	 * Message: There are too many users on the server. Please try again later.
	 */
	THERE_ARE_TOO_MANY_USERS_ON_THE_SERVER_PLEASE_TRY_AGAIN_LATER(183),
	
	/**
	 * ID: 184<br>
	 * Message: Please try again later.
	 */
	PLEASE_TRY_AGAIN_LATER(184),
	
	/**
	 * ID: 185<br>
	 * Message: You must first select a user to invite to your party.
	 */
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_PARTY(185),
	
	/**
	 * ID: 186<br>
	 * Message: You must first select a user to invite to your clan.
	 */
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_CLAN(186),
	
	/**
	 * ID: 187<br>
	 * Message: Select user to expel.
	 */
	SELECT_USER_TO_EXPEL(187),
	
	/**
	 * ID: 188<br>
	 * Message: Please create your clan name.
	 */
	PLEASE_CREATE_YOUR_CLAN_NAME(188),
	
	/**
	 * ID: 189<br>
	 * Message: Your clan has been created.
	 */
	YOUR_CLAN_HAS_BEEN_CREATED(189),
	
	/**
	 * ID: 190<br>
	 * Message: You have failed to create a clan.
	 */
	YOU_HAVE_FAILED_TO_CREATE_A_CLAN(190),
	
	/**
	 * ID: 191<br>
	 * Message: Clan member $s1 has been expelled.
	 */
	CLAN_MEMBER_S1_HAS_BEEN_EXPELLED(191),
	
	/**
	 * ID: 192<br>
	 * Message: You have failed to expel $s1 from the clan.
	 */
	YOU_HAVE_FAILED_TO_EXPEL_S1_FROM_THE_CLAN(192),
	
	/**
	 * ID: 193<br>
	 * Message: Clan has dispersed.
	 */
	CLAN_HAS_DISPERSED(193),
	
	/**
	 * ID: 194<br>
	 * Message: You have failed to disperse the clan.
	 */
	YOU_HAVE_FAILED_TO_DISPERSE_THE_CLAN(194),
	
	/**
	 * ID: 195<br>
	 * Message: Entered the clan.
	 */
	ENTERED_THE_CLAN(195),
	
	/**
	 * ID: 196<br>
	 * Message: $s1 declined your clan invitation.
	 */
	S1_DECLINED_YOUR_CLAN_INVITATION(196),
	
	/**
	 * ID: 197<br>
	 * Message: You have withdrawn from the clan.
	 */
	YOU_HAVE_WITHDRAWN_FROM_CLAN(197),
	
	/**
	 * ID: 198<br>
	 * Message: You have failed to withdraw from the $s1 clan.
	 */
	YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_S1_CLAN(198),
	
	/**
	 * ID: 199<br>
	 * Message: You have recently been dismissed from a clan. You are not allowed to join another clan for 24-hours.
	 */
	YOU_HAVE_RECENTLY_DIMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWD_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS(199),
	
	/**
	 * ID: 200<br>
	 * Message: You have withdrawn from the party.
	 */
	YOU_HAVE_WITHDRAWN_FROM_THE_PARTY(200),
	
	/**
	 * ID: 201<br>
	 * Message: $s1 was expelled from the party.
	 */
	S1_WAS_EXPELLED_FROM_THE_PARTY(201),
	
	/**
	 * ID: 202<br>
	 * Message: You have been expelled from the party.
	 */
	YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY(202),
	
	/**
	 * ID: 203<br>
	 * Message: The party has dispersed.
	 */
	PARTY_DISPERSED(203),
	
	/**
	 * ID: 204<br>
	 * Message: Incorrect name. Please try again.
	 */
	INCORRECT_NAME_PLEASE_TRY_AGAIN(204),
	
	/**
	 * ID: 205<br>
	 * Message: Incorrect character name.  Please try again.
	 */
	INCORRECT_CHARACTER_NAME_PLEASE_TRY_AGAIN(205),
	
	/**
	 * ID: 206<br>
	 * Message: Please enter the name of the clan you wish to declare war on.
	 */
	PLEASE_ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_DECLARE_WAR_ON(206),
	
	/**
	 * ID: 207<br>
	 * Message: $s2 of the clan $s1 requests declaration of war. Do you accept?
	 */
	S2_OF_THE_CLAN_S1_REQUESTS_DECLARATION_OF_WAR_DO_YOU_ACCEPT(207),
	
	/**
	 * ID: 208<br>
	 * Message: Please include file type when entering file path.
	 */
	PLEASE_INCLUDE_FILE_TYPE_WHEN_ENTERING_FILE_PATH(208),
	
	/**
	 * ID: 209<br>
	 * Message: The size of the image file is inappropriate.  Please adjust to 16*12.
	 */
	THE_SIZE_OF_THE_IMAGE_FILE_IS_INAPPROPRIATE_PLEASE_ADJUST_TO_16X12(209),
	
	/**
	 * ID: 210<br>
	 * Message: Cannot find file. Please enter precise path.
	 */
	CANNOT_FIND_FILE_PLEASE_ENTER_PRECISE_PATH(210),
	
	/**
	 * ID: 211<br>
	 * Message: You may only register a 16 x 12 pixel, 256-color BMP.
	 */
	YOU_MAY_ONLY_REGISTER_A_16_X_12_PIXEL_256_COLOR_BMP(211),
	
	/**
	 * ID: 212<br>
	 * Message: You are not a clan member and cannot perform this action.
	 */
	YOU_ARE_NOT_A_CLAN_MEMBER(212),
	
	/**
	 * ID: 213<br>
	 * Message: Not working. Please try again later.
	 */
	NOT_WORKING_PLEASE_TRY_AGAIN_LATER(213),
	
	/**
	 * ID: 214<br>
	 * Message: Your title has been changed.
	 */
	TITLE_CHANGED(214),
	
	/**
	 * ID: 215<br>
	 * Message: War with the $s1 clan has begun.
	 */
	WAR_WITH_THE_S1_CLAN_HAS_BEGUN(215),
	
	/**
	 * ID: 216<br>
	 * Message: War with the $s1 clan has ended.
	 */
	WAR_WITH_THE_S1_CLAN_HAS_ENDED(216),
	
	/**
	 * ID: 217<br>
	 * Message: You have won the war over the $s1 clan!
	 */
	YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN(217),
	
	/**
	 * ID: 218<br>
	 * Message: You have surrendered to the $s1 clan.
	 */
	YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN(218),
	
	/**
	 * ID: 219<br>
	 * Message: Your clan leader has died. You have been defeated by the $s1 Clan.
	 */
	YOUR_CLAN_LEADER_HAS_DIED_YOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN(219),
	
	/**
	 * ID: 220<br>
	 * Message: You have $s1 minutes left until the clan war ends.
	 */
	YOU_HAVE_S1_MINUTES_LEFT_UNTIL_THE_CLAN_WAR_ENDS(220),
	
	/**
	 * ID: 221<br>
	 * Message: The time limit for the clan war is up. War with the $s1 clan is over.
	 */
	THE_TIME_LIMIT_FOR_THE_CLAN_WAR_IS_UP_WAR_WITH_THE_S1_CLAN_IS_OVER(221),
	
	/**
	 * ID: 222<br>
	 * Message: $s1 has joined the clan.
	 */
	S1_HAS_JOINED_THE_CLAN(222),
	
	/**
	 * ID: 223<br>
	 * Message: $s1 has withdrawn from the clan.
	 */
	S1_HAS_WITHDRAWN_FROM_THE_CLAN(223),
	
	/**
	 * ID: 224<br>
	 * Message: $s1 did not respond: Invitation to the clan has been cancelled.
	 */
	S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED(224),
	
	/**
	 * ID: 225<br>
	 * Message: You didn't respond to $s1's invitation: joining has been cancelled.
	 */
	YOU_DIDNT_RESPOND_TO_S1S_INVITATION_JOINING_HAS_BEEN_CANCELLED(225),
	
	/**
	 * ID: 226<br>
	 * Message: The $s1 clan did not respond: war proclamation has been refused.
	 */
	THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED(226),
	
	/**
	 * ID: 227<br>
	 * Message: Clan war has been refused because you did not respond to $s1 clan's war proclamation.
	 */
	CLAN_WAR_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLAN_S_WAR_PROCLAMATI(227),
	
	/**
	 * ID: 228<br>
	 * Message: Request to end war has been denied.
	 */
	REQUEST_TO_END_WAR_HAS_BEEN_DENIED(228),
	
	/**
	 * ID: 229<br>
	 * Message: You do not meet the criteria in order to create a clan.
	 */
	YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN(229),
	
	/**
	 * ID: 230<br>
	 * Message: You must wait 10 days before creating a new clan.
	 */
	YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN(230),
	
	/**
	 * ID: 231<br>
	 * Message: After a clan member is dismissed from a clan, the clan must wait at least a day before accepting a new member.
	 */
	AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BE(231),
	
	/**
	 * ID: 232<br>
	 * Message: After leaving or having been dismissed from a clan, you must wait at least a day before joining another clan.
	 */
	AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_B(232),
	
	/**
	 * ID: 233<br>
	 * Message: The Academy/Royal Guard/Order of Knights is full and cannot accept new members at this time.
	 */
	THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS(233),
	
	/**
	 * ID: 234<br>
	 * Message: The target must be a clan member.
	 */
	THE_TARGET_MUST_BE_A_CLAN_MEMBER(234),
	
	/**
	 * ID: 235<br>
	 * Message: You are not authorized to bestow these rights.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_BESTOW_THESE_RIGHTS(235),
	
	/**
	 * ID: 236<br>
	 * Message: Only the clan leader is enabled.
	 */
	ONLY_THE_CLAN_LEADER_IS_ENABLED(236),
	
	/**
	 * ID: 239<br>
	 * Message: The clan leader cannot withdraw.
	 */
	THE_CLAN_LEADER_CANNOT_WITHDRAW(239),
	
	/**
	 * ID: 240<br>
	 * Message: Currently involved in clan war.
	 */
	CURRENTLY_INVOLVED_IN_CLAN_WAR(240),
	
	/**
	 * ID: 241<br>
	 * Message: Leader of the $s1 Clan is not logged in.
	 */
	LEADER_OF_THE_S1_CLAN_IS_NOT_LOGGED_IN(241),
	
	/**
	 * ID: 242<br>
	 * Message: Select target.
	 */
	SELECT_TARGET(242),
	
	/**
	 * ID: 243<br>
	 * Message: You cannot declare war on an allied clan.
	 */
	YOU_CANNOT_DECLARE_WAR_ON_AN_ALLIED_CLAN(243),
	
	/**
	 * ID: 244<br>
	 * Message: You are not allowed to issue this challenge.
	 */
	YOU_ARE_NOT_ALLOWED_TO_ISSUE_THIS_CHALLENGE(244),
	
	/**
	 * ID: 245<br>
	 * Message: 5 days has not passed since you were refused war. Do you wish to continue?
	 */
	FIVE_DAYS_HAS_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR_DO_YOU_WISH_TO_CONTINUE(245),
	
	/**
	 * ID: 246<br>
	 * Message: That clan is currently at war.
	 */
	THAT_CLAN_IS_CURRENTLY_AT_WAR(246),
	
	/**
	 * ID: 247<br>
	 * Message: You have already been at war with the $s1 clan: 5 days must pass before you can challenge this clan again.
	 */
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_CHA(247),
	
	/**
	 * ID: 248<br>
	 * Message: You cannot proclaim war: the $s1 clan does not have enough members.
	 */
	YOU_CANNOT_PROCLAIM_WAR_THE_S1_CLAN_DOES_NOT_HAVE_ENOUGH_MEMBERS(248),
	
	/**
	 * ID: 249<br>
	 * Message: Do you wish to surrender to the $s1 clan?
	 */
	DO_YOU_WISH_TO_SURRENDER_TO_THE_S1_CLAN(249),
	
	/**
	 * ID: 250<br>
	 * Message: You have personally surrendered to the $s1 clan. You are no longer participating in this clan war.
	 */
	YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN_YOU_ARE_NO_LONGER_PARTICIPATING_IN_THIS_CLAN_WAR(250),
	
	/**
	 * ID: 251<br>
	 * Message: You cannot proclaim war: you are at war with another clan.
	 */
	YOU_CANNOT_PROCLAIM_WAR_YOU_ARE_AT_WAR_WITH_ANOTHER_CLAN(251),
	
	/**
	 * ID: 252<br>
	 * Message: Enter the name of clan to surrender to.
	 */
	ENTER_THE_NAME_OF_CLAN_TO_SURRENDER_TO(252),
	
	/**
	 * ID: 253<br>
	 * Message: Enter the name of the clan you wish to end the war with.
	 */
	ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_END_THE_WAR_WITH(253),
	
	/**
	 * ID: 254<br>
	 * Message: A clan leader cannot personally surrender.
	 */
	A_CLAN_LEADER_CANNOT_PERSONALLY_SURRENDER(254),

	/**
	 * ID: 255<br>
	 * Message: The $s1 Clan has requested to end war. Do you agree?
	 */
	THE_S1_CLAN_HAS_REQUESTED_TO_END_WAR_DO_YOU_AGREE(255),

	/**
	 * ID: 256<br>
	 * Message: Enter Title
	 */
	ENTER_TITLE(256),

	/**
	 * ID: 257<br>
	 * Message: Do you offer the $s1 clan a proposal to end the war?
	 */
	DO_YOU_OFFER_THE_S1_CLAN_A_PROPOSAL_TO_END_THE_WAR(257),

	/**
	 * ID: 258<br>
	 * Message: You are not involved in a clan war.
	 */
	YOU_ARE_NOT_INVOLVED_IN_A_CLAN_WAR(258),

	/**
	 * ID: 259<br>
	 * Message: Select clan members from list.
	 */
	SELECT_CLAN_MEMBERS_FROM_LIST(259),

	/**
	 * ID: 260<br>
	 * Message: Fame level has decreased: 5 days have not passed since you were refused war.
	 */
	FAME_LEVEL_HAS_DECREASED_5_DAYS_HAVE_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR(260),
	
	/**
	 * ID: 261<br>
	 * Message: Clan name is invalid.
	 */
	CLAN_NAME_IS_INVALID(261),
	
	/**
	 * ID: 262<br>
	 * Message: Clan name's length is incorrect.
	 */
	CLAN_NAMES_LENGTH_IS_INCORRECT(262),
	
	/**
	 * ID: 263<br>
	 * Message: You have already requested the dissolution of your clan.
	 */
	YOU_HAVE_ALREADY_REQUESTES_THE_DISSOLUTION_OF_YOUR_CLAN(263),
	
	/**
	 * ID: 264<br>
	 * Message: You cannot dissolve a clan while engaged in a war.
	 */
	YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR(264),
	
	/**
	 * ID: 265<br>
	 * Message: You cannot dissolve a clan during a siege or while protecting a castle.
	 */
	YOU_CANNOT_DISSOLVE_A_CLAN_DURING_SIEGE_OR_WHILE_PROTECTING_A_CASTLE(265),
	
	/**
	 * ID: 266<br>
	 * Message: You cannot dissolve a clan while owning a clan hall or castle.
	 */
	YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE(266),
	
	/**
	 * ID: 267<br>
	 * Message: There are no requests to disperse.
	 */
	THERE_ARE_NO_REQUESTS_TO_DISPERSE(267),

	/**
	 * ID: 268<br>
	 * Message: That player already belongs to another clan.
	 */
	THAT_PLAYER_ALREADY_BELONGS_TO_ANOTHER_CLAN(268),
	
	/**
	 * ID: 269<br>
	 * Message: You cannot dismiss yourself.
	 */
	YOU_CANNOT_DISMISS_YOURSELF(269),
	
	/**
	 * ID: 270<br>
	 * Message: You have already surrendered.
	 */
	YOU_HAVE_ALREADY_SURRENDERED(270),
	
	/**
	 * ID: 271<br>
	 * Message: A player can only be granted a title if the clan is level 3 or above.
	 */
	A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE(271),
	
	/**
	 * ID: 272<br>
	 * Message: A clan crest can only be registered when the clan's skill level is 3 or above.
	 */
	A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE(272),
	
	/**
	 * ID: 273<br>
	 * Message: A clan war can only be declared when a clan's skill level is 3 or above.
	 */
	A_CLAN_WAR_CAN_ONLY_BE_DECLARED_WHEN_A_CLAN_S_SKILL_LEVEL_IS_3_OR_ABOVE(273),
	
	/**
	 * ID: 274<br>
	 * Message: Your clan's skill level has increased.
	 */
	YOU_CLANS_SKILL_LEVEL_HAS_INCREASED(274),
	
	/**
	 * ID: 275<br>
	 * Message: Clan has failed to increase skill level.
	 */
	CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL(275),
	
	/**
	 * ID: 276<br>
	 * Message: You do not have the necessary materials or prerequisites to learn this skill.
	 */
	YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL(276),
	
	/**
	 * ID: 277<br>
	 * Message: You have earned $s1.
	 */
	YOU_HAVE_EARNED_S1_2(277),
	
	/**
	 * ID: 278<br>
	 * Message: You do not have enough SP to learn this skill.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL(278),
	
	/**
	 * ID: 279<br>
	 * Message: You do not have enough adena.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_ADENA(279),
	
	/**
	 * ID: 280<br>
	 * Message: You do not have any items to sell.
	 */
	YOU_DO_NOT_HAVE_ANY_ITEMS_TO_SELL(280),
	
	/**
	 * ID: 281<br>
	 * Message: You do not have enough adena to pay the fee.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_PAY_THE_FEE(281),
	
	/**
	 * ID: 282<br>
	 * Message: You have not deposited any items in your warehouse.
	 */
	YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE(282),
	
	/**
	 * ID: 283<br>
	 * Message: You have entered a combat zone.
	 */
	YOU_HAVE_ENTERED_A_COMBAT_ZONE(283),
	
	/**
	 * ID: 284<br>
	 * Message: You have left a combat zone.
	 */
	YOU_HAVE_LEFT_A_COMBAT_ZONE(284),
	
	/**
	 * ID: 285<br>
	 * Message: Clan $s1 has succeeded in engraving the ruler!
	 */
	CLAN_S1_HAS_SUCCEEDED_IN_ENGRAVING_THE_RULER(285),
	
	/**
	 * ID: 286<br>
	 * Message: Your base is being attacked.
	 */
	YOUR_BASE_IS_BEING_ATTACKED(286),

	/**
	 * ID: 287<br>
	 * Message: The opposing clan has started to engrave the monument!
	 */
	THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_MONUMENT(287),

	/**
	 * ID: 288<br>
	 * Message: The castle gate has been broken down.
	 */
	THE_CASTLE_GATE_HAS_BEEN_BROKEN_DOWN(288),

	/**
	 * ID: 289<br>
	 * Message: You cannot build another headquarters since one already exists.
	 */
	YOU_CANNOT_BUILD_ANOTHER_HEADQUARTERS_SINCE_ONE_ALREADY_EXISTS(289),

	/**
	 * ID: 290<br>
	 * Message: You cannot set up a base here.
	 */
	YOU_CANNOT_SET_UP_A_BASE_HERE(290),

	/**
	 * ID: 291<br>
	 * Message: Clan $s1 is victorious over $s2's castle siege!
	 */
	CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE(291),

	/**
	 * ID: 292<br>
	 * Message: $s1 has announced the castle siege time.
	 */
	S1_HAS_ANNOUNCED_THE_CASTLE_SIEGE_TIME(292),

	/**
	 * ID: 293<br>
	 * Message: The registration term for $s1 has ended.
	 */
	THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED(293),

	/**
	 * ID: 294<br>
	 * Message: Because your clan is not currently on the offensive in a Clan Hall siege war, it cannot summon its base camp.
	 */
	BECAUSE_YOUR_CLAN_IS_NOT_CURRENTLY_ON_THE_OFFENSIVE_IN_A_CLAN_HALL_SIEGE_WAR_IT_C(294),
	
	/**
	 * ID: 295<br>
	 * Message: $s1's siege was canceled because there were no clans that participated.
	 */
	S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED(295),
	
	/**
	 * ID: 296<br>
	 * Message: You received $s1 damage from taking a high fall.
	 */
	YOU_RECEIVED_S1_DAMAGE_FROM_A_HIGH_FALL(296),
	
	/**
	 * ID: 297<br>
	 * Message: You have taken $s1 damage because you were unable to breathe.
	 */
	YOU_HAVE_TAKEN_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE(297),
	
	/**
	 * ID: 298<br>
	 * Message: You have dropped $s1.
	 */
	YOU_HAVE_DROPPED_S1(298),
	
	/**
	 * ID: 299<br>
	 * Message: $s1 has obtained $s3 $s2.
	 */
	S1_HAS_OBTAINED_S3_S2(299),
	
	/**
	 * ID: 300<br>
	 * Message: $s1 has obtained $s2.
	 */
	S1_HAS_OBTAINED_S2(300),
	
	/**
	 * ID: 301<br>
	 * Message: $s2 $s1 has disappeared.
	 */
	S2_S1_HAS_DISAPPEARED(301),
	
	/**
	 * ID: 302<br>
	 * Message: $s1 has disappeared.
	 */
	S1_HAS_DISAPPEARED(302),
	
	/**
	 * ID: 303<br>
	 * Message: Select item to enchant.
	 */
	SELECT_ITEM_TO_ENCHANT(303),
	
	/**
	 * ID: 304<br>
	 * Message: Clan member $s1 has logged into game.
	 */
	CLAN_MEMBER_S1_LOGGED_INTO_GAME(304),
	
	/**
	 * ID: 305<br>
	 * Message: The player declined to join your party.
	 */
	THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY(305),
	
	/**
	 * ID: 306<br>
	 * Message: You have failed to delete the character.
	 */
	YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER(306),
	
	/**
	 * ID: 307<br>
	 * Message: You cannot trade with a warehouse keeper.
	 */
	YOU_CANNOT_TRADE_WITH_A_WAREHOUSE_KEEPER(307),

	/**
	 * ID: 308<br>
	 * Message: The player declined your clan invitation.
	 */
	THE_PLAYER_DECLINED_YOUR_CLAN_INVITATION(308),
	
	/**
	 * ID: 309<br>
	 * Message: You have succeeded in expelling the clan member.
	 */
	YOU_HAVE_SUCCEEDED_IN_EXPELLING_CLAN_MEMBER(309),
	
	/**
	 * ID: 310<br>
	 * Message: You have failed to expel the clan member.
	 */
	YOU_HAVE_FAILED_TO_EXPEL_THE_CLAN_MEMBER(310),
	
	/**
	 * ID: 311<br>
	 * Message: The clan war declaration has been accepted.
	 */
	THE_CLAN_WAR_DECLARATION_HAS_BEEN_ACCEPTED(311),

	/**
	 * ID: 312<br>
	 * Message: The clan war declaration has been refused.
	 */
	THE_CLAN_WAR_DECLARATION_HAS_BEEN_REFUSED(312),

	/**
	 * ID: 313<br>
	 * Message: The cease war request has been accepted.
	 */
	THE_CEASE_WAR_REQUEST_HAS_BEEN_ACCEPTED(313),

	/**
	 * ID: 314<br>
	 * Message: You have failed to surrender.
	 */
	YOU_HAVE_FAILED_TO_SURRENDER(314),

	/**
	 * ID: 315<br>
	 * Message: You have failed to personally surrender.
	 */
	YOU_HAVE_FAILED_TO_PERSONALLY_SURRENDER(315),

	/**
	 * ID: 316<br>
	 * Message: You have failed to withdraw from the party.
	 */
	YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_PARTY(316),

	/**
	 * ID: 317<br>
	 * Message: You have failed to expel the party member.
	 */
	YOU_HAVE_FAILED_TO_EXPEL_THE_PARTY_MEMBER(317),

	/**
	 * ID: 318<br>
	 * Message: You have failed to disperse the party.
	 */
	YOU_HAVE_FAILED_TO_DISPERSE_THE_PARTY(318),
	
	/**
	 * ID: 319<br>
	 * Message: This door cannot be unlocked.
	 */
	THIS_DOOR_CANNOT_BE_UNLOCKED(319),
	
	/**
	 * ID: 320<br>
	 * Message: You have failed to unlock the door.
	 */
	YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR(320),
	
	/**
	 * ID: 321<br>
	 * Message: It is not locked.
	 */
	IT_IS_NOT_LOCKED(321),

	/**
	 * ID: 322<br>
	 * Message: Please decide on the sales price.
	 */
	PLEASE_DECIDE_ON_THE_SALES_PRICE(322),
	
	/**
	 * ID: 323<br>
	 * Message: Your force has increased to $s1 level.
	 */
	YOU_FORCE_HAS_INCREASED_TO_S1_LEVEL(323),
	
	/**
	 * ID: 324<br>
	 * Message: Your force has reached maximum capacity.
	 */
	YOU_FORCE_HAS_REACHED_MAXIMUM_CAPACITY(324),
	
	/**
	 * ID: 326<br>
	 * Message: Select target from list.
	 */
	SELECT_TARGET_FROM_LIST(326),

	/**
	 * ID: 327<br>
	 * Message: You cannot exceed 80 characters.
	 */
	YOU_CANNOT_EXCEED_80_CHARACTERS(327),

	/**
	 * ID: 328<br>
	 * Message: Please input title using less than 128 characters.
	 */
	PLEASE_INPUT_TITLE_USING_LESS_THAN_128_CHARACTERS(328),

	/**
	 * ID: 329<br>
	 * Message: Please input contents using less than 3000 characters.
	 */
	PLEASE_INPUT_CONTENTS_USING_LESS_THAN_3000_CHARACTERS(329),

	/**
	 * ID: 330<br>
	 * Message: A one-line response may not exceed 128 characters.
	 */
	A_ONE_LINE_RESPONSE_MAY_NOT_EXCEED_128_CHARACTERS(330),

	/**
	 * ID: 331<br>
	 * Message: You have acquired $s1 SP.
	 */
	YOU_HAVE_ACQUIRED_S1_SP(331),

	/**
	 * ID: 332<br>
	 * Message: Do you want to be restored?
	 */
	DO_YOU_WANT_TO_BE_RESTORED(332),

	/**
	 * ID: 333<br>
	 * Message: You have received $s1 damage by Core's barrier.
	 */
	YOU_HAVE_RECEIVED_S1_DAMAGE_BY_CORE_S_BARRIER(333),

	/**
	 * ID: 334<br>
	 * Message: Please enter your private store display message.
	 */
	PLEASE_ENTER_YOUR_PRIVATE_STORE_DISPLAY_MESSAGE(334),

	/**
	 * ID: 335<br>
	 * Message: $s1 has been aborted.
	 */
	S1_HAS_BEEN_ABORTED(335),
	
	/**
	 * ID: 336<br>
	 * Message: You are attempting to crystalize $s1.  Do you wish to continue?
	 */
	YOU_ARE_ATTEMPTING_TO_CRYSTALIZE_S1_DO_YOU_WISH_TO_CONTINUE(336),
	
	/**
	 * ID: 337<br>
	 * Message: The soulshot you are attempting to use does not match the grade of your equipped weapon.
	 */
	THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON(337),
	
	/**
	 * ID: 338<br>
	 * Message: You do not have enough soulshots for that.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT(338),
	
	/**
	 * ID: 339<br>
	 * Message: Cannot use soulshots.
	 */
	CANNOT_USE_SOULSHOTS(339),
	
	/**
	 * ID: 340<br>
	 * Message: Your private store is now open for business.
	 */
	YOUR_PRIVATE_STORE_IS_NOW_OPEN_FOR_BUSINESS(340),
	
	/**
	 * ID: 341<br>
	 * Message: You do not have enough materials to perform that action.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION(341),
	
	/**
	 * ID: 342<br>
	 * Message: Power of the spirits enabled.
	 */
	POWER_OF_THE_SPIRITS_ENABLED(342),
	
	/**
	 * ID: 343<br>
	 * Message: Sweeper failed, target not spoiled.
	 */
	SWEEPER_FAILED_TARGET_NOT_SPOILED(343),
	
	/**
	 * ID: 344<br>
	 * Message: Power of the spirits disabled.
	 */
	POWER_OF_THE_SPIRITS_DISABLED(344),

	/**
	 * ID: 345<br>
	 * Message: Chat enabled.
	 */
	CHAT_ENABLED(345),

	/**
	 * ID: 346<br>
	 * Message: Chat disabled.
	 */
	CHAT_DISABLED(346),

	/**
	 * ID: 347<br>
	 * Message: Incorrect item count.
	 */
	INCORRECT_ITEM_COUNT(347),

	/**
	 * ID: 348<br>
	 * Message: Incorrect item price.
	 */
	INCORRECT_ITEM_PRICE(348),

	/**
	 * ID: 349<br>
	 * Message: Private store already closed.
	 */
	PRIVATE_STORE_ALREADY_CLOSED(349),

	/**
	 * ID: 350<br>
	 * Message: Item out of stock.
	 */
	ITEM_OUT_OF_STOCK(350),
	
	/**
	 * ID: 351<br>
	 * Message: Incorrect item count.
	 */
	INCORRECT_ITEM_COUNT_2(351),
	
	/**
	 * ID: 352<br>
	 * Message: Incorrect item.
	 */
	INCORRECT_ITEM(352),

	/**
	 * ID: 353<br>
	 * Message: Cannot purchase.
	 */
	CANNOT_PURCHASE(353),

	/**
	 * ID: 354<br>
	 * Message: Cancel enchant.
	 */
	CANCEL_ENCHANT(354),
	
	/**
	 * ID: 355<br>
	 * Message: Inappropriate enchant conditions.
	 */
	INAPPROPRIATE_ENCHANT_CONDITION(355),
	
	/**
	 * ID: 356<br>
	 * Message: Reject resurrection.
	 */
	REJECT_RESURRECTION(356),
	
	/**
	 * ID: 357<br>
	 * Message: It has already been spoiled.
	 */
	IT_HAS_ALREDAY_BEEN_SPOILED(357),
	
	/**
	 * ID: 358<br>
	 * Message: $s1 hour(s) until castle siege conclusion.
	 */
	S1_HOUR_UNTIL_CASTLE_SIEGE_CONCLUSION(358),

	/**
	 * ID: 359<br>
	 * Message: $s1 minute(s) until castle siege conclusion.
	 */
	S1_MINUTE_UNTIL_CASTLE_SIEGE_CONCLUSION(359),

	/**
	 * ID: 360<br>
	 * Message: Castle siege $s1 second(s) left!
	 */
	CASTLE_SIEGE_S1_SECOND_LEFT(360),
	
	/**
	 * ID: 361<br>
	 * Message: Over-hit!
	 */
	OVER_HIT(361),
	
	/**
	 * ID: 362<br>
	 * Message: You have acquired $s1 bonus experience from a successful over-hit.
	 */
	YOU_HAVE_ACQUIRED_S1_BONUS_EXPERIENCE_FROM_A_SUCCESSFUL_OVER_HIT(362),
	
	/**
	 * ID: 363<br>
	 * Message: Chat available time: $s1 minute.
	 */
	CHAT_AVAILABLE_TIME_S1_MINUTE(363),

	/**
	 * ID: 364<br>
	 * Message: Enter user's name to search.
	 */
	ENTER_USER_S_NAME_TO_SEARCH(364),

	/**
	 * ID: 365<br>
	 * Message: Are you sure?
	 */
	ARE_YOU_SURE(365),

	/**
	 * ID: 366<br>
	 * Message: Please select your hair color.
	 */
	PLEASE_SELECT_YOUR_HAIR_COLOR(366),

	/**
	 * ID: 367<br>
	 * Message: You cannot remove that clan character at this time.
	 */
	YOU_CANNOT_REMOVE_THAT_CLAN_CHARACTER_AT_THIS_TIME(367),
	
	/**
	 * ID: 368<br>
	 * Message: Equipped +$s1 $s2.
	 */
	EQUIPPED_PLUS_S1_S2(368),
	
	/**
	 * ID: 369<br>
	 * Message: You have obtained a +$s1 $s2.
	 */
	YOU_HAVE_OBTAINED_A_PLUS_S1_S2(369),
	
	/**
	 * ID: 370<br>
	 * Message: Failed to pick up $s1.
	 */
	FAILED_TO_PICK_UP_S1(370),
	
	/**
	 * ID: 371<br>
	 * Message: Acquired +$s1 $s2.
	 */
	ACQUIRED_PLUS_S1_S2(371),
	
	/**
	 * ID: 372<br>
	 * Message: Failed to earn $s1.
	 */
	FAILED_TO_EARN_S1(372),

	/**
	 * ID: 373<br>
	 * Message: You are trying to destroy +$s1 $s2.  Do you wish to continue?
	 */
	YOU_ARE_TRYING_TO_DESTROY_PLUS_S1_S2_DO_YOU_WISH_TO_CONTINUE(373),

	/**
	 * ID: 374<br>
	 * Message: You are attempting to crystalize +$s1 $s2.  Do you wish to continue?
	 */
	YOU_ARE_ATTEMPTING_TO_CRYSTALIZE_PLUS_S1_S2_DO_YOU_WISH_TO_CONTINUE(374),

	/**
	 * ID: 375<br>
	 * Message: You have dropped +$s1 $s2.
	 */
	YOU_HAVE_DROPPED_PLUS_S1_S2(375),

	/**
	 * ID: 376<br>
	 * Message: $s1 has obtained +$s2$s3.
	 */
	S1_HAS_OBTAINED_PLUS_S2_S3(376),

	/**
	 * ID: 377<br>
	 * Message: $S1 $S2 disappeared.
	 */
	S1_S2_DISAPPEARED(377),
	
	/**
	 * ID: 378<br>
	 * Message: $s1 purchased $s2.
	 */
	S1_PURCHASED_S2(378),
	
	/**
	 * ID: 379<br>
	 * Message: $s1 purchased +$s2 $s3.
	 */
	S1_PURCHASED_PLUS_S2_S3(379),
	
	/**
	 * ID: 380<br>
	 * Message: $s1 purchased $s3 $s2(s).
	 */
	S1_PURCHASED_S3_S2_S(380),
	
	/**
	 * ID: 381<br>
	 * Message: The game client encountered an error and was unable to connect to the petition server.
	 */
	THE_GAME_CLIENT_ENCOUNTERED_AN_ERROR_AND_WAS_UNABLE_TO_CONNECT_TO_THE_PETITION_SERVER(381),
	
	/**
	 * ID: 382<br>
	 * Message: Currently there are no users that have checked out a GM ID.
	 */
	CURRENTLY_THERE_ARE_NO_USERS_THAT_HAVE_CHECKED_OUT_A_GM_ID(382),

	/**
	 * ID: 383<br>
	 * Message: Request confirmed to end consultation at petition server.
	 */
	REQUEST_CONFIRMED_TO_END_CONSULTATION_AT_PETITION_SERVER(383),

	/**
	 * ID: 384<br>
	 * Message: The client is not logged onto the game server.
	 */
	THE_CLIENT_IS_NOT_LOGGED_ONTO_THE_GAME_SERVER(384),

	/**
	 * ID: 385<br>
	 * Message: Request confirmed to begin consultation at petition server.
	 */
	REQUEST_CONFIRMED_TO_BEGIN_CONSULTATION_AT_PETITION_SERVER(385),

	/**
	 * ID: 386<br>
	 * Message: The body of your petition must be more than five characters in length.
	 */
	THE_BODY_OF_YOUR_PETITION_MUST_BE_MORE_THAN_FIVE_CHARACTERS_IN_LENGTH(386),
	
	/**
	 * ID: 387<br>
	 * Message: This ends the GM petition consultation. \n Please take a moment to provide feedback about this service.
	 */
	THIS_ENDS_THE_GM_PETITION_CONSULTATION(387),
	
	/**
	 * ID: 388<br>
	 * Message: Not under petition consultation.
	 */
	NOT_UNDER_PETITION_CONSULTATION(388),
	
	/**
	 * ID: 389<br>
	 * Message: Your petition application has been accepted. \n - Receipt No. is $s1.
	 */
	YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_RECEIPT_NO_IS_S1(389),
	
	/**
	 * ID: 390<br>
	 * Message: You may only submit one petition (active) at a time.
	 */
	YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME(390),
	
	/**
	 * ID: 391<br>
	 * Message: Receipt No. $s1, petition cancelled.
	 */
	RECEIPT_NO_S1_PETITION_CANCELLED(391),
	
	/**
	 * ID: 392<br>
	 * Message: Under petition advice.
	 */
	UNDER_PETITION_ADVICE(392),
	
	/**
	 * ID: 393<br>
	 * Message: Failed to cancel petition. Please try again later.
	 */
	FAILED_CANCEL_PETITION_TRY_LATER(393),
	
	/**
	 * ID: 394<br>
	 * Message: Petition consultation with $s1, under way.
	 */
	PETITION_CONSULTATION_WITH_S1_UNDER_WAY(394),
	
	/**
	 * ID: 395<br>
	 * Message: Ending petition consultation with $s1.
	 */
	ENDING_PETITION_CONSULTATION_WITH_S1(395),
	
	/**
	 * ID: 396<br>
	 * Message: Please login after changing your temporary password.
	 */
	PLEASE_LOGIN_AFTER_CHANGING_YOUR_TEMPORARY_PASSWORD(396),

	/**
	 * ID: 397<br>
	 * Message: Not a paid account.
	 */
	NOT_A_PAID_ACCOUNT(397),

	/**
	 * ID: 398<br>
	 * Message: There is no time left on this account.
	 */
	THERE_IS_NO_TIME_LEFT_ON_THIS_ACCOUNT(398),

	/**
	 * ID: 399<br>
	 * Message: System error.
	 */
	SYSTEM_ERROR(399),
	
	/**
	 * ID: 400<br>
	 * Message: You are attempting to drop $s1.  Do you wish to continue?
	 */
	YOU_ARE_ATTEMPTING_TO_DROP_S1_DO_YOU_WISH_TO_CONTINUE(400),
	
	/**
	 * ID: 401<br>
	 * Message: You currently have too many quests in progress.
	 */
	YOU_CURRENTLY_HAVE_TOO_MANY_QUESTS_IN_PROGRESS(401),

	/**
	 * ID: 402<br>
	 * Message: You do not possess the correct ticket to board the boat.
	 */
	YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT(402),

	/**
	 * ID: 403<br>
	 * Message: You have exceeded your out-of-pocket adena limit.
	 */
	YOU_HAVE_EXCEEDED_YOUR_OUT_OF_POCKET_ADENA_LIMIT(403),
	
	/**
	 * ID: 404<br>
	 * Message: Your Create Item level is too low to register this recipe.
	 */
	YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE(404),
	
	/**
	 * ID: 405<br>
	 * Message: The total price of the product is too high.
	 */
	THE_TOTAL_PRICE_OF_THE_PRODUCT_IS_TOO_HIGH(405),
	
	/**
	 * ID: 406<br>
	 * Message: Petition application accepted.
	 */
	PETITION_APPLICATION_ACCEPTED(406),
	
	/**
	 * ID: 407<br>
	 * Message: Petition under process.
	 */
	PETITION_UNDER_PROCESS(407),
	
	/**
	 * ID: 408<br>
	 * Message: Set Period
	 */
	SET_PERIOD(408),
	
	/**
	 * ID: 409<br>
	 * Message: Set Time-$s1: $s2: $s3
	 */
	SET_TIME_S1_S2_S3(409),
	
	/**
	 * ID: 410<br>
	 * Message: Registration Period
	 */
	REGISTRATION_PERIOD(410),
	
	/**
	 * ID: 411<br>
	 * Message: Registration TIme-$s1: $s2: $s3
	 */
	REGISTRATION_TIME_S1_S2_S3(411),

	/**
	 * ID: 412<br>
	 * Message: Battle begins in $s1: $s2: $s4
	 */
	BATTLE_BEGINS_IN_S1_S2_S4(412),

	/**
	 * ID: 413<br>
	 * Message: Battle ends in $s1: $s2: $s5
	 */
	BATTLE_ENDS_IN_S1_S2_S5(413),

	/**
	 * ID: 414<br>
	 * Message: Standby
	 */
	STANDBY(414),

	/**
	 * ID: 415<br>
	 * Message: Under Siege
	 */
	UNDER_SIEGE(415),

	/**
	 * ID: 416<br>
	 * Message: This item cannot be exchanged.
	 */
	THIS_ITEM_CANNOT_BE_EXCHANGED(416),
	
	/**
	 * ID: 417<br>
	 * Message: $s1 has been disarmed.
	 */
	S1_HAS_BEEN_DISARMED(417),
	
	/**
	 * ID: 418<br>
	 * Message: There is a significant difference between the item's price and its standard price. Please check again.
	 */
	THERE_IS_A_SIGNIFICANT_DIFFERENCE_BETWEEN_THE_ITEM_S_PRICE_AND_ITS_STANDARD_PRICE(418),

	/**
	 * ID: 419<br>
	 * Message: $s1 minute(s) of usage time left.
	 */
	S1_MINUTE_OF_USAGE_TIME_LEFT(419),

	/**
	 * ID: 420<br>
	 * Message: Time expired.
	 */
	TIME_EXPIRED(420),
	
	/**
	 * ID: 421<br>
	 * Message: Another person has logged in with the same account.
	 */
	ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT(421),
	
	/**
	 * ID: 422<br>
	 * Message: You have exceeded the weight limit.
	 */
	YOU_HAVE_EXCEED_THE_WEIGHT_LIMIT(422),
	
	/**
	 * ID: 423<br>
	 * Message: You have cancelled the enchanting process.
	 */
	YOU_HAVE_CANCELLED_THE_ENCHANTING_PROCESS(423),
	
	/**
	 * ID: 424<br>
	 * Message: Does not fit strengthening conditions of the scroll.
	 */
	DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL(424),

	/**
	 * ID: 425<br>
	 * Message: Your Create Item level is too low to register this recipe.
	 */
	YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE_2(425),

	/**
	 * ID: 426<br>
	 * Message: Your account has been reported for intentionally not paying the cyber cafÃ© fees.
	 */
	YOUR_ACCOUNT_HAS_BEEN_REPORTED_FOR_INTENTIONALLY_NOT_PAYING_THE_CYBER_CAFE_FEES(426),

	/**
	 * ID: 427<br>
	 * Message: Please contact us.
	 */
	PLEASE_CONTACT_US(427),

	/**
	 * ID: 428<br>
	 * Message: In accordance with company policy, your account has been suspended due to suspicion of illegal use and/or misappropriation of another player's data. Details of the incident(s) in question have been sent to the email address on file with the company. If you are not directly involved with the reported conduct, visit the PlayNC website (http://http://www.plaync.com/us/support/) and go to the Account Appropriation Report Center (Lineage II) to submit an appeal.
	 */
	IN_ACCORDANCE_WITH_COMPANY_POLICY_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_SUSPICIO(428),

	/**
	 * ID: 429<br>
	 * Message: In accordance with company policy, your account has been suspended due to falsely reporting a misappropriation. Submitting an irrelevant report to the Report Center may harm other players. For more information on account suspension, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	IN_ACCORDANCE_WITH_COMPANY_POLICY_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_FALSELY_(429),

	/**
	 * ID: 430<br>
	 * Message: u,ë²ˆì—­ë¶ˆí•„ìš”  (Doesn't need to translate.)
	 */
	DOESN_T_NEED_TO_TRANSLATE(430),
	
	/**
	 * ID: 431<br>
	 * Message: Your account has been suspended due to violating the EULA, RoC and/or User Agreement. {Chapter 4, Section 17 of the End User Licence Agreement (Limiting Service Use) : When a user violates the terms of the User Agreement, the company can impose a restriction on the applicable user's account.} For more information on account suspension, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_VIOLATING_THE_EULA_ROC_ANDOR_USER_AGREEMEN(431),

	/**
	 * ID: 432<br>
	 * Message: Your account has been suspended for 7 days (retroactive to the day of disclosure), under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for dealing or attempting to deal items or characters (accounts) within the game in exchange for cash/spots/items of other games. Suspension of your account will automatically expire after 7 days. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_7_DAYS_RETROACTIVE_TO_THE_DAY_OF_DISCLOSURE_U(432),

	/**
	 * ID: 433<br>
	 * Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for dealing or attempting to deal items or characters (accounts) within the game in exchange for cash/spots/items of other games. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV(433),

	/**
	 * ID: 434<br>
	 * Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for unethical behavior or fraud. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV_2(434),

	/**
	 * ID: 435<br>
	 * Message: Your account has been suspended, under Chapter 3 Section 14 of the Lineage II Service Use Agreement, for unethical behavior. For more information, please visit the PlayNC website (http://www.plaync.com/us/support/) and use our Support Center's 1 on 1 inquiry.
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV_3(435),

	/**
	 * ID: 436<br>
	 * Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for abusing the game system or exploiting bug(s). Abusing bug(s) may cause critical situations as well as harm the game world's balance. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV_4(436),

	/**
	 * ID: 437<br>
	 * Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for using illegal software which has not been authenticated by our company. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV_5(437),

	/**
	 * ID: 438<br>
	 * Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for impersonating an official Game Master or staff member. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERV_6(438),

	/**
	 * ID: 439<br>
	 * Message: In accordance with the company's User Agreement and Operational Policy this account has been suspended at the account holder's request. If you have any questions regarding your account please contact support at http://support.plaync.com
	 */
	IN_ACCORDANCE_WITH_THE_COMPANY_S_USER_AGREEMENT_AND_OPERATIONAL_POLICY_THIS_ACCOU(439),

	/**
	 * ID: 440<br>
	 * Message: Because you are registered as a minor, your account has been suspended at the request of your parents or guardian. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	BECAUSE_YOU_ARE_REGISTERED_AS_A_MINOR_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_AT_THE_REQU(440),

	/**
	 * ID: 441<br>
	 * Message: Per our company's User Agreement, the use of this account has been suspended. If you have any questions regarding your account please contact support at http://support.plaync.com.
	 */
	PER_OUR_COMPANY_S_USER_AGREEMENT_THE_USE_OF_THIS_ACCOUNT_HAS_BEEN_SUSPENDED_IF_YO(441),

	/**
	 * ID: 442<br>
	 * Message: Your account has been suspended, under Chapter 2, Section 7 of the Lineage II Service Use Agreement, for misappropriating payment under another player's account. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_2_SECTION_7_OF_THE_LINEAGE_II_SERVI(442),

	/**
	 * ID: 443<br>
	 * Message: The identity of this account has not been veen verified. Therefore, Lineage II service for this account is currently unavailable. To verify your identity, please fax a copy of your social security card, driver's license, passport, medical insurance card, etc. to 02-2186-3282. Include your account, name and contact information. For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	THE_IDENTITY_OF_THIS_ACCOUNT_HAS_NOT_BEEN_VEEN_VERIFIED_THEREFORE_LINEAGE_II_SERV(443),

	/**
	 * ID: 444<br>
	 * Message: Since we have received a withdrawal request from the holder of this account access to all applicable accounts has been automatically suspended.
	 */
	SINCE_WE_HAVE_RECEIVED_A_WITHDRAWAL_REQUEST_FROM_THE_HOLDER_OF_THIS_ACCOUNT_ACCES(444),

	/**
	 * ID: 445<br>
	 * Message: (Reference Number Regarding Membership Withdrawal Request: $s1)
	 */
	REFERENCE_NUMBER_REGARDING_MEMBERSHIP_WITHDRAWAL_REQUEST_S1(445),

	/**
	 * ID: 446<br>
	 * Message: For more information, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	FOR_MORE_INFORMATION_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_PLAYNC_WEBSITE_HTTP(446),

	/**
	 * ID: 447<br>
	 * Message: .
	 */
	DOT(447),

	/**
	 * ID: 448<br>
	 * Message: System error, please log in again later.
	 */
	SYSTEM_ERROR_PLEASE_LOG_IN_AGAIN_LATER(448),

	/**
	 * ID: 449<br>
	 * Message: Password does not match this account.
	 */
	PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT(449),

	/**
	 * ID: 450<br>
	 * Message: Confirm your account information and log in again later.
	 */
	CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_LOG_IN_AGAIN_LATER(450),

	/**
	 * ID: 451<br>
	 * Message: The password you have entered is incorrect.
	 */
	THE_PASSWORD_YOU_HAVE_ENTERED_IS_INCORRECT(451),

	/**
	 * ID: 452<br>
	 * Message: Please confirm your account information and try logging in again.
	 */
	PLEASE_CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_TRY_LOGGING_IN_AGAIN(452),

	/**
	 * ID: 453<br>
	 * Message: Your account information is incorrect.
	 */
	YOUR_ACCOUNT_INFORMATION_IS_INCORRECT(453),

	/**
	 * ID: 454<br>
	 * Message: For more details, please contact our Support Center at http://support.plaync.com
	 */
	FOR_MORE_DETAILS_PLEASE_CONTACT_OUR_SUPPORT_CENTER_AT_HTTP_SUPPORT_PLAYNC_COM(454),

	/**
	 * ID: 455<br>
	 * Message: This account is already in use.  Access denied.
	 */
	THIS_ACCOUNT_IS_ALREADY_IN_USE_ACCESS_DENIED(455),

	/**
	 * ID: 456<br>
	 * Message: Lineage II game services may be used by individuals 15 years of age or older except for PvP servers, which may only be used by adults 18 years of age and older. (Korea Only)
	 */
	LINEAGE_II_GAME_SERVICES_MAY_BE_USED_BY_INDIVIDUALS_15_YEARS_OF_AGE_OR_OLDER_EXCE(456),

	/**
	 * ID: 457<br>
	 * Message: Server under maintenance. Please try again later.
	 */
	SERVER_UNDER_MAINTENANCE_PLEASE_TRY_AGAIN_LATER(457),

	/**
	 * ID: 458<br>
	 * Message: Your usage term has expired.
	 */
	YOUR_USAGE_TERM_HAS_EXPIRED(458),

	/**
	 * ID: 459<br>
	 * Message: PlayNC website (http://www.plaync.com/us/support/)
	 */
	PLAYNC_WEBSITE_HTTP_WWW_PLAYNC_COMUSSUPPORT(459),

	/**
	 * ID: 460<br>
	 * Message: to reactivate your account.
	 */
	TO_REACTIVATE_YOUR_ACCOUNT(460),

	/**
	 * ID: 461<br>
	 * Message: Access failed.
	 */
	ACCESS_FAILED(461),

	/**
	 * ID: 462<br>
	 * Message: Please try again later.
	 */
	PLEASE_TRY_AGAIN_LATER_2(462),

	/**
	 * ID: 463<br>
	 * Message: .
	 */
	DOT_2(463),
	
	/**
	 * ID: 464<br>
	 * Message: This feature is only available alliance leaders.
	 */
	THIS_FEATURE_IS_ONLY_AVAILABLE_ALLIANCE_LEADERS(464),
	
	/**
	 * ID: 465<br>
	 * Message: You are not currently allied with any clans.
	 */
	YOU_ARE_NOT_CURRENT_ALLIED_WITH_ANY_CLANS(465),
	
	/**
	 * ID: 466<br>
	 * Message: You have exceeded the limit.
	 */
	YOU_HAVE_EXCEEDED_THE_LIMIT(466),
	
	/**
	 * ID: 467<br>
	 * Message: You may not accept any clan within a day after expelling another clan.
	 */
	YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN(467),
	
	/**
	 * ID: 468<br>
	 * Message: A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.
	 */
	A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_ALLIANCIE_WIHING_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION(468),
	
	/**
	 * ID: 469<br>
	 * Message: You may not ally with a clan you are currently at war with. That would be diabolical and treacherous.
	 */
	YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH(469),
	
	/**
	 * ID: 470<br>
	 * Message: Only the clan leader may apply for withdrawal from the alliance.
	 */
	ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE(470),
	
	/**
	 * ID: 472<br>
	 * Message: You cannot expel yourself from the clan.
	 */
	YOU_CANNOT_EXPEL_YOURSELF_FROM_THE_CLAN(472),
	
	/**
	 * ID: 471<br>
	 * Message: Alliance leaders cannot withdraw.
	 */
	ALLIANCE_LEADERS_CANNOT_WITHDRAW(471),
	
	/**
	 * ID: 473<br>
	 * Message: Different alliance.
	 */
	DIFFERENT_ALLIANCE(473),
	
	/**
	 * ID: 474<br>
	 * Message: That clan does not exist.
	 */
	THAT_CLAN_DOES_NOT_EXIST(474),
	
	/**
	 * ID: 475<br>
	 * Message: Different alliance.
	 */
	DIFFERENT_ALLIANCE_2(475),

	/**
	 * ID: 476<br>
	 * Message: Please adjust the image size to 8x12.
	 */
	PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12(476),
	
	/**
	 * ID: 477<br>
	 * Message: No response. Invitation to join an alliance has been cancelled.
	 */
	NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED(477),
	
	/**
	 * ID: 478<br>
	 * Message: No response. Your entrance to the alliance has been cancelled.
	 */
	NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED(478),
	
	/**
	 * ID: 479<br>
	 * Message: $s1 has joined as a friend.
	 */
	S1_JOINED_AS_FRIEND(479),
	
	/**
	 * ID: 480<br>
	 * Message: Please check your friends list.
	 */
	PLEASE_CHECK_YOUR_FRIENDS_LIST(480),
	
	/**
	 * ID: 481<br>
	 * Message: $s1 has been deleted from your friends list.
	 */
	S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST(481),
	
	/**
	 * ID: 482<br>
	 * Message: You cannot add yourself to your own friend list.
	 */
	YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIENDS_LIST(482),
	
	/**
	 * ID: 483<br>
	 * Message: This function is inaccessible right now.  Please try again later.
	 */
	THIS_FUNCTION_IS_INACCESSIBLE_RIGHT_NOW_PLEASE_TRY_AGAIN_LATER(483),
	
	/**
	 * ID: 484<br>
	 * Message: This player is already registered in your friends list.
	 */
	THIS_PLAYER_IS_ALREADY_REGISTERED_IN_YOUR_FRIENDS_LIST(484),
	
	/**
	 * ID: 485<br>
	 * Message: No new friend invitations may be accepted.
	 */
	NO_NEW_FRIEND_INVITATION_MAY_BE_ACCEPTED(485),
	
	/**
	 * ID: 486<br>
	 * Message: The following user is not in your friends list.
	 */
	THE_FOLLOWING_USER_IS_NOT_IN_YOUR_FRIEND_LIST(486),
	
	/**
	 * ID: 487<br>
	 * Message: ======<Friends List>======
	 */
	FRIEND_LIST_HEAD(487),
	
	/**
	 * ID: 488<br>
	 * Message: $s1 (Currently: Online)
	 */
	S1_CURRENTLY_ONLINE(488),
	
	/**
	 * ID: 489<br>
	 * Message: $s1 (Currently: Offline)
	 */
	S1_CURRENTLY_OFFLINE(489),
	
	/**
	 * ID: 490<br>
	 * Message: ========================
	 */
	FOOTER(490),
	
	/**
	 * ID: 490<br>
	 * Message: ========================
	 */
	FOOTER_2(490),
	
	/**
	 * ID: 491<br>
	 * Message: =======<Alliance Information>=======
	 */
	ALLIANCE_INFO_HEAD(491),
	
	/**
	 * ID: 492<br>
	 * Message: Alliance Name: $s1
	 */
	ALLIANCE_NAME_S1(492),
	
	/**
	 * ID: 493<br>
	 * Message: Connection: $s1 / Total $s2
	 */
	CONNECTION_S1_TOTAL_S2(493),
	
	/**
	 * ID: 494<br>
	 * Message: Alliance Leader: $s2 of $s1
	 */
	ALLIANCE_LEADER_S2_OF_S1(494),
	
	/**
	 * ID: 495<br>
	 * Message: Affiliated clans: Total $s1 clan(s)
	 */
	AFFILIATED_CLANS_TOTAL_S1_CLANS(495),
	
	/**
	 * ID: 496<br>
	 * Message: =====<Clan Information>=====
	 */
	CLAN_INFO_HEAD(496),
	
	/**
	 * ID: 497<br>
	 * Message: Clan Name: $s1
	 */
	CLAN_NAME_S1(497),
	
	/**
	 * ID: 498<br>
	 * Message: Clan Leader: $s1
	 */
	CLAN_LEADER_S1(498),
	
	/**
	 * ID: 499<br>
	 * Message: Clan Level: $s1
	 */
	CLAN_LEVEL_S1(499),
	
	/**
	 * ID: 500<br>
	 * Message: ------------------------
	 */
	SEPARATOR(500),
	
	/**
	 * ID: 501<br>
	 * Message: ========================
	 */
	FOOTER_3(501),
	
	/**
	 * ID: 502<br>
	 * Message: You already belong to another alliance.
	 */
	YOU_ALREADY_JOINED_TO_ANOTHER_ALLIANNCE(502),
	
	/**
	 * ID: 503<br>
	 * Message: $s1 (Friend) has logged in.
	 */
	S1_FRIEND_HAS_LOGGED_IN(503),
	
	/**
	 * ID: 504<br>
	 * Message: Only clan leaders may create alliances.
	 */
	ONLY_CLAN_LEADER_MAY_CREATE_ALLIANNCES(504),
	
	/**
	 * ID: 505<br>
	 * Message: You cannot create a new alliance within 10 days after dissolution.
	 */
	YOU_CANNOT_CREATE_NEW_ALLIANCE_WITHIN_10_DAYS_AFTER_DISSOLUTION(505),
	
	/**
	 * ID: 506<br>
	 * Message: Incorrect alliance name. Please try again.
	 */
	INCORRECT_ALLIANCE_NAME(506),
	
	/**
	 * ID: 507<br>
	 * Message: Incorrect length for an alliance name.
	 */
	INCORRECTT_LENGTH_FOR_AN_ALLIANCE_NAME(507),
	
	/**
	 * ID: 508<br>
	 * Message: This alliance name already exists.
	 */
	THE_ALLIANCE_NAME_ALREADY_EXISTS(508),
	
	/**
	 * ID: 509<br>
	 * Message: Cannot accept. clan ally is registered as an enemy during siege battle.
	 */
	CANNOT_ACCEPT_CLAN_ALLY_IS_REGISTERED_AS_ENEMY_DURING_SIEGE_BATTLE(509),
	
	/**
	 * ID: 510<br>
	 * Message: You have invited someone to your alliance.
	 */
	YOU_HAVE_INVITED_SOMEONE_TO_YOUR_ALLIANCE(510),
	
	/**
	 * ID: 511<br>
	 * Message: You must first select a user to invite.
	 */
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE(511),
	
	/**
	 * ID: 512<br>
	 * Message: Do you really wish to withdraw from the alliance?
	 */
	DO_YOU_REALLY_WISH_TO_WITHDRAW_FROM_THE_ALLIANCE(512),
	
	/**
	 * ID: 513<br>
	 * Message: Enter the name of the clan you wish to expel.
	 */
	ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_EXPEL(513),
	
	/**
	 * ID: 514<br>
	 * Message: Do you really wish to dissolve the alliance?
	 */
	DO_YOU_REALLY_WISH_TO_DISOLVE_THE_ALLIANCE(514),
	
	/**
	 * ID: 515<br>
	 * Message: Enter a file name for the alliance crest.
	 */
	ENTER_A_FILE_NAME_FOR_THE_ALLIANCE_CREST(515),
	
	/**
	 * ID: 516<br>
	 * Message: $s1 has invited you to be their friend.
	 */
	S1_HAS_INVITED_YOU_TO_BE_THEIR_FRIEND(516),
	
	/**
	 * ID: 517<br>
	 * Message: You have accepted the alliance.
	 */
	YOU_ACCEPTED_THE_ALLIANCE(517),
	
	/**
	 * ID: 518<br>
	 * Message: You have failed to invite a clan into the alliance.
	 */
	YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_ALLIANCE(518),
	
	/**
	 * ID: 519<br>
	 * Message: You have withdrawn from the alliance.
	 */
	YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE(519),
	
	/**
	 * ID: 520<br>
	 * Message: You have failed to withdraw from the alliance.
	 */
	YOU_HAVE_FAILED_TO_WITHDRAWN_FROM_THE_ALLIANCE(520),
	
	/**
	 * ID: 521<br>
	 * Message: You have succeeded in expelling a clan.
	 */
	YOU_HAVE_SUCCEEDED_IN_EXPELLING_A_CLAN(521),
	
	/**
	 * ID: 522<br>
	 * Message: You have failed to expel a clan.
	 */
	YOU_HAVE_FAILED_TO_EXPEL_A_CLAN(522),
	
	/**
	 * ID: 523<br>
	 * Message: The alliance has been dissolved.
	 */
	THE_ALLIANCE_HAS_BEEN_DISSOLVED(523),
	
	/**
	 * ID: 524<br>
	 * Message: You have failed to dissolve the alliance.
	 */
	YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE(524),
	
	/**
	 * ID: 525<br>
	 * Message: You have succeeded in inviting a friend to your friends list.
	 */
	YOU_HAVE_SUCCEEDED_INVITING_A_FRIEND_TO_YOUR_FRIENDS_LIST(525),
	
	/**
	 * ID: 526<br>
	 * Message: You have failed to add a friend to your friends list.
	 */
	YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST(526),
	
	/**
	 * ID: 527<br>
	 * Message: $s1 leader, $s2, has requested an alliance.
	 */
	S1_LEADER_S2_HAS_REQUESTED_AN_ALLIANCE(527),
	
	/**
	 * ID: 528<br>
	 * Message: Unable to find file at target location.
	 */
	UNABLE_TO_FIND_AT_TARGET_LOCATION(528),
	
	/**
	 * ID: 529<br>
	 * Message: You may only register an 8 x 12 pixel, 256-color BMP.
	 */
	YOU_MAY_ONLY_REGISTER_AN_8_X_12_PIXEL_256_COLOR_BMP(529),
	
	/**
	 * ID: 530<br>
	 * Message: The Spiritshot does not match the weapon's grade.
	 */
	THE_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE(530),
	
	/**
	 * ID: 531<br>
	 * Message: You do not have enough Spiritshots for that.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOTS_FOR_THAT(531),
	
	/**
	 * ID: 532<br>
	 * Message: You may not use Spiritshots.
	 */
	YOU_MAY_NOT_USE_SPIRITSHOTS(532),
	
	/**
	 * ID: 533<br>
	 * Message: Power of Mana enabled.
	 */
	POWER_OF_MANA_ENABLED(533),
	
	/**
	 * ID: 534<br>
	 * Message: Power of Mana disabled.
	 */
	POWER_OF_MANA_DISABLED(534),
	
	/**
	 * ID: 535<br>
	 * Message: Enter a name for your pet.
	 */
	ENTER_A_NAME_FOR_YOUR_PET(535),
	
	/**
	 * ID: 536<br>
	 * Message: How much adena do you wish to transfer to your Inventory?
	 */
	HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_INVENTORY(536),
	
	/**
	 * ID: 537<br>
	 * Message: How much will you transfer?
	 */
	HOW_MUCH_WILL_YOU_TRANSFER(537),
	
	/**
	 * ID: 538<br>
	 * Message: Your SP has decreased by $s1.
	 */
	YOUR_SP_HAS_DECREASED_BY_S1(538),
	
	/**
	 * ID: 539<br>
	 * Message: Your Experience has decreased by $s1.
	 */
	YOU_EXPERIENCE_HAS_DECREASED_BY_S1(539),
	
	/**
	 * ID: 540<br>
	 * Message: Clan leaders may not be deleted. Dissolve the clan first and try again.
	 */
	CLAN_LEADERS_MAY_NOT_BE_DELETED_DISSOLVE_THE_CLAN_FIRST_AND_TRY_AGAIN(540),
	
	/**
	 * ID: 541<br>
	 * Message: You may not delete a clan member. Withdraw from the clan first and try again.
	 */
	YOU_MAY_NOT_DELETE_A_CLAN_MEMBER_WITHDRAW_FROM_THE_CLAN_FIRST_AND_TRY_AGAIN(541),
	
	/**
	 * ID: 542<br>
	 * Message: The NPC server is currently down.  Pets and servitors cannot be summoned at this time.
	 */
	THE_NPC_SERVER_IS_CURRENTLY_DOWN_PETS_AND_SERVITORS_CANNOT_BE_SUMMONED_AT_THIS_TI(542),
	
	/**
	 * ID: 543<br>
	 * Message: You already have a pet.
	 */
	YOU_ALREADY_HAVE_A_PET(543),
	
	/**
	 * ID: 544<br>
	 * Message: Your pet cannot carry this item.
	 */
	YOU_PET_CANNOT_CARRY_THIS_ITEM(544),
	
	/**
	 * ID: 545<br>
	 * Message: Your pet cannot carry any more items. Remove some, then try again.
	 */
	YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_REMOVE_SOME_THEN_TRY_AGAIN(545),

	/**
	 * ID: 546<br>
	 * Message: Unable to place item, your pet is too encumbered.
	 */
	UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED(546),
	
	/**
	 * ID: 547<br>
	 * Message: Summoning your pet!
	 */
	SUMMONING_YOUR_PET(547),
	
	/**
	 * ID: 548<br>
	 * Message: Your pet's name can be up to 8 characters in length.
	 */
	YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH(548),
	
	/**
	 * ID: 549<br>
	 * Message: To create an alliance, your clan must be Level 5 or higher.
	 */
	TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER(549),
	
	/**
	 * ID: 550<br>
	 * Message: You may not create an alliance during the term of dissolution postponement.
	 */
	YOU_MAY_NOT_CREATTE_AN_ALLIANCE_DURING_THE_TERM_OF_DISSOLUTION_POSTPONEMENT(550),
	
	/**
	 * ID: 551<br>
	 * Message: You cannot raise your clan level during the term of dispersion postponement.
	 */
	YOU_CANNOT_RAISE_YOUR_CLAN_LEVEL_DURING_THE_TERM_OF_DISPERSION_POSTPONEMENT(551),
	
	/**
	 * ID: 552<br>
	 * Message: During the grace period for dissolving a clan, the registration or deletion of a clan's crest is not allowed.
	 */
	DURING_THE_GRACE_PERIOD_FOR_DISSOLVING_A_CLAN_THE_REGISTRATION_OR_DELETION_OF_A_CLANS_CREST_IS_NOT_ALLOWED(552),
	
	/**
	 * ID: 553<br>
	 * Message: The opposing clan has applied for dispersion.
	 */
	THE_OPPOSING_CLAN_HAS_APPLIED_FOR_DISPERSION(553),
	
	/**
	 * ID: 554<br>
	 * Message: You cannot disperse the clans in your alliance.
	 */
	YOU_CANNOT_DISPERSE_THE_CLANS_YOUR_ALLIANCE(554),
	
	/**
	 * ID: 559<br>
	 * Message: You have purchased $s2 from $s1.
	 */
	YOU_HAVE_PURCHASED_S2_FROM_S1(559),
	
	/**
	 * ID: 560<br>
	 * Message: You have purchased +$s2 $s3 from $s1.
	 */
	YOU_HAVE_PURCHASED_PLUS_S2_S3_FROM_S1(560),
	
	/**
	 * ID: 561<br>
	 * Message: You have purchased $s3 $s2(s) from $s1.
	 */
	YOU_HAVE_PURCHASED_S3_S2S_FROM_S1(561),
	
	/**
	 * ID: 562<br>
	 * Message: You may not crystallize this item. Your crystallization skill level is too low.
	 */
	YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW(562),
	
	/**
	 * ID: 563<br>
	 * Message: Failed to disable attack target.
	 */
	FAILED_TO_DISABLE_ATTACK_TARGET(563),
	
	/**
	 * ID: 564<br>
	 * Message: Failed to change attack target.
	 */
	FAILED_TO_CHANGE_ATTACK_TARGET(564),

	/**
	 * ID: 565<br>
	 * Message: Not enough luck.
	 */
	NOT_ENOUGH_LUCK(565),

	/**
	 * ID: 566<br>
	 * Message: Your confusion spell failed.
	 */
	YOUR_CONFUSION_SPELL_FAILED(566),

	/**
	 * ID: 567<br>
	 * Message: Your fear spell failed.
	 */
	YOUR_FEAR_SPELL_FAILED(567),
	
	/**
	 * ID: 568<br>
	 * Message: Cubic Summoning failed.
	 */
	CUBIC_SUMMONING_FAILED(568),
	
	/**
	 * ID: 569<br>
	 * Message: Caution -- this item's price greatly differs from non-player run shops. Do you wish to continue?
	 */
	CAUTION_THIS_ITEM_S_PRICE_GREATLY_DIFFERS_FROM_NON_PLAYER_RUN_SHOPS_DO_YOU_WISH_T(569),
	
	/**
	 * ID: 570<br>
	 * Message: How many  $s1(s) do you want to purchase?
	 */
	HOW_MANY_S1_DO_YOU_WANT_TO_PURCHASE(570),

	/**
	 * ID: 571<br>
	 * Message: How many  $s1(s) do you want to purchase?
	 */
	HOW_MANY_S1_DO_YOU_WANT_TO_PURCHASE_2(571),
	
	/**
	 * ID: 572<br>
	 * Message: Do you wish to join $s1's party? (Item distribution: Finders Keepers)
	 */
	DO_YOU_WISH_TO_JOIN_S1S_PARTY_FINDERS_KEEPERS(572),
	
	/**
	 * ID: 573<br>
	 * Message: Do you wish to join $s1's party? (Item distribution: Random)
	 */
	DO_YOU_WISH_TO_JOIN_S1S_PARTY_RANDOM(573),
	
	/**
	 * ID: 574<br>
	 * Message: Pets and Servitors are not available at this time.
	 */
	PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME(574),
	
	/**
	 * ID: 575<br>
	 * Message: How much adena do you wish to transfer to your pet?
	 */
	HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_PET(575),

	/**
	 * ID: 576<br>
	 * Message: How much do you wish to transfer?
	 */
	HOW_MUCH_DO_YOU_WISH_TO_TRANSFER(576),

	/**
	 * ID: 577<br>
	 * Message: You cannot summon during a trade or while using the private shops.
	 */
	YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS(577),
	
	/**
	 * ID: 578<br>
	 * Message: You cannot summon during combat.
	 */
	YOU_CANNOT_SUMMON_DURING_COMBAT(578),
	
	/**
	 * ID: 579<br>
	 * Message: A pet cannot be sent back during battle.
	 */
	A_PET_CANNOT_BE_SENT_BACK_DURING_BATTLE(579),
	
	/**
	 * ID: 580<br>
	 * Message: You may not use multiple pets or servitors at the same time.
	 */
	YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME(580),
	
	/**
	 * ID: 581<br>
	 * Message: There is a space in the name.
	 */
	THERE_IS_A_SPACE_IN_THE_GAME(581),
	
	/**
	 * ID: 582<br>
	 * Message: Inappropriate character name.
	 */
	INNAPROPIATE_CHARACTER_NAME(582),
	
	/**
	 * ID: 583<br>
	 * Message: Name includes forbidden words.
	 */
	NAME_INCLUDES_FORBIDDEN_WORDS(583),
	
	/**
	 * ID: 584<br>
	 * Message: This is already in use by another pet.
	 */
	THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET(584),
	
	/**
	 * ID: 585<br>
	 * Message: Please decide on the price.
	 */
	PLEASE_DECIDE_ON_THE_PRICE(585),

	/**
	 * ID: 586<br>
	 * Message: Pet items cannot be registered as shortcuts.
	 */
	PET_ITEMS_CANNOT_BE_REGISTERED_AS_SHORTCUTS(586),

	/**
	 * ID: 587<br>
	 * Message: Irregular system speed.
	 */
	IRREGULAR_SYSTEM_SPEED(587),

	/**
	 * ID: 588<br>
	 * Message: Your pet's inventory is full.
	 */
	YOUR_PET_S_INVENTORY_IS_FULL(588),
	
	/**
	 * ID: 589<br>
	 * Message: A dead pet cannot be sent back.
	 */
	A_DEAD_PET_CANNOT_SENT_BACK(589),
	
	/**
	 * ID: 590<br>
	 * Message: Your pet is motionless and any attempt you make to give it something goes unrecognized.
	 */
	YOUR_PET_IS_MOTIONLESS_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED(590),
	
	/**
	 * ID: 591<br>
	 * Message: An invalid character is included in the pet's name.
	 */
	AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PETS_NAME(591),
	
	/**
	 * ID: 592<br>
	 * Message: Do you wish to dismiss your pet? Dismissing your pet will cause the pet necklace to disappear.
	 */
	DO_YOU_WISH_TO_DISMISS_YOUR_PET_DISMISSING_YOUR_PET_WILL_CAUSE_THE_PET_NECKLACE_T(592),

	/**
	 * ID: 593<br>
	 * Message: Starving, grumpy and fed up, your pet has left.
	 */
	STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT(593),
	
	/**
	 * ID: 594<br>
	 * Message: You may not restore a hungry pet.
	 */
	YOU_MAY_NOT_RESTORE_A_HUNGRY_PET(594),
	
	/**
	 * ID: 595<br>
	 * Message: Your pet is very hungry.
	 */
	YOUR_PET_IS_VERY_HUNGRY(595),

	/**
	 * ID: 596<br>
	 * Message: Your pet ate a little, but is still hungry.
	 */
	YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY(596),

	/**
	 * ID: 597<br>
	 * Message: Your pet is very hungry. Please be careful.
	 */
	YOUR_PET_IS_VERY_HUNGRY_PLEASE_BE_CAREFUL(597),

	/**
	 * ID: 598<br>
	 * Message: You may not chat while you are invisible.
	 */
	YOU_MAY_NOT_CHAT_WHILE_YOU_ARE_INVISIBLE(598),

	/**
	 * ID: 599<br>
	 * Message: The GM has an imprtant notice. Chat has been temporarily disabled.
	 */
	THE_GM_HAS_AN_IMPRTANT_NOTICE_CHAT_HAS_BEEN_TEMPORARILY_DISABLED(599),
	
	/**
	 * ID: 600<br>
	 * Message: You may not equip a pet item.
	 */
	YOU_MAY_NOT_EQUIP_A_PET_ITEM(600),
	
	/**
	 * ID: 601<br>
	 * Message: There are $S1 petitions currently on the waiting list.
	 */
	THERE_ARE_S1_PETITIONS_CURRENTLY_ON_THE_WAITING_LIST(601),
	
	/**
	 * ID: 602<br>
	 * Message: The petition system is currently unavailable. Please try again later.
	 */
	THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER(602),
	
	/**
	 * ID: 603<br>
	 * Message: That item cannot be discarded or exchanged.
	 */
	THIS_ITEMM_CANNOT_BE_DISCARDED_OR_EXCHANGED(603),
	
	/**
	 * ID: 604<br>
	 * Message: You may not call forth a pet or summoned creature from this location.
	 */
	YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION(604),

	/**
	 * ID: 605<br>
	 * Message: You may register up to 64 people on your list.
	 */
	YOU_MAY_REGISTER_UP_TO_64_PEOPLE_ON_YOUR_LIST(605),

	/**
	 * ID: 606<br>
	 * Message: You cannot be registered because the other person has already registered 64 people on his/her list.
	 */
	YOU_CANNOT_BE_REGISTERED_BECAUSE_THE_OTHER_PERSON_HAS_ALREADY_REGISTERED_64_PEOPL(606),
	
	/**
	 * ID: 607<br>
	 * Message: You do not have any further skills to learn. Come back when you have reached Level $s1.
	 */
	YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_REACHED_LEVEL_S1(607),
	
	/**
	 * ID: 608<br>
	 * Message: $s1 has obtained $s3 $s2 by using Sweeper.
	 */
	S1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER(608),
	
	/**
	 * ID: 609<br>
	 * Message: $s1 has obtained $s2 by using Sweeper.
	 */
	S1_HAS_OBTAINED_S2_BY_USING_SWEEPER(609),
	
	/**
	 * ID: 610<br>
	 * Message: Your skill has been canceled due to lack of HP.
	 */
	YOU_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP(610),
	
	/**
	 * ID: 611<br>
	 * Message: You have succeeded in Confusing the enemy.
	 */
	YOU_HAVE_SUCCEEDED_IN_CONFUSING_THE_ENEMY(611),
	
	/**
	 * ID: 612<br>
	 * Message: The Spoil condition has been activated.
	 */
	THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED(612),
	
	/**
	 * ID: 613<br>
	 * Message: ======<Ignore List>======
	 */
	IGNORE_LIST_HEAD(613),
	
	/**
	 * ID: 614<br>
	 * Message: $s1 $s2
	 */
	S1_S2(614),
	
	/**
	 * ID: 615<br>
	 * Message: You have failed to register the user to your Ignore List.
	 */
	YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST(615),
	
	/**
	 * ID: 616<br>
	 * Message: You have failed to delete the character.
	 */
	YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_2(616),
	
	/**
	 * ID: 617<br>
	 * Message: $s1 has been added to your Ignore List.
	 */
	S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST(617),
	
	/**
	 * ID: 618<br>
	 * Message: $s1 has been removed from your Ignore List.
	 */
	S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST(618),
	
	/**
	 * ID: 619<br>
	 * Message: $s1 has placed you on his/her Ignore List.
	 */
	S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST(619),
	
	/**
	 * ID: 620<br>
	 * Message: $s1  has placed you on his/her Ignore List.
	 */
	S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST_2(620),
	
	/**
	 * ID: 621<br>
	 * Message: This server is reserved for players in Korea.  To play Lineage II, please connect to the server in your region.
	 */
	THIS_SERVER_IS_RESERVED_FOR_PLAYERS_IN_KOREA_TO_PLAY_LINEAGE_II_PLEASE_CONNECT_TO(621),

	/**
	 * ID: 622<br>
	 * Message: You may not make a declaration of war during an alliance battle.
	 */
	YOU_MAY_NOT_MAKE_A_DECLARATION_OF_WAR_DURING_AN_ALLIANCE_BATTLE(622),

	/**
	 * ID: 623<br>
	 * Message: Your opponent has exceeded the number of simultaneous alliance battles allowed.
	 */
	YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED(623),

	/**
	 * ID: 624<br>
	 * Message: $s1 Clan leader is not currently connected to the game server.
	 */
	S1_CLAN_LEADER_IS_NOT_CURRENTLY_CONNECTED_TO_THE_GAME_SERVER(624),

	/**
	 * ID: 625<br>
	 * Message: Your request for Alliance Battle truce has been denied.
	 */
	YOUR_REQUEST_FOR_ALLIANCE_BATTLE_TRUCE_HAS_BEEN_DENIED(625),
	
	/**
	 * ID: 626<br>
	 * Message: The $s1 clan did not respond: war proclamation has been refused.
	 */
	THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED_2(626),
	
	/**
	 * ID: 627<br>
	 * Message: Clan battle has been refused because you did not respond to $s1 clan's war proclamation.
	 */
	CLAN_BATTLE_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLAN_S_WAR_PROCLAM(627),
	
	/**
	 * ID: 628<br>
	 * Message: You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.
	 */
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN(628),
	
	/**
	 * ID: 629<br>
	 * Message: Your opponent has exceeded the number of simultaneous alliance battles allowed.
	 */
	YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED_2(629),
	
	/**
	 * ID: 630<br>
	 * Message: War with the $s1 clan has begun.
	 */
	WAR_WITH_THE_S1_CLAN_HAS_BEGUN_2(630),
	
	/**
	 * ID: 631<br>
	 * Message: War with the $s1 clan is over.
	 */
	WAR_WITH_THE_S1_CLAN_IS_OVER(631),

	/**
	 * ID: 632<br>
	 * Message: You have won the war over the $s1 clan!
	 */
	YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN_2(632),

	/**
	 * ID: 633<br>
	 * Message: You have surrendered to the $s1 clan.
	 */
	YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN_2(633),

	/**
	 * ID: 634<br>
	 * Message: Your alliance leader has been slain. You have been defeated by the $s1 clan.
	 */
	YOUR_ALLIANCE_LEADER_HAS_BEEN_SLAIN_YOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN(634),

	/**
	 * ID: 635<br>
	 * Message: The time limit for the clan war has been exceeded. War with the $s1 clan is over.
	 */
	THE_TIME_LIMIT_FOR_THE_CLAN_WAR_HAS_BEEN_EXCEEDED_WAR_WITH_THE_S1_CLAN_IS_OVER(635),

	/**
	 * ID: 636<br>
	 * Message: You are not involved in a clan war.
	 */
	YOU_ARE_NOT_INVOLVED_IN_A_CLAN_WAR_2(636),

	/**
	 * ID: 637<br>
	 * Message: A clan ally has registered itself to the opponent.
	 */
	A_CLAN_ALLY_HAS_REGISTERED_ITSELF_TO_THE_OPPONENT(637),

	/**
	 * ID: 638<br>
	 * Message: You have already requested a Siege Battle.
	 */
	YOU_HAVE_ALREADY_REQUESTED_A_SIEGE_BATTLE(638),

	/**
	 * ID: 639<br>
	 * Message: Your application has been denied because you have already submitted a request for another Siege Battle.
	 */
	YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR(639),

	/**
	 * ID: 640<br>
	 * Message: You have failed to refuse castle defense aid.
	 */
	YOU_HAVE_FAILED_TO_REFUSE_CASTLE_DEFENSE_AID(640),

	/**
	 * ID: 641<br>
	 * Message: You have failed to approve castle defense aid.
	 */
	YOU_HAVE_FAILED_TO_APPROVE_CASTLE_DEFENSE_AID(641),
	
	/**
	 * ID: 642<br>
	 * Message: You are already registered to the attacker side and must cancel your registration before submitting your request.
	 */
	YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST(642),
	
	/**
	 * ID: 643<br>
	 * Message: You have already registered to the defender side and must cancel your registration before submitting your request.
	 */
	YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATIO(643),

	/**
	 * ID: 644<br>
	 * Message: You are not yet registered for the castle siege.
	 */
	YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE(644),

	/**
	 * ID: 645<br>
	 * Message: Only clans of level 4 or higher may register for a castle siege.
	 */
	ONLY_CLANS_OF_LEVEL_4_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE(645),

	/**
	 * ID: 646<br>
	 * Message: You do not have the authority to modify the castle defender list.
	 */
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST(646),

	/**
	 * ID: 647<br>
	 * Message: You do not have the authority to modify the siege time.
	 */
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME(647),

	/**
	 * ID: 648<br>
	 * Message: No more registrations may be accepted for the attacker side.
	 */
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE(648),

	/**
	 * ID: 649<br>
	 * Message: No more registrations may be accepted for the defender side.
	 */
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE(649),
	
	/**
	 * ID: 650<br>
	 * Message: You may not summon from your current location.
	 */
	YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION(650),
	
	/**
	 * ID: 651<br>
	 * Message: Place $s1 in the current location and direction. Do you wish to continue?
	 */
	PLACE_S1_IN_THE_CURRENT_LOCATION_AND_DIRECTION_DO_YOU_WISH_TO_CONTINUE(651),

	/**
	 * ID: 652<br>
	 * Message: The target of the summoned monster is wrong.
	 */
	THE_TARGET_OF_THE_SUMMONED_MONSTER_IS_WRONG(652),

	/**
	 * ID: 653<br>
	 * Message: You do not have the authority to position mercenaries.
	 */
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES(653),

	/**
	 * ID: 654<br>
	 * Message: You do not have the authority to cancel mercenary positioning.
	 */
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING(654),

	/**
	 * ID: 655<br>
	 * Message: Mercenaries cannot be positioned here.
	 */
	MERCENARIES_CANNOT_BE_POSITIONED_HERE(655),

	/**
	 * ID: 656<br>
	 * Message: This mercenary cannot be positioned anymore.
	 */
	THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE(656),

	/**
	 * ID: 657<br>
	 * Message: Positioning cannot be done here because the distance between mercenaries is too short.
	 */
	POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_S(657),

	/**
	 * ID: 658<br>
	 * Message: This is not a mercenary of a castle that you own and so you cannot cancel its positioning.
	 */
	THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POS(658),

	/**
	 * ID: 659<br>
	 * Message: This is not the time for siege registration and so registrations cannot be accepted or rejected.
	 */
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPT(659),

	/**
	 * ID: 660<br>
	 * Message: This is not the time for siege registration and so registration and cancellation cannot be done.
	 */
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_(660),
	
	/**
	 * ID: 661<br>
	 * Message: This character cannot be spoiled.
	 */
	THIS_CHARACTER_CANNOT_BE_SPOILED(661),
	
	/**
	 * ID: 662<br>
	 * Message: The other player is rejecting friend invitations.
	 */
	THE_OTHER_PLAYER_IS_REJECTING_FRIEND_INVITATIONS(662),
	
	/**
	 * ID: 663<br>
	 * Message: The siege time has been declared for $s. It is not possible to change the time after a siege time has been declared. Do you want to continue?
	 */
	THE_SIEGE_TIME_HAS_BEEN_DECLARED_FOR_S_IT_IS_NOT_POSSIBLE_TO_CHANGE_THE_TIME_AFTE(663),

	/**
	 * ID: 664<br>
	 * Message: Please choose a person to receive.
	 */
	PLEASE_CHOOSE_A_PERSON_TO_RECEIVE(664),

	/**
	 * ID: 665<br>
	 * Message: $s2 of $s1 alliance is applying for alliance war. Do you want to accept the challenge?
	 */
	S2_OF_S1_ALLIANCE_IS_APPLYING_FOR_ALLIANCE_WAR_DO_YOU_WANT_TO_ACCEPT_THE_CHALLENG(665),

	/**
	 * ID: 666<br>
	 * Message: A request for ceasefire has been received from $s1 alliance. Do you agree?
	 */
	A_REQUEST_FOR_CEASEFIRE_HAS_BEEN_RECEIVED_FROM_S1_ALLIANCE_DO_YOU_AGREE(666),

	/**
	 * ID: 667<br>
	 * Message: You are registering on the attacking side of the $s1 siege. Do you want to continue?
	 */
	YOU_ARE_REGISTERING_ON_THE_ATTACKING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE(667),

	/**
	 * ID: 668<br>
	 * Message: You are registering on the defending side of the $s1 siege. Do you want to continue?
	 */
	YOU_ARE_REGISTERING_ON_THE_DEFENDING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE(668),

	/**
	 * ID: 669<br>
	 * Message: You are canceling your application to participate in the $s1 siege battle. Do you want to continue?
	 */
	YOU_ARE_CANCELING_YOUR_APPLICATION_TO_PARTICIPATE_IN_THE_S1_SIEGE_BATTLE_DO_YOU_W(669),

	/**
	 * ID: 670<br>
	 * Message: You are refusing the registration of $s1 clan on the defending side. Do you want to continue?
	 */
	YOU_ARE_REFUSING_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT_TO(670),

	/**
	 * ID: 671<br>
	 * Message: You are agreeing to the registration of $s1 clan on the defending side. Do you want to continue?
	 */
	YOU_ARE_AGREEING_TO_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT(671),
	
	/**
	 * ID: 672<br>
	 * Message: $s1 adena disappeared.
	 */
	S1_ADENA_DISSAPEARED(672),
	
	/**
	 * ID: 673<br>
	 * Message: Only a clan leader whose clan is of level 2 or higher is allowed to participate in a clan hall auction.
	 */
	ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_I(673),

	/**
	 * ID: 674<br>
	 * Message: It has not yet been seven days since canceling an auction.
	 */
	IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION(674),

	/**
	 * ID: 675<br>
	 * Message: There are no clan halls up for auction.
	 */
	THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION(675),

	/**
	 * ID: 676<br>
	 * Message: Since you have already submitted a bid, you are not allowed to participate in another auction at this time.
	 */
	SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOT(676),

	/**
	 * ID: 677<br>
	 * Message: Your bid price must be higher than the minimum price that can be bid.
	 */
	YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_THAT_CAN_BE_BID(677),

	/**
	 * ID: 678<br>
	 * Message: You have submitted a bid in the auction of $s1.
	 */
	YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1(678),

	/**
	 * ID: 679<br>
	 * Message: You have canceled your bid.
	 */
	YOU_HAVE_CANCELED_YOUR_BID(679),

	/**
	 * ID: 680<br>
	 * Message: You cannot participate in an auction.
	 */
	YOU_CANNOT_PARTICIPATE_IN_AN_AUCTION(680),
	
	/**
	 * ID: 681<br>
	 * Message: The clan does not own a clan hall.
	 */
	THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL(681),
	
	/**
	 * ID: 682<br>
	 * Message: You are moving to another village. Do you want to continue?
	 */
	YOU_ARE_MOVING_TO_ANOTHER_VILLAGE_DO_YOU_WANT_TO_CONTINUE(682),
	
	/**
	 * ID: 683<br>
	 * Message: There are no priority rights on a sweeper.
	 */
	THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER(683),
	
	/**
	 * ID: 684<br>
	 * Message: You cannot position mercenaries during a siege.
	 */
	YOU_CANNOT_POSITION_MERCENARIES_DURING_A_SIEGE(684),

	/**
	 * ID: 685<br>
	 * Message: You cannot apply for clan war with a clan that belongs to the same alliance.
	 */
	YOU_CANNOT_APPLY_FOR_CLAN_WAR_WITH_A_CLAN_THAT_BELONGS_TO_THE_SAME_ALLIANCE(685),
	
	/**
	 * ID: 686<br>
	 * Message: You have received $s1 damage from the fire of magic.
	 */
	YOU_HAVE_RECEIVED_S1_DAMAGE_FROM_THE_FIRE_OF_MAGIC(686),

	/**
	 * ID: 687<br>
	 * Message: You cannot move while frozen. Please wait.
	 */
	YOU_CANNOT_MOVE_WHILE_FROZEN_PLEASE_WAIT(687),
	
	/**
	 * ID: 688<br>
	 * Message: The clan that owns the castle is automatically registered on the defending side.
	 */
	THE_CLAN_THAT_OWNS_THE_CASTLE_IS_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE(688),
	
	/**
	 * ID: 689<br>
	 * Message: A clan that owns a castle cannot participate in another siege.
	 */
	A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE(689),

	/**
	 * ID: 690<br>
	 * Message: You cannot register on the attacking side because you are part of an alliance with the clan that owns the castle.
	 */
	YOU_CANNOT_REGISTER_ON_THE_ATTACKING_SIDE_BECAUSE_YOU_ARE_PART_OF_AN_ALLIANCE_WIT(690),
	
	/**
	 * ID: 691<br>
	 * Message: $s1 clan is already a member of $s2 alliance.
	 */
	S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE(691),
	
	/**
	 * ID: 692<br>
	 * Message: The other party is frozen. Please wait a moment.
	 */
	OTHER_PARTY_IS_DROZEN(692),
	
	/**
	 * ID: 693<br>
	 * Message: The package that arrived is in another warehouse.
	 */
	THE_PACKAGE_THAT_ARRIVED_IS_IN_ANOTHER_WAREHOUSE(693),

	/**
	 * ID: 694<br>
	 * Message: No packages have arrived.
	 */
	NO_PACKAGES_HAVE_ARRIVED(694),
	
	/**
	 * ID: 695<br>
	 * Message: You cannot set the name of the pet.
	 */
	YOU_CANNOT_SET_THE_NAME_OF_THE_PET(695),
	
	/**
	 * ID: 696<br>
	 * Message: Your account is restricted for not paying your PC room usage fees.
	 */
	YOUR_ACCOUNT_IS_RESTRICTED_FOR_NOT_PAYING_YOUR_PC_ROOM_USAGE_FEES(696),
	
	/**
	 * ID: 697<br>
	 * Message: The item enchant value is strange.
	 */
	THE_ITEM_ENCHANT_VALUE_IS_STRANGE(697),

	/**
	 * ID: 698<br>
	 * Message: The price is different than the same item on the sales list.
	 */
	THE_PRICE_IS_DIFFERENT_THAN_THE_SAME_ITEM_ON_THE_SALES_LIST(698),

	/**
	 * ID: 699<br>
	 * Message: Currently not purchasing.
	 */
	CURRENTLY_NOT_PURCHASING(699),
	
	/**
	 * ID: 700<br>
	 * Message: The purchase is complete.
	 */
	THE_PURCHASE_IS_COMPLETE(700),
	
	/**
	 * ID: 701<br>
	 * Message: You do not have enough required items.
	 */
	YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS(701),
	
	/**
	 * ID: 702<br>
	 * Message: There are no GMs currently visible in the public list as they may be performing other functions at the moment.
	 */
	THERE_ARE_NOT_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT(702),
	
	/**
	 * ID: 703<br>
	 * Message: ======<GM List>======
	 */
	GM_LIST_HEAD(703),
	
	/**
	 * ID: 704<br>
	 * Message: GM : $s1
	 */
	GM_S1(704),
	
	/**
	 * ID: 705<br>
	 * Message: You cannot exclude yourself.
	 */
	YOU_CANNOT_EXCLUDE_YOURSELF(705),
	
	/**
	 * ID: 706<br>
	 * Message: You can only register up to 64 names on your exclude list.
	 */
	YOU_CAN_ONLY_REGISTER_UP_TO_64_NAMES_ON_YOUR_EXCLUDE_LIST(706),
	
	/**
	 * ID: 707<br>
	 * Message: You cannot teleport to a village that is in a siege.
	 */
	YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE(707),
	
	/**
	 * ID: 708<br>
	 * Message: You do not have the right to use the castle warehouse.
	 */
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CASTLE_WAREHOUSE(708),
	
	/**
	 * ID: 709<br>
	 * Message: You do not have the right to use the clan warehouse.
	 */
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE(709),
	
	/**
	 * ID: 710<br>
	 * Message: Only clans of clan level 1 or higher can use a clan warehouse.
	 */
	ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE(710),
	
	/**
	 * ID: 711<br>
	 * Message: The siege of $s1 has started.
	 */
	THE_SIEGE_OF_S1_HAS_STARTED(711),

	/**
	 * ID: 712<br>
	 * Message: The siege of $s1 has finished.
	 */
	THE_SIEGE_OF_S1_HAS_FINISHED(712),

	/**
	 * ID: 713<br>
	 * Message: $s1/$s2/$s3 $s4:$s5
	 */
	S1S2S3_S4_S5(713),

	/**
	 * ID: 714<br>
	 * Message: A trap device has been tripped.
	 */
	A_TRAP_DEVICE_HAS_BEEN_TRIPPED(714),

	/**
	 * ID: 715<br>
	 * Message: The trap device has been stopped.
	 */
	THE_TRAP_DEVICE_HAS_BEEN_STOPPED(715),

	/**
	 * ID: 716<br>
	 * Message: If a base camp does not exist, resurrection is not possible.
	 */
	IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE(716),

	/**
	 * ID: 717<br>
	 * Message: The guardian tower has been destroyed and resurrection is not possible.
	 */
	THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE(717),

	/**
	 * ID: 718<br>
	 * Message: The castle gates cannot be opened and closed during a siege.
	 */
	THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE(718),

	/**
	 * ID: 719<br>
	 * Message: You failed at mixing the item.
	 */
	YOU_FAILED_AT_MIXING_THE_ITEM(719),
	
	/**
	 * ID: 720<br>
	 * Message: The purchase price is higher than the amount of money that you have and so you cannot open a personal store.
	 */
	THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE(720),
	
	/**
	 * ID: 721<br>
	 * Message: You cannot create an alliance while participating in a siege.
	 */
	YOU_CANNOT_CREATE_AN_ALLIANCE_WHILE_PARTICIPATING_IN_A_SIEGE(721),
	
	/**
	 * ID: 722<br>
	 * Message: You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.
	 */
	YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE(722),
	
	/**
	 * ID: 723<br>
	 * Message: The opposing clan is participating in a siege battle.
	 */
	THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE(723),
	
	/**
	 * ID: 724<br>
	 * Message: You cannot leave while participating in a siege battle.
	 */
	YOU_CANNOT_LEAVE_WHILE_PARTICIPATING_IN_A_SIEGE_BATTLE(724),

	/**
	 * ID: 725<br>
	 * Message: You cannot banish a clan from an alliance while the clan is participating in a siege.
	 */
	YOU_CANNOT_BANISH_A_CLAN_FROM_AN_ALLIANCE_WHILE_THE_CLAN_IS_PARTICIPATING_IN_A_SI(725),

	/**
	 * ID: 726<br>
	 * Message: The frozen condition has started. Please wait a moment.
	 */
	THE_FROZEN_CONDITION_HAS_STARTED_PLEASE_WAIT_A_MOMENT(726),

	/**
	 * ID: 727<br>
	 * Message: The frozen condition was removed.
	 */
	THE_FROZEN_CONDITION_WAS_REMOVED(727),

	/**
	 * ID: 728<br>
	 * Message: You cannot apply for dissolution again within seven days after a previous application for dissolution.
	 */
	YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICA(728),

	/**
	 * ID: 729<br>
	 * Message: That item cannot be discarded.
	 */
	THAT_ITEM_CANNOT_BE_DISCARDED(729),
	
	/**
	 * ID: 730<br>
	 * Message: - You have submitted $s1 petition(s). \n - You may submit $s2 more petition(s) today.
	 */
	YOU_HAVE_SUBMITTED_S1_PETITIONS_YOU_MAY_SUBMIT_S2_MORE_PETITIONS_TODAY(730),
	
	/**
	 * ID: 731<br>
	 * Message: A petition has been received by the GM on behalf of $s1. It is petition #$s2.
	 */
	A_PETITION_HAS_BEEN_RECEIVED_BY_THE_GM_ON_BEHALF_OF_S1_IT_IS_PETITION_S2(731),

	/**
	 * ID: 732<br>
	 * Message: $s1 has received a request for a consultation with the GM.
	 */
	S1_HAS_RECEIVED_A_REQUEST_FOR_A_CONSULTATION_WITH_THE_GM(732),
	
	/**
	 * ID: 733<br>
	 * Message: We have received $s1 petitions from you today and that is the maximum that you can submit in one day. You cannot submit any more petitions.
	 */
	WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM(733),
	
	/**
	 * ID: 734<br>
	 * Message: You failed at submitting a petition on behalf of someone else. $s1 already submitted a petition.
	 */
	YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_SOMEONE_ELSE_S1_ALREADY_SUBMITTE(734),

	/**
	 * ID: 735<br>
	 * Message: You failed at submitting a petition on behalf of $s1. The error is #$s2.
	 */
	YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_IS_S2(735),
	
	/**
	 * ID: 736<br>
	 * Message: The petition was canceled. You may submit $s1 more petition(s) today.
	 */
	THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITIONS_TODAY(736),
	
	/**
	 * ID: 737<br>
	 * Message: You failed at submitting a petition on behalf of $s1.
	 */
	YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1(737),
	
	/**
	 * ID: 738<br>
	 * Message: You have not submitted a petition.
	 */
	PETITION_NOT_SUBMITTED(738),
	
	/**
	 * ID: 739<br>
	 * Message: You failed at canceling a petition on behalf of $s1. The error code is $s2.
	 */
	YOU_FAILED_AT_CANCELING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_CODE_IS_S2(739),
	
	/**
	 * ID: 740<br>
	 * Message: $s1 participated in a petition chat at the request of the GM.
	 */
	S1_PARTICIPATED_IN_A_PETITION_CHAT_AT_THE_REQUEST_OF_THE_GM(740),

	/**
	 * ID: 741<br>
	 * Message: You failed at adding $s1 to the petition chat. A petition has already been submitted.
	 */
	YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_A_PETITION_HAS_ALREADY_BEEN_SUBMITTE(741),

	/**
	 * ID: 742<br>
	 * Message: You failed at adding $s1 to the petition chat. The error code is $s2.
	 */
	YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2(742),

	/**
	 * ID: 743<br>
	 * Message: $s1 left the petition chat.
	 */
	S1_LEFT_THE_PETITION_CHAT(743),

	/**
	 * ID: 744<br>
	 * Message: You failed at removing $s1 from the petition chat. The error code is $s2.
	 */
	YOU_FAILED_AT_REMOVING_S1_FROM_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2(744),
	
	/**
	 * ID: 745<br>
	 * Message: You are currently not in a petition chat.
	 */
	YOU_ARE_CURRENTLY_NOT_IN_PETITION_CHAT(745),
	
	/**
	 * ID: 746<br>
	 * Message: It is not currently a petition.
	 */
	IT_IS_NOT_CURRENTLY_A_PETITION(746),

	/**
	 * ID: 747<br>
	 * Message: If you need help, please visit the Support Center on the PlayNC website (http://www.plaync.com/us/support/).
	 */
	IF_YOU_NEED_HELP_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_PLAYNC_WEBSITE_HTTP_WWW_P(747),

	/**
	 * ID: 748<br>
	 * Message: The distance is too far and so the casting has been stopped.
	 */
	THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED(748),
	
	/**
	 * ID: 749<br>
	 * Message: The effect of $s1 has been removed.
	 */
	THE_EFFECT_OF_S1_HAS_BEEN_REMOVED(749),
	
	/**
	 * ID: 750<br>
	 * Message: There are no other skills to learn.
	 */
	THERE_ARE_NO_OTHER_SKILLS_TO_LEARN(750),
	
	/**
	 * ID: 751<br>
	 * Message: As there is a conflict in the siege relationship with a clan in the alliance, you cannot invite that clan to the alliance.
	 */
	AS_THERE_IS_A_CONFLICT_IN_THE_SIEGE_RELATIONSHIP_WITH_A_CLAN_IN_THE_ALLIANCE_YOU_(751),

	/**
	 * ID: 752<br>
	 * Message: That name cannot be used.
	 */
	THAT_NAME_CANNOT_BE_USED(752),

	/**
	 * ID: 753<br>
	 * Message: You cannot position mercenaries here.
	 */
	YOU_CANNOT_POSITION_MERCENARIES_HERE(753),

	/**
	 * ID: 754<br>
	 * Message: There are $s1 hours and $s2 minutes left in this week's usage time.
	 */
	THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THIS_WEEK_S_USAGE_TIME(754),

	/**
	 * ID: 755<br>
	 * Message: There are $s1 minutes left in this week's usage time.
	 */
	THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEK_S_USAGE_TIME(755),

	/**
	 * ID: 756<br>
	 * Message: This week's usage time has finished.
	 */
	THIS_WEEK_S_USAGE_TIME_HAS_FINISHED(756),

	/**
	 * ID: 757<br>
	 * Message: There are $s1 hours and $s2 minutes left in the fixed use time.
	 */
	THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THE_FIXED_USE_TIME(757),

	/**
	 * ID: 758<br>
	 * Message: There are $s1 hour(s) $s2 minute(s) left in this week's play time.
	 */
	THERE_ARE_S1_HOUR_S2_MINUTE_LEFT_IN_THIS_WEEK_S_PLAY_TIME(758),

	/**
	 * ID: 759<br>
	 * Message: There are $s1 minutes left in this week's play time.
	 */
	THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEK_S_PLAY_TIME(759),
	
	/**
	 * ID: 760<br>
	 * Message: $s1 cannot join the clan because one day has not yet passed since he/she left another clan.
	 */
	S1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAYS_HAS_NOT_YET_PASSED_SINCE_HE_SHHE_LEFT_ANOTHER_CLAN(760),
	
	/**
	 * ID: 761<br>
	 * Message: $s1 clan cannot join the alliance because one day has not yet passed since it left another alliance.
	 */
	S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_PASSED_SINCE_IT_LEFT_ANOTHER_ALLIANCE(761),
	
	/**
	 * ID: 762<br>
	 * Message: $s1 rolled $s2 and $s3's eye came out.
	 */
	S1_ROLLED_S2_AND_S3_S_EYE_CAME_OUT(762),

	/**
	 * ID: 763<br>
	 * Message: You failed at sending the package because you are too far from the warehouse.
	 */
	YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE(763),
	
	/**
	 * ID: 764<br>
	 * Message: You have been playing for an extended period of time. Please consider taking a break.
	 */
	YOU_HAVE_BEEN_PLAYING_FOR_AN_EXTENDED_PERIOD_OF_TIME_PLEASE_CONSIDER_TAKING_A_BREAK(764),
	
	/**
	 * ID: 765<br>
	 * Message: GameGuard is already running. Please try running it again after rebooting.
	 */
	GAMEGUARD_IS_ALREADY_RUNNING_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOOTING(765),
	
	/**
	 * ID: 766<br>
	 * Message: There is a GameGuard initialization error. Please try running it again after rebooting.
	 */
	THERE_IS_A_GAMEGUARD_INITIALIZATION_ERROR_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOO(766),

	/**
	 * ID: 767<br>
	 * Message: The GameGuard file is damaged . Please reinstall GameGuard.
	 */
	THE_GAMEGUARD_FILE_IS_DAMAGED_PLEASE_REINSTALL_GAMEGUARD(767),

	/**
	 * ID: 768<br>
	 * Message: A Windows system file is damaged. Please reinstall Internet Explorer.
	 */
	A_WINDOWS_SYSTEM_FILE_IS_DAMAGED_PLEASE_REINSTALL_INTERNET_EXPLORER(768),
	
	/**
	 * ID: 769<br>
	 * Message: A hacking tool has been discovered. Please try playing again after closing unnecessary programs.
	 */
	A_HACKING_TOOL_HAS_BEEN_DISCOVERED_PLEASE_TRY_PLAYING_AGAIN_AFTER_CLOSING_UNNECESSARY_PROGRAMS(769),
	
	/**
	 * ID: 770<br>
	 * Message: The GameGuard update was canceled. Please check your network connection status or firewall.
	 */
	THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_CHECK_YOUR_NETWORK_CONNECTION_STATUS_OR_(770),

	/**
	 * ID: 771<br>
	 * Message: The GameGuard update was canceled. Please try running it again after doing a virus scan or changing the settings in your PC management program.
	 */
	THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_DOING_A_VIRUS(771),

	/**
	 * ID: 772<br>
	 * Message: There was a problem when running GameGuard.
	 */
	THERE_WAS_A_PROBLEM_WHEN_RUNNING_GAMEGUARD(772),

	/**
	 * ID: 773<br>
	 * Message: The game or GameGuard files are damaged.
	 */
	THE_GAME_OR_GAMEGUARD_FILES_ARE_DAMAGED(773),

	/**
	 * ID: 774<br>
	 * Message: Play time is no longer accumulating.
	 */
	PLAY_TIME_IS_NO_LONGER_ACCUMULATING(774),

	/**
	 * ID: 775<br>
	 * Message: From here on, play time will be expended.
	 */
	FROM_HERE_ON_PLAY_TIME_WILL_BE_EXPENDED(775),

	/**
	 * ID: 776<br>
	 * Message: The clan hall which was put up for auction has been awarded to $s1 clan.
	 */
	THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN(776),

	/**
	 * ID: 777<br>
	 * Message: The clan hall which had been put up for auction was not sold and therefore has been re-listed.
	 */
	THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BE(777),

	/**
	 * ID: 778<br>
	 * Message: You may not log out from this location.
	 */
	YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION(778),

	/**
	 * ID: 779<br>
	 * Message: You may not restart in this location.
	 */
	YOU_MAY_NOT_RESTART_IN_THIS_LOCATION(779),
	
	/**
	 * ID: 780<br>
	 * Message: Observation is only possible during a siege.
	 */
	OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE(780),
	
	/**
	 * ID: 781<br>
	 * Message: Observers cannot participate.
	 */
	OBSERVERS_CANNOT_PARTICIPATE(781),
	
	/**
	 * ID: 782<br>
	 * Message: You may not observe a siege with a pet or servitor summoned.
	 */
	YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_PET_OR_SERVITOR_SUMMONED(782),
	
	/**
	 * ID: 783<br>
	 * Message: Lottery ticket sales have been temporarily suspended.
	 */
	LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED(783),
	
	/**
	 * ID: 784<br>
	 * Message: Tickets for the current lottery are no longer available.
	 */
	TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE(784),
	
	/**
	 * ID: 785<br>
	 * Message: The results of lottery number $s1 have not yet been published.
	 */
	THE_RESULTS_OF_LOTTERY_NUMBER_S1_HAVE_NOT_YET_BEEN_PUBLISHED(785),
	
	/**
	 * ID: 786<br>
	 * Message: Incorrect syntax.
	 */
	INCORRECT_SYNTAX(786),

	/**
	 * ID: 787<br>
	 * Message: The tryouts are finished.
	 */
	THE_TRYOUTS_ARE_FINISHED(787),

	/**
	 * ID: 788<br>
	 * Message: The finals are finished.
	 */
	THE_FINALS_ARE_FINISHED(788),

	/**
	 * ID: 789<br>
	 * Message: The tryouts have begun.
	 */
	THE_TRYOUTS_HAVE_BEGUN(789),

	/**
	 * ID: 790<br>
	 * Message: The finals have begun.
	 */
	THE_FINALS_HAVE_BEGUN(790),

	/**
	 * ID: 791<br>
	 * Message: The final match is about to begin. Line up!
	 */
	THE_FINAL_MATCH_IS_ABOUT_TO_BEGIN_LINE_UP(791),

	/**
	 * ID: 792<br>
	 * Message: The siege of the clan hall is finished.
	 */
	THE_SIEGE_OF_THE_CLAN_HALL_IS_FINISHED(792),

	/**
	 * ID: 793<br>
	 * Message: The siege of the clan hall has begun.
	 */
	THE_SIEGE_OF_THE_CLAN_HALL_HAS_BEGUN(793),
	
	/**
	 * ID: 794<br>
	 * Message: You are not authorized to do that.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT(794),
	
	/**
	 * ID: 795<br>
	 * Message: Only clan leaders are authorized to set rights.
	 */
	ONLY_CLAN_LEADERS_ARE_AUTHORIZED_TO_SET_RIGHTS(795),

	/**
	 * ID: 796<br>
	 * Message: Your remaining observation time is $s1 minutes.
	 */
	YOUR_REMAINING_OBSERVATION_TIME_IS_S1_MINUTES(796),
	
	/**
	 * ID: 797<br>
	 * Message: You may create up to 24 macros.
	 */
	YOU_MAY_CREATE_UP_TO_24_MACROS(797),
	
	/**
	 * ID: 798<br>
	 * Message: Item registration is irreversible. Do you wish to continue?
	 */
	ITEM_REGISTRATION_IS_IRREVERSIBLE_DO_YOU_WISH_TO_CONTINUE(798),

	/**
	 * ID: 799<br>
	 * Message: The observation time has expired.
	 */
	THE_OBSERVATION_TIME_HAS_EXPIRED(799),
	
	/**
	 * ID: 800<br>
	 * Message: You are too late. The registration period is over.
	 */
	YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER(800),

	/**
	 * ID: 801<br>
	 * Message: Registration for the clan hall siege is closed.
	 */
	REGISTRATION_FOR_THE_CLAN_HALL_SIEGE_IS_CLOSED(801),

	/**
	 * ID: 802<br>
	 * Message: Petitions are not being accepted at this time. You may submit your petition after $s1 a.m./p.m.
	 */
	PETITIONS_ARE_NOT_BEING_ACCEPTED_AT_THIS_TIME_YOU_MAY_SUBMIT_YOUR_PETITION_AFTER(802),

	/**
	 * ID: 803<br>
	 * Message: Enter the specifics of your petition.
	 */
	ENTER_THE_SPECIFICS_OF_YOUR_PETITION(803),

	/**
	 * ID: 804<br>
	 * Message: Select a type.
	 */
	SELECT_A_TYPE(804),

	/**
	 * ID: 805<br>
	 * Message: Petitions are not being accepted at this time. You may submit your petition after $s1 a.m./p.m.
	 */
	PETITIONS_ARE_NOT_BEING_ACCEPTED_AT_THIS_TIME_YOU_MAY_SUBMIT_YOUR_PETITION_AFTER_2(805),

	/**
	 * ID: 806<br>
	 * Message: If you are trapped, try typing "/unstuck".
	 */
	IF_YOU_ARE_TRAPPED_TRY_TYPING_UNSTUCK_(806),

	/**
	 * ID: 807<br>
	 * Message: This terrain is navigable. Prepare for transport to the nearest village.
	 */
	THIS_TERRAIN_IS_NAVIGABLE_PREPARE_FOR_TRANSPORT_TO_THE_NEAREST_VILLAGE(807),

	/**
	 * ID: 808<br>
	 * Message: You are stuck. You may submit a petition by typing "/gm".
	 */
	YOU_ARE_STUCK_YOU_MAY_SUBMIT_A_PETITION_BY_TYPING_GM_(808),

	/**
	 * ID: 809<br>
	 * Message: You are stuck. You will be transported to the nearest village in five minutes.
	 */
	YOU_ARE_STUCK_YOU_WILL_BE_TRANSPORTED_TO_THE_NEAREST_VILLAGE_IN_FIVE_MINUTES(809),
	
	/**
	 * ID: 810<br>
	 * Message: Invalid macro. Refer to the Help file for instructions.
	 */
	INVALID_MACRO_REFER_TO_HELP_FILE_FOR_INSTRUCTIONS(810),
	
	/**
	 * ID: 811<br>
	 * Message: You will be moved to ($s1). Do you wish to continue?
	 */
	YOU_WILL_BE_MOVED_TO_S1_DO_YOU_WISH_TO_CONTINUE(811),

	/**
	 * ID: 812<br>
	 * Message: The secret trap has inflicted $s1 damage on you.
	 */
	THE_SECRET_TRAP_HAS_INFLICTED_S1_DAMAGE_ON_YOU(812),

	/**
	 * ID: 813<br>
	 * Message: You have been poisoned by a Secret Trap.
	 */
	YOU_HAVE_BEEN_POISONED_BY_A_SECRET_TRAP(813),

	/**
	 * ID: 814<br>
	 * Message: Your speed has been decreased by a Secret Trap.
	 */
	YOUR_SPEED_HAS_BEEN_DECREASED_BY_A_SECRET_TRAP(814),

	/**
	 * ID: 815<br>
	 * Message: The tryouts are about to begin. Line up!
	 */
	THE_TRYOUTS_ARE_ABOUT_TO_BEGIN_LINE_UP(815),
	
	/**
	 * ID: 816<br>
	 * Message: Tickets are now available for Monster Race $s1!
	 */
	TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1(816),
	
	/**
	 * ID: 817<br>
	 * Message: Now selling tickets for Monster Race $s1!
	 */
	NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1(817),
	
	/**
	 * ID: 818<br>
	 * Message: Ticket sales for the Monster Race will end in $s1 minute(s).
	 */
	TICKETS_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES(818),
	
	/**
	 * ID: 819<br>
	 * Message: Tickets sales are closed for Monster Race $s1. Odds are posted.
	 */
	TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1_ODDS_ARE_POSTED(819),
	
	/**
	 * ID: 820<br>
	 * Message: Monster Race $s2 will begin in $s1 minute(s)!
	 */
	MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES(820),
	
	/**
	 * ID: 821<br>
	 * Message: Monster Race $s1 will begin in 30 seconds!
	 */
	MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS(821),
	
	/**
	 * ID: 822<br>
	 * Message: Monster Race $s1 is about to begin! Countdown in five seconds!
	 */
	MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS(822),
	
	/**
	 * ID: 823<br>
	 * Message: The race will begin in $s1 second(s)!
	 */
	THE_RACE_WILL_BEGIN_IN_S1_SECONDS(823),
	
	/**
	 * ID: 824<br>
	 * Message: They're off!
	 */
	THEY_RE_OFF(824),
	
	/**
	 * ID: 825<br>
	 * Message: Monster Race $s1 is finished!
	 */
	MONSTER_RACE_S1_IS_FINISHED(825),
	
	/**
	 * ID: 826<br>
	 * Message: First prize goes to the player in lane $s1. Second prize goes to the player in lane $s2.
	 */
	FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2(826),
	
	/**
	 * ID: 827<br>
	 * Message: You may not impose a block on a GM.
	 */
	YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM(827),
	
	/**
	 * ID: 829<br>
	 * Message: You cannot recommend yourself.
	 */
	YOU_CANNOT_RECOMMEND_YOURSELF(829),
	
	/**
	 * ID: 830<br>
	 * Message: You have recommended $s1. You are authorized to make $s2 more recommendations.
	 */
	YOU_HAVE_RECOMMENDED_S1_YOU_ARE_AUTHORIZED_TO_MAKE_S2_MORE_RECOMMENDATIONS(830),
	
	/**
	 * ID: 831<br>
	 * Message: You have been recommended by $s1.
	 */
	YOU_HAVE_BEEN_RECOMMENDED_BY_S1(831),
	
	/**
	 * ID: 832<br>
	 * Message: That character has already been recommended.
	 */
	THAT_CHARACTER_HAS_ALREADY_BEEN_RECOMMENDED(832),
	
	/**
	 * ID: 833<br>
	 * Message: You are not authorized to make further recommendations at this time. You will receive more recommendation credits each day at 1 p.m.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_MAKE_FURTHER_RECOMMENDATIONS_AT_THIS_TIME(833),
	
	/**
	 * ID: 834<br>
	 * Message: $s1 has rolled $s2.
	 */
	S1_HAS_ROLLED_S2(834),
	
	/**
	 * ID: 835<br>
	 * Message: You may not throw the dice at this time. Try again later.
	 */
	YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER(835),
	
	/**
	 * ID: 836<br>
	 * Message: You have exceeded your inventory volume limit and cannot take this item.
	 */
	YOU_HAVE_EXCEEDED_YOUR_INVENTORY_VOLUME_LIMIT_AND_CANNOT_TAKE_THIS_TIME(836),
	
	/**
	 * ID: 837<br>
	 * Message: Macro descriptions may contain up to 32 characters.
	 */
	MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS(837),
	
	/**
	 * ID: 838<br>
	 * Message: Enter the name of the macro.
	 */
	ENTER_THE_NAME_OF_THE_MACRO(838),
	
	/**
	 * ID: 839<br>
	 * Message: That name is already assigned to another macro.
	 */
	THAT_NAME_IS_ALREADY_ASSIGNED_TO_ANOTHER_MACRO(839),
	
	/**
	 * ID: 840<br>
	 * Message: That recipe is already registered.
	 */
	THAT_RECIPE_IS_ALREADY_REGISTERED(840),
	
	/**
	 * ID: 841<br>
	 * Message: No further recipes may be registered.
	 */
	NO_FUTHER_RECIPES_MAY_BE_REGISTERED(841),
	
	/**
	 * ID: 842<br>
	 * Message: You are not authorized to register a recipe.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE(842),

	/**
	 * ID: 843<br>
	 * Message: The siege of $s1 is finished.
	 */
	THE_SIEGE_OF_S1_IS_FINISHED(843),

	/**
	 * ID: 844<br>
	 * Message: The siege to conquer $s1 has begun.
	 */
	THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN(844),

	/**
	 * ID: 845<br>
	 * Message: The deadline to register for the siege of $s1 has passed.
	 */
	THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED(845),
	
	/**
	 * ID: 846<br>
	 * Message: The siege of $s1 has been canceled due to lack of interest.
	 */
	SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST(846),
	
	/**
	 * ID: 847<br>
	 * Message: A clan that owns a clan hall may not participate in a clan hall siege.
	 */
	A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE(847),

	/**
	 * ID: 848<br>
	 * Message: $s1 has been deleted.
	 */
	S1_HAS_BEEN_DELETED(848),

	/**
	 * ID: 849<br>
	 * Message: $s1 cannot be found.
	 */
	S1_CANNOT_BE_FOUND(849),

	/**
	 * ID: 850<br>
	 * Message: $s1 already exists.
	 */
	S1_ALREADY_EXISTS_2(850),
	
	/**
	 * ID: 851<br>
	 * Message: $s1 has been added.
	 */
	S1_HAS_BEEN_ADDED(851),
	
	/**
	 * ID: 852<br>
	 * Message: The recipe is incorrect.
	 */
	THE_RECIPE_IS_INCORRECT(852),
	
	/**
	 * ID: 853<br>
	 * Message: You may not alter your recipe book while engaged in manufacturing.
	 */
	YOU_MAY_NOT_ALTER_YOU_RECIPEBOOK_WHILE_ENGAGED_IN_MANUFACTURING(853),
	
	/**
	 * ID: 854<br>
	 * Message: You are missing $s2 $s1 required to create that.
	 */
	YOU_ARE_MISSING_S2_S1_REQUIRED_TO_CREATE_THAT(854),

	/**
	 * ID: 855<br>
	 * Message: $s1 clan has defeated $s2.
	 */
	S1_CLAN_HAS_DEFEATED_S2(855),

	/**
	 * ID: 856<br>
	 * Message: The siege of $s1 has ended in a draw.
	 */
	THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW(856),

	/**
	 * ID: 857<br>
	 * Message: $s1 clan has won in the preliminary match of $s2.
	 */
	S1_CLAN_HAS_WON_IN_THE_PRELIMINARY_MATCH_OF_S2(857),

	/**
	 * ID: 858<br>
	 * Message: The preliminary match of $s1 has ended in a draw.
	 */
	THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW(858),

	/**
	 * ID: 859<br>
	 * Message: Please register a recipe.
	 */
	PLEASE_REGISTER_A_RECIPE(859),

	/**
	 * ID: 860<br>
	 * Message: You may not build your headquarters in close proximity to another headquarters.
	 */
	YOU_MAY_NOT_BUILD_YOUR_HEADQUARTERS_IN_CLOSE_PROXIMITY_TO_ANOTHER_HEADQUARTERS(860),

	/**
	 * ID: 861<br>
	 * Message: You have exceeded the maximum number of memos.
	 */
	YOU_HAVE_EXCEEDED_THE_MAXIMUM_NUMBER_OF_MEMOS(861),

	/**
	 * ID: 862<br>
	 * Message: Odds are not posted until ticket sales have closed.
	 */
	ODDS_ARE_NOT_POSTED_UNTIL_TICKET_SALES_HAVE_CLOSED(862),

	/**
	 * ID: 863<br>
	 * Message: You feel the energy of fire.
	 */
	YOU_FEEL_THE_ENERGY_OF_FIRE(863),

	/**
	 * ID: 864<br>
	 * Message: You feel the energy of water.
	 */
	YOU_FEEL_THE_ENERGY_OF_WATER(864),

	/**
	 * ID: 865<br>
	 * Message: You feel the energy of wind.
	 */
	YOU_FEEL_THE_ENERGY_OF_WIND(865),

	/**
	 * ID: 866<br>
	 * Message: You may no longer gather energy.
	 */
	YOU_MAY_NO_LONGER_GATHER_ENERGY(866),

	/**
	 * ID: 867<br>
	 * Message: The energy is depleted.
	 */
	THE_ENERGY_IS_DEPLETED(867),

	/**
	 * ID: 868<br>
	 * Message: The energy of fire has been delivered.
	 */
	THE_ENERGY_OF_FIRE_HAS_BEEN_DELIVERED(868),

	/**
	 * ID: 869<br>
	 * Message: The energy of water has been delivered.
	 */
	THE_ENERGY_OF_WATER_HAS_BEEN_DELIVERED(869),

	/**
	 * ID: 870<br>
	 * Message: The energy of wind has been delivered.
	 */
	THE_ENERGY_OF_WIND_HAS_BEEN_DELIVERED(870),

	/**
	 * ID: 871<br>
	 * Message: The seed has been sown.
	 */
	THE_SEED_HAS_BEEN_SOWN(871),
	
	/**
	 * ID: 872<br>
	 * Message: This seed may not be sown here.
	 */
	THIS_SEED_MAY_NOT_BE_SOWN_HERE(872),
	
	/**
	 * ID: 873<br>
	 * Message: That character does not exist.
	 */
	CHARACTER_DOES_NOT_EXIST(873),
	
	/**
	 * ID: 874<br>
	 * Message: The capacity of the warehouse has been exceeded.
	 */
	THE_CAPACITY_OF_THE_WAREHOUSE_HAS_BEEN_EXCEEDED(874),

	/**
	 * ID: 875<br>
	 * Message: The transport of the cargo has been canceled.
	 */
	THE_TRANSPORT_OF_THE_CARGO_HAS_BEEN_CANCELED(875),

	/**
	 * ID: 876<br>
	 * Message: The cargo was not delivered.
	 */
	THE_CARGO_WAS_NOT_DELIVERED(876),
	
	/**
	 * ID: 877<br>
	 * Message: The symbol has been added.
	 */
	THE_SYMBOL_HAS_BEEN_ADDED(877),
	
	/**
	 * ID: 878<br>
	 * Message: The symbol has been deleted.
	 */
	THE_SYMBOL_HAS_BEEN_DELETED(878),
	
	/**
	 * ID: 879<br>
	 * Message: The manor system is currently under maintenance.
	 */
	THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE(879),
	
	/**
	 * ID: 880<br>
	 * Message: The transaction is complete.
	 */
	THE_TRANSACTION_IS_COMPLETE(880),
	
	/**
	 * ID: 881<br>
	 * Message: There is a discrepancy on the invoice.
	 */
	THERE_IS_A_DISCREPANCY_ON_THE_INVOICE(881),
	
	/**
	 * ID: 882<br>
	 * Message: The seed quantity is incorrect.
	 */
	THE_SEED_QUANTITY_IS_INCORRECT(882),
	
	/**
	 * ID: 883<br>
	 * Message: The seed information is incorrect.
	 */
	THE_SEED_INFORMATION_IS_INCORRECT(883),
	
	/**
	 * ID: 884<br>
	 * Message: The manor information has been updated.
	 */
	THE_MANOR_INFORMATION_HAS_BEEN_UPDATED(884),
	
	/**
	 * ID: 885<br>
	 * Message: The number of crops is incorrect.
	 */
	THE_NUMBER_OF_CROPS_IS_INCORRECT(885),
	
	/**
	 * ID: 886<br>
	 * Message: The crops are priced incorrectly.
	 */
	THE_CROPS_ARE_PRICED_INCORRECTLY(886),
	
	/**
	 * ID: 887<br>
	 * Message: The type is incorrect.
	 */
	THE_TYPE_IS_INCORRECT(887),
	
	/**
	 * ID: 888<br>
	 * Message: No crops can be purchased at this time.
	 */
	NO_CROPS_CAN_BE_PURCHASED_AT_THIS_TIME(888),
	
	/**
	 * ID: 889<br>
	 * Message: The seed was successfully sown.
	 */
	THE_SEED_WAS_SUCCESSFULLY_SOWN(889),
	
	/**
	 * ID: 890<br>
	 * Message: The seed was not sown.
	 */
	THE_SEED_WAS_NOT_SOWN(890),
	
	/**
	 * ID: 891<br>
	 * Message: You are not authorized to harvest.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_HARVEST(891),
	
	/**
	 * ID: 892<br>
	 * Message: The harvest has failed.
	 */
	THE_HARVEST_HAS_FAILED(892),
	
	/**
	 * ID: 893<br>
	 * Message: The harvest failed because the seed was not sown.
	 */
	THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN(893),
	
	/**
	 * ID: 894<br>
	 * Message: Up to $s1 recipes can be registered.
	 */
	UP_TO_S1_RECIPES_CAN_REGISTER(894),
	
	/**
	 * ID: 895<br>
	 * Message: No recipes have been registered.
	 */
	NO_RECIPES_HAVE_BEEN_REGISTERED(895),
	
	/**
	 * ID: 896<br>
	 * Message: Quest recipes can not be registered.
	 */
	QUEST_RECIPES_CAN_NOT_BE_REGISTERED(896),

	/**
	 * ID: 897<br>
	 * Message: The fee to create the item is incorrect.
	 */
	THE_FEE_TO_CREATE_THE_ITEM_IS_INCORRECT(897),
	
	/**
	 * ID: 898<br>
	 * Message: Only characters of level 10 or above are authorized to make recommendations.
	 */
	ONLY_CHARACTERS_OF_LEVEL_10_OR_ABOVE_ARE_AUTHORIZED_TO_MAKE_RECOMMENDATIONS(898),
	
	/**
	 * ID: 899<br>
	 * Message: The symbol cannot be drawn.
	 */
	THE_SYMBOL_CANNOT_BE_DRAWN(899),
	
	/**
	 * ID: 900<br>
	 * Message: No slot exists to draw the symbol
	 */
	NO_SLOTS_EXISTS_TO_DRAW_THE_SYMBOL(900),
	
	/**
	 * ID: 901<br>
	 * Message: The symbol information cannot be found.
	 */
	THE_SYMBOL_INFORMATION_CANNOT_BE_FOUND(901),
	
	/**
	 * ID: 902<br>
	 * Message: The number of items is incorrect.
	 */
	THE_NUMBER_OF_ITEMS_IS_INCORRECT(902),

	/**
	 * ID: 903<br>
	 * Message: You may not submit a petition while frozen. Be patient.
	 */
	YOU_MAY_NOT_SUBMIT_A_PETITION_WHILE_FROZEN_BE_PATIENT(903),

	/**
	 * ID: 904<br>
	 * Message: Items cannot be discarded while in private store status.
	 */
	ITEMS_CANNOT_BE_DISCARDED_WHILE_IN_PRIVATE_STORE_STATUS(904),

	/**
	 * ID: 905<br>
	 * Message: The current score for the Humans is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_HUMANS_IS_S1(905),

	/**
	 * ID: 906<br>
	 * Message: The current score for the Elves is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_ELVES_IS_S1(906),

	/**
	 * ID: 907<br>
	 * Message: The current score for the Dark Elves is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_DARK_ELVES_IS_S1(907),

	/**
	 * ID: 908<br>
	 * Message: The current score for the Orcs is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_ORCS_IS_S1(908),

	/**
	 * ID: 909<br>
	 * Message: The current score for the Dwarves is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_DWARVES_IS_S1(909),
	
	/**
	 * ID: 910<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Talking Island Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_TALKING_ISLAND_VILLAGE(910),
	
	/**
	 * ID: 911<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Gludin Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_GLUDIN_VILLAGE(911),
	
	/**
	 * ID: 912<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Gludio)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GLUDIO(912),
	
	/**
	 * ID: 913<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Neutral Zone)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_NEUTRAL_ZONE(913),
	
	/**
	 * ID: 914<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Elven Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ELVEN_VILLAGE(914),
	
	/**
	 * ID: 915<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Dark Elf Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DARK_ELF_VILLAGE(915),
	
	/**
	 * ID: 916<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Dion)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_DION(916),
	
	/**
	 * ID: 917<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Floran Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_FLORAN_VILLAGE(917),
	
	/**
	 * ID: 918<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Giran)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GIRAN(918),
	
	/**
	 * ID: 919<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Giran Harbor)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_GIRAN_HARBOR(919),
	
	/**
	 * ID: 920<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Orc Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ORC_VILLAGE(920),
	
	/**
	 * ID: 921<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Dwarven Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DWARVEN_VILLAGE(921),
	
	/**
	 * ID: 922<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Town of Oren)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_OREN(922),
	
	/**
	 * ID: 923<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Hunters Village)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_HUNTERS_VILLAGE(923),
	
	/**
	 * ID: 924<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Aden Castle Town)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_ADEN_CASTLE_TOWN(924),
	
	/**
	 * ID: 925<br>
	 * Message: Current location : $s1, $s2, $s3 (Near the Coliseum)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_COLISEUM(925),
	
	/**
	 * ID: 926<br>
	 * Message: Current location : $s1, $s2, $s3 (Near Heine)
	 */
	CURRENT_LOCATION_S1_S2_S3_NEAR_HEINE(926),
	
	/**
	 * ID: 927<br>
	 * Message: The current time is $s1:$s2 in the day.
	 */
	THE_CURRENT_TIME_IS_S1_S2_IN_THE_DAY(927),
	
	/**
	 * ID: 928<br>
	 * Message: The current time is $s1:$s2 in the night.
	 */
	THE_CURRENT_TIME_IS_S1_S2_IN_THE_NIGHT(928),
	
	/**
	 * ID: 909<br>
	 * Message: The current score for the Dwarves is $s1.
	 */
	THE_CURRENT_SCORE_FOR_THE_DWARVES_IS_S1_2(909),
	
	/**
	 * ID: 930<br>
	 * Message: Lottery tickets are not currently being sold.
	 */
	LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD(930),
	
	/**
	 * ID: 931<br>
	 * Message: The winning lottery ticket number has not yet been announced.
	 */
	THE_WINNING_LOTTERY_TICKET_NUMBER_HAS_NOT_YET_BEEN_ANNOUNCED(931),

	/**
	 * ID: 932<br>
	 * Message: You cannot chat locally while observing.
	 */
	YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING(932),
	
	/**
	 * ID: 933<br>
	 * Message: The seed pricing greatly differs from standard seed prices.
	 */
	THE_SEED_PRICING_GREATLY_DIFFERS_FROM_STANDARD_SEED_PRICES(933),
	
	/**
	 * ID: 934<br>
	 * Message: It is a deleted recipe.
	 */
	IT_IS_A_DELETED_RECIPE(934),
	
	/**
	 * ID: 935<br>
	 * Message: The amount is not sufficient and so the manor is not in operation.
	 */
	THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION(935),
	
	/**
	 * ID: 936<br>
	 * Message: Use $s1.
	 */
	USE_S1(936),
	
	/**
	 * ID: 937<br>
	 * Message: Currently preparing for private workshop.
	 */
	CURRENTLY_PREPARING_FOR_PRIVATE_WORKSHOP(937),
	
	/**
	 * ID: 938<br>
	 * Message: The community server is currently offline.
	 */
	THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE(938),
	
	/**
	 * ID: 939<br>
	 * Message: You cannot exchange while blocking everything.
	 */
	YOU_CANNOT_EXCHANGE_WHILE_BLOCKING_EVERYTHING(939),

	/**
	 * ID: 940<br>
	 * Message: $s1 is blocking everything.
	 */
	S1_IS_BLOCKING_EVERYTHING(940),

	/**
	 * ID: 941<br>
	 * Message: Restart at Talking Island Village.
	 */
	RESTART_AT_TALKING_ISLAND_VILLAGE(941),

	/**
	 * ID: 942<br>
	 * Message: Restart at Gludin Village.
	 */
	RESTART_AT_GLUDIN_VILLAGE(942),

	/**
	 * ID: 943<br>
	 * Message: Restart at the Town of Gludin.
	 */
	RESTART_AT_THE_TOWN_OF_GLUDIN(943),

	/**
	 * ID: 944<br>
	 * Message: Restart at the Neutral Zone.
	 */
	RESTART_AT_THE_NEUTRAL_ZONE(944),

	/**
	 * ID: 945<br>
	 * Message: Restart at the Elven Village.
	 */
	RESTART_AT_THE_ELVEN_VILLAGE(945),

	/**
	 * ID: 946<br>
	 * Message: Restart at the Dark Elf Village.
	 */
	RESTART_AT_THE_DARK_ELF_VILLAGE(946),

	/**
	 * ID: 947<br>
	 * Message: Restart at the Town of Dion.
	 */
	RESTART_AT_THE_TOWN_OF_DION(947),

	/**
	 * ID: 948<br>
	 * Message: Restart at Floran Village.
	 */
	RESTART_AT_FLORAN_VILLAGE(948),

	/**
	 * ID: 949<br>
	 * Message: Restart at the Town of Giran.
	 */
	RESTART_AT_THE_TOWN_OF_GIRAN(949),

	/**
	 * ID: 950<br>
	 * Message: Restart at Giran Harbor.
	 */
	RESTART_AT_GIRAN_HARBOR(950),

	/**
	 * ID: 951<br>
	 * Message: Restart at the Orc Village.
	 */
	RESTART_AT_THE_ORC_VILLAGE(951),

	/**
	 * ID: 952<br>
	 * Message: Restart at the Dwarven Village.
	 */
	RESTART_AT_THE_DWARVEN_VILLAGE(952),

	/**
	 * ID: 953<br>
	 * Message: Restart at the Town of Oren.
	 */
	RESTART_AT_THE_TOWN_OF_OREN(953),

	/**
	 * ID: 954<br>
	 * Message: Restart at Hunters Village.
	 */
	RESTART_AT_HUNTERS_VILLAGE(954),

	/**
	 * ID: 955<br>
	 * Message: Restart at the Town of Aden.
	 */
	RESTART_AT_THE_TOWN_OF_ADEN(955),

	/**
	 * ID: 956<br>
	 * Message: Restart at the Coliseum.
	 */
	RESTART_AT_THE_COLISEUM(956),

	/**
	 * ID: 957<br>
	 * Message: Restart at Heine.
	 */
	RESTART_AT_HEINE(957),

	/**
	 * ID: 958<br>
	 * Message: Items cannot be discarded or destroyed while operating a private store or workshop.
	 */
	ITEMS_CANNOT_BE_DISCARDED_OR_DESTROYED_WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHO(958),

	/**
	 * ID: 959<br>
	 * Message: $s1 (*$s2) manufactured successfully.
	 */
	S1_XS2_MANUFACTURED_SUCCESSFULLY(959),

	/**
	 * ID: 960<br>
	 * Message: $s1 manufacturing failure.
	 */
	S1_MANUFACTURING_FAILURE(960),

	/**
	 * ID: 961<br>
	 * Message: You are now blocking everything.
	 */
	YOU_ARE_NOW_BLOCKING_EVERYTHING(961),

	/**
	 * ID: 962<br>
	 * Message: You are no longer blocking everything.
	 */
	YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING(962),

	/**
	 * ID: 963<br>
	 * Message: Please determine the manufacturing price.
	 */
	PLEASE_DETERMINE_THE_MANUFACTURING_PRICE(963),

	/**
	 * ID: 964<br>
	 * Message: Chatting is prohibited for one minute.
	 */
	CHATTING_IS_PROHIBITED_FOR_ONE_MINUTE(964),

	/**
	 * ID: 965<br>
	 * Message: The chatting prohibition has been removed.
	 */
	THE_CHATTING_PROHIBITION_HAS_BEEN_REMOVED(965),

	/**
	 * ID: 966<br>
	 * Message: Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will become even longer.
	 */
	CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REM(966),

	/**
	 * ID: 967<br>
	 * Message: Do you accept the party invitation from $s1? (Item distribution: Random including spoil)
	 */
	DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_RANDOM_INCLUDING_SPO(967),

	/**
	 * ID: 968<br>
	 * Message: Do you accept the party invitation from $s1? (Item distribution: By turn)
	 */
	DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_BY_TURN(968),

	/**
	 * ID: 969<br>
	 * Message: Do you accept the party invitation from $s1? (Item distribution: By turn including spoil)
	 */
	DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_BY_TURN_INCLUDING_SP(969),
	
	/**
	 * ID: 970<br>
	 * Message: $s2's MP has been drained by $s1.
	 */
	S2S_MP_HAS_BEEN_DRAINED_BY_S1(970),
	
	/**
	 * ID: 971<br>
	 * Message: Petitions cannot exceed 255 characters.
	 */
	PETTITIONS_CANNOT_EXCEED_255_CHARACTERS(971),
	
	/**
	 * ID: 972<br>
	 * Message: This pet cannot use this item.
	 */
	THIS_PET_CANNOT_USE_THIS_ITEM(972),
	
	/**
	 * ID: 973<br>
	 * Message: Please input no more than the number you have.
	 */
	PLEASE_INPUT_NO_MORE_THAN_THE_NUMBER_YOU_HAVE(973),
	
	/**
	 * ID: 974<br>
	 * Message: The soul crystal succeeded in absorbing a soul.
	 */
	THE_SOUL_CRYSTAL_SUCEEDED_IN_ABSORBING_A_SOUL(974),
	
	/**
	 * ID: 975<br>
	 * Message: The soul crystal was not able to absorb a soul.
	 */
	THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_A_SOUL(975),
	
	/**
	 * ID: 976<br>
	 * Message: The soul crystal broke because it was not able to endure the soul energy.
	 */
	THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY(976),
	
	/**
	 * ID: 977<br>
	 * Message: The soul crystals caused resonation and failed at absorbing a soul.
	 */
	THE_SOUL_CRYSTALS_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL(977),
	
	/**
	 * ID: 978<br>
	 * Message: The soul crystal is refusing to absorb a soul.
	 */
	THE_SOUL_CRUSTAL_IS_REFUSING_TO_ABSORB_A_SOUL(978),
	
	/**
	 * ID: 979<br>
	 * Message: The ferry arrived at Talking Island Harbor.
	 */
	THE_FERRY_ARRIVED_AT_TALKING_ISLAND_HARBOR(979),

	/**
	 * ID: 980<br>
	 * Message: The ferry will leave for Gludin Harbor after anchoring for ten minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(980),

	/**
	 * ID: 981<br>
	 * Message: The ferry will leave for Gludin Harbor in five minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_FIVE_MINUTES(981),

	/**
	 * ID: 982<br>
	 * Message: The ferry will leave for Gludin Harbor in one minute.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_ONE_MINUTE(982),

	/**
	 * ID: 983<br>
	 * Message: Those wishing to ride should make haste to get on.
	 */
	THOSE_WISHING_TO_RIDE_SHOULD_MAKE_HASTE_TO_GET_ON(983),

	/**
	 * ID: 984<br>
	 * Message: The ferry will be leaving soon for Gludin Harbor.
	 */
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_GLUDIN_HARBOR(984),

	/**
	 * ID: 985<br>
	 * Message: The ferry is leaving for Gludin Harbor.
	 */
	THE_FERRY_IS_LEAVING_FOR_GLUDIN_HARBOR(985),

	/**
	 * ID: 986<br>
	 * Message: The ferry has arrived at Gludin Harbor.
	 */
	THE_FERRY_HAS_ARRIVED_AT_GLUDIN_HARBOR(986),

	/**
	 * ID: 987<br>
	 * Message: The ferry will leave for Talking Island Harbor after anchoring for ten minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(987),

	/**
	 * ID: 988<br>
	 * Message: The ferry will leave for Talking Island Harbor in five minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_FIVE_MINUTES(988),

	/**
	 * ID: 989<br>
	 * Message: The ferry will leave for Talking Island Harbor in one minute.
	 */
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_ONE_MINUTE(989),

	/**
	 * ID: 990<br>
	 * Message: The ferry will be leaving soon for Talking Island Harbor.
	 */
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_TALKING_ISLAND_HARBOR(990),

	/**
	 * ID: 991<br>
	 * Message: The ferry is leaving for Talking Island Harbor.
	 */
	THE_FERRY_IS_LEAVING_FOR_TALKING_ISLAND_HARBOR(991),

	/**
	 * ID: 992<br>
	 * Message: The ferry has arrived at Giran Harbor.
	 */
	THE_FERRY_HAS_ARRIVED_AT_GIRAN_HARBOR(992),

	/**
	 * ID: 993<br>
	 * Message: The ferry will leave for Giran Harbor after anchoring for ten minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(993),

	/**
	 * ID: 994<br>
	 * Message: The ferry will leave for Giran Harbor in five minutes.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_IN_FIVE_MINUTES(994),

	/**
	 * ID: 995<br>
	 * Message: The ferry will leave for Giran Harbor in one minute.
	 */
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_IN_ONE_MINUTE(995),

	/**
	 * ID: 996<br>
	 * Message: The ferry will be leaving soon for Giran Harbor.
	 */
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_GIRAN_HARBOR(996),

	/**
	 * ID: 997<br>
	 * Message: The ferry is leaving for Giran Harbor.
	 */
	THE_FERRY_IS_LEAVING_FOR_GIRAN_HARBOR(997),

	/**
	 * ID: 998<br>
	 * Message: The Innadril pleasure boat has arrived. It will anchor for ten minutes.
	 */
	THE_INNADRIL_PLEASURE_BOAT_HAS_ARRIVED_IT_WILL_ANCHOR_FOR_TEN_MINUTES(998),

	/**
	 * ID: 999<br>
	 * Message: The Innadril pleasure boat will leave in five minutes.
	 */
	THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_FIVE_MINUTES(999),
	
	/**
	 * ID: 1000<br>
	 * Message: The Innadril pleasure boat will leave in one minute.
	 */
	THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_ONE_MINUTE(1000),

	/**
	 * ID: 1001<br>
	 * Message: The Innadril pleasure boat will be leaving soon.
	 */
	THE_INNADRIL_PLEASURE_BOAT_WILL_BE_LEAVING_SOON(1001),

	/**
	 * ID: 1002<br>
	 * Message: The Innadril pleasure boat is leaving.
	 */
	THE_INNADRIL_PLEASURE_BOAT_IS_LEAVING(1002),

	/**
	 * ID: 1003<br>
	 * Message: Cannot process a monster race ticket.
	 */
	CANNOT_PROCESS_A_MONSTER_RACE_TICKET(1003),

	/**
	 * ID: 1004<br>
	 * Message: You have registered for a clan hall auction.
	 */
	YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION(1004),

	/**
	 * ID: 1005<br>
	 * Message: There is not enough adena in the clan hall warehouse.
	 */
	THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE(1005),

	/**
	 * ID: 1006<br>
	 * Message: You have bid in a clan hall auction.
	 */
	YOU_HAVE_BID_IN_A_CLAN_HALL_AUCTION(1006),

	/**
	 * ID: 1007<br>
	 * Message: The preliminary match registration of $s1 has finished.
	 */
	THE_PRELIMINARY_MATCH_REGISTRATION_OF_S1_HAS_FINISHED(1007),

	/**
	 * ID: 1008<br>
	 * Message: A hungry strider cannot be mounted or dismounted.
	 */
	A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED(1008),
	
	/**
	 * ID: 1009<br>
	 * Message: A strider cannot be ridden when dead.
	 */
	A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD(1009),
	
	/**
	 * ID: 1010<br>
	 * Message: A dead strider cannot be ridden.
	 */
	A_DEAD_STRIDER_CANNOT_BE_RIDDEN(1010),
	
	/**
	 * ID: 1011<br>
	 * Message: A strider in battle cannot be ridden.
	 */
	STRIDER_IN_BATLLE_CANT_BE_RIDDEN(1011),
	
	/**
	 * ID: 1012<br>
	 * Message: A strider cannot be ridden while in battle.
	 */
	STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE(1012),
	
	/**
	 * ID: 1013<br>
	 * Message: A strider can be ridden only when standing.
	 */
	STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING(1013),
	
	/**
	 * ID: 1014<br>
	 * Message: Your pet gained $s1 experience points.
	 */
	PET_EARNED_S1_EXP(1014),
	
	/**
	 * ID: 1015<br>
	 * Message: Your pet hit for $s1 damage.
	 */
	PET_HIT_FOR_S1_DAMAGE(1015),
	
	/**
	 * ID: 1016<br>
	 * Message: Your pet received $s2 damage caused by $s1.
	 */
	PET_RECEIVED_S2_DAMAGE_BY_S1(1016),
	
	/**
	 * ID: 1016<br>
	 * Message: Your pet received $s2 damage caused by $s1.
	 */
	S1_GAME_PET_S2_DMG(1016),
	
	/**
	 * ID: 1017<br>
	 * Message: Pet's critical hit!
	 */
	CRITICAL_HIT_BY_PET(1017),
	
	/**
	 * ID: 1018<br>
	 * Message: Your pet uses $s1.
	 */
	PET_USES_S1(1018),
	
	/**
	 * ID: 1026<br>
	 * Message: The summoned monster gave damage of $s1.
	 */
	SUMMON_GAVE_DAMAGE_S1(1026),
	
	/**
	 * ID: 1027<br>
	 * Message: The summoned monster received damage of $s2 caused by $s1.
	 */
	SUMMON_RECEIVED_DAMAGE_S2_BY_S1(1027),
	
	/**
	 * ID: 1028<br>
	 * Message: Summoned monster's critical hit!
	 */
	CRITICAL_HIT_BY_SUMMONED_MOB(1028),
	
	/**
	 * ID: 1030<br>
	 * Message: <Party Information>
	 */
	PARTY_INFORMATION(1030),
	
	/**
	 * ID: 1031<br>
	 * Message: Looting method: Finders keepers
	 */
	LOOTING_FINDERS_KEEPERS(1031),
	
	/**
	 * ID: 1032<br>
	 * Message: Looting method: Random
	 */
	LOOTING_RANDOM(1032),
	
	/**
	 * ID: 1033<br>
	 * Message: Looting method: Random including spoil
	 */
	LOOTING_RANDOM_INCLUDE_SPOIL(1033),
	
	/**
	 * ID: 1034<br>
	 * Message: Looting method: By turn
	 */
	LOOTING_BY_TURN(1034),
	
	/**
	 * ID: 1035<br>
	 * Message: Looting method: By turn including spoil
	 */
	LOOTING_BY_TURN_INCLUDE_SPOIL(1035),
	
	/**
	 * ID: 1036<br>
	 * Message: You have exceeded the quantity that can be inputted.
	 */
	YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED(1036),
	
	/**
	 * ID: 1039<br>
	 * Message: Items left at the clan hall warehouse can only be retrieved by the clan leader. Do you want to continue?
	 */
	ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE(1039),
	
	/**
	 * ID: 1041<br>
	 * Message: The next seed purchase price is $s1 adena.
	 */
	THE_NEXT_SEED_PURCHASE_PRICE_IS_S1_ADENA(1041),
	
	/**
	 * ID: 1044<br>
	 * Message: Monster race payout information is not available while tickets are being sold.
	 */
	MONSRACE_NO_PAYOUT_INFO(1044),
	
	/**
	 * ID: 1046<br>
	 * Message: Monster race tickets are no longer available.
	 */
	MONSRACE_TICKETS_NOT_AVAILABLE(1046),
	
	/**
	 * ID: 1050<br>
	 * Message: There are no communities in my clan. Clan communities are allowed for clans with skill levels of 2 and higher.
	 */
	NO_CB_IN_MY_CLAN(1050),
	
	/**
	 * ID: 1051<br>
	 * Message: Payment for your clan hall has not been made. Please make payment to your clan warehouse by $s1 tomorrow.
	 */
	PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW(1051),
	
	/**
	 * ID: 1052<br>
	 * Message: The clan hall fee is one week overdue; therefore the clan hall ownership has been revoked.
	 */
	THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED(1052),
	
	/**
	 * ID: 1053<br>
	 * Message: It is not possible to resurrect in battlefields where a siege war is taking place.
	 */
	CANNOT_BE_RESURRECTED_DURING_SIEGE(1053),
	
	/**
	 * ID: 1058<br>
	 * Message: The sales price for seeds is $s1 adena.
	 */
	THE_SALES_PRICE_FOR_SEEDS_IS_S1_ADENA(1058),
	
	/**
	 * ID: 1060<br>
	 * Message: The remainder after selling the seeds is $s1.
	 */
	THE_REMAINDER_AFTER_SELLING_THE_SEEDS_IS_S1(1060),
	
	/**
	 * ID: 1061<br>
	 * Message: The recipe cannot be registered. You do not have the ability to create items.
	 */
	CANT_REGISTER_NO_ABILITY_TO_CRAFT(1061),
	
	/**
	 * ID: 1064<br>
	 * Message: The equipment, +$s1 $s2, has been removed.
	 */
	EQUIPMENT_S1_S2_REMOVED(1064),
	
	/**
	 * ID: 1065<br>
	 * Message: While operating a private store or workshop, you cannot discard, destroy, or trade an item.
	 */
	CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE(1065),
	
	/**
	 * ID: 1066<br>
	 * Message: $s1 HP has been restored.
	 */
	S1_HP_RESTORED(1066),
	
	/**
	 * ID: 1067<br>
	 * Message: $s2 HP has been restored by $s1.
	 */
	S2_HP_RESTORED_BY_S1(1067),
	
	/**
	 * ID: 1068<br>
	 * Message: $s1 MP has been restored.
	 */
	S1_MP_RESTORED(1068),
	
	/**
	 * ID: 1069<br>
	 * Message: $s2 MP has been restored by $s1.
	 */
	S2_MP_RESTORED_BY_S1(1069),
	
	/**
	 * ID: 1112<br>
	 * Message: The prize amount for the winner of Lottery #$s1 is $s2 adena. We have $s3 first prize winners.
	 */
	AMOUNT_FOR_WINNER_S1_IS_S2_ADENA_WE_HAVE_S3_PRIZE_WINNER(1112),
	
	/**
	 * ID: 1113<br>
	 * Message: The prize amount for Lucky Lottery #$s1 is $s2 adena. There was no first prize winner in this drawing, therefore the jackpot will be added to the next drawing.
	 */
	AMOUNT_FOR_LOTTERY_S1_IS_S2_ADENA_NO_WINNER(1113),
	
	/**
	 * ID: 1114<br>
	 * Message: Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.
	 */
	CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS(1114),
	
	/**
	 * ID: 1116<br>
	 * Message: One cannot leave one's clan during combat.
	 */
	YOU_CANNOT_LEAVE_DURING_COMBAT(1116),
	
	/**
	 * ID: 1117<br>
	 * Message: A clan member may not be dismissed during combat.
	 */
	CLAN_MEMBER_CANNOT_BE_DISMISSED_DURING_COMBAT(1117),
	
	/**
	 * ID: 1118<br>
	 * Message: Progress in a quest is possible only when your inventory's weight and volume are less than 80 percent of capacity.
	 */
	INVENTORY_LESS_THAN_80_PERCENT(1118),
	
	/**
	 * ID: 1125<br>
	 * Message: An item may not be created while engaged in trading.
	 */
	CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING(1125),
	
	/**
	 * ID: 1128<br>
	 * Message: A private store may not be opened while using a skill.
	 */
	A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL(1128),
	
	/**
	 * ID: 1130<br>
	 * Message: You have given $s1 damage to your target and $s2 damage to the servitor.
	 */
	GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_SERVITOR(1130),
	
	/**
	 * ID: 1131<br>
	 * Message: It is now midnight and the effect of $s1 can be felt.
	 */
	NIGHT_EFFECT_APPLIES(1131),
	
	/**
	 * ID: 1132<br>
	 * Message: It is dawn and the effect of $s1 will now disappear.
	 */
	DAY_EFFECT_DISAPPEARS(1132),
	
	/**
	 * ID: 1135<br>
	 * Message: While you are engaged in combat, you cannot operate a private store or private workshop.
	 */
	CANT_CRAFT_DURING_COMBAT(1135),
	
	/**
	 * ID: 1137<br>
	 * Message: $s1 harvested $s3 $s2(s).
	 */
	S1_HARVESTED_S3_S2S(1137),
	
	/**
	 * ID: 1138<br>
	 * Message: $s1 harvested $s2(s).
	 */
	S1_HARVESTED_S2S(1138),
	
	/**
	 * ID: 1140<br>
	 * Message: Would you like to open the gate?
	 */
	WOULD_YOU_LIKE_TO_OPEN_THE_GATE(1140),
	
	/**
	 * ID: 1141<br>
	 * Message: Would you like to close the gate?
	 */
	WOULD_YOU_LIKE_TO_CLOSE_THE_GATE(1141),
	
	/**
	 * ID: 1146<br>
	 * Message: $s1 created $s2 after receiving $s3 adena.
	 */
	 S1_CREATED_S2_AFTER_RECEIVING_S3_ADENA(1146),
	
	/**
	 * ID: 1176<br>
	 * Message: This is a quest event period.
	 */
	QUEST_EVENT_PERIOD(1176),
	
	/**
	 * ID: 1177<br>
	 * Message: This is the seal validation period.
	 */
	VALIDATION_PERIOD(1177),
	
	/**
	 * ID: 1183<br>
	 * Message: This is the initial period.
	 */
	INITIAL_PERIOD(1183),
	
	/**
	 * ID: 1184<br>
	 * Message: This is a period of calculating statistics in the server.
	 */
	RESULTS_PERIOD(1184),
	
	/**
	 * ID: 1188<br>
	 * Message: Your selected target can no longer receive a recommendation.
	 */
	YOU_NO_LONGER_RECIVE_A_RECOMMENDATION(1188),
	
	/**
	 * ID: 1196<br>
	 * Message: Your force has reached maximum capacity.
	 */
	FORCE_MAXIMUM(1196),
	
	/**
	 * ID: 1197<br>
	 * Message: Summoning a servitor costs $s2 $s1.
	 */
	SUMMONING_SERVITOR_COSTS_S2_S1(1197),
	
	/**
	 * ID: 1200<br>
	 * Message: #Â¡REF!
	 */
	S1_S2_ALLIANCE(1200),
	
	/**
	 * ID: 1202<br>
	 * Message: #Â¡REF!
	 */
	S1_NO_ALLI_EXISTS(1202),
	
	/**
	 * ID: 1208<br>
	 * Message: $s1 died and dropped $s3 $s2.
	 */
	S1_DIED_DROPPED_S3_S2(1208),
	
	/**
	 * ID: 1209<br>
	 * Message: Congratulations. Your raid was successful.
	 */
	RAID_WAS_SUCCESSFUL(1209),
	
	/**
	 * ID: 1210<br>
	 * Message: Seven Signs: The quest event period has begun. Visit a Priest of Dawn or Priestess of Dusk to participate in the event.
	 */
	QUEST_EVENT_PERIOD_BEGUN(1210),
	
	/**
	 * ID: 1211<br>
	 * Message: Seven Signs: The quest event period has ended. The next quest event will start in one week.
	 */
	QUEST_EVENT_PERIOD_ENDED(1211),
	
	/**
	 * ID: 1212<br>
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Avarice.
	 */
	DAWN_OBTAINED_AVARICE(1212),
	
	/**
	 * ID: 1213<br>
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Gnosis.
	 */
	DAWN_OBTAINED_GNOSIS(1213),
	
	/**
	 * ID: 1214<br>
	 * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Strife.
	 */
	DAWN_OBTAINED_STRIFE(1214),
	
	/**
	 * ID: 1215<br>
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Avarice.
	 */
	DUSK_OBTAINED_AVARICE(1215),
	
	/**
	 * ID: 1216<br>
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Gnosis.
	 */
	DUSK_OBTAINED_GNOSIS(1216),
	
	/**
	 * ID: 1217<br>
	 * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Strife.
	 */
	DUSK_OBTAINED_STRIFE(1217),
	
	/**
	 * ID: 1218<br>
	 * Message: Seven Signs: The Seal Validation period has begun.
	 */
	SEAL_VALIDATION_PERIOD_BEGUN(1218),
	
	/**
	 * ID: 1219<br>
	 * Message: Seven Signs: The Seal Validation period has ended.
	 */
	SEAL_VALIDATION_PERIOD_ENDED(1219),
	
	/**
	 * ID: 1235<br>
	 * Message: Do you wish to delete all your friends?
	 */
	DO_YOU_WISH_TO_DELETE_FRIENDLIST(1235),
	
	/**
	 * ID: 1240<br>
	 * Message: Seven Signs: The Revolutionaries of Dusk have won.
	 */
	DUSK_WON(1240),
	
	/**
	 * ID: 1241<br>
	 * Message: Seven Signs: The Lords of Dawn have won.
	 */
	DAWN_WON(1241),
	
	/**
	 * ID: 1260<br>
	 * Message: Seven Signs: Preparations have begun for the next quest event.
	 */
	PREPARATIONS_PERIOD_BEGUN(1260),
	
	/**
	 * ID: 1261<br>
	 * Message: Seven Signs: The quest event period has begun. Speak with a Priest of Dawn or Dusk Priestess if you wish to participate in the event.
	 */
	COMPETITION_PERIOD_BEGUN(1261),
	
	/**
	 * ID: 1262<br>
	 * Message: Seven Signs: Quest event has ended. Results are being tallied.
	 */
	RESULTS_PERIOD_BEGUN(1262),
	
	/**
	 * ID: 1263<br>
	 * Message: Seven Signs: This is the seal validation period. A new quest event period begins next Monday.
	 */
	VALIDATION_PERIOD_BEGUN(1263),
	
	/**
	 * ID: 1267<br>
	 * Message: Your contribution score is increased by $s1.
	 */
	CONTRIB_SCORE_INCREASED(1267),
	
	/**
	 * ID: 1269<br>
	 * Message: The new sub class has been added.
	 */
	ADD_NEW_SUBCLASS(1269),
	
	/**
	 * ID: 1270<br>
	 * Message: The transfer of sub class has been completed.
	 */
	SUBCLASS_TRANSFER_COMPLETED(1270),
	
	/**
	 * ID: 1273<br>
	 * Message: You will participate in the Seven Signs as a member of the Lords of Dawn.
	 */
	SEVENSIGNS_PARTECIPATION_DAWN(1273),
	
	/**
	 * ID: 1274<br>
	 * Message: You will participate in the Seven Signs as a member of the Revolutionaries of Dusk.
	 */
	SEVENSIGNS_PARTECIPATION_DUSK(1274),
	
	/**
	 * ID: 1275<br>
	 * Message: You've chosen to fight for the Seal of Avarice during this quest event period.
	 */
	FIGHT_FOR_AVARICE(1275),
	
	/**
	 * ID: 1276<br>
	 * Message: You've chosen to fight for the Seal of Gnosis during this quest event period.
	 */
	FIGHT_FOR_GNOSIS(1276),
	
	/**
	 * ID: 1277<br>
	 * Message: You've chosen to fight for the Seal of Strife during this quest event period.
	 */
	FIGHT_FOR_STRIFE(1277),
	
	/**
	 * ID: 1278<br>
	 * Message: The NPC server is not operating at this time.
	 */
	NPC_SERVER_NOT_OPERATING(1278),
	
	/**
	 * ID: 1279<br>
	 * Message: Contribution level has exceeded the limit. You may not continue.
	 */
	CONTRIB_SCORE_EXCEEDED(1279),
	
	/**
	 * ID: 1280<br>
	 * Message: Magic Critical Hit!
	 */
	CRITICAL_HIT_MAGIC(1280),
	
	/**
	 * ID: 1281<br>
	 * Message: Your excellent shield defense was a success!
	 */
	YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS(1281),
	
	/**
	 * ID: 1282<br>
	 * Message: Your Karma has been changed to $s1.
	 */
	YOUR_KARMA_HAS_BEEN_CHANGED_TO(1282),
	
	/**
	 * ID: 1286<br>
	 * Message: (Until next Monday at 6:00 p.m.)
	 */
	UNTIL_MONDAY_6PM(1286),
	
	/**
	 * ID: 1287<br>
	 * Message: (Until today at 6:00 p.m.)
	 */
	UNTIL_TODAY_6PM(1287),
	
	/**
	 * ID: 1289<br>
	 * Message: Since the seal was owned during the previous period and 10 percent or more people have voted.
	 */
	SEAL_OWNED_10_MORE_VOTED(1289),
	
	/**
	 * ID: 1290<br>
	 * Message: Although the seal was not owned, since 35 percent or more people have voted.
	 */
	SEAL_NOT_OWNED_35_MORE_VOTED(1290),
	
	/**
	 * ID: 1291<br>
	 * Message: Although the seal was owned during the previous period, because less than 10 percent of people have voted.
	 */
	SEAL_OWNED_10_LESS_VOTED(1291),
	
	/**
	 * ID: 1292<br>
	 * Message: Since the seal was not owned during the previous period, and since less than 35 percent of people have voted.
	 */
	SEAL_NOT_OWNED_35_LESS_VOTED(1292),
	
	/**
	 * ID: 1294<br>
	 * Message: The competition has ended in a tie. Therefore, nobody has been awarded the seal.
	 */
	COMPETITION_TIE_SEAL_NOT_AWARDED(1294),
	
	/**
	 * ID: 1295<br>
	 * Message: Sub classes may not be created or changed while a skill is in use.
	 */
	SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE(1295),
	
	/**
	 * ID: 1296<br>
	 * Message: You cannot open a Private Store here.
	 */
	YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE(1296),
	
	/**
	 * ID: 1301<br>
	 * Message: Only a Lord of Dawn may use this.
	 */
	CAN_BE_USED_BY_DAWN(1301),
	
	/**
	 * ID: 1302<br>
	 * Message: Only a Revolutionary of Dusk may use this.
	 */
	CAN_BE_USED_BY_DUSK(1302),
	
	/**
	 * ID: 1308<br>
	 * Message: Congratulations - You've completed a class transfer!
	 */
	CLASS_TRANSFER(1308),
	
	/**
	 * ID: 1384<br>
	 * Message: $s1 has become the party leader.
	 */
	S1_HAS_BECOME_A_PARTY_LEADER(1384),
	
	/**
	 * ID: 1388<br>
	 * Message: A party room has been created.
	 */
	PARTY_ROOM_CREATED(1388),
	
	/**
	 * ID: 1389<br>
	 * Message: The party room's information has been revised.
	 */
	PARTY_ROOM_REVISED(1389),
	
	/**
	 * ID: 1390<br>
	 * Message: You are not allowed to enter the party room.
	 */
	PARTY_ROOM_FORBIDDEN(1390),
	
	/**
	 * ID: 1391<br>
	 * Message: You have exited from the party room.
	 */
	PARTY_ROOM_EXITED(1391),
	
	/**
	 * ID: 1392<br>
	 * Message: $s1 has left the party room.
	 */
	S1_LEFT_PARTY_ROOM(1392),
	
	/**
	 * ID: 1393<br>
	 * Message: You have been ousted from the party room.
	 */
	OUSTED_FROM_PARTY_ROOM(1393),
	
	/**
	 * ID: 1394<br>
	 * Message: $s1 has been ousted from the party room.
	 */
	S1_KICKED_FROM_PARTY_ROOM(1394),
	
	/**
	 * ID: 1395<br>
	 * Message: The party room has been disbanded.
	 */
	PARTY_ROOM_DISBANDED(1395),
	
	/**
	 * ID: 1396<br>
	 * Message: The list of party rooms can only be viewed by a person who has not joined a party or who is currently the leader of a party.
	 */
	CANT_VIEW_PARTY_ROOMS(1396),
	
	/**
	 * ID: 1397<br>
	 * Message: The leader of the party room has changed.
	 */
	PARTY_ROOM_LEADER_CHANGED(1397),
	
	/**
	 * ID: 1399<br>
	 * Message: Only the leader of the party can transfer party leadership to another player.
	 */
	ONLY_A_PARTY_LEADER_CAN_TRANSFER_ONES_RIGHTS_TO_ANOTHER_PLAYER(1399),
	
	/**
	 * ID: 1400<br>
	 * Message: Please select the person you wish to make the party leader.
	 */
	PLEASE_SELECT_THE_PERSON_TO_WHOM_YOU_WOULD_LIKE_TO_TRANSFER_THE_RIGHTS_OF_A_PARTY_LEADER(1400),
	
	/**
	 * ID: 1401<br>
	 * Message: Slow down, you are already the party leader.
	 */
	YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF(1401),
	
	/**
	 * ID: 1402<br>
	 * Message: You may only transfer party leadership to another member of the party.
	 */
	YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER(1402),
	
	/**
	 * ID: 1403<br>
	 * Message: You have failed to transfer the party leadership.
	 */
	YOU_HAVE_FAILED_TO_TRANSFER_THE_PARTY_LEADER_RIGHTS(1403),
	
	/**
	 * ID: 1405<br>
	 * Message: $s1 CPs have been restored.
	 */
	S1_CP_WILL_BE_RESTORED(1405),
	
	/**
	 * ID: 1413<br>
	 * Message: You do not meet the requirements to enter that party room.
	 */
	CANT_ENTER_PARTY_ROOM(1413),
	
	/**
	 * ID: 1433<br>
	 * Message: The automatic use of $s1 has been activated.
	 */
	USE_OF_S1_WILL_BE_AUTO(1433),
	
	/**
	 * ID: 1434<br>
	 * Message: The automatic use of $s1 has been deactivated.
	 */
	AUTO_USE_OF_S1_CANCELLED(1434),
	
	/**
	 * ID: 1435<br>
	 * Message: Due to insufficient $s1, the automatic use function has been deactivated.
	 */
	AUTO_USE_CANCELLED_LACK_OF_S1(1435),
	
	/**
	 * ID: 1436<br>
	 * Message: Due to insufficient $s1, the automatic use function cannot be activated.
	 */
	CANNOT_AUTO_USE_LACK_OF_S1(1436),
	
	/**
	 * ID: 1438<br>
	 * Message: There is no skill that enables enchant.
	 */
	THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT(1438),
	
	/**
	 * ID: 1439<br>
	 * Message: You do not have all of the items needed to enchant that skill.
	 */
	YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL(1439),
	
	/**
	 * ID: 1440<br>
	 * Message: Skill enchant was successful! $s1 has been enchanted.
	 */
	YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1(1440),
	
	/**
	 * ID: 1441<br>
	 * Message: Skill enchant failed. The skill will be initialized.
	 */
	YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1(1441),
	
	/**
	 * ID: 1443<br>
	 * Message: You do not have enough SP to enchant that skill.
	 */
	YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL(1443),
	
	/**
	 * ID: 1444<br>
	 * Message: You do not have enough experience (Exp) to enchant that skill.
	 */
	YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL(1444),
	
	/**
	 * ID: 1447<br>
	 * Message: You cannot do that while fishing.
	 */
	CANNOT_DO_WHILE_FISHING_1(1447),
	
	/**
	 * ID: 1448<br>
	 * Message: Only fishing skills may be used at this time.
	 */
	ONLY_FISHING_SKILLS_NOW(1448),
	
	/**
	 * ID: 1449<br>
	 * Message: You've got a bite!
	 */
	GOT_A_BITE(1449),
	
	/**
	 * ID: 1450<br>
	 * Message: That fish is more determined than you are - it spit the hook!
	 */
	FISH_SPIT_THE_HOOK(1450),
	
	/**
	 * ID: 1451<br>
	 * Message: Your bait was stolen by that fish!
	 */
	BAIT_STOLEN_BY_FISH(1451),
	
	/**
	 * ID: 1452<br>
	 * Message: Baits have been lost because the fish got away.
	 */
	BAIT_LOST_FISH_GOT_AWAY(1452),
	
	/**
	 * ID: 1453<br>
	 * Message: You do not have a fishing pole equipped.
	 */
	FISHING_POLE_NOT_EQUIPPED(1453),
	
	/**
	 * ID: 1454<br>
	 * Message: You must put bait on your hook before you can fish.
	 */
	BAIT_ON_HOOK_BEFORE_FISHING(1454),
	
	/**
	 * ID: 1455<br>
	 * Message: You cannot fish while under water.
	 */
	CANNOT_FISH_UNDER_WATER(1455),
	
	/**
	 * ID: 1456<br>
	 * Message: You cannot fish while riding as a passenger of a boat - it's against the rules.
	 */
	CANNOT_FISH_ON_BOAT(1456),
	
	/**
	 * ID: 1457<br>
	 * Message: You can't fish here.
	 */
	CANNOT_FISH_HERE(1457),
	
	/**
	 * ID: 1458<br>
	 * Message: Your attempt at fishing has been cancelled.
	 */
	FISHING_ATTEMPT_CANCELLED(1458),
	
	/**
	 * ID: 1459<br>
	 * Message: You do not have enough bait.
	 */
	NOT_ENOUGH_BAIT(1459),
	
	/**
	 * ID: 1460<br>
	 * Message: You reel your line in and stop fishing.
	 */
	REEL_LINE_AND_STOP_FISHING(1460),
	
	/**
	 * ID: 1461<br>
	 * Message: You cast your line and start to fish.
	 */
	CAST_LINE_AND_START_FISHING(1461),
	
	/**
	 * ID: 1462<br>
	 * Message: You may only use the Pumping skill while you are fishing.
	 */
	CAN_USE_PUMPING_ONLY_WHILE_FISHING(1462),
	
	/**
	 * ID: 1463<br>
	 * Message: You may only use the Reeling skill while you are fishing.
	 */
	CAN_USE_REELING_ONLY_WHILE_FISHING(1463),
	
	/**
	 * ID: 1464<br>
	 * Message: The fish has resisted your attempt to bring it in.
	 */
	FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN(1464),
	
	/**
	 * ID: 1465<br>
	 * Message: Your pumping is successful, causing $s1 damage.
	 */
	PUMPING_SUCCESFUL_S1_DAMAGE(1465),
	
	/**
	 * ID: 1466<br>
	 * Message: You failed to do anything with the fish and it regains $s1 HP.
	 */
	FISH_RESISTED_PUMPING_S1_HP_REGAINED(1466),
	
	/**
	 * ID: 1467<br>
	 * Message: You reel that fish in closer and cause $s1 damage.
	 */
	REELING_SUCCESFUL_S1_DAMAGE(1467),
	
	/**
	 * ID: 1468<br>
	 * Message: You failed to reel that fish in further and it regains $s1 HP.
	 */
	FISH_RESISTED_REELING_S1_HP_REGAINED(1468),
	
	/**
	 * ID: 1469<br>
	 * Message: You caught something!
	 */
	YOU_CAUGHT_SOMETHING(1469),
	
	/**
	 * ID: 1470<br>
	 * Message: You cannot do that while fishing.
	 */
	CANNOT_DO_WHILE_FISHING_2(1470),
	
	/**
	 * ID: 1471<br>
	 * Message: You cannot do that while fishing.
	 */
	CANNOT_DO_WHILE_FISHING_3(1471),
	
	/**
	 * ID: 1472<br>
	 * Message: You look oddly at the fishing pole in disbelief and realize that you can't attack anything with this.
	 */
	CANNOT_ATTACK_WITH_FISHING_POLE(1472),
	
	/**
	 * ID: 1479<br>
	 * Message: That is the wrong grade of soulshot for that fishing pole.
	 */
	WRONG_FISHINGSHOT_GRADE(1479),
	
	/**
	 * ID: 1490<br>
	 * Message: Traded $s2 of crop $s1.
	 */
	TRADED_S2_OF_CROP_S1(1490),
	
	/**
	 * ID: 1491<br>
	 * Message: Failed in trading $s2 of crop $s1.
	 */
	FAILED_IN_TRADING_S2_OF_CROP_S1(1491),
	
	/**
	 * ID: 1492<br>
	 * Message: You will be moved to the Olympiad Stadium in $s1 second(s).
	 */
	YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S(1492),
	
	/**
	 * ID: 1493<br>
	 * Message: Your opponent made haste with their tail between their legs; the match has been cancelled.
	 */
	THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME(1493),
	
	/**
	 * ID: 1494<br>
	 * Message: Your opponent does not meet the requirements to do battle; the match has been cancelled.
	 */
	THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME(1494),
	
	/**
	 * ID: 1495<br>
	 * Message: The Grand Olympiad match will start in $s1 second(s).
	 */
	THE_GAME_WILL_START_IN_S1_SECOND_S(1495),
	
	/**
	 * ID: 1496<br>
	 * Message: The match has started, fight!
	 */
	STARTS_THE_GAME(1496),
	
	/**
	 * ID: 1497<br>
	 * Message: Congratulations $s1, you win the match!
	 */
	S1_HAS_WON_THE_GAME(1497),
	
	/**
	 * ID: 1498<br>
	 * Message: There is no victor; the match ends in a tie.
	 */
	THE_GAME_ENDED_IN_A_TIE(1498),
	
	/**
	 * ID: 1499<br>
	 * Message: You will be moved back to town in $s1 second(s).
	 */
	YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S(1499),
	
	/**
	 * ID: 1499<br>
	 * Message: You will be moved back to town in $s1 second(s).
	 */
	YOU_WILL_BE_MOVED_TO_TOWN_IN_S1_SECONDS(1499),
	
	/**
	 * ID: 1500<br>
	 * Message: You cannot participate in the Grand Olympiad Games with a character in their subclass.
	 */
	YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER(1500),
	
	/**
	 * ID: 1501<br>
	 * Message: Only Noblesse can participate in the Olympiad.
	 */
	ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD(1501),
	
	/**
	 * ID: 1502<br>
	 * Message: You have already been registered in a waiting list of an event.
	 */
	YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT(1502),
	
	/**
	 * ID: 1503<br>
	 * Message: You have been registered in the Grand Olympiad Games waiting list for a class specific match.
	 */
	YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES(1503),
	
	/**
	 * ID: 1504<br>
	 * Message: You have been registered in the Grand Olympiad Games waiting list for a non-class specific match.
	 */
	YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES(1504),
	
	/**
	 * ID: 1505<br>
	 * Message: You have been removed from the Grand Olympiad Games waiting list.
	 */
	YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME(1505),
	
	/**
	 * ID: 1506<br>
	 * Message: You are not currently registered on any Grand Olympiad Games waiting list.
	 */
	YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME(1506),
	
	/**
	 * ID: 1507<br>
	 * Message: You cannot equip that item in a Grand Olympiad Games match.
	 */
	THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT(1507),
	
	/**
	 * ID: 1508<br>
	 * Message: You cannot use that item in a Grand Olympiad Games match.
	 */
	THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT(1508),
	
	/**
	 * ID: 1509<br>
	 * Message: You cannot use that skill in a Grand Olympiad Games match.
	 */
	THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT(1509),
	
	/**
	 * ID: 1510<br>
	 * Message: $s1 is making an attempt at resurrection. Do you want to continue with this resurrection?
	 */
	RESSURECTION_REQUEST(1510),
	
	/**
	 * ID: 1511<br>
	 * Message: While a pet is attempting to resurrect, it cannot help in resurrecting its master.
	 */
	MASTER_CANNOT_RES(1511),
	
	/**
	 * ID: 1513<br>
	 * Message: Resurrection has already been proposed.
	 */
	RES_HAS_ALREADY_BEEN_PROPOSED(1513),
	
	/**
	 * ID: 1515<br>
	 * Message: A pet cannot be resurrected while it's owner is in the process of resurrecting.
	 */
	PET_CANNOT_RES(1515),
	
	/**
	 * ID: 1516<br>
	 * Message: The target is unavailable for seeding.
	 */
	THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING(1516),
	
	/**
	 * ID: 1517<br>
	 * Message: Failed in Blessed Enchant. The enchant value of the item became 0.
	 */
	BLESSED_ENCHANT_FAILED(1517),
	
	/**
	 * ID: 1518<br>
	 * Message: You do not meet the required condition to equip that item.
	 */
	YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM(1518),
	
	/**
	 * ID: 1527<br>
	 * Message: Your pet was hungry so it ate $s1.
	 */
	PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY(1527),
	
	/**
	 * ID: 1529<br>
	 * Message: $s1 has invited you to join a Command Channel. Do you wish to accept?
	 */
	COMMAND_CHANNEL_CONFIRM(1529),
	
	/**
	 * ID: 1533<br>
	 * Message: Attention: $s1 picked up $s2.
	 */
	ATTENTION_S1_PICKED_UP_S2(1533),
	
	/**
	 * ID: 1534<br>
	 * Message: Attention: $s1 picked up +$s2 $s3.
	 */
	ATTENTION_S1_PICKED_UP_S2_S3(1534),
	
	/**
	 * ID: 1537<br>
	 * Message: Current Location: $s1, $s2, $s3 (near Rune Village)
	 */
	LOC_RUNE_S1_S2_S3(1537),
	
	/**
	 * ID: 1538<br>
	 * Message: Current Location: $s1, $s2, $s3 (near the Town of Goddard)
	 */
	LOC_GODDARD_S1_S2_S3(1538),
	
	/**
	 * ID: 1557<br>
	 * Message: Seed price should be more than $s1 and less than $s2.
	 */
	SEED_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1557),
	
	/**
	 * ID: 1558<br>
	 * Message: The quantity of seed should be more than $s1 and less than $s2.
	 */
	THE_QUANTITY_OF_SEED_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1558),
	
	/**
	 * ID: 1559<br>
	 * Message: Crop price should be more than $s1 and less than $s2.
	 */
	CROP_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1559),
	
	/**
	 * ID: 1560<br>
	 * Message: The quantity of crop should be more than $s1 and less than $s2 .
	 */
	THE_QUANTITY_OF_CROP_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1560),
	
	/**
	 * ID: 1561<br>
	 * Message: The clan, $s1, has declared a Clan War.
	 */
	CLAN_S1_DECLARED_WAR(1561),
	
	/**
	 * ID: 1562<br>
	 * Message: A Clan War has been declared against the clan, $s1. If you are killed during the Clan War by members of the opposing clan, you will only lose a quarter of the normal experience from death.
	 */
	CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP(1562),
	
	/**
	 * ID: 1564<br>
	 * Message: A Clan War can be declared only if the clan is level three or above, and the number of clan members is fifteen or greater.
	 */
	CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER(1564),
	
	/**
	 * ID: 1565<br>
	 * Message: A Clan War cannot be declared against a clan that does not exist!
	 */
	CLAN_WAR_CANNOT_DECLARED_CLAN_NOT_EXIST(1565),
	
	/**
	 * ID: 1566<br>
	 * Message: The clan, $s1, has decided to stop the war.
	 */
	CLAN_S1_HAS_DECIDED_TO_STOP(1566),
	
	/**
	 * ID: 1567<br>
	 * Message: The war against $s1 Clan has been stopped.
	 */
	WAR_AGAINST_S1_HAS_STOPPED(1567),
	
	/**
	 * ID: 1569<br>
	 * Message: A declaration of Clan War against an allied clan can't be made.
	 */
	CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK(1569),
	
	/**
	 * ID: 1571<br>
	 * Message: ======<Clans You've Declared War On>======
	 */
	CLANS_YOU_DECLARED_WAR_ON(1571),
	
	/**
	 * ID: 1572<br>
	 * Message: ======<Clans That Have Declared War On You>======
	 */
	CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU(1572),
	
	/**
	 * ID: 1575<br>
	 * Message: Command Channels can only be formed by a party leader who is also the leader of a level 5 clan.
	 */
	COMMAND_CHANNEL_ONLY_BY_LEVEL_5_CLAN_LEADER_PARTY_LEADER(1575),
	
	/**
	 * ID: 1576<br>
	 * Message: Pet uses the power of spirit.
	 */
	PET_USE_THE_POWER_OF_SPIRIT(1576),
	
	/**
	 * ID: 1580<br>
	 * Message: The Command Channel has been formed.
	 */
	COMMAND_CHANNEL_FORMED(1580),
	
	/**
	 * ID: 1581<br>
	 * Message: The Command Channel has been disbanded.
	 */
	COMMAND_CHANNEL_DISBANDED(1581),
	
	/**
	 * ID: 1582<br>
	 * Message: You have joined the Command Channel.
	 */
	JOINED_COMMAND_CHANNEL(1582),
	
	/**
	 * ID: 1586<br>
	 * Message: You have quit the Command Channel.
	 */
	LEFT_COMMAND_CHANNEL(1586),
	
	/**
	 * ID: 1587<br>
	 * Message: $s1's party has left the Command Channel.
	 */
	S1_PARTY_LEFT_COMMAND_CHANNEL(1587),
	
	/**
	 * ID: 1589<br>
	 * Message: Command Channel authority has been transferred to $s1.
	 */
	COMMAND_CHANNEL_LEADER_NOW_S1(1589),
	
	/**
	 * ID: 1593<br>
	 * Message: You do not have authority to invite someone to the Command Channel.
	 */
	CANNOT_INVITE_TO_COMMAND_CHANNEL(1593),
	
	/**
	 * ID: 1594<br>
	 * Message: $s1's party is already a member of the Command Channel.
	 */
	S1_ALREADY_MEMBER_OF_COMMAND_CHANNEL(1594),
	
	/**
	 * ID: 1598<br>
	 * Message: Soulshots and spiritshots are not available for a dead pet or servitor. Sad, isn't it?
	 */
	SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET(1598),
	
	/**
	 * ID: 1604<br>
	 * Message: While dressed in formal wear, you can't use items that require all skills and casting operations.
	 */
	CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR(1604),
	
	/**
	 * ID: 1605<br>
	 * Message: * Here, you can buy only seeds of $s1 Manor.
	 */
	HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR(1605),
	
	/**
	 * ID: 1606<br>
	 * Message: Congratulations - You've completed the third-class transfer quest!
	 */
	THIRD_CLASS_TRANSFER(1606),
	
	/**
	 * ID: 1607<br>
	 * Message: $s1 adena has been withdrawn to pay for purchasing fees.
	 */
	S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES(1607),
	
	/**
	 * ID: 1611<br>
	 * Message: Party Leader: $s1
	 */
	PARTY_LEADER_S1(1611),
	
	/**
	 * ID: 1612<br>
	 * Message: =====<War List>=====
	 */
	WAR_LIST(1612),
	
	/**
	 * ID: 1638<br>
	 * Message: You cannot fish while using a recipe book, private manufacture or private store.
	 */
	CANNOT_FISH_WHILE_USING_RECIPE_BOOK(1638),
	
	/**
	 * ID: 1639<br>
	 * Message: Period $s1 of the Grand Olympiad Games has started!
	 */
	OLYMPIAD_PERIOD_S1_HAS_STARTED(1639),
	
	/**
	 * ID: 1640<br>
	 * Message: Period $s1 of the Grand Olympiad Games has now ended.
	 */
	OLYMPIAD_PERIOD_S1_HAS_ENDED(1640),
	
	/**
	 * ID: 1641<br>
	 * Message: Sharpen your swords, tighten the stitchings in your armor, and make haste to a Grand Olympiad Manager! Battles in the Grand Olympiad Games are now taking place!
	 */
	THE_OLYMPIAD_GAME_HAS_STARTED(1641),
	
	/**
	 * ID: 1642<br>
	 * Message: Much carnage has been left for the cleanup crew of the Olympiad Stadium. Battles in the Grand Olympiad Games are now over!
	 */
	THE_OLYMPIAD_GAME_HAS_ENDED(1642),
	
	/**
	 * ID: 1651<br>
	 * Message: The Grand Olympiad Games are not currently in progress.
	 */
	THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS(1651),
	
	/**
	 * ID: 1655<br>
	 * Message: You caught something smelly and scary, maybe you should throw it back!?
	 */
	YOU_CAUGHT_SOMETHING_SMELLY_THROW_IT_BACK(1655),
	
	/**
	 * ID: 1657<br>
	 * Message: $s1 has earned $s2 points in the Grand Olympiad Games.
	 */
	S1_HAS_GAINED_S2_OLYMPIAD_POINTS(1657),
	
	/**
	 * ID: 1658<br>
	 * Message: $s1 has lost $s2 points in the Grand Olympiad Games.
	 */
	S1_HAS_LOST_S2_OLYMPIAD_POINTS(1658),
	
	/**
	 * ID: 1662<br>
	 * Message: The fish are no longer biting here because you've caught too many! Try fishing in another location.
	 */
	FISH_NO_MORE_BITING_TRY_OTHER_LOCATION(1662),
	
	/**
	 * ID: 1663<br>
	 * Message: The clan crest was successfully registered. Remember, only a clan that owns a clan hall or castle can have their crest displayed.
	 */
	CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED(1663),
	
	/**
	 * ID: 1664<br>
	 * Message: The fish is resisting your efforts to haul it in! Look at that bobber go!
	 */
	FISH_RESISTING_LOOK_BOBBLER(1664),
	
	/**
	 * ID: 1665<br>
	 * Message: You've worn that fish out! It can't even pull the bobber under the water!
	 */
	YOU_WORN_FISH_OUT(1665),
	
	/**
	 * ID: 1667<br>
	 * Message: Lethal Strike!
	 */
	LETHAL_STRIKE(1667),
	
	/**
	 * ID: 1668<br>
	 * Message: Your lethal strike was successful!
	 */
	LETHAL_STRIKE_SUCCESSFUL(1668),
	
	/**
	 * ID: 1669<br>
	 * Message: There was nothing found inside of that.
	 */
	NOTHING_INSIDE_THAT(1669),
	
	/**
	 * ID: 1670<br>
	 * Message: Due to your Reeling and/or Pumping skill being three or more levels higher than your Fishing skill, a 50 damage penalty will be applied.
	 */
	REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY(1670),
	
	/**
	 * ID: 1671<br>
	 * Message: Your reeling was successful! (Mastery Penalty:$s1 )
	 */
	REELING_SUCCESSFUL_PENALTY_S1(1671),
	
	/**
	 * ID: 1672<br>
	 * Message: Your pumping was successful! (Mastery Penalty:$s1 )
	 */
	PUMPING_SUCCESSFUL_PENALTY_S1(1672),
	
	/**
	 * ID: 1673<br>
	 * Message: Your current record for this Grand Olympiad is $s1 match(es), $s2 win(s) and $s3 defeat(s). You have earned $s4 Olympiad Point(s).
	 */
	THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS(1673),
	
	/**
	 * ID: 1675<br>
	 * Message: A manor cannot be set up between 6 a.m. and 8 p.m.
	 */
	A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM(1675),
	
	/**
	 * ID: 1676<br>
	 * Message: You do not have a servitor or pet and therefore cannot use the automatic-use function.
	 */
	NO_SERVITOR_CANNOT_AUTOMATE_USE(1676),
	
	/**
	 * ID: 1680<br>
	 * Message: $s1 has declined the channel invitation.
	 */
	S1_DECLINED_CHANNEL_INVITATION(1680),
	
	/**
	 * ID: 1685<br>
	 * Message: You are unable to equip this item when your PK count is greater than or equal to one.
	 */
	YOUT_ARE_UNABLE_TO_EQUIP_THIS_ITEM_WHEN_YOU_PK(1685),
	
	/**
	 * ID: 1689<br>
	 * Message: You have already joined the waiting list for a class specific match.
	 */
	YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS(1689),
	
	/**
	 * ID: 1690<br>
	 * Message: You have already joined the waiting list for a non-class specific match.
	 */
	YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME(1690),
	
	/**
	 * ID: 1691<br>
	 * Message: You can't join a Grand Olympiad Game match with that much stuff on you! Reduce your weight to below 80 percent full and request to join again!
	 */
	SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1691),
	
	/**
	 * ID: 1692<br>
	 * Message: You have changed from your main class to a subclass and therefore are removed from the Grand Olympiad Games waiting list.
	 */
	SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1692),
	
	/**
	 * ID: 1693<br>
	 * Message: You may not observe a Grand Olympiad Games match while you are on the waiting list.
	 */
	WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME(1693),
	
	/**
	 * ID: 1694<br>
	 * Message: Only a clan leader that is a Noblesse can view the Siege War Status window during a siege war.
	 */
	ONLY_NOBLESSE_LEADER_CAN_VIEW_SIEGE_STATUS_WINDOW(1694),
	
	/**
	 * ID: 1699<br>
	 * Message: You cannot dismiss a party member by force.
	 */
	CANNOT_DISMISS_PARTY_MEMBER(1699),
	
	/**
	 * ID: 1700<br>
	 * Message: You don't have enough spiritshots needed for a pet/servitor.
	 */
	NOT_ENOUGH_SPIRITHOTS_FOR_PET(1700),
	
	/**
	 * ID: 1701<br>
	 * Message: You don't have enough soulshots needed for a pet/servitor.
	 */
	NOT_ENOUGH_SOULSHOTS_FOR_PET(1701),
	
	/**
	 * ID: 1707<br>
	 * Message: You acquired $s1 PC Bang Point.
	 */
	YOU_RECEVIED_$51_GLASSES_PC(1707),
	
	/**
	 * ID: 1708<br>
	 * Message: Double points! You acquired $s1 PC Bang Point.
	 */
	DOUBLE_POINTS_YOU_GOT_$51_GLASSES_PC(1708),
	
	/**
	 * ID: 1709<br>
	 * Message: You are using $s1 point.
	 */
	USING_S1_PCPOINT(1709),
	
	/**
	 * ID: 1714<br>
	 * Message: Current Location: $s1, $s2, $s3 (Near the Town of Schuttgart)
	 */
	LOC_SCHUTTGART_S1_S2_S3(1714),
	
	/**
	 * ID: 1727<br>
	 * Message: $s1 has invited you to a party room. Would you like to accept the invitation?
	 */
	S1_INVITED_YOU_TO_PARTY_ROOM_CONFIRM(1727),
	
	/**
	 * ID: 1728<br>
	 * Message: The recipient of your invitation did not accept the party matching invitation.
	 */
	PARTY_MATCHING_REQUEST_NO_RESPONSE(1728),
	
	/**
	 * ID: 1730<br>
	 * Message: To establish a Clan Academy, your clan must be Level 5 or higher.
	 */
	YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN_ACADEMY(1730),
	
	/**
	 * ID: 1734<br>
	 * Message: To join a Clan Academy, characters must be Level 40 or below, not belong another clan and not yet completed their 2nd class transfer.
	 */
	ACADEMY_REQUIREMENTS(1734),
	
	/**
	 * ID: 1735<br>
	 * Message: $s1 does not meet the requirements to join a Clan Academy.
	 */
	S1_DOESNOT_MEET_REQUIREMENTS_TO_JOIN_ACADEMY(1735),
	
	/**
	 * ID: 1738<br>
	 * Message: Your clan has already established a Clan Academy.
	 */
	CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY(1738),
	
	/**
	 * ID: 1741<br>
	 * Message: Congratulations! The $s1's Clan Academy has been created.
	 */
	THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED(1741),
	
	/**
	 * ID: 1748<br>
	 * Message: Clan Academy member $s1 has successfully completed the 2nd class transfer and obtained $s2 Clan Reputation points.
	 */
	CLAN_MEMBER_GRADUATED_FROM_ACADEMY(1748),
	
	/**
	 * ID: 1749<br>
	 * Message: Congratulations! You will now graduate from the Clan Academy and leave your current clan. As a graduate of the academy, you can immediately join a clan as a regular member without being subject to any penalties.
	 */
	ACADEMY_MEMBERSHIP_TERMINATED(1749),
	
	/**
	 * ID: 1750<br>
	 * Message: If you possess $s1, you cannot participate in the Olympiad.
	 */
	CANNOT_JOIN_OLYMPIAD_POSSESSING_S1(1750),
	
	/**
	 * ID: 1755<br>
	 * Message: $s2 has been designated as the apprentice of clan member $s1.
	 */
	S2_HAS_BEEN_DESIGNATED_AS_APPRENTICE_OF_CLAN_MEMBER_S1(1755),
	
	/**
	 * ID: 1756<br>
	 * Message: Your apprentice, $s1, has logged in.
	 */
	YOUR_APPRENTICE_S1_HAS_LOGGED_IN(1756),
	
	/**
	 * ID: 1757<br>
	 * Message: Your apprentice, $s1, has logged out.
	 */
	YOUR_APPRENTICE_S1_HAS_LOGGED_OUT(1757),
	
	/**
	 * ID: 1758<br>
	 * Message: Your sponsor, $s1, has logged in.
	 */
	YOUR_SPONSOR_S1_HAS_LOGGED_IN(1758),
	
	/**
	 * ID: 1759<br>
	 * Message: Your sponsor, $s1, has logged out.
	 */
	YOUR_SPONSOR_S1_HAS_LOGGED_OUT(1759),
	
	/**
	 * ID: 1762<br>
	 * Message: You do not have the right to dismiss an apprentice.
	 */
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE(1762),
	
	/**
	 * ID: 1763<br>
	 * Message: $s2, clan member $s1's apprentice, has been removed.
	 */
	S2_CLAN_MEMBER_S1_S_APPRENTICE_HAS_BEEN_REMOVED(1763),
	
	/**
	 * ID: 1771<br>
	 * Message: Now that your clan level is above Level 5, it can accumulate clan reputation points.
	 */
	CLAN_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS(1771),
	
	/**
	 * ID: 1772<br>
	 * Message: Since your clan was defeated in a siege, $s1 points have been deducted from your clan's reputation score and given to the opposing clan.
	 */
	CLAN_WAS_DEFEATED_IN_SIEGE_AND_LOST_S1_REPUTATION_POINTS(1772),
	
	/**
	 * ID: 1773<br>
	 * Message: Since your clan emerged victorious from the siege, $s1 points have been added to your clan's reputation score.
	 */
	CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS(1773),
	
	/**
	 * ID: 1774<br>
	 * Message: Your clan's newly acquired contested clan hall has added $s1 points to your clan's reputation score.
	 */
	CLAN_ACQUIRED_CONTESTED_CLAN_HALL_AND_S1_REPUTATION_POINTS(1774),
	
	/**
	 * ID: 1775<br>
	 * Message: Clan member $s1 was an active member of the highest-ranked party in the Festival of Darkness. $s2 points have been added to your clan's reputation score.
	 */
	CLAN_MEMBER_S1_WAS_IN_HIGHEST_RANKED_PARTY_IN_FESTIVAL_OF_DARKNESS_AND_GAINED_S2_REPUTATION(1775),
	
	/**
	 * ID: 1776<br>
	 * Message: Clan member $s1 was named a hero. $2s points have been added to your clan's reputation score.
	 */
	CLAN_MEMBER_S1_BECAME_HERO_AND_GAINED_S2_REPUTATION_POINTS(1776),
	
	/**
	 * ID: 1777<br>
	 * Message: You have successfully completed a clan quest. $s1 points have been added to your clan's reputation score.
	 */
	CLAN_QUEST_COMPLETED_AND_S1_POINTS_GAINED(1777),
	
	/**
	 * ID: 1778<br>
	 * Message: An opposing clan has captured your clan's contested clan hall. $s1 points have been deducted from your clan's reputation score.
	 */
	OPPOSING_CLAN_CAPTURED_CLAN_HALL_AND_YOUR_CLAN_LOSES_S1_POINTS(1778),
	
	/**
	 * ID: 1779<br>
	 * Message: After losing the contested clan hall, 300 points have been deducted from your clan's reputation score.
	 */
	CLAN_LOST_CONTESTED_CLAN_HALL_AND_300_POINTS(1779),
	
	/**
	 * ID: 1780<br>
	 * Message: Your clan has captured your opponent's contested clan hall. $s1 points have been deducted from your opponent's clan reputation score.
	 */
	CLAN_CAPTURED_CONTESTED_CLAN_HALL_AND_S1_POINTS_DEDUCTED_FROM_OPPONENT(1780),
	
	/**
	 * ID: 1781<br>
	 * Message: Your clan has added $1s points to its clan reputation score.
	 */
	CLAN_ADDED_S1S_POINTS_TO_REPUTATION_SCORE(1781),
	
	/**
	 * ID: 1782<br>
	 * Message: Your clan member $s1 was killed. $s2 points have been deducted from your clan's reputation score and added to your opponent's clan reputation score.
	 */
	CLAN_MEMBER_S1_WAS_KILLED_AND_S2_POINTS_DEDUCTED_FROM_REPUTATION(1782),
	
	/**
	 * ID: 1783<br>
	 * Message: For killing an opposing clan member, $s1 points have been deducted from your opponents' clan reputation score.
	 */
	FOR_KILLING_OPPOSING_MEMBER_S1_POINTS_WERE_DEDUCTED_FROM_OPPONENTS(1783),
	
	/**
	 * ID: 1784<br>
	 * Message: Your clan has failed to defend the castle. $s1 points have been deducted from your clan's reputation score and added to your opponents'.
	 */
	YOUR_CLAN_FAILED_TO_DEFEND_CASTLE_AND_S1_POINTS_LOST_AND_ADDED_TO_OPPONENT(1784),
	
	/**
	 * ID: 1785<br>
	 * Message: The clan you belong to has been initialized. $s1 points have been deducted from your clan reputation score.
	 */
	YOUR_CLAN_HAS_BEEN_INITIALIZED_AND_S1_POINTS_LOST(1785),
	
	/**
	 * ID: 1786<br>
	 * Message: Your clan has failed to defend the castle. $s1 points have been deducted from your clan's reputation score.
	 */
	YOUR_CLAN_FAILED_TO_DEFEND_CASTLE_AND_S1_POINTS_LOST(1786),
	
	/**
	 * ID: 1787<br>
	 * Message: $s1 points have been deducted from the clan's reputation score.
	 */
	S1_DEDUCTED_FROM_CLAN_REP(1787),
	
	/**
	 * ID: 1788<br>
	 * Message: The clan skill $s1 has been added.
	 */
	CLAN_SKILL_S1_ADDED(1788),
	
	/**
	 * ID: 1789<br>
	 * Message: Since the Clan Reputation Score has dropped to 0 or lower, your clan skill(s) will be de-activated.
	 */
	REPUTATION_POINTS_0_OR_LOWER_CLAN_SKILLS_DEACTIVATED(1789),
	
	/**
	 * ID: 1790<br>
	 * Message: The conditions necessary to increase the clan's level have not been met.
	 */
	FAILED_TO_INCREASE_CLAN_LEVEL(1790),
	
	/**
	 * ID: 1791<br>
	 * Message: The conditions necessary to create a military unit have not been met.
	 */
	YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT(1791),
	
	/**
	 * ID: 1793<br>
	 * Message: $s1 has been selected as the captain of $s2.
	 */
	S1_HAS_BEEN_SELECTED_AS_CAPTAIN_OF_S2(1793),
	
	/**
	 * ID: 1794<br>
	 * Message: The Knights of $s1 have been created.
	 */
	THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED(1794),
	
	/**
	 * ID: 1795<br>
	 * Message: The Royal Guard of $s1 have been created.
	 */
	THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED(1795),
	
	/**
	 * ID: 1798<br>
	 * Message: Clan lord privileges have been transferred to $s1.
	 */
	CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_S1(1798),
	
	/**
	 * ID: 1803<br>
	 * Message: The request to participate in the game cannot be made starting from 10 minutes before the end of the game.
	 */
	GAME_REQUEST_CANNOT_BE_MADE(1803),
	
	/**
	 * ID: 1813<br>
	 * Message: $s1 has $s2 hour(s) of usage time remaining.
	 */
	THERE_IS_S1_HOUR_AND_S2_MINUTE_LEFT_OF_THE_FIXED_USAGE_TIME(1813),
	
	/**
	 * ID: 1814<br>
	 * Message: $s1 has $s2 minute(s) of usage time remaining.
	 */
	S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1(1814),
	
	/**
	 * ID: 1815<br>
	 * Message: $s2 was dropped in the $s1 region.
	 */
	S2_WAS_DROPPED_IN_THE_S1_REGION(1815),
	
	/**
	 * ID: 1816<br>
	 * Message: The owner of $s2 has appeared in the $s1 region.
	 */
	THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION(1816),
	
	/**
	 * ID: 1817<br>
	 * Message: $s2's owner has logged into the $s1 region.
	 */
	S2_OWNER_HAS_LOGGED_INTO_THE_S1_REGION(1817),
	
	/**
	 * ID: 1818<br>
	 * Message: $s1 has disappeared.
	 */
	S1_HAS_DISAPPEARED_2(1818),
	
	/**
	 * ID: 1835<br>
	 * Message: $s1 is full and cannot accept additional clan members at this time.
	 */
	S1_CLAN_IS_FULL(1835),
	
	/**
	 * ID: 1842<br>
	 * Message: $s1 wishes to summon you from $s2. Do you accept?
	 */
	S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT(1842),
	
	/**
	 * ID: 1843<br>
	 * Message: $s1 is engaged in combat and cannot be summoned.
	 */
	S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED(1843),
	
	/**
	 * ID: 1844<br>
	 * Message: $s1 is dead at the moment and cannot be summoned.
	 */
	S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED(1844),
	
	/**
	 * ID: 1850<br>
	 * Message: The Captain of the Order of Knights cannot be appointed.
	 */
	CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED(1850),
	
	/**
	 * ID: 1851<br>
	 * Message: The Captain of the Royal Guard cannot be appointed.
	 */
	CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED(1851),
	
	/**
	 * ID: 1852<br>
	 * Message: The attempt to acquire the skill has failed because of an insufficient Clan Reputation Score.
	 */
	ACQUIRE_SKILL_FAILED_BAD_CLAN_REP_SCORE(1852),
	
	/**
	 * ID: 1855<br>
	 * Message: Another military unit is already using that name. Please enter a different name.
	 */
	ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME(1855),
	
	/**
	 * ID: 1858<br>
	 * Message: You cannot participate in the Olympiad while dead.
	 */
	CANNOT_PARTICIPATE_OLYMPIAD_WHILE_DEAD(1858),
	
	/**
	 * ID: 1860<br>
	 * Message: The Clan Reputation Score is too low.
	 */
	THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW(1860),
	
	/**
	 * ID: 1860<br>
	 * Message: The Clan Reputation Score is too low.
	 */
	CLAN_REPUTATION_SCORE_IS_TOO_LOW(1860),
	
	/**
	 * ID: 1861<br>
	 * Message: The clan's crest has been deleted.
	 */
	CLAN_CREST_HAS_BEEN_DELETED(1861),
	
	/**
	 * ID: 1862<br>
	 * Message: Clan skills will now be activated since the clan's reputation score is 0 or higher.
	 */
	CLAN_SKILLS_WILL_BE_ACTIVATED_SINCE_REPUTATION_IS_0_OR_HIGHER(1862),
	
	/**
	 * ID: 1867<br>
	 * Message: Your opponent's MP was reduced by $s1.
	 */
	YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1(1867),
	
	/**
	 * ID: 1896<br>
	 * Message: $s1 has already been summoned!
	 */
	S1_ALREADY_SUMMONED(1896),
	
	/**
	 * ID: 1897<br>
	 * Message: $s1 is required for summoning.
	 */
	S1_REQUIRED_FOR_SUMMONING(1897),
	
	/**
	 * ID: 1898<br>
	 * Message: $s1 is currently trading or operating a private store and cannot be summoned.
	 */
	S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED(1898),
	
	/**
	 * ID: 1899<br>
	 * Message: Your target is in an area which blocks summoning.
	 */
	YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING(1899),
	
	/**
	 * ID: 1900<br>
	 * Message: $s1 has entered the party room.
	 */
	S1_ENTERED_PARTY_ROOM(1900),
	
	/**
	 * ID: 1901<br>
	 * Message: $s1 has invited you to enter the party room.
	 */
	S1_INVITED_YOU_TO_PARTY_ROOM(1901),
	
	/**
	 * ID: 1902<br>
	 * Message: Incompatible item grade. This item cannot be used.
	 */
	INCOMPATIBLE_ITEM_GRADE(1902),
	
	/**
	 * ID: 1911<br>
	 * Message: You cannot summon players who are currently participating in the Grand Olympiad.
	 */
	YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD(1911),
	
	/**
	 * ID: 1916<br>
	 * Message: Your Death Penalty is now level $s1.
	 */
	DEATH_PENALTY_LEVEL_S1_ADDED(1916),
	
	/**
	 * ID: 1917<br>
	 * Message: Your Death Penalty has been lifted.
	 */
	DEATH_PENALTY_LIFTED(1917),
	
	/**
	 * ID: 1919<br>
	 * Message: The Grand Olympiad registration period has ended.
	 */
	OLYMPIAD_REGISTRATION_PERIOD_ENDED(1919),
	
	/**
	 * ID: 1923<br>
	 * Message: Court Magician: The portal has been created!
	 */
	COURT_MAGICIAN_CREATED_PORTAL(1923),
	
	/**
	 * ID: 1923<br>
	 * Message: Court Magician: The portal has been created!
	 */
	THE_PORTAL_HAS_BEEN_CREATED(1923),
	
	/**
	 * ID: 1924<br>
	 * Message: Current Location: $s1, $s2, $s3 (near the Primeval Isle)
	 */
	LOC_PRIMEVAL_ISLE_S1_S2_S3(1924),
	
	/**
	 * ID: 1926<br>
	 * Message: There is no opponent to receive your challenge for a duel.
	 */
	THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL(1926),
	
	/**
	 * ID: 1927<br>
	 * Message: $s1 has been challenged to a duel.
	 */
	S1_HAS_BEEN_CHALLENGED_TO_A_DUEL(1927),
	
	/**
	 * ID: 1928<br>
	 * Message: $s1's party has been challenged to a duel.
	 */
	S1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL(1928),
	
	/**
	 * ID: 1929<br>
	 * Message: $s1 has accepted your challenge to a duel. The duel will begin in a few moments.
	 */
	S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1929),
	
	/**
	 * ID: 1930<br>
	 * Message: You have accepted $s1's challenge to a duel. The duel will begin in a few moments.
	 */
	YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1930),
	
	/**
	 * ID: 1931<br>
	 * Message: $s1 has declined your challenge to a duel.
	 */
	S1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1931),
	
	/**
	 * ID: 1933<br>
	 * Message: You have accepted $s1's challenge to a party duel. The duel will begin in a few moments.
	 */
	YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1933),
	
	/**
	 * ID: 1934<br>
	 * Message: $s1 has accepted your challenge to duel against their party. The duel will begin in a few moments.
	 */
	S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1934),
	
	/**
	 * ID: 1936<br>
	 * Message: The opposing party has declined your challenge to a duel.
	 */
	THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1936),
	
	/**
	 * ID: 1937<br>
	 * Message: Since the person you challenged is not currently in a party, they cannot duel against your party.
	 */
	SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY(1937),
	
	/**
	 * ID: 1938<br>
	 * Message: $s1 has challenged you to a duel.
	 */
	S1_HAS_CHALLENGED_YOU_TO_A_DUEL(1938),
	
	/**
	 * ID: 1939<br>
	 * Message: $s1's party has challenged your party to a duel.
	 */
	S1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL(1939),
	
	/**
	 * ID: 1940<br>
	 * Message: You are unable to request a duel at this time.
	 */
	YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME(1940),
	
	/**
	 * ID: 1942<br>
	 * Message: The opposing party is currently unable to accept a challenge to a duel.
	 */
	THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL(1942),
	
	/**
	 * ID: 1944<br>
	 * Message: In a moment, you will be transported to the site where the duel will take place.
	 */
	IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE(1944),
	
	/**
	 * ID: 1945<br>
	 * Message: The duel will begin in $s1 second(s).
	 */
	THE_DUEL_WILL_BEGIN_IN_S1_SECONDS(1945),
	
	/**
	 * ID: 1949<br>
	 * Message: Let the duel begin!
	 */
	LET_THE_DUEL_BEGIN(1949),
	
	/**
	 * ID: 1950<br>
	 * Message: $s1 has won the duel.
	 */
	S1_HAS_WON_THE_DUEL(1950),
	
	/**
	 * ID: 1951<br>
	 * Message: $s1's party has won the duel.
	 */
	S1S_PARTY_HAS_WON_THE_DUEL(1951),
	
	/**
	 * ID: 1952<br>
	 * Message: The duel has ended in a tie.
	 */
	THE_DUEL_HAS_ENDED_IN_A_TIE(1952),
	
	/**
	 * ID: 1955<br>
	 * Message: Since $s1 withdrew from the duel, $s2 has won.
	 */
	SINCE_S1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON(1955),
	
	/**
	 * ID: 1956<br>
	 * Message: Since $s1's party withdrew from the duel, $s2's party has won.
	 */
	SINCE_S1S_PARTY_WITHDREW_FROM_THE_DUEL_S1S_PARTY_HAS_WON(1956),
	
	/**
	 * ID: 1957<br>
	 * Message: Select the item to be augmented.
	 */
	SELECT_THE_ITEM_TO_BE_AUGMENTED(1957),
	
	/**
	 * ID: 1958<br>
	 * Message: Select the catalyst for augmentation.
	 */
	SELECT_THE_CATALYST_FOR_AUGMENTATION(1958),
	
	/**
	 * ID: 1959<br>
	 * Message: Requires $s2 $s1.
	 */
	REQUIRES_S1_S2(1959),
	
	/**
	 * ID: 1960<br>
	 * Message: This is not a suitable item.
	 */
	THIS_IS_NOT_A_SUITABLE_ITEM(1960),
	
	/**
	 * ID: 1961<br>
	 * Message: Gemstone quantity is incorrect.
	 */
	GEMSTONE_QUANTITY_IS_INCORRECT(1961),
	
	/**
	 * ID: 1962<br>
	 * Message: The item was successfully augmented!
	 */
	THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED(1962),
	
	/**
	 * ID: 1963<br>
	 * Message: Select the item from which you wish to remove augmentation.
	 */
	SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION(1963),
	
	/**
	 * ID: 1964<br>
	 * Message: Augmentation removal can only be done on an augmented item.
	 */
	AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM(1964),
	
	/**
	 * ID: 1965<br>
	 * Message: Augmentation has been successfully removed from your $s1.
	 */
	AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1(1965),
	
	/**
	 * ID: 1970<br>
	 * Message: Once an item is augmented, it cannot be augmented again.
	 */
	ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN(1970),
	
	/**
	 * ID: 1972<br>
	 * Message: You cannot augment items while a private store or private workshop is in operation.
	 */
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION(1972),
	
	/**
	 * ID: 1974<br>
	 * Message: You cannot augment items while dead.
	 */
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD(1974),
	
	/**
	 * ID: 1976<br>
	 * Message: You cannot augment items while paralyzed.
	 */
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED(1976),
	
	/**
	 * ID: 1977<br>
	 * Message: You cannot augment items while fishing.
	 */
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING(1977),
	
	/**
	 * ID: 1978<br>
	 * Message: You cannot augment items while sitting down.
	 */
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN(1978),
	
	/**
	 * ID: 1979<br>
	 * Message: $s1's remaining Mana is now 10.
	 */
	S1S_REMAINING_MANA_IS_NOW_10(1979),
	
	/**
	 * ID: 1980<br>
	 * Message: $s1's remaining Mana is now 5.
	 */
	S1S_REMAINING_MANA_IS_NOW_5(1980),
	
	/**
	 * ID: 1981<br>
	 * Message: $s1's remaining Mana is now 1. It will disappear soon.
	 */
	S1S_REMAINING_MANA_IS_NOW_1(1981),
	
	/**
	 * ID: 1982<br>
	 * Message: $s1's remaining Mana is now 0, and the item has disappeared.
	 */
	S1S_REMAINING_MANA_IS_NOW_0(1982),
	
	/**
	 * ID: 1983<br>
	 * Message: $s1
	 */
	S1(1983),
	
	/**
	 * ID: 1984<br>
	 * Message: Press the Augment button to begin.
	 */
	PRESS_THE_AUGMENT_BUTTON_TO_BEGIN(1984),
	
	/**
	 * ID: 1996<br>
	 * Message: The attack has been blocked.
	 */
	ATTACK_WAS_BLOCKED(1996),
	
	/**
	 * ID: 2001<br>
	 * Message: Augmentation failed due to inappropriate conditions.
	 */
	AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS(2001),
	
	/**
	 * ID: 2011<br>
	 * Message: The augmented item cannot be discarded.
	 */
	AUGMENTED_ITEM_CANNOT_BE_DISCARDED(2011),
	
	/**
	 * ID: 2013<br>
	 * Message: Your seed or remaining purchase amount is inadequate.
	 */
	YOUR_SEED_OR_REMAINING_PURCHASE_AMOUNT_IS_INADEQUATE(2013),
	
	/**
	 * ID: 2017<br>
	 * Message: $s1 cannot duel because $s1 is currently engaged in a private store or manufacture.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE(2017),
	
	/**
	 * ID: 2018<br>
	 * Message: $s1 cannot duel because $s1 is currently fishing.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_FISHING(2018),
	
	/**
	 * ID: 2019<br>
	 * Message: $s1 cannot duel because $s1's HP or MP is below 50 percent.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1S_HP_OR_MP_IS_BELOW_50_PERCENT(2019),
	
	/**
	 * ID: 2020<br>
	 * Message: $s1 cannot make a challenge to a duel because $s1 is currently in a duel-prohibited area (Peaceful Zone / Seven Signs Zone / Near Water / Restart Prohibited Area).
	 */
	S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA(2020),
	
	/**
	 * ID: 2021<br>
	 * Message: $s1 cannot duel because $s1 is currently engaged in battle.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_BATTLE(2021),
	
	/**
	 * ID: 2022<br>
	 * Message: $s1 cannot duel because $s1 is already engaged in a duel.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_ALREADY_ENGAGED_IN_A_DUEL(2022),
	
	/**
	 * ID: 2023<br>
	 * Message: $s1 cannot duel because $s1 is in a chaotic state.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_IN_A_CHAOTIC_STATE(2023),
	
	/**
	 * ID: 2024<br>
	 * Message: $s1 cannot duel because $s1 is participating in the Olympiad.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD(2024),
	
	/**
	 * ID: 2025<br>
	 * Message: $s1 cannot duel because $s1 is participating in a clan hall war.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR(2025),
	
	/**
	 * ID: 2026<br>
	 * Message: $s1 cannot duel because $s1 is participating in a siege war.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_SIEGE_WAR(2026),
	
	/**
	 * ID: 2027<br>
	 * Message: $s1 cannot duel because $s1 is currently riding a boat, wyvern, or strider.
	 */
	S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_RIDING_A_BOAT_WYVERN_OR_STRIDER(2027),
	
	/**
	 * ID: 2028<br>
	 * Message: $s1 cannot receive a duel challenge because $s1 is too far away.
	 */
	S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY(2028),
	
	/**
	 * ID: 2041<br>
	 * Message: Cannot open a private store.
	 */
	CANNOT_OPEN_A_PRIVATE_STORE(2041),
	
	/**
	 * ID: 1688<br>
	 * Message: You cannot enchant while operating a Private Store or Private Workshop.
	 */
	YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(1688),
	
	/**
	 * ID: 1802<br>
	 * Message: The attempt to trade has failed.
	 */
	THE_ATTEMP_TO_TRADE_HAS_FAILED(1802),
	
	AUTO_FARM_DESACTIVATED(2155),
	ACTIVATE_SUMMON_ACTACK(2156),
	DESACTIVATE_SUMMON_ACTACK(2157),		
	ACTIVATE_RESPECT_HUNT(2158),
	DESACTIVATE_RESPECT_HUNT(2159),
	AUTO_FARM_ACTIVATED(2160);
	
	private int id;
	
	private SystemMessageId(final int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public static SystemMessageId getMessage(int id)
	{
		for(SystemMessageId message : values())
		{
			if(message.getId() == id)
			{
				return message;
			}
		}
		
		return null;
	}
	
}