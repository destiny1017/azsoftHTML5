package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.CodeInfo;
//import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0211 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getFileList(HashMap<String,String> etcData) throws SQLException, Exception {
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

			//UserInfo     userinfo = new UserInfo();
			boolean adminYn = false;//userinfo.isAdmin_conn(etcData.get("UserID"),conn);
			//userinfo = null;

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_rsrcname,a.cr_langcd,a.cr_rsrccd,a.cr_jobcd, \n");
			strQuery.append("       a.cr_dsncd,a.cr_syscd,a.cr_itemid, a.cr_editor,               \n");
			strQuery.append("       to_char(f.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,      \n");
			strQuery.append("       f.cr_prjno,b.cr_story,c.cm_dirpath,e.cm_codename as JAWON,    \n");
			strQuery.append("       d.cm_info,g.cm_jobname,b.cr_status                            \n");
			strQuery.append("  from cmr1000 f,                                                    \n");
			strQuery.append("      (select cm_micode,cm_codename from cmm0020 where cm_macode='JAWON') e, \n");
			strQuery.append("       cmm0070 c,cmr0020 b,cmr1010 a,cmm0036 d,cmm0102 g             \n");
			strQuery.append(" where a.cr_confno is null                                           \n");
			//strQuery.append("   and a.cr_syscd = ?                                                \n");
			strQuery.append("   and a.cr_syscd = ? and a.cr_baseno is null                        \n");
			strQuery.append("   and a.cr_status not in('0','3') and a.cr_prcdate is not null      \n");
			if (etcData.get("txtProg") != "" && etcData.get("txtProg") != null){
				strQuery.append(" and upper(a.cr_rsrcname) like upper('%' || ? || '%')			  \n");
			}
			if (!adminYn){
				strQuery.append(" and a.cr_editor=? \n");
			}
			strQuery.append("   and f.cr_qrycd='03'                                               \n");
			strQuery.append("   and a.cr_itemid=a.cr_baseitem and a.cr_itemid = b.cr_itemid       \n");
			strQuery.append("   and b.cr_status='B' and b.cr_lstver=0                             \n");
			strQuery.append("   and a.cr_syscd = c.cm_syscd and a.cr_dsncd = c.cm_dsncd           \n");
			strQuery.append("   and a.cr_syscd = d.cm_syscd and a.cr_rsrccd = d.cm_rsrccd         \n");
			strQuery.append("   and a.cr_rsrccd = e.cm_micode                                     \n");
			strQuery.append("   and a.cr_acptno = f.cr_acptno                                     \n");
			strQuery.append("   and a.cr_jobcd = g.cm_jobcd                                       \n");
			strQuery.append(" order by a.cr_rsrcname, a.cr_acptno desc                            \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

			pstmt.setString(pstmtcount++, etcData.get("syscd"));
			if (etcData.get("txtProg") != "" && etcData.get("txtProg") != null){
				pstmt.setString(pstmtcount++, "%"+etcData.get("txtProg")+"%");
			}
			if (!adminYn){
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
			}

			//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("sysgb",etcData.get("sysgb"));
				rst.put("cr_langcd",rs.getString("cr_langcd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_syscd",etcData.get("syscd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("selected_flag","0");
				rtList.add(rst);
				rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			etcData.clear();

			rs = null;
			pstmt = null;
			conn = null;
			etcData = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0211.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0211.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0211.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0211.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr0211.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getDownFileList(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		rtList.clear();

		try {

			conn = connectionContext.getConnection();

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("cm_dirpath",fileList.get(i).get("cm_dirpath"));
				rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
				rst.put("jobname", fileList.get(i).get("cm_jobname"));
				rst.put("jawon", fileList.get(i).get("jawon"));
				rst.put("sysgb", fileList.get(i).get("sysgb"));
				rst.put("cr_langcd",fileList.get(i).get("cr_langcd"));
				rst.put("cr_rsrccd",fileList.get(i).get("cr_rsrccd"));
				rst.put("cr_dsncd",fileList.get(i).get("cr_dsncd"));
				rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
				rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
				rst.put("acptno", fileList.get(i).get("acptno"));
				rst.put("cr_acptno", fileList.get(i).get("cr_acptno"));
				rst.put("cr_itemid",fileList.get(i).get("cr_itemid"));
				rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
				rst.put("cm_info",fileList.get(i).get("cm_info"));
				rst.put("cr_editor",fileList.get(i).get("cr_editor"));
				rtList.add(rst);
				rst = null;

				strQuery.setLength(0);
				strQuery.append("select b.cr_rsrcname,b.cr_langcd,b.cr_rsrccd,b.cr_jobcd,b.cr_dsncd,b.cr_syscd, \n");
				strQuery.append("b.cr_itemid,b.cr_editor,b.cr_acptno,b.cr_sysgb,c.cm_dirpath,d.cm_codename as jawon,e.cm_info,f.cm_jobname \n");
				strQuery.append("from cmr0020 a,cmr1010 b,cmm0070 c,cmm0020 d ,cmm0036 e ,cmm0102 f\n");
				strQuery.append("where b.cr_acptno= ? \n");
				strQuery.append("and b.cr_baseitem= ? \n");
				strQuery.append("and b.cr_itemid<> ? \n");
				strQuery.append("and b.cr_itemid=a.cr_itemid \n");
				strQuery.append("and a.cr_syscd=c.cm_syscd \n");
				strQuery.append("and a.cr_dsncd=c.cm_dsncd \n");
				strQuery.append("and a.cr_syscd=e.cm_syscd \n");
				strQuery.append("and a.cr_rsrccd=e.cm_rsrccd \n");
				strQuery.append("and a.cr_jobcd=f.cm_jobcd \n");
				strQuery.append("and d.cm_macode='JAWON' \n");
				strQuery.append("and d.cm_micode=a.cr_rsrccd \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());

	            pstmt.setString(1, fileList.get(i).get("cr_acptno"));
	            pstmt.setString(2, fileList.get(i).get("cr_itemid"));
	            pstmt.setString(3, fileList.get(i).get("cr_itemid"));

	            rs = pstmt.executeQuery();


				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cm_dirpath",rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
					rst.put("jobname", rs.getString("cm_jobname"));
					rst.put("jawon", rs.getString("jawon"));
					rst.put("sysgb", fileList.get(i).get("sysgb"));
					rst.put("cr_langcd",rs.getString("cr_langcd"));
					rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
					rst.put("cr_dsncd",rs.getString("cr_dsncd"));
					rst.put("cr_jobcd",rs.getString("cr_jobcd"));
					rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
					rst.put("cr_itemid",rs.getString("cr_itemid"));
					rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
					rst.put("cr_acptno", rs.getString("cr_acptno"));
					rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
					rst.put("cm_info",rs.getString("cm_info"));
					rst.put("cr_editor",rs.getString("cr_editor"));
					rtList.add(rst);
					rst = null;

				}//end of while-loop statement

				rs.close();
				pstmt.close();

			}

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			fileList.clear();
			fileList = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0211.getDownFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0211.getDownFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0211.getDownFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0211.getDownFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr0211.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int request_Check_Out_Cancel(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData,ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		CodeInfo		  codeInfo	  = new CodeInfo();
		String			  AcptNo	  = null;
		int				  i;
		HashMap<String,String>	rData = null;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

	        for (i=0;i<chkOutList.size();i++){
	        	strQuery.setLength(0);
	        	strQuery.append("select cr_status,cr_editor,cr_story,cr_rsrcname from cmr0020 \n");
	        	strQuery.append("where cr_itemid = ? \n");
	        	strQuery.append("and cr_status <> 'B' \n");

	        	pstmt = conn.prepareStatement(strQuery.toString());

	        	pstmt.setString(1, chkOutList.get(i).get("cr_itemid"));

	        	rs = pstmt.executeQuery();

	        	while (rs.next()){
	        		if (chkOutList.get(i).get("cr_itemid").equals(chkOutList.get(i).get("cr_baseitem")))
	        			throw new Exception(rs.getString("cr_rsrcname")+" 파일은 이미 취소가 됐거나 CHECK-OUT 취소가능상태가 아닙니다. ");
	        	}
	        	rs.close();
	        	pstmt.close();
	        }

	        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));
	        autoseq = null;

	        strQuery.setLength(0);

	        strQuery.append("select count(*) as acptnocnt from cmr1000 \n");
        	strQuery.append("where cr_acptno= ? \n");


        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);

        	rs = pstmt.executeQuery();

        	while (rs.next()){
        		i = rs.getInt("acptnocnt");
        	}
        	rs.close();
        	pstmt.close();



        	if (i>0){
        		throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
        	}


            strQuery.setLength(0);
        	strQuery.append("insert into cmr1000 ");
        	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, ");
        	strQuery.append("CR_PASSOK,CR_PASSCD,CR_EDITOR,CR_ISRID,CR_ISRSUB) values ( ");
        	strQuery.append("?, ?, ?, ?, sysdate, '0', ?, ?, '0', ?, ?, ?, ? ) ");



        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_syscd"));
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("sysgb"));
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_jobcd"));


        	Object[] uInfo = userInfo.getUserInfo(etcData.get("UserID"));
        	rData = (HashMap<String, String>) uInfo[0];
        	pstmt.setString(pstmtcount++, rData.get("teamcd"));
        	rData = null;
        	uInfo = null;

        	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));

        	uInfo = codeInfo.getCodeInfo("REQUEST", "", "n");
        	codeInfo = null;
        	for (i=0;i<uInfo.length;i++){
        		rData = (HashMap<String, String>) uInfo[i];
        		if (rData.get("cm_micode").equals(etcData.get("ReqCD"))){
        			pstmt.setString(pstmtcount++, rData.get("cm_codename"));
        			rData = null;
        			break;
        		}
        		rData = null;
        	}
        	uInfo = null;

        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        	pstmt.setString(pstmtcount++, etcData.get("isrid"));
        	pstmt.setString(pstmtcount++, etcData.get("isrsub"));

        	pstmt.executeUpdate();

        	pstmt.close();

        	for (i=0;i<chkOutList.size();i++){
            	strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD, ");
            	strQuery.append("CR_RSRCCD,CR_LANGCD,CR_DSNCD,CR_RSRCNAME,CR_RSRCNAM2, ");
            	strQuery.append("CR_EDITOR,CR_CONFNO,CR_EDITCON,CR_BASENO,CR_BASEITEM,CR_ITEMID) values ( ");
            	strQuery.append("?, ?, ?, ?, ?, '0', ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, i+1);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("sysgb"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_jobcd"));
            	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_langcd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_editor"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("baseitemid"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));

            	pstmt.executeUpdate();
            	pstmt.close();

            	strQuery.setLength(0);
            	strQuery.append("update cmr0020 set ");
            	strQuery.append("cr_status='6', ");
            	strQuery.append("cr_editor= ? ");
            	strQuery.append("where cr_itemid= ? and cr_status='B' ");

            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));

            	pstmt.executeUpdate();
            	pstmt.close();

            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set ");
            	strQuery.append("cr_confno= ?, ");
            	strQuery.append("cr_cnclyn= 'Y' ");
            	strQuery.append("where cr_acptno= ? ");
            	strQuery.append("and   cr_itemid= ? ");
            	strQuery.append("and   CR_CONFNO IS NULL");

            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));

            	pstmt.executeUpdate();
            	pstmt.close();
        	}
        	Cmr0200 cmr0200 = new Cmr0200();
        	String retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}  else {
				if (etcData.get("ReqCD").equals("12") && etcData.get("isrid") != null && etcData.get("isrid") != "") {
					strQuery.setLength(0);
					strQuery.append("update cmc0110                    \n");
					strQuery.append("   set cc_substatus='23',         \n");
					strQuery.append("       cc_mainstatus='02'         \n");
					strQuery.append(" where cc_isrid=? and cc_isrsub=? \n");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmtcount = 1;
	            	pstmt.setString(pstmtcount++, AcptNo);
	            	pstmt.setString(pstmtcount++, etcData.get("isrid"));
	            	pstmt.setString(pstmtcount++, etcData.get("isrsub"));

	            	pstmt.executeUpdate();
	            	pstmt.close();
				}
        	}
        	conn.commit();
        	conn.close();
			conn = null;
			pstmt = null;
			rs = null;

        	chkOutList.clear();
        	chkOutList = null;
        	etcData.clear();
        	etcData = null;

        	return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() SQLException END ##");
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
					ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0211.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}
