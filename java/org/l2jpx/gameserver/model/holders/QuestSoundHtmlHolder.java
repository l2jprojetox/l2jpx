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
package org.l2jpx.gameserver.model.holders;

/**
 * @author Mobius
 */
public class QuestSoundHtmlHolder
{
	private final String _sound;
	private final String _html;
	
	public QuestSoundHtmlHolder(String sound, String html)
	{
		_sound = sound;
		_html = html;
	}
	
	public String getSound()
	{
		return _sound;
	}
	
	public String getHtml()
	{
		return _html;
	}
}