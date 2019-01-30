
/*****************************************************************************************
	1. program ID	: eCAMSBase.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: BBS DAO
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class eCAMSBase{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getCmr0100(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			//체크아웃건수
			strQuery.append("select '01' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c \n");
			strQuery.append(" where b.cr_qrycd='01'               \n");
			strQuery.append("   and b.cr_status ='9'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_CONFNO is null           \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID     \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID       \n");
			strQuery.append("   and c.cr_status='5'               \n");
			//신규등록건수
			strQuery.append(" union                               \n");
			strQuery.append("select '00' cd,count(*) cnt          \n");
			strQuery.append("  from cmr0020 c,cmm0036 b           \n");
			strQuery.append(" where c.cr_status='3'               \n");
			strQuery.append("   and c.cr_lstver=0                 \n");
			strQuery.append("   and c.CR_EDITOR = ?               \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd         \n");
			strQuery.append("   and c.cr_rsrccd=b.cm_rsrccd       \n");
			strQuery.append("   and substr(b.cm_info,26,1)='0'    \n");
			strQuery.append("   and b.cm_rsrccd not in            \n");
			strQuery.append("       (select cm_samersrc from cmm0037 \n");
			strQuery.append("         where cm_syscd=b.cm_syscd   \n");
			strQuery.append("           and cm_rsrccd<>cm_samersrc) \n");
			//체크인건수
			strQuery.append(" union                               \n");
			strQuery.append("select a.cr_qrycd cd,count(*) cnt    \n");
			strQuery.append("  from cmr1010 a,cmr1000 b           \n");
			strQuery.append(" where b.cr_qrycd in('04','06')      \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_status='0'               \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID     \n");
			strQuery.append("  group by a.cr_qrycd                \n");
			//체크인건 중 배포대기건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'SW' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where b.cr_qrycd in('04','06')      \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd='1'               \n");
			strQuery.append("   and a.cr_team in ('SYSED','SYSRC')\n");
			//체크인건 중 결재진행 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'CW' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where b.cr_qrycd in('04','06')      \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and ((a.cr_teamcd='1' and         \n");
			strQuery.append("        a.cr_team not in ('SYSED','SYSRC')) \n");
			strQuery.append("        or a.cr_teamcd<>'1')         \n");
			//체크인건 중 오류대기 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'ER' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a,          \n");
			strQuery.append("      (select y.cr_acptno            \n");
			strQuery.append("         from cmr1000 y,cmr1010 x    \n");
			strQuery.append("        where y.cr_qrycd in('04','06')\n");
			strQuery.append("          and y.cr_status ='0'       \n");
			strQuery.append("          and y.CR_EDITOR = ?        \n");
			strQuery.append("          and y.cr_acptno=x.cr_acptno\n");
			strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000'\n");
			strQuery.append("        group by y.cr_acptno) c      \n");
			strQuery.append(" where b.cr_qrycd in('04','06')      \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd='1'               \n");
			strQuery.append("   and b.cr_acptno=c.cr_acptno       \n");
			//결재대상건1
			strQuery.append(" union                               \n");
			strQuery.append("select 'S1' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd in ('2','3','5','6','7','8') \n");
			strQuery.append("   and a.cr_team=?                   \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno 	  \n");
			strQuery.append("   and b.cr_status not in('3','9')   \n");
			//결재대상건2
			strQuery.append(" union                               \n");
			strQuery.append("select 'S2' cd,count(*) cnt          \n");
			strQuery.append("  from cmm0043 c,cmr1000 b,cmr9900 a \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd not in ('2','3','5','6','7','8') \n");
			strQuery.append("   and instr(a.cr_team,c.cm_rgtcd)>0 \n");
			strQuery.append("   and c.cm_userid=?           	  \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno 	  \n");
			strQuery.append("   and b.cr_status not in('3','9')   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("CD", rs.getString("cd"));
				rst.put("cnt", rs.getString("cnt"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## eCAMSBase.getCmr0100() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## eCAMSBase.getCmr0100() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## eCAMSBase.getCmr0100() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## eCAMSBase.getCmr0100() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## eCAMSBase.getCmr0100() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getSecuList(String Sv_UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("SELECT c.cm_filename   	          \n");
			strQuery.append("  FROM cmm0080 c,cmm0090 b,CMM0043 a \n");
			strQuery.append(" where a.cm_userid= ? 		          \n");  //Sv_UserID
			strQuery.append("   and a.cm_rgtcd=b.cm_rgtcd         \n");
			strQuery.append("   and b.cm_menucd=c.cm_menucd       \n");
			strQuery.append(" group by c.cm_filename              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, Sv_UserID);
			rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_filename", rs.getString("cm_filename"));
				rtList.add(rst);

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;


			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## UserInfo.getSecuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## UserInfo.getSecuList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getSecuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getSecuList() method statement
	public String getSysInfo_CkOut(String gbnName,String gbnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strMsg1     = "0";
		String            strMsg2     = "0";
		String            strMsg3     = "0";
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			if (gbnCd.equals("ACPT")) {
				//체크아웃 파일전송여부
				strQuery.append("select '01' cd,count(*) cnt          \n");
				strQuery.append("  from cmr1010 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_acptno=?                 \n");
				strQuery.append("   and a.cr_status<>'3'              \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,2,2)='10' 	  \n");
				//체크아웃 스크립크실행여부
				strQuery.append(" union                               \n");
				strQuery.append("select '02' cd,count(*) cnt          \n");
				strQuery.append("  from cmr1010 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_acptno=?                 \n");
				strQuery.append("   and a.cr_status<>'3'              \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,14,1)='1' 	  \n");
				//영향분석대상여부
				strQuery.append(" union                               \n");
				strQuery.append("select '03' cd,count(*) cnt          \n");
				strQuery.append("  from cmr1010 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_acptno=?                 \n");
				strQuery.append("   and a.cr_status<>'3'              \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,34,1)='1' 	  \n");
			} else if (gbnCd.equals("SYS")) {
				//체크아웃 파일전송여부
				strQuery.append("select '01' cd,count(*) cnt          \n");
				strQuery.append("  from cmm0036 b                     \n");
				strQuery.append(" where b.cm_syscd=?                  \n");
				strQuery.append("   and b.cm_closedt is null          \n");
				strQuery.append("   and substr(b.cm_info,2,2)='10' 	  \n");
				//체크아웃 스크립크실행여부
				strQuery.append(" union                               \n");
				strQuery.append("select '02' cd,count(*) cnt          \n");
				strQuery.append("  from cmm0036 b                     \n");
				strQuery.append(" where b.cm_syscd=?                  \n");
				strQuery.append("   and b.cm_closedt is null          \n");
				strQuery.append("   and substr(b.cm_info,14,1)='1' 	  \n");
				//영향분석대상여부
				strQuery.append(" union                               \n");
				strQuery.append("select '03' cd,count(*) cnt          \n");
				strQuery.append("  from cmm0036 b                     \n");
				strQuery.append(" where b.cm_syscd=?                  \n");
				strQuery.append("   and b.cm_closedt is null          \n");
				strQuery.append("   and substr(b.cm_info,34,1)='1' 	  \n");
			} else if (gbnCd.equals("ITEM")) {
				//체크아웃 파일전송여부
				strQuery.append("select '01' cd,count(*) cnt          \n");
				strQuery.append("  from cmr0020 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_itemid=?                 \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,2,2)='10' 	  \n");
				//체크아웃 스크립크실행여부
				strQuery.append(" union                               \n");
				strQuery.append("select '02' cd,count(*) cnt          \n");
				strQuery.append("  from cmr1010 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_itemid=?                 \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,14,1)='1' 	  \n");
				//영향분석대상여부
				strQuery.append(" union                               \n");
				strQuery.append("select '03' cd,count(*) cnt          \n");
				strQuery.append("  from cmr1010 a,cmm0036 b           \n");
				strQuery.append(" where a.cr_itemid=?                 \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd         \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd       \n");
				strQuery.append("   and substr(b.cm_info,34,1)='1' 	  \n");
			}

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(pstmtcount++, gbnName);
			pstmt.setString(pstmtcount++, gbnName);
			pstmt.setString(pstmtcount++, gbnName);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (rs.getString("CD").equals("01")) {
					if (rs.getInt("cnt")>0) strMsg1 = "1";
				} else if (rs.getString("CD").equals("02")) {
					if (rs.getInt("cnt")>0) strMsg2 = "1";
				} else if (rs.getString("CD").equals("03")){
					if (rs.getInt("cnt")>0) strMsg3 = "1";
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			conn = null;
			pstmt = null;
			rs = null;

			return strMsg1 + strMsg2 + strMsg3;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## eCAMSBase.getSysInfo_CkOut() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## eCAMSBase.getSysInfo_CkOut() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## eCAMSBase.getSysInfo_CkOut() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## eCAMSBase.getSysInfo_CkOut() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## eCAMSBase.getSysInfo_CkOut() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
