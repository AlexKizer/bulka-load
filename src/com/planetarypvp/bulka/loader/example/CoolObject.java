package com.planetarypvp.bulka.loader.example;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alexkizer
 * Date: 6/10/13
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoolObject implements ConfigurationSerializable
{
    private String name = "";
    private int x;
    private int y;
    private int z;

    public CoolObject()
    {

    }

    public String getName()
    {
        return name;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public CoolObject(Map<String, Object> map)
    {
        deserialize(map);
    }


    public void deserialize(Map<String, Object> map)
    {
        name = (String) map.get("name");
        x = (int) map.get("x");
        y = (int) map.get("x");
        z = (int) map.get("x");
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }
}
