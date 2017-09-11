package com.pool;

import java.sql.Connection;

public class Main {
	public static void main(String[] args){
		Pool < Connection > pool = 
				PoolFactory.newBoundedBlockingPool(
						10, 
						new JDBCConnectionFactory("", "", "", ""), 
						new JDBCConnectionValidator());
		Connection conn = pool.get();
		pool.release(conn);
	}
}
