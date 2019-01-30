package app.common;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

//import com.ecams.common.dbconn.ConnectionContext;
//import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class AutoSeq {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public String getSeqNo(Connection conn,String ReqNo) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();

		Integer			  SV_DBseq;
		String			  Seq_Val = null;
		String			  nowDt= null;

    	SimpleDateFormat formatter = null;
    	Date currentTime = null;


		try {
			conn.setAutoCommit(true);
			formatter = new SimpleDateFormat("yyyy",Locale.KOREA);
			currentTime = new Date();
			nowDt = formatter.format(currentTime);
			currentTime = null;
			formatter = null;

		    strQuery.append("select cr_no,to_char(cr_lastdt,'yyyy') as lstdt ");
		    strQuery.append("from cmr0010 ");
			strQuery.append("where cr_gubun= ? ");

			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, ReqNo);

            rs = pstmt.executeQuery();


            if (rs.next() == false){
            	strQuery.setLength(0);
            	strQuery.append("insert into cmr0010 (CR_GUBUN,CR_NO,CR_LASTDT,CR_LASTID) ");
            	strQuery.append(" values (?,'000001',SYSDATE,'') ");

            	pstmt2 = conn.prepareStatement(strQuery.toString());
            	pstmt2.setString(1, ReqNo);

            	pstmt2.executeUpdate();
            	pstmt2.close();
            	SV_DBseq = 0;
            }
            else{
            	SV_DBseq = Integer.parseInt(rs.getString("cr_no"));

            	if (Integer.parseInt(rs.getString("lstdt")) < Integer.parseInt(nowDt)){
            		SV_DBseq = 0;
            	}
            	strQuery.setLength(0);
            	strQuery.append("update cmr0010 ");
            	strQuery.append("set cr_no= ? , ");
            	strQuery.append("    cr_lastdt=SYSDATE ");
            	strQuery.append("where cr_gubun= ? ");

            	pstmt2 = conn.prepareStatement(strQuery.toString());

            	pstmt2.setString(1, Integer.toString(SV_DBseq+1));
            	pstmt2.setString(2, ReqNo);

            	pstmt2.executeUpdate();

            	pstmt2.close();

            }
            pstmt2 = null;

            rs.close();
            pstmt.close();
            conn.commit();

            rs = null;
            pstmt = null;

            Seq_Val = nowDt + ReqNo.substring(ReqNo.length()-2, ReqNo.length());

            Seq_Val = Seq_Val+ String.format("%06d", SV_DBseq+1);

            return Seq_Val;

		} catch (SQLException exception){
			ecamsLogger.error(exception);
			conn.rollback();
			return null;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## AutoSeq.getSeqNo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## AutoSeq.getSeqNo() Exception END ##");
			throw exception;
		}finally{
			conn.setAutoCommit(false);
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}

	public String getSeqNo_yyyymm(String ReqNo,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();

		Integer			  SV_DBseq;
		String			  Seq_Val = null;
		String			  nowDt= null;

    	SimpleDateFormat formatter = null;
    	Date currentTime = null;


		try {
			conn.setAutoCommit(true);
			formatter = new SimpleDateFormat("yyyyMM",Locale.KOREA);
			currentTime = new Date();
			nowDt = formatter.format(currentTime);
			currentTime = null;
			formatter = null;

		    strQuery.append("select cr_no,to_char(cr_lastdt,'yyyymm') as lstdt     \n");
		    strQuery.append("from cmr0010                                          \n");
			strQuery.append("where cr_gubun= ?                                     \n");

			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, ReqNo);

            rs = pstmt.executeQuery();


            if (rs.next() == false){
            	strQuery.setLength(0);
            	strQuery.append("insert into cmr0010 (CR_GUBUN,CR_NO,CR_LASTDT,CR_LASTID) ");
            	strQuery.append(" values (?,1,SYSDATE,'') ");

            	pstmt2 = conn.prepareStatement(strQuery.toString());
            	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
            	pstmt2.setString(1, ReqNo);

            	pstmt2.executeUpdate();
            	pstmt2.close();
            	SV_DBseq = 0;
            }
            else{
            	SV_DBseq = rs.getInt("cr_no")+1;

            	if (Integer.parseInt(rs.getString("lstdt")) < Integer.parseInt(nowDt)){
            		SV_DBseq = 0;
            	}

            	strQuery.setLength(0);
            	strQuery.append("update cmr0010 set cr_no=?, cr_lastdt=SYSDATE     \n");
            	strQuery.append("where cr_gubun=?                                  \n");

            	pstmt2 = conn.prepareStatement(strQuery.toString());

            	pstmt2.setInt(1, SV_DBseq);
            	pstmt2.setString(2, ReqNo);

            	pstmt2.executeUpdate();

            	pstmt2.close();

            }
            pstmt2 = null;

            rs.close();
            pstmt.close();
            conn.commit();

            rs = null;
            pstmt = null;

            Seq_Val = nowDt + String.format("%06d", SV_DBseq+1);

            return Seq_Val;

		} catch (SQLException exception){
			ecamsLogger.error(exception);
			conn.rollback();
			return null;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## AutoSeq.getSeqNo_yyyymm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## AutoSeq.getSeqNo_yyyymm() Exception END ##");
			throw exception;
		}finally{
			conn.setAutoCommit(false);
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
}
