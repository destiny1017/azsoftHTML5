package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


public class Cmr1100 {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getFileList_detail(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		Boolean			  AdminChk	  = false;
		int				 subrowcnt    = 0;
		String			 sin		  = null;

		try {
			conn = connectionContext.getConnection();

			UserInfo		  userInfo	  = new UserInfo();
			AdminChk = userInfo.isAdmin(etcData.get("UserID"));
			userInfo = null;

			strQuery.append("select a.cr_acptno,a.cr_sysgb,to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate, \n");
			strQuery.append("       a.cr_syscd,a.cr_editor,a.cr_qrycd qry,   \n");
			strQuery.append("       b.cm_codename,d.cm_SYSMSG sysgb,e.cm_jobname,f.cm_codename sin,\n");
			strQuery.append("       c.cr_serno,c.cr_qrycd,a.cr_prcreq,a.cr_sayu,                   \n");
			strQuery.append("       c.cr_rsrcname,c.cr_dsncd,c.cr_confno,c.cr_baseno,              \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate,         \n");
			strQuery.append("       c.cr_status,c.cr_editcon,g.cm_dirpath,a.cr_sayucd,cr_passcd,   \n");
			strQuery.append("       j.cc_srid,j.cc_reqtitle,i.cm_codename procgbn                  \n");
			strQuery.append("from cmm0020 f,cmm0030 d,cmm0102 e, cmm0020 b, cmr1000 a,cmr1010 c,cmm0070 g, \n");
			strQuery.append("     (select distinct nvl(b.cr_baseno,b.cr_acptno) cr_baseno,b.cr_syscd,b.cr_dsncd,b.cr_rsrcname \n");
			strQuery.append("        from cmr1000 a,cmr1010 b \n");
			strQuery.append("       where a.cr_qrycd<'50' \n");
			if (AdminChk == false){
				strQuery.append("     and a.cr_editor= ? \n");
			}
			if (etcData.get("req").equals("1")){
				strQuery.append("     and a.cr_qrycd in ('01','02','11') \n");
			}
			else{
				strQuery.append("     and a.cr_qrycd in ('03','04','06','12') \n");
			}
		    if (!etcData.get("reqcd").equals("00")){
		    	strQuery.append("     and a.cr_qrycd= ? \n");
		    }
			if (etcData.get("stepcd").equals("0")){
				strQuery.append("     and ((a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append(" and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append(" and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') ) or \n");
				}else{
					strQuery.append(" and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append(" and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') ) or \n");
				}
				strQuery.append("         a.cr_status='0') \n");
			}
			else if (etcData.get("stepcd").equals("1")){
				strQuery.append("    and a.cr_status='0' \n");
			} else if (etcData.get("stepcd").equals("3")) {
				strQuery.append("and a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}else{
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}
				strQuery.append("and a.cr_status='3' \n");
			}
			else if (etcData.get("stepcd").equals("9")){
				strQuery.append("    and a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}else{
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') ) or \n");
				}
			}
			strQuery.append("       and a.cr_acptno=b.cr_acptno \n");
			strQuery.append("       and b.cr_itemid is not null \n");
			strQuery.append("       and b.cr_itemid=b.cr_baseitem \n");
			if (etcData.get("prgname") != null){
				if (!etcData.get("prgname").equals("") && etcData.get("prgname").length() > 0){
					strQuery.append("and upper(b.cr_rsrcname) like upper(?) \n");
				}
			}
			strQuery.append("     ) h, cmm0020 i,cmc0100 j                  \n");
			strQuery.append("where nvl(c.cr_baseno,c.cr_acptno)=h.cr_baseno and c.cr_syscd=h.cr_syscd \n");
			strQuery.append("  and c.cr_dsncd=h.cr_dsncd and c.cr_rsrcname=h.cr_rsrcname \n");
			strQuery.append("  and c.cr_acptno=a.cr_acptno \n");
			strQuery.append("  and c.cr_syscd=g.cm_syscd and c.cr_dsncd=g.cm_dsncd \n");
			strQuery.append("  and b.cm_macode = 'CMR1000' and a.cr_status = b.cm_micode \n");
			strQuery.append("  and a.cr_sysCD = d.cm_SYSCD \n");
			strQuery.append("  and a.cr_jobcd = e.cm_jobcd \n");
			strQuery.append("  and f.cm_macode = 'REQUEST' and a.cr_qrycd = f.cm_micode \n");
			if(!etcData.get("syscd").equals("00000")){
				strQuery.append(" and a.cr_sysCD = ?                       \n");
			}
			if(!etcData.get("jobcd").equals("0000")){
				strQuery.append(" and a.cr_jobcd = ?                       \n");
			}
			if(!etcData.get("gbn").equals("ALL")){
				strQuery.append(" and a.cr_passok = ?                      \n");
			}
			strQuery.append("   and a.cr_passok = i.cm_micode              \n");
			strQuery.append("   and i.cm_macode = 'REQPASS'                \n");
			strQuery.append("   and a.cr_itsmid=j.cc_srid(+)               \n");
			if(etcData.get("spms") !=null && etcData.get("spms")!=""){
				strQuery.append(" and (j.cc_srid like ? or upper(j.cc_reqtitle) like upper(?))      \n");
			}
			if (etcData.get("qrycd").equals("A")) {
				strQuery.append("order by a.cr_acptno,c.cr_serno           \n");
			} else {
			   strQuery.append("order by c.cr_rsrcname,a.cr_acptdate       \n");
			}

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

			if (AdminChk == false){
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
			}

			if (!etcData.get("reqcd").equals("00")){
				pstmt.setString(pstmtcount++, etcData.get("reqcd"));
			}

			if (etcData.get("stepcd").equals("0") || etcData.get("stepcd").equals("3") || etcData.get("stepcd").equals("9") ){
				pstmt.setString(pstmtcount++, etcData.get("stDt"));
				pstmt.setString(pstmtcount++, etcData.get("edDt"));
			}

			if (etcData.get("prgname") != null){
				if (!etcData.get("prgname").equals("") && etcData.get("prgname").length() > 0){
					pstmt.setString(pstmtcount++, "%"+etcData.get("prgname")+"%");
				}
			}
			if(!etcData.get("syscd").equals("00000")){
				pstmt.setString(pstmtcount++, etcData.get("syscd"));
			}
			if(!etcData.get("jobcd").equals("0000")){
				pstmt.setString(pstmtcount++, etcData.get("jobcd"));
			}
			if(!etcData.get("gbn").equals("ALL")){
				pstmt.setString(pstmtcount++, etcData.get("gbn"));
			}
			if(etcData.get("spms") !=null && etcData.get("spms")!=""){
				pstmt.setString(pstmtcount++, "%"+etcData.get("spms")+"%");
				pstmt.setString(pstmtcount++, "%"+etcData.get("spms")+"%");
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next())
			{
				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_reqtitle", rs.getString("cc_reqtitle"));
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("sysgbname",rs.getString("SysGb"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("procgbn", rs.getString("procgbn"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cr_status",rs.getString("cr_status"));
				if (rs.getString("cr_editcon") != null) rst.put("cr_editcon", rs.getString("cr_editcon"));
				if (rs.getString("cr_sayu") != null) {
					rst.put("cr_sayu", rs.getString("cr_sayu"));
				}
				
				if (rs.getString("qry").equals("03") || rs.getString("qry").equals("04")){
					strQuery2.setLength(0);
					strQuery2.append("select cm_codename from cmm0020 \n");
					strQuery2.append("where cm_macode='CHECKIN' and \n");
					strQuery2.append("cm_micode= ? \n");


					pstmt2 = conn.prepareStatement(strQuery2.toString());

					pstmt2.setString(1,rs.getString("cr_qrycd"));

					rs2 = pstmt2.executeQuery();

					while (rs2.next()){
						sin = rs2.getString("cm_codename");
					}
					rs2.close();
					pstmt2.close();

					rst.put("sin", rs.getString("sin") + "(" + sin + ")");
				}
				else{
					rst.put("sin", rs.getString("sin"));
				}
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));

				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cr_serno",rs.getString("cr_serno"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_qrycd",rs.getString("qry"));
				rst.put("cr_baseno",rs.getString("cr_baseno"));
				rst.put("cr_editor",rs.getString("cr_editor"));

				if (rs.getString("prcdate") != null) rst.put("prcdate", rs.getString("prcdate"));
				if (rs.getString("cr_prcreq") != null)
					rst.put("prcreq", rs.getString("cr_prcreq").substring(0, 4) + "/" +
							rs.getString("cr_prcreq").substring(4, 6) + "/" +
							rs.getString("cr_prcreq").substring(6, 8) + " " +
							rs.getString("cr_prcreq").substring(8, 10) + ":" +
							rs.getString("cr_prcreq").substring(10, 12));

			    strQuery2.setLength(0);
				strQuery2.append("select count(*) as cnt from cmr1011 where \n");
				strQuery2.append("cr_acptno= ? and \n");
				strQuery2.append("cr_serno= ? and \n");
				strQuery2.append("cr_rstfile is not null \n");


				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1,rs.getString("cr_acptno"));
				pstmt2.setInt(2, rs.getInt("cr_serno"));

				rs2 = pstmt2.executeQuery();

				while (rs2.next()){
					subrowcnt = rs2.getInt("cnt");
				}

				rs2.close();
				pstmt2.close();

				if (subrowcnt > 0){
					rst.put("result","1");
				}
				else{
					rst.put("result","0");
				}

				rst.put("cr_syscd",rs.getString("cr_syscd"));

				if (rs.getString("prcdate") == null){
					strQuery2.setLength(0);
					strQuery2.append("select count(*) as cnt from cmr1010 where \n");
					strQuery2.append("cr_acptno= ? and \n");
					strQuery2.append("cr_putcode is not null and cr_putcode='WAIT' \n");


					pstmt2 = conn.prepareStatement(strQuery2.toString());

					pstmt2.setString(1,rs.getString("cr_acptno"));

					rs2 = pstmt2.executeQuery();
					subrowcnt = 0;
					while (rs2.next()){
						subrowcnt = subrowcnt + rs2.getInt("cnt");
					}
					rs2.close();
					pstmt2.close();

					strQuery2.setLength(0);
					strQuery2.append("select count(*) as cnt from cmr1011 where  \n");
					strQuery2.append("cr_acptno= ? and \n");
					strQuery2.append("(cr_prcrst='WAIT' or cr_wait='W') \n");


					pstmt2 = conn.prepareStatement(strQuery2.toString());

					pstmt2.setString(1,rs.getString("cr_acptno"));

					rs2 = pstmt2.executeQuery();
					while (rs2.next()){
						subrowcnt = subrowcnt + rs2.getInt("cnt");
					}

					rs2.close();
					pstmt2.close();

					if (subrowcnt > 0){
						rst.put("errflag","1");
					}
					else{
						rst.put("errflag","0");
					}
				}
				else{
					rst.put("errflag","0");
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr1100.getFileList_detail() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1100.getFileList_detail() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1100.getFileList_detail() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1100.getFileList_detail() Exception END ##");
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
					ecamsLogger.error("## Cmr1100.getFileList_detail() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

	public Object[] getFileList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		int				 subrowcnt    = 0;
		String           reqName      = "";

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cr_acptno,a.cr_editor,a.cr_syscd,a.cr_status,     \n");
			strQuery.append("to_char(a.CR_ACPTDATE,'yyyy/mm/dd hh24:mi') as acptdate,   \n");
			strQuery.append("a.cr_acptno,a.cr_editor,a.cr_syscd,a.cr_status,            \n");
			strQuery.append("a.cr_passcd,a.cr_sysgb,a.cr_qrycd,a.cr_passok,             \n");
			strQuery.append("to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate,     \n");
			strQuery.append("a.cr_prcreq,a.cr_sayucd,a.cr_sayu,a.cr_closeyn,            \n");
			strQuery.append("d.cm_SYSMSG sysgb,e.cm_jobname,f.cm_codename sin,          \n");
			strQuery.append("g.cnt as subrowcnt,h.cr_rsrcname,i.cm_codename procgbn,    \n");
			strQuery.append("j.cc_srid, j.cc_docid, j.cc_reqtitle, h.cr_story           \n");
			strQuery.append("from cmm0020 f,cmm0030 d,cmm0102 e, cmr1000 a ,            \n");
			strQuery.append("    (select cr_acptno,count(*) as cnt from cmr1010         \n");
			
			if(etcData.get("chkSelf").equals("true")) {
				strQuery.append("Where cr_editor= ?                                     \n");
			    strQuery.append("  and cr_baseitem=cr_itemid                            \n");					
			} else {
			    strQuery.append("where cr_baseitem=cr_itemid                            \n");					
			}
			strQuery.append("       group by cr_acptno) g,                              \n");
			strQuery.append("       cmr1010 h, cmm0020 i,cmc0100 j                      \n");
			if (etcData.get("req").equals("01")){
				strQuery.append("where a.cr_qrycd in ('01','02','11')      \n");
			}else if(etcData.get("req").equals("03")){
				strQuery.append("where a.cr_qrycd in ('03','07','06','12') \n");
			}else if(etcData.get("req").equals("04")){
				strQuery.append("where a.cr_qrycd in ('03','04','07','06','12') \n");
			}else{
				strQuery.append("where a.cr_qrycd in ('07') 			   \n");
			}
			//else{
			//	strQuery.append("where a.cr_qrycd in ('03','04','06','12') \n");
			//}

//			if (AdminChk == false){
			if(etcData.get("chkSelf").equals("true")) {
				strQuery.append("and a.cr_editor= ? \n");					
			} else if( etcData.get("chkSelf").equals("false") && !etcData.get("userName").equals("") && (etcData.get("userName") != null) ){
					strQuery.append("and a.cr_editor in (select cm_userid		\n");
					strQuery.append("          from cmm0040						\n");
					strQuery.append("          where cm_username = ?)			\n");
			}
//				strQuery.append("and a.cr_editor= ? \n");
//			}

		    if (!etcData.get("reqcd").equals("00")){
		    	strQuery.append("and a.cr_qrycd= ? \n");
		    	if(etcData.get("reqcd").equals("93") || etcData.get("reqcd").equals("94")) { //93:테스트 폐기(qrycd=03), 94:운영폐기(qrycd=04)
		    		strQuery.append("and a.cr_closeyn = 'Y' \n");
		    	}
		    }

			if (etcData.get("stepcd").equals("0")){
				strQuery.append("and ((a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') ) or \n");
				}else{
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') ) or \n");
				}
				strQuery.append("a.cr_status='0') \n");
			}
			else if (etcData.get("stepcd").equals("1")){
				strQuery.append("and a.cr_status='0' \n");
			} else if (etcData.get("stepcd").equals("3")) {
				strQuery.append("and a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}else{
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}
				strQuery.append("and a.cr_status='3' \n");
			}
			else if (etcData.get("stepcd").equals("9")){
				strQuery.append("and a.cr_prcdate is not null \n");
				if(etcData.get("dategbn").equals("0")){
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}else{
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>= replace(? ,'/','') \n");
					strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<= replace(? ,'/','') \n");
				}
			}
			strQuery.append("and a.cr_acptno = g.cr_acptno \n");
			strQuery.append("and g.cr_acptno = h.cr_acptno \n");
			strQuery.append("and h.cR_serno = (select cr_serno from cmr1010 where cr_acptno=h.cr_acptno and rownum=1 ) \n");
			//strQuery.append("and b.cm_macode = 'CMR1000' and a.cr_status = b.cm_micode \n");
			strQuery.append("and a.cr_sysCD = d.cm_SYSCD \n");
			strQuery.append("and a.cr_jobcd = e.cm_jobcd \n");
			strQuery.append("and f.cm_macode = 'REQUEST' and a.cr_qrycd = f.cm_micode \n");
			if(!etcData.get("syscd").equals("00000")){
				strQuery.append("and a.cr_sysCD = ? \n");
			}
			if(!etcData.get("jobcd").equals("0000")){
				strQuery.append(" and a.cr_jobcd = ?                       \n");
			}
			if(!etcData.get("gbn").equals("ALL")){
				strQuery.append(" and a.cr_passok = ?                      \n");
			}
			strQuery.append(" and a.cr_passok = i.cm_micode                \n");
			strQuery.append(" and i.cm_macode = 'REQPASS'                  \n");
			strQuery.append(" and a.cr_itsmid=j.cc_srid(+)                 \n");
			if(etcData.get("spms") !=null && etcData.get("spms")!=""){
				strQuery.append(" and (j.cc_srid like ? or upper(j.cc_reqtitle) like upper(?) or j.cc_docid like ?)      \n");
			}
			if (etcData.get("prgname") != null && etcData.get("prgname") != ""){
				strQuery.append("and (upper(h.cr_rsrcname) like upper(?) or upper(h.cr_story) like upper(?))  \n");
			}
			//strQuery.append("order by a.cr_acptdate desc \n");
           // pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
//			if (AdminChk == false){
			if(etcData.get("chkSelf").equals("true")) {
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
			}else if( etcData.get("chkSelf").equals("false") && !etcData.get("userName").equals("") && (etcData.get("userName") != null) ){
				pstmt.setString(pstmtcount++, etcData.get("userName"));
			}
//			}

			if (!etcData.get("reqcd").equals("00")){
		    	if(etcData.get("reqcd").equals("93")) { //93:테스트 폐기(qrycd=03)
		    		pstmt.setString(pstmtcount++, "03");	
		    	} else if(etcData.get("reqcd").equals("94")) { // 94:운영폐기(qrycd=04)
		    		pstmt.setString(pstmtcount++, "04");	
		    	} else {
					pstmt.setString(pstmtcount++, etcData.get("reqcd"));	    		
		    	}
			}

			if (etcData.get("stepcd").equals("0") || etcData.get("stepcd").equals("3") || etcData.get("stepcd").equals("9") ){
				pstmt.setString(pstmtcount++, etcData.get("stDt"));
				pstmt.setString(pstmtcount++, etcData.get("edDt"));
			}

			if(!etcData.get("syscd").equals("00000")){
				pstmt.setString(pstmtcount++, etcData.get("syscd"));
			}
			if(!etcData.get("jobcd").equals("0000")){
				pstmt.setString(pstmtcount++, etcData.get("jobcd"));
			}
			if(!etcData.get("gbn").equals("ALL")){
				pstmt.setString(pstmtcount++, etcData.get("gbn"));
			}
			if(etcData.get("spms") !=null && etcData.get("spms")!=""){
				pstmt.setString(pstmtcount++, "%"+etcData.get("spms")+"%");
				pstmt.setString(pstmtcount++, "%"+etcData.get("spms")+"%");
				pstmt.setString(pstmtcount++, "%"+etcData.get("spms")+"%");
			}
			if (etcData.get("prgname") != null && etcData.get("prgname") != ""){
				pstmt.setString(pstmtcount++, "%"+etcData.get("prgname")+"%");
				pstmt.setString(pstmtcount++, "%"+etcData.get("prgname")+"%");
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){

				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("sysgbname",rs.getString("SysGb"));
				rst.put("cc_srid",rs.getString("cc_srid"));
				rst.put("genieid",rs.getString("cc_docid"));
				rst.put("cc_reqtitle", rs.getString("cc_reqtitle"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("procgbn", rs.getString("procgbn"));
				if (rs.getString("cr_sayu") != null) {
					rst.put("cr_sayu", rs.getString("cr_sayu"));
				}
				if (rs.getString("cr_closeyn").equals("Y")) {
					if (rs.getString("cr_qrycd").equals("03")) reqName = "테스트폐기";
					else if (rs.getString("cr_qrycd").equals("04")) reqName = "테스트폐기";//"운영폐기"; //운영반영시 주석해제
					else reqName = rs.getString("sin");
					if (rs.getString("cr_passok").equals("2")){ 
						rst.put("sin","[긴급]" + reqName);
					} else{
						rst.put("sin",reqName);
					}
				} else {
					if (rs.getString("cr_passok").equals("2")){ 
						rst.put("sin","[긴급]" + rs.getString("sin"));
					} else{
						rst.put("sin",rs.getString("sin"));
					}
				}
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				if (rs.getString("cr_status").equals("3"))
					rst.put("cm_codename", "반려");
				else if (rs.getString("cr_status").equals("9"))
					rst.put("cm_codename", "처리완료");
				else {
					strQuery2.setLength(0);
					strQuery2.append("select cr_teamcd,cr_confname from cmr9900  \n");
					strQuery2.append(" where cr_acptno= ? and cr_locat='00'      \n");
					pstmt2 = conn.prepareStatement(strQuery2.toString());
					pstmt2.setString(1,rs.getString("cr_acptno"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						if (rs2.getString("cr_teamcd").equals("1"))
						   rst.put("cm_codename", rs2.getString("cr_confname") + "중");
						else rst.put("cm_codename", rs2.getString("cr_confname") + "결재대기중");
					}
					rs2.close();
					pstmt2.close();
				}

				if (rs.getInt("subrowcnt") > 1){
					rst.put("rsrcnamememo", rs.getString("cr_rsrcname")+ "외 " + Integer.toString(rs.getInt("subrowcnt")-1) + "건" );
				}
				else{
					rst.put("rsrcnamememo", rs.getString("cr_rsrcname"));
				}
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_sysgb",rs.getString("cr_sysgb"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				if (rs.getString("prcdate") != null) rst.put("prcdate", rs.getString("prcdate"));
				if (rs.getString("cr_prcreq") != null)
					rst.put("prcreq", rs.getString("cr_prcreq").substring(0, 4) + "/" +
							rs.getString("cr_prcreq").substring(4, 6) + "/" +
							rs.getString("cr_prcreq").substring(6, 8) + " " +
							rs.getString("cr_prcreq").substring(8, 10) + ":" +
							rs.getString("cr_prcreq").substring(10, 12));


				strQuery2.setLength(0);
				strQuery2.append("select count(*) as cnt from cmr1010 where \n");
				strQuery2.append("cr_acptno= ? and \n");
				strQuery2.append("cr_confno is null \n");


				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1,rs.getString("cr_acptno"));

				rs2 = pstmt2.executeQuery();

				if (rs2.next()){
					subrowcnt = rs2.getInt("cnt");
				}
				rs2.close();
				pstmt2.close();

				if (subrowcnt > 0){
					rst.put("relno","1");
				}
				else{
					rst.put("relno","0");
				}

				
				rst.put("cr_editor",rs.getString("cr_editor")); //신청자 사번
				
				strQuery2.setLength(0);
				strQuery2.append("select cm_username		\n");
				strQuery2.append("from cmm0040				\n");
				strQuery2.append("where cm_userid = ? 		\n");

				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1,rs.getString("cr_editor"));

				rs2 = pstmt2.executeQuery();

				while (rs2.next()){
					rst.put("editor",rs2.getString("cm_username")); //신청자 이름
				}
				rs2.close();
				pstmt2.close();


				strQuery2.setLength(0);
				strQuery2.append("select count(*) as cnt from cmr1011 where \n");
				strQuery2.append("cr_acptno= ? and \n");
				strQuery2.append("cr_rstfile is not null \n");


				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1,rs.getString("cr_acptno"));

				rs2 = pstmt2.executeQuery();

				while (rs2.next()){
					subrowcnt = rs2.getInt("cnt");
				}
				rs2.close();
				pstmt2.close();

				if (subrowcnt > 0){
					rst.put("result","1");
				}
				else{
					rst.put("result","0");
				}

				rst.put("cr_syscd",rs.getString("cr_syscd"));

				if (rs.getString("prcdate") == null){

					strQuery2.setLength(0);
					strQuery2.append("select count(*) as cnt from cmr1010   \n");
					strQuery2.append(" where cr_acptno= ?                   \n");
					strQuery2.append("   and cr_putcode is not null         \n");
					strQuery2.append("   and nvl(cr_putcode,'0000')<>'0000' \n");
					strQuery2.append("   and nvl(cr_putcode,'RTRY')<>'RTRY' \n");


					pstmt2 = conn.prepareStatement(strQuery2.toString());

					pstmt2.setString(1,rs.getString("cr_acptno"));

					rs2 = pstmt2.executeQuery();
					subrowcnt = 0;
					while (rs2.next()){
						subrowcnt = subrowcnt + rs2.getInt("cnt");
					}
					rs2.close();
					pstmt2.close();

					if (subrowcnt == 0) {
						strQuery2.setLength(0);
						strQuery2.append("select count(*) as cnt from cmr1011      \n");
						strQuery2.append(" where cr_acptno= ?                      \n");
						strQuery2.append("   and cr_prcrst is not null             \n");
						strQuery2.append("   and(nvl(cr_prcrst,'0000')<>'0000'     \n");
						strQuery2.append("  or nvl(cr_wait,'0')='W')               \n");


						pstmt2 = conn.prepareStatement(strQuery2.toString());

						pstmt2.setString(1,rs.getString("cr_acptno"));

						rs2 = pstmt2.executeQuery();
						while (rs2.next()){
							subrowcnt = subrowcnt + rs2.getInt("cnt");
						}
						rs2.close();
						pstmt2.close();
					}
					if (subrowcnt > 0){
						rst.put("errflag","1");
					}
					else{
						rst.put("errflag","0");
					}
				}
				else{
					rst.put("errflag","0");
				}
				rst.put("cr_story",rs.getString("cr_story"));

				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr1100.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1100.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1100.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1100.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (strQuery2 != null) 	strQuery2 = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1100.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

}
