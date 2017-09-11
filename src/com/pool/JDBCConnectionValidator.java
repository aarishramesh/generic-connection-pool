package com.pool;

import java.sql.Connection;
import java.sql.SQLException;

import com.pool.Pool.ConnectionValidator;


public final class JDBCConnectionValidator implements ConnectionValidator < Connection >
{
	public boolean isValid(Connection con)
	{ 
		if(con == null)
		{
			return false;
		}

		try
		{
			return !con.isClosed();
		}
		catch(SQLException se)
		{
			return false;
		}
	}

	public void invalidate(Connection con)
	{
		try
		{
			con.close();
		}
		catch(SQLException se)
		{

		}
	}
}
