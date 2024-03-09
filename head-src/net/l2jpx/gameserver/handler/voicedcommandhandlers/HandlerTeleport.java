package net.l2jpx.gameserver.handler.voicedcommandhandlers;


import engine.enums.Tokenizer;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

public class HandlerTeleport
{
	private static final Logger LOGGER = Logger.getLogger(HandlerTeleport.class.getName());

	public static void handlerTeleport(L2PcInstance player, Tokenizer tokenizer) {
		int X = tokenizer.getAsInteger(0, 0); // Índice do token para X
		int Y = tokenizer.getAsInteger(1, 0); // Índice do token para Y
		int Z = tokenizer.getAsInteger(2, 0); // Índice do token para Z

		LOGGER.info("Attempting to teleport player: " + player.getName() + " to X: " + X + " Y: " + Y + " Z: " + Z);

		if (player.getKarma() < 0) {
			String message = player.getName() + " " + "You don't teleport due to negative karma.";
			player.sendMessage(message);
			LOGGER.warning("Teleport cancelled due to negative karma. Player: " + player.getName());
			return;
		}

		player.teleToLocation(X, Y, Z, true);
		LOGGER.info("Teleport successful for player: " + player.getName() + " to X: " + X + " Y: " + Y + " Z: " + Z);
	}
}