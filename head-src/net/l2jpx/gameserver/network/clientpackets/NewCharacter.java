package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.CharTemplateTable;
import net.l2jpx.gameserver.model.base.ClassId;
import net.l2jpx.gameserver.network.serverpackets.CharTemplates;
import net.l2jpx.gameserver.templates.L2PcTemplate;

public final class NewCharacter extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(NewCharacter.class);
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
		{
			LOGGER.debug(getType() + ": Create New Char");
		}
		
		final CharTemplates ct = new CharTemplates();
		
		L2PcTemplate template = CharTemplateTable.getInstance().getTemplate(0);
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Human_Fighter); // Human Fighter
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Human_Mystic); // Human Mage
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Elven_Fighter); // Elf Fighter
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Elven_Mystic); // Elf Mage
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Dark_Fighter); // DE Fighter
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Dark_Mystic); // DE Mage
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Orc_Fighter); // Orc Fighter
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Orc_Mystic); // Orc Mage
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.Dwarven_Fighter); // Dwarf Fighter
		ct.addChar(template);
		
		// Finally
		sendPacket(ct);
	}
	
	@Override
	public String getType()
	{
		return "[C] 0E NewCharacter";
	}
}