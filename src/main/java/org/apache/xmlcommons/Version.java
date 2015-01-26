package org.apache.xmlcommons;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Version {

    public static String getVersion()
    {
        return getProduct() + " " + getVersionNum();
    }

    public static String getProduct()
    {
        return "XmlCommonsExternal";
    }

    public static String getVersionNum()
    {
        return "1.3.04";
    }

    public static void main(String[] paramArrayOfString)
    {
        System.out.println(getVersion());
    }

}
