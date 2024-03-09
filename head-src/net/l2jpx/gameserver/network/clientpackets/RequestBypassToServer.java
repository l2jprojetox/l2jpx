package net.l2jpx.gameserver.network.clientpackets;

import java.util.StringTokenizer;

import engine.enums.Tokenizer;
import net.l2jpx.gameserver.handler.voicedcommandhandlers.HandlerTeleport;
import org.apache.log4j.Logger;

import engine.EngineModsManager;
import net.l2jpx.Config;
import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.autofarm.AutofarmPlayerRoutine;
import net.l2jpx.gameserver.communitybbs.CommunityBoard;
import net.l2jpx.gameserver.datatables.xml.AdminCommandAccessRights;
import net.l2jpx.gameserver.handler.AdminCommandHandler;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.handler.IVoicedCommandHandler;
import net.l2jpx.gameserver.handler.VoicedCommandHandler;
import net.l2jpx.gameserver.handler.custom.CustomBypassHandler;
import net.l2jpx.gameserver.handler.voicedcommandhandlers.Menu;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.position.L2CharPosition;
import net.l2jpx.gameserver.model.entity.olympiad.Olympiad;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.ExShowScreenMessage;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.util.StringUtil;
import net.l2jpx.util.L2Log;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestBypassToServer.class);
	
	private String bypassCommand;
	
	@Override
	protected void readImpl()
	{
		bypassCommand = readS();
	}
	
	@SuppressWarnings("unused")
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(bypassCommand))
		{
			return;
		}
		
		try
		{
			
			final AutofarmPlayerRoutine bot = activeChar.getBot();
			if (bypassCommand.startsWith("admin_"))
			{
				if (!activeChar.isGM())
				{
					LOGGER.warn(activeChar.getName() + " tried to use admin command without being GM!");
					return;
				}
				
				if (EngineModsManager.onVoiced(activeChar, bypassCommand))
				{
					return;
				}
				
				// DaDummy: this way we LOGGER every admincommand with all related info
				String command;
				
				if (bypassCommand.contains(" "))
				{
					command = bypassCommand.substring(0, bypassCommand.indexOf(" "));
				}
				else
				{
					command = bypassCommand;
				}
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command + " does not exists!");
					LOGGER.warn("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access right to use this command!");
					LOGGER.warn("Character " + activeChar.getName() + " tried to use admin command " + command + ", but doesn't have access to it!");
					return;
				}
				
				if (Config.GMAUDIT)
				{
					String target = activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target";
					String message = "GM: " + activeChar + ", Target: [" + target + "], Command: [" + command + "], Params: [" + bypassCommand.replace(command, "") + "]";
					String fileName = activeChar.getName() + "_" + activeChar.getObjectId(); // ReynalDev_268477761
					L2Log.add(message, "gm_commands", fileName);
				}
				
				ach.useAdminCommand(bypassCommand, activeChar);
			}
			else if (bypassCommand.equals("come_here") && activeChar.isGM())
			{
				comeHere(activeChar);
			}
			else if (bypassCommand.startsWith("player_help "))
			{
				playerHelp(activeChar, bypassCommand.substring(12));
			}
			else if (bypassCommand.startsWith("teleport ")) {
				try {
					String commandParameters = bypassCommand.substring("teleport ".length()).trim();
					Tokenizer tokenizer = new Tokenizer(commandParameters);

					HandlerTeleport.handlerTeleport(activeChar, tokenizer);
				} catch (Exception e) {
					activeChar.sendMessage("Error processing teleport command.");
				}
			}
			else if (bypassCommand.startsWith("voiced_"))
			{
				String command = bypassCommand.split(" ")[0];
				IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getVoicedCommandHandler(bypassCommand.substring(7));
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command.substring(7) + " does not exist!");
					LOGGER.warn("No handler registered for command '" + bypassCommand + "'");
					return;
				}

				else if (bypassCommand.startsWith("_infosettings"))
				{
					showAutoFarm(activeChar);
				}
				
				else if (bypassCommand.startsWith("_autofarm"))
				{
					if (activeChar.isAutoFarm())
					{
						bot.stop();
						activeChar.setAutoFarm(false);
					}
					else
					{
						bot.start();
						activeChar.setAutoFarm(true);
					}
					
				}
				
				if (bypassCommand.startsWith("_pageAutoFarm"))
				{
					StringTokenizer st = new StringTokenizer(bypassCommand, " ");
					st.nextToken();
					try
					{
						String param = st.nextToken();
						
						if (param.startsWith("inc_page") || param.startsWith("dec_page"))
						{
							int newPage;
							
							if (param.startsWith("inc_page"))
							{
								newPage = activeChar.getPage() + 1;
							}
							else
							{
								newPage = activeChar.getPage() - 1;
							}
							
							if (newPage >= 0 && newPage <= 9)
							{
								String[] pageStrings =
								{
									"F1",
									"F2",
									"F3",
									"F4",
									"F5",
									"F6",
									"F7",
									"F8",
									"F9",
									"F10"
								};
								
								activeChar.setPage(newPage);
								activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Skill Bar " + pageStrings[newPage], 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
								activeChar.saveAutoFarmSettings();
								
							}
							
						}
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if (bypassCommand.startsWith("_enableBuffProtect"))
				{
					activeChar.setNoBuffProtection(!activeChar.isNoBuffProtected());
					if (activeChar.isNoBuffProtected())
					{
						activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Buff Protect On", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					else
					{
						activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Buff Protect Off", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					activeChar.saveAutoFarmSettings();
				}
				if (bypassCommand.startsWith("_enableSummonAttack"))
				{
					activeChar.setSummonAttack(!activeChar.isSummonAttack());
					if (activeChar.isSummonAttack())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.ACTIVATE_SUMMON_ACTACK));
						activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Summon Attack On", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					else
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.DESACTIVATE_SUMMON_ACTACK));
						activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Summon Attack Off", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					activeChar.saveAutoFarmSettings();
				}
				
				if (bypassCommand.startsWith("_enableRespectHunt"))
				{
					
					activeChar.setAntiKsProtection(!activeChar.isAntiKsProtected());
					if (activeChar.isAntiKsProtected())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.ACTIVATE_RESPECT_HUNT));
						activeChar.sendPacket(new ExShowScreenMessage("Respct Hunt On", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					else
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.DESACTIVATE_RESPECT_HUNT));
						activeChar.sendPacket(new ExShowScreenMessage("Respct Hunt Off", 3 * 1000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
					}
					activeChar.saveAutoFarmSettings();
					
				}
				
				if (bypassCommand != null && activeChar != null)
				{
					if (bypassCommand.startsWith("_radiusAutoFarm"))
					{
						StringTokenizer st = new StringTokenizer(bypassCommand, " ");
						
						// Verifica se há um token após o comando "_radiusAutoFarm"
						if (st.countTokens() >= 2)
						{
							st.nextToken(); // Pula o comando inicial
							
							String param = st.nextToken(); // Pega o próximo token que é o parâmetro
							int radiusChange = 200; // Define o valor padrão de alteração do raio
							
							try
							{
								if (param.startsWith("inc_radius"))
								{
									activeChar.setRadius(activeChar.getRadius() + radiusChange);
								}
								else if (param.startsWith("dec_radius"))
								{
									activeChar.setRadius(activeChar.getRadius() - radiusChange);
								}
								activeChar.sendPacket(new ExShowScreenMessage("Auto Farm Range: " + activeChar.getRadius(), 3000, ExShowScreenMessage.POSITION.BOTTOM_CENTER, null, false));
								
								// Salvando as configurações após ajustar o raio
								activeChar.saveAutoFarmSettings();
							}
							catch (Exception e)
							{
								e.printStackTrace();
								// Considerar adicionar alguma forma de notificação ao usuário aqui, se aplicável
							}
						}
						else
						{
							// Log ou notificação para o caso de comando malformado
							System.err.println("Comando não reconhecido ou faltando parâmetros.");
						}
					}

				}
				else
				{
					// Caso o bypassCommand ou activeChar sejam nulos
					System.err.println("Erro: Comando não reconhecido ou personagem ativo nulo.");
				}
				
				ach.useVoicedCommand(bypassCommand.substring(7), activeChar, null);
				
			}
			else if (bypassCommand.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(bypassCommand))
				{
					return;
				}
				
				int endOfId = bypassCommand.indexOf('_', 5);
				String id;
				
				if (endOfId > 0)
				{
					id = bypassCommand.substring(4, endOfId);
				}
				else
				{
					id = bypassCommand.substring(4);
				}
				
				try
				{
					L2Object npc = L2World.getInstance().findObject(Integer.parseInt(id));
					
					if (npc instanceof L2NpcInstance && endOfId > 0 && activeChar.isInsideRadius(npc, L2NpcInstance.INTERACTION_DISTANCE, false, false))
					{
						((L2NpcInstance) npc).onBypassFeedback(activeChar, bypassCommand.substring(endOfId + 1));
					}
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
					LOGGER.error("RequestByPassToServer.runImpl : invalid number format", nfe);
				}
			}
			// Navigate throught Manor windows
			else if (bypassCommand.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2NpcInstance)
				{
					((L2NpcInstance) object).onBypassFeedback(activeChar, bypassCommand);
				}
			}
			else if (bypassCommand.startsWith("bbs_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), bypassCommand);
			}
			else if (bypassCommand.startsWith("_bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), bypassCommand);
			}
			else if (bypassCommand.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(bypassCommand))
				{
					return;
				}
				
				L2PcInstance player = getClient().getActiveChar();
				if (player == null)
				{
					return;
				}
				
				String p = bypassCommand.substring(6).trim();
				int idx = p.indexOf(' ');
				
				if (idx < 0)
				{
					player.processQuestEvent(p, "");
				}
				else
				{
					player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
				}
			}
			
			else if (bypassCommand.startsWith("buffprot"))
			{
				if (activeChar.isBuffProtected())
				{
					activeChar.setIsBuffProtected(false);
					activeChar.sendMessage("Buff protection is disabled.");
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsBuffProtected(true);
					activeChar.sendMessage("Buff protection is enabled.");
					Menu.showMainHtml(activeChar);
				}
			}
			else if (bypassCommand.startsWith("tradeprot"))
			{
				if (activeChar.isInTradeProt())
				{
					activeChar.setIsInTradeProt(false);
					activeChar.sendMessage("Trade acceptance mode is enabled.");
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsInTradeProt(true);
					activeChar.sendMessage("Trade refusal mode is enabled.");
					Menu.showMainHtml(activeChar);
				}
			}
			else if (bypassCommand.startsWith("ssprot"))
			{
				if (activeChar.isSSDisabled())
				{
					activeChar.setIsSSDisabled(false);
					activeChar.sendMessage("Soulshots effects are enabled.");
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsSSDisabled(true);
					activeChar.sendMessage("Soulshots effects are disabled.");
					Menu.showMainHtml(activeChar);
				}
			}
			else if (bypassCommand.startsWith("xpnot"))
			{
				if (activeChar.cantGainXP())
				{
					activeChar.cantGainXP(false);
					activeChar.sendMessage("Enable Xp");
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.cantGainXP(true);
					activeChar.sendMessage("Disable Xp");
					Menu.showMainHtml(activeChar);
				}
			}
			else if (bypassCommand.startsWith("pmref"))
			{
				if (activeChar.getMessageRefusal())
				{
					activeChar.setMessageRefusal(false);
					activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_ACCEPTANCE_MODE));
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.setMessageRefusal(true);
					activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_REFUSAL_MODE));
					Menu.showMainHtml(activeChar);
				}
			}
			else if (bypassCommand.startsWith("partyin"))
			{
				if (activeChar.isPartyInvProt())
				{
					activeChar.setIsPartyInvProt(false);
					activeChar.sendMessage("Party acceptance mode is enabled.");
					Menu.showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsPartyInvProt(true);
					activeChar.sendMessage("Party refusal mode is enabled.");
					Menu.showMainHtml(activeChar);
				}
			}
			
			else if (bypassCommand.startsWith("custom_"))
			{
				CustomBypassHandler.getInstance().handleBypass(activeChar, bypassCommand);
			}
			else if (bypassCommand.startsWith("OlympiadArenaChange"))
			{
				Olympiad.bypassChangeArena(bypassCommand, activeChar);
			}
			else if (bypassCommand.startsWith("Engine"))
			{
				EngineModsManager.onEvent(activeChar, bypassCommand.replace("Engine ", ""));
			}
			
		}
		catch (Exception e)
		{
			LOGGER.warn("Bad RequestBypassToServer: ", e);
		}
	}
	
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj == null)
		{
			return;
		}
		
		if (obj instanceof L2NpcInstance)
		{
			final L2NpcInstance temp = (L2NpcInstance) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
		}
		
	}
	
	private void playerHelp(L2PcInstance activeChar, String path)
	{
		if (path.contains(".."))
		{
			return;
		}
		
		String filename = "data/html/help/" + path;
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	private static final String ACTIVED = "<font color=00FF00>STARTED</font>";
	private static final String DESATIVED = "<font color=FF0000>STOPPED</font>";
	private static final String STOP = "STOP";
	private static final String START = "START";
	
	public static void showAutoFarm(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/auto/AutoFarm.htm");
		html.replace("%player%", activeChar.getName());
		html.replace("%page%", StringUtil.formatNumber(activeChar.getPage() + 1));
		html.replace("%heal%", StringUtil.formatNumber(activeChar.getHealPercent()));
		html.replace("%radius%", StringUtil.formatNumber(activeChar.getRadius()));
		html.replace("%summonSkill%", StringUtil.formatNumber(activeChar.getSummonSkillPercent()));
		html.replace("%hpPotion%", StringUtil.formatNumber(activeChar.getHpPotionPercentage()));
		html.replace("%mpPotion%", StringUtil.formatNumber(activeChar.getMpPotionPercentage()));
		html.replace("%noBuff%", activeChar.isNoBuffProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%summonAtk%", activeChar.isSummonAttack() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%antiKs%", activeChar.isAntiKsProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%autofarm%", activeChar.isAutoFarm() ? ACTIVED : DESATIVED);
		html.replace("%button%", activeChar.isAutoFarm() ? STOP : START);
		activeChar.sendPacket(html);
	}
	
	@Override
	public String getType()
	{
		return "[C] 21 RequestBypassToServer";
	}
}
