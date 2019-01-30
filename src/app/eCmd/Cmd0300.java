
/*****************************************************************************************
	1. program ID	: eCmd0300.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: eCmd0300 [문서관리]->[프로젝트등록]
*****************************************************************************************/

package app.eCmd;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
//import app.common.UserInfo;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd0300{
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


	public Object[] getMKDIR(String PrjNo) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.append("select a.cd_prjname,c.cm_codename from cmd0300 a, cmd0301 b, cmm0020 c ");
			strQuery.append("where a.cd_prjno  = ? ");//PrjNo
			strQuery.append("  and a.cd_prjno = b.cd_prjno ");
			strQuery.append("  and c.cm_macode='DEVSTEP' ");
			strQuery.append("  and b.cd_devstep = c.cm_micode ");
			strQuery.append("order by b.cd_seqno ");
            pstmt = conn.prepareStatement(strQuery.toString());
           	pstmt.setString(1, PrjNo);
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (rs.getRow() == 1){
					rst = new HashMap<String, String>();
					rst.put("cd_prjname", rs.getString("cd_prjname"));
					rst.put("cm_codename", "");
					rst.put("Sv_LocalF", rs.getString("cd_prjname"));
					rsval.add(rst);
				}

				rst = new HashMap<String, String>();
				rst.put("cd_prjname", rs.getString("cd_prjname"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("Sv_LocalF", rs.getString("cd_prjname")+"\\\\"+rs.getString("cm_codename"));
	           	rsval.add(rst);
	           	rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getMKDIR() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.getMKDIR() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getMKDIR() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.getMKDIR() Exception END ##");
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
					ecamsLogger.error("## Cmd0300.getMKDIR() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] METHCD_Set(String PrjNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
           	strQuery.append("select a.CD_PRJNO,a.CD_PRJNAME,a.CD_STATUS,b.CD_METHCD, ");
           	strQuery.append("B.CD_DEVSTEP,B.CD_SEQNO,c.cm_codename ");
           	strQuery.append("FROM cmd0300 a,cmd0301 b, cmm0020 c ");
           	strQuery.append(" WHERE a.CD_PRJNO = ? AND a.CD_PRJNO = b.CD_PRJNO ");		//PrjNo
           	strQuery.append("   AND c.CM_MACODE = 'DEVSTEP' AND b.CD_DEVSTEP = c.CM_MICODE ");
           	strQuery.append(" order by B.CD_SEQNO ");
            pstmt = conn.prepareStatement(strQuery.toString());
           	pstmt.setString(1, PrjNo);
            rs = pstmt.executeQuery();

            String MICODE = null;
			while(rs.next()){
				if (rs.getRow()==1){
					rst = new HashMap<String, String>();
					rst.put("ID", "Check_MethCd");
					rst.put("CD_METHCD", rs.getString("CD_METHCD"));
					rsval.add(rst);
					rst = null;
				}

				rst = new HashMap<String, String>();
				rst.put("cm_macode", "DEVSTEP");
				rst.put("cm_micode", rs.getString("CD_DEVSTEP"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("checkbox", "true");
				strQuery.setLength(0);
				strQuery.append("SELECT count(*) as cnt FROM CMR0030 ");
				strQuery.append("WHERE cr_prjno=? ");
				strQuery.append("AND cr_devstep=? ");
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	           	pstmt2.setString(1, PrjNo);
	           	pstmt2.setString(2, rs.getString("CD_DEVSTEP"));
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()){
	            	if (rs2.getString("CNT").equals("0"))
	            		rst.put("docFileYN", "N");
	            	else
	            		rst.put("docFileYN", "Y");
	            }
				if (MICODE == null)
					MICODE = rs.getString("CD_DEVSTEP");
				else MICODE = MICODE + "," + rs.getString("CD_DEVSTEP");

	            rs2.close();
	            pstmt2.close();
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			if (MICODE != "" && MICODE != null){
				String[] micode = MICODE.split(",");
				strQuery.setLength(0);
				strQuery.append("select cm_macode,cm_micode,cm_codename from cmm0020 ");
				strQuery.append("where cm_macode='DEVSTEP' and cm_micode <> '****' ");
				strQuery.append("  and cm_closedt is null ");
				strQuery.append("  and cm_micode not in ( ");
				if (micode.length == 1)
					strQuery.append(" ? ");
				else{
					for (int i=0;i<micode.length;i++){
						if (i == micode.length-1)
							strQuery.append(" ? ");
						else
							strQuery.append(" ? ,");
					}
				}
				strQuery.append(" ) ");
				strQuery.append("order by cm_micode ");
	            pstmt = conn.prepareStatement(strQuery.toString());
				for (int i=0;i<micode.length;i++){
					pstmt.setString(i+1, micode[i]);
				}
			}else{
				strQuery.setLength(0);
				strQuery.append("select cm_macode,cm_micode,cm_codename from cmm0020 ");
				strQuery.append("where cm_macode='DEVSTEP' and cm_micode <> '****' ");
				strQuery.append("  and cm_closedt is null ");
				strQuery.append("order by cm_micode ");
				pstmt = conn.prepareStatement(strQuery.toString());
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_macode", rs.getString("cm_macode"));
				rst.put("docFileYN", "N");
				rst.put("checkbox", "");
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
			ecamsLogger.error("## Cmd0300.METHCD_Set() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.METHCD_Set() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.METHCD_Set() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.METHCD_Set() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.METHCD_Set() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of METHCD_Set() method statement

	//Make PrjNo
	public String PrjNo_make(String PrjNo) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String 		      rPrjNo	  = null;

		try {
			conn = connectionContext.getConnection();
			//프로젝트번호
			strQuery.setLength(0);
	        strQuery.append("with tmp as                                                            \n");
	        strQuery.append("(select case when nvl(max(substr(cd_prjno,16,2)), 'x') = 'x'           \n");
	        strQuery.append("        then 1                                                         \n");
	        strQuery.append("        else to_number(max(substr(cd_prjno,16,2))) + 1 end as maxprjno \n");
	        strQuery.append(" from  cmd0300                                                         \n");
	        strQuery.append(" where substr(cd_prjno,1,15) = substr(?,1,15))                         \n");
	        strQuery.append(" select lpad(maxprjno, 2, '0') seqno  from tmp                         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());

	        pstmt.setString(1, PrjNo);
            rs = pstmt.executeQuery();

            if (rs.next()){
				rPrjNo = PrjNo + rs.getString("seqno") ;
			}

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rPrjNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.PrjNo_make() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.PrjNo_make() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.PrjNo_make() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.PrjNo_make() Exception END ##");
			throw exception;
		}
	}//end of PrjNo_make(String) method statement




	//INSERT
	public String Cmd0300_INSERT(HashMap<String,String> dataObj,
			ArrayList<HashMap<String,String>> DEVSTEP,
			ArrayList<HashMap<String,String>> UnDEVSTEP,
			ArrayList<HashMap<String,String>> PRJUSER) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            TKey        = "";
		String            UpKey       = "";
		String			  PrjNo       = "";
		String			  UserId      = "";
		String			  PrjName     = "";
		String			  MethCd      = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			UserId      = dataObj.get("UserId");
			PrjNo       = dataObj.get("PrjNo");
			PrjName     = dataObj.get("PrjName");
			MethCd      = dataObj.get("MethCd");

			strQuery.setLength(0);
			//if (dataObj.get("PrjNo").equals("00000")){
			if (dataObj.get("NewFlag").equals("Y")){

				strQuery.append("select cd_prjname from cmd0300 ");
				strQuery.append("Where cd_prjname = ? ");//PrjName
				strQuery.append("  and substr(cd_prjno,1,15)=? ");//PrjName
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, PrjName);
	            pstmt.setString(2, PrjNo);
	            rs = pstmt.executeQuery();
				if (rs.next()){
					//throw new Exception("["+PrjName+"] 프로젝트가 이미 존재합니다.");
					return "0000";
				}
				rs.close();
				pstmt.close();
				// 번호 자동 생성이 아니라 입력 받는 것으로 변경 (PMS와 맞추기 위함)
				PrjNo = PrjNo_make(PrjNo);

				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMD0300 (CD_PRJNO,CD_PRJNAME,CD_CREATDT, \n");
				strQuery.append("CD_LASTDT,CD_STATUS,CD_EDITOR,CD_CREATOR,CD_JOBUSEYN,\n");
				strQuery.append("CD_METHCD) values ( \n");
				strQuery.append("?, \n");				//CD_PRJNO
				strQuery.append("?, \n"); 				//CD_PRJNAME
				strQuery.append("SYSDATE,   \n");
				strQuery.append("SYSDATE,   \n");
				strQuery.append("'0', \n");
				strQuery.append("?, ?, \n");      		//CD_EDITOR, CD_CREATOR
				strQuery.append("'N', ?) \n");
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, PrjNo);
                pstmt.setString(2, PrjName);
                pstmt.setString(3, UserId);
                pstmt.setString(4, UserId);
                pstmt.setString(5, MethCd);
                //ecamsLogger.error("1 => " + new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")));
                pstmt.executeUpdate();
                pstmt.close();
			}else{
				PrjNo = dataObj.get("PrjNo");
				strQuery.setLength(0);
				strQuery.append("UPDATE CMD0300 SET \n");
				strQuery.append("CD_PRJNAME = ?, \n"); 			//CD_PRJNAME
				strQuery.append("CD_METHCD = ?,  \n"); 			//CD_PRJNAME
				strQuery.append("CD_LASTDT = SYSDATE,   \n");
				strQuery.append("CD_EDITOR = ? \n");        	//CD_EDITOR
				strQuery.append("Where CD_PRJNO = ? \n");     	//CD_PRJNO

				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());

                pstmt.setString(1, PrjName);
                pstmt.setString(2, MethCd);
                pstmt.setString(3, UserId);
                pstmt.setString(4, PrjNo);
                //ecamsLogger.error("2 => " + new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")));
                pstmt.executeUpdate();
                pstmt.close();
			}

		    //CMD0301_INSERT start
			strQuery.setLength(0);
			strQuery.append("update cmd0301 set cd_closedt=sysdate \n");
			strQuery.append("where cd_prjno=? \n");  //PrjNo
			pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, PrjNo);
		    pstmt.executeUpdate();
		    pstmt.close();

		    int Cnt = 0;
			for (int i=0 ; i<DEVSTEP.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from CMD0301 \n");
				strQuery.append("Where CD_PRJNO = ? \n");		//PrjNo
				strQuery.append("  AND CD_DEVSTEP = ? \n");		//DEVSTEP
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, PrjNo);
			    pstmt.setString(2, DEVSTEP.get(i).get("cm_micode"));
	        	rs = pstmt.executeQuery();
	        	strQuery.setLength(0);
	        	if (rs.next()){
	        		if (rs.getString("cnt").equals("0")){
	            		strQuery.append("INSERT INTO CMD0301 (CD_METHCD,CD_EDITOR,CD_SEQNO, ");
	            		strQuery.append("CD_PRJNO,CD_DEVSTEP,CD_CREATDT,CD_LASTDT) values ( ");
	            		strQuery.append("?, ");      					//METHCD
	            		strQuery.append("?, ");							//UserID
	            		strQuery.append("?, ");							//SEQNO
	            		strQuery.append("?, ");     					//PrjNo
	            		strQuery.append("?, ");         				//DEVSTEP
	            		strQuery.append("SYSDATE, ");
	            		strQuery.append("SYSDATE) ");
	        		}else{
	            		strQuery.append("UPDATE CMD0301 SET ");
	            		strQuery.append("CD_LASTDT=SYSDATE,CD_CLOSEDT='',");
	            		strQuery.append("CD_METHCD = ?, "); 			//METHCD
	            		strQuery.append("CD_EDITOR=?, ");               //UserID
	            		strQuery.append("CD_SEQNO=? ");					//SEQNO
	            		strQuery.append("Where CD_PRJNO=? ");           //PrjNo
	            		strQuery.append("  AND CD_DEVSTEP=? ");     	//DEVSTEP
	        		}
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, MethCd);
	        		pstmt2.setString(2, UserId);
	        		pstmt2.setInt(3, ++Cnt);
	        		pstmt2.setString(4, PrjNo);
	        		pstmt2.setString(5, DEVSTEP.get(i).get("cm_micode"));
	        		pstmt2.executeUpdate();
	        		pstmt2.close();
	        	}
	        	rs.close();
	        	pstmt.close();
			}

			strQuery.setLength(0);
			strQuery.append("DELETE CMD0301 \n");
			strQuery.append("Where CD_PRJNO = ? \n"); //CD_PRJNO
			strQuery.append("  AND CD_CLOSEDT IS NOT NULL \n");
			pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, PrjNo);
		    pstmt.executeUpdate();
		    pstmt.close();
		    //////////////////////////
		    //  CMD0301_INSERT end  //
		    //////////////////////////


		    ///////////////////////////
		    //  CMD0303_INSERT Start //
		    ///////////////////////////
		    strQuery.setLength(0);
        	strQuery.append("select count(*) as cnt from cmd0303 \n");
        	strQuery.append("where cd_prjno=? \n");  	//CD_PRJNO
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, PrjNo);
        	rs = pstmt.executeQuery();
        	strQuery.setLength(0);
        	if (rs.next()){
        		if (rs.getString("cnt").equals("0")) {
            		strQuery.append("insert into cmd0303 (cd_dirname,cd_prjno,cd_docseq,cd_dirpath) \n");
            		strQuery.append("values (?,?,'00001',?) \n");			//PRJNAME,PRJNO
            		pstmt2 = conn.prepareStatement(strQuery.toString());
            		pstmt2.setString(1, PrjName);
            		pstmt2.setString(2, PrjNo);
            		pstmt2.setString(3, PrjName);
            		pstmt2.executeUpdate();
            		pstmt2.close();
        		}else{
	        		strQuery.append("update cmd0303 set cd_dirname=?,cd_dirpath=? \n");	//PRJNAME
	        		strQuery.append("where cd_prjno=? \n");					//PRJNO
	        		strQuery.append("  and CD_DOCSEQ='00001' \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, PrjName);
	        		pstmt2.setString(2, PrjName);
	        		pstmt2.setString(3, PrjNo);
	        		pstmt2.executeUpdate();
	        		pstmt2.close();
        		}
        	}
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("select count(*) as cnt from cmd0305 \n");
        	strQuery.append("where cd_prjno=? 		\n");
        	strQuery.append("  and cd_dsncd='00001' \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, PrjNo);
        	rs = pstmt.executeQuery();
        	strQuery.setLength(0);
        	if (rs.next()){
        		if (rs.getInt("cnt") > 0) {
	                strQuery.append("update cmd0305 set cd_dirpath=?, \n");//Txt_PrjName
					strQuery.append("cd_editor=?, 		\n"); //SV_UserID
					strQuery.append("cd_lastupdt=SYSDATE,cd_clsdt='' \n");
					strQuery.append("where cd_prjno=? 	\n");//Txt_PrjNo
					strQuery.append("  AND CD_Dsncd='00001' \n");
        		}else{
	                strQuery.append("insert into cmd0305 (CD_DIRPATH,CD_EDITOR,CD_PRJNO,CD_DSNCD,CD_OPENDT,CD_LASTUPDT) values (\n");
	                strQuery.append("?,?,?, \n");   //Txt_PrjName SV_UserID Txt_PrjNo
	                strQuery.append("'00001', \n");
	                strQuery.append("SYSDATE,SYSDATE) \n");
        		}
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, PrjName);
        		pstmt2.setString(2, UserId);
        		pstmt2.setString(3, PrjNo);
        		pstmt2.executeUpdate();
        		pstmt2.close();
        	}
        	rs.close();
        	pstmt.close();

			for (int i=0 ; i<DEVSTEP.size() ; i++){
				strQuery.setLength(0);
	        	strQuery.append("select cd_docseq from cmd0303 \n");
	        	strQuery.append("where cd_prjno=? \n");//PrjNo
	        	strQuery.append("  and CD_UPDOCSEQ='00001' \n");
	        	strQuery.append("  and CD_DEVSTEP=? \n");//DevStep
				pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, PrjNo);
			    pstmt.setString(2, DEVSTEP.get(i).get("cm_micode"));
			    rs = pstmt.executeQuery();
			    strQuery.setLength(0);
			    if (rs.next()){
			    	TKey = rs.getString("cd_docseq");
		    		strQuery.append("UPDATE CMD0303 SET CD_DIRNAME=?,CD_DIRPATH=? \n");	//DevStepName
            		strQuery.append("where CD_PRJNO=? \n");					//PrjNo
            		strQuery.append("  and CD_DOCSEQ=? \n");				//TKey
            		pstmt2 = conn.prepareStatement(strQuery.toString());
            		pstmt2.setString(1, DEVSTEP.get(i).get("cm_codename"));
            		pstmt2.setString(2, PrjName + "\\" + DEVSTEP.get(i).get("cm_codename"));
            		pstmt2.setString(3, PrjNo);
            		pstmt2.setString(4, TKey);
            		pstmt2.executeUpdate();
            		pstmt2.close();
			    }else{
		    		TKey = "00001";
		    		UpKey = "00001";
		    		strQuery.append("select max(cd_docseq) max from cmd0303 \n");
	                strQuery.append("where cd_prjno=? \n");//PrjNo
	                pstmt2 = conn.prepareStatement(strQuery.toString());
	                pstmt2.setString(1, PrjNo);
				    rs2 = pstmt2.executeQuery();
				    if (rs2.next()){
						int j = Integer.parseInt(rs2.getString("max"))+1;

				    	TKey = "00000".substring(0,5-Integer.toString(j).length()) +
				    		Integer.toString(j);
				    }
				    rs2.close();
				    pstmt2.close();

				    strQuery.setLength(0);
				    strQuery.append("INSERT into cmd0303 (CD_PRJNO,CD_DOCSEQ,CD_DIRNAME,\n");
				    strQuery.append("CD_DEVSTEP,CD_UPDOCSEQ,CD_DIRPATH) values (\n");
                	strQuery.append("?, \n");						//PrjNo
                	strQuery.append("?, \n");						//TKey
                	strQuery.append("?, \n");						//DevStepName
                	strQuery.append("?, \n");						//DevStep
                	strQuery.append("?, \n");						//UpKey
                	strQuery.append("?) \n");						//FULLPATH
                	pstmt2 = conn.prepareStatement(strQuery.toString());
                	pstmt2.setString(1, PrjNo);
                	pstmt2.setString(2, TKey);
                	pstmt2.setString(3, DEVSTEP.get(i).get("cm_codename"));
                	pstmt2.setString(4, DEVSTEP.get(i).get("cm_micode"));
                	pstmt2.setString(5, UpKey);
                	pstmt2.setString(6, PrjName + "\\" + DEVSTEP.get(i).get("cm_codename"));
                	pstmt2.executeUpdate();
                	pstmt2.close();
			    }
			    rs.close();
			    pstmt.close();

			    strQuery.setLength(0);
			    strQuery.append("select COUNT(*) AS cnt from cmd0305 \n");
			    strQuery.append("where cd_prjno=? 	\n"); //PrjNo
			    strQuery.append("  and CD_dsncd=?	\n"); //TKey
				pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, PrjNo);
			    pstmt.setString(2, TKey);
			    rs = pstmt.executeQuery();
			    strQuery.setLength(0);
			    if (rs.next()){
			    	if (rs.getInt("cnt") == 0){
				    	strQuery.append("insert into cmd0305 (CD_DIRPATH,CD_EDITOR,CD_PRJNO,");
				    	strQuery.append("CD_DSNCD,CD_OPENDT,CD_LASTUPDT) values ( \n");
				    	strQuery.append("?, \n");	//PrjName "/" Lst_DevStep
				    	strQuery.append("?, \n");  	//UserId
				    	strQuery.append("?, \n");	//PrjNo
				    	strQuery.append("?, \n"); 	//TKey
				    	strQuery.append("SYSDATE,SYSDATE) \n");
			    	}else{
			    		strQuery.append("UPDATE CMD0305 SET \n");
			    		strQuery.append(" CD_DIRPATH=?, \n");  	//PrjName "/" Lst_DevStep
			    		strQuery.append("  CD_EDITOR=?, \n");  	//UserId
			    		strQuery.append("CD_LASTUPDT=SYSDATE,cd_clsdt='' \n");
			    		strQuery.append("where CD_PRJNO=? \n");	//PrjNo
			    		strQuery.append("  and CD_DSNCD=? \n");	//TKey
			    	}
			    	pstmt2 = conn.prepareStatement(strQuery.toString());
			    	pstmt2.setString(1, PrjName + "/" + DEVSTEP.get(i).get("cm_codename"));
			    	pstmt2.setString(2, UserId);
			    	pstmt2.setString(3, PrjNo);
			    	pstmt2.setString(4, TKey);
			    	pstmt2.executeUpdate();
			        pstmt2.close();
			    }
			    rs.close();
			    pstmt.close();

			}


			//단계선택 안한 경우
			for (int i=0 ; i<UnDEVSTEP.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("DELETE CMD0305 \n");
				strQuery.append("where cd_prjno=? \n");  //PrjNo
				strQuery.append("  and CD_DSNCD=(select cd_docseq from cmd0303 \n");
				strQuery.append("       where cd_prjno=? \n");  //PrjNo
				strQuery.append("         and cd_updocseq='00001' \n");
				strQuery.append("         and CD_DIRNAME=?) \n");//DevStepName
				pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, PrjNo);
		        pstmt.setString(2, PrjNo);
		        pstmt.setString(3, UnDEVSTEP.get(i).get("cm_codename"));
		        pstmt.executeUpdate();
		        pstmt.close();

		    	strQuery.setLength(0);
		    	strQuery.append("DELETE CMD0303 \n");
		    	strQuery.append("where cd_prjno=? \n");   			//PrjNo
		    	strQuery.append("  and CD_UPDOCSEQ='00001' \n");
				strQuery.append("  and CD_DIRNAME=? \n");			//DevStepName
				pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, PrjNo);
		        pstmt.setString(2, UnDEVSTEP.get(i).get("cm_codename"));
		        pstmt.executeUpdate();
		        pstmt.close();
			}
			//CMD0303_INSERT  END
			conn.commit();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			Cmd0400 cmd0400 = new Cmd0400();
			cmd0400.Cmd0304_Update(PrjNo, UserId, PRJUSER);

		    return PrjNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() SQLException END ##");
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
					ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmd0300_INSERT() method statement

	public int Cmd0300_UPDATE(String UserId,String PrjNo,int index) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			switch(index){
			case 1://플젝 삭제처리
				strQuery.setLength(0);
				strQuery.append("Select count(*) as cnt from cmr0031 ");
				strQuery.append("where cr_prjno=? ");//PrjNo
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, PrjNo);
	            rs = pstmt.executeQuery();
	            if (rs.next()){
	            	if (rs.getInt("cnt") >0){
	            		throw new Exception("등록된 산출물이 존재합니다. 프로젝트 삭제가 취소 되었습니다.");
	            	}
	            }
	            rs.close();
	            pstmt.close();

	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMD0300 SET ");
	            strQuery.append("CD_CLOSEDT=SYSDATE, ");
	            strQuery.append("CD_STATUS='3', ");
	            strQuery.append("CD_EDITOR =? ");
	            strQuery.append("Where CD_PRJNO =? ");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, UserId);
	            pstmt.setString(2, PrjNo);
				pstmt.executeUpdate();
				pstmt.close();

	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMD0301 SET ");
	            strQuery.append("CD_CLOSEDT=SYSDATE, ");
	            strQuery.append("CD_EDITOR =? ");
	            strQuery.append("Where CD_PRJNO =? ");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, UserId);
	            pstmt.setString(2, PrjNo);
				pstmt.executeUpdate();
				pstmt.close();

	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMD0304 SET ");
	            strQuery.append("CD_CLOSEDT=SYSDATE, ");
	            strQuery.append("CD_EDITOR =? ");
	            strQuery.append("Where CD_PRJNO =? ");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, UserId);
	            pstmt.setString(2, PrjNo);
				pstmt.executeUpdate();
				pstmt.close();

	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMD0305 SET ");
	            strQuery.append("CD_CLSDT=SYSDATE, ");
	            strQuery.append("CD_EDITOR =? ");
	            strQuery.append("Where CD_PRJNO =? ");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, UserId);
	            pstmt.setString(2, PrjNo);
				pstmt.executeUpdate();
				pstmt.close();
				break;
			case 2://플젝 완료처리
				strQuery.setLength(0);
				strQuery.append("UPDATE CMD0300 SET ");
				strQuery.append("CD_EDDATE=SYSDATE, ");
				strQuery.append("CD_STATUS='9', ");
				strQuery.append("CD_EDITOR = ? ");//UserId
				strQuery.append("Where CD_PRJNO = ? ");//PrjNo
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, UserId);
	            pstmt.setString(2, PrjNo);
				pstmt.executeUpdate();
				pstmt.close();
				break;
			}
			conn.commit();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return index;

  		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() SQLException END ##");
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
					ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.Cmd0300_UPDATE() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmd0300_UPDATE() method statement

	
	public Object[] getHTSFileInfo(String cm_syscd, String fileName, String gubun) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			int pstmtCnt = 0;
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_itemid, a.cr_syscd, a.cr_jobcd, a.cr_rsrccd, a.cr_dsncd, a.cr_rsrcname, c.cm_dirpath	\n");
			strQuery.append("from cmr0020 a, cmm0036 b, cmm0070 c		\n");
			strQuery.append("where a.cr_syscd = b.cm_syscd				\n");
			strQuery.append("and a.cr_rsrccd = b.cm_rsrccd				\n");
			strQuery.append("and a.cr_syscd = c.cm_syscd				\n");
			strQuery.append("and a.cr_dsncd = c.cm_dsncd				\n");
			strQuery.append("and substr(b.cm_info,49,1)='1'				\n");
			strQuery.append("and a.cr_lstver>0 							\n");
			strQuery.append("and substr(a.cr_rsrcname, -4) <> '.rec'	\n");
			
			if( !(cm_syscd.equals("") || cm_syscd == null || cm_syscd.equals("00000")) ) {
				strQuery.append("and a.cr_syscd = ?						\n");				
			}
			if( !(fileName.equals("") || fileName == null) ) {
				strQuery.append("and upper(a.cr_rsrcname) like upper(?)	\n");	
				//strQuery.append("and instr( upper(a.cr_rsrcname), upper(?) ) >= 1	\n");	
			}

            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
			if( !(cm_syscd.equals("") || cm_syscd == null || cm_syscd.equals("00000")) ) {
	           	pstmt.setString(++pstmtCnt, cm_syscd);			
			}
			if( !(fileName.equals("") || fileName == null) ) {
				//pstmt.setString(++pstmtCnt, "%"+fileName+"%");	
				pstmt.setString(++pstmtCnt, fileName);	
			}			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
