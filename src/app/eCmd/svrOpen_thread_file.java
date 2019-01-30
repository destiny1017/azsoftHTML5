package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

//import com.ecams.common.dbconn.ConnectionContext;
//import com.ecams.common.dbconn.ConnectionResource;
//import app.common.LoggableStatement;
import com.ecams.common.logger.EcamsLogger;
import app.common.LoggableStatement;

public class svrOpen_thread_file implements Runnable{
 
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	private HashMap<String,String> myetcData = null;
	private ArrayList<HashMap<String, String>> parrent_rtList = null;
	private Connection conn1 = null;


	svrOpen_thread_file(HashMap<String,String> etcData,ArrayList<HashMap<String, String>> rtList,Connection conn){
		myetcData = etcData;
		parrent_rtList = rtList;
		conn1 = conn;
	}

	public void run() {
		HashMap<String,String> tmpRtList = null;
		try {
			tmpRtList = Master_Check(myetcData,conn1);

			if (tmpRtList != null) {
					parrent_rtList.add(tmpRtList);

			}
			//ecamsLogger.error("+++null+++"+tmpRtList.toString());

		} catch ( InterruptedException e ){
			e.printStackTrace();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public HashMap<String,String> Master_Check(HashMap<String,String> etcData,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            wkDsnCd     = "";
		String            wkB         = "";
		String            wkC         = "";
		String            chgPath     = "";
		String            baseHome    = "";
		String            svrHome     = "";
		boolean           findSw      = false;
		HashMap<String,String> rst = null;

		try {
			wkB = etcData.get("filename");
			if ( etcData.get("svrchg").equals("Y") ) {
				strQuery.setLength(0);
	        	strQuery.append("select a.cr_rsrccd,b.cm_dirpath      \n");
	        	strQuery.append("  from cmr0020 a,cmm0070 b           \n");
				strQuery.append(" where a.cr_syscd=?                  \n");
				strQuery.append("   and a.cr_rsrcname=?               \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_dsncd=b.cm_dsncd         \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, etcData.get("syscd"));
	            pstmt.setString(2, wkB);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	strQuery.setLength(0);
	            	strQuery.append("select b.cm_volpath from cmm0031 a,cmm0038 b  \n");
	            	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd=?          \n");
	            	strQuery.append("   and a.cm_closedt is null                   \n");
	            	strQuery.append("   and a.cm_syscd=b.cm_syscd                  \n");
	            	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                  \n");
	            	strQuery.append("   and a.cm_seqno=b.cm_seqno                  \n");
	            	strQuery.append("   and b.cm_rsrccd=?                          \n");
	            	//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, etcData.get("syscd"));
		            pstmt2.setString(2, etcData.get("basesvr"));
		            pstmt2.setString(3, rs.getString("cr_rsrccd"));
		            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()) {
		            	baseHome = rs2.getString("cm_volpath");
		            }
		            rs2.close();
		            pstmt2.close();
		            
	            	strQuery.setLength(0);
	            	strQuery.append("select b.cm_volpath from cmm0031 a,cmm0038 b  \n");
	            	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd=?          \n");
	            	strQuery.append("   and a.cm_seqno=?                           \n");
	            	strQuery.append("   and a.cm_closedt is null                   \n");
	            	strQuery.append("   and a.cm_syscd=b.cm_syscd                  \n");
	            	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                  \n");
	            	strQuery.append("   and a.cm_seqno=b.cm_seqno                  \n");
	            	strQuery.append("   and b.cm_rsrccd=?                          \n");
	            	//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, etcData.get("syscd"));
		            pstmt2.setString(2, etcData.get("svrcd"));
		            pstmt2.setString(3, etcData.get("svrseq"));
		            pstmt2.setString(4, rs.getString("cr_rsrccd"));
		            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()) {
		            	svrHome = rs2.getString("cm_volpath");
		            }
		            rs2.close();
		            pstmt2.close();
		            
		            chgPath = etcData.get("dirpath").replace(svrHome, baseHome);
		            
		            //ecamsLogger.error("dirpath,chgpath,svrhome,basehome===="+etcData.get("dirpath")+", "+chgPath+", "+svrHome+", "+baseHome);
		            strQuery.setLength(0);
					strQuery.append("select cm_dsncd from cmm0070            \n");
					strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, etcData.get("syscd"));
		            pstmt2.setString(2, chgPath);
		            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()) {
		            	wkDsnCd = rs2.getString("cm_dsncd");
		            	findSw = true;
		            }
		            rs2.close();
		            pstmt2.close();
		            if (findSw) break;
	            }
	            rs.close();
	            pstmt.close();
			} else {   
				strQuery.setLength(0);
				strQuery.append("select cm_dsncd from cmm0070            \n");
				strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, etcData.get("syscd"));
	            pstmt.setString(2, etcData.get("dirpath"));
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	wkDsnCd = rs.getString("cm_dsncd");
	            }
	            rs.close();
	            pstmt.close();
	
	            if (wkB.indexOf(".")>=0) {
	            	wkC = wkB.substring(0,wkB.lastIndexOf("."));
	            }
	            findSw = false;
	            if (wkDsnCd != null && wkDsnCd != "") {
	            	strQuery.setLength(0);
	            	strQuery.append("select count(*) cnt from cmr0020 a,cmm0036 b  \n");
					strQuery.append(" where a.cr_syscd=? and a.cr_dsncd=? \n");
					strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
					strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
					strQuery.append("   and a.cr_rsrcname=decode(substr(b.cm_info,27,1),'1',?,?) \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn,strQuery.toString());
		            pstmt.setString(1, etcData.get("syscd"));
		            pstmt.setString(2, wkDsnCd);
		            pstmt.setString(3, wkC);
		            pstmt.setString(4, wkB);
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
		            if ( rs.next() && rs.getInt("cnt") > 0 ) {
		            	findSw = true;
		            }
		            rs.close();
		            pstmt.close();
	            }
			}
            if ( !findSw ) {
            	rst = new HashMap<String, String>();
				rst.put("cm_dirpath", etcData.get("dirpath"));
				rst.put("dirpath", etcData.get("cm_dirpath"));
				rst.put("pcdir", etcData.get("pcdir"));
				rst.put("filename", wkB);
				rst.put("cm_dsncd", wkDsnCd);
            	rst.put("enable1", "1");
				rst.put("selected", "0");
				rst.put("error", "");

				//신한데이타시스템 네이밍룰 없음
//            	if (etcData.get("syscd").equals("00001") || etcData.get("syscd").equals("00002")) {
//            		Cmd0100 cmd0100 = new Cmd0100();
//            		rst = cmd0100.Program_NammingRule(rst);
//            		cmd0100 = null;
//            	}else{
            		rst.put("errmsg", "");
        			rst.put("rsrccd", "");
        			rst.put("jobcd", "");
//            	}

            } else rst = null;
			return rst;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen_thread.Master_Proc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen_thread.Master_Proc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_thread.Master_Proc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_thread.Master_Proc() Exception END ##");
			throw exception;
		}finally{
			if (rst != null) rst = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
}
