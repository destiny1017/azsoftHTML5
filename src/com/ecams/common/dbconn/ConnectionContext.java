
package com.ecams.common.dbconn;

public interface ConnectionContext{
	public java.sql.Connection getConnection();
	public void rollback();
	public void commit();
	public void release();
}//end of ConnectionContext interface statement