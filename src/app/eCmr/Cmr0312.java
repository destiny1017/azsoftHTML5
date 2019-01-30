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

public class Cmr0312 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getPrjNo(String UserId) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		 rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			UserInfo userinfo = new UserInfo();
			boolean adminYN = userinfo.isAdmin_conn(UserId, conn);
			userinfo = null;

			String PrjNo = "";
			strQuery.setLength(0);
			strQuery.append(" select cr_prjno from cmr1000	\n");
			strQuery.append(" where cr_editor= ?           	\n"); //UserId
			strQuery.append(" and cr_prjno is not null 		\n");
			strQuery.append(" order by cr_acptdate desc     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	//	    pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, UserId);
	//		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if(rs.next()){
				PrjNo = rs.getString("cr_prjno");
			}
			rs.close();
			pstmt.close();


	        strQuery.setLength(0);
			/*
	        strQuery.append(" select a.cr_prjno,a.cr_prjname from cmr1000 a,cmr1100 b 	\n");
		    strQuery.append(" where B.CR_EDITOR= ? 										\n");
			strQuery.append(" and B.CR_CONFNO IS NULL									\n");
			strQuery.append(" and b.cr_status NOT IN ('0','3')							\n");
			strQuery.append(" AND A.CR_QRYCD='31'										\n");
			strQuery.append(" AND A.CR_PRJNO IS NOT NULL								\n");
			strQuery.append(" AND A.CR_ACPTNO=B.CR_ACPTNO								\n");
			strQuery.append(" group by a.cr_prjno,a.cr_prjname							\n");
			strQuery.append(" order by a.cR_prjno										\n");
            */
            strQuery.append("select b.cd_prjno,b.cd_prjname from cmd0304 a, cmd0300 b \n");
            strQuery.append(" where a.CD_CLOSEDT is null \n");
            if (!adminYN){
            	strQuery.append("   and a.cd_prjuser= ? \n");
            }
            strQuery.append("   and b.CD_CLOSEDT is null \n");
            strQuery.append("   and a.cd_prjno=b.cd_prjno \n");
            strQuery.append(" group by b.cd_prjno,b.cd_prjname \n");
            strQuery.append(" order by b.cd_prjno \n");
            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
		    if (!adminYN){
		    	pstmt.setString(1, UserId);
		    }
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("cd_prjno", "00");
					rst.put("prjnoname", "선택하세요");
					rtList.add(rst);
					rst = null;
				}
				rst = new HashMap<String,String>();
				rst.put("cd_prjno", rs.getString("cd_prjno"));
				rst.put("cd_prjname", rs.getString("cd_prjname"));
				rst.put("prjnoname",rs.getString("cd_prjno")+"   "+ rs.getString("cd_prjname"));

				if (rs.getString("cd_prjno").equals(PrjNo)) rst.put("lastPrj", "Y");
				else rst.put("lastPrj", "N");

				rtList.add(rst);
				rst = null;
			}

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
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
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getFileList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			UserInfo userinfo = new UserInfo();
			boolean adminYN = userinfo.isAdmin_conn(etcData.get("UserID"), conn);
			userinfo = null;

	        strQuery.setLength(0);
			strQuery.append(" select a.cr_acptno,a.cr_docid, \n");
		    strQuery.append(" f.cr_prjno PRJNO,f.cr_prjname,a.cr_pcdir,b.cr_docfile, \n");
		    strQuery.append(" to_char(f.cr_acptdate,'yyyy-mm-dd')as acptdate \n");
		    strQuery.append(" from cmr1000 f, cmr0030 b, cmr1100 a \n");
		    strQuery.append(" where a.cr_confno is null	\n");
            if (!adminYN){
            	strQuery.append("   and a.cr_editor = ? \n");
            }
		    strQuery.append(" and a.cr_status not in('0','3') \n");
		    strQuery.append(" and a.cr_prcdate is not null \n");
		    strQuery.append(" and f.cr_qrycd='31' \n");//31 반출의뢰, 32 이전버전반출의뢰, 34 반입의뢰
		   	strQuery.append(" and f.cr_prjno= ? \n");
		   	strQuery.append(" and a.cr_acptno=f.cr_acptno	\n");
		    strQuery.append(" and a.cr_docid = b.cr_docid	\n");
		    strQuery.append(" and b.cr_docfile like ?		\n");
		    strQuery.append(" and b.cr_status='5'			\n");
		    strQuery.append(" order by a.cr_acptno desc		\n");

            pstmt = conn.prepareStatement(strQuery.toString());
         //   pstmt = new LoggableStatement(conn,strQuery.toString());
            int parametaCount=0;
		    if (!adminYN){
		    	pstmt.setString(++parametaCount, etcData.get("UserID"));
		    }
            pstmt.setString(++parametaCount, etcData.get("prjno"));
            pstmt.setString(++parametaCount, "%"+etcData.get("filename")+"%");
        //    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cr_acptno", rs.getString("cr_acptno"));//요청번호
				rst.put("cr_prjno",rs.getString("PRJNO"));
				rst.put("cr_prjname",rs.getString("cr_prjname"));
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("cr_prjnoname",rs.getString("PRJNO")+"   "+ rs.getString("cr_prjname"));//프로젝트
				rst.put("acptdate",rs.getString("acptdate"));			//요청일시
				rst.put("cr_docfile",rs.getString("cr_docfile"));		//산출물명
				rst.put("cr_pcdir",rs.getString("cr_pcdir"));			//반출위치
				rst.put("cr_docid",rs.getString("cr_docid"));
				rst.put("cr_qrycd",etcData.get("cr_qrycd"));
				rst.put("selected_flag","0");
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

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0312.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0312.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0312.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0312.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (etcData != null)	etcData = null;
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0312.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int request_Check_Out_Cancel(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData) throws SQLException, Exception {
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

	        for (i=0 ; i<chkOutList.size() ; i++){
	        	strQuery.setLength(0);
	        	strQuery.append("select cr_status from cmr0030 \n");
	        	strQuery.append("where cr_docid = ? \n");  //docid
	        	strQuery.append("and cr_status <> '5' \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    //pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(1, chkOutList.get(i).get("cr_docid"));
	        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	rs = pstmt.executeQuery();
	        	if (rs.next()){
	        		 throw new Exception(rs.getString("cr_rsrcname")+" 파일은 이미 취소가 됐거나 반출 취소가능상태가 아닙니다.");
	        	}
	        	rs.close();
	        	pstmt.close();
	        }

            //신청번호 만들기
	        AcptNo = autoseq.getSeqNo(conn,"39");
	        autoseq = null;

	        //CMR1000 에 같은 신청번호 있는지 확인
	        strQuery.setLength(0);
	        strQuery.append("select count(*) as acptnocnt from cmr1000 \n");
        	strQuery.append("where cr_acptno= ? \n");//AcptNo
        	pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		if (rs.getInt("acptnocnt") >0 ){
        			throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
        		}
        	}
        	rs.close();
        	pstmt.close();


        	//CMR1000 에 기본 정보 입력
            strQuery.setLength(0);
            strQuery.append( "insert into cmr1000 ");
            strQuery.append( "(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, ");
            strQuery.append( "CR_PASSOK,CR_PASSCD,cr_EDITOR,cr_sayu,cr_prjno,cr_prjname) values (  ");
            strQuery.append("?, 99999, 9, 99999, SYSDATE, '0', ?, ?, '0', ?,  ?,  ?,  ?,  ?) ");
            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(pstmtcount++, AcptNo);
            Object[] uInfo = userInfo.getUserInfo(etcData.get("UserID"));
            rData = (HashMap<String, String>) uInfo[0];
            pstmt.setString(pstmtcount++, rData.get("teamcd"));
            rData = null;
            uInfo = null;

            pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
            uInfo = codeInfo.getCodeInfo("REQUEST", "", "n");
            codeInfo = null;
        	for(i=0;i<uInfo.length;i++){
	        	rData = (HashMap<String, String>) uInfo[i];
	        	if (rData.get("cm_micode").equals(etcData.get("ReqCD"))){
	        		pstmt.setString(pstmtcount++, rData.get("cm_codename"));
	        		break;
	        	}
	        	rData = null;
        	}

        	uInfo = null;

			pstmt.setString(pstmtcount++, etcData.get("UserID"));
			pstmt.setString(pstmtcount++, rData.get("cm_codename"));

			rData = null;
			pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_prjno"));
			pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_prjname"));


			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			//CMR1100 입력
			for (i=0;i<chkOutList.size();i++){
				strQuery.setLength(0);
				strQuery.append( "insert into cmr1100 ");
				strQuery.append( "(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_BASECD,CR_PRJNO,");
				strQuery.append( "CR_VERSION,CR_BASENO,CR_PCDIR,CR_EDITOR, CR_ERRCD,CR_DOCSEQ)");
				strQuery.append( "(SELECT  ?, ? ,'0', ?,    ");
				strQuery.append( "CR_DOCID,CR_BASECD,CR_PRJNO,CR_VERSION,CR_ACPTNO,CR_PCDIR, ? ,'0000',CR_DOCSEQ ");
				strQuery.append( "FROM CMR1100 WHERE CR_ACPTNO=? AND CR_DOCID=?)");
				pstmt = conn.prepareStatement(strQuery.toString());
				//stmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setInt(pstmtcount++, i+1);
				pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_docid"));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();

				//취소건에 반출번호 set
				strQuery.setLength(0);
				strQuery.append("update cmr1100 set ");
				strQuery.append("cr_confno = ? ");
				strQuery.append("where cr_acptno= ? ");
				strQuery.append("and cr_docid= ? ");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_acptno"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_docid"));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}

			strQuery.setLength(0);
			strQuery.append("insert into cmr9900 ");
			strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD,CR_STATUS,CR_CONGBN, ");
			strQuery.append("CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR, CR_PRCSW) ");
			strQuery.append("(SELECT ?,1,lpad(CM_seqno,2,'0'),CM_NAME,CM_JOBCD,CM_GUBUN, ");
			strQuery.append("'0',CM_COMMON,CM_COMMON,CM_BLANK,CM_EMG,CM_HOLIDAY,CM_POSITION, CM_ORGSTEP,CM_JOBCD,CM_PRCSW ");
			strQuery.append("FROM cmm0060 ");
			strQuery.append("WHERE CM_SYSCD= ? ");
			strQuery.append("AND CM_REQCD= ? ");

			uInfo = userInfo.getUserInfo(etcData.get("UserID"));
			userInfo = null;
			rData = (HashMap<String, String>) uInfo[0];
			if (rData.get("cm_manid").equals("N")){
				strQuery.append("AND CM_MANID='2' ");
			}
			else{
				strQuery.append("AND CM_MANID='1' ");
			}
			rData = null;
			uInfo = null;

			strQuery.append("AND CM_GUBUN='1') ");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++, AcptNo);
			pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_syscd"));
			pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("insert into cmr9900 ");
			strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_STATUS,CR_CONFUSR) ");
			strQuery.append("values ( ");
			strQuery.append("?, '1', '00', '0', '9999' ) ");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++, AcptNo);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);

			strQuery.append("Begin CMR9900_STR ( ");
			strQuery.append("?, '000000', '', '9', ?, '1' ); End;");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++, AcptNo);
			pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			conn.commit();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			chkOutList.clear();
			etcData.clear();
			chkOutList = null;
			etcData = null;

			return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (chkOutList != null)	chkOutList = null;
			if (etcData != null)	etcData = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (chkOutList != null)	chkOutList = null;
			if (etcData != null)	etcData = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() Exception END ##");
			throw exception;
		}finally{
			if (chkOutList != null)	chkOutList = null;
			if (etcData != null)	etcData = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0312.request_Check_Out_Cancel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}