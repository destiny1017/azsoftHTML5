/*****************************************************************************************
	1. program ID	: ConnectionResource.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: ConnectionResource
 *****************************************************************************************/

package com.ecams.common.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ecams.common.base.ConfigFactory;
import com.ecams.common.base.Encryptor;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 */
public class ConnectionResource implements ConnectionContext {

	private Connection connection = null;
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

    //private boolean wasType = true;  /* true: ����  false: ������ , jeus */


	public ConnectionResource() throws Exception {
		init(false);
	}// end of ConnectionResource construct statement

	/**
	 * @param transaction
	 * @throws Exception
	 */
	public ConnectionResource(boolean transaction) throws Exception {
		init(transaction);
	}// end of ConnectionResource construct statement

	public ConnectionResource(boolean transaction,String constr) throws Exception {
		init(transaction,constr);
	}// end of ConnectionResource construct statement

	/**
	 * @param transaction
	 * @throws Exception
	 *
	 * connection �� �δ´�.
	 */
	public void init(boolean transaction) throws Exception {
		try{
			if (ConfigFactory.getProperties("O_jdbcUse").equals("true")){
				String strUrl = "";
				String strUsernm = "";
				String strUserps = "";
				if (ConfigFactory.getProperties("O_secu").equals("true")) {
					Encryptor oEncryptor = Encryptor.instance();
					strUrl = oEncryptor.strGetDecrypt(ConfigFactory.getProperties("O_url"));
					strUsernm = oEncryptor.strGetDecrypt(ConfigFactory.getProperties("O_username"));
					strUserps = oEncryptor.strGetDecrypt(ConfigFactory.getProperties("O_password"));
				} else {
					strUrl = ConfigFactory.getProperties("O_url");
					strUsernm = ConfigFactory.getProperties("O_username");
					strUserps = ConfigFactory.getProperties("O_password");
				}
				//System.out.println("strUrl,strUsernm,strUserps " + strUrl + strUsernm + strUserps);
				Class.forName(ConfigFactory.getProperties("O_driverClassName"));
				connection = DriverManager.getConnection(strUrl,strUsernm,strUserps);
			}
			else{
				Context ctx = new InitialContext();

				DataSource ds = (DataSource)ctx.lookup(ConfigFactory.getProperties("O_poolName"));
				connection = ds.getConnection();
			}

			if(connection == null){
				System.out.println("ConnectionResource>connection is " + connection);
				throw new Exception("DriverManager fail to get connection");
			}
			else if(transaction)
				connection.setAutoCommit(false);

		} catch (Exception e) {
			e.printStackTrace();
			ecamsLogger.error("## ConnectionResource.init() Exception START ##");
			ecamsLogger.error("## Error DESC : ", e);
			ecamsLogger.error("## ConnectionResource.init() Exception END ##");
		}
	}// end of init method statement

	public void init(boolean transaction,String connstr) throws Exception {
		try{
			//System.out.println("transaction "+transaction+" connstr "+connstr);
			if (ConfigFactory.getProperties(connstr+"_jdbcUse").equals("true")){
				String strUrl = "";
				String strUsernm = "";
				String strUserps = "";
				if (ConfigFactory.getProperties(connstr+"_secu").equals("true")) {
					Encryptor oEncryptor = Encryptor.instance();
					strUrl = oEncryptor.strGetDecrypt(ConfigFactory.getProperties(connstr+"_url"));
					strUsernm = oEncryptor.strGetDecrypt(ConfigFactory.getProperties(connstr+"_username"));
					strUserps = oEncryptor.strGetDecrypt(ConfigFactory.getProperties(connstr+"_password"));
				} else {
					strUrl = ConfigFactory.getProperties(connstr+"_url");
					strUsernm = ConfigFactory.getProperties(connstr+"_username");
					strUserps = ConfigFactory.getProperties(connstr+"_password");
				}
				//Class.forName(ConfigFactory.getProperties(connstr+"_driverClassName"));
				Class.forName(ConfigFactory.getProperties(connstr+"_driverClassName"));
				//System.out.println("111strUrl,strUsernm,strUserps " + strUrl + strUsernm + strUserps);
				connection = DriverManager.getConnection(strUrl,strUsernm,strUserps);
				//System.out.println("222strUrl,strUsernm,strUserps " + strUrl + strUsernm + strUserps);
			}
			else{
				Context ctx = new InitialContext();
				DataSource ds = (DataSource)ctx.lookup(ConfigFactory.getProperties(connstr+"_poolName"));
				connection = ds.getConnection();
			}

			if(connection == null){
				System.out.println("ConnectionResource>connection is " + connection);
				throw new Exception("DriverManager fail to get connection");
			}
			else if(transaction)
				connection.setAutoCommit(false);

		} catch (Exception e) {
			e.printStackTrace();
			ecamsLogger.error("## ConnectionResource.init() Exception START ##");
			ecamsLogger.error("## Error DESC : ", e);
			ecamsLogger.error("## ConnectionResource.init() Exception END ##");
		}

	}// end of init method statement


	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecams.common.dbconn.ConnectionContext#getConnection()
	 */
	public Connection getConnection(){
		return connection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecams.common.dbconn.ConnectionContext#rollback()
	 */
	public void rollback() {
		// if (transaction)
		// if (connection != null)
		// try {
		// connection.rollback();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}// end of rollback method statement

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecams.common.dbconn.ConnectionContext#commit()
	 */
	public void commit() {
		// if (transaction)
		// if (connection != null)
		// try {
		// connection.commit();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}// end of commit method statement

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ecams.common.dbconn.ConnectionContext#release()
	 */
	public void release() {
		// if(connection != null)
		// if(transaction){
		// try{connection.setAutoCommit(true);}catch(Exception
		// ex){ex.printStackTrace();}
		// }
		// try{
		// //DBCP
		// connection.close();
		// }catch(Exception ex)
		// {ex.printStackTrace();}
	}// end of release method statement

	public static void release(Connection conn) {
		if (conn != null) {
			try {
				// DBCP
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}// end of ConnectionResource class statement
