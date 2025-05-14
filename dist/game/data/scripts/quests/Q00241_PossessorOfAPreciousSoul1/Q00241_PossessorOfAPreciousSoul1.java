/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00241_PossessorOfAPreciousSoul1;

import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.quest.Quest;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.quest.State;
import org.l2jpx.gameserver.network.NpcStringId;

public class Q00241_PossessorOfAPreciousSoul1 extends Quest
{
	// NPCs
	private static final int TALIEN = 31739;
	private static final int GABRIELLE = 30753;
	private static final int GILMORE = 30754;
	private static final int KANTABILON = 31042;
	private static final int STEDMIEL = 30692;
	private static final int VIRGIL = 31742;
	private static final int OGMAR = 31744;
	private static final int RAHORAKTI = 31336;
	private static final int KASSANDRA = 31743;
	private static final int CARADINE = 31740;
	private static final int NOEL = 31272;
	
	// Monsters
	private static final int BARAHAM = 27113;
	private static final int MALRUK_SUCCUBUS = 20244;
	private static final int MALRUK_SUCCUBUS_TUREN = 20245;
	private static final int SPLINTER_STAKATO = 21508;
	private static final int SPLINTER_STAKATO_WALKER = 21509;
	private static final int SPLINTER_STAKATO_SOLDIER = 21510;
	private static final int SPLINTER_STAKATO_DRONE1 = 21511;
	private static final int SPLINTER_STAKATO_DRONE2 = 21512;
	
	// Items
	private static final int LEGEND_OF_SEVENTEEN = 7587;
	private static final int MALRUK_SUCCUBUS_CLAW = 7597;
	private static final int ECHO_CRYSTAL = 7589;
	private static final int POETRY_BOOK = 7588;
	private static final int CRIMSON_MOSS = 7598;
	private static final int RAHORAKTIS_MEDICINE = 7599;
	private static final int LUNARGENT = 6029;
	private static final int HELLFIRE_OIL = 6033;
	private static final int VIRGILS_LETTER = 7677;
	
	// Rewards
	private static final int CRIMSON_MOSS_CHANCE = 30;
	private static final int MALRUK_SUCCUBUS_CLAW_CHANCE = 60;
	
