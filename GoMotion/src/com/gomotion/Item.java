package com.gomotion;

public class Item
{
    public final String text;
    public final int icon;
    
    public Item(String text, Integer icon)
    {
        this.text = text;
        this.icon = icon;
    }
    
    @Override
    public String toString()
    {
        return text;
    }
}