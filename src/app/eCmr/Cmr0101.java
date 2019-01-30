package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0101 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getFileList(HashMap<String,String> etcData) throws SQLException, Exception {
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

			UserInfo     userinfo = new UserInfo();
			boolean adminYn = userinfo.isAdmin_conn(etcData.get("UserID"),conn);
			userinfo = null;
			boolean srSw = false;
			
			if (etcData.get("srid") != null && etcData.get("srid") != "") srSw = true;

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_rsrcname,a.cr_langcd,a.cr_rsrccd,a.cr_jobcd, \n");
			strQuery.append("       a.cr_dsncd,a.cr_syscd,a.cr_itemid, a.cr_editor,a.cr_qrycd,    \n");
			strQuery.append("       to_char(f.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,      \n");
			strQuery.append("       b.cr_story,c.cm_dirpath,e.cm_codename as JAWON,               \n");
			strQuery.append("       d.cm_info,g.cm_jobname,b.cr_status,b.cr_lstver,               \n");
			strQuery.append("       nvl(f.cr_eclipse,'R') eclipse, a.cr_story                     \n");
			strQuery.append("  from cmr1000 f,                                                    \n");
			strQuery.append("      (select cm_micode,cm_codename from cmm0020 where cm_macode='JAWON') e, \n");
			strQuery.append("       cmm0070 c,cmr0020 b,cmr1010 a,cmm0036 d,cmm0102 g             \n");
			strQuery.append(" where b.cr_syscd=?                                                  \n");
			if (srSw) strQuery.append("and b.cr_isrid=?                                           \n");
			strQuery.append("   and b.cr_status in ('5','B','D')							      \n");
			strQuery.append("   and decode(b.cr_status,'5',b.cr_ckoutacpt,b.cr_devacpt)=f.cr_acptno \n");
			strQuery.append("   and f.cr_acptno=a.cr_acptno   							          \n");
			strQuery.append("   and b.cr_itemid=a.cr_itemid   							          \n");
			if (etcData.get("txtProg") != "" && etcData.get("txtProg") != null){
				strQuery.append(" and (b.cr_rsrcname like ?	or b.cr_story like ?)		    		\n");
				//strQuery.append(" and upper(b.cr_rsrcname) like upper(?)			  				\n"); //2013.11.13 일단 upper 제외
			}
			strQuery.append("   and a.cr_itemid=a.cr_baseitem                                     \n");
			strQuery.append("   and a.cr_status<>'3' and a.cr_prcdate is not null                 \n");
			if (!adminYn){
				strQuery.append(" and a.cr_editor=? \n");
			}
			strQuery.append("   and a.cr_acptno=f.cr_acptno                                       \n");			
			strQuery.append("   and f.cr_status not in ('0','3')                                  \n");
			strQuery.append("   and b.cr_syscd = c.cm_syscd and b.cr_dsncd = c.cm_dsncd           \n");
			strQuery.append("   and b.cr_syscd = d.cm_syscd and b.cr_rsrccd = d.cm_rsrccd         \n");
			strQuery.append("   and a.cr_rsrccd = e.cm_micode                                     \n");
			strQuery.append("   and a.cr_jobcd = g.cm_jobcd                                       \n");
			strQuery.append(" order by acptdate desc,a.cr_rsrcname,a.cr_acptno desc \n");

            //pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(pstmtcount++, etcData.get("syscd"));
			if (srSw) pstmt.setString(pstmtcount++, etcData.get("srid"));
			if (etcData.get("txtProg") != "" && etcData.get("txtProg") != null){
				pstmt.setString(pstmtcount++, "%"+etcData.get("txtProg")+"%");
				pstmt.setString(pstmtcount++, "%"+etcData.get("txtProg")+"%");
			}
			if (!adminYn){
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
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
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("baseitemid",rs.getString("cr_itemid"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("eclipse",rs.getString("eclipse"));
				//rst.put("cr_isrid",rs.getString("cr_isrid"));
				rst.put("selected_flag","0");
				rst.put("cr_story",rs.getString("cr_Story"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			etcData.clear();
			etcData = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0101.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0101.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0101.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0101.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0101.getFileList() connection release exception ##");
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
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("cm_dirpath",fileList.get(i).get("cm_dirpath"));
				rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
				rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
				rst.put("jawon", fileList.get(i).get("jawon"));
				rst.put("sysgb", fileList.get(i).get("sysgb"));
				rst.put("cr_langcd",fileList.get(i).get("cr_langcd"));
				rst.put("cr_rsrccd",fileList.get(i).get("cr_rsrccd"));
				rst.put("cr_dsncd",fileList.get(i).get("cr_dsncd"));
				rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
				rst.put("cr_qrycd",fileList.get(i).get("cr_qrycd"));
				rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
				rst.put("acptno", fileList.get(i).get("acptno"));
				rst.put("cr_acptno", fileList.get(i).get("cr_acptno"));
				rst.put("cr_itemid",fileList.get(i).get("cr_itemid"));
				rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
				rst.put("cm_info",fileList.get(i).get("cm_info"));
				rst.put("cr_editor",fileList.get(i).get("cr_editor"));
				rst.put("cr_status",fileList.get(i).get("cr_status"));
				rst.put("cr_lstver",fileList.get(i).get("cr_lstver"));
				rst.put("cr_story",fileList.get(i).get("cr_story"));
				rtList.add(rst);
				rst = null;

				strQuery.setLength(0);
				strQuery.append("select distinct b.cr_rsrcname,b.cr_langcd,b.cr_rsrccd,\n");
				strQuery.append("       b.cr_jobcd,b.cr_dsncd,b.cr_syscd,b.cr_itemid,  \n");
				strQuery.append("       b.cr_editor,b.cr_baseno,b.cr_sysgb,b.cr_qrycd, \n");
				strQuery.append("       c.cm_dirpath,d.cm_codename as jawon,e.cm_info, \n");
				strQuery.append("       f.cm_jobname,a.cr_status,a.cr_lstver,b.cr_story \n");
				strQuery.append("  from cmr0020 a,cmr1010 b,cmm0070 c,cmm0020 d,       \n");
				strQuery.append("       cmm0036 e,cmm0102 f                            \n");
				strQuery.append(" where b.cr_acptno= ?                                 \n");
				strQuery.append("   and b.cr_status<>'3'                               \n");
				strQuery.append("   and instr(nvl(b.cr_basepgm,b.cr_baseitem),?)>0     \n");
				strQuery.append("   and b.cr_itemid<> ?                                \n");
				strQuery.append("   and b.cr_itemid=a.cr_itemid                        \n");
				strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd   \n");
				strQuery.append("   and a.cr_syscd=e.cm_syscd and a.cr_rsrccd=e.cm_rsrccd \n");
				strQuery.append("   and a.cr_jobcd=f.cm_jobcd                             \n");
				strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=a.cr_rsrccd   \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, fileList.get(i).get("cr_acptno"));
	            pstmt.setString(2, fileList.get(i).get("cr_itemid"));
	            pstmt.setString(3, fileList.get(i).get("cr_itemid"));
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cm_dirpath",rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
					rst.put("cm_jobname", rs.getString("cm_jobname"));
					rst.put("jawon", rs.getString("jawon"));
					rst.put("sysgb", fileList.get(i).get("sysgb"));
					rst.put("cr_langcd",rs.getString("cr_langcd"));
					rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
					rst.put("cr_dsncd",rs.getString("cr_dsncd"));
					rst.put("cr_jobcd",rs.getString("cr_jobcd"));
					rst.put("cr_qrycd",rs.getString("cr_qrycd"));
					rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
					rst.put("cr_itemid",rs.getString("cr_itemid"));
					rst.put("acptno", fileList.get(i).get("cr_acptno").substring(0,4)+"-"+
							fileList.get(i).get("cr_acptno").substring(4,6)+"-"+
							fileList.get(i).get("cr_acptno").substring(6));
					rst.put("cr_acptno", fileList.get(i).get("cr_acptno"));
					rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
					rst.put("cm_info",rs.getString("cm_info"));
					rst.put("cr_editor",rs.getString("cr_editor"));
					rst.put("cr_status",rs.getString("cr_status"));
					rst.put("cr_lstver",rs.getString("cr_lstver"));
					rst.put("cr_story",rs.getString("cr_story"));
					rtList.add(rst);
					rst = null;
				}//end of while-loop statement
				rs.close();
				pstmt.close();
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			fileList.clear();
			fileList = null;

			return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0101.getDownFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0101.getDownFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0101.getDownFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0101.getDownFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0101.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_Check_Out_Cancel(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData,ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		String			  AcptNo	  = null;
		int				  i;

		try {
			conn = connectionContext.getConnection();

			conn.setAutoCommit(false);

	        for (i=0;i<chkOutList.size();i++){
	        	if (chkOutList.get(i).get("cr_itemid").equals(chkOutList.get(i).get("baseitemid"))) {
		        	strQuery.setLength(0);
		        	strQuery.append("select cr_rsrcname from cmr0020 \n");
		        	strQuery.append("where cr_itemid = ? \n");
		        	strQuery.append("and cr_status<>?    \n");

		        	pstmt = conn.prepareStatement(strQuery.toString());

		        	pstmt.setString(1, chkOutList.get(i).get("cr_itemid"));
		        	pstmt.setString(2, chkOutList.get(i).get("cr_status"));

		        	rs = pstmt.executeQuery();

		        	while (rs.next()){
		        		throw new Exception(rs.getString("cr_rsrcname")+" 파일은 이미 취소가 됐거나 취소가능상태가 아닙니다. ");//CHECK-OUT 취소가능상태가 아닙니다. ");
		        	}

		        	rs.close();
		        	pstmt.close();
	        	}
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

        	if (i>0){
        		throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
        	}

        	rs.close();
        	pstmt.close();

            strQuery.setLength(0);
        	strQuery.append("insert into cmr1000 ");
        	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,\n");
        	strQuery.append(" CR_STATUS,CR_TEAMCD,CR_QRYCD,CR_PASSOK,          \n");
        	strQuery.append(" CR_PASSCD,CR_EDITOR,CR_ITSMID,CR_ITSMTITLE,CR_ECLIPSE) \n");
        	strQuery.append("(select ?, ?, ?, ?, sysdate, '0', cm_project,     \n");
        	strQuery.append("        ?, '0', ?, cm_userid, ?,?, ?              \n");
        	strQuery.append("   from cmm0040                                   \n");
        	strQuery.append("  where cm_userid=?)                              \n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_syscd"));
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("sysgb"));
        	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_jobcd"));
        	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
        	pstmt.setString(pstmtcount++, etcData.get("sayu"));
        	pstmt.setString(pstmtcount++, etcData.get("srid"));
        	pstmt.setString(pstmtcount++, etcData.get("cc_reqtitle"));
        	pstmt.setString(pstmtcount++, etcData.get("eclipse"));
        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        	pstmt.executeUpdate();
        	pstmt.close();


        	for (i=0;i<chkOutList.size();i++){
            	strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD, \n");
            	strQuery.append("CR_RSRCCD,CR_LANGCD,CR_DSNCD,CR_RSRCNAME,CR_RSRCNAM2,CR_VERSION,   \n");
            	strQuery.append("CR_EDITOR,CR_CONFNO,CR_EDITCON,CR_BASENO,CR_BASEITEM,CR_ITEMID,CR_STORY)    \n");
            	strQuery.append("values (?,?,?,?,?,'0',?,   ?,?,?,?,?,?,   ?,?,?,?,?,?,?)             \n");

            	pstmt = conn.prepareStatement(strQuery.toString());

            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, i+1);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("sysgb"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_jobcd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_qrycd"));


            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_langcd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_lstver"));


            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
            	pstmt.setString(pstmtcount++, etcData.get("sayu"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("baseitemid"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_story"));
            	pstmt.executeUpdate();
            	pstmt.close();

            	strQuery.setLength(0);
            	strQuery.append("update cmr0020 set cr_savesta=cr_status, \n");
            	strQuery.append("       cr_status='6',             \n");
            	strQuery.append("       cr_editor=?,               \n");
            	strQuery.append("       cr_lastdate=SYSDATE        \n");
            	strQuery.append(" where cr_itemid=?                \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));
            	pstmt.executeUpdate();
            	pstmt.close();


            	strQuery.setLength(0);
            	strQuery.append("update cmr1010 set cr_confno=?  \n");
            	strQuery.append(" where cr_itemid= ?             \n");
            	strQuery.append("   and substr(cr_acptno,5,2) in ('01','02','03','07') \n");
            	strQuery.append("   and cr_confno is null        \n");
            	//테스트적용취소 일때 테스트적용 신청 건만 confno 셋팅
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
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
			}


        	conn.commit();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

        	chkOutList.clear();
        	chkOutList = null;
        	etcData.clear();
        	etcData = null;

        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0101.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}
