package com.planetarypvp.bulka.loader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkaLoader
{
    private static Map<String, YamlConfiguration> configs = new HashMap<>();

    public static void load(Plugin plugin, String settings)
    {
        File file = new File(plugin.getDataFolder(), settings);

        if(!file.exists())
            return;

        for(YamlConfiguration config : getConfigs(file))
        {
            configs.put(config.getString("class"), config);
        }
    }

    private static List<YamlConfiguration> getConfigs(File file)
    {
        List<YamlConfiguration> configs = new ArrayList<YamlConfiguration>();

        for(File f : file.listFiles())
        {
            if(f.isDirectory())
                getConfigs(f);
            else
            {
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(f);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvalidConfigurationException e) {
                    // Ignore
                }
                if(config.get("class") != null)
                    configs.add(config);
            }

        }

        return configs;
    }

    public static void load(ConfigurableClass conf)
    {
        Class<? extends ConfigurableClass> confClass = conf.getClass();
        if(!configs.containsKey(confClass.getName()))
            return;//TODO
        YamlConfiguration config = configs.get(confClass.getName());
        List<Method> annotatedMethods = new ArrayList<>();

        for(Method m : confClass.getMethods())
        {
            if(m.isAnnotationPresent(Configurable.class))
            {
                String key = m.getName().substring(3);
                //System.out.println("config type is " + config.get(key).getClass().getName());
                if(config.get(key) != null)
                {
                    if(config.get(key) instanceof ArrayList && ConfigurationSerializable.class.isAssignableFrom(m.getParameterTypes()[0].getComponentType()))
                    {
                        System.out.println("list of maps detected");
                        List<?> list = config.getList(key);
                        List<Map<String, Object>> maps = new ArrayList<>();
                        List<ConfigurationSerializable> deserializeds = new ArrayList<>();
                        Class<?> configSerializableClass = m.getParameterTypes()[0].getComponentType();
                        Constructor constructor = null;
                        try {
                            constructor = configSerializableClass.getConstructor(Map.class);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        System.out.println("list of maps length: " + list.size());

                        for(Object o : list)
                        {
                            Map map = (Map) o;
                            System.out.println("map.alias = " + map.get("alias"));
                            maps.add(map);
                        }

                        for(Map map : maps)
                        {
                            try {
                                deserializeds.add((ConfigurationSerializable) constructor.newInstance(map));
                                System.out.println("null deserialized?" + deserializeds.get(0) == null);
                            } catch (InstantiationException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }

                        try {
                            System.out.println("ALIAS BE: " + deserializeds.get(0).getClass().getMethod("getAlias").invoke(deserializeds.get(0)));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }


                        System.out.println("Passin' " + m.getParameterTypes()[0].cast(listToArray(deserializeds, m.getParameterTypes()[0])).getClass().getName() + " for " + m.getParameterTypes()[0].getName());


                        try {
                            m.invoke(conf, m.getParameterTypes()[0].cast(listToArray(deserializeds, m.getParameterTypes()[0])));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                    else if(config.get(key) instanceof ArrayList)
                    {
                        System.out.println("[List]Passing " + ((ArrayList) config.get(key)).toArray().getClass().getName() + " for " + m.getParameterTypes()[0].getName());
                        System.out.println("length: " + m.getParameterTypes().length);

                        try {
                            m.invoke(conf, m.getParameterTypes()[0].cast(listToArray(config.getList(key), m.getParameterTypes()[0])));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                    else if(config.get(key) instanceof MemorySection)
                    {
                        MemorySection mem = (MemorySection) config.get(key);
                        Constructor constructor = null;
                        try {
                            constructor = m.getParameterTypes()[0].getConstructor(Map.class);
                            constructor.setAccessible(true);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            return;
                        }
                        ConfigurationSerializable serializable = null;
                        try {
                            serializable = (ConfigurationSerializable) constructor.newInstance((Map) mem.getValues(true));
                        } catch (InstantiationException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        try {
                            m.invoke(conf, m.getParameterTypes()[0].cast(serializable));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                    else
                    {
                        try {
                            System.out.println("Passing " + config.get(key).getClass().getSimpleName() + " for " + m.getParameterTypes()[0].getSimpleName());
                            m.invoke(conf, (Object) config.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                }

            }
        }
    }

    private static Object listToArray(List<?> list, Class<?> arrayType)
    {
        Object array = Array.newInstance(arrayType.getComponentType(), list.size());
        System.arraycopy(list.toArray(), 0, array, 0, list.size());
        return array;
    }
}