//				rst = new HashMap<String, String>();
//				rst.put("cd_itemid", rs.getString("cr_itemid"));
//				rst.put("cd_syscd", rs.getString("cr_syscd"));
//				rst.put("cd_rsrccd", rs.getString("cr_rsrccd"));
//				rst.put("cd_dsncd", rs.getString("cr_dsncd"));
//				
//				rst.put("cd_dirpath", rs.getString("cm_dirpath"));//				cd_DirPath (파일경로)
//				rst.put("cd_rsrcname", rs.getString("cr_rsrcname"));//				cd_RsrcName (파일명)						
				
				String cr_itemid = rs.getString("cr_itemid");
				String cr_rsrcname = rs.getString("cr_rsrcname");
				
				/*
				if( cr_rsrcname.indexOf(".rps") > 0 ) {
					PreparedStatement pstmt4       = null;
					ResultSet         rs4          = null;	
					
					strQuery.setLength(0);
					strQuery.append("select cr_itemid		\n");
					strQuery.append("from cmr0020			\n");
					strQuery.append("where cr_rsrcname = ?	\n");
					
					pstmt4 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt4.setString(1, cr_rsrcname.replace(".rps", ".rec"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs4 = pstmt4.executeQuery();
					
					if( rs4.next() ) {
						cr_itemid = rs4.getString("cr_itemid");
					}
					
					rs4.close();
					pstmt4.close();
					
					rs4 = null;
					pstmt4 = null;
				}
				*/
				
				PreparedStatement pstmt2       = null;
				ResultSet         rs2          = null;
				
				strQuery.setLength(0);
				strQuery.append("select a.cd_vstgbn, a.cd_media, a.cd_testyn, a.cd_realyn, a.cd_testclsyn, a.cd_realclsyn, 		\n");
				strQuery.append("to_char(a.cd_lastdate, 'YYYY/MM/DD HH24:MI') as cd_lastdate, a.cd_editor, a.cd_itemid,	\n");
				strQuery.append("c.cm_codename as cd_vstgbnname, b.cm_username										\n");
				strQuery.append("from cmd0030 a, cmm0040 b, cmm0020 c													\n");
				strQuery.append("where a.cd_itemid = ?																\n");
				strQuery.append("and a.cd_editor = b.cm_userid														\n");
				strQuery.append("and c.cm_macode = 'HTSVST'															\n");
				strQuery.append("and c.cm_micode <> '****'															\n");
				strQuery.append("and a.cd_vstgbn = c.cm_codename													\n");				
