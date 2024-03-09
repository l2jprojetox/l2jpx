package engine.Instance;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.model.actor.instance.L2DoorInstance;

/**
 * @author Administrador
 */
public class Instance
{
	private int id;
	private List<L2DoorInstance> doors;
	
	public Instance(int id)
	{
		this.id = id;
		doors = new ArrayList<>();
	}
	
	public void openDoors()
	{
		for (L2DoorInstance door : doors)
			door.openMe();
	}
	
	public void closeDoors()
	{
		for (L2DoorInstance door : doors)
			door.closeMe();
	}
	
	public void addDoor(L2DoorInstance door)
	{
		doors.add(door);
	}
	
	public List<L2DoorInstance> getDoors()
	{
		return doors;
	}
	
	public int getId()
	{
		return id;
	}
}
