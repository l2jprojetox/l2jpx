package com.l2jpx.gameserver.handler;

import com.l2jpx.gameserver.handler.voicedcommandhandlers.*;

import java.util.HashMap;
import java.util.Map;

public class VoicedCommandHandler
{
    private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();

    public static VoicedCommandHandler getInstance()
    {
        return SingletonHolder._instance;
    }

    protected VoicedCommandHandler()
    {
        registerHandler(new AutoFarm());
        registerHandler(new NewMenu());
    }

    public void registerHandler(IVoicedCommandHandler handler)
    {
        String[] ids = handler.getVoicedCommandList();

        for (int i = 0; i < ids.length; i++)
            _datatable.put(ids[i].hashCode(), handler);
    }

    public IVoicedCommandHandler getHandler(String voicedCommand)
    {
        String command = voicedCommand;

        if (voicedCommand.indexOf(" ") != -1)
            command = voicedCommand.substring(0, voicedCommand.indexOf(" "));

        return _datatable.get(command.hashCode());
    }

    public int size()
    {
        return _datatable.size();
    }

    private static class SingletonHolder
    {
        protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
    }
}

