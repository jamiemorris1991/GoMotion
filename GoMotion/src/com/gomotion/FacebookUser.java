package com.gomotion;

public class FacebookUser
{
	private String id;
	private String name;
	private String pictureURL;
	
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

	public String toString()
	{
		return this.name;
	}
}