	public Q00241_PossessorOfAPreciousSoul1()
	{
		super(241);
		// Corrected registering of quest items
		registerQuestItems(LEGEND_OF_SEVENTEEN, MALRUK_SUCCUBUS_CLAW, ECHO_CRYSTAL, POETRY_BOOK, CRIMSON_MOSS, RAHORAKTIS_MEDICINE);
		addStartNpc(TALIEN);
		addTalkId(TALIEN, GABRIELLE, GILMORE, KANTABILON, STEDMIEL, VIRGIL, OGMAR, RAHORAKTI, KASSANDRA, CARADINE, NOEL);
		addKillId(BARAHAM, MALRUK_SUCCUBUS, MALRUK_SUCCUBUS_TUREN, SPLINTER_STAKATO, SPLINTER_STAKATO_WALKER, SPLINTER_STAKATO_SOLDIER, SPLINTER_STAKATO_DRONE1, SPLINTER_STAKATO_DRONE2);
		setQuestNameNpcStringId(NpcStringId.PATH_OF_THE_NOBLESSE_PRECIOUS_SOUL_1_2);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// Talien
		if (event.equalsIgnoreCase("31739-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("31739-07.htm"))
		{
			st.setCond(5);
			takeItems(player, LEGEND_OF_SEVENTEEN, 1);
		}
		else if (event.equalsIgnoreCase("31739-10.htm"))
		{
			st.setCond(9);
			takeItems(player, ECHO_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("31739-13.htm"))
		{
			st.setCond(11);
			takeItems(player, POETRY_BOOK, 1);
		}
		// Gabrielle
		else if (event.equalsIgnoreCase("30753-02.htm"))
		{
			st.setCond(2);
		}
		// Gilmore
		else if (event.equalsIgnoreCase("30754-02.htm"))
		{
			st.setCond(3);
		}
		// Kantabilon
		else if (event.equalsIgnoreCase("31042-02.htm"))
		{
			st.setCond(6);
		}
		else if (event.equalsIgnoreCase("31042-05.htm"))
		{
			st.setCond(8);
			takeItems(player, MALRUK_SUCCUBUS_CLAW, 10);
			giveItems(player, ECHO_CRYSTAL, 1);
		}
		// Stedmiel
		else if (event.equalsIgnoreCase("30692-02.htm"))
		{
			st.setCond(10);
			giveItems(player, POETRY_BOOK, 1);
		}
		// Virgil
		else if (event.equalsIgnoreCase("31742-02.htm"))
		{
			st.setCond(12);
		}
		else if (event.equalsIgnoreCase("31742-05.htm"))
		{
			st.setCond(18);
		}
		// Ogmar
		else if (event.equalsIgnoreCase("31744-02.htm"))
		{
			st.setCond(13);
		}
		// Rahorakti
		else if (event.equalsIgnoreCase("31336-02.htm"))
		{
			st.setCond(14);
		}
		else if (event.equalsIgnoreCase("31336-05.htm"))
		{
			st.setCond(16);
			takeItems(player, CRIMSON_MOSS, 5);
			giveItems(player, RAHORAKTIS_MEDICINE, 1);
		}
		// Kassandra
		else if (event.equalsIgnoreCase("31743-02.htm"))
		{
			st.setCond(17);
			takeItems(player, RAHORAKTIS_MEDICINE, 1);
		}
		// Caradine
		else if (event.equalsIgnoreCase("31740-02.htm"))
		{
			st.setCond(19);
		}
		else if (event.equalsIgnoreCase("31740-05.htm"))
		{
			giveItems(player, VIRGILS_LETTER, 1);
			addExpAndSp(player, 263043, 0);
			st.exitQuest(false, true);
		}
		// Noel
		else if (event.equalsIgnoreCase("31272-02.htm"))
		{
			st.setCond(20);
		}
		else if (event.equalsIgnoreCase("31272-05.htm"))
		{
			if (hasQuestItems(player, HELLFIRE_OIL) && (getQuestItemsCount(player, LUNARGENT) >= 5))
			{
				takeItems(player, LUNARGENT, 5);
				takeItems(player, HELLFIRE_OIL, 1);
				st.setCond(21);
			}
			else
			{
				htmltext = "31272-07.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (!player.isSubClassActive() || (player.getLevel() < 50))
				{
					htmltext = "31739-02.htm";
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "31739-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case TALIEN:
					{
						if (cond == 1)
						{
							htmltext = "31739-04.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "31739-05.htm";
						}
						else if (cond == 4)
						{
							htmltext = "31739-06.htm";
						}
						else if (cond == 5)
						{
							htmltext = "31739-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "31739-09.htm";
						}
						else if (cond == 9)
						{
							htmltext = "31739-11.htm";
						}
						else if (cond == 10)
						{
							htmltext = "31739-12.htm";
						}
						else if (cond == 11)
						{
							htmltext = "31739-14.htm";
						}
						break;
					}
					case GABRIELLE:
					{
						if (cond == 1)
						{
							htmltext = "30753-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30753-03.htm";
						}
						break;
					}
					case GILMORE:
					{
						if (cond == 2)
						{
							htmltext = "30754-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30754-03.htm";
						}
						break;
					}
					case KANTABILON:
					{
						if (cond == 5)
						{
							htmltext = "31042-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "31042-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "31042-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "31042-06.htm";
						}
						break;
					}
					case STEDMIEL:
					{
						if (cond == 9)
						{
							htmltext = "30692-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "30692-03.htm";
						}
						break;
					}
					case VIRGIL:
					{
						if (cond == 11)
						{
							htmltext = "31742-01.htm";
						}
						else if (cond == 12)
						{
							htmltext = "31742-03.htm";
						}
						else if (cond == 17)
						{
							htmltext = "31742-04.htm";
						}
						else if (cond == 18)
						{
							htmltext = "31742-06.htm";
						}
						break;
					}
					case OGMAR:
					{
						if (cond == 12)
						{
							htmltext = "31744-01.htm";
						}
						else if (cond == 13)
						{
							htmltext = "31744-03.htm";
						}
						break;
					}
					case RAHORAKTI:
					{
						if (cond == 13)
						{
							htmltext = "31336-01.htm";
						}
						else if (cond == 14)
						{
							htmltext = "31336-03.htm";
						}
						else if (cond == 15)
						{
							htmltext = "31336-04.htm";
						}
						else if (cond == 16)
						{
							htmltext = "31336-06.htm";
						}
						break;
					}
					case KASSANDRA:
					{
						if (cond == 16)
						{
							htmltext = "31743-01.htm";
						}
						else if (cond == 17)
						{
							htmltext = "31743-03.htm";
						}
						break;
					}
					case CARADINE:
					{
						if (cond == 18)
						{
							htmltext = "31740-01.htm";
						}
						else if (cond == 19)
						{
							htmltext = "31740-03.htm";
						}
						else if (cond == 21)
						{
							htmltext = "31740-04.htm";
						}
						break;
					}
					case NOEL:
					{
						if (cond == 19)
						{
							htmltext = "31272-01.htm";
						}
						else if (cond == 20)
						{
							if (hasQuestItems(player, HELLFIRE_OIL) && (getQuestItemsCount(player, LUNARGENT) >= 5))
							{
								htmltext = "31272-04.htm";
							}
							else
							{
								htmltext = "31272-03.htm";
							}
						}
						else if (cond == 21)
						{
							htmltext = "31272-06.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		if (!player.isSubClassActive())
		{
			return null;
		}
		
		final QuestState st;
		final Player partyMember;
		
		switch (npc.getId())
		{
			case BARAHAM:
			{
				partyMember = getRandomPartyMember(player, 3);
				if (partyMember == null)
				{
					return null;
				}
				
				st = partyMember.getQuestState(getName());
				giveItems(partyMember, LEGEND_OF_SEVENTEEN, 1);
				st.setCond(4, true);
				break;
			}
			case MALRUK_SUCCUBUS:
			case MALRUK_SUCCUBUS_TUREN:
			{
				partyMember = getRandomPartyMember(player, 6);
				if (partyMember == null)
				{
					return null;
				}
				st = partyMember.getQuestState(getName());
				if ((MALRUK_SUCCUBUS_CLAW_CHANCE >= getRandom(100)) && (getQuestItemsCount(partyMember, MALRUK_SUCCUBUS_CLAW) < 10))
				{
					giveItems(partyMember, MALRUK_SUCCUBUS_CLAW, 1);
					if (getQuestItemsCount(partyMember, MALRUK_SUCCUBUS_CLAW) == 10)
					{
						st.setCond(7, true);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case SPLINTER_STAKATO:
			case SPLINTER_STAKATO_WALKER:
			case SPLINTER_STAKATO_SOLDIER:
			case SPLINTER_STAKATO_DRONE1:
			case SPLINTER_STAKATO_DRONE2:
			{
				partyMember = getRandomPartyMember(player, 14);
				if (partyMember == null)
				{
					return null;
				}
				st = partyMember.getQuestState(getName());
				if ((CRIMSON_MOSS_CHANCE >= getRandom(100)) && (getQuestItemsCount(partyMember, CRIMSON_MOSS) < 5))
				{
					giveItems(partyMember, CRIMSON_MOSS, 1);
					if (getQuestItemsCount(partyMember, CRIMSON_MOSS) == 5)
					{
						st.setCond(15, true);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}