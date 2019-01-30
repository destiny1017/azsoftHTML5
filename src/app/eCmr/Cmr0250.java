/*****************************************************************************************
	1. program ID	: Cmr0250.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 2009. 05. 20
	5. auth		    : no name
	6. description	: [체크인신청상세]
*****************************************************************************************/

package app.eCmr;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

//import app.common.LoggableStatement;
import app.common.LoggableStatement;
import app.common.UserInfo;
import app.common.SystemPath;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0250{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getReqList(String UserId,String AcptNo) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		//Object[] 		  returnObject = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') acptdate,     \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') prcdate,       \n");
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_sayu,a.cr_status,    \n");
			strQuery.append("       a.cr_qrycd,a.cr_passsub,a.cr_sayucd,a.cr_gyuljae,            \n");
			strQuery.append("       a.cr_svrcd,a.cr_svrseq,c.cc_srid,c.cc_reqtitle,              \n");
			strQuery.append("       to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,           \n");
			strQuery.append("       a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,a.cr_prcreq,        \n");
			strQuery.append("       a.cr_eclipse,nvl(a.cr_svryn,'Y') cr_svryn,                   \n");
			strQuery.append("       nvl(a.cr_version,'Y') cr_version,                            \n");
			strQuery.append("       a.cr_passok,b.cm_sysmsg,b.cm_sysinfo,d.cm_username,          \n");
			strQuery.append("       (select cm_deptname from cmm0100 where cm_deptcd =           \n");
			strQuery.append("        d.cm_project) as cm_dept,CR_PASSCD,a.cr_prctime,a.cr_dept,  \n");
			strQuery.append("        a.cr_reqdate,a.cr_reqdoc,a.cr_reqtitle,a.cr_etc,a.CR_EMRGB,a.CR_EMRCD, h.cm_codename as reqName, a.cr_closeyn	\n");
			//strQuery.append("  from cmm0020 g,cmm0020 f,cmm0040 d,cmm0030 b,cmr1000 a            \n");
			strQuery.append("  from cmc0100 c,cmm0020 h,cmm0020 g,cmm0040 d,cmm0030 b,cmr1000 a  \n");
			strQuery.append(" where a.cr_acptno=?                                                \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=d.cm_userid            \n");
			//strQuery.append("   and f.cm_macode='REQCD' and f.cm_micode=a.cr_emgcd              \n");
			strQuery.append("   and g.cm_macode='REQPASS' and g.cm_micode=a.cr_passok            \n");
			strQuery.append("   and h.cm_macode='REQUEST' and h.cm_micode=a.cr_qrycd           	 \n");
			strQuery.append("   and a.cr_itsmid=c.cc_srid(+)                       	             \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptdate",rs.getString("acptdate"));
				if (rs.getString("prcdate") != null){
					rst.put("prcdate",rs.getString("prcdate"));
				}
				else if (rs.getString("cr_gyuljae").equals("1")){
					rst.put("prcdate","우선적용 중");
				}
				rst.put("cr_dept", rs.getString("CR_DEPT"));
				rst.put("cr_reqdate", rs.getString("CR_REQDATE"));
				rst.put("cr_reqdoc", rs.getString("CR_REQDOC"));
				rst.put("cr_rettitle", rs.getString("CR_REQTITLE"));
				rst.put("cr_etc", rs.getString("CR_ETC"));
				rst.put("cr_passcd", rs.getString("CR_PASSCD"));
				rst.put("cr_sayu", rs.getString("cr_sayu"));
				rst.put("cr_gyuljae",rs.getString("cr_gyuljae"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username")+"["+rs.getString("cm_dept")+"]");
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_passok",rs.getString("cr_passok"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_sayucd", rs.getString("cr_sayucd"));
				if( !rs.getString("cr_qrycd").equals("07") && rs.getString("cr_closeyn").equals("Y") ) {
					rst.put("reqName",rs.getString("reqName")+"(폐기)");
				}else {
					rst.put("reqName",rs.getString("reqName"));
				}
				
				if (rs.getString("cr_prcreq") != null )rst.put("aplydate", rs.getString("cr_prcreq"));
				else rst.put("aplydate", "");

				//신청구분  ITSM 정보
				if(rs.getString("cc_srid")!=null && rs.getString("cc_srid")!=""){
					rst.put("cc_srid", rs.getString("cc_srid"));
					rst.put("cc_reqtitle", rs.getString("cc_reqtitle"));
				}
				rst.put("befsw", "N");
				//rst.put("reqpass",rs.getString("reqpass"));
				rst.put("srcview", "Y");
				//rst.put("reqgbn", rs.getString("reqgbn"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				if (rs.getString("cr_qrycd").equals("01") || rs.getString("cr_qrycd").equals("02") || rs.getString("cr_qrycd").equals("07")) {
					if (rs.getString("cr_eclipse").equals("R")) rst.put("etcdata", "Remote");
					else if (rs.getString("cr_eclipse").equals("E")) rst.put("etcdata", "eclipse");
					else rst.put("etcdata", "Local");
					if (rs.getString("cr_qrycd").equals("07")) {
						if (rs.getString("cr_svryn").equals("N")) {
							rst.put("etcdata", rst.get("etcdata")+" / 서버미적용");							
						}
						if (rs.getString("cr_version").equals("N")) {
							rst.put("etcdata", rst.get("etcdata")+" / 버전업미적용");							
						}
					}
				}
				rst.put("befjob","0");
				if (rs.getString("cr_sayu") != null)
				   rst.put("cr_sayu",rs.getString("cr_sayu"));
				if (rs.getString("cr_passsub") != null && rs.getString("cr_passsub") !="" && rs.getString("cr_passok") != null && rs.getString("cr_passok") != ""){
					if (rs.getString("cr_passsub").equals("1") || rs.getString("cr_passok").equals("2")) {
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020            \n");
						//strQuery.append(" where cm_macode='PASSCD' and cm_micode=?  \n");
						strQuery.append(" where cm_macode='REQSAYU' and cm_micode=?  \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1,rs.getString("cr_sayucd"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs.getString("cr_passsub").equals("1"))
								rst.put("passsub","긴급-업무후["+rs2.getString("cm_codename")+"]");
							else
								rst.put("passsub","긴급["+rs2.getString("cm_codename")+"]");
						}
						rs2.close();
						pstmt2.close();
					}
				}
				else{
					rst.put("passsub", "정상");
				}
				if (rs.getString("cr_qrycd").equals("16")) {
					strQuery.setLength(0);
					strQuery.append("select cm_svrname from cmm0031    \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
					strQuery.append(" and cm_seqno=?                   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_syscd"));
					pstmt2.setString(2, rs.getString("cr_svrcd"));
					pstmt2.setInt(3, rs.getInt("cr_svrseq"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("web", rs2.getString("cm_svrname"));
					}
					rs2.close();
					pstmt2.close();
				}

				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmr9900                   \n");
				strQuery.append(" where cr_acptno=? and cr_locat<>'00'              \n");
				strQuery.append("   and cr_teamcd not in('1','2')                   \n");
				strQuery.append("   and (cr_team=? or cr_confusr=?)                 \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, AcptNo);
				pstmt2.setString(2, UserId);
				pstmt2.setString(3, UserId);
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					if (rs2.getInt("cnt")>0) rst.put("confsw", "1");
					else rst.put("confsw", "0");
				}
				rs2.close();
				pstmt2.close();

				rst.put("qacheck", "N");				
				rst.put("qaUserCheck", "N");

				if (rs.getString("cr_status").equals("9")) { //|| rs.getString("cr_status").equals("8")) {
					rst.put("confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
					
					strQuery.setLength(0);
					strQuery.append("select cr_conmsg from cmr9900       					\n");
					strQuery.append(" where cr_locat in ( select substr(cr_confusr,0,2)  	\n");
					strQuery.append("  from cmr9900 where cr_acptno=? and cr_locat='00' )   \n");
					strQuery.append(" and cr_acptno=? and cr_status='3'  					\n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		        	pstmt2.setString(1, AcptNo);
		        	pstmt2.setString(2, AcptNo);
		        	rs2 = pstmt2.executeQuery();
		        	if (rs2.next()) {
		        		rst.put("cr_conmsg",rs2.getString("cr_conmsg"));
		        	}
		        	rs2.close();
		        	pstmt2.close();
				} else {
					String strPrcSw = "0";
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
						strPrcSw = "0";
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmr1030       \n");
						strQuery.append(" where cr_befact=? and cr_autolink='N' \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
			        	pstmt2.setString(1, AcptNo);
			        	rs2 = pstmt2.executeQuery();
			        	if (rs2.next()) {
			        		if (rs2.getInt("cnt")>0) rst.put("befsw", "Y");
			        	}
			        	rs2.close();
			        	pstmt2.close();
                        /*
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmr9900                   \n");
						strQuery.append(" where cr_acptno=? and cr_locat<>'00'              \n");
						strQuery.append("   and cr_teamcd='1' and cr_team='SYSUP'           \n");
						strQuery.append("   and cr_status='9'                               \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, AcptNo);
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getInt("cnt")==0) rst.put("srcview", "N");
						}
						rs2.close();
						pstmt2.close();*/
					}
					rst.put("endsw", "0");

					//ecamsLogger.debug("++++++++++RetryYn++++++++"+rs.getString("cncldat"));
					ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();

					Cmr0150 cmr0150 = new Cmr0150();
					rsconf = cmr0150.confLocat_conn(AcptNo,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"),conn);
					cmr0150 = null;

					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					if(rs.getString("cr_prctime") != null && rs.getString("cr_prctime") != ""){
						rst.put("confname","[정기적용("+rs.getString("cr_prctime").substring(0,2)+":"+rs.getString("cr_prctime").substring(2)+")]"+rsconf.get(0).get("confname"));
					}else{
						rst.put("confname", rsconf.get(0).get("confname"));
					}
					rst.put("updtsw1", rsconf.get(0).get("updtsw1"));
					rst.put("updtsw2", rsconf.get(0).get("updtsw2"));
					rst.put("updtsw3", rsconf.get(0).get("updtsw3"));
					rst.put("errtry", rsconf.get(0).get("errtry"));
					rst.put("log", rsconf.get(0).get("log"));
					rst.put("sttry", rsconf.get(0).get("sttry"));
					rst.put("ermsg", rsconf.get(0).get("ermsg"));

					rsconf.clear();
					rsconf = null;
				}

				if(rs.getString("CR_EMRGB").equals("1")){
					strQuery.setLength(0);
					strQuery.append("SELECT CM_CODENAME FROM CMM0020               	\n");
					strQuery.append(" WHERE CM_MACODE = 'EMERGENCY'               	\n");
					strQuery.append(" AND CM_MICODE =  ?               				\n");
			        pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("CR_EMRCD"));
			        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			        rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("emrcd", rs2.getString("CM_CODENAME"));
					}else{
						rst.put("emrcd", "");
					}
					rs2.close();
					pstmt2.close();

				}else{
					rst.put("emrcd", "");
				}

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1001               \n");
				strQuery.append(" where cr_acptno=? and cr_gubun='1'               \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("file","1");
					}
					else{
						rst.put("file","0");
					}
				}
				rs2.close();
				pstmt2.close();

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1030 \n");
				strQuery.append(" where cr_acptno=? or cr_befact=?   \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
				pstmt2.setString(2, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					rst.put("befjob","0");
					if (rs2.getInt("cnt") > 0){
						rst.put("befjob","1");
					}
				}
				rs2.close();
				pstmt2.close();
				rsval.add(rst);
				rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public Object[] getProgList(String UserId,String AcptNo,String chkYn,boolean qrySw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] 		  returnObject = null;
		boolean           findSw      = false;
		boolean           errSw       = false;
		String            strPutCd    = "";
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			UserInfo         userinfo = new UserInfo();
			String strTeam = userinfo.getUserInfo_sub(conn, UserId, "CM_PROJECT");
			boolean adminSw = userinfo.isAdmin_conn(UserId, conn);
			userinfo = null;

			//ecamsLogger.error("++++proglist start++++++++++");
			strQuery.setLength(0);
			strQuery.append("select a.cr_rsrcname,a.cr_serno,a.cr_putcode,a.cr_rsrccd,           \n");
			strQuery.append("       a.cr_status,a.cr_confno,a.cr_itemid,a.cr_editcon, 	         \n");
			strQuery.append("       a.cr_baseno,a.cr_baseitem,a.cr_dsncd,                        \n");
			strQuery.append("       (select cr_story from cmr0020 where cr_itemid =a.cr_itemid) cr_story, \n");
// 2015.10.07 하나카드
//			strQuery.append("       (select cr_lstver from cmr0020 where cr_itemid =a.cr_itemid) reqver , \n");
			strQuery.append("       a.cr_aplydate,a.cr_prcdate,a.cr_priority,a.cr_version,a.cr_jobcd,\n");
			strQuery.append("       a.cr_qrycd,b.cm_dirpath,c.cm_codename,e.cm_jobname,d.cm_info,\n");
			strQuery.append("       f.cm_codename checkin,a.cr_editor,g.cr_teamcd,g.cr_syscd,    \n");
			strQuery.append("       g.cr_jobcd,nvl(x.cnt,0) rst,nvl(y.cnt,0) err,                \n");
			strQuery.append("       nvl(z.cnt,0) basecnt,nvl(u.cnt,0) baserst,a.cr_befver        \n");
			strQuery.append("  from cmr1000 g,cmm0020 f,cmm0102 e,cmm0036 d,cmm0020 c,cmm0070 b,cmr1010 a \n");
			strQuery.append(" ,(select cr_serno,count(*) cnt from cmr1011						 \n");
			strQuery.append("    where cr_acptno=? and cr_prcdate is not null	                 \n");
			strQuery.append("    group by cr_serno) x						                     \n");
			strQuery.append(" ,(select a.cr_serno,count(*) cnt from cmr1011 a,cmr1010 b 	     \n");
			strQuery.append("    where a.cr_acptno=? and nvl(a.cr_prcrst,'0000')<>'0000'	     \n");
			strQuery.append("      and a.cr_acptno=b.cr_acptno and a.cr_serno=b.cr_serno         \n");
			strQuery.append("      and b.cr_prcdate is null                                      \n");
			strQuery.append("    group by a.cr_serno) y						                     \n");
			strQuery.append(" ,(select b.cr_baseitem,count(*) cnt from cmr1011 a,cmr1010 b		 \n");
			strQuery.append("    where a.cr_acptno=? and nvl(a.cr_prcrst,'0000')<>'0000'         \n");
			strQuery.append("      and a.cr_acptno=b.cr_acptno and a.cr_serno=b.cr_serno         \n");
			strQuery.append("      and b.cr_prcdate is null                                      \n");
			strQuery.append("    group by b.cr_baseitem) z		   		                         \n");
			strQuery.append(" ,(select b.cr_baseitem,count(*) cnt from cmr1011 a,cmr1010 b		 \n");
			strQuery.append("    where a.cr_acptno=? and a.cr_prcdate is not null                \n");
			strQuery.append("      and a.cr_acptno=b.cr_acptno and a.cr_serno=b.cr_serno         \n");
			strQuery.append("    group by b.cr_baseitem) u		   		                         \n");
			strQuery.append(" where a.cr_acptno=? and a.cr_acptno=g.cr_acptno					 \n");
			if (qrySw == false) {
				strQuery.append("and a.cr_itemid=a.cr_baseitem                                   \n");
			}
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd              \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd              \n");
			strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=a.cr_qrycd             \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd            \n");
			strQuery.append("   and a.cr_jobcd=e.cm_jobcd                 					     \n");
	        strQuery.append("   and a.cr_serno=x.cr_serno(+) and a.cr_serno=y.cr_serno(+)        \n");
	        strQuery.append("   and a.cr_baseitem=z.cr_baseitem(+)                               \n");
	        strQuery.append("   and a.cr_baseitem=u.cr_baseitem(+)                               \n");
	        //strQuery.append(" order by a.cr_serno                                                \n");
	        strQuery.append(" order by a.cr_baseitem, a.cr_itemid                                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, AcptNo);
			pstmt.setString(3, AcptNo);
			pstmt.setString(4, AcptNo);
			pstmt.setString(5, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        String tmpDate = "";
			while (rs.next()){
				findSw = true;
				errSw = false;
				rst = new HashMap<String, String>();
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("cr_befver",rs.getString("cr_befver"));
				rst.put("cr_version", rs.getString("cr_version"));
				if (rs.getString("cr_status").equals("3")){
					rst.put("checkin",rs.getString("checkin")+"[반송]");
				}
				else{
					rst.put("checkin", rs.getString("checkin"));
				}
				if (rs.getString("cr_itemid") != null && rs.getString("cr_itemid") != "") {
					if (rs.getString("cr_itemid").equals(rs.getString("cr_baseitem"))) {
						rst.put("sortfg", rs.getString("cr_baseitem")+"0");
					} else {
						rst.put("sortfg",rs.getString("cr_baseitem")+"2");
					}
				} else {
					rst.put("sortfg", rs.getString("cr_baseitem")+"1");
				}
				if (rs.getString("cr_confno") != null) {
					if (rs.getString("cr_confno").substring(4,6).equals("01") || rs.getString("cr_confno").substring(4,6).equals("02")){
						rst.put("cr_confno",rs.getString("cr_confno"));
					} else {
						tmpDate = "";
						if (AcptNo.substring(4,6).equals("04")){//체크인(운영)상세 일 경우)
							strQuery.setLength(0);
							strQuery.append("select to_char(cr_prcdate,'yyyy/mm/dd hh24:mi') testprcdate  \n");
							strQuery.append("  from cmr1000 \n");
							strQuery.append(" where cr_acptno = ? \n");
					        pstmt2 = conn.prepareStatement(strQuery.toString());
					        //pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt2.setString(1, rs.getString("cr_confno"));
							//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					        rs2 = pstmt2.executeQuery();
							if(rs2.next()){
								tmpDate = rs2.getString("testprcdate");
							}
							rs2.close();
							pstmt2.close();
						}
						rst.put("testprcdate", tmpDate);
					}
				}
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_baseitem",rs.getString("cr_baseitem"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_confno", rs.getString("cr_confno"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("priority", rs.getString("cr_priority"));
				rst.put("cr_version",Integer.toString(rs.getInt("cr_version")));
				if (rs.getString("cr_aplydate") != null){
					rst.put("cr_aplydate", rs.getString("cr_aplydate"));
				}
				rst.put("check", "false");
				rst.put("visible", "false");

				if (rs.getString("cr_prcdate") != null){
					rst.put("cr_prcdate", rs.getString("cr_prcdate"));
				} else if (rs.getString("cr_itemid") != null && !AcptNo.substring(4,6).equals("04") && !AcptNo.substring(4,6).equals("06")) {
					if(rs.getString("cr_baseitem") != null && rs.getString("cr_baseitem") != ""){
						if(rs.getString("cr_baseitem").equals(rs.getString("cr_itemid"))) {
							if (UserId.equals(rs.getString("cr_editor")) || adminSw) {
								rst.put("visible", "true");
								rst.put("check", "true");
							}
						}
					}
				}
				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				if (rs.getString("cr_putcode") != null){
					if (rs.getString("cr_putcode") != null) {
					   if (!rs.getString("cr_putcode").equals("0000")) {
						   rst.put("cr_putcode",rs.getString("cr_putcode"));
					   } else if (strPutCd != "") rst.put("cr_putcode",strPutCd);
					   else rst.put("cr_putcode",rs.getString("cr_putcode"));
					} else rst.put("cr_putcode",strPutCd);
				}

				if (rs.getString("cr_editor").equals(UserId)) rst.put("secusw", "Y");
				else if (rs.getString("cr_teamcd").equals(strTeam)) rst.put("secusw", "Y");

				if (!rs.getString("cr_status").equals("3") && rs.getString("cr_baseitem") != null) {
					if (rs.getString("basecnt") != null) {
						if (rs.getInt("basecnt")>0) errSw = true;
					}
				}

				if (rs.getString("cr_editcon") != null){
					rst.put("cr_editcon",rs.getString("cr_editcon"));
				}
				if (!rs.getString("cr_status").equals("3")) {
					if (rs.getString("cr_putcode") != null && !rs.getString("cr_putcode").equals("0000") && !rs.getString("cr_putcode").equals("RTRY")){
						rst.put("ColorSw","5");
					} else if (errSw) rst.put("ColorSw","5");
					else{
						if (rs.getString("cr_status").equals("8") || rs.getString("cr_status").equals("9"))
							rst.put("ColorSw","9");
						else rst.put("ColorSw","0");
					}
					if (rs.getString("cr_status").equals("8") || rs.getString("cr_status").equals("9")) {
						rst.put("rst", "Y");
					}
					if (rs.getString("cr_putcode") == null && rs.getString("cr_status").equals("0") && rs.getString("cr_itemid") != null) {
						strQuery.setLength(0);
						if (rs.getString("cr_itemid").equals(rs.getString("cr_baseitem"))) {
							if (rs.getString("baserst") != null) {
								if (rs.getInt("baserst") > 0) rst.put("rst", "Y");
								else rst.put("rst", "N");
							} else {
								rst.put("rst", "N");
							}
						} else {
							if (rs.getString("rst") != null) {
								if (rs.getInt("rst") > 0) rst.put("rst", "Y");
								else rst.put("rst", "N");
							} else {
								rst.put("rst", "N");
							}
						}
					} else rst.put("rst", "Y");
				} else rst.put("ColorSw","3");


		        if (findSw == true){
		        	rsval.add(rst);
		        	rst = null;
		        }
// 2015.10.07 하나카드 		        
//		        if ( rs.getString("reqver").equals("0")) {
//		        	rst.put("reqver", "신규");
//		        } else {
//		        	rst.put("reqver", "수정");
//		        }
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
	        rs2 = null;
	        pstmt2 = null;
			conn = null;
			int j = 0;
			for (int i=0;rsval.size()>i;i++) {
				if (rsval.get(i).get("cr_itemid") != null && rsval.get(i).get("cr_baseitem") != null) {
					if (!rsval.get(i).get("cr_itemid").equals(rsval.get(i).get("cr_baseitem"))) {
						for (j=0;rsval.size()>j;j++) {
							if (rsval.get(j).get("cr_itemid") != null && rsval.get(j).get("cr_baseitem") != null) {
								if (rsval.get(j).get("cr_itemid").equals(rsval.get(i).get("cr_baseitem"))) {
									if (!rsval.get(j).get("cr_itemid").equals(rsval.get(j).get("cr_baseitem"))) {
										rst = new HashMap<String, String>();
										rst = rsval.get(i);
										rst.put("sortfg", rsval.get(j).get("cr_baseitem")+"2");
										rsval.set(i, rst);
									}
									break;
								}
							}
						}
					}
				}
			}
			//ecamsLogger.error("++++proglist end++++++++++"+rsval.toString());
			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (returnObject != null) returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

	public Object[] getRstList(String UserId,String AcptNo,String prcSys) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] 		  returnObject = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			int			parmCnt	 = 0;
			UserInfo	userinfo = new UserInfo();
			boolean 	adminYn  = userinfo.isAdmin_conn(UserId, conn);
			userinfo = null;

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_serno,a.cr_rsrcname,a.cr_jobcd,      \n");
			strQuery.append("       a.cr_rsrccd,a.cr_aplydate,a.cr_sysstep,b.cr_ipaddr,b.cr_seqno,   \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,b.cr_svrseq,  \n");
			strQuery.append("       b.cr_prcrst,b.cr_prcdate,b.cr_prcsys,b.cr_putcode,b.cr_rstfile,  \n");
			strQuery.append("       b.cr_svrcd,c.cm_codename jawon,b.cr_svrname,b.cr_portno,         \n");
			strQuery.append("       b.cr_wait,d.cm_dirpath,e.cm_codename SYSGBN,f.cm_info,b.cr_rungbn\n");
			strQuery.append("  from cmm0036 f,cmm0020 c,cmm0020 e,cmm0070 d, cmr1011 b,cmr1010 a     \n");
			strQuery.append(" where a.cr_acptno=?                                                    \n");

			strQuery.append("   and a.cr_acptno=b.cr_acptno and a.cr_serno=b.cr_serno                \n");
			strQuery.append("   and (nvl(b.cr_putcode,'NONE')<>'0000' or b.cr_rstfile is not null)   \n");
			if (prcSys != "" && prcSys != "") {
				strQuery.append("and b.cr_prcsys=?                                                   \n");
			}
			if (!adminYn && AcptNo.substring(4,6).equals("04")){
				strQuery.append(" and b.cr_svrcd<>'90'                             \n");
			}
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_dsncd=d.cm_dsncd                  \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd                  \n");
			strQuery.append("   and e.cm_macode='SYSGBN' and e.cm_micode=b.cr_prcsys                 \n");
			strQuery.append("   and a.cr_syscd=f.cm_syscd and a.cr_rsrccd=f.cm_rsrccd                \n");
			strQuery.append("union                                                                   \n");
			strQuery.append("select a.cr_acptno,a.cr_syscd,b.cr_serno,'파일송수신결과' cr_rsrcname,'00' cr_jobcd, \n");
			strQuery.append("       '00' cr_rsrccd,'' cr_aplydate,0 cr_sysstep,b.cr_ipaddr,b.cr_seqno, \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,b.cr_svrseq,  \n");
			strQuery.append("       b.cr_prcrst,b.cr_prcdate,b.cr_prcsys,b.cr_putcode,               \n");
			strQuery.append("       b.cr_rstfile,b.cr_svrcd,'' jawon,b.cr_svrname,b.cr_portno,       \n");
			strQuery.append("       b.cr_wait,'' cm_dirpath,e.cm_codename SYSGBN,'' cm_info,b.cr_rungbn \n");
			strQuery.append("  from cmm0020 e,cmr1011 b,cmr1000 a                                    \n");
			strQuery.append(" where a.cr_acptno=?                                                    \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno and b.cr_serno=0                         \n");
			strQuery.append("   and (nvl(b.cr_putcode,'NONE')<>'0000' or b.cr_rstfile is not null)   \n");
			if (prcSys != "" && prcSys != "") {
				strQuery.append("and b.cr_prcsys=?                                                   \n");
			}
			if (!adminYn){
				strQuery.append(" and b.cr_svrcd<>'90'                                               \n");
			}
			strQuery.append("   and e.cm_macode='SYSGBN' and e.cm_micode=b.cr_prcsys                 \n");
			strQuery.append(" order by cr_prcdate,cr_prcsys,cr_seqno,cr_ipaddr                       \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
			if (prcSys != "" && prcSys != ""){
				pstmt.setString(++parmCnt, prcSys);
			}
			pstmt.setString(++parmCnt, AcptNo);
			if (prcSys != "" && prcSys != ""){
				if (!prcSys.equals("SYSCB") && !prcSys.equals("SYSAC")) pstmt.setString(++parmCnt, prcSys);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				if (rs.getString("cr_serno").equals("0")) {
					rst.put("prcsys",rs.getString("SYSGBN"));
				} else if (rs.getString("cr_rungbn") == null){
					rst.put("prcsys", "cr_rungbn=null[err]");
				} else{
					if (rs.getString("cr_rungbn").equals("B")) {
						rst.put("prcsys",rs.getString("SYSGBN")+"[전]");
					} else {
						rst.put("prcsys",rs.getString("SYSGBN")+"[후]");
					}
				}
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cr_seqno",rs.getString("cr_seqno"));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("cr_prcsys", rs.getString("cr_prcsys"));
				if (!rs.getString("cr_rsrccd").equals("00")) {
					rst.put("cm_dirpath", chgVolPath(rs.getString("cr_syscd"),rs.getString("cm_dirpath"),rs.getString("cr_svrcd"),rs.getString("cr_rsrccd"),rs.getInt("cr_svrseq"),rs.getString("cr_jobcd"),conn));
				}
				rst.put("cr_svrname",rs.getString("cr_svrname"));
				if (rs.getString("cr_putcode") != null) {
					PreparedStatement pstmt2       = null;
					ResultSet         rs2         = null;

					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020             \n");
					strQuery.append(" where cm_macode='ERRACCT' and cm_micode=?  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,rs.getString("cr_putcode"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("prcrst", rs2.getString("cm_codename"));
					}
					else{
						rst.put("prcrst", rs.getString("cr_putcode"));
					}
					rs2.close();
					pstmt2.close();
					rs2 = null;
					pstmt2 = null;
				}

				if (rs.getString("cr_svrname") != null){
					rst.put("cr_svrname", rs.getString("cr_svrname"));
				}
				if (rs.getString("cr_prcdate") != null){
					rst.put("cr_prcdate", rs.getString("cr_prcdate"));
				}
				if (rs.getString("prcdate") != null){
					rst.put("prcdate", rs.getString("prcdate"));
				}
				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				if (rs.getString("cr_putcode") != null){
					rst.put("cr_putcode",rs.getString("cr_putcode"));
				}
				if (rs.getString("cr_prcdate") != null) {
					if (rs.getString("cr_prcrst") != null && !rs.getString("cr_prcrst").equals("0000")){
						rst.put("ColorSw","5");
					} else rst.put("ColorSw","9");
				}
				else{
					if (rs.getString("cr_prcrst") != null && !rs.getString("cr_prcrst").equals("0000")){
						rst.put("ColorSw","5");
					} else rst.put("ColorSw","0");
				}

		        rsval.add(rst);
		        rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getRstList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.getRstList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getRstList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.getRstList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (returnObject !=  null)	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();conn=null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getRstList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

	/**  TP 서버 리스트 카운트
	 * @param AcptNo : 체크인신청번호
	 * @return int
	 * @throws SQLException
	 * @throws Exception
	 */
	public int getTPSCount(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			int returnCnt = 0;
			conn = connectionContext.getConnection();

        	strQuery.setLength(0);
        	strQuery.append("select count(*) cnt  from cmr1010 \n");
            strQuery.append(" where cr_acptno = ? \n");
            strQuery.append("   and cr_status <> '3' \n");
            strQuery.append("   and cr_rsrccd ='69' \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, AcptNo);
            rs = pstmt.executeQuery();
            if (rs.next()){
            	returnCnt = rs.getInt("cnt");
            }
            pstmt.close();
            rs.close();
            conn.close();
            rs = null;
			pstmt = null;
			conn = null;

            return returnCnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getTPSCount() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.getTPSCount() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.getTPSCount() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getTPSCount() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.getTPSCount() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.getTPSCount() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null) rs = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getTPSCount() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTPSCount() method statement

	/** 재처리 작업
	 * @param AcptNo
	 * @param prcCd
	 * @param prcSys
	 * @param UserId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String svrProc(String AcptNo,String prcCd,String prcSys,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            wkPrcSys    = "";
		boolean           endSw       = false;
		String            wkRunGbn    = "";
		Runtime  run = null;
		Process p = null;


		ConnectionContext connectionContext = new ConnectionResource();

		try {

			String  binpath;
			String[] chkAry;
			int	cmdrtn;
			SystemPath systemPath = new SystemPath();
			binpath = systemPath.getTmpDir("15");
			systemPath = null;

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;

			p = run.exec(chkAry);
			p.waitFor();
			ecamsLogger.error("chkAry : "+chkAry[0] + " " +chkAry[1] + " " +chkAry[2]);

			cmdrtn = p.exitValue();
			ecamsLogger.error("cmdrtn : "+cmdrtn);
			chkAry = null;
			p = null;
			run = null;

			if (cmdrtn > 0) {
				return "2";
			}
//
//			shFileName = tmpPath + "/" + UserID+ ItemID1 +"_cmpsrc.sh";
//			fileName = UserID+ ItemID1;
//
//			shfile = new File(shFileName);
//
//			if( !(shfile.isFile()) )              //File이 없으면
//			{
//				shfile.createNewFile();          //File 생성
//			}
//
//
//			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
//			writer.write("cd "+strBinPath +"\n");
//			writer.write("./ecams_cmpsrc " + ItemID1 + " " + ver1 +" " + ItemID2 + " " +ver2 + " " + fileName +"\n");
//			writer.write("exit $?\n");
//			writer.close();
//
//
//			strAry = new String[3];
//			strAry[0] = "chmod";
//			strAry[1] = "777";
//			strAry[2] = shFileName;
//
//			run = Runtime.getRuntime();
//
//			p = run.exec(strAry);
//			p.waitFor();
//
//
//
//			run = Runtime.getRuntime();
//
//			strAry = new String[2];
//
//			strAry[0] = "/bin/sh";
//			strAry[1] = shFileName;
//
//			p = run.exec(strAry);
//			p.waitFor();
//
//			if (p.exitValue() != 0) {
//				shfile.delete();
//				throw new Exception("해당 소스 생성  실패. run=["+shFileName +"]" + " return=[" + p.exitValue() + "]" );
//			}
//			else{
//				//shfile.delete();
//			}

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			wkPrcSys = prcSys;
			wkRunGbn = "B";

//			if (prcSys.equals("SYSGB")) {
//				wkPrcSys = "SYSUA,SYSGB";
//			}
			//SYSUA 있으면 SYSUA 넣고 없으면 , SYSCB,SYSGB 로 셋팅
			wkPrcSys = prcSys;
			if (prcCd.equals("Retry")) {
				if (prcSys.equals("SYSUA")) {
					wkPrcSys = "SYSUA,SYSGB";
				} else if (prcSys.equals("SYSGB")) {
					strQuery.setLength(0);
					strQuery.append("select count(*) as cnt \n");
			        strQuery.append("  from cmr9900         \n");
			        strQuery.append(" where cr_acptno=?     \n");
			        strQuery.append("   and cr_team='SYSUA' \n");
			        pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, AcptNo);
					rs = pstmt.executeQuery();
					if (rs.next() && rs.getInt("cnt") > 0) {
						wkPrcSys = "SYSUA,SYSGB";
					} else {
						wkPrcSys = "SYSCB,SYSGB";
					}
					rs.close();
					pstmt.close();
				} else {
					wkPrcSys = prcSys;
				}
			}
			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt                                      \n");
	        strQuery.append("  from cmr1011 a,cmr1010 b                                  \n");
	        strQuery.append(" where b.cr_acptno=? and b.cr_prcdate is null               \n");
	        strQuery.append("   and b.cr_acptno=a.cr_acptno                              \n");
	        strQuery.append("   and b.cr_serno=a.cr_serno                                \n");
	        strQuery.append("   and a.cr_prcsys=?                                        \n");
	        strQuery.append("   and nvl(a.cr_putcode,'NONE')<>'0000'                     \n");
	        strQuery.append("   and nvl(a.cr_rungbn,'B')<>'B'                            \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, prcSys);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt("cnt") > 0) {
					wkRunGbn = "A";
				}
			}
			rs.close();
			pstmt.close();

			//Retry:전체재처리, Errtry:오류건재처리 ,Sttry:다음단계진행
			if (prcCd.equals("Retry") || prcCd.equals("Sttry")) {
				if (prcCd.equals("Retry")) {
					strQuery.setLength(0);
					strQuery.append("update cmr1010 set cr_putcode=''                        \n");
					strQuery.append(" where cr_acptno=? and cr_prcdate is null               \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, AcptNo);
					pstmt.executeUpdate();
					pstmt.close();
				}
                /*
				strQuery.setLength(0);
		        strQuery.append("select count(*) as cnt                                      \n");
		        strQuery.append("  from cmr1010 a,cmm0036 b                                  \n");
		        strQuery.append(" where a.cr_acptno=? and a.cr_prcdate is null               \n");
		        strQuery.append("   and nvl(a.cr_putcode,'NONE')<>'0000'                     \n");
		        strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd    \n");
		        if (prcSys.equals("SYSPF")) {
		        	strQuery.append("and (substr(b.cm_info,4,1)='1' or                       \n");
		        	strQuery.append("     substr(b.cm_info,9,1)='1' or                       \n");
		        	strQuery.append("     substr(b.cm_info,27,1)='1')                        \n");
		        }else if (prcSys.equals("SYSUP")) {
		        	strQuery.append("and (substr(b.cm_info,22,1)='1' or                      \n");
		        	strQuery.append("     substr(b.cm_info,24,1)='1')                        \n");
		        }else if (prcSys.equals("SYSCB")) {
		        	strQuery.append("and (substr(b.cm_info,1,1)='1' or                       \n");
		        	strQuery.append("     substr(b.cm_info,13,1)='1' or                      \n");
		        	strQuery.append("     substr(b.cm_info,25,1)='1')                        \n");
		        }else if (prcSys.equals("SYSUA")) {
		        	strQuery.append("and substr(b.cm_info,49,1)='1'                          \n");
		        }else if (prcSys.equals("SYSPC")) {
		        	strQuery.append("and substr(b.cm_info,50,1)='1'                          \n");
		        }else if (prcSys.equals("SYSED")) {
		        	strQuery.append("and (substr(b.cm_info,11,1)='1' or                      \n");
		        	strQuery.append("     substr(b.cm_info,21,1)='1')                        \n");
		        }else if (prcSys.equals("SYSRC")) {
		        	strQuery.append("and (substr(b.cm_info,35,1)='1')                        \n");
		        }else if (prcSys.equals("SYSAR")) {
		        	strQuery.append("and (substr(b.cm_info,40,1)='1')                        \n");
		        }else if (prcSys.equals("SYSGB")) {
		        	strQuery.append("and substr(b.cm_info,25,1)='1'                          \n");
		        }
		        strQuery.append("union                                                       \n");
		        strQuery.append("select count(*) as cnt                                      \n");
		        strQuery.append("  from cmr1011 a,cmr1010 b                                  \n");
		        strQuery.append(" where b.cr_acptno=? and b.cr_prcdate is null               \n");
		        strQuery.append("   and b.cr_acptno=a.cr_acptno                              \n");
		        strQuery.append("   and b.cr_serno=a.cr_serno                                \n");
		        strQuery.append("   and instr(?,a.cr_prcsys)>0                               \n");
		        strQuery.append("   and nvl(a.cr_putcode,'NONE')<>'0000'                     \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, AcptNo);
				pstmt.setString(3, wkPrcSys);
				rs = pstmt.executeQuery();
				endSw = true;
				while (rs.next()) {
					if (rs.getInt("cnt") > 0) {
						endSw = false;
						break;
					}
				}
				rs.close();
				pstmt.close();
				*/
				endSw = false;
			}

			if (endSw == true) {
				strQuery.setLength(0);
				strQuery.append("update cmr1010 set cr_pid='',cr_srccmp='Y',cr_putcode=''     \n");
	            strQuery.append(" where cr_acptno=? and cr_status='0'                         \n");
	            strQuery.append("   and cr_serno in (select cr_serno from cmr1011             \n");
	            strQuery.append("                         where cr_acptno=?                   \n");
	            strQuery.append("                           and cr_prcsys=?                   \n");
	            strQuery.append("                           and cr_rungbn=?                   \n");
	            strQuery.append("                           and (nvl(cr_prcrst,'0000')='0000' \n");
	            strQuery.append("                           or   nvl(cr_prcrst,'RTRY')='RTRY')) \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,AcptNo);
	            pstmt.setString(3,prcSys);
	            pstmt.setString(4,wkRunGbn);
	            pstmt.executeUpdate();
		        pstmt.close();
			}
			if (prcCd.equals("Retry") && endSw == false) {  //전체재처리
				strQuery.setLength(0);
				strQuery.append("delete cmr1011                                          \n");
				strQuery.append(" where cr_acptno=?                                      \n");
				strQuery.append("   and (cr_serno in (select cr_serno from cmr1010       \n");
				strQuery.append("                     where cr_acptno=?                  \n");
				strQuery.append("                       and cr_prcdate is null           \n");
				strQuery.append("                       and cr_status='0')               \n");
				strQuery.append("                 or cr_serno=0)                         \n");
				strQuery.append("   and instr(?,cr_prcsys)>0                             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2,AcptNo);
				pstmt.setString(3,wkPrcSys);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();

				strQuery.setLength(0);
				strQuery.append("update cmr1010                                              \n");
				strQuery.append("   set cr_putcode='',cr_pid='',cr_srccmp='Y',cr_sysstep=0   \n");
				strQuery.append(" where cr_acptno=? and cr_prcdate is null                   \n");
				strQuery.append("   and cr_status='0'                                        \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();
			} else if (prcCd.equals("Sttry") && endSw == false) {   //다음단계진행
				strQuery.setLength(0);
				strQuery.append("update cmr1010 set cr_pid='',cr_srccmp='Y',cr_putcode=''     \n");
	            strQuery.append(" where cr_acptno=? and cr_status='0'                         \n");
	            strQuery.append("   and cr_serno not in (select cr_serno from cmr1011         \n");
	            strQuery.append("                         where cr_acptno=?                   \n");
	            strQuery.append("                           and cr_prcsys=?                   \n");
	            strQuery.append("                           and (nvl(cr_prcrst,'0000')='0000' \n");
	            strQuery.append("                           or   nvl(cr_prcrst,'RTRY')='RTRY')) \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,AcptNo);
	            pstmt.setString(3,prcSys);
	            pstmt.executeUpdate();
		        pstmt.close();
			} else if (endSw == false) { //오류건재처리
				strQuery.setLength(0);
				strQuery.append("update cmr1010 set cr_pid='',cr_putcode=''                   \n");
	            strQuery.append(" where cr_acptno=? and cr_status='0'                         \n");
	            strQuery.append("   and cr_serno not in (select cr_serno from cmr1011         \n");
	            strQuery.append("                         where cr_acptno=?                   \n");
	            strQuery.append("                           and cr_prcsys=?                   \n");
	            strQuery.append("                           and cr_rungbn=?                   \n");
	            strQuery.append("                           and (nvl(cr_prcrst,'0000')='0000' \n");
	            strQuery.append("                           or   nvl(cr_prcrst,'RTRY')='RTRY')) \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	           // pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,AcptNo);
	            pstmt.setString(3,prcSys);
	            pstmt.setString(4,wkRunGbn);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.executeUpdate();
		        pstmt.close();


				strQuery.setLength(0);
				strQuery.append("update cmr1010 set cr_pid='',cr_putcode=''                   \n");
	            strQuery.append(" where cr_acptno=? and cr_status='0'                         \n");
	            strQuery.append("   and cr_serno in (select cr_serno from cmr1011         \n");
	            strQuery.append("                         where cr_acptno=?                   \n");
	            strQuery.append("                           and cr_prcsys=?                   \n");
	            strQuery.append("                           and cr_rungbn=?                   \n");
	            strQuery.append("                           and nvl(cr_prcrst,'WAIT')<>'0000') \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	           // pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,AcptNo);
	            pstmt.setString(3,prcSys);
	            pstmt.setString(4,wkRunGbn);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.executeUpdate();
		        pstmt.close();


				strQuery.setLength(0);
				strQuery.append("delete cmr1011                                             \n");
				strQuery.append(" where cr_acptno=?                                         \n");
				strQuery.append("   and cr_serno in (select cr_serno from cmr1010           \n");
				strQuery.append("                     where cr_acptno=?                     \n");
				strQuery.append("                       and cr_prcdate is null              \n");
				strQuery.append("                       and cr_status='0')                  \n");
				strQuery.append("   and cr_prcsys=? and nvl(cr_putcode,'NONE')<>'0000'      \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2,AcptNo);
				pstmt.setString(3,prcSys);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();
			}
			strQuery.setLength(0);
			strQuery.append("update cmr1000 set cr_notify='0',cr_retryyn='Y'             \n");
			strQuery.append(" where cr_acptno=?                                          \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
	        pstmt.close();

	        if ((prcSys.equals("SYSUA") || prcSys.equals("SYSGB")) && prcCd.equals("Retry")) {
	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900  set                             \n");
	        	strQuery.append("  (cr_team,cr_confname,cr_sgngbn,cr_confusr)    \n");
	        	strQuery.append(" =(select cr_team,cr_confname,cr_sgngbn,cr_locat || cr_locat    \n");
	        	strQuery.append("     from cmr9900          \n");
	        	strQuery.append("    where cr_acptno=?      \n");
	        	strQuery.append("      and cr_locat<>'00'   \n");
	        	strQuery.append("      and cr_team=?)       \n");
	        	strQuery.append(" where cr_acptno=?         \n");
	        	strQuery.append("   and cr_locat='00'       \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
				if (wkPrcSys.indexOf("SYSUA")>=0) pstmt.setString(2, "SYSUA");
				else pstmt.setString(2, "SYSCB");
				pstmt.setString(3,AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();

	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900  set             \n");
	        	strQuery.append("  cr_status='0',cr_confdate='', \n");
	        	strQuery.append("  cr_conmsg='',cr_confusr=''    \n");
	        	strQuery.append(" where cr_acptno=?              \n");
	        	strQuery.append("   and cr_locat<>'00'           \n");
	        	strQuery.append("   and instr(?,cr_team)>0       \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, wkPrcSys);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();
	        }

	        conn.commit();
	        conn.close();
			rs = null;
			pstmt = null;
			conn = null;

	        return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.svrProc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.svrProc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.svrProc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.putDeploy() Exception END ##");
			throw exception;
		}finally{
			if (run != null) run = null;
			if (p != null) p = null;
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of putDeploy() method statement

	public String delReq(String AcptNo,String ItemId,String SignTeam,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

            strQuery.setLength(0);
            strQuery.append("delete cmr1011 where cr_acptno=?                     \n");
            strQuery.append("   and cr_serno in (select cr_serno from cmr1010     \n");
            strQuery.append("                     where cr_acptno=?               \n");
            strQuery.append("                       and cr_baseitem=?)            \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,AcptNo);
            pstmt.setString(3,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete cmr1010 where cr_acptno=? and cr_baseitem=?    \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr0020 set cr_status='0'                      \n");
            strQuery.append(" where cr_itemid in (select cr_itemid from cmr1010    \n");
            strQuery.append("                      where cr_acptno=?               \n");
            strQuery.append("                        and cr_baseitem=?)            \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strErMsg = "0";
            strQuery.setLength(0);
            strQuery.append("select count(*) as cnt from cmr1010                    \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	if (rs.getInt("cnt") == 0) {
            		strQuery.setLength(0);
                    strQuery.append("delete cmr9920 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr9910 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

                    strQuery.setLength(0);
                    strQuery.append("delete cmr9900 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr1001 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr1000 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();
            	}
            }
            else {
	            strQuery.setLength(0);
	            strQuery.append("select count(*) as cnt from cmr1010                    \n");
	            strQuery.append(" where cr_acptno=?                                     \n");
	            strQuery.append("   and (cr_putcode is null or cr_putcode != '0000')    \n");
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	            pstmt2.setString(1,AcptNo);
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()) {
	            	if (rs2.getInt("cnt") == 0) {
	            		Cmr3100 gyulProc = new Cmr3100();
	            		strErMsg = gyulProc.nextConf(AcptNo, SignTeam, "삭제 후 자동완료", "1", ReqCd);
	            		gyulProc = null;
	            	}
	            }
	            rs2.close();
	            pstmt2.close();
            }

            rs.close();
            pstmt.close();

            if (strErMsg.equals("0")){
            	conn.commit();
            }
            else{
            	conn.rollback();
            }

            conn.close();

			rs = null;
			pstmt = null;
	        rs2 = null;
	        pstmt2 = null;
			conn = null;

            return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.delReq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.delReq() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.delReq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.delReq() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delReq() method statement

	public String chgVolPath(String SysCd,String DirPath,String SvrCd,String RsrcCd,int SvrSeq,String JobCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strPath     = null;
		String            baseDir     = null;
		String            svrDir     = null;

		try {
			strQuery.append("select a.cm_svrcd,a.cm_volpath                            \n");
			strQuery.append("  from cmm0038 a,cmm0031 b,cmm0030 c                      \n");
			strQuery.append(" where c.cm_syscd=? and c.cm_syscd=b.cm_syscd             \n");
			strQuery.append("   and nvl(c.cm_dirbase,'01')=b.cm_svrcd                  \n");
			strQuery.append("   and b.cm_closedt is null                               \n");
			strQuery.append("   and nvl(b.cm_cmpsvr,'N')='Y'                           \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");
			strQuery.append("   and rownum=1                                           \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("cm_volpath") != "") baseDir = rs.getString("cm_volpath");
			} 
            rs.close();
            pstmt.close();
            
            if (baseDir == null) {
            	strQuery.setLength(0);
            	strQuery.append("select a.cm_svrcd,a.cm_volpath                            \n");
    			strQuery.append("  from cmm0038 a,cmm0031 b,cmm0030 c                      \n");
    			strQuery.append(" where c.cm_syscd=? and c.cm_syscd=b.cm_syscd             \n");
    			strQuery.append("   and nvl(c.cm_dirbase,'01')=b.cm_svrcd                  \n");
    			strQuery.append("   and b.cm_closedt is null                               \n");
    			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
    			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");
    			strQuery.append("   and rownum=1                                           \n");

    			pstmt = conn.prepareStatement(strQuery.toString());
    			//pstmt = new LoggableStatement(conn,strQuery.toString());
    			pstmt.setString(1, SysCd);
    			pstmt.setString(2, RsrcCd);
                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
                if (rs.next()){
                	if (rs.getString("cm_volpath") != "") baseDir = rs.getString("cm_volpath");
    			} 
                rs.close();
                pstmt.close();
            }
            strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_volpath                            \n");
			strQuery.append("  from cmm0038 a,cmm0031 b                                \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_svrcd=?                      \n");	//AcptNo
			strQuery.append("   and b.cm_seqno=?                                       \n");	//AcptNo
			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, SvrCd);
			pstmt.setInt(3, SvrSeq);
			pstmt.setString(4, RsrcCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("cm_volpath") != ""){
            		svrDir = rs.getString("cm_volpath");
            	}
			}

            rs.close();
            pstmt.close();

            if (svrDir != null && baseDir != null) {
            	if (svrDir.equals(baseDir)){
            		strPath = DirPath;
            	} else if (DirPath.length()>0 && DirPath.length()>baseDir.length()){
            		strPath = svrDir + DirPath.substring(baseDir.length());
            	} else {
            		strPath = svrDir;
            	}
            }else{
            	strPath = DirPath;
            }

			rs = null;
			pstmt = null;

            return strPath;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.chgVolPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.chgVolPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.chgVolPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.chgVolPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement

	public String chgVolPath2(String SysCd,String DirPath,String ReqCd,String RsrcCd,String PrcSys,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strPath     = null;
		String            baseDir     = null;
		String            svrDir     = null;
		String            cmInfo     = "";

		try {
			strQuery.append("select a.cm_svrcd,a.cm_volpath,d.cm_info                  \n");
			strQuery.append("  from cmm0036 d,cmm0038 a,cmm0031 b,cmm0030 c            \n");
			strQuery.append(" where c.cm_syscd=? and c.cm_dirbase=b.cm_svrcd           \n");
			strQuery.append("   and b.cm_closedt is null and nvl(b.cm_cmpsvr,'N')='Y'  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");
			strQuery.append("   and b.cm_syscd=d.cm_syscd                              \n");
			strQuery.append("   and d.cm_rsrccd=? and rownum=1                         \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.setString(3, RsrcCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("cm_volpath") != "") baseDir = rs.getString("cm_volpath");
            	cmInfo = rs.getString("cm_info");
			}
            rs.close();
            pstmt.close();
            
            if (baseDir == null) {
            	strQuery.setLength(0);
            	strQuery.append("select a.cm_svrcd,a.cm_volpath,d.cm_info                  \n");
    			strQuery.append("  from cmm0036 d,cmm0038 a,cmm0031 b,cmm0030 c            \n");
    			strQuery.append(" where c.cm_syscd=? and c.cm_dirbase=b.cm_svrcd           \n");
    			strQuery.append("   and b.cm_closedt is null                               \n");
    			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
    			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");
    			strQuery.append("   and b.cm_syscd=d.cm_syscd                              \n");
    			strQuery.append("   and d.cm_rsrccd=? and rownum=1                         \n");

    			pstmt = conn.prepareStatement(strQuery.toString());
    			//pstmt = new LoggableStatement(conn,strQuery.toString());
    			pstmt.setString(1, SysCd);
    			pstmt.setString(2, RsrcCd);
    			pstmt.setString(3, RsrcCd);
                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
                if (rs.next()){
                	if (rs.getString("cm_volpath") != "") baseDir = rs.getString("cm_volpath");
                	cmInfo = rs.getString("cm_info");
    			}
                rs.close();
                pstmt.close();
            }
            strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_volpath                            \n");
			strQuery.append("  from cmm0038 a,cmm0031 b                                \n");
			strQuery.append(" where b.cm_syscd=?                                       \n");	//AcptNo
			strQuery.append("   and b.cm_svrcd=decode(?,'SYSCB',decode(?,'07','23','03'),decode(?,'07','25','05'))   \n");	//AcptNo
			strQuery.append("   and b.cm_closedt is null and nvl(b.cm_cmpsvr,'Y')='Y'  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, PrcSys);
			pstmt.setString(3, ReqCd);
			pstmt.setString(4, ReqCd);
			pstmt.setString(5, RsrcCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("cm_volpath") != ""){
            		svrDir = rs.getString("cm_volpath");
            	}
			}

            rs.close();
            pstmt.close();

            if (svrDir != null && baseDir != null) {
            	if (svrDir.equals(baseDir)){
            		strPath = DirPath;
            	} else if (DirPath.length()>0 && DirPath.length()>baseDir.length()){
            		strPath = svrDir + DirPath.substring(baseDir.length());
            	} else {
            		strPath = svrDir;
            	}
            }else{
            	strPath = DirPath;
            }
            if (PrcSys.equals("SYSED")) {
	            if (cmInfo.substring(36,37).equals("1")) strPath = svrDir;
	            if (cmInfo.substring(22,23).equals("1")) strPath = strPath + "/tmp";
            }
			rs = null;
			pstmt = null;

            return strPath;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.chgVolPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.chgVolPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.chgVolPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.chgVolPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement
	public Object[] getPrcSys(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] 		  returnObject = null;
		//String            strAcptNo    = "";
		//int               i = 0;
		int               parmCnt = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select b.cm_micode,b.cm_codename,a.cr_confdate            \n");
			strQuery.append("  from cmr9900 a,cmm0020 b                                \n");
			strQuery.append(" where a.cr_acptno=?                                      \n");
			strQuery.append("   and ((a.cr_locat<>'00' and a.cr_teamcd='1'             \n");
			strQuery.append("         and a.cr_confdate is not null) or                \n");
			strQuery.append("        (a.cr_locat='00' and a.cr_teamcd='1'              \n");
			strQuery.append("                         and a.cr_status in ('0','3')))   \n");
			strQuery.append("   and b.cm_macode='SYSGBN' and b.cm_micode=a.cr_team     \n");
			strQuery.append(" order by a.cr_confdate                                   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            while (rs.next()){
            	if (rs.getRow() == 1) {
            		//findSw = true;
            		rst = new HashMap<String, String>();
    				//rst.put("ID", Integer.toString(rs.getRow()));
    				rst.put("cm_codename", "전체");
    				rst.put("cm_micode", "0");
    				rsval.add(rst);
            	}
            	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("qrycd", AcptNo.substring(4,6));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();

            conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getPrcSys() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.getPrcSys() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0250.getPrcSys() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.getPrcSys() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (returnObject != null) returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.getPrcSys() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrcSys() method statement


	public String updtSeq(String AcptNo,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            for (int i=0;i<fileList.size();i++){
            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set cr_priority=?                    \n");
                strQuery.append(" where cr_acptno=? and cr_serno=?                   \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setInt(1,Integer.parseInt(fileList.get(i).get("priority").trim()));
                pstmt.setString(2,AcptNo);
                pstmt.setInt(3,Integer.parseInt(fileList.get(i).get("cr_serno").trim()));
                pstmt.executeUpdate();
                pstmt.close();
            }
            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtSeq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtSeq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.updtSeq() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtSeq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtSeq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.updtSeq() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtSeq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtSeq() method statement

	public String updtDeploy(String AcptNo,String ReqPass,String DeployDate,String PassCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
        	strQuery.append("update cmr1000 set cr_passok=?             \n");
        	if (ReqPass.equals("2")) strQuery.append(",cr_sayucd=?      \n");
            strQuery.append(" where cr_acptno=?                         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++parmCnt,ReqPass);
            if (ReqPass.equals("2")) pstmt.setString(++parmCnt, PassCd);
            pstmt.setString(++parmCnt,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            if (ReqPass.equals("4")) {
            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set cr_aplydate=?           \n");
                strQuery.append(" where cr_acptno=?                         \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,DeployDate);
                pstmt.setString(2,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();

                strQuery.setLength(0);
            	strQuery.append("update cmr1000 set cr_prcreq=?             \n");
                strQuery.append(" where cr_acptno=?                         \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,DeployDate);
                pstmt.setString(2,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();


            } else {
            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set cr_aplydate=''          \n");
                strQuery.append(" where cr_acptno=?                         \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();

                strQuery.setLength(0);
            	strQuery.append("update cmr1000 set cr_prcreq=''            \n");
                strQuery.append(" where cr_acptno=?                         \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();

            }

            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtDeploy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.updtDeploy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtDeploy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.updtDeploy() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtDeploy() method statement

	public String updtDeploy_2(String AcptNo,String CD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
        	strQuery.append("update cmr1000 set cr_gyuljae=?            \n");
            strQuery.append(" where cr_acptno=?                         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,CD);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            if (CD.equals("1")) {
            	strQuery.setLength(0);
            	strQuery.append("update cmr1010                                        \n");
            	strQuery.append("   set cr_aplydate=to_char(SYSDATE,'yyyymmddhh24mi')  \n");
                strQuery.append(" where cr_acptno=?                                    \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();

            } else {
            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set cr_aplydate=''          \n");
                strQuery.append(" where cr_acptno=?                         \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,AcptNo);
                pstmt.executeUpdate();
                pstmt.close();

            }

            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtDeploy_2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtDeploy_2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.updtDeploy_2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## updtDeploy_2.updtDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtDeploy_2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.updtDeploy_2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtDeploy_2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtDeploy_2() method statement

	public String updtPrjInfo(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			conn.setAutoCommit(false);
            strQuery.setLength(0);
        	strQuery.append("update cmr1000 set             				\n");
		    strQuery.append("		CR_etc=?,CR_SAYU=?,cr_sayucd=?,			\n");
	        strQuery.append("       CR_dept=?,CR_reqdate=?,					\n");
	        strQuery.append("       CR_reqdoc=?,CR_reqtitle=? 				\n");
            strQuery.append(" where cr_acptno=?                         	\n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,etcData.get("etc"));
            pstmt.setString(2,etcData.get("sayu"));
            pstmt.setString(3,etcData.get("sayucd"));
            pstmt.setString(4,etcData.get("dept"));
            pstmt.setString(5,etcData.get("reqdate"));
            pstmt.setString(6,etcData.get("reqdoc"));
            pstmt.setString(7,etcData.get("reqtit"));
            pstmt.setString(8,etcData.get("acptno"));
            pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtPrjInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.updtPrjInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtPrjInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.updtPrjInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtPrjInfo() method statement
	public String updtTemp(String AcptNo,String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
        	strQuery.append("update cmr1010 set cr_tmpdelyn='N'             \n");
            strQuery.append(" where cr_acptno=? and cr_baseitem=?           \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtTemp() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtTemp() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.updtTemp() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtTemp() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.updtTemp() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.updtTemp() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.updtTemp() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtTemp() method statement


	public String progCncl(String AcptNo,String ItemId,String PrcSys) throws SQLException, Exception {
		Connection        conn        = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		Runtime  run = null;
		Process p = null;


		try {
			String  binpath;
			String[] chkAry;
			int		cmdrtn;

			SystemPath systemPath = new SystemPath();
			binpath = systemPath.getTmpDir("15");
			systemPath = null;

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;

			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();

			chkAry = null;
			p = null;
			run = null;

			if (cmdrtn > 0) {
				return "2";
			}

			conn = connectionContext.getConnection();
			if (PrcSys.equals("SYSCB") && !AcptNo.substring(4,6).equals("16")) {
				boolean findSw = false;
				strQuery.setLength(0);
	        	strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b     \n");
	        	strQuery.append(" where a.cr_acptno=? and a.cr_baseitem=?         \n");
	            strQuery.append("   and a.cr_syscd=b.cm_syscd                     \n");
	            strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                   \n");
	            strQuery.append("   and substr(b.cm_info,7,1)='1'                 \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,ItemId);
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	if (rs.getInt("cnt") > 0) {
	            		findSw = true;
	            	}
	            }
	            pstmt.close();
	            rs.close();

	            if (findSw == true) {
	            	/*
	            	run = Runtime.getRuntime();

	    			chkAry = new String[4];
	    			chkAry[0] = "/bin/sh";
	    			chkAry[1] = binpath+"/procck2";
	    			if (AcptNo.substring(4,6).equals("16")) chkAry[2] = "ecams_ih_new";
	    			else chkAry[2] = "ecams_acct";
	    			chkAry[3] = AcptNo;

	    			p = run.exec(chkAry);
	    			p.waitFor();

	    			cmdrtn = p.exitValue();
	    			if (cmdrtn > 1) {
	    				ecamsLogger.error(chkAry[0]+" " + chkAry[1] + " " + chkAry[2]);
	    				ecamsLogger.error(cmdrtn);
	    				return "2";
	    			}
	    			*/
	            }
			}

			conn.setAutoCommit(false);
            /*
            strQuery.setLength(0);
        	strQuery.append("delete cmr1011 where cr_acptno=?                 \n");
            strQuery.append("   and cr_serno in (select cr_serno from cmr1010 \n");
            strQuery.append("                     where cr_acptno=?           \n");
            strQuery.append("                       and cr_baseitem=?)        \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,AcptNo);
            pstmt.setString(3,ItemId);
            pstmt.executeUpdate();
            pstmt.close();
            */
            strQuery.setLength(0);
        	strQuery.append("update cmr1010 set cr_status='3',cr_prcdate=SYSDATE,\n");
        	strQuery.append("                   cr_cnclstep=?                    \n");
        	strQuery.append(" where cr_acptno=? and cr_baseitem=?                \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,PrcSys);
            pstmt.setString(2,AcptNo);
            pstmt.setString(3,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
        	strQuery.append("update cmr1010 set cr_confno=''             \n");
        	strQuery.append(" where cr_confno=? and cr_baseitem=?        \n");
        	strQuery.append("   and cr_acptno<>cr_confno                 \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr1000 set cr_cnclstep=?,           \n");
            strQuery.append("       cr_cncllstd=SYSDATE,cr_retryyn='N'   \n");
            strQuery.append(" where cr_acptno=?                          \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, PrcSys);
            pstmt.setString(2, AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
            conn.close();
            rs = null;
			pstmt = null;
			conn = null;

    		return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.progCncl() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.progCncl() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.progCncl() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.progCncl() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of progCncl() method statement

	public String progCncl_sel(String AcptNo,ArrayList<HashMap<String,String>> fileList,String PrcSys) throws SQLException, Exception {
		Connection        conn        = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		Runtime  run = null;
		Process p = null;
		String          basePgm    = "";
		String          baseItem   = "";
		int             baseCnt    = 0;

		try {
			String  binpath;
			String[] chkAry;
			int		cmdrtn;

			SystemPath systemPath = new SystemPath();
			binpath = systemPath.getTmpDir("15");
			systemPath = null;

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;

			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();

			chkAry = null;
			p = null;
			run = null;

			if (cmdrtn > 0) {
				//ecamsLogger.error(chkAry[0]+" " + chkAry[1] + " " + chkAry[2]);
				//ecamsLogger.error(cmdrtn);
				return "2";
			}
			conn = connectionContext.getConnection();

			conn.setAutoCommit(false);
			for (int i=0;fileList.size()>i;i++) {
				baseCnt = 0;
				strQuery.setLength(0);
		    	strQuery.append("select cr_itemid,cr_basepgm,cr_baseitem from cmr1010   \n");
		    	strQuery.append(" where cr_acptno=? and instr(cr_basepgm,?)>0           \n");
		    	strQuery.append("   and cr_itemid<>cr_baseitem                          \n");
		    	strQuery.append("   and cr_baseitem<>cr_basepgm                         \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1,AcptNo);
		        pstmt.setString(2,fileList.get(i).get("cr_itemid"));
				rs = pstmt.executeQuery();
				while (rs.next()) {
					++baseCnt;
					basePgm = rs.getString("cr_basepgm").replace(fileList.get(i).get("cr_itemid"), "");
					basePgm = basePgm.replaceAll(",,", ",");
					if (basePgm.length()<12) {
						basePgm = "";
					}
					if (basePgm.length()>0){
						if (basePgm.substring(basePgm.length()-1).equals(",")) {
							basePgm = basePgm.substring(0,basePgm.length()-1);
						}
						if (basePgm.substring(0,1).equals(",")) {
							basePgm = basePgm.substring(1);
						}
					}
					if (rs.getString("cr_baseitem").equals(fileList.get(i).get("cr_itemid"))) {
						baseItem = basePgm.substring(0,12);
					} else {
						baseItem = rs.getString("cr_baseitem");
					}
					strQuery.setLength(0);
			    	strQuery.append("update cmr1010 set cr_basepgm=?,cr_baseitem=?          \n");
			    	strQuery.append(" where cr_acptno=? and cr_itemid=?                     \n");
			        pstmt = conn.prepareStatement(strQuery.toString());
			        pstmt.setString(1,basePgm);
			        pstmt.setString(2,baseItem);
			        pstmt.setString(3,AcptNo);
			        pstmt.setString(4,rs.getString("cr_itemid"));
			        pstmt.executeUpdate();
			        pstmt.close();
				}
				rs.close();
				pstmt.close();
				
		        strQuery.setLength(0);
		    	strQuery.append("update cmr1010 set cr_status='3',cr_prcdate=SYSDATE,\n");
		    	strQuery.append("                   cr_cnclstep=?                    \n");
		    	strQuery.append(" where cr_acptno=? and cr_basepgm=?                 \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1,PrcSys);
		        pstmt.setString(2,AcptNo);
		        pstmt.setString(3,fileList.get(i).get("cr_itemid"));
		        pstmt.executeUpdate();
		        pstmt.close();
				
		        strQuery.setLength(0);
		    	strQuery.append("update cmr1010 set cr_confno=''             \n");
		    	strQuery.append(" where cr_confno=? and cr_basepgm=?         \n");
		    	strQuery.append("   and cr_acptno<>cr_confno                 \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1,AcptNo);
		        pstmt.setString(2,fileList.get(i).get("cr_itemid"));
		        pstmt.executeUpdate();
		        pstmt.close();

		        strQuery.setLength(0);
		        strQuery.append("update cmr1000 set cr_cnclstep=?,           \n");
		        strQuery.append("       cr_cncllstd=SYSDATE,cr_retryyn='N'   \n");
		        strQuery.append(" where cr_acptno=?                          \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, PrcSys);
		        pstmt.setString(2, AcptNo);
		        pstmt.executeUpdate();
		        pstmt.close();
			}
	        conn.commit();
	        conn.close();
			pstmt = null;
			conn = null;

			return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl_sel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.progCncl_sel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0250.progCncl_sel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl_sel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0250.progCncl_sel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0250.progCncl_sel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0250.progCncl_sel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of progCncl_sel() method statement

	
}//end of Cmr0250 class statement
