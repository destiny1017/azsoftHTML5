
/*****************************************************************************************
	1. program ID	: eCmd0400.java
	2. create date	: 2006.08. 08
	3. auth		    : NoName
	4. update date	:
	5. auth		    :
	6. description	: eCmd0400 [문서관리]->[프로젝트정보]
*****************************************************************************************/

package app.eCmd;

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
//import app.common.UserInfo;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Cmd0400{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */


    public Object[] Cbo_DevStep_Set(String PrjNo) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("select b.cm_micode,b.cm_codename from cmd0301 a, cmm0020 b \n");
		    strQuery.append("where a.cd_prjno=? \n");//PrjNo
		    strQuery.append("  and b.cm_micode<>'****' and b.cm_closedt is null \n");
		    strQuery.append("  and b.cm_macode='DEVSTEP' and a.CD_DEVSTEP=b.cm_micode \n");
		    strQuery.append(" order by b.cm_micode \n");

            pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, PrjNo);

            rs = pstmt.executeQuery();

            rsval.clear();
			rst = new HashMap<String, String>();
			rst.put("cm_codename", "전체");
			rst.put("cm_micode", "00");
			rst.put("cm_macode", "DEVSTEP");
			rsval.add(rst);
			rst = null;
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_macode", "DEVSTEP");
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;


    		return rsval.toArray();

		} catch (SQLException sqlexception){
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Cbo_DevStep_Set() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Cbo_DevStep_Set() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Cbo_DevStep_Set() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Cbo_DevStep_Set() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Cbo_DevStep_Set() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_Hist() method statement

    public Object[] Sql_Qry_Hist(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		String PrjNo = dataObj.get("PrjNo");
		String Cbo_ReqCd = dataObj.get("Cbo_ReqCd");
		int Cbo_Cond = Integer.parseInt(dataObj.get("Cbo_Cond"));
		String Txt_Cond = dataObj.get("Txt_Cond");

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("select a.cr_acptno,a.cr_docid,a.cr_errcd,a.cr_version,a.cr_qrycd,a.cr_unittit,        ");
		    strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') cr_prcdate,   ");
		    strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi') cr_acptdate, ");
    	    strQuery.append("       b.cm_username,c.cr_sayu,c.cr_emgcd,c.cr_docno,           ");
    	    strQuery.append("       c.cr_qrycd qrycd,d.cm_codename,f.cr_docfile,f.cr_devstep ");
    	    strQuery.append("from cmr0030 f,cmm0020 d,cmr1100 a,cmm0040 b,cmr1000 c          ");
    	    strQuery.append("where c.cr_prjno=? "); //PrjNo
    	    if (!Cbo_ReqCd.equals("00"))//Cbo_ReqCd 전체가 아니면
    	       strQuery.append("and c.cr_qrycd=? ");//Cbo_ReqCd
    	    if (Cbo_Cond > 0){
    	    	switch(Cbo_Cond){
    	    	case 2://요청번호
    	    		strQuery.append("and c.cr_acptno like ? ");// % Replace(Txt_Cond, "-", "") %
    	    		break;
    	    	case 3://요청사유
    	            strQuery.append("and c.cr_sayu like ? ");//%Txt_Cond%
    	            break;
    	        case 4://요청자
    	            strQuery.append("and (c.cr_editor like ? or ");//%Txt_Cond%
    	            strQuery.append("     b.cm_username like ?) ");//%Txt_Cond%
    	            break;
    	        case 5:
    	        	break;
    	        case 6://변경근거
    	        	strQuery.append("and c.cr_docno is not null ");
    	        	strQuery.append("and c.cr_docno like ? ");//%Txt_Cond%
    	        	break;
    	       }
    	    }
    	    strQuery.append("  and c.cr_acptno=a.cr_acptno ");
    	    strQuery.append("  and a.cr_editor=b.cm_userid ");
    	    strQuery.append("  and a.cr_docid=f.cr_docid ");
    	    strQuery.append("  and d.cm_macode='REQUEST' and d.cm_micode=c.cr_qrycd ");
    	    strQuery.append("order by c.cr_acptdate desc ");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());

            int paramIndex = 0;
            pstmt.setString(++paramIndex, PrjNo);
            if (!Cbo_ReqCd.equals("00"))//Cbo_ReqCd 전체가 아니면
            	pstmt.setString(++paramIndex, Cbo_ReqCd);
    	    if (Cbo_Cond > 0){
    	    	switch(Cbo_Cond){
    	    	case 2://요청번호
    	    		pstmt.setString(++paramIndex,"%"+Txt_Cond.replace("-", "")+"%");
    	    		break;
    	    	case 3://요청사유
    	    	case 6://변경근거
    	            pstmt.setString(++paramIndex,"%"+Txt_Cond+"%");
    	            break;
    	        case 4://요청자
    	        	pstmt.setString(++paramIndex,"%"+Txt_Cond+"%");
    	        	pstmt.setString(++paramIndex,"%"+Txt_Cond+"%");
    	            break;
    	       }
    	    }

           	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
            String Tmp = "";
            String SubItems1 = "";
            String CodeName_Tmp1 = "";
            String CodeName_Tmp2 = "";
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("NO", Integer.toString(rs.getRow()));

            	SubItems1 = rs.getString("cm_codename");
                if (!rs.getString("qrycd").equals("31")){
                    if (!Tmp.equals(rs.getString("cr_qrycd"))){
                    	strQuery.setLength(0);
                    	strQuery.append("select cm_codename from cmm0020 \n");
                    	strQuery.append("where cm_macode='CHECKIN' and cm_micode=? \n");//rs.getString("cr_qrycd")
                        pstmt2 = conn.prepareStatement(strQuery.toString());
                        pstmt2.setString(1, rs.getString("cr_qrycd"));
                        rs2 = pstmt2.executeQuery();
                        if (rs2.next()){
                            CodeName_Tmp1 = rs2.getString("cm_codename");
                            if (CodeName_Tmp1 != null && CodeName_Tmp1 != "")
                            	SubItems1 = SubItems1 + "[" + CodeName_Tmp1 + "]";
                        }
                        pstmt2.close();
                        rs2.close();
                    }else{
                    	if (CodeName_Tmp1 != null)
                    		SubItems1 = SubItems1 + "[" + CodeName_Tmp1 + "]";
                    }
                }

                rst.put("SubItems1", SubItems1);
                rst.put("cm_username", rs.getString("cm_username"));
                rst.put("cr_prcdate", rs.getString("cr_prcdate"));
                rst.put("cr_docfile", rs.getString("cr_docfile"));
                rst.put("cr_acptno", rs.getString("cr_acptno"));
                rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+
                		             rs.getString("cr_acptno").substring(4,6)+"-"+
                		             rs.getString("cr_acptno").substring(6,12));
                rst.put("qrycd", rs.getString("qrycd"));
                rst.put("cr_version", rs.getString("cr_version"));
                rst.put("cr_docid", rs.getString("cr_docid"));
                rst.put("cr_devstep", rs.getString("cr_devstep"));
                if (rs.getString("cr_errcd") != null && rs.getString("cr_errcd") != ""){
                	rst.put("cr_errcd", rs.getString("cr_errcd"));
                }else{
                	rst.put("cr_errcd", "");
                }

                if (rs.getString("cr_emgcd") != null){
                	strQuery.setLength(0);
                	strQuery.append("select cm_codename from cmm0020 \n");
                	strQuery.append("where cm_macode='REQGBN' and cm_micode=? \n");//cr_emgcd
                	pstmt2 = conn.prepareStatement(strQuery.toString());
                	pstmt2.setString(1,rs.getString("cr_emgcd"));
                	rs2 = pstmt2.executeQuery();
                	if (rs2.next()){
                		CodeName_Tmp2 = "[" + rs2.getString("cm_codename") + "] ";
                	}
                    if (rs.getString("cr_docno") != null){
                    	CodeName_Tmp2 = CodeName_Tmp2 + rs.getString("cr_docno");
                    }
                    pstmt2.close();
                    rs2.close();
                    rst.put("SubItems7", CodeName_Tmp2);
                }else rst.put("SubItems7", "");

                if (rs.getString("cr_unittit") != null)
                   rst.put("SubItems8", rs.getString("cr_unittit"));
                else
                   rst.put("SubItems8", rs.getString("cr_sayu"));

                rst.put("cr_acptno", rs.getString("cr_acptno"));
                Tmp = rs.getString("cr_qrycd");
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;


    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_Hist() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_Hist() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_Hist() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_Hist() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Sql_Qry_Hist() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_Hist() method statement

    public Object[] Sql_Qry_Sub(String PrjNo) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
	        strQuery.append("select distinct a.cd_prjname,a.cd_ppmsno,b.cm_username creator, \n");
	        strQuery.append("       to_char(a.cd_creatdt,'yyyy/mm/dd hh24:mi') cd_creatdt,   \n");
	        strQuery.append("       to_char(a.cd_lastdt,'yyyy/mm/dd hh24:mi') cd_lastdt,     \n");
	        strQuery.append("       a.cd_ppmsno,b.cm_username creator,                       \n");
	        strQuery.append("       a.cd_status,c.cm_username editor,d.cm_codename sta,      \n");
	        strQuery.append("       f.cm_codename methname                                   \n");
	        strQuery.append(" from cmm0020 f,cmm0020 d,cmm0040 c,cmm0040 b,cmd0300 a \n");
	        strQuery.append("where a.cd_prjno=? 	\n");//PrjNo
	        strQuery.append("  and a.cd_creator=b.cm_userid \n");
	        strQuery.append("  and a.cd_editor=c.cm_userid \n");
	        strQuery.append("  and d.cm_macode='CMD0300' and d.cm_micode=a.cd_status \n");
	        strQuery.append("  and f.cm_macode='METHCD' and a.CD_METHCD=f.cm_micode \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, PrjNo);

           	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
            if (rs.next()){
            	rst = new HashMap<String, String>();
            	if (rs.getString("cd_status").equals("0")) rst.put("EndSw", "false");
	            else rst.put("EndSw", "true");
            	rst.put("cd_prjno", PrjNo);
            	rst.put("cd_prjname", rs.getString("cd_prjname"));
            	rst.put("cd_creatdt", rs.getString("cd_creatdt"));
            	rst.put("creator", rs.getString("creator"));
            	rst.put("cd_lastdt", rs.getString("cd_lastdt"));
            	rst.put("editor", rs.getString("editor"));
            	rst.put("sta", rs.getString("sta"));
            	if (rs.getString("cd_ppmsno") != null) rst.put("cd_ppmsno", rs.getString("cd_ppmsno"));
            	else rst.put("cd_ppmsno", "");
            	rst.put("methname", rs.getString("methname"));
            	rst.put("stepcd", Cmr0035_Select(PrjNo,conn));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

            //ecamsLogger.error("+++++rsval++++"+rsval.toString());
    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_Sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_Sub() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_Sub() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_Sub() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Sql_Qry_Sub() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_Sub() method statement

    public String Cmr0035_Select(String PrjNo,Connection conn) throws SQLException, Exception{
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		String            retStep     = null;

		try {

			strQuery.setLength(0);
			strQuery.append("SELECT CR_STEPCD FROM  cmr0035    \n");
			strQuery.append(" WHERE  CR_PRJNO  = ?             \n");
			strQuery.append(" order by cr_lastdt desc          \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
           	pstmt.setString(1, PrjNo);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
           	rs = pstmt.executeQuery();
            if (rs.next()) {
				retStep= rs.getString("CR_STEPCD");
			}
			rs.close();
			pstmt.close();

			rs = null;
			pstmt = null;

			return retStep;

  		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Cmr0035_Select() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception                   );
			ecamsLogger.error("## Cmd0400.Cmr0035_Select() SQLException END ##"  );
			throw sqlexception;
		} catch (Exception exception) {
			ecamsLogger.error("## Cmd0400.Cmr0035_Select() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception                   );
			ecamsLogger.error("## Cmd0400.Cmr0035_Select() Exception END ##"  );
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of Cmr0035_Select() method statement
    public Object[] Sql_Qry_DocCd(String PrjNo) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			String Devstep = "";

			strQuery.setLength(0);
		    strQuery.append("select a.cd_devstep, c.cm_codename DEVSTEP \n");
		    strQuery.append("from cmd0301 a, cmm0020 c  \n");
		    strQuery.append("where a.cd_prjno =? \n");//PrjNo
		    strQuery.append("  and c.cm_macode='DEVSTEP' and a.cd_devstep=c.cm_micode \n");
		    strQuery.append("order by a.CD_DEVSTEP \n");

            pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, PrjNo);

            rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("NO", Integer.toString(rs.getRow()));
                if (!Devstep.equals(rs.getString("DEVSTEP"))){
                   Devstep = rs.getString("DEVSTEP");
                   rst.put("DEVSTEP", rs.getString("DEVSTEP"));
                   rst.put("cd_devstep", rs.getString("cd_devstep"));
                }
    			int clsCcbyn = 0;
    			int NonclsCcbyn = 0;
    			int clsNonCcbyn = 0;
    			int NonclsNonCcbyn = 0;
    			int totalCnt = 0;
    			strQuery.setLength(0);
                strQuery.append("SELECT cr_closedt,cr_ccbyn from CMR0030  \n");
        		strQuery.append("WHERE CR_PRJNO=? \n"); //PrjNo
        		strQuery.append("  AND CR_devstep=? \n");//Devstep
                pstmt2 = conn.prepareStatement(strQuery.toString());
                pstmt2.setString(1, PrjNo);
                pstmt2.setString(2, rs.getString("cd_devstep"));
                rs2 = pstmt2.executeQuery();
                while (rs2.next()){
                    //if (rs2.getString("cr_closedt") != null)
                	//형상관리대상 폐기문서
                    if (rs2.getString("cr_ccbyn").equals("Y") && rs2.getString("cr_closedt") != null){
                    	++clsCcbyn;
                    }
                    //형상관리대상문서
                    else if(rs2.getString("cr_ccbyn").equals("Y") && rs2.getString("cr_closedt") == null){
                    	++NonclsCcbyn;
                    }
                    //비형상관리대상 폐기문서
                    else if(rs2.getString("cr_ccbyn").equals("N") && rs2.getString("cr_closedt") != null){
                    	++clsNonCcbyn;
                    }
                    //비형상관리대상문서
                    else if(rs2.getString("cr_ccbyn").equals("N") && rs2.getString("cr_closedt") == null){
                    	++NonclsNonCcbyn;
                    }
                    ++totalCnt;
                }
                if (totalCnt >0){
	                rst.put("CCBDoc", Integer.toString(NonclsCcbyn));
	                rst.put("NonCCBDoc", Integer.toString(NonclsNonCcbyn));
	                rst.put("ALLDoc", Integer.toString(NonclsCcbyn+NonclsNonCcbyn));
                }
				rsval.add(rst);
				rst = null;
				rs2.close();
				pstmt2.close();
			}
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;
    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Sql_Qry_DocCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_DocCd() method statement

    public Object[] Sql_Qry_DocFile(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		String PrjNo = dataObj.get("PrjNo");
		String DocSeq = dataObj.get("DocSeq");
		String Opt_Cd = dataObj.get("Opt_Cd");
		String DevStep_Index = dataObj.get("DevStep_Index");
		String Chk_Dir = dataObj.get("Chk_Dir");
		String fileName = dataObj.get("fileName");
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			int parameterIndex = 0;
		    if (Opt_Cd.equals("true")) {
		    	strQuery.append("select a.cd_methcd,a.cd_devstep,b.cm_codename as methcd ,c.cm_codename as DEVSTEP \n");
		    	strQuery.append("from cmd0301 a,cmm0020 b,cmm0020 c \n");
		    	strQuery.append("where a.cd_prjno =? \n");//PrjNo
		        if (Integer.parseInt(DevStep_Index) > 0)
		        	strQuery.append("and a.cd_devstep=? \n");//cd_devstep
		        strQuery.append("  and b.cm_macode='METHCD' and b.cm_micode=a.cd_methcd \n");
		        strQuery.append("  and c.cm_macode='DEVSTEP' and c.cm_micode=a.cd_devstep \n");
		        strQuery.append("order by a.cd_methcd,a.cd_devstep \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt =  new LoggableStatement(conn, strQuery.toString());

	            pstmt.setString(1, PrjNo);
	            if (Integer.parseInt(DevStep_Index) > 0) pstmt.setString(2, dataObj.get("DevStep_code"));

	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	            rsval.clear();
	            while (rs.next()){
	            	parameterIndex = 0;
	                strQuery.setLength(0);
	                strQuery.append("select a.cr_docid,a.cr_docfile,a.cr_status,a.cr_lstver, ");
	                strQuery.append("to_char(a.cr_lastdt,'yyyy/mm/dd hh24:mi') cr_lastdt,a.cr_devstep, ");
	                strQuery.append("a.cr_defyn,a.cr_ccbyn,d.cm_username ");
	                strQuery.append("from cmm0040 d,cmr0030 a,cmr0031 c ");
	                strQuery.append("where c.cr_prjno =? ");//PrjNo
	                if (DocSeq != ""){
	                    if (Chk_Dir.equals("true")){//하위폴더포함
	                    	strQuery.append("and c.cr_docseq in (SELECT CD_DOCSEQ ");
	                    	strQuery.append("   FROM (select CD_DOCSEQ,CD_UPDOCSEQ from CMD0303 ");
	                    	strQuery.append("   WHERE CD_PRJNO = ?) ");//PrjNo
	                    	strQuery.append("   START WITH CD_DOCSEQ=? ");//DocSeq
	                    	strQuery.append("   CONNECT BY PRIOR CD_DOCSEQ = CD_UPDOCSEQ) ");
	                    }else
	                    	strQuery.append("and c.cr_docseq=? ");//DocSeq
	                }
	                strQuery.append("  and a.cr_methcd=? ");//DbSet!CD_METHCD
	                strQuery.append("  and a.cr_devstep=? ");//DbSet!CD_DEVSTEP
	                strQuery.append("  and c.cr_docid=a.cr_docid ");
	                strQuery.append("  and a.cr_editor=d.cm_userid ");
	                strQuery.append("  and a.cr_closedt is null ");
	                strQuery.append("order by a.cr_docid ");

	                pstmt2 = conn.prepareStatement(strQuery.toString());
	                //pstmt2 =  new LoggableStatement(conn, strQuery.toString());

	                pstmt2.setString(++parameterIndex, PrjNo);
	                if (DocSeq != ""){
	                    if (Chk_Dir.equals("true")){//하위폴더포함
	                    	pstmt2.setString(++parameterIndex, PrjNo);
	                    	pstmt2.setString(++parameterIndex, DocSeq);
	                    }else pstmt2.setString(++parameterIndex, DocSeq);
	                }
	                pstmt2.setString(++parameterIndex, rs.getString("cd_methcd"));
	                pstmt2.setString(++parameterIndex, rs.getString("cd_devstep"));

	                //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
	                rs2 = pstmt2.executeQuery();

	                while (rs2.next()){
	                	rst = new HashMap<String, String>();
	                	rst.put("DEVSTEP", rs.getString("DEVSTEP"));
                	    rst.put("DocName", rs2.getString("cr_docfile"));
                	    rst.put("cr_status", rs2.getString("cr_status"));
                	    rst.put("ccbyn", rs2.getString("cr_ccbyn"));
                	    rst.put("cr_lastdt", rs2.getString("cr_lastdt"));
                	    rst.put("lastEditor", rs2.getString("cm_username"));
                	    rst.put("cr_lstver", rs2.getString("cr_lstver"));
                	    rst.put("cr_docid", rs2.getString("cr_docid"));
                	    rst.put("cr_devstep", rs2.getString("cr_devstep"));
                	    rsval.add(rst);
                	    rst = null;
	                }
	                rs2.close();
					pstmt2.close();
				}
		    }else{
		    	strQuery.append("select a.cr_docid,a.cr_docfile,a.cr_status,a.cr_lstver,a.cr_defyn,a.cr_ccbyn, ");
		    	strQuery.append("to_char(a.cr_lastdt,'yyyy/mm/dd hh24:mi') cr_lastdt,a.cr_devstep, ");
		    	strQuery.append("d.cm_username,f.cm_codename DEVSTEP,g.cm_codename methcd ");
		    	strQuery.append("from cmr0030 a,cmr0031 c,cmm0040 d,cmm0020 f, cmm0020 g ");
		    	strQuery.append("where c.cr_prjno =? ");//PrjNo
		    	if (fileName != "") strQuery.append("  and a.cr_docfile like ? ");//docname
	            strQuery.append("  and c.cr_docid=a.cr_docid ");
	            strQuery.append("  and a.cr_editor=d.cm_userid ");
	            strQuery.append("  and a.cr_closedt is null ");
	            strQuery.append("  and g.cm_macode='METHCD' and g.cm_micode=a.cr_methcd ");
	            strQuery.append("  and f.cm_macode='DEVSTEP' and f.cm_micode=a.cr_devstep ");
	            strQuery.append("order by cr_devstep,cr_docid ");

	            pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt =  new LoggableStatement(conn, strQuery.toString());

	            pstmt.setString(++parameterIndex, PrjNo);
	            if (fileName != "") pstmt.setString(++parameterIndex, "%"+fileName+"%");

	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            rsval.clear();
	            while (rs.next()){
	            	rst = new HashMap<String, String>();
	            	rst.put("DEVSTEP", rs.getString("DEVSTEP"));
	                rst.put("DocName", rs.getString("cr_docfile"));
	                rst.put("cr_status", rs.getString("cr_status"));
            	    rst.put("ccbyn", rs.getString("cr_ccbyn"));
            	    rst.put("cr_lastdt", rs.getString("cr_lastdt"));
            	    rst.put("lastEditor", rs.getString("cm_username"));
            	    rst.put("cr_lstver", rs.getString("cr_lstver"));
            	    rst.put("cr_docid", rs.getString("cr_docid"));
            	    rst.put("cr_devstep", rs.getString("cr_devstep"));
					rsval.add(rst);
					rst = null;
	            }
		    }
		    rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Sql_Qry_DocFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Sql_Qry_DocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_DocFile() method statement

    public String Cmd0304_Update(String PrjNo,String UserId,
    		ArrayList<HashMap<String,String>> PRJUSER) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

		    //CMD0304_INSERT start
			strQuery.setLength(0);
			strQuery.append("update cmd0304 set cd_closedt=sysdate ");
			strQuery.append("where cd_prjno=? ");
			strQuery.append("  AND CD_PRJJIK<>'PM' ");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, PrjNo);
		    pstmt.executeUpdate();
		    pstmt.close();

		    for (int i=0 ; i<PRJUSER.size() ; i++){
	            strQuery.setLength(0);
	            strQuery.append("SELECT COUNT(*) AS cnt FROM CMD0304 \n");
	            strQuery.append("WHERE CD_PRJNO=? \n");			//PrjNo
	            strQuery.append("  AND CD_PRJUSER=? \n");		//PRJUSER_ID
	            strQuery.append("  AND CD_PRJJIK=? \n");		//PRJJIK
	            pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, PrjNo);
			    pstmt.setString(2, PRJUSER.get(i).get("CM_USERID"));
			    pstmt.setString(3, PRJUSER.get(i).get("CD_PRJJIK"));
			    rs = pstmt.executeQuery();

	            if (rs.next()){
	            	strQuery.setLength(0);
	            	if (rs.getString("cnt").equals("0")){
		            	strQuery.append("INSERT INTO CMD0304 (CD_PRJNO,CD_PRJUSER,CD_USERTEAM, ");
		            	strQuery.append("CD_PRJJIK,CD_CREATDT,CD_EDITOR,CD_EDITYN,CD_DOCYN) VALUES ( ");
		            	strQuery.append("?, ");				//PrjNo
		            	strQuery.append("?, ");				//Lv_Sgn.ListItems.id
		            	strQuery.append("?, ");				//Lv_Sgn.ListItems.TeamCD
		            	strQuery.append("?, ");				//PRJJIK
		            	strQuery.append("SYSDATE,?, ");		//UserID
	                    if (PRJUSER.get(i).get("CD_PRJJIK").equals("PM") || PRJUSER.get(i).get("CD_PRJJIK").equals("SM"))
	                    	strQuery.append("'Y', ");   	//CD_EDITYN
	                    else
	                    	strQuery.append("'N', ");   	//CD_EDITYN

	                    if (PRJUSER.get(i).get("CD_PRJJIK").equals("RF"))
	                    	strQuery.append("'N') ");		//CD_DOCYN
	                    else
	                    	strQuery.append("'Y') ");		//CD_DOCYN
	                    pstmt2 = conn.prepareStatement(strQuery.toString());
	                    pstmt2.setString(1, PrjNo);
	                    pstmt2.setString(2, PRJUSER.get(i).get("CM_USERID"));
	                    pstmt2.setString(3, PRJUSER.get(i).get("TEAMCD"));
	                    pstmt2.setString(4, PRJUSER.get(i).get("CD_PRJJIK"));
	                    pstmt2.setString(5, UserId);
	            	}else{
	            		strQuery.append("UPDATE CMD0304 SET CD_USERTEAM=?, \n");	//Lv_Sgn.ListItems.TeamCD
		            	strQuery.append("CD_CLOSEDT='', \n");
		            	strQuery.append("CD_EDITOR=?, \n");							//UserID
	                    if (PRJUSER.get(i).get("CD_PRJJIK").equals("PM") || PRJUSER.get(i).get("CD_PRJJIK").equals("SM"))
	                    	strQuery.append("CD_EDITYN='Y',");
	                    else
	                    	strQuery.append("CD_EDITYN='N',");

	                    if (PRJUSER.get(i).get("CD_PRJJIK").equals("RF"))
	                    	strQuery.append("CD_DOCYN='N' ");
	                    else
	                    	strQuery.append("CD_DOCYN='Y' ");
		            	strQuery.append("WHERE CD_PRJNO=? \n");						//PrjNo
		            	strQuery.append("  AND CD_PRJUSER=? \n");					//Lv_Sgn.ListItems
		            	strQuery.append("  AND CD_PRJJIK=? \n");
		            	pstmt2 = conn.prepareStatement(strQuery.toString());
		            	pstmt2.setString(1, PRJUSER.get(i).get("TEAMCD"));
		            	pstmt2.setString(2, UserId);
		            	pstmt2.setString(3, PrjNo);
		            	pstmt2.setString(4, PRJUSER.get(i).get("CM_USERID"));
		            	pstmt2.setString(5, PRJUSER.get(i).get("CD_PRJJIK"));
	            	}
	            	pstmt2.executeUpdate();
	            	pstmt2.close();
	            }
	            pstmt2 = null;

	            rs.close();
	            pstmt.close();

	            rs = null;
	            pstmt = null;
		    }
		    conn.commit();
            conn.close();
            conn = null;

		    return PrjNo;

  		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0400.Cmd0304_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Cmd0304_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0400.Cmd0304_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Cmd0304_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Cmd0304_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmd0304_Update() method statement

    public int Cmr0030_Update(String UserId,
    		ArrayList<HashMap<String,String>> Spr_Lst) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		int				  i           = 0;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

		    for (i=0 ; i<Spr_Lst.size() ; i++){
	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMR0030 SET \n");
                if (Spr_Lst.get(i).get("ccbyn").equals("Y")){
                	strQuery.append("CR_CCBYN='Y', 	\n");
                }else{
                	strQuery.append("CR_CCBYN='N', 	\n");
                }
                strQuery.append("CR_LASTDT=SYSDATE, \n");
                strQuery.append("CR_EDITOR=? 		\n");//UserId
                strQuery.append("WHERE CR_DOCID=? 	\n");//docid
            	pstmt = conn.prepareStatement(strQuery.toString());
    		    pstmt.setString(1, UserId);
    		    pstmt.setString(2, Spr_Lst.get(i).get("cr_docid"));
            	pstmt.executeUpdate();
	            pstmt.close();

		    }
		    conn.commit();
		    conn.close();
            pstmt = null;
		    conn = null;

		    return i-1;

  		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0400.Cmr0030_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0400.Cmr0030_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0400.Cmr0030_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0400.Cmr0030_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Cmr0030_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmr0030_Update() method statement

    public int Cmr0035_Insert(String PrjNo, String flag,String UserId) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append(" INSERT INTO CMR0035                                  \n");
			strQuery.append(" (CR_PRJNO,CR_STEPCD,CR_EDITOR,CR_LASTDT)             \n");
			strQuery.append(" VALUES (?, ?, ?, SYSDATE)                            \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());

           	pstmt.setString(1, PrjNo);
           	pstmt.setString(2, flag);
           	pstmt.setString(3, UserId);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
           	pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append(" INSERT INTO CMR0036                                  \n");
			strQuery.append("  (CR_PRJNO,CR_STEPCD,CR_DOCID,CR_ACPTNO)             \n");
			strQuery.append("  (SELECT ?, ?, Y.CR_DOCID, Y.CR_ACPTNO               \n");
			strQuery.append("     FROM (SELECT CR_DOCID, MAX(CR_PRCDATE) CR_PRCDATE\n");
			strQuery.append("             FROM   CMR0034                           \n");
			strQuery.append("            WHERE  CR_PRJNO = ?                       \n");
			strQuery.append("            GROUP  BY CR_DOCID) X,                    \n");
			strQuery.append("           CMR0034 Y                                  \n");
			strQuery.append("   WHERE X.CR_DOCID   = Y.CR_DOCID                    \n");
			strQuery.append("     AND X.CR_PRCDATE = Y.CR_PRCDATE)                 \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());

           	pstmt.setString(1, PrjNo);
           	pstmt.setString(2, flag);
           	pstmt.setString(3, PrjNo);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
           	pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			pstmt=null;
			conn =null;

		    return 0;

  		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Cmr0035_Insert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception                   );
			ecamsLogger.error("## Cmd0400.Cmr0035_Insert() SQLException END ##"  );
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0400.Cmr0035_Insert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception                   );
			ecamsLogger.error("## Cmd0400.Cmr0035_Insert() Exception END ##"  );
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0400.Cmr0035_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmr0035_Insert() method statement

}//end of Cmd0400 class statement
