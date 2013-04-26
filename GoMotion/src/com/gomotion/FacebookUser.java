package com.gomotion;

/**
 * Convinience class for holding data about 
 * a Facebook user, mainly used for the news feed
 * and leaderboard.
 * 
 * @author Jack Hindmarch
 *
 */
public class FacebookUser
{
	private String id;
	private String name;
	private String pictureURL;
	private int data;
	private int num;
	private double dataDouble;

	public FacebookUser(String id, String name, String pictureURL)
	{
		this.id = id;
		this.name = name;
		this.pictureURL = pictureURL;
	}

	public String getId() 
	{
		return id;
	}
	public void setId(String id) 
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getPictureURL() 
	{
		return pictureURL;
	}
	public void setPictureURL(String pictureURL) 
	{
		this.pictureURL = pictureURL;
	}
	
	public int getData() 
	{
		return data;
	}
	public void setData(int data) 
	{
		this.data = data;
	}
	public double getDataDouble()
	{
		return dataDouble;
	}
	public void setDataDouble(double dataDouble)
	{
		this.dataDouble = dataDouble;
	}
	
	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}
	public String toString()
	{
		return this.name;
	}
}