//				strQuery.append("and a.cd_vstgbn = c.cm_micode														\n");

				pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, cr_itemid);
				//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				rs2 = pstmt2.executeQuery();

				if(rs2.next()) {
					
					if(gubun.equals("ALL") || gubun.equals("REG")){
						rst = new HashMap<String, String>();
						rst.put("cd_itemid", rs.getString("cr_itemid"));
						rst.put("cd_syscd", rs.getString("cr_syscd"));
						rst.put("cd_rsrccd", rs.getString("cr_rsrccd"));
						rst.put("cd_jobcd", rs.getString("cr_jobcd"));
						rst.put("cd_dsncd", rs.getString("cr_dsncd"));
						
						rst.put("cd_dirpath", chgVolPath(rs.getString("cr_syscd"), rs.getString("cm_dirpath"), rs.getString("cr_rsrccd"), rs.getString("cr_jobcd"), conn));
						//rst.put("cd_dirpath", rs.getString("cm_dirpath"));//				cd_DirPath (파일경로)
						
						cr_rsrcname = rs.getString("cr_rsrcname");
						
						if(cr_rsrcname.indexOf(".rps") > 0) {
							cr_rsrcname = cr_rsrcname.replace(".rps", ".rec");
						}
						
						rst.put("cd_rsrcname", cr_rsrcname);//				cd_RsrcName (파일명)			
						
						rst.put("cd_vstgbn", rs2.getString("cd_vstgbnname"));//				cd_VstGbn (VST구분)
						rst.put("cd_testyn", rs2.getString("cd_testyn"));//				cd_TestYN (테스트적용여부)
						rst.put("cd_realyn", rs2.getString("cd_realyn"));//				cd_RealYN (운영적용여부)
						rst.put("cd_testclsyn", rs2.getString("cd_testclsyn"));//				cd_TestClsYN (테스트폐기여부)
						rst.put("cd_realclsyn", rs2.getString("cd_realclsyn"));//				cd_RealClsYN (운영폐기여부)
						rst.put("cd_lastdate", rs2.getString("cd_lastdate"));//				cd_LastDate (최종변경일시)
						rst.put("cd_editor", rs2.getString("cm_username"));//				cd_Editor (최종변경인)		
						
						if( (rs2.getString("cd_media") != null) && !rs2.getString("cd_media").equals("") ) {
							String mediaCode = rs2.getString("cd_media");
							String mediaCodeName = "";
							int mediaCodeCnt = mediaCode.length()/3;
							int tmpI = 0;
										
							for(int i=0; i<mediaCodeCnt; i++) {
								PreparedStatement pstmt3       = null;
								ResultSet         rs3          = null;			
								
								strQuery.setLength(0);
								strQuery.append("select cm_codename as cd_medianame 		\n");
								strQuery.append("from cmm0020 								\n");
								strQuery.append("where cm_macode = 'HTSMEDIA' 				\n");
								strQuery.append("and cm_micode <> '****' 					\n");
								strQuery.append("and cm_micode = ? 							\n");
								
								pstmt3 = conn.prepareStatement(strQuery.toString());
								//pstmt3 = new LoggableStatement(conn,strQuery.toString());
								pstmt3.setString(1, mediaCode.substring(tmpI, (tmpI+3)));
								//ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
								rs3 = pstmt3.executeQuery();
								
								if(rs3.next()) {
									mediaCodeName += rs3.getString("cd_medianame")+"/";
									tmpI += 3;
								}
								pstmt3.close();
								rs3.close();
								
								pstmt3 = null;
								rs3 = null;
							}
							rst.put("cd_media", mediaCodeName);//				cd_Media (매체구분)			
							
							mediaCode = null;
							mediaCodeName = null;
						} else {
							rst.put("cd_media", "");//				cd_Media (매체구분)
						}
			           	rsval.add(rst);
			           	rst = null;
					}
				} else {
					if(gubun.equals("ALL") || gubun.equals("NON")) {
						rst = new HashMap<String, String>();
						rst.put("cd_itemid", rs.getString("cr_itemid"));
						rst.put("cd_syscd", rs.getString("cr_syscd"));
						rst.put("cd_rsrccd", rs.getString("cr_rsrccd"));
						rst.put("cd_jobcd", rs.getString("cr_jobcd"));
						rst.put("cd_dsncd", rs.getString("cr_dsncd"));
						
						rst.put("cd_dirpath", chgVolPath(rs.getString("cr_syscd"), rs.getString("cm_dirpath"), rs.getString("cr_rsrccd"), rs.getString("cr_jobcd"), conn));
						//rst.put("cd_dirpath", rs.getString("cm_dirpath"));//				cd_DirPath (파일경로)
						
						cr_rsrcname = rs.getString("cr_rsrcname");
						
						if(cr_rsrcname.indexOf(".rps") > 0) {
							cr_rsrcname = cr_rsrcname.replace(".rps", ".rec");
						}
						
						rst.put("cd_rsrcname", cr_rsrcname);//				cd_RsrcName (파일명)
						
					
						rst.put("cd_vstgbn", "");//				cd_VstGbn (VST구분)
						rst.put("cd_media", "");//				cd_Media (매체구분)
						rst.put("cd_testyn", "");//				cd_TestYN (테스트적용여부)
						rst.put("cd_realyn", "");//				cd_RealYN (운영적용여부)
						rst.put("cd_testclsyn", "");//				cd_TestClsYN (테스트폐기여부)
						rst.put("cd_realclsyn", "");//				cd_RealClsYN (운영폐기여부)
						rst.put("cd_lastdate", "");//				cd_LastDate (최종변경일시)
						rst.put("cd_editor", "");//				cd_Editor (최종변경인)		
			           	rsval.add(rst);
			           	rst = null;
					}
				}


				
				
				
	           	//rsval.add(rst);
	           	//rst = null;
	           	
	           	pstmt2.close();
	           	rs2.close();
	           	
	           	pstmt2 = null;
	           	rs2 = null;
	           	
	           	cr_itemid = null;
	           	cr_rsrcname = null;
			}//end of while-loop statement

			
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getHTSFileInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.getHTSFileInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getHTSFileInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.getHTSFileInfo() Exception END ##");
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
					ecamsLogger.error("## Cmd0300.getHTSFileInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHTSFileInfo() method statement	

	public Object[] getHTSFileInfo2(String cm_syscd, ArrayList<HashMap<String,String>> fileNameList, boolean isNoRegFlag) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			
			conn = connectionContext.getConnection();
			
			int fileNameLength = fileNameList.size(); //조회를 요청한 파일의 수
			final int maxCnt = 1000; //1회당 최대 조회 수(in 구분자 최대 1000개 제한)
			int loopCnt = fileNameLength / maxCnt; //조회요청 파일을 maxCnt만큼 나눈 몫, 총 for횟수
			int tmpMod = fileNameLength % maxCnt; //조회요청 파일을 maxCnt만큼 나눈 나머지
			
			if(tmpMod != 0) loopCnt++; //나머지 값이 있다면 총 for문 횟수 증가
			
			for(int tmpCnt=0; tmpCnt<loopCnt; tmpCnt++)	{ //파일갯수/maxCnt 만큼		
				int pstmtCnt = 0;
				int innerLoopCnt = 0; //내부 for문 횟수
				
				if( (tmpCnt+1) == loopCnt ) innerLoopCnt = fileNameLength;//(tmpCnt * maxCnt) + tmpMod; //마지막 반복일 때, 내부 반복횟수를 파일갯수만큼지정
				else innerLoopCnt = (tmpCnt+1) * maxCnt; //일반 반복일 때, 내부반복횟수를 다음 maxCnt만큼 지정
				
				strQuery.setLength(0);
				strQuery.append("select a.cr_itemid, a.cr_syscd, a.cr_jobcd, a.cr_rsrccd, a.cr_dsncd, a.cr_rsrcname, c.cm_dirpath	\n");
				strQuery.append("from cmr0020 a, cmm0036 b, cmm0070 c		\n");
				strQuery.append("where a.cr_syscd = b.cm_syscd				\n");
				strQuery.append("and a.cr_rsrccd = b.cm_rsrccd				\n");
				strQuery.append("and a.cr_syscd = c.cm_syscd				\n");
				strQuery.append("and a.cr_dsncd = c.cm_dsncd				\n");
				strQuery.append("and substr(b.cm_info,49,1)='1'				\n");
				strQuery.append("and a.cr_lstver>0 							\n");
				strQuery.append("and substr(a.cr_rsrcname, -4) <> '.rec'	\n");
				
				if( !(cm_syscd.equals("") || cm_syscd == null || cm_syscd.equals("00000")) ) {
					strQuery.append("and a.cr_syscd = ?						\n");				
				}
				
				if(fileNameList != null) {
					//int fileNameLength = fileNameList.size();
					
					//strQuery.append("and upper(a.cr_rsrcname) in (	\n");
					strQuery.append("and a.cr_itemid in (	\n");
					for(int i=(tmpCnt*maxCnt); i<innerLoopCnt; i++) { //현재반복수*maxCnt로 시작값 지정
						strQuery.append("?					\n");
						if((i+1) != innerLoopCnt) {
							strQuery.append(",		\n");
						}
					}
					strQuery.append(")				\n");
					
					/*
					strQuery.append("and (	\n");
					for(int i=(tmpCnt*maxCnt); i<innerLoopCnt; i++) {
						strQuery.append("a.cr_itemid = ?					\n");
						if((i+1) != innerLoopCnt) {
							strQuery.append("or		\n");
						}
					}
					strQuery.append(")				\n");
					*/
				}
	
	            pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn,strQuery.toString());
				
				if( !(cm_syscd.equals("") || cm_syscd == null || cm_syscd.equals("00000")) ) {
		           	pstmt.setString(++pstmtCnt, cm_syscd);			
				}
				
				if(fileNameList != null) {
					//int fileNameLength = fileNameList.size();
					
					for(int i=(tmpCnt*maxCnt); i<innerLoopCnt; i++) {
						//pstmt.setString(++pstmtCnt, fileNameList.get(i).get("cr_rsrcname"));	
						//String cr_itemid = fileNameList.get(i).get("cr_itemid");
						//String cr_rsrcname = fileNameList.get(i).get("cr_rsrcname");
						
						/*
						if( cr_rsrcname.indexOf(".rps") > 0 ) {
							PreparedStatement pstmt4       = null;
							ResultSet         rs4          = null;	
							
							strQuery.setLength(0);
							strQuery.append("select cr_itemid		\n");
							strQuery.append("from cmr0020			\n");
							strQuery.append("where cr_rsrcname = ?	\n");
							
							pstmt4 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt4.setString(1, cr_rsrcname.replace(".rps", ".rec"));
							//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
							rs4 = pstmt4.executeQuery();
							
							if( rs4.next() ) {
								cr_itemid = rs4.getString("cr_itemid");
							}
							
							rs4.close();
							pstmt4.close();
							
							rs4 = null;
							pstmt4 = null;
						}
						*/
						
						pstmt.setString(++pstmtCnt, fileNameList.get(i).get("cr_itemid"));
						//cr_itemid = null;
						//cr_rsrcname = null;
					}
				}
				
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	
				while (rs.next()){
					if( isNoRegFlag == true ) { //미등록건만 조회시
						PreparedStatement pstmt4       = null;
						ResultSet         rs4          = null;			
						
						strQuery.setLength(0);
						strQuery.append("select count(*) as cnt		\n");
						strQuery.append("from cmd0030				\n");
						strQuery.append("where cd_itemid = ?		\n");
						
						pstmt4 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
						pstmt4.setString(1, rs.getString("cr_itemid"));
						//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
						rs4 = pstmt4.executeQuery();
						rs4.next();
						
						if(rs4.getInt("cnt") != 0) {
							rs4.close();
							pstmt4.close();
							
							rs4 = null;
							pstmt4 = null;
							
							continue;
						}else {
							rs4.close();
							pstmt4.close();
							
							rs4 = null;
							pstmt4 = null;
						}
					}
					
					rst = new HashMap<String, String>();
					rst.put("cd_itemid", rs.getString("cr_itemid"));
					rst.put("cd_syscd", rs.getString("cr_syscd"));
					rst.put("cd_rsrccd", rs.getString("cr_rsrccd"));
					rst.put("cd_jobcd", rs.getString("cr_jobcd"));
					rst.put("cd_dsncd", rs.getString("cr_dsncd"));
					
					rst.put("cd_dirpath", chgVolPath(rs.getString("cr_syscd"), rs.getString("cm_dirpath"), rs.getString("cr_rsrccd"), rs.getString("cr_jobcd"), conn));
					//rst.put("cd_dirpath", rs.getString("cm_dirpath"));//				cd_DirPath (파일경로)
					
					String cd_rsrcname = rs.getString("cr_rsrcname");
					
					if( cd_rsrcname.indexOf(".rps") > 0 ) {
						cd_rsrcname = cd_rsrcname.replace(".rps", ".rec");
					}
					
					rst.put("cd_rsrcname", cd_rsrcname);//				cd_RsrcName (파일명)
					
					
					PreparedStatement pstmt2       = null;
					ResultSet         rs2          = null;
					
					strQuery.setLength(0);
					strQuery.append("select a.cd_vstgbn, a.cd_media, a.cd_testyn, a.cd_realyn, a.cd_testclsyn, a.cd_realclsyn, 		\n");
					strQuery.append("to_char(a.cd_lastdate, 'YYYY/MM/DD HH24:MI') as cd_lastdate, a.cd_editor, a.cd_itemid,	\n");
					strQuery.append("c.cm_codename as cd_vstgbnname, b.cm_username										\n");
					strQuery.append("from cmd0030 a, cmm0040 b, cmm0020 c													\n");
					strQuery.append("where a.cd_itemid = ?																\n");
					strQuery.append("and a.cd_editor = b.cm_userid														\n");
					strQuery.append("and c.cm_macode = 'HTSVST'															\n");
					strQuery.append("and c.cm_micode <> '****'															\n");
					strQuery.append("and a.cd_vstgbn = c.cm_codename													\n");				
	//				strQuery.append("and a.cd_vstgbn = c.cm_micode														\n");
					
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_itemid"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
	
					if(rs2.next()) {
						rst.put("cd_vstgbn", rs2.getString("cd_vstgbnname"));//				cd_VstGbn (VST구분)
						rst.put("cd_testyn", rs2.getString("cd_testyn"));//				cd_TestYN (테스트적용여부)
						rst.put("cd_realyn", rs2.getString("cd_realyn"));//				cd_RealYN (운영적용여부)
						rst.put("cd_testclsyn", rs2.getString("cd_testclsyn"));//				cd_TestClsYN (테스트폐기여부)
						rst.put("cd_realclsyn", rs2.getString("cd_realclsyn"));//				cd_RealClsYN (운영폐기여부)
						rst.put("cd_lastdate", rs2.getString("cd_lastdate"));//				cd_LastDate (최종변경일시)
						rst.put("cd_editor", rs2.getString("cm_username"));//				cd_Editor (최종변경인)		
						
						if( (rs2.getString("cd_media") != null) && !rs2.getString("cd_media").equals("") ) {
							String mediaCode = rs2.getString("cd_media");
							String mediaCodeName = "";
							int mediaCodeCnt = mediaCode.length()/3;
							int tmpI = 0;
										
							for(int i=0; i<mediaCodeCnt; i++) {
								PreparedStatement pstmt3       = null;
								ResultSet         rs3          = null;			
								
								strQuery.setLength(0);
								strQuery.append("select cm_codename as cd_medianame 		\n");
								strQuery.append("from cmm0020 								\n");
								strQuery.append("where cm_macode = 'HTSMEDIA' 				\n");
								strQuery.append("and cm_micode <> '****' 					\n");
								strQuery.append("and cm_micode = ? 							\n");
								
								pstmt3 = conn.prepareStatement(strQuery.toString());
								//pstmt3 = new LoggableStatement(conn,strQuery.toString());
								pstmt3.setString(1, mediaCode.substring(tmpI, (tmpI+3)));
								//ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
								rs3 = pstmt3.executeQuery();
								
								if(rs3.next()) {
									mediaCodeName += rs3.getString("cd_medianame")+"/";
									tmpI += 3;
								}
								pstmt3.close();
								rs3.close();
								
								pstmt3 = null;
								rs3 = null;
							}
							rst.put("cd_media", mediaCodeName);//				cd_Media (매체구분)			
							
							mediaCode = null;
							mediaCodeName = null;
						} else {
							rst.put("cd_media", "");//				cd_Media (매체구분)
						}
						
	
					} else {
						rst.put("cd_vstgbn", "");//				cd_VstGbn (VST구분)
						rst.put("cd_media", "");//				cd_Media (매체구분)
						rst.put("cd_testyn", "");//				cd_TestYN (테스트적용여부)
						rst.put("cd_realyn", "");//				cd_RealYN (운영적용여부)
						rst.put("cd_testclsyn", "");//				cd_TestClsYN (테스트폐기여부)
						rst.put("cd_realclsyn", "");//				cd_RealClsYN (운영폐기여부)
						rst.put("cd_lastdate", "");//				cd_LastDate (최종변경일시)
						rst.put("cd_editor", "");//				cd_Editor (최종변경인)	
						//rst.put("selected", "1");
					}
	
		           	rsval.add(rst);
		           	rst = null;
		           	
		           	pstmt2.close();
		           	rs2.close();
		           	
		           	pstmt2 = null;
		           	rs2 = null;
		           	cd_rsrcname = null;
				}//end of while-loop statement
	
				
				rs.close();
				pstmt.close();
				
	
				rs = null;
				pstmt = null;
			}
			
			conn.close();
			conn = null;
			
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getHTSFileInfo2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.getHTSFileInfo2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getHTSFileInfo2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.getHTSFileInfo2() Exception END ##");
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
					ecamsLogger.error("## Cmd0300.getHTSFileInfo2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHTSFileInfo() method statement		
	
	public String setVSTInfo(ArrayList<HashMap<String,String>> itemIdList) throws SQLException, Exception{
		
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet		  rs		  = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);	

			int cnt = itemIdList.size();
			
			for (int i=0; i<cnt; i++) {
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt		\n");
				strQuery.append("from cmd0030				\n");
				strQuery.append("where cd_itemid = ?		\n");		
				
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, itemIdList.get(i).get("cd_itemid"));	
	            rs = pstmt.executeQuery();
	            
	            rs.next();
	            
	            if(rs.getInt("cnt") == 0) {
					int pstmtCnt = 0;
					PreparedStatement pstmt2       = null;
					
					strQuery.setLength(0);
					strQuery.append("insert into cmd0030																\n");
					strQuery.append("(cd_itemid, cd_dirpath, cd_rsrcname, cd_vstgbn, cd_media, cd_lastdate, cd_editor)	\n");
					strQuery.append("values (?, ?, ?, ?, ?, sysdate, ?)													\n");

		            pstmt2 = conn.prepareStatement(strQuery.toString());
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_itemid"));	
		            pstmt2.setString(++pstmtCnt, chgVolPath(itemIdList.get(i).get("cd_syscd"), itemIdList.get(i).get("cd_dirpath"), itemIdList.get(i).get("cd_rsrccd"), itemIdList.get(i).get("cd_jobcd"), conn));
		            //pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_dirpath"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_rsrcname"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_vstgbn"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_media"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_editor"));	
		            pstmt2.executeUpdate();

		            pstmt2.close();
		            pstmt2 = null;            	
	            } else {
					int pstmtCnt = 0;
					PreparedStatement pstmt2       = null;
					
					strQuery.setLength(0);
					strQuery.append("update cmd0030				\n");
					strQuery.append("set cd_dirpath = ?,		\n");
					strQuery.append("	 cd_rsrcname = ?,		\n");
					strQuery.append("	 cd_vstgbn = ?,			\n");
					strQuery.append("	 cd_media = ?,			\n");
					strQuery.append("	 cd_editor = ?,			\n");
					strQuery.append("	 cd_lastdate = sysdate	\n");
					strQuery.append("where cd_itemid = ?		\n");
	
		            pstmt2 = conn.prepareStatement(strQuery.toString());
		            pstmt2.setString(++pstmtCnt, chgVolPath(itemIdList.get(i).get("cd_syscd"), itemIdList.get(i).get("cd_dirpath"), itemIdList.get(i).get("cd_rsrccd"), itemIdList.get(i).get("cd_jobcd"), conn));
		            //pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_dirpath"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_rsrcname"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_vstgbn"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_media"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_editor"));	
		            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cd_itemid"));			            
		            pstmt2.executeUpdate();

		            pstmt2.close();
		            pstmt2 = null;  	            	
	            }
	            rs.close();
	            pstmt.close();
			}
			 rs = null;
            pstmt = null;   
            conn.commit();
            conn.close();
            
		    return "OK";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.setVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.setVSTInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.setVSTInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.setVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.setVSTInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.setVSTInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);					
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.setVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setVSTInfo() method statement		
	
	public String delVSTInfo(ArrayList<HashMap<String,String>> itemIdList) throws SQLException, Exception{
		
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			int cnt = itemIdList.size();
			
			for (int i=0; i<cnt; i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmd0030			\n");
				strQuery.append("where cd_itemid = ?	\n");
				
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, itemIdList.get(i).get("cd_itemid"));				
	            pstmt.executeUpdate();

	            pstmt.close();
	            pstmt = null;
			}
	        conn.commit();
	        conn.close();
			
		    return "OK";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.delVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.delVSTInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.delVSTInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.delVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.delVSTInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.delVSTInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.delVSTInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delVSTInfo() method statement		
	
	public Object[] getNullVst(ArrayList<HashMap<String,String>> itemIdList) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			int cnt = itemIdList.size();
			String cr_itemid = "";
			String cr_rsrcname = "";
			//ecamsLogger.error("cnt="+cnt);
			for (int i=0; i<cnt; i++) {
				
				cr_itemid = itemIdList.get(i).get("cr_itemid");
				cr_rsrcname = itemIdList.get(i).get("cr_rsrcname");
				
				//ecamsLogger.error("cr_rsrcname = " + cr_rsrcname + "rsrccd = " + itemIdList.get(i).get("cr_rsrccd"));
				
				/*
				if( cr_rsrcname.indexOf(".rps") > 0 ) {
					//cr_rsrcname = cr_rsrcname.replace(".rps", ".rec");
            		
					PreparedStatement pstmt2       = null;
					ResultSet         rs2          = null;			
					
					strQuery.setLength(0);
					strQuery.append("select cr_itemid 			\n");
					strQuery.append("from cmr0020 				\n");
					strQuery.append("where cr_rsrcname = ? 		\n");
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, cr_rsrcname.replace(".rps", ".rec"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					
					if(rs2.next()) {
						cr_itemid = rs2.getString("cr_itemid");
						
					}
					
            		rs2.close();
					pstmt2.close();
					
					rs2 = null;
					pstmt2 = null;
            	}
				*/
				if( cr_rsrcname.indexOf(".rec") > 0 ) {
					continue;
				}
				//ecamsLogger.error("itemid="+itemIdList.get(i).get("cr_itemid"));
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt		\n");
				strQuery.append("from cmd0030				\n");
				strQuery.append("where cd_itemid = ?		\n");		
				
	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, cr_itemid);	
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            
	            rs.next();
	            
	            if(rs.getInt("cnt") == 0) {
	            	
	            	if( itemIdList.get(i).get("cr_rsrccd").equals("52") || itemIdList.get(i).get("cr_rsrccd").equals("53") ) { //52 - 보고서파일, 53 - 통합단말 _화면파일
						int pstmtCnt = 0;
						PreparedStatement pstmt2 = null;
						
						strQuery.setLength(0);
						strQuery.append("insert into cmd0030																\n");
						strQuery.append("(cd_itemid, cd_dirpath, cd_rsrcname, cd_vstgbn, cd_media, cd_lastdate, cd_editor)	\n");
						strQuery.append("values (?, ?, ?, ?, ?, sysdate, ?)													\n");

			            pstmt2 = conn.prepareStatement(strQuery.toString());
			            pstmt2.setString(++pstmtCnt, cr_itemid);//cr_itemid itemIdList.get(i).get("cr_itemid")	
			            pstmt2.setString(++pstmtCnt, chgVolPath(itemIdList.get(i).get("cr_syscd"), itemIdList.get(i).get("cm_dirpath"), itemIdList.get(i).get("cr_rsrccd"), itemIdList.get(i).get("cr_jobcd"), conn));
			            
			            if( itemIdList.get(i).get("cr_rsrccd").equals("52") ) { //파일 종류가 보고서파일인 경우
			            	pstmt2.setString(++pstmtCnt, cr_rsrcname.replace(".rps", ".rec"));	
			            	pstmt2.setString(++pstmtCnt, "reportfile.vst");	//cmm0020에 HTSVST의 코드
			            } else if ( itemIdList.get(i).get("cr_rsrccd").equals("53") ) {//파일 종류가 통합단말_화면파일인 경우
			            	pstmt2.setString(++pstmtCnt, cr_rsrcname);	
			            	pstmt2.setString(++pstmtCnt, "screen.vst");	//cmm0020에 HTSVST의 코드
			            }
			            pstmt2.setString(++pstmtCnt, "011021");	//cmm0020에 HTSMEDIA의 코드 011 - 직원, 021 - CTI
			            
			            
			            pstmt2.setString(++pstmtCnt, itemIdList.get(i).get("cm_editor"));	
			            pstmt2.executeUpdate();

			            pstmt2.close();
			            pstmt2 = null;    
	            	} else { //그 외
		            	rst = new HashMap<String, String>();
		            	rst.put("cr_itemid",cr_itemid);
		            	rst.put("cr_rsrcname",cr_rsrcname);
			           	rsval.add(rst);
			           	rst = null;
	            	}
	            } 
	            rs.close();
	            pstmt.close();			
				
				rs = null;
				pstmt = null;
				cr_itemid = null;
				cr_rsrcname = null;
			}

			conn.close();
			conn = null;
			
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getNullVst() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.getNullVst() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getNullVst() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.getNullVst() Exception END ##");
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
					ecamsLogger.error("## Cmd0300.getNullVst() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHTSFileInfo() method statement		
	
	public String chgVolPath(String SysCd, String DirPath, String RsrcCd, String JobCd, Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            svrDir     = null;
		boolean           eclipseSw  = false;

		try {
			strQuery.append("select a.cm_svrcd,a.cm_volpath,substr(c.cm_sysinfo,13,1) eclipse   \n");
			strQuery.append("  from cmm0038 a,cmm0031 b,cmm0030 c                      \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_svrcd='01' and b.cm_closedt is null \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?            \n");
			strQuery.append("   and b.cm_syscd=c.cm_syscd                              \n");

			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
//            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("eclipse").equals("1")) eclipseSw = true;
			}
            rs.close();
            pstmt.close();
            
//            ecamsLogger.error("eclipseSw = "+eclipseSw);
//            ecamsLogger.error("JobCd = "+JobCd);
//            ecamsLogger.error("DirPath = "+DirPath);
            if (eclipseSw && JobCd.length() == 5 && DirPath.length()>=13) { //딱 13자리까지 포함
            	svrDir = "/ecams/" + JobCd.substring(3) + "/" + JobCd.substring(0,3);
//            	ecamsLogger.error("svrDir = "+svrDir);
//            	ecamsLogger.error("DirPath2 = "+DirPath.substring(0,13));
//            	ecamsLogger.error("DirPath2 = "+DirPath.substring(0,13).equals(svrDir));
            	if (DirPath.substring(0,13).equals(svrDir)) {
//            		ecamsLogger.error("DirPath2 length = "+DirPath.length());
            		if(DirPath.length() > 13) { //13자리 이상이면 뒷 부분만
            			DirPath = DirPath.substring(13).substring(1);
            		}else { //그 이하거나 13자리면 공백으로
            			DirPath = "";
            		}
//            		ecamsLogger.error("DirPath2 = "+DirPath);
            	}
            }

			rs = null;
			pstmt = null;

            return DirPath;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0030.chgVolPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0030.chgVolPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0030.chgVolPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0030.chgVolPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
	
	public Object[] getFileList_excel(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String cd_syscd = ""; //시스템코드
			String cd_rsrccd = ""; //파일유형
			String cd_itemid = ""; //파일고유관리번호
			String cd_jobcd = ""; //업무코드
			
			String fullPath = ""; //파일경로 + 파일명
			String realPath = ""; //실제 파일 경로
			String cd_dirpath = ""; //파일경로
			String cd_rsrcname = ""; //파일명	
			String cd_vstgbn = ""; //VST파일
			String cd_media = ""; //매체구분코드
			String errMsg = ""; //에러메세지

			boolean errSw = false;
			boolean targetSw = false;
			
			int pathIndex = 0; //전체경로명에서 "/"의 마지막 자릿수
			
			rsval.clear();
			for (int i=0 ; i<fileList.size() ; i++)
			{
				rst = new HashMap<String, String>();
				errSw = false;
				errMsg = "";
				pathIndex = 0;
				cd_dirpath = "";
				cd_rsrcname = "";
				
				fullPath = "";

				fullPath = fileList.get(i).get("cd_dirpathFull").trim();
				
				if( (fullPath != null) && !fullPath.equals("") ) {
					pathIndex = fullPath.lastIndexOf("/");

					if(pathIndex > 0) {
						cd_dirpath = fullPath.substring(0, pathIndex);
						cd_rsrcname = fullPath.substring(pathIndex+1);
					} else {
						cd_dirpath = "";
						cd_rsrcname = fullPath;
					}
				}else {
					errSw = true;
					errMsg += "전체경로명 미입력";
				}
				
				cd_vstgbn = "";
				if( (fileList.get(i).get("cd_vstgbn") != null) && !fileList.get(i).get("cd_vstgbn").equals("")  ) {
					cd_vstgbn = fileList.get(i).get("cd_vstgbn").trim();
				} else {
					errMsg += "VST파일미입력/";
					errSw = true;
				}
				
				int pstmtcnt = 0;
				
				strQuery.setLength(0); /*파일명으로 파일정보 조회*/
				strQuery.append("SELECT a.cr_syscd, a.cr_rsrccd, a.cr_itemid, a.cr_dsncd, a.cr_rsrcname, a.cr_jobcd, b.cm_dirpath	\n");//
				strQuery.append("FROM cmr0020 a,cmm0070 b 										        							\n");
				strQuery.append("WHERE a.cr_rsrcname = ?																			\n");
				strQuery.append("  AND a.cr_lstver>0 																				\n");
				strQuery.append("  AND a.cr_syscd=b.cm_syscd 																		\n");
				strQuery.append("  AND a.cr_dsncd=b.cm_dsncd 																		\n");
				strQuery.append("  AND b.cm_dirpath like ? 																			\n");
				

                pstmt = conn.prepareStatement(strQuery.toString());
//    			pstmt = new LoggableStatement(conn,strQuery.toString());
             
                pstmt.setString(++pstmtcnt, cd_rsrcname);
                pstmt.setString(++pstmtcnt, "%"+cd_dirpath);
//              ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());  
                
                rs = pstmt.executeQuery();
                
    			cd_syscd = "";
    			cd_rsrccd = ""; 
    			cd_itemid = "";
    			cd_jobcd = "";
    			realPath = "";
    			targetSw = false;
    			
    			while( rs.next() ) {
        			cd_syscd = rs.getString("cr_syscd");
        			cd_rsrccd = rs.getString("cr_rsrccd");
        			cd_itemid = rs.getString("cr_itemid");
        			cd_jobcd = rs.getString("cr_jobcd");
        			realPath = rs.getString("cm_dirpath");
        			
        			if( cd_dirpath.equals("") && ((cd_jobcd != null) && !cd_jobcd.equals("")) ){ //파일경로가 없으면 기본경로 지정
        				cd_dirpath = "/ecams/" + cd_jobcd.substring(3) + "/" + cd_jobcd.substring(0,3);
        			}
        			
        			String tmpPath = "/ecams/" + cd_jobcd.substring(3) + "/" + cd_jobcd.substring(0,3) + "/" + cd_dirpath;
        			if( realPath.equals(tmpPath) ) {
        				targetSw = true;
        				break;
        			}
    			}
    			
                if( targetSw ) {
//        			cd_syscd = rs.getString("cr_syscd");
//        			cd_rsrccd = rs.getString("cr_rsrccd");
//        			cd_itemid = rs.getString("cr_itemid");
//        			cd_jobcd = rs.getString("cr_jobcd");
//        			realPath = rs.getString("cm_dirpath");
        			
//        			if( cd_dirpath.equals("") && ((cd_jobcd != null) && !cd_jobcd.equals("")) ){ //파일경로가 없으면 기본경로 지정
//        				cd_dirpath = "/ecams/" + cd_jobcd.substring(3) + "/" + cd_jobcd.substring(0,3);
//        			}
/*                	
    				pstmtcnt = 0;
    				
    				strQuery.setLength(0); //파일경로 정보 조회
    				strQuery.append("SELECT cm_dirpath			\n");
    				strQuery.append("FROM cmm0070				\n");
    				strQuery.append("WHERE cm_syscd = ?			\n");
    				strQuery.append("AND cm_dsncd = ?			\n");
    				strQuery.append("AND cm_dirpath LIKE ?		\n");
    				
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    
                    pstmt2.setString(++pstmtcnt, rs.getString("cr_syscd"));
                    pstmt2.setString(++pstmtcnt, rs.getString("cr_dsncd"));
                    pstmt2.setString(++pstmtcnt, "%"+cd_dirpath+"%");      
                    
                    rs2 = pstmt2.executeQuery();
                    
                    realPath = "";
                    if(rs2.next()) {
*/                    	
    				
    				
//                	realPath = rs2.getString("cm_dirpath");
//                    rs2.close();
//                    pstmt2.close();
                	
            		PreparedStatement pstmt3      = null;
            		ResultSet         rs3         = null;
            		
    				pstmtcnt = 0;
    				strQuery.setLength(0);
    				strQuery.append("SELECT count(*) cnt					\n");
    				strQuery.append("FROM cmm0020							\n");
    				strQuery.append("WHERE cm_macode = 'HTSVST'				\n");
    				strQuery.append("AND cm_closedt is null					\n");
    				strQuery.append("AND cm_micode <> '****'				\n");
    				strQuery.append("AND lower(cm_codename) = lower(?)		\n");

                    pstmt3 = conn.prepareStatement(strQuery.toString());
                    
                    pstmt3.setString(++pstmtcnt, cd_vstgbn);
                    
                    rs3 = pstmt3.executeQuery();
                    rs3.next();
                    
                    if(rs3.getInt("cnt") == 0) {
                    	errMsg = errMsg + "미등록된VST파일명/";
                    	errSw = true;  
                    }                         
    				rs3.close();
    				pstmt3.close();	 		

    				/*        			
        				strQuery.setLength(0);
        				strQuery.append("SELECT COUNT(*) AS cnt			\n");
        				strQuery.append("FROM cmm0020					\n");
        				strQuery.append("WHERE cm_macode = 'HTSMEDIA'	\n");
        				strQuery.append("AND cm_micode <> '****'		\n");
        				strQuery.append("AND cm_closedt is null			\n");
        				
        				pstmt3 = conn.prepareStatement(strQuery.toString());
        				rs3 = pstmt3.executeQuery();
                        rs3.next();
                        
                        ArrayList<String> cd_mediaCode = new ArrayList<String>();
//                        String[] cd_mediaCode = new String[rs3.getInt("cnt")]; //매체구분코드 갯수
                        
                        rs3.close();
        				pstmt3.close();	 
*/        				
    				ArrayList<String> MediaCodeArray = new ArrayList<String>();
    				int mediaCnt = 0; //입력한 매체구분 수
    				
    				if( (fileList.get(i).get("cd_media01") != null) && !fileList.get(i).get("cd_media01").equals("") ) {
    					mediaCnt++;
    					MediaCodeArray.add(fileList.get(i).get("cd_media01").trim());
    				} 

    				if( (fileList.get(i).get("cd_media02") != null) && !fileList.get(i).get("cd_media02").equals("") ) {
    					mediaCnt++;
    					MediaCodeArray.add(fileList.get(i).get("cd_media02").trim());
    				}
    				
    				if( (fileList.get(i).get("cd_media03") != null) && !fileList.get(i).get("cd_media03").equals("") ) {
    					mediaCnt++;
    					MediaCodeArray.add(fileList.get(i).get("cd_media03").trim());
    				}
    				
    				if( (fileList.get(i).get("cd_media04") != null) && !fileList.get(i).get("cd_media04").equals("") ) {
    					mediaCnt++;
    					MediaCodeArray.add(fileList.get(i).get("cd_media04").trim());
    				}
    				
    				if( (fileList.get(i).get("cd_media05") != null) && !fileList.get(i).get("cd_media05").equals("") ) {
    					mediaCnt++;
    					MediaCodeArray.add(fileList.get(i).get("cd_media05").trim());
    				}
    				
    				cd_media = "";
    				if(mediaCnt > 0) {
    					pstmtcnt = 0;
    					
        				strQuery.setLength(0);
        				strQuery.append("SELECT cm_micode				\n");
        				strQuery.append("FROM cmm0020					\n");
        				strQuery.append("WHERE cm_macode = 'HTSMEDIA'	\n");
        				strQuery.append("AND cm_micode <> '****'		\n");
        				strQuery.append("AND cm_closedt is null			\n");
        				strQuery.append("AND cm_micode in (				\n");
        				for(int j=0; j<mediaCnt; j++) {
        					if(j > 0) {
        						strQuery.append(",						\n");
        					}
        					strQuery.append("?							\n");	        					
        				}
        				strQuery.append(")								\n");
        				strQuery.append("ORDER BY cm_micode				\n");
        				
        				pstmt3 = conn.prepareStatement(strQuery.toString());
        				
        				for(int j=0; j<mediaCnt; j++) {
        					pstmt3.setString(++pstmtcnt, MediaCodeArray.get(j));
        				}
        				
        				rs3 = pstmt3.executeQuery();
        				
        				
        				while ( rs3.next() ) {
        					cd_media += rs3.getString("cm_micode");
        				}
        				
        				if( ((cd_media == null) || cd_media.equals("")) || (cd_media.length() != (mediaCnt*3)) ) {
                        	errMsg = errMsg + "미등록매체구분코드입력/";
                        	errSw = true;  	        					
        				}
        				
                        rs3.close();
                        pstmt3.close();
                        MediaCodeArray = null;
                        
    				} else {
                    	errMsg = errMsg + "매체구분미입력/";
                    	errSw = true;  
    				}
/*    				
                    } else {
    					errMsg += "미등록파일경로/";
    					errSw = true;   
                    }
*/ 

                    
                    rs2 = null;
                    pstmt2 = null;
                } else {
					errMsg += "형상관리미등록/";
					errSw = true;                	
                }

                rst.put("cd_syscd", cd_syscd);
                rst.put("cd_rsrccd", cd_rsrccd);
                rst.put("cd_itemid", cd_itemid);
                rst.put("cd_jobcd", cd_jobcd);
                
				rst.put("cd_dirpathFull", fullPath);
				
				rst.put("cd_media01", fileList.get(i).get("cd_media01"));
				rst.put("cd_media02", fileList.get(i).get("cd_media02"));
				rst.put("cd_media03", fileList.get(i).get("cd_media03"));
				rst.put("cd_media04", fileList.get(i).get("cd_media04"));
				rst.put("cd_media05", fileList.get(i).get("cd_media05"));
				
				rst.put("cd_rsrcname", cd_rsrcname);
				rst.put("cd_dirpath", cd_dirpath);
				rst.put("cd_vstgbn", cd_vstgbn.toLowerCase());
				rst.put("cd_media", cd_media);
				rst.put("cd_editor", dataObj.get("cm_editor"));
				
				rst.put("progmsg","프로그램경로 : " + cd_dirpath + "\n" + "파일명 : " + cd_rsrcname);
				rst.put("etcmsg", fileList.get(i).get("etcmsg"));
				
				if(errSw) {
					rst.put("errsw", "1");
					rst.put("errmsg", errMsg);
					rst.put("errmsg2", errMsg.replaceAll("/", "\n"));
				} else {
					rst.put("errsw", "0");
					rst.put("errmsg", "정상");
					rst.put("errmsg2", "정상");
				}
				rsval.add(rst);
				rst = null;
				
	            rs.close();
	            pstmt.close();
			}
			conn.commit();
            conn.close();
			
			return rsval.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.getFileList_excel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.getFileList_excel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0300.getFileList_excel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.getFileList_excel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.getFileList_excel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
	public Object[] getArrayCollection(String filePath,ArrayList<String> headerDef) throws IOException, Exception {
		Object[]		  rtObj		  = null;

		ArrayList<HashMap<String, String>>  rtList = null;
		HashMap<String, String>			    rst	   = null;

		Workbook wb;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;

		int firstRow;
		int lastRow;
		int rowIdx;

		short firstCell;
		short lastCell;
		short cellIdx;

		try {
			filePath = filePath.trim();
			filePath = filePath.replace("//", "/");
			filePath = filePath.replace("/", "//");
			//ecamsLogger.error("filePath[2] : " + filePath);

			//워크북 오브젝트의 취득
			//wb = getXlsWorkBook(filePath);//xls 과 xlsx의 구분위한 함수
			//xls와 xlsx 통합 사용
			wb = WorkbookFactory.create(new FileInputStream(filePath));

			// 총 워크시트수의 취득
			//int sheetcount = wb.getNumberOfSheets();
			if (wb == null){
				throw new Exception("엑셀 sheet 읽기 실패[excelUtil]");
			}
			sheet = wb.getSheetAt(0);

			// 워크시트에 있는 첫행과 마지막행의 인덱스를 취득
			firstRow = sheet.getFirstRowNum()+1;
			lastRow = sheet.getLastRowNum();


			rtList = new ArrayList<HashMap<String, String>>();
			
			// 행에서 첫셀과 마지막 셀의 인덱스를 취득
			firstCell = sheet.getRow(0).getFirstCellNum();
			lastCell = sheet.getRow(0).getLastCellNum();
			
			// 행 별로 데이터를 취득
			for (rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
				//행을 표시하는 오브젝트의 취득
				row = sheet.getRow(rowIdx);

				// 행에 데이터가 없는 경우
				if (row == null) break;


				// 행에서 첫셀과 마지막 셀의 인덱스를 취득
//				firstCell = row.getFirstCellNum();
//				lastCell = row.getLastCellNum();

				if ((lastCell-firstCell) < headerDef.size()){
					throw new Exception("엑셀파일의 열의 갯수가 지정한 해더의 갯수보다 적습니다.");
				}


				//셀 별로 데이터를 취득

				rst   = new HashMap<String, String>();
				for (cellIdx = firstCell ; cellIdx <= lastCell; cellIdx++) {
					String data = null;

					if (headerDef.size()-1 < cellIdx){
						break;
					}
					if (headerDef.get(cellIdx) == null){
						break;
					}

					// 셀을 표시하는 오브젝트를 취득
					cell = row.getCell(cellIdx);

					// 빈 셀인 경우
					if (cell == null) break;

					//cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// 셀에 있는 데이터의 종류를 취득
					int type = cell.getCellType();

					// 데이터 종류별로 데이터를 취득
					switch (type) {
						case Cell.CELL_TYPE_BOOLEAN:
							boolean bdata = cell.getBooleanCellValue();
							data = String.valueOf(bdata);
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double ddata = cell.getNumericCellValue();
							data = String.valueOf(((int)ddata));
							break;
						case Cell.CELL_TYPE_STRING:
							data = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_BLANK:
						case Cell.CELL_TYPE_ERROR:
						case Cell.CELL_TYPE_FORMULA:
						default:
							continue;
					}
					rst.put(headerDef.get(cellIdx), data);
				}
				rtList.add(rst);
			}

			//excelFis.close();

			rtObj =  rtList.toArray();
			//System.out.println("excelUtil[1]:"+rtList.toString());

			rtList = null;

			return rtObj;

		} catch (IOException exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception END ##");
			throw exception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception END ##");
			throw exception;
		} finally{
			if (rtObj != null)	rtObj = null;
		}
	}	
	
///////////////////////////////////////////////SR 관련 쿼리//////////////////////////////////////////////////////////////////////////////////////////////	
	public Object[] getUserId(String UserName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		Object[] returnObjectArray = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT  cm_userid, cm_username		\n");
			strQuery.append("FROM    CMM0040					\n");
			strQuery.append("WHERE   cm_active = '1'			\n");
			strQuery.append("AND     cm_username = ?			\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			
			pstmt.setString(1, UserName);
			
            rs = pstmt.executeQuery();
            
            rtList.clear();
            
            while( rs.next() ) {
				rst = new HashMap<String,String>();
				rst.put("cm_userid",rs.getString("cm_userid"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("viewname",rs.getString("cm_username")+"["+rs.getString("cm_userid")+"]");
				rtList.add(rst);
				rst = null;            	
            }

			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			returnObjectArray = rtList.toArray();
			rtList = null;
			
			return returnObjectArray;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getUserId() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0300.getUserId() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getUserId() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0300.getUserId() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.getUserId() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getUserId() method statement	

	public String getUserName(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_username from cmm0040     \n");
			strQuery.append(" where cm_userid= ?  				\n");  //Sv_UserID
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, UserID);
            rs = pstmt.executeQuery();
                        
			if (rs.next()){
				retMsg = rs.getString("cm_username");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getUserName() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0300.getUserName() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.getUserName() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0300.getUserName() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.getUserName() connection release exception ##");
					ex3.printStackTrace();
				}
			}			
		}
	}//end of getUserName() method statement

	public Object[] isTeamLeader(String UserID, String cm_deptcd, String cm_rgtcd) throws SQLException, Exception {
		Connection        					conn        		= null;
		PreparedStatement					pstmt       		= null;
		ResultSet        					rs          		= null;
		StringBuffer      					strQuery    		= new StringBuffer();
		HashMap<String, String>			  	rst		  			= null;
		ArrayList<HashMap<String, String>>	rtList	 			= new ArrayList<HashMap<String, String>>();
		Object[] 							returnObjectArray	= null;
		
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT DISTINCT   a.cm_userid, a.cm_username, a.cm_position,        						\n");
			strQuery.append("                  a.cm_status, a.cm_duty, a.cm_deptseq, d.cm_codename      				\n");
			strQuery.append("FROM              CMM0040 a, CMM0043 b, CMM0020 d    										\n");
			strQuery.append("WHERE             a.cm_active = '1'                          								\n");
			strQuery.append("AND               a.cm_manid = 'Y'                           								\n");
			strQuery.append("AND               a.cm_userid <> ?   														\n");
			strQuery.append("AND               (a.cm_project in (select cm_deptcd       								\n");
			strQuery.append("                                    from (select * from cmm0100 where cm_useyn = 'Y')		\n");
			strQuery.append("                                    start with cm_deptcd = ?   							\n");
			strQuery.append("                                    connect by prior cm_updeptcd = cm_deptcd) 				\n");
			strQuery.append("                   or a.cm_project2 in (select cm_deptcd          							\n");
			strQuery.append("                                        from (select * from cmm0100 where cm_useyn = 'Y')	\n");
			strQuery.append("                                        start with cm_deptcd = ?  							\n");
			strQuery.append("                                        connect by prior cm_updeptcd = cm_deptcd)) 		\n");
			strQuery.append("AND                a.cm_userid=b.cm_userid                    								\n");
			strQuery.append("AND               INSTR(?,RTRIM(b.cm_rgtcd)) > 0                						\n");
			strQuery.append("AND               d.cm_macode = 'POSITION'		               								\n");
			strQuery.append("AND               d.cm_micode = a.cm_position		           								\n");
			strQuery.append("ORDER BY          a.cm_userid																\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			
			pstmt.setString(1, UserID);
			pstmt.setString(2, cm_deptcd);
			pstmt.setString(3, cm_deptcd);
			pstmt.setString(4, cm_rgtcd);
			
            rs = pstmt.executeQuery();
            
            rtList.clear();
            
                        
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_userid",rs.getString("cm_userid"));
				rst.put("cm_username",rs.getString("cm_username"));
				rtList.add(rst);
				rst = null;       				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			returnObjectArray = rtList.toArray();
			rtList = null;
			
			return returnObjectArray;			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.isTeamLeader() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0300.isTeamLeader() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.isTeamLeader() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0300.isTeamLeader() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null ) returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.isTeamLeader() connection release exception ##");
					ex3.printStackTrace();
				}
			}			
		}
	}//end of getUserName() method statement	
	
	public Object[] getSRList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT    a.cc_csrid, a.cc_deptname, a.cc_requser, a.cc_recvname, a.cc_reqtitle, 										\n");
			strQuery.append("          TO_CHAR(cc_confdate,'YYYY-MM-DD') as cc_acptdate,															\n");
			//strQuery.append("          a.cc_confdate, TO_CHAR(cc_confdate,'YYYY-MM-DD') as cc_acptdate,					\n");
			strQuery.append("          b. cc_srid, b.cc_syscd, b.cc_title, b.cc_comment, b.cc_reqstday, b.cc_reqedday,								\n");
			strQuery.append("          b.cc_workday, b.cc_gbncd, 																					\n");
			strQuery.append("		   (SELECT cm_username FROM CMM0040 WHERE cm_userid = b.cc_devuser) as cc_devuser, cc_devuser as cc_devuserid,  \n");
			strQuery.append(" 		   b.cc_recvuser, b.cc_procmsg, b.cc_cost, b.cc_emgyn, 															\n");
			strQuery.append("          (SELECT cm_codename FROM CMM0020 WHERE cm_macode='CMC0010' AND cm_micode =  b.cc_status) cc_statusname, 		\n");
			strQuery.append("          b.cc_status, to_char(b.cc_recvdate,'yyyy-mm-dd') as cc_recvdate, b.cc_confday, 								\n");
			strQuery.append("          b.cc_reqid, b.cc_dropmsg,																					\n");
			strQuery.append("          (SELECT cm_project FROM CMM0040 WHERE cm_userid = b.cc_devuser) as cc_project,								\n");
			strQuery.append("          DECODE( TO_CHAR(cc_confdate,'YYYY-MM-DD'),'',																\n");
			strQuery.append("          		   SUBSTR(b.cc_reqstday,0,4) ||'-'|| SUBSTR(b.cc_reqstday,5,2) ||'-'|| SUBSTR(b.cc_reqstday,7,2) , 		\n");
			strQuery.append("          		   TO_CHAR(cc_confdate,'YYYY-MM-DD') ) as cc_confdate													\n");
			//strQuery.append("          b.cc_reqid, b.cc_dropmsg, to_date(b.cc_reqstday, 'yyyy/mm/dd hh24:mi') as cc_confdate2						\n");
			strQuery.append("FROM      CMC0090 a     FULL OUTER JOIN     CMC0010 b 																	\n");
			strQuery.append("          ON      a.cc_csrid = b.cc_reqid																				\n");
			strQuery.append("WHERE     NVL(a.cc_status, '256') = '256'																				\n");
			
			if( (etcData.get("searchText") != null) && !etcData.get("searchText").equals("") ){
				if( !etcData.get("searchCon").equals("1") ) {
					
					strQuery.append("AND   INSTR(                                                               ");
					if( etcData.get("searchCon").equals("0") ) { //의뢰자
						strQuery.append("a.cc_requser															  ");
					} else if( etcData.get("searchCon").equals("2") ) { //수신부서
						strQuery.append("a.cc_recvname														  ");
					} else if( etcData.get("searchCon").equals("3") ) { //의뢰자 소속부서
						strQuery.append("a.cc_deptname														  ");
					} else if( etcData.get("searchCon").equals("4") ) { //SRID
						strQuery.append("b.cc_srid														  ");
					} else {}
					strQuery.append("            , ? ) > 0                                                         		 \n");	
				} else { //개발자
					strQuery.append("AND b.cc_devuser in (select cm_userid from cmm0040 where INSTR(cm_username, ?) > 0) 				\n");
				}
			}	
//			ecamsLogger.error("noDateFlag = " + etcData.get("noDateFlag"));
			if( etcData.get("noDateFlag").equals("false") ) {
				strQuery.append("AND         	 ( ( TO_CHAR(a.cc_confdate,'YYYYMMDD') >= ? AND                      	 \n");
				strQuery.append("              	     TO_CHAR(a.cc_confdate,'YYYYMMDD') <= ? ) OR                      	 \n");
				strQuery.append("              	    ( b.cc_reqstday >= ? AND                     						 \n");
				strQuery.append("              	      b.cc_reqstday <= ? ) )                        					 \n");
			}
			
			if( !etcData.get("srStatus").equals("0000") && !etcData.get("srStatus").equals("9999")) {
				strQuery.append("AND		  b.cc_status = ?  														\n");
			} else if( etcData.get("srStatus").equals("9999") ) {
				strQuery.append("AND		  b.cc_status IS NULL 													\n");
			}
			strQuery.append("ORDER BY		NVL(a.cc_confdate, TO_DATE(b.cc_reqstday, 'YYYY/MM/DD HH24:mi')) DESC						\n");
//			strQuery.append("AND			 ( b.cc_status = ? OR  																	\n");
//			strQuery.append("				   b.cc_status IS NULL )                       											\n");

            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
            
            if( (etcData.get("searchText") != null) && !etcData.get("searchText").equals("") ){
            	pstmt.setString(pstmtcount++, etcData.get("searchText"));
            }
            if( etcData.get("noDateFlag").equals("false") ) {
				pstmt.setString(pstmtcount++, etcData.get("acptStDate").replace("-", ""));
				pstmt.setString(pstmtcount++, etcData.get("acptEdDate").replace("-", ""));
				pstmt.setString(pstmtcount++, etcData.get("acptStDate").replace("-", ""));
				pstmt.setString(pstmtcount++, etcData.get("acptEdDate").replace("-", ""));			
            }
			if( !etcData.get("srStatus").equals("0000") && !etcData.get("srStatus").equals("9999")) {
				pstmt.setString(pstmtcount++, etcData.get("srStatus"));
			}
//			pstmt.setString(pstmtcount++, etcData.get("srStatus"));
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				
				rst.put("cc_csrid", rs.getString("cc_csrid")); //의뢰서 id
				rst.put("cc_deptname",rs.getString("cc_deptname")); //소속
				rst.put("cc_requser",rs.getString("cc_requser")); //의뢰자
				rst.put("cc_recvname",rs.getString("cc_recvname")); //수신부서
				
				if( (rs.getString("cc_reqtitle") != null) && !rs.getString("cc_reqtitle").equals("") ) {
					rst.put("cc_reqtitle", rs.getString("cc_reqtitle")); //SR 제목
				} else {
					rst.put("cc_reqtitle", rs.getString("cc_title")); //SR 제목
				}
				
				//rst.put("cc_reqtitle", rs.getString("cc_reqtitle")); //제목
				
				rst.put("cc_confdate", rs.getString("cc_confdate"));
				/*
				if( (rs.getString("cc_acptdate") != null) && !rs.getString("cc_acptdate").equals("") ) {
					rst.put("cc_confdate", rs.getString("cc_acptdate")); //신청일
				} else {
					String tmpConfdate = rs.getString("cc_reqstday").substring(0, 4) + "-" + rs.getString("cc_reqstday").substring(4, 6) + "-" + rs.getString("cc_reqstday").substring(6, 8);
					rst.put("cc_confdate", tmpConfdate); //신청일
					tmpConfdate = null;
				}
				*/
				/*
				if( (rs.getString("cc_confdate") != null) && !rs.getString("cc_confdate").equals("") ) {
					rst.put("cc_confdate", rs.getString("cc_confdate")); //신청일
				} else {
					rst.put("cc_confdate", rs.getString("cc_confdate2")); //신청일
				}
				*/
				
				
				//rst.put("cc_confdate", rs.getString("cc_confdate")); //신청일
				
				rst.put("cc_devuserid", rs.getString("cc_devuserid")); //개발자 id
				rst.put("cc_devuser", rs.getString("cc_devuser")); //개발자
				
				rst.put("cc_recvuser", rs.getString("cc_recvuser")); //접수자
				
				rst.put("cc_srid", rs.getString("cc_srid")); //SR ID
				rst.put("cc_status", rs.getString("cc_status")); //SR 상태
				rst.put("cc_statusname", rs.getString("cc_statusname")); //SR 상태명
				rst.put("cc_acptdate", rs.getString("cc_acptdate")); //상세내역에 표시할 신청일자
				
				rst.put("cc_syscd", rs.getString("cc_syscd")); //업무코드
				rst.put("cc_comment", rs.getString("cc_comment")); //SR 내용
				
				rst.put("cc_gbncd", rs.getString("cc_gbncd")); //SR 유형
				
				
				String str_cc_reqstday = rs.getString("cc_reqstday");
				if( (str_cc_reqstday != null ) && !str_cc_reqstday.equals("") ) {
					str_cc_reqstday = str_cc_reqstday.substring(0, 4) + "-" + str_cc_reqstday.substring(4, 6) + "-" + str_cc_reqstday.substring(6, 8);
				}
				rst.put("cc_reqstday", str_cc_reqstday); //신청일자
				
				String str_cc_reqedday = rs.getString("cc_reqedday");
				if( (str_cc_reqedday != null ) && !str_cc_reqedday.equals("") ) {
					str_cc_reqedday = str_cc_reqedday.substring(0, 4) + "-" + str_cc_reqedday.substring(4, 6) + "-" + str_cc_reqedday.substring(6, 8);
				}
				rst.put("cc_reqedday", str_cc_reqedday); //완료요청일
				
				String str_cc_workday = rs.getString("cc_workday");
				if( (str_cc_workday != null ) && !str_cc_workday.equals("") ) {
					str_cc_workday = str_cc_workday.substring(0, 4) + "-" + str_cc_workday.substring(4, 6) + "-" + str_cc_workday.substring(6, 8);
				}
				rst.put("cc_workday", str_cc_workday); //시행예정일
				
				String str_cc_confday = rs.getString("cc_confday");
				if( (str_cc_confday != null ) && !str_cc_confday.equals("") ) {
					str_cc_confday = str_cc_confday.substring(0, 4) + "-" + str_cc_confday.substring(4, 6) + "-" + str_cc_confday.substring(6, 8);
				}
				rst.put("cc_confday", str_cc_confday); //확인접수일
				
				rst.put("cc_recvdate", rs.getString("cc_recvdate")); //접수일
				rst.put("cc_procmsg", rs.getString("cc_procmsg")); //처리내용
				rst.put("cc_cost", rs.getString("cc_cost")); //투입 공수
				rst.put("cc_emgyn", rs.getString("cc_emgyn")); //임시 선처리 여부 (DB상 긴급여부)
				
				rst.put("cc_reqid", rs.getString("cc_reqid")); //전산처리의뢰서 ID (link 사용할지도 몰라 임시로)
				rst.put("cc_dropmsg", rs.getString("cc_dropmsg")); //기각 사유
				rst.put("cc_project", rs.getString("cc_project")); //기각 사유
				
				if( (rs.getString("cc_status") != null) && !rs.getString("cc_status").equals("") ) {
					if( rs.getString("cc_status").equals("103") ) { //기각
						rst.put("colorsw", "3");
					} else if ( rs.getString("cc_status").equals("600") ) { //처리완료
						rst.put("colorsw", "x");
					} else {
						rst.put("colorsw", "0");
					}
				}
					
				str_cc_reqstday = null;
				str_cc_reqedday = null;
				str_cc_workday = null;
				str_cc_confday = null;
				
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
	
	public String setSRList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String			str_cc_srid   = null;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT count(*) as cnt																\n");
			strQuery.append("FROM CMC0010 																		\n");
			strQuery.append("WHERE TO_CHAR(cc_creatdt, 'YYYY/MM/DD') = to_char(sysdate, 'YYYY/MM/DD')			\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();
            rs.next();
            
            if( rs.getInt("cnt")==0 ) {
            	
            	strQuery.setLength(0);
            	strQuery.append("SELECT TO_CHAR(sysdate, 'YYYYMMDD') || '-' ||LPAD(COUNT(*)+1, 6, '0') as cc_srid	\n");
    			strQuery.append("FROM CMC0010 																		\n");
    			strQuery.append("WHERE TO_CHAR(cc_creatdt, 'YYYY/MM/DD') = to_char(sysdate, 'YYYY/MM/DD')			\n");
    			
    			pstmt2 = conn.prepareStatement(strQuery.toString());

                rs2 = pstmt2.executeQuery();
                
                str_cc_srid = "";
                if( rs2.next() ) {
                	str_cc_srid = rs2.getString("cc_srid");
                }
                
            } else {
            	strQuery.setLength(0);
            	strQuery.append("SELECT MAX(cc_srid) as cc_srid														\n");
    			strQuery.append("FROM CMC0010 																		\n");
    			strQuery.append("WHERE TO_CHAR(cc_creatdt, 'YYYY/MM/DD') = to_char(sysdate, 'YYYY/MM/DD')			\n");
    			
    			pstmt2 = conn.prepareStatement(strQuery.toString());

                rs2 = pstmt2.executeQuery();
                
                str_cc_srid = "";
                if( rs2.next() ) {
                	DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                	DecimalFormat df = new DecimalFormat("000000",dfs);
                	
                	str_cc_srid = rs2.getString("cc_srid");
                	str_cc_srid = str_cc_srid.substring(0, 9) + df.format((Integer.parseInt(str_cc_srid.substring(9, str_cc_srid.length())) + 1));

                	dfs = null;
                	df = null;
                }
            }

            rs2.close();
            rs.close();
            pstmt2.close();
            pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("INSERT INTO CMC0010                                                                                \n");
			strQuery.append("            (cc_srid, cc_syscd, cc_title, cc_comment, cc_creatdt,                                  \n");
			strQuery.append("             cc_reqstday, cc_reqedday, cc_workday, cc_gbncd, cc_devuser,                           \n");
			strQuery.append("             cc_procmsg, cc_cost, cc_emgyn, cc_status, cc_recvdate,   								\n");
			strQuery.append("             cc_confday, cc_reqid, cc_recvuser)                                                    \n");
//			strQuery.append("VALUES( (SELECT TO_CHAR(sysdate, 'YYYYMMDD') || '-' ||LPAD(COUNT(*)+1, 5, '0') AS cc_srid          \n");
//			strQuery.append("         FROM CMC0010                                                                              \n");
//			strQuery.append("         WHERE TO_CHAR(cc_creatdt, 'YYYY/MM/DD') = to_char(sysdate, 'YYYY/MM/DD')),                \n");
			strQuery.append("VALUES(?,         ?, ?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'YYYY/MM/DD hh24:mi'), ?, ?, ? )   \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
            
            pstmt.setString(pstmtcount++, str_cc_srid); //SR ID
			pstmt.setString(pstmtcount++, etcData.get("cc_syscd")); //업무코드
			pstmt.setString(pstmtcount++, etcData.get("cc_title")); //sr 명
			pstmt.setString(pstmtcount++, etcData.get("cc_comment")); //sr 내용
			pstmt.setString(pstmtcount++, etcData.get("cc_reqstday").replaceAll("-", "")); //신청일
			pstmt.setString(pstmtcount++, etcData.get("cc_reqedday").replaceAll("-", "")); //완료요청일
			pstmt.setString(pstmtcount++, etcData.get("cc_workday").replaceAll("-", "")); //시행예정일
			pstmt.setString(pstmtcount++, etcData.get("cc_gbncd")); //sr 유형
			pstmt.setString(pstmtcount++, etcData.get("cc_devuser")); //개발자명
			pstmt.setString(pstmtcount++, etcData.get("cc_procmsg")); //처리 내용
			pstmt.setString(pstmtcount++, etcData.get("cc_cost")); //투입공수
			pstmt.setString(pstmtcount++, etcData.get("cc_emgyn")); //긴급여부(선처리여부?)
			pstmt.setString(pstmtcount++, etcData.get("cc_status")); //처리진행상태
			pstmt.setString(pstmtcount++, etcData.get("cc_recvdate").replaceAll("-", "")); //접수일
			pstmt.setString(pstmtcount++, etcData.get("cc_confday").replaceAll("-", "")); //확인접수일
			pstmt.setString(pstmtcount++, etcData.get("cc_reqid")); //전산처리의뢰서 id
			pstmt.setString(pstmtcount++, etcData.get("cc_recvuser")); //접수자 = 개발자?
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
            int result = pstmt.executeUpdate();

			strQuery.setLength(0);
			strQuery.append("SELECT		cc_srid                         \n");
			strQuery.append("FROM		CMC0010                         \n");
			strQuery.append("WHERE		cc_creatdt = sysdate			\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
            
			String cc_srid = "";
			if( rs.next() ) {
				cc_srid = rs.getString("cc_srid");
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			
			conn = null;
			pstmt = null;
			rs = null;
            
            if( result != 1) {
            	return null;
            }
			
			return cc_srid;

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

	public String updateSRList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE		CMC0010 										\n");
			strQuery.append("SET		cc_syscd = ?,									\n");
			strQuery.append("    		cc_title = ?,									\n");
			strQuery.append("    		cc_comment = ?,									\n");
			strQuery.append("   		cc_reqstday = ?,								\n");
			strQuery.append("    		cc_reqedday = ?,								\n");
			strQuery.append("    		cc_workday = ?,									\n");
			strQuery.append("    		cc_gbncd = ?,									\n");
			strQuery.append("    		cc_devuser = ?,									\n");
			strQuery.append("    		cc_procmsg = ?,									\n");			
			strQuery.append("    		cc_cost = ?,									\n");	
			strQuery.append("    		cc_emgyn = ?,									\n");	
			strQuery.append("    		cc_status = ?,									\n");	
			strQuery.append("    		cc_recvdate = to_date(?,'YYYY/MM/DD hh24:mi'),	\n");	
			strQuery.append("    		cc_confday = ?,									\n");	
			strQuery.append("    		cc_reqid = ?,									\n");	
			strQuery.append("    		cc_recvuser = ?									\n");	
			strQuery.append("WHERE 		cc_srid = ?										\n");	
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
            
			pstmt.setString(pstmtcount++, etcData.get("cc_syscd")); //업무코드
			pstmt.setString(pstmtcount++, etcData.get("cc_title")); //sr 명
			pstmt.setString(pstmtcount++, etcData.get("cc_comment")); //sr 내용
			pstmt.setString(pstmtcount++, etcData.get("cc_reqstday").replaceAll("-", "")); //신청일
			pstmt.setString(pstmtcount++, etcData.get("cc_reqedday").replaceAll("-", "")); //완료요청일
			pstmt.setString(pstmtcount++, etcData.get("cc_workday").replaceAll("-", "")); //시행예정일
			pstmt.setString(pstmtcount++, etcData.get("cc_gbncd")); //sr 유형
			pstmt.setString(pstmtcount++, etcData.get("cc_devuser")); //개발자명
			pstmt.setString(pstmtcount++, etcData.get("cc_procmsg")); //처리 내용
			pstmt.setString(pstmtcount++, etcData.get("cc_cost")); //투입공수
			pstmt.setString(pstmtcount++, etcData.get("cc_emgyn")); //긴급여부(선처리여부?)
			pstmt.setString(pstmtcount++, etcData.get("cc_status")); //처리진행상태
			pstmt.setString(pstmtcount++, etcData.get("cc_recvdate").replaceAll("-", "")); //접수일
			pstmt.setString(pstmtcount++, etcData.get("cc_confday").replaceAll("-", "")); //확인접수일
			pstmt.setString(pstmtcount++, etcData.get("cc_reqid")); //전산처리의뢰서 id
			pstmt.setString(pstmtcount++, etcData.get("cc_recvuser")); //접수자 = 개발자?
			pstmt.setString(pstmtcount++, etcData.get("cc_srid")); //SR ID
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
            int result = pstmt.executeUpdate();
            
			pstmt.close();
			conn.close();
			
			conn = null;
			pstmt = null;
            
            if( result != 1) {
            	return null;
            }
			
			return "OK";

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

	public String rejectSRInfo(String srid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE		CMC0010 										\n");
			strQuery.append("SET		cc_status = '103'								\n");
			strQuery.append("WHERE 		cc_srid = ?										\n");	
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
            
			pstmt.setString(pstmtcount++, srid); //SR ID
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
            int result = pstmt.executeUpdate();
            
			pstmt.close();
			conn.close();
			
			conn = null;
			pstmt = null;
            
            if( result != 1) {
            	return null;
            }
			
			return "OK";

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
	
   public String group_Approval_Load() throws SQLException, Exception {
		Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			CallableStatement cs = null;
			cs = conn.prepareCall("{call GROUP_APPROVAL_LOAD()}");
            cs.execute();

            cs.close();
            conn.close();
            cs = null;
            conn = null;

            return "OK";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0300.group_Approval_Load() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0300.group_Approval_Load() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0300.group_Approval_Load() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0300.group_Approval_Load() Exception END ##");
			throw exception;
		}finally{
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.group_Approval_Load() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of group_Approval_Load() method statement
	   
 /*
	public Object[] getSRLinkList() throws SQLException, Exception { //SR조회화면에서 조회시
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		//int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT  cc_confdate, cc_requser, cc_csrid, cc_reqtitle		\n");
			strQuery.append("FROM    CMC0090											\n");
			strQuery.append("WHERE   cc_status = '256'									\n");

            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
//			pstmt.setString(pstmtcount++, etcData.get("srStatus"));
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();            
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				
				rst.put("cc_confdate",		rs.getString("cc_confdate"));
				rst.put("cc_requser",		rs.getString("cc_requser"));
				rst.put("cc_csrid",			rs.getString("cc_csrid"));
				rst.put("cc_reqtitle",		rs.getString("cc_reqtitle"));
				
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
  */
   
	public Object[] getSRList2(HashMap<String,String> etcData) throws SQLException, Exception { //SR조회화면에서 조회시
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT    b.cc_syscd, c.cm_sysmsg AS cc_sysmsg,  b.cc_srid, b.cc_title, b.cc_procmsg,										\n");
			strQuery.append("          b.cc_status AS cc_statuscd, d.cm_codename cc_status,																\n");
			strQuery.append("          (select cm_userid from cmm0040 where cm_username = a.cc_requser) AS cc_requser, a.cc_requser as cc_requsername,	\n");
			strQuery.append("          b.cc_devuser, (select cm_username from cmm0040 where cm_userid = b.cc_devuser) AS cc_devusername, b.cc_emgyn,	\n");
			strQuery.append("          to_char(a.cc_confdate,'YYYY-MM-DD') cc_confdate, b.cc_reqstday, to_char(b.cc_recvdate,'YYYY-MM-DD') cc_recvdate, \n");
			strQuery.append("          b.cc_workday,	to_char(b.cc_realdate,'YYYY-MM-DD') cc_realdate													\n");
//			strQuery.append("          a.cc_confdate, to_date(b.cc_reqstday, 'YYYY/MM/DD HH24:mi') AS cc_reqstday, b.cc_recvdate,						\n");
//			strQuery.append("          to_date(b.cc_workday, 'YYYY/MM/DD HH24:mi') AS cc_workday, b.cc_realdate											\n");			
			strQuery.append("FROM      CMC0090 a RIGHT OUTER JOIN CMC0010 b																				\n");
			strQuery.append("          ON a.cc_csrid = b.cc_reqid, CMM0030 c, CMM0020 d																	\n");
			strQuery.append("WHERE     c.cm_closedt is null																								\n");
			strQuery.append("AND       c.cm_syscd = b.cc_syscd																							\n");
			strQuery.append("AND       d.cm_macode = 'CMC0010'																							\n");
			strQuery.append("AND       d.cm_micode <> '****'																							\n");
			strQuery.append("AND       d.cm_micode = b.cc_status																						\n");
			
			if( !etcData.get("cc_syscd").equals("00000") ) {
				strQuery.append("AND       b.cc_syscd = ?																									\n");
			}
			if( !etcData.get("cc_status").equals("0000") && !etcData.get("cc_status").equals("8888") ) {
				strQuery.append("AND       b.cc_status = ?																								\n");
			} else if ( etcData.get("cc_status").equals("8888") ) {
				strQuery.append("AND       b.cc_status NOT IN ('103','600')																				\n");
			}
			
			if( !etcData.get("cc_emgyn").equals("ALL") ) {
				strQuery.append("AND       b.cc_emgyn = ?																								\n");
			}
			
			if( etcData.get("searchCon").equals("0") ){ //의뢰부서
				strQuery.append("AND       a.cc_deptname like ?																						\n");
			} else if( etcData.get("searchCon").equals("1") ){ //의뢰자
				strQuery.append("AND       a.cc_requser = ?																							\n");
			} else if( etcData.get("searchCon").equals("2") ){ //수신부서
				strQuery.append("AND       a.cc_recvname like ?																						\n");	
			} else if( etcData.get("searchCon").equals("3") ){ //수신자
				strQuery.append("AND       a.cc_recvuser like ?																						\n");	
			} else if( etcData.get("searchCon").equals("4") ){ //SR-ID
				strQuery.append("AND       b.cc_srid like ?																							\n");	
			} else if( etcData.get("searchCon").equals("5") ){ //sr명
				strQuery.append("AND       b.cc_title like ?																						\n");
			} else if( etcData.get("searchCon").equals("6") ){ //sr내용
				strQuery.append("AND       b.cc_comment like ?																						\n");
			} else if( etcData.get("searchCon").equals("7") ){ //개발자
				strQuery.append("AND      b.cc_devuser in (select cm_userid from cmm0040 where cm_username = ? ) 									\n");
			} else if( etcData.get("searchCon").equals("8") ){ //처리내용
				strQuery.append("AND       b.cc_procmsg like ?																						\n");
			} else{}
			
			if ( !etcData.get("cc_status").equals("8888") ) {
				switch( Integer.parseInt(etcData.get("dateSearchCon")) ) { //조회기준일
					case 0: //신청일기준
						strQuery.append("AND		NVL( TO_CHAR(a.cc_confdate, 'YYYYMMDD'), b.cc_reqstday) >= ? 						\n");
						strQuery.append("AND		NVL( TO_CHAR(a.cc_confdate, 'YYYYMMDD'), b.cc_reqstday) <= ? 						\n");	
						break;
					case 1: //접수일기준
						strQuery.append("AND		TO_CHAR(b.cc_recvdate, 'YYYYMMDD') >= ? 											\n");
						strQuery.append("AND		TO_CHAR(b.cc_recvdate, 'YYYYMMDD') <= ? 											\n");		
						break;
					case 2: //시행예정일기준
						strQuery.append("AND		b.cc_workday >= ? 																	\n");
						strQuery.append("AND		b.cc_workday <= ? 																	\n");	
						break;
					case 3: //운영계이관일기준
						strQuery.append("AND		TO_CHAR(b.cc_realdate, 'YYYYMMDD') >= ? 											\n");
						strQuery.append("AND		TO_CHAR(b.cc_realdate, 'YYYYMMDD') <= ? 											\n");	
						break;	
					default: break;					
				}
			}
			
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			if( !etcData.get("cc_syscd").equals("00000") ) {
				pstmt.setString(pstmtcount++, etcData.get("cc_syscd"));
			}
            if( !etcData.get("cc_status").equals("0000") && !etcData.get("cc_status").equals("8888") ) {
            	pstmt.setString(pstmtcount++, etcData.get("cc_status"));
            }
            if( !etcData.get("cc_emgyn").equals("ALL") ) {
            	pstmt.setString(pstmtcount++, etcData.get("cc_emgyn"));
            }
            if( !etcData.get("searchCon").equals("0000") ) {
                if( etcData.get("searchCon").equals("1") || etcData.get("searchCon").equals("7") )  {	
                	pstmt.setString(pstmtcount++, etcData.get("searchText"));
                }else {
                	pstmt.setString(pstmtcount++, "%"+etcData.get("searchText")+"%");
                }           	
            }
            
            if ( !etcData.get("cc_status").equals("8888") ) {
	            pstmt.setString(pstmtcount++, etcData.get("stDate").replaceAll("-", ""));
	            pstmt.setString(pstmtcount++, etcData.get("edDate").replaceAll("-", ""));
			}
            
//			pstmt.setString(pstmtcount++, etcData.get("srStatus"));
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();            
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				
				rst.put("cc_syscd",			rs.getString("cc_syscd"));
				rst.put("cc_sysmsg",		rs.getString("cc_sysmsg"));
				rst.put("cc_srid",			rs.getString("cc_srid"));
				rst.put("cc_title",			rs.getString("cc_title"));
				rst.put("cc_procmsg",		rs.getString("cc_procmsg"));
				rst.put("cc_statuscd",		rs.getString("cc_statuscd"));
				rst.put("cc_status",		rs.getString("cc_status"));
				rst.put("cc_requser",		rs.getString("cc_requser"));
				rst.put("cc_requsername",	rs.getString("cc_requsername"));
				rst.put("cc_devuser",		rs.getString("cc_devuser"));
				rst.put("cc_devusername",	rs.getString("cc_devusername"));
				rst.put("cc_emgyn",			rs.getString("cc_emgyn"));
				if( (rs.getString("cc_confdate") != null) && (!rs.getString("cc_confdate").equals("")) ) {
					rst.put("cc_reqstday",	rs.getString("cc_confdate"));
				}else {
					String tmpReqstday = rs.getString("cc_reqstday").substring(0, 4) + "-" + rs.getString("cc_reqstday").substring(4, 6) + "-" + rs.getString("cc_reqstday").substring(6, 8);
					rst.put("cc_reqstday",	tmpReqstday);
					tmpReqstday = null;
				}
				rst.put("cc_recvdate",		rs.getString("cc_recvdate"));
				String tmpWorkday = rs.getString("cc_workday").substring(0, 4) + "-" + rs.getString("cc_workday").substring(4, 6) + "-" + rs.getString("cc_workday").substring(6, 8);
				rst.put("cc_workday",		tmpWorkday);
				
				rst.put("cc_realdate",		rs.getString("cc_realdate"));
				
				rtList.add(rst);
				rst = null;
				
				tmpWorkday = null;
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
	
	public Object[] getSRList_detail(String srid) throws SQLException, Exception { 
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT  a.cc_srid, b.cr_syscd, c.cm_sysmsg, b.cr_acptno, b.cr_acptdate, b.cr_qrycd,	\n");
			strQuery.append("        d.cm_codename AS cr_qrygbn, a.cc_comment, a.cc_recvuser, f.cm_username,		\n");
			strQuery.append("        b.cr_prcdate as cc_realdate, b.cr_status as cc_status, 										\n");
			strQuery.append("        DECODE(b.cr_status, '3', '반려', DECODE(b.cr_status, '9', '처리완료', '진행중')) as cc_statusname	\n");
			//strQuery.append("        a.cc_status, e.cm_codename AS cc_statusname, a.cc_realdate, b.cr_status		\n");
			//			strQuery.append("FROM    CMC0010 a, CMR1000 b, CMM0030 c, CMM0020 d, CMM0020 e, CMM0040 f				\n");
			strQuery.append("FROM    CMC0010 a, CMR1000 b, CMM0030 c, CMM0020 d, CMM0040 f							\n");
			strQuery.append("WHERE   a.cc_srid = b.cr_itsmid														\n");
			strQuery.append("AND     c.cm_syscd = b.cr_syscd														\n");
			strQuery.append("AND     d.cm_macode = 'REQUEST'														\n");
			strQuery.append("AND     d.cm_micode <> '****'															\n");
			strQuery.append("AND     d.cm_micode = b.cr_qrycd														\n");
			//strQuery.append("AND     e.cm_macode = 'CMC0010'														\n");
			//strQuery.append("AND     e.cm_micode <> '****'															\n");
			//strQuery.append("AND     e.cm_micode = a.cc_status														\n");
			strQuery.append("AND     f.cm_userid = a.cc_recvuser													\n");
			strQuery.append("AND     a.cc_srid = ?																	\n");
			strQuery.append("ORDER BY	b.cr_acptno DESC															\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			pstmt.setString(pstmtcount++, srid);
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();            
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				
				rst.put("cc_srid",				rs.getString("cc_srid"));
				rst.put("cc_syscd",				rs.getString("cr_syscd"));
				rst.put("cc_sysmsg",			rs.getString("cm_sysmsg"));
				rst.put("cc_acptno",			rs.getString("cr_acptno").substring(0, 4)+"-"+rs.getString("cr_acptno").substring(4, 6)+"-"+rs.getString("cr_acptno").substring(6, 12));
				rst.put("cc_acptno2",			rs.getString("cr_acptno"));
				rst.put("cc_acptdate",			rs.getString("cr_acptdate"));
				rst.put("cc_qrycd",				rs.getString("cr_qrycd"));
				rst.put("cc_qrygbn",			rs.getString("cr_qrygbn"));
				rst.put("cc_comment",			rs.getString("cc_comment"));
				rst.put("cc_recvuser",			rs.getString("cc_recvuser"));
				rst.put("cc_recvusername",		rs.getString("cm_username"));
				rst.put("cc_statuscd",			rs.getString("cc_status"));
				rst.put("cc_status",			rs.getString("cc_statusname"));
				rst.put("cc_realdate",			rs.getString("cc_realdate"));
				
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
	
	public Object[] getSRList_resource(String acptno, String srid, String isAllFlag) throws SQLException, Exception { 
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT  DISTINCT a.cr_itemid, a.cr_dsncd, a.cr_rsrcname,					\n");
			strQuery.append("CHGPATH(a.cr_syscd, a.cr_rsrccd, a.cr_jobcd, d.cm_dirpath) as cm_dirpath,	\n");
			strQuery.append("c.cr_teststa, e.cm_codename as cr_teststaname, cr_testdate,				\n"); //a.cr_prcdate,
			strQuery.append("c.cr_realsta, f.cm_codename as cr_realstaname, cr_realdate					\n");	
			if( ((isAllFlag != null) && !isAllFlag.equals("") ) && isAllFlag.equals("N") ) {
				strQuery.append(",a.cr_acptno															\n");
			}
			strQuery.append("FROM    CMR1010 a, CMR1000 b, CMR0020 c, CMM0070 d, CMM0020 e, CMM0020 f	\n");	
			strQuery.append("WHERE   b.cr_itsmid = ?													\n");
			if( ((isAllFlag != null) && !isAllFlag.equals("") ) && isAllFlag.equals("N") ) {
				strQuery.append("AND     a.cr_acptno = ?													\n");
			}
			strQuery.append("AND     a.cr_acptno = b.cr_acptno											\n");
			strQuery.append("AND     d.cm_syscd = a.cr_syscd											\n");
			strQuery.append("AND     d.cm_dsncd = a.cr_dsncd											\n");
			strQuery.append("AND     c.cr_itemid = a.cr_itemid											\n");
			strQuery.append("AND     e.cm_macode = 'CMR0020'											\n");
			strQuery.append("AND     e.cm_micode <> '****'												\n");
			strQuery.append("AND     e.cm_micode = c.cr_teststa											\n");
			strQuery.append("AND     f.cm_macode = 'CMR0020'											\n");
			strQuery.append("AND     f.cm_micode <> '****'												\n");
			strQuery.append("AND     f.cm_micode = c.cr_realsta											\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			pstmt.setString(pstmtcount++, srid);
			if( ((isAllFlag != null) && !isAllFlag.equals("") ) && isAllFlag.equals("N") ) {
				pstmt.setString(pstmtcount++, acptno); //.replaceAll("-", "")
			}
			
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();            
            rtList.clear();
			while (rs.next()){
				String actpno = "";
				
				if( ((isAllFlag != null) && !isAllFlag.equals("") ) && isAllFlag.equals("Y") ) {
					PreparedStatement pstmt2       = null;
					ResultSet         rs2          = null;			
					
					strQuery.setLength(0);
					strQuery.append("SELECT    a.cr_acptno							\n");
					strQuery.append("FROM      CMR1010 a, CMR1000 b, CMR0020 c		\n");
					strQuery.append("WHERE     a.cr_itemid = ?						\n");
					strQuery.append("AND       b.cr_itsmid = ?						\n");
					strQuery.append("AND       a.cr_acptno = b.cr_acptno			\n");
					strQuery.append("AND       c.cr_itemid = a.cr_itemid			\n");
					strQuery.append("ORDER BY  a.cr_acptno desc						\n");
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_itemid"));
					pstmt2.setString(2, srid);
					
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					rs2.next();
					
					actpno = rs2.getString("cr_acptno").substring(0, 4)+"-"+rs2.getString("cr_acptno").substring(4, 6)+"-"+rs2.getString("cr_acptno").substring(6, 12);
					
					rs2.close();
					pstmt2.close();
					
					rs2 = null;
					pstmt2 = null;				
				} else {
					actpno = rs.getString("cr_acptno").substring(0, 4)+"-"+rs.getString("cr_acptno").substring(4, 6)+"-"+rs.getString("cr_acptno").substring(6, 12);
				}
				
				
				rst = new HashMap<String,String>();
				
				rst.put("cc_acptno",			actpno);
				rst.put("cc_dsncd",				rs.getString("cr_dsncd"));
				rst.put("cc_dirpath",			rs.getString("cm_dirpath"));
				rst.put("cc_rsrcname",			rs.getString("cr_rsrcname"));
				rst.put("cr_teststa",			rs.getString("cr_teststa"));
				rst.put("cc_teststaname",		rs.getString("cr_teststaname"));
				rst.put("cc_testdate",			rs.getString("cr_testdate"));
				rst.put("cc_realsta",			rs.getString("cr_realsta"));
				rst.put("cc_realstaname",		rs.getString("cr_realstaname"));	
				rst.put("cc_realdate",			rs.getString("cr_realdate"));	
				
				//rst.put("cc_prcdate",			rs.getString("cr_prcdate"));
				
				rtList.add(rst);
				rst = null;
				acptno = null;
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
	
	
	public Object[] getSRList_popup(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2   = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>	 rst  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT    a.cc_csrid, a.cc_deptname, a.cc_requser, a.cc_recvname, a.cc_reqtitle, a.cc_confdate 		\n");
			strQuery.append("FROM      CMC0090 a 																					\n");
			strQuery.append("WHERE     NVL(a.cc_status, '256') = '256'																\n");
			
			if( (etcData.get("searchText") != null) && !etcData.get("searchText").equals("") ){
					strQuery.append("AND   INSTR(                                                               ");
					if( etcData.get("searchCon").equals("0") ) { //의뢰자
						strQuery.append("a.cc_requser															  ");
					} else if( etcData.get("searchCon").equals("2") ) { //수신부서
						strQuery.append("a.cc_recvname														  ");
					} else if( etcData.get("searchCon").equals("3") ) { //의뢰자 소속부서
						strQuery.append("a.cc_deptname														  ");
					} else {}
					strQuery.append("            , ? ) > 0                                                         		 \n");	
			}	
			
//			ecamsLogger.error("noDateFlag = " + etcData.get("noDateFlag"));
			if( etcData.get("noDateFlag").equals("false") ) {
				strQuery.append("AND         	  ( TO_CHAR(a.cc_confdate,'YYYYMMDD') >= ? AND                      	 \n");
				strQuery.append("              	     TO_CHAR(a.cc_confdate,'YYYYMMDD') <= ? )	                      	 \n");
			}
			strQuery.append("ORDER BY a.cc_confdate desc																\n");

            //pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
            
            if( (etcData.get("searchText") != null) && !etcData.get("searchText").equals("") ){
            	pstmt.setString(pstmtcount++, etcData.get("searchText"));
            }
            if( etcData.get("noDateFlag").equals("false") ) {
				pstmt.setString(pstmtcount++, etcData.get("acptStDate").replace("-", ""));
				pstmt.setString(pstmtcount++, etcData.get("acptEdDate").replace("-", ""));
            }
			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				
				rst.put("cc_csrid", rs.getString("cc_csrid")); //의뢰서 id
				rst.put("cc_deptname",rs.getString("cc_deptname")); //소속
				rst.put("cc_requser",rs.getString("cc_requser")); //의뢰자
				rst.put("cc_recvname",rs.getString("cc_recvname")); //수신부서
				rst.put("cc_reqtitle", rs.getString("cc_reqtitle")); //의뢰서 제목
				rst.put("cc_confdate", rs.getString("cc_confdate")); //신청일
				rst.put("colorsw", "x");
				
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
	
}//end of Cmd0300 class statement

