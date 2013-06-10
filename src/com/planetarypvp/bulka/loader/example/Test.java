package com.planetarypvp.bulka.loader.example;

import com.planetarypvp.bulka.loader.BukkitLoader;
import com.planetarypvp.bulka.loader.Configurable;
import com.planetarypvp.bulka.loader.ConfigurableClass;

/**
 * Created with IntelliJ IDEA.
 * User: alexkizer
 * Date: 6/10/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test implements ConfigurableClass
{
    private int number;
    private double doub;
    private CoolObject[] coolObjects;


    public Test()
    {
        BukkitLoader.load(this);
        test();
    }

    private void test()
    {
        System.out.println("number = " + number);
        System.out.println("doub = " + doub);

        for(CoolObject o : coolObjects)
        {
            System.out.println("--CoolObject--");
            System.out.println("name = " + o.getName());
            System.out.println("x,y,z = " + o.getX() + ", " + o.getY() + ", " + o.getZ());
        }
    }

    @Configurable
    public void setNumber(int number)
    {
        this.number = number;
    }

    @Configurable
    public void setDoub(double doub)
    {
        this.doub = doub;
    }

    @Configurable
    public void setCoolObjects(CoolObject[] coolObjects)
    {
        this.coolObjects = coolObjects;
    }

    @Override
    public void loadDefaults(String message)
    {
        System.out.println("ERROR in Test: " + message);
    }
}
