package engine.enums;

import net.l2jpx.gameserver.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public final class Tokenizer
{
    private final Map<Integer, String> _tokens = new HashMap<>();

    public Tokenizer(String str)
    {
        this(str, " ");
    }

    public Tokenizer(String str, String separator)
    {
        if (StringUtil.isEmpty(str, separator))
        {
            return;
        }

        for (final String s : str.split(separator))
        {
            _tokens.put(_tokens.size(), s);
        }
    }

    public final String getToken(int index)
    {
        return _tokens.get(index);
    }

    public final String getFirstToken()
    {
        return getToken(0);
    }

    public final String getLastToken()
    {
        return _tokens.isEmpty() ? null : _tokens.get(_tokens.size() - 1);
    }

    public final byte getAsByte(int index, byte defaultValue)
    {
        final String token = getToken(index);
        if (!StringUtil.isByte(token))
        {
            return defaultValue;
        }

        return Byte.parseByte(token);
    }

    public final int getAsInteger(int index, int defaultValue)
    {
        final String token = getToken(index);
        if (!StringUtil.isInteger(token))
        {
            return defaultValue;
        }

        return Integer.parseInt(token);
    }

    public final float getAsFloat(int index, float defaultValue)
    {
        final String token = getToken(index);
        if (!StringUtil.isFloat(token))
        {
            return defaultValue;
        }

        return Float.parseFloat(token);
    }

    public final double getAsDouble(int index, double defaultValue)
    {
        final String token = getToken(index);
        if (!StringUtil.isDouble(token))
        {
            return defaultValue;
        }

        return Double.parseDouble(token);
    }

    public final long getAsLong(int index, long defaultValue)
    {
        final String token = getToken(index);
        if (!StringUtil.isLong(token))
        {
            return defaultValue;
        }

        return Long.parseLong(token);
    }

    public final boolean getAsBoolean(int index)
    {
        return Boolean.parseBoolean(getToken(index));
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getAsEnum(int index, final Class<E> enumClass, final E defaultValue)
    {
        final Object val = _tokens.get(index);

        if (val != null && enumClass.isInstance(val))
        {
            return (E) val;
        }
        if (val instanceof String)
        {
            return Enum.valueOf(enumClass, (String) val);
        }

        return defaultValue;
    }

    public final int size()
    {
        return _tokens.size();
    }

    public final boolean isEmpty()
    {
        return _tokens.isEmpty();
    }

}

