
/******************************************************************************
   프로젝트명   : 농협 형상관리 시스템
   서브시스템명 : Query_Execute CLASS
   파일명      : Query_Execute.java      
   수정내역
   수정일         담당자       수정내용
------------------------------------------------------------------------------
   2006. 08. 08.  TEOK.KANG        최초생성 
******************************************************************************/


package com.ecams.common.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.Logger;
import com.ecams.common.base.ConfigFactory;
import com.ecams.common.logger.DebugPrintAssist;
import com.ecams.common.logger.EcamsLogger;


/**
 * Executes SQL queries  
 * <code>ResultSet</code>s.  This class is thread safe.
 * 
 * @see ResultSetHandler
 * 
 * @author TEOK.KANG
 */
public class Query_Execute {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
		
	/**
     * Constructor for Query_Execute.
     */
    public Query_Execute() {
        super();
    }

    /**
     * 넘겨받은 SQL_SCRIPT에 PARAM을 SETTING
     * @param stmt   PreparedStatement
     * @param params Query에 필요한 파라미터
     * value to pass in.
     * @throws SQLException
     */
    protected void fillStatement(PreparedStatement stmt, Object[] params)
        throws SQLException {

        if (params == null) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
        	if (params[i] != null){
        		ecamsLogger.debug("params["+i+"]=["+params[i].toString()+"]"); 
        		stmt.setObject(i + 1, params[i]);     		
        	}else if (params[i] == null){
        		ecamsLogger.debug("params["+i+"]=[NULL]");   
        		stmt.setNull(i + 1, Types.REAL);
        		//stmt.setNull(i + 1, Types.OTHER);
        	} else {
        		ecamsLogger.debug("WARNING...FAIL_STATEMENT");   
        	}//end of if-else statement
        }
    }//end of method fillStatement()

    
    /**
     * 파라미터가 없는 SQL SELECT QUERY EXECUTION.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rtn_type return object type.
     * @return The object returned by the handler.
     * @throws SQLException
     * @throws IOException 
     */
    public Object query(Connection conn, String prop_type, String sql, String rtn_type)
        throws SQLException, IOException {

        return this.query(conn, prop_type, sql, (Object[]) null, rtn_type);
    }//end of method() query
    
    /**
     * 파라미터가 한개이상 SQL SELECT QUERY EXECUTION.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param param The replacement parameter.
     * @param rtn_type return object type.
     * @return The object returned by the handler.
     * @throws SQLException
     * @throws IOException 
     */
    public Object query(
        Connection conn,
        String prop_type,
        String sql,
        Object param,
        String rtn_type)
        throws SQLException, IOException {

		return this.query(conn, prop_type, sql, new Object[] { param }, rtn_type);
    }

    /**
     * SQL SELECT QUERY EXECUTION. (ONLY SELECT QUERY)
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param params The replacement parameters.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(
        Connection conn,
        String prop_type,
        String sql,
        Object[] params,
        String rtn_type)
        throws SQLException, IOException {

        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        Object    rtn           = null;
        String    prop_query    = null;
        ArrayList arraylist     = new ArrayList();
        
        try {
        	//propertie file read
        	prop_query = ConfigFactory.getProperties( sql);
        	//prepareStatement setting...
            pstmt = conn.prepareStatement(prop_query);

            //parameter set prepareStatement
            this.fillStatement(pstmt, params);
            
            //sql-execution
            rs = this.wrap(pstmt.executeQuery());
			ResultSetMetaData rsmd = rs.getMetaData();

            //D는 DATASET H은 ResultSet
            if (rtn_type.equals("R")){
	            rtn = rs;
	            //Logger Print ONLY DEBUG
	            DebugPrintAssist.loggerPrint(sql+" IS EXECUTION : ", rs, rsmd);
            }else if (rtn_type.equals("H")){
            	arraylist = Query_Execute_Util.getHashMapFromRs(rs);
            	rtn       = arraylist;
            }else{
            	String cause   = "SELECT시 파라미터 R,H로 사용(사용법참조)바랍니다."; 
                SQLException e = new SQLException(cause);       	
            	this.rethrow(e, sql, prop_query, params);
            }//end of if-else statement
        } catch (SQLException e) {
        	ecamsLogger.error("SQLException..." + e.getMessage());
            this.rethrow(e, sql, prop_query, params);
        } catch (Exception e) {
        	ecamsLogger.error("Exception..." + e.getMessage());
            this.rethrow(e, sql, prop_query, params);   
        } finally {
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
        }
        return rtn;
    }

 
    /**
     * Exception 처리를 하여주는 메쏘드.
     * 
     * @param cause The original exception that will be chained to the new 
     * exception when it's rethrown. 
     * @param sql The query that was executing when the exception happened.
     * @param params The query replacement paramaters; <code>null</code> is a 
     * valid value to pass in.
     * @throws SQLException
     */
    protected synchronized void rethrow(Exception cause, String sql_index, String bind_sql, Object[] params)
        throws SQLException {

        StringBuffer msg = new StringBuffer(cause.getMessage().trim());

        msg.append(" ## ERROR Query_Index  : ");
        msg.append(sql_index);
        msg.append(" ## ERROR Query_Script : ");
        msg.append(bind_sql);       
        msg.append(" ## ERROR Parameters   : ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.asList(params));
        }

        SQLException e = new SQLException(msg.toString());
        e.setNextException((SQLException)cause);

        throw e;
    }


    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return The number of rows updated.
     * @throws SQLException
     * @throws IOException 
     */
    public int update(Connection conn, String prop_type, String sql) throws SQLException, IOException {
        return this.update(conn, prop_type, sql, (Object[]) null);
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query with a single replacement
     * parameter.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     * @throws SQLException
     * @throws IOException 
     */
    public int update(Connection conn, String prop_type, String sql, Object param)
        throws SQLException, IOException {

        return this.update(conn, prop_type, sql, new Object[] { param });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query. ONLY TRANSACTION QUERY
     * 
     * @param  conn The connection to use to run the query.
     * @param  sql The SQL to execute.
     * @param  params The query replacement parameters.
     * @return The number of rows updated.
     * @throws SQLException
     * @throws IOException 
     */
    public int update(Connection conn, String prop_type, String sql, Object[] params)
        throws SQLException, IOException {

        PreparedStatement pstmt = null;
        int rows                = 0;
        String    prop_query    = null;
        try {
        	//propertie file read
        	prop_query = ConfigFactory.getProperties( sql);
        	//preparedstatement setting...
            pstmt = conn.prepareStatement(prop_query);
        	//parameter set preparedstatement
            this.fillStatement(pstmt, params);
            
            //Logger Print ONLY DEBUG
            DebugPrintAssist.loggerPrint_tr(sql+" IS TRANSACTION EXECUTION", prop_query, params);
            
            //sql-execution
            rows = pstmt.executeUpdate();

        } catch (SQLException e) {
        	ecamsLogger.error("SQLException..." + e.getMessage());
            this.rethrow(e, sql, prop_query, params);
        } catch (Exception e) {
        	ecamsLogger.error("Exception..." + e.getMessage());
            this.rethrow(e, sql, prop_query, params);   
        } finally {
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
        }//end of try-catch statement

        return rows;
    }//end of method() update

/** Wrap  ResultSet
  * @param rs ResultSet
  * @return ResultSet
  */
   protected ResultSet wrap(ResultSet rs) {
       return rs;
   }

}//end of class Query_Execute
