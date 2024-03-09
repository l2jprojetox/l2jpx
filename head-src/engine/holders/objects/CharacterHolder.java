package engine.holders.objects;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

import engine.enums.TeamType;

/**
 * @author fissban
 */
public class CharacterHolder extends ObjectHolder
{
	private TeamType team = TeamType.NONE;
	
	public CharacterHolder(L2Character character)
	{
		super(character);
	}
	
	@Override
	public L2Character getInstance()
	{
		return (L2Character) super.getInstance();
	}
	
	public PlayerHolder getActingPlayer()
	{
		return null;
	}
	
	/**
	 * Obtengo el team del objeto
	 * @return -> {@link TeamType}
	 */
	public TeamType getTeam()
	{
		return team;
	}
	
	/**
	 * Define el team del personaje:
	 * @param team -> {@link TeamType}
	 */
	public void setTeam(TeamType team)
	{
		this.team = team;
		
		if ((getInstance() != null) && (getInstance() instanceof L2PcInstance))
		{
			((L2PcInstance) getInstance()).broadcastUserInfo();
		}
	}
}
