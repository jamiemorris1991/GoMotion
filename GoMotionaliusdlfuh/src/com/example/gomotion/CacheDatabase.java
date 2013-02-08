package com.example.gomotion;

import android.content.Context;

public class CacheDatabase extends OfflineDatabase
{
	protected final String DATABASE_NAME = "cacheDB";
	
	public CacheDatabase(Context context)
	{
		super(context);
	}

}
