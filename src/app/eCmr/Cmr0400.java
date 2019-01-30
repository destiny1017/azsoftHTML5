/*****************************************************************************************
	1. program ID	: Cmr0400.java
	2. create date	: 2008.09.17
	3. auth		    : no name
	4. update date	: 2009.05.25
	5. auth		    : no name
	6. description	: Cmr0400 [문서관리] -> [반입의뢰]
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import app.common.AutoSeq;
import app.common.CodeInfo;
//import app.common.LoggableStatement;
import app.common.UserInfo;
/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr0400{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * [문서관리] -> [반입의뢰] : 반입의뢰 가능건 조회
	 * @param  HashMap
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] Sql_Qry_Out(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	  = null;
		Object[] 		  returnObject = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			int paramIndex = 0;
		    if (dataObj.get("ReqCd").equals("04")){
		        strQuery.append("select distinct a.cr_docid,a.cr_docfile,a.cr_status, \n");
		        strQuery.append("to_char(e.cr_acptdate,'yyyy-mm-dd hh24:mi') lastdt, \n");
		        strQuery.append("e.cr_sayu sayu,a.cr_lstver,c.cr_ccbyn,d.cm_codename,e.cr_acptno as acptno, \n");
		        strQuery.append("C.CR_UNITTIT UNITTIT,C.CR_PCDIR DIRPATH,c.CR_DOCSEQ,f.cm_username \n");
		        strQuery.append("from cmr0030 a,cmr0031 b,cmm0020 d,cmr1100 c,cmr1000 e,cmm0040 f,cmd0301 g \n");
		        strQuery.append("where e.cr_editor=? \n"); //UserId
		        strQuery.append("  and c.cr_confno is null \n");
		        strQuery.append("  and c.cr_acptno=e.cr_acptno \n");
		        strQuery.append("  and e.cr_qrycd='31' \n");
		        strQuery.append("  and c.cr_status in('8','9') \n");
		        strQuery.append("  and c.cr_prjno=? \n"); //PrjNo
		        strQuery.append("  and c.cr_docid=a.cr_docid \n");
		        if (dataObj.get("DocCd") != ""){
					if (dataObj.get("subnode_check").equals("true")){
						strQuery.append("and c.cr_docseq in ( SELECT CD_DOCSEQ FROM (SELECT * FROM CMD0303 	\n");
						strQuery.append("			                         		 WHERE CD_PRJNO = ?) 	\n");
						strQuery.append("   				  START WITH CD_UPDOCSEQ = ? 					\n");
						strQuery.append("					  CONNECT BY PRIOR CD_DOCSEQ = CD_UPDOCSEQ 		\n");
						strQuery.append("					  UNION 										\n");
						strQuery.append("                     SELECT ? FROM DUAL) 							\n");
					}
					else{
			            strQuery.append("  and b.cr_docseq=? \n"); //DocCd
					}
					strQuery.append("  and a.cr_docid =b.cr_docid \n");
		        }
		        strQuery.append("  and d.cm_macode='CMR0020' and d.cm_micode=a.cr_status \n");
		        strQuery.append("  and a.cr_lstver>0 and a.cr_status='5' and a.CR_EDITOR=f.cm_userid \n");
		        strQuery.append("  and c.cr_prjno=g.cd_prjno \n");
		        strQuery.append("  and a.cr_methcd=g.cd_methcd \n");
		        if (dataObj.get("Cd").equals("04")){//단계검색
			        strQuery.append("  and a.cr_DEVSTEP=(SELECT CM_micode FROM CMM0020 \n");
			        strQuery.append("     WHERE CM_MACODE='DEVSTEP' AND CM_CODENAME=?) \n");//Txt_Jawon
		        }else if (dataObj.get("Cd").equals("05")){
		        	strQuery.append("  and a.CR_DOCFILE like ? \n");//Txt_Jawon
		        }

		        strQuery.append("order by ACPTNO \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
		        //pstmt = new LoggableStatement(conn,strQuery.toString());

				pstmt.setString(++paramIndex, dataObj.get("UserId"));
				pstmt.setString(++paramIndex, dataObj.get("PrjNo"));

				if (dataObj.get("DocCd") != ""){
					if (dataObj.get("subnode_check").equals("true")){
						pstmt.setString(++paramIndex, dataObj.get("PrjNo"));
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
					}else{
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
					}
				}
				if (dataObj.get("Cd").equals("04")){
					pstmt.setString(++paramIndex, dataObj.get("Txt_Jawon"));
				}
				else if (dataObj.get("Cd").equals("05")){
					pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Jawon")+"%");
				}

		    }else{
		        strQuery.append("select distinct a.CR_DOCID,a.CR_DOCFILE,a.CR_STATUS, \n");
		        strQuery.append("to_char(a.cr_lastdt,'yyyy-mm-dd hh24:mi') lastdt, \n");
		        strQuery.append("'' sayu,a.CR_LSTVER,a.CR_CCBYN,d.cm_codename,'' as acptno, \n");
		        strQuery.append("'' AS UNITTIT,A.CR_DIRPATH DIRPATH,c.cr_docseq,e.cm_username \n");
		        strQuery.append("from cmr0030 a,cmd0301 b,cmr0031 c,cmm0020 d, cmm0040 e \n");
		        strQuery.append("where c.cr_prjno=? \n"); //PrjNo
		        strQuery.append("  and c.cr_docid=a.cr_docid \n");
		        if (dataObj.get("DocCd") != ""){
		        	if (dataObj.get("subnode_check").equals("true")){
						strQuery.append("and c.cr_docseq in ( SELECT CD_DOCSEQ FROM (SELECT * FROM CMD0303 	\n");
						strQuery.append("			                         		 WHERE CD_PRJNO = ?) 	\n");
						strQuery.append("   				  START WITH CD_UPDOCSEQ = ? 					\n");
						strQuery.append("					  CONNECT BY PRIOR CD_DOCSEQ = CD_UPDOCSEQ 		\n");
						strQuery.append("					  UNION 										\n");
						strQuery.append("                     SELECT ? FROM DUAL) 							\n");
		        	}else{
		        		strQuery.append("and c.cr_docseq=? \n"); //DocCd
		        	}
		        }
		        strQuery.append("  and d.cm_macode='CMR0020' and d.cm_micode=a.cr_status and a.CR_EDITOR=e.cm_userid \n");
		        if (dataObj.get("ReqCd").equals("03")){ //신규
	                strQuery.append("and a.cr_editor=? \n"); //UserId
	                strQuery.append("and a.cr_lstver=0 and a.cr_status='3' \n");
		        }else if(dataObj.get("ReqCd").equals("05")){ //삭제
		            strQuery.append("and a.cr_lstver>0 and a.cr_status='0' \n");
		        }
		        strQuery.append(" and c.cr_prjno=b.cd_prjno \n");
		        strQuery.append(" and a.cr_methcd=b.cd_methcd \n");
		        strQuery.append(" and a.cr_devstep=b.cd_devstep \n");
		        if (dataObj.get("Cd").equals("04")){//단계검색
			        strQuery.append("  and a.cr_DEVSTEP=(SELECT CM_micode FROM CMM0020 \n");
			        strQuery.append("     WHERE CM_MACODE='DEVSTEP' AND CM_CODENAME=?) \n");//Txt_Jawon
		        }else if (dataObj.get("Cd").equals("05")){
		        	strQuery.append("  and a.CR_DOCFILE like ? \n");//Txt_Jawon
		        }

		        pstmt = conn.prepareStatement(strQuery.toString());
		        //pstmt = new LoggableStatement(conn,strQuery.toString());

				pstmt.setString(++paramIndex, dataObj.get("PrjNo"));
				if (dataObj.get("DocCd") != ""){
					if (dataObj.get("subnode_check").equals("true")){
						pstmt.setString(++paramIndex, dataObj.get("PrjNo"));
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
					}else{
						pstmt.setString(++paramIndex, dataObj.get("DocCd"));
					}
				}
				if (dataObj.get("ReqCd").equals("03")){
					pstmt.setString(++paramIndex, dataObj.get("UserId"));
				}
				if (dataObj.get("Cd").equals("04")){
					pstmt.setString(++paramIndex, dataObj.get("Txt_Jawon"));
				}
				else if (dataObj.get("Cd").equals("05")){
					pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Jawon")+"%");
				}
		    }

		    ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("NO", Integer.toString(rs.getRow()));
	            rst.put("Acptno", rs.getString("Acptno"));
	            rst.put("cr_docfile", rs.getString("cr_docfile"));
	            rst.put("UNITTIT", rs.getString("UNITTIT"));
	            rst.put("sayu", rs.getString("sayu"));
	            rst.put("cm_username", rs.getString("cm_username"));
	            rst.put("LASTDT", rs.getString("LASTDT"));
	            rst.put("sta", rs.getString("cm_codename"));
	            rst.put("DIRPATH", rs.getString("DIRPATH"));
	            rst.put("cr_docid", rs.getString("cr_docid"));
	            rst.put("cr_docseq", rs.getString("cr_docseq"));
	            rst.put("cr_lstver", rs.getString("cr_lstver"));
	            rst.put("cr_ccbyn", rs.getString("cr_ccbyn"));
	            rst.put("cr_docfile", rs.getString("cr_docfile"));
	            rst.put("ReqCd", dataObj.get("ReqCd"));
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
			ecamsLogger.error("## Cmr0400.Sql_Qry_Out() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0400.Sql_Qry_Out() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0400.Sql_Qry_Out() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0400.Sql_Qry_Out() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObject != null)		returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0400.Sql_Qry_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_Out() method statement


	/**
	 * [문서관리] -> [반입의뢰] : 반입의뢰 결재 조회
	 * @param  HashMap
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String confSelect(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int               rsCnt       = 0;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_gubun,a.cm_rsrccd,a.cm_position        \n");
			strQuery.append("  from cmm0060 a,cmm0040 b                         \n");
			strQuery.append(" where a.cm_syscd=? 							    \n");
			strQuery.append("   and a.cm_reqcd=?                    			\n");
			strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid 	\n");
			strQuery.append("   and b.cm_userid=?                               \n");
			if (dataObj.get("CCB_YN").equals("Y")){
	        	strQuery.append(" and (a.cm_rsrccd is null or (a.cm_rsrccd is not null and a.cm_rsrccd='Y')) \n");
			}
	        else{
	        	strQuery.append(" and a.cm_rsrccd is null \n");
	        }
			strQuery.append(" and a.cm_gubun NOT IN ('1','4','P') \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, dataObj.get("SysCd"));
            pstmt.setString(2, dataObj.get("SinCd"));
            pstmt.setString(3, dataObj.get("UserId"));
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	++rsCnt;
				if (rs.getString("cm_gubun").equals("4")) {
					if (rs.getString("cm_position").indexOf("16")>=0) {
						rsCnt = 99;
						break;
					}
				}
            }
            rs.close();
            pstmt.close();
            conn.close();
            
            return Integer.toString(rsCnt);


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0400.confSelect() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0400.confSelect() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0400.confSelect() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0400.confSelect() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0400.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confSelect() method statement


	/**
	 * [문서관리] -> [반입의뢰] : 반입신청
	 * @param  ArrayList,HashMap,ArrayList
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String request_Check_In(ArrayList<HashMap<String,String>> chkInList,
				HashMap<String,String> dataObj,
				ArrayList<HashMap<String,Object>> gyulData) throws SQLException, Exception {
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
		HashMap<String,String>	rData = null;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			int i = 0;
			boolean UpDownYN = false;

			String UserId = dataObj.get("UserId");
			String PrjNo = dataObj.get("PrjNo");
			String PrjName = dataObj.get("PrjName");
			String SinCd = dataObj.get("SinCd");//반입코드 = 34
			String Sayu = dataObj.get("Sayu");
			String ChgCd = dataObj.get("ChgCd");
			String ChgTxt = dataObj.get("ChgTxt");
			String SysCd = dataObj.get("SysCd");
			String JobCd = dataObj.get("JobCd");
			String SysGb = dataObj.get("SysGb");
        	//String befAcptNo = dataObj.get("befAcptNo");
        	//String CCB_YN = dataObj.get("CCB_YN");

	        for (i=0;i<chkInList.size();i++){
	        	strQuery.setLength(0);
	        	strQuery.append("select cr_docfile,cr_status from cmr0030 ");
	        	strQuery.append("where cr_docid = ? ");//DocId
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, chkInList.get(i).get("cr_docid"));
	        	rs = pstmt.executeQuery();
	        	if (rs.next()){
	        		String retMsg = "";
        			switch(Integer.parseInt(chkInList.get(i).get("ReqCd"))){
        			case 3:
        				if (!rs.getString("cr_status").equals("3"))
        					retMsg = "[" + rs.getString("cr_docfile") + "]는 신규가능상태가 아닙니다.";break;
        			case 4:
        				if (!rs.getString("cr_status").equals("5"))
        					retMsg = "[" + rs.getString("cr_docfile") + "]는 반출상태가 아닙니다.";break;
        			case 5:
        				if (!rs.getString("cr_status").equals("0"))
        					retMsg = "[" + rs.getString("cr_docfile") + "]는 삭제가능상태가 아닙니다.";break;
        			}
	        		if (retMsg.length() >0 ) throw new Exception(retMsg);
	        	}
	        	rs.close();
	        	pstmt.close();
	        }

	        ////  신청번호    ////
	        AcptNo = autoseq.getSeqNo(conn,SinCd);
	        autoseq = null;

	        ////	insert Cmr1000 start
	        strQuery.setLength(0);
	        strQuery.append("select count(*) as cnt from cmr1000 ");
        	strQuery.append("where cr_acptno= ? ");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		if (rs.getInt("cnt")>0) throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
        	}
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("insert into cmr1000 ");
        	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD, ");
        	strQuery.append("CR_QRYCD,CR_PASSOK,CR_PASSCD,CR_EDITOR,CR_SAYU,CR_EMGCD,CR_DOCNO, ");
        	strQuery.append("CR_PRJNO,CR_PRJNAME) values ( ");
        	strQuery.append("?,?,?,?,sysdate,'0',?,?,'2',?,?,?,?,?,?,?) ");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, SysCd);
        	pstmt.setString(pstmtcount++, SysGb);
        	pstmt.setString(pstmtcount++, JobCd);

        	rData = (HashMap<String, String>) userInfo.getUserInfo(UserId)[0];
        	pstmt.setString(pstmtcount++, rData.get("teamcd"));
        	pstmt.setString(pstmtcount++, SinCd);
        	rData = null;

        	Object[] uInfo = codeInfo.getCodeInfo("REQUEST", "", "n");
        	for (i=0 ; i<uInfo.length ; i++){
        		rData = (HashMap<String, String>) uInfo[i];
        		if (rData.get("cm_micode").equals(SinCd)){
        			pstmt.setString(pstmtcount++, rData.get("cm_codename"));
        			rData = null;
        			break;
        		}
        		rData = null;
        	}
        	uInfo = null;

        	pstmt.setString(pstmtcount++, UserId);
        	pstmt.setString(pstmtcount++, Sayu);
        	pstmt.setString(pstmtcount++, ChgCd);
        	pstmt.setString(pstmtcount++, ChgTxt);
        	pstmt.setString(pstmtcount++, PrjNo);
        	pstmt.setString(pstmtcount++, PrjName);
        	pstmt.executeUpdate();
        	pstmt.close();
        	////	insert Cmr1000 end

        	////   	insert Cmr1100 start
        	for (i=0 ; i<chkInList.size() ; i++){
        		if (!chkInList.get(i).get("ReqCd").equals("05")) UpDownYN = true;

            	strQuery.setLength(0);
            	strQuery.append("insert into cmr1100 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,CR_VERSION,");
            	strQuery.append("CR_BASENO,CR_PCDIR,CR_EDITOR,CR_CCBYN,CR_UNITTIT,CR_DOCSEQ) values (");
            	strQuery.append("?,?,'0',?,?,?,?,?,?,?,?,?,?)");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, i+1);
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("ReqCd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_docid"));
            	pstmt.setString(pstmtcount++, PrjNo);

            	if (Integer.parseInt(chkInList.get(i).get("cr_lstver")) >= 99 && !chkInList.get(i).get("ReqCd").equals("05")){
            		pstmt.setInt(pstmtcount++, 1);
            	}else if(!chkInList.get(i).get("ReqCd").equals("05")){
            		pstmt.setInt(pstmtcount++, Integer.parseInt(chkInList.get(i).get("cr_lstver"))+1);
            	}else{
            		pstmt.setInt(pstmtcount++, Integer.parseInt(chkInList.get(i).get("cr_lstver")));
            	}

            	if (chkInList.get(i).get("Acptno") != null){
            		pstmt.setString(pstmtcount++, chkInList.get(i).get("Acptno"));
            	}else{
            		pstmt.setString(pstmtcount++, AcptNo);
            	}

            	pstmt.setString(pstmtcount++, chkInList.get(i).get("DIRPATH").replace("'", "''"));
            	pstmt.setString(pstmtcount++, UserId);
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_ccbyn"));
            	String UNITTIT = chkInList.get(i).get("UNITTIT");
            	if (UNITTIT != null){
            		pstmt.setString(pstmtcount++, UNITTIT.replace("'", "''"));
            	}
            	else{
            		pstmt.setString(pstmtcount++, UNITTIT);
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_docseq"));
            	pstmt.executeUpdate();
            	pstmt.close();


            	////	docfile 상태값 변경
            	strQuery.setLength(0);
            	strQuery.append("update cmr0030 set ");
                if (SinCd.equals("31")){//반출
                	strQuery.append("cr_status='4', ");
                }
                else if (SinCd.equals("34")){//반입
                   strQuery.append("cr_status='7', ");
                }
                else {//반출취소
                   strQuery.append("cr_status='6', ");
                }

                strQuery.append("cr_editor=? ");//UserId
                strQuery.append("where cr_docid=? ");//DocId
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, UserId);
            	pstmt.setString(2, chkInList.get(i).get("cr_docid"));
            	pstmt.executeUpdate();
            	pstmt.close();


            	////	반출 정보에 confno update
                if (chkInList.get(i).get("Acptno") != ""){
                	strQuery.setLength(0);
                	strQuery.append("update cmr1100 set cr_confno=? ");//AcptNo
                	strQuery.append("where cr_acptno=? ");//chkInList.get(i).get("Acptno")
                	strQuery.append("  and cr_docid=? ");//docid
                	pstmt = conn.prepareStatement(strQuery.toString());
                	pstmt.setString(1, AcptNo);
                	pstmt.setString(2, chkInList.get(i).get("Acptno"));
                	pstmt.setString(3, chkInList.get(i).get("cr_docid"));
                	pstmt.executeUpdate();
                	pstmt.close();
                }
        	}
			Cmr0200 cmr0200 = new Cmr0200();
			String retMsg = null;
        	if (gyulData.size() > 0) {
        		retMsg = cmr0200.request_Confirm(AcptNo,SysCd,SinCd,UserId,true,gyulData,conn);
        	} else {
        		retMsg = cmr0200.request_Confirm(AcptNo,SysCd,SinCd,UserId,false,gyulData,conn);
        	}
        	if (!retMsg.equals("OK")) {
        		AcptNo = "ERROR결재정보작성 중 오류가 발생하였습니다.";
        		conn.rollback();
        	} else {
        		conn.commit();
        	}
        	userInfo = null;
        	codeInfo = null;

        	// 폐기건 cr_errcd = '0000' 업데이트
			strQuery.setLength(0);
			strQuery.append("update cmr1100 set cr_errcd = '0000' \n");
			strQuery.append("where cr_acptno= ? and CR_QRYCD='05' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

        	if (UpDownYN){
        		conn.commit();
        		conn.close();
        		rs = null;
        		pstmt = null;
        		conn = null;
        		return AcptNo;
        	}else{
            	strQuery.setLength(0);
            	strQuery.append("Begin CMR9900_STR ( ");
            	strQuery.append("?, 'SYSDUP', 'eCAMS자동처리', '9', '34', '1' ); End;");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, AcptNo);
            	pstmt.executeUpdate();
            	pstmt.close();

            	conn.commit();
        		conn.close();
        		rs = null;
        		pstmt = null;
        		conn = null;
        		return "";
        	}

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0400.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0400.request_Check_In() SQLException END ##");
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
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0400.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0400.request_Check_In() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0400.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of request_Check_In() method statement

}//end of Cmr0400 class statement
