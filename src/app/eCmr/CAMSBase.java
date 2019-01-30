
/*****************************************************************************************
	1. program ID	: CAMSBase.java
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


public class CAMSBase{

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
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			//테스트요청대기건[체크아웃]
			strQuery.append("select 'T1' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c \n");
			strQuery.append(" where b.cr_qrycd='01'               \n");
			strQuery.append("   and b.cr_status ='9'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and a.CR_CONFNO is null           \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID     \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID       \n");
			strQuery.append("   and c.cr_status='5'               \n");
			strQuery.append("   and TESTSVR_YN(b.cr_syscd)='OK'   \n");
			//테스트요청대기건[신규]
			strQuery.append(" union                               \n");
			strQuery.append("select 'T0' cd,count(*) cnt          \n");
			strQuery.append("  from cmr0020 c,cmm0036 b           \n");
			strQuery.append(" where c.cr_status='3'               \n");
			strQuery.append("   and c.cr_lstver=0                 \n");
			strQuery.append("   and c.CR_EDITOR = ?               \n");
			strQuery.append("   and TESTSW(b.cm_syscd)=1          \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd         \n");
			strQuery.append("   and c.cr_rsrccd=b.cm_rsrccd       \n");
			strQuery.append("   and substr(b.cm_info,26,1)<>'1'   \n");
			strQuery.append("   and b.cm_rsrccd not in            \n");
			strQuery.append("       (select cm_samersrc from cmm0037 \n");
			strQuery.append("         where cm_syscd=b.cm_syscd   \n");
			strQuery.append("           and cm_rsrccd<>cm_samersrc) \n");
			//테스트요청대기건[테스트요청]
			strQuery.append(" union                               \n");
			strQuery.append("select 'T' || a.cr_qrycd cd,count(*) cnt \n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c \n");
			strQuery.append(" where b.cr_qrycd='03'               \n");
			strQuery.append("   and b.cr_status ='9'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and a.CR_CONFNO is null           \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID     \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID       \n");
			strQuery.append("   and c.cr_status='B'               \n");
			strQuery.append(" group by a.cr_qrycd                 \n");
			//체크인[운영]요청대기건[체크아웃]
			strQuery.append(" union                               \n");
			strQuery.append("select 'R1' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c \n");
			strQuery.append(" where b.cr_qrycd='01'               \n");
			strQuery.append("   and b.cr_status ='9'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and a.CR_CONFNO is null           \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID     \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID       \n");
			strQuery.append("   and c.cr_status='5'               \n");
			strQuery.append("   and TESTSVR_YN(b.cr_syscd)='NO'   \n");
			//체크인[운영]요청대기건[신규]
			strQuery.append(" union                               \n");
			strQuery.append("select 'R0' cd,count(*) cnt          \n");
			strQuery.append("  from cmr0020 c,cmm0036 b           \n");
			strQuery.append(" where c.cr_status='3'               \n");
			strQuery.append("   and c.cr_lstver=0                 \n");
			strQuery.append("   and c.CR_EDITOR = ?               \n");
			strQuery.append("   and TESTSVR_YN(b.cm_syscd)='NO'   \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd         \n");
			strQuery.append("   and c.cr_rsrccd=b.cm_rsrccd       \n");
			strQuery.append("   and substr(b.cm_info,26,1)<>'1'   \n");
			strQuery.append("   and b.cm_rsrccd not in            \n");
			strQuery.append("       (select cm_samersrc from cmm0037 \n");
			strQuery.append("         where cm_syscd=b.cm_syscd   \n");
			strQuery.append("           and cm_rsrccd<>cm_samersrc) \n");
			//체크인건수
			strQuery.append(" union                               \n");
			strQuery.append("select a.cr_qrycd cd,count(*) cnt    \n");
			strQuery.append("  from cmr1010 a,cmr1000 b           \n");
			strQuery.append(" where b.cr_qrycd in('03','04','06') \n");
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
			strQuery.append(" where b.cr_qrycd in('03','04','06') \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd='1'               \n");
			strQuery.append("   and a.cr_team in ('SYSCB','SYSED','SYSRC')\n");
			//체크인건 중 결재진행 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'CW' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where b.cr_qrycd in('03','04','06') \n");
			strQuery.append("   and b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	  \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and (a.cr_teamcd<>'1' or          \n");
			strQuery.append("        a.cr_team not in ('SYSCB','SYSED','SYSRC')) \n");
			//체크아웃건 중 오류대기 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'E01' cd,count(*) cnt         \n");
			strQuery.append("  from cmr9900 a,                    \n");
			strQuery.append("      (select y.cr_acptno            \n");
			strQuery.append("         from cmr1000 y,cmr1010 x    \n");
			strQuery.append("        where y.cr_qrycd in('01','02')\n");
			strQuery.append("          and y.cr_status ='0'       \n");
			strQuery.append("          and y.CR_EDITOR = ?        \n");
			strQuery.append("          and y.cr_acptno=x.cr_acptno\n");
			strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000'\n");
			strQuery.append("        group by y.cr_acptno) c      \n");
			strQuery.append(" where c.cr_acptno=a.cr_acptno       \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd='1'               \n");
			//체크인건 중 오류대기 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'ER' cd,count(*) cnt          \n");
			strQuery.append("  from cmr9900 a,                    \n");
			strQuery.append("      (select y.cr_acptno            \n");
			strQuery.append("         from cmr1000 y,cmr1010 x    \n");
			strQuery.append("        where y.cr_qrycd in('03','04','06')\n");
			strQuery.append("          and y.cr_status ='0'       \n");
			strQuery.append("          and y.CR_EDITOR = ?        \n");
			strQuery.append("          and y.cr_acptno=x.cr_acptno\n");
			strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000'\n");
			strQuery.append("        group by y.cr_acptno) c      \n");
			strQuery.append(" where c.cr_acptno=a.cr_acptno       \n");
			strQuery.append("   and a.CR_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd='1'               \n");
			//결재대상건1
			strQuery.append(" union                               \n");
			strQuery.append("select 'S1' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd in ('2','3','5','6','7','8','P') \n");
			strQuery.append("   and a.cr_team=?                   \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno 	  \n");
			strQuery.append("   and b.cr_status not in('3','9')   \n");
			//결재대상건2
			strQuery.append(" union                               \n");
			strQuery.append("select 'S2' cd,count(*) cnt          \n");
			strQuery.append("  from cmm0043 c,cmr1000 b,cmr9900 a \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd not in ('2','3','5','6','7','8','P') \n");
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
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			pstmt.setString(pstmtcount++, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
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

			rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;

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
			if (rtObj != null)	rtObj = null;
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

}
