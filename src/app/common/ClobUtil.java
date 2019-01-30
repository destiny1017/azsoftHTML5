package app.common;


import java.io.OutputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import weblogic.jdbc.vendor.oracle.OracleThinClob;



import org.apache.log4j.Logger;

import com.ecams.common.logger.EcamsLogger;

public class ClobUtil {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public String getClobData(PreparedStatement pstmt) throws SQLException, Exception {
		ResultSet         rs          = null;
		
		StringBuffer cloboutput = null;
		Reader clobInput  = null;
		char[] clobBuffer = null;
		int byteRead  = 0;
		
		try
		{
			rs = pstmt.executeQuery();
			
			cloboutput = new StringBuffer();
			
			if(rs.next())
			{
				clobInput = rs.getCharacterStream(1);
				clobBuffer = new char[1024];
				byteRead = 0;
				while((byteRead=clobInput.read(clobBuffer,0,1024))!=-1)
				{
					cloboutput.append(clobBuffer,0,byteRead);
				}
				clobInput.close();
			}
    
			rs.close();

		} catch (SQLException exception){
			ecamsLogger.error(exception);
			return null;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## ClobUtil.setClobData() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## ClobUtil.setClobData() Exception END ##");				
			return null;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
		}
		
		return cloboutput.toString();
	}//end of SelectUserInfo() method statement	

	public Boolean setClobData(PreparedStatement pstmt,String clobData) throws SQLException, Exception {
		ResultSet         rs          = null;
		byte[] 			  clobBuffer = null;
		Clob			  clobCol    = null;
		OutputStream	  clobStream = null;
		
		try {
			rs = pstmt.executeQuery();
			while (rs.next()) 
            {
				clobCol = rs.getClob(1);
                clobStream = ((OracleThinClob) clobCol).getAsciiOutputStream();
                clobBuffer = clobData.getBytes("ASCII");
                clobStream.write(clobBuffer);
                clobStream.flush();
            }

            
		} catch (SQLException exception){
			ecamsLogger.error("## ClobUtil.setClobData() Exception START ##");
			ecamsLogger.error(exception);
			ecamsLogger.error("## ClobUtil.setClobData() Exception END ##");				
			return false;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## ClobUtil.setClobData() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## ClobUtil.setClobData() Exception END ##");				
			return false;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
		}
		
		return true;
	}//end of SelectUserInfo() method statement	

}


