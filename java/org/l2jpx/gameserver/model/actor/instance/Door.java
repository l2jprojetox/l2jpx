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
package org.l2jpx.gameserver.model.actor.instance;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Future;

import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.ai.CreatureAI;
import org.l2jpx.gameserver.ai.DoorAI;
import org.l2jpx.gameserver.data.xml.DoorData;
import org.l2jpx.gameserver.enums.DoorOpenType;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.FortManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.stat.DoorStat;
import org.l2jpx.gameserver.model.actor.status.DoorStatus;
import org.l2jpx.gameserver.model.actor.templates.DoorTemplate;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.DoorStatusUpdate;
import org.l2jpx.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jpx.gameserver.network.serverpackets.StaticObjectInfo;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class Door extends Creature
{
	boolean _open = false;
	private boolean _isAttackableDoor = false;
	private boolean _isInverted = false;
	private int _meshindex = 1;
	private Future<?> _autoCloseTask;
	
	public Door(DoorTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Door);
		setInvul(false);
		setLethalable(false);
		_open = template.isOpenByDefault();
		_isAttackableDoor = template.isAttackable();
		_isInverted = template.isInverted();
		super.setTargetable(template.isTargetable());
		
		if (isOpenableByTime())
		{
			startTimerOpen();
		}
	}
	
	@Override
	protected CreatureAI initAI()
	{
		return new DoorAI(this);
	}
	
	@Override
	public void moveToLocation(int x, int y, int z, int offset)
	{
	}
	
	@Override
	public void stopMove(Location loc)
	{
	}
	
	@Override
	public void doAutoAttack(Creature target)
	{
	}
	
	@Override
	public void doCast(Skill skill)
	{
	}
	
	private void startTimerOpen()
	{
		int delay = _open ? getTemplate().getOpenTime() : getTemplate().getCloseTime();
		if (getTemplate().getRandomTime() > 0)
		{
			delay += Rnd.get(getTemplate().getRandomTime());
		}
		ThreadPool.schedule(new TimerOpen(), delay * 1000);
	}
	
	@Override
	public DoorTemplate getTemplate()
	{
		return (DoorTemplate) super.getTemplate();
	}
	
	@Override
	public DoorStatus getStatus()
	{
		return (DoorStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new DoorStatus(this));
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new DoorStat(this));
	}
	
	@Override
	public DoorStat getStat()
	{
		return (DoorStat) super.getStat();
	}
	
	/**
	 * @return {@code true} if door is open-able by skill.
	 */
	public boolean isOpenableBySkill()
	{
		return (getTemplate().getOpenType()) == DoorOpenType.BY_SKILL;
	}
	
	/**
	 * @return {@code true} if door is open-able by item.
	 */
	public boolean isOpenableByItem()
	{
		return (getTemplate().getOpenType()) == DoorOpenType.BY_ITEM;
	}
	
	/**
	 * @return {@code true} if door is open-able by double-click.
	 */
	public boolean isOpenableByClick()
	{
		return (getTemplate().getOpenType()) == DoorOpenType.BY_CLICK;
	}
	
	/**
	 * @return {@code true} if door is open-able by time.
	 */
	public boolean isOpenableByTime()
	{
		return (getTemplate().getOpenType()) == DoorOpenType.BY_TIME;
	}
	
	/**
	 * @return {@code true} if door is open-able by Field Cycle system.
	 */
	public boolean isOpenableByCycle()
	{
		return (getTemplate().getOpenType()) == DoorOpenType.BY_CYCLE;
	}
	
	@Override
	public int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * Gets the door ID.
	 * @return the door ID
	 */
	@Override
	public int getId()
	{
		return getTemplate().getId();
	}
	
	/**
	 * @return Returns if the door is open.
	 */
	public boolean isOpen()
	{
		return _open;
	}
	
	/**
	 * @param open The door open status.
	 */
	public void setOpen(boolean open)
	{
		_open = open;
		if (getChildId() > 0)
		{
			final Door sibling = getSiblingDoor(getChildId());
			if (sibling != null)
			{
				sibling.notifyChildEvent(open);
			}
			else
			{
				LOGGER.warning(getClass().getSimpleName() + ": cannot find child id: " + getChildId());
			}
		}
	}
	
	public boolean isAttackableDoor()
	{
		return _isAttackableDoor;
	}
	
	public boolean isInverted()
	{
		return _isInverted;
	}
	
	public boolean isShowHp()
	{
		return getTemplate().isShowHp();
	}
	
	public void setIsAttackableDoor(boolean value)
	{
		_isAttackableDoor = value;
	}
	
	public int getDamage()
	{
		if ((getCastle() == null) && (getFort() == null))
		{
			return 0;
		}
		final int dmg = 6 - (int) Math.ceil((getCurrentHp() / getMaxHp()) * 6);
		if (dmg > 6)
		{
			return 6;
		}
		if (dmg < 0)
		{
			return 0;
		}
		return dmg;
	}
	
	public Castle getCastle()
	{
		return CastleManager.getInstance().getCastle(this);
	}
	
	public Fort getFort()
	{
		return FortManager.getInstance().getFort(this);
	}
	
	public boolean isEnemy()
	{
		if ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getZone().isActive() && isShowHp())
		{
			return true;
		}
		else if ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getZone().isActive() && isShowHp())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		// Doors can`t be attacked by NPCs
		if (!attacker.isPlayable())
		{
			return false;
		}
		else if (_isAttackableDoor)
		{
			return true;
		}
		else if (!isShowHp())
		{
			return false;
		}
		
		final Player actingPlayer = attacker.getActingPlayer();
		
		// Attackable only during siege by everyone (not owner)
		final boolean isCastle = ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getZone().isActive());
		final boolean isFort = ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getZone().isActive());
		if (isFort)
		{
			final Clan clan = actingPlayer.getClan();
			if ((clan != null) && (clan == getFort().getOwnerClan()))
			{
				return false;
			}
		}
		else if (isCastle)
		{
			final Clan clan = actingPlayer.getClan();
			if ((clan != null) && (clan.getId() == getCastle().getOwnerId()))
			{
				return false;
			}
		}
		return (isCastle || isFort);
	}
	
	/**
	 * Return null.
	 */
	@Override
	public Item getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public Item getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public void broadcastStatusUpdate(Creature caster)
	{
		final Collection<Player> knownPlayers = World.getInstance().getVisibleObjects(this, Player.class);
		if ((knownPlayers == null) || knownPlayers.isEmpty())
		{
			return;
		}
		
		final StaticObjectInfo su = new StaticObjectInfo(this, false);
		final StaticObjectInfo targetableSu = new StaticObjectInfo(this, true);
		final DoorStatusUpdate dsu = new DoorStatusUpdate(this);
		OnEventTrigger oe = null;
		if (getEmitter() > 0)
		{
			if (_isInverted)
			{
				oe = new OnEventTrigger(getEmitter(), !_open);
			}
			else
			{
				oe = new OnEventTrigger(getEmitter(), _open);
			}
		}
		
		for (Player player : knownPlayers)
		{
			if ((player == null) || !isVisibleFor(player))
			{
				continue;
			}
			
			if (player.isGM() || (((getCastle() != null) && (getCastle().getResidenceId() > 0)) || ((getFort() != null) && (getFort().getResidenceId() > 0))))
			{
				player.sendPacket(targetableSu);
			}
			else
			{
				player.sendPacket(su);
			}
			
			player.sendPacket(dsu);
			if (oe != null)
			{
				player.sendPacket(oe);
			}
		}
	}
	
	public void openCloseMe(boolean open)
	{
		if (open)
		{
			openMe();
		}
		else
		{
			closeMe();
		}
	}
	
	public void openMe()
	{
		if (getGroupName() != null)
		{
			manageGroupOpen(true, getGroupName());
			return;
		}
		
		if (!isOpen())
		{
			setOpen(true);
			broadcastStatusUpdate();
			startAutoCloseTask();
		}
	}
	
	public void closeMe()
	{
		// remove close task
		final Future<?> oldTask = _autoCloseTask;
		if (oldTask != null)
		{
			_autoCloseTask = null;
			oldTask.cancel(false);
		}
		if (getGroupName() != null)
		{
			manageGroupOpen(false, getGroupName());
			return;
		}
		
		if (isOpen())
		{
			setOpen(false);
			broadcastStatusUpdate();
		}
	}
	
	private void manageGroupOpen(boolean open, String groupName)
	{
		final Set<Integer> set = DoorData.getInstance().getDoorsByGroup(groupName);
		Door first = null;
		for (Integer id : set)
		{
			final Door door = getSiblingDoor(id);
			if (first == null)
			{
				first = door;
			}
			
			if (door.isOpen() != open)
			{
				door.setOpen(open);
				door.broadcastStatusUpdate();
			}
		}
		if ((first != null) && open)
		{
			first.startAutoCloseTask(); // only one from group
		}
	}
	
	/**
	 * Door notify child about open state change
	 * @param open true if opened
	 */
	private void notifyChildEvent(boolean open)
	{
		final byte openThis = open ? getTemplate().getMasterDoorOpen() : getTemplate().getMasterDoorClose();
		if (openThis == 1)
		{
			openMe();
		}
		else if (openThis == -1)
		{
			closeMe();
		}
	}
	
	@Override
	public String getName()
	{
		return getTemplate().getName();
	}
	
	public int getX(int i)
	{
		return getTemplate().getNodeX()[i];
	}
	
	public int getY(int i)
	{
		return getTemplate().getNodeY()[i];
	}
	
	public int getZMin()
	{
		return getTemplate().getNodeZ();
	}
	
	public int getZMax()
	{
		return getTemplate().getNodeZ() + getTemplate().getHeight();
	}
	
	public void setMeshIndex(int mesh)
	{
		_meshindex = mesh;
	}
	
	public int getMeshIndex()
	{
		return _meshindex;
	}
	
	public int getEmitter()
	{
		return getTemplate().getEmmiter();
	}
	
	public boolean isWall()
	{
		return getTemplate().isWall();
	}
	
	public String getGroupName()
	{
		return getTemplate().getGroupName();
	}
	
	public int getChildId()
	{
		return getTemplate().getChildDoorId();
	}
	
	@Override
	public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect)
	{
		if (isWall() && !isInInstance())
		{
			if (!attacker.isServitor())
			{
				return;
			}
			
			final Servitor servitor = (Servitor) attacker;
			if (servitor.getTemplate().getRace() != Race.SIEGE_WEAPON)
			{
				return;
			}
		}
		super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		final boolean isFort = ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getSiege().isInProgress());
		final boolean isCastle = ((getCastle() != null) && (getCastle().getResidenceId() > 0) && getCastle().getSiege().isInProgress());
		if (isFort || isCastle)
		{
			broadcastPacket(new SystemMessage(SystemMessageId.THE_CASTLE_GATE_HAS_BEEN_DESTROYED));
		}
		else
		{
			openMe();
		}
		
		return true;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		if (isVisibleFor(player))
		{
			player.sendPacket(new StaticObjectInfo(this, player.isGM()));
			player.sendPacket(new DoorStatusUpdate(this));
			if (getEmitter() > 0)
			{
				if (_isInverted)
				{
					player.sendPacket(new OnEventTrigger(getEmitter(), !_open));
				}
				else
				{
					player.sendPacket(new OnEventTrigger(getEmitter(), _open));
				}
			}
		}
	}
	
	@Override
	public void setTargetable(boolean targetable)
	{
		super.setTargetable(targetable);
		broadcastStatusUpdate();
	}
	
	public boolean checkCollision()
	{
		return getTemplate().isCheckCollision();
	}
	
	/**
	 * All doors are stored at DoorTable except instance doors
	 * @param doorId
	 * @return
	 */
	private Door getSiblingDoor(int doorId)
	{
		final Instance inst = getInstanceWorld();
		return (inst != null) ? inst.getDoor(doorId) : DoorData.getInstance().getDoor(doorId);
	}
	
	private void startAutoCloseTask()
	{
		if ((getTemplate().getCloseTime() < 0) || isOpenableByTime())
		{
			return;
		}
		
		final Future<?> oldTask = _autoCloseTask;
		if (oldTask != null)
		{
			_autoCloseTask = null;
			oldTask.cancel(false);
		}
		_autoCloseTask = ThreadPool.schedule(new AutoClose(), getTemplate().getCloseTime() * 1000);
	}
	
	class AutoClose implements Runnable
	{
		@Override
		public void run()
		{
			if (_open)
			{
				closeMe();
			}
		}
	}
	
	class TimerOpen implements Runnable
	{
		@Override
		public void run()
		{
			if (_open)
			{
				closeMe();
			}
			else
			{
				openMe();
			}
			
			int delay = _open ? getTemplate().getCloseTime() : getTemplate().getOpenTime();
			if (getTemplate().getRandomTime() > 0)
			{
				delay += Rnd.get(getTemplate().getRandomTime());
			}
			ThreadPool.schedule(this, delay * 1000);
		}
	}
	
	@Override
	public boolean isDoor()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getTemplate().getId());
		sb.append("](");
		sb.append(getObjectId());
		sb.append(")");
		return sb.toString();
	}
}
