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
package org.l2jpx.gameserver.model.quest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.gameserver.enums.QuestSound;
import org.l2jpx.gameserver.enums.QuestType;
import org.l2jpx.gameserver.instancemanager.QuestManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerQuestComplete;
import org.l2jpx.gameserver.network.serverpackets.ExShowQuestMark;
import org.l2jpx.gameserver.network.serverpackets.QuestList;

/**
 * Quest state class.
 * @author Luis Arias
 */
public class QuestState
{
	protected static final Logger LOGGER = Logger.getLogger(QuestState.class.getName());
	
	// Constants
	private static final String COND_VAR = "cond";
	private static final String RESTART_VAR = "restartTime";
	private static final String MEMO_VAR = "memoState";
	private static final String MEMO_EX_VAR = "memoStateEx";
	
	/** The name of the quest of this QuestState */
	private final String _questName;
	
	/** The "owner" of this QuestState object */
	private final Player _player;
	
	/** The current state of the quest */
	private byte _state;
	
	/** The current condition of the quest */
	private int _cond = 0;
	
	/** Used for simulating Quest onTalk */
	private boolean _simulated = false;
	
	/** A map of key->value pairs containing the quest state variables and their values */
	private Map<String, String> _vars;
	
	/**
	 * boolean flag letting QuestStateManager know to exit quest when cleaning up
	 */
	private boolean _isExitQuestOnCleanUp = false;
	
	/**
	 * Constructor of the QuestState. Creates the QuestState object and sets the player's progress of the quest to this QuestState.
	 * @param quest the {@link Quest} object associated with the QuestState
	 * @param player the owner of this {@link QuestState} object
	 * @param state the initial state of the quest
	 */
	public QuestState(Quest quest, Player player, byte state)
	{
		_questName = quest.getName();
		_player = player;
		_state = state;
		player.setQuestState(this);
	}
	
	/**
	 * @return the name of the quest of this QuestState
	 */
	public String getQuestName()
	{
		return _questName;
	}
	
	/**
	 * @return the {@link Quest} object of this QuestState
	 */
	public Quest getQuest()
	{
		return QuestManager.getInstance().getQuest(_questName);
	}
	
	/**
	 * @return the {@link Player} object of the owner of this QuestState
	 */
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * @return the current State of this QuestState
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public byte getState()
	{
		return _state;
	}
	
	/**
	 * @return {@code true} if the State of this QuestState is CREATED, {@code false} otherwise
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public boolean isCreated()
	{
		return _state == State.CREATED;
	}
	
	/**
	 * @return {@code true} if the State of this QuestState is STARTED, {@code false} otherwise
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public boolean isStarted()
	{
		return _state == State.STARTED;
	}
	
	/**
	 * @return {@code true} if the State of this QuestState is COMPLETED, {@code false} otherwise
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public boolean isCompleted()
	{
		return _state == State.COMPLETED;
	}
	
	/**
	 * @param state the new state of the quest to set
	 * @see #setState(byte state, boolean saveInDb)
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public void setState(byte state)
	{
		setState(state, true);
	}
	
	/**
	 * Change the state of this quest to the specified value.
	 * @param state the new state of the quest to set
	 * @param saveInDb if {@code true}, will save the state change in the database
	 * @see org.l2jpx.gameserver.model.quest.State
	 */
	public void setState(byte state, boolean saveInDb)
	{
		if (_simulated)
		{
			return;
		}
		
		if (_state == state)
		{
			return;
		}
		
		final boolean newQuest = isCreated();
		_state = state;
		if (saveInDb)
		{
			if (newQuest)
			{
				Quest.createQuestInDb(this);
			}
			else
			{
				Quest.updateQuestInDb(this);
			}
		}
		
		_player.sendPacket(new QuestList(_player));
	}
	
	/**
	 * Add parameter used in quests.
	 * @param variable String pointing out the name of the variable for quest
	 * @param value String pointing out the value of the variable for quest
	 */
	public void setInternal(String variable, String value)
	{
		if (_simulated)
		{
			return;
		}
		
		if (_vars == null)
		{
			_vars = new HashMap<>();
		}
		
		if (value == null)
		{
			_vars.put(variable, "");
			return;
		}
		
		if (COND_VAR.equals(variable))
		{
			try
			{
				_cond = Integer.parseInt(value);
			}
			catch (Exception ignored)
			{
			}
		}
		
		_vars.put(variable, value);
	}
	
	public void set(String variable, int value)
	{
		if (_simulated)
		{
			return;
		}
		
		set(variable, Integer.toString(value));
	}
	
	/**
	 * Return value of parameter "value" after adding the couple (var,value) in class variable "vars".<br>
	 * Actions:<br>
	 * <ul>
	 * <li>Initialize class variable "vars" if is null.</li>
	 * <li>Initialize parameter "value" if is null</li>
	 * <li>Add/Update couple (var,value) in class variable Map "vars"</li>
	 * <li>If the key represented by "var" exists in Map "vars", the couple (var,value) is updated in the database.<br>
	 * The key is known as existing if the preceding value of the key (given as result of function put()) is not null.<br>
	 * If the key doesn't exist, the couple is added/created in the database</li>
	 * <ul>
	 * @param variable String indicating the name of the variable for quest
	 * @param value String indicating the value of the variable for quest
	 */
	public void set(String variable, String value)
	{
		if (_simulated)
		{
			return;
		}
		
		if (_vars == null)
		{
			_vars = new HashMap<>();
		}
		
		String newValue = value;
		if (newValue == null)
		{
			newValue = "";
		}
		
		final String old = _vars.put(variable, newValue);
		if (old != null)
		{
			Quest.updateQuestVarInDb(this, variable, newValue);
		}
		else
		{
			Quest.createQuestVarInDb(this, variable, newValue);
		}
		
		if (COND_VAR.equals(variable))
		{
			try
			{
				int previousVal = 0;
				try
				{
					previousVal = Integer.parseInt(old);
				}
				catch (Exception ignored)
				{
				}
				int newCond = 0;
				try
				{
					newCond = Integer.parseInt(newValue);
				}
				catch (Exception ignored)
				{
				}
				
				_cond = newCond;
				setCond(newCond, previousVal);
				getQuest().sendNpcLogList(getPlayer());
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, _player.getName() + ", " + _questName + " cond [" + newValue + "] is not an integer.  Value stored, but no packet was sent: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Internally handles the progression of the quest so that it is ready for sending appropriate packets to the client.<br>
	 * <u><i>Actions :</i></u><br>
	 * <ul>
	 * <li>Check if the new progress number resets the quest to a previous (smaller) step.</li>
	 * <li>If not, check if quest progress steps have been skipped.</li>
	 * <li>If skipped, prepare the variable completedStateFlags appropriately to be ready for sending to clients.</li>
	 * <li>If no steps were skipped, flags do not need to be prepared...</li>
	 * <li>If the passed step resets the quest to a previous step, reset such that steps after the parameter are not considered, while skipped steps before the parameter, if any, maintain their info.</li>
	 * </ul>
	 * @param cond the current quest progress condition (0 - 31 including)
	 * @param old the previous quest progress condition to check against
	 */
	private void setCond(int cond, int old)
	{
		if (_simulated)
		{
			return;
		}
		
		if (cond == old)
		{
			return;
		}
		
		int completedStateFlags = 0;
		// cond 0 and 1 do not need completedStateFlags. Also, if cond > 1, the 1st step must
		// always exist (i.e. it can never be skipped). So if cond is 2, we can still safely
		// assume no steps have been skipped.
		// Finally, more than 31 steps CANNOT be supported in any way with skipping.
		if ((cond < 3) || (cond > 31))
		{
			unset("__compltdStateFlags");
		}
		else
		{
			completedStateFlags = getInt("__compltdStateFlags");
		}
		
		// case 1: No steps have been skipped so far...
		if (completedStateFlags == 0)
		{
			// Check if this step also doesn't skip anything. If so, no further work is needed also, in this case, no work is needed if the state is being reset to a smaller value in those cases, skip forward to informing the client about the change...
			// ELSE, if we just now skipped for the first time...prepare the flags!!!
			if (cond > (old + 1))
			{
				// set the most significant bit to 1 (indicates that there exist skipped states)
				// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter what the cond says)
				completedStateFlags = 0x80000001;
				
				// since no flag had been skipped until now, the least significant bits must all be set to 1, up until "old" number of bits.
				completedStateFlags |= (1 << old) - 1;
				
				// now, just set the bit corresponding to the passed cond to 1 (current step)
				completedStateFlags |= 1 << (cond - 1);
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}
		// case 2: There were exist previously skipped steps
		else if (cond < old) // if this is a push back to a previous step, clear all completion flags ahead
		{
			completedStateFlags &= (1 << cond) - 1; // note, this also unsets the flag indicating that there exist skips
			
			// now, check if this resulted in no steps being skipped any more
			if (completedStateFlags == ((1 << cond) - 1))
			{
				unset("__compltdStateFlags");
			}
			else
			{
				// set the most significant bit back to 1 again, to correctly indicate that this skips states.
				// also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter what the cond says)
				completedStateFlags |= 0x80000001;
				set("__compltdStateFlags", String.valueOf(completedStateFlags));
			}
		}
		// If this moves forward, it changes nothing on previously skipped steps.
		// Just mark this state and we are done.
		else
		{
			completedStateFlags |= 1 << (cond - 1);
			set("__compltdStateFlags", String.valueOf(completedStateFlags));
		}
		
		// send a packet to the client to inform it of the quest progress (step change)
		_player.sendPacket(new QuestList(_player));
		
		final Quest q = getQuest();
		if (!q.isCustomQuest() && (cond > 0))
		{
			_player.sendPacket(new ExShowQuestMark(q.getId(), getCond()));
		}
	}
	
	/**
	 * Removes a quest variable from the list of existing quest variables.
	 * @param variable the name of the variable to remove
	 */
	public void unset(String variable)
	{
		if (_simulated)
		{
			return;
		}
		
		if (_vars == null)
		{
			return;
		}
		
		final String old = _vars.remove(variable);
		if (old != null)
		{
			if (COND_VAR.equals(variable))
			{
				_cond = 0;
			}
			
			Quest.deleteQuestVarInDb(this, variable);
		}
	}
	
	/**
	 * @param variable the name of the variable to get
	 * @return the value of the variable from the list of quest variables
	 */
	public String get(String variable)
	{
		if (_vars == null)
		{
			return null;
		}
		
		return _vars.get(variable);
	}
	
	/**
	 * @param variable the name of the variable to get
	 * @return the integer value of the variable or 0 if the variable does not exist or its value is not an integer
	 */
	public int getInt(String variable)
	{
		if (_vars == null)
		{
			return 0;
		}
		
		final String varStr = _vars.get(variable);
		if ((varStr == null) || varStr.isEmpty())
		{
			return 0;
		}
		
		int varInt = 0;
		try
		{
			varInt = Integer.parseInt(varStr);
		}
		catch (NumberFormatException nfe)
		{
			LOGGER.log(Level.INFO, "Quest " + _questName + ", method getInt(" + variable + "), tried to parse a non-integer value (" + varStr + "). Char Id: " + _player.getObjectId(), nfe);
		}
		
		return varInt;
	}
	
	/**
	 * Checks if the quest state progress ({@code cond}) is at the specified step.
	 * @param condition the condition to check against
	 * @return {@code true} if the quest condition is equal to {@code condition}, {@code false} otherwise
	 * @see #getInt(String var)
	 */
	public boolean isCond(int condition)
	{
		return _cond == condition;
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.
	 * @param value the new value of the quest state progress
	 * @see #set(String var, String value)
	 * @see #setCond(int, boolean)
	 */
	public void setCond(int value)
	{
		if (_simulated)
		{
			return;
		}
		
		if (isStarted())
		{
			set(COND_VAR, Integer.toString(value));
		}
	}
	
	/**
	 * @return the current quest progress ({@code cond})
	 */
	public int getCond()
	{
		if (isStarted())
		{
			return _cond;
		}
		
		return 0;
	}
	
	/**
	 * Get bit set representing completed conds.
	 * @return if none cond is set {@code 0}, otherwise cond bit set.
	 */
	public int getCondBitSet()
	{
		if (isStarted())
		{
			int val = getInt(COND_VAR);
			if ((val & 0x80000000) != 0)
			{
				val &= 0x7fffffff;
				for (int i = 1; i < 32; i++)
				{
					val = (val >> 1);
					if (val == 0)
					{
						val = i;
						break;
					}
				}
			}
			return val;
		}
		return 0;
	}
	
	/**
	 * Check if a given variable is set for this quest.
	 * @param variable the variable to check
	 * @return {@code true} if the variable is set, {@code false} otherwise
	 * @see #get(String)
	 * @see #getInt(String)
	 * @see #getCond()
	 */
	public boolean isSet(String variable)
	{
		return get(variable) != null;
	}
	
	/**
	 * Sets the quest state progress ({@code cond}) to the specified step.
	 * @param value the new value of the quest state progress
	 * @param playQuestMiddle if {@code true}, plays "ItemSound.quest_middle"
	 * @see #setCond(int value)
	 * @see #set(String var, String value)
	 */
	public void setCond(int value, boolean playQuestMiddle)
	{
		if (_simulated)
		{
			return;
		}
		
		if (!isStarted())
		{
			return;
		}
		
		set(COND_VAR, String.valueOf(value));
		if (playQuestMiddle)
		{
			_player.sendPacket(QuestSound.ITEMSOUND_QUEST_MIDDLE.getPacket());
		}
	}
	
	public void setMemoState(int value)
	{
		if (_simulated)
		{
			return;
		}
		
		set(MEMO_VAR, String.valueOf(value));
	}
	
	/**
	 * @return the current Memo State
	 */
	public int getMemoState()
	{
		if (isStarted())
		{
			return getInt(MEMO_VAR);
		}
		
		return 0;
	}
	
	public boolean isMemoState(int memoState)
	{
		return getInt(MEMO_VAR) == memoState;
	}
	
	/**
	 * Gets the memo state ex.
	 * @param slot the slot where the value was saved
	 * @return the memo state ex
	 */
	public int getMemoStateEx(int slot)
	{
		if (isStarted())
		{
			return getInt(MEMO_EX_VAR + slot);
		}
		
		return 0;
	}
	
	/**
	 * Sets the memo state ex.
	 * @param slot the slot where the value will be saved
	 * @param value the value
	 */
	public void setMemoStateEx(int slot, int value)
	{
		if (_simulated)
		{
			return;
		}
		
		set(MEMO_EX_VAR + slot, String.valueOf(value));
	}
	
	/**
	 * Verifies if the given value is equal to the current memos state ex.
	 * @param slot the slot where the value was saved
	 * @param memoStateEx the value to verify
	 * @return {@code true} if the values are equal, {@code false} otherwise
	 */
	public boolean isMemoStateEx(int slot, int memoStateEx)
	{
		return (getMemoStateEx(slot) == memoStateEx);
	}
	
	/**
	 * @return {@code true} if quest is to be exited on clean up by QuestStateManager, {@code false} otherwise
	 */
	public boolean isExitQuestOnCleanUp()
	{
		return _isExitQuestOnCleanUp;
	}
	
	/**
	 * @param isExitQuestOnCleanUp {@code true} if quest is to be exited on clean up by QuestStateManager, {@code false} otherwise
	 */
	public void setIsExitQuestOnCleanUp(boolean isExitQuestOnCleanUp)
	{
		if (_simulated)
		{
			return;
		}
		
		_isExitQuestOnCleanUp = isExitQuestOnCleanUp;
	}
	
	/**
	 * Set condition to 1, state to STARTED and play the "ItemSound.quest_accept".<br>
	 * Works only if state is CREATED and the quest is not a custom quest.
	 */
	public void startQuest()
	{
		if (_simulated)
		{
			return;
		}
		
		if (isCreated() && !getQuest().isCustomQuest())
		{
			set(COND_VAR, "1");
			setState(State.STARTED);
			_player.sendPacket(QuestSound.ITEMSOUND_QUEST_ACCEPT.getPacket());
			getQuest().sendNpcLogList(getPlayer());
		}
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
	 * @param type the {@link QuestType} of the quest
	 * @see #exitQuest(QuestType type, boolean playExitQuest)
	 * @see #exitQuest(boolean repeatable)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	public void exitQuest(QuestType type)
	{
		if (_simulated)
		{
			return;
		}
		
		switch (type)
		{
			case DAILY:
			{
				exitQuest(false);
				setRestartTime();
				break;
			}
			// case ONE_TIME:
			// case REPEATABLE:
			default:
			{
				exitQuest(type == QuestType.REPEATABLE);
				break;
			}
		}
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_QUEST_COMPLETE, _player))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(_player, getQuest().getId(), type), _player);
		}
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
	 * @param type the {@link QuestType} of the quest
	 * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
	 * @see #exitQuest(QuestType type)
	 * @see #exitQuest(boolean repeatable)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	public void exitQuest(QuestType type, boolean playExitQuest)
	{
		if (_simulated)
		{
			return;
		}
		
		exitQuest(type);
		if (playExitQuest)
		{
			_player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
		}
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
	 * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise keeps them
	 * @see #exitQuest(QuestType type)
	 * @see #exitQuest(QuestType type, boolean playExitQuest)
	 * @see #exitQuest(boolean repeatable, boolean playExitQuest)
	 */
	private void exitQuest(boolean repeatable)
	{
		if (_simulated)
		{
			return;
		}
		
		_player.removeNotifyQuestOfDeath(this);
		
		if (!isStarted())
		{
			return;
		}
		
		// Clean registered quest items
		getQuest().removeRegisteredQuestItems(_player);
		
		Quest.deleteQuestInDb(this, repeatable);
		if (repeatable)
		{
			_player.delQuestState(_questName);
			_player.sendPacket(new QuestList(_player));
		}
		else
		{
			setState(State.COMPLETED);
		}
		_vars = null;
	}
	
	/**
	 * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
	 * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
	 * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise keeps them
	 * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
	 * @see #exitQuest(QuestType type)
	 * @see #exitQuest(QuestType type, boolean playExitQuest)
	 * @see #exitQuest(boolean repeatable)
	 */
	public void exitQuest(boolean repeatable, boolean playExitQuest)
	{
		if (_simulated)
		{
			return;
		}
		
		exitQuest(repeatable);
		if (playExitQuest)
		{
			_player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
		}
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_QUEST_COMPLETE, _player))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(_player, getQuest().getId(), repeatable ? QuestType.REPEATABLE : QuestType.ONE_TIME), _player);
		}
	}
	
	/**
	 * Set the restart time for the daily quests.<br>
	 * The time is hardcoded at {@link Quest#getResetHour()} hours, {@link Quest#getResetMinutes()} minutes of the following day.<br>
	 * It can be overridden in scripts (quests).
	 */
	public void setRestartTime()
	{
		if (_simulated)
		{
			return;
		}
		
		final Calendar reDo = Calendar.getInstance();
		if (reDo.get(Calendar.HOUR_OF_DAY) >= getQuest().getResetHour())
		{
			reDo.add(Calendar.DATE, 1);
		}
		reDo.set(Calendar.HOUR_OF_DAY, getQuest().getResetHour());
		reDo.set(Calendar.MINUTE, getQuest().getResetMinutes());
		set(RESTART_VAR, String.valueOf(reDo.getTimeInMillis()));
	}
	
	/**
	 * Check if a daily quest is available to be started over.
	 * @return {@code true} if the quest is available, {@code false} otherwise.
	 */
	public boolean isNowAvailable()
	{
		final String val = get(RESTART_VAR);
		return (val != null) && (Long.parseLong(val) <= System.currentTimeMillis());
	}
	
	public void setSimulated(boolean simulated)
	{
		_simulated = simulated;
	}
}