package net.l2jpx.gameserver.model.quest.jython;

import net.l2jpx.gameserver.model.quest.Quest;

public abstract class QuestJython extends Quest
{
	public QuestJython(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);
	}
}
