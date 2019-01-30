
/*****************************************************************************************
	1. program ID	: Cmd1300 Table
	2. create date	: 2008.05. 26
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: Cmd1300
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
//import app.common.LoggableStatement;
import app.common.CreateXml;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd1300{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 조직도 트리
	 * @param
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] get_Cbo_SignNMADD(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select distinct CM_SIGNNM from CMM0045 \n");
			strQuery.append("Where CM_USERID= ? 			\n");  //UserID
			strQuery.append("AND CM_SIGNUSER is not null 	\n");
			strQuery.append("Order by CM_SIGNNM 			\n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserID);

            rs = pstmt.executeQuery();
            rtList.clear();

            rst = new HashMap<String, String>();
            rst.put("CM_SIGNNM", "결재추가");
            rtList.add(rst);
            rst = null;

            while (rs.next()){
            	rst = new HashMap<String, String>();
    			rst.put("CM_SIGNNM", rs.getString("CM_SIGNNM"));
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
    		rtList = null;
    		return rtObj;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
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


	}//end of Cbo_SignNMADD() method statement

    public int get_SignNM_cnt(String UserId,String SignNM) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtInt       = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select Count(*) as CNT from cmm0045 \n");
			strQuery.append("where CM_SIGNNM= ? 				\n");
			strQuery.append("and CM_USERID= ?					\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SignNM);
			pstmt.setString(2, UserId);
            rs = pstmt.executeQuery();
            if (rs.next()){
            	rtInt = Integer.parseInt(rs.getString("CNT"));
            }
            rs.close();
            pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

            return rtInt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
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

	}//end of SignNM_cnt() method statement



    public Object[] get_Cbo_Select(String UserID,String SignNM) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select distinct a.cm_userid,a.cm_username,b.cm_deptname,c.cm_CODEname as pos,c.cm_seqno,e.CM_SIGNSTEP,e.CM_SIGNCD,d.cm_codename as SIGNCD\n");
			strQuery.append("from cmm0040 a,cmm0100 b,cmm0020 c,cmm0020 d,cmm0045 e				\n");
			strQuery.append("where a.cm_active='1'												\n");
			strQuery.append("and e.cm_userid=	?												\n");
			strQuery.append("and e.CM_SIGNNM=	?												\n");
			strQuery.append("and a.cm_userid=e.CM_SIGNUSER										\n");
			strQuery.append("and a.cm_project=b.cm_deptcd										\n");
			strQuery.append("and C.CM_MACODE='POSITION' AND C.CM_MICODE=a.cm_position			\n");
			strQuery.append("and d.cm_macode='SIGNCD' and d.cm_micode=e.CM_SIGNCD				\n");
			strQuery.append("order by e.CM_SIGNSTEP												\n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserID);
			pstmt.setString(2, SignNM);
            rs = pstmt.executeQuery();
            rtList.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("CM_USERID", rs.getString("cm_userid"));
				rst.put("CM_USERNAME", rs.getString("cm_username"));
				rst.put("TEAMNAME", rs.getString("cm_deptname"));
				rst.put("POSITION", rs.getString("pos"));
				rst.put("CM_SEQNO", rs.getString("cm_seqno"));
				rst.put("CM_SIGNSTEP", rs.getString("CM_SIGNSTEP"));
				rst.put("ITEM7", rs.getString("CM_SIGNCD"));
				rst.put("ITEM1", rs.getString("SIGNCD")); //codename
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
    		rtList = null;
    		return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
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

	}//end of Cbo_Select() method statement

    public int getTblUpdate(String UserId,String SysCd,String Lbl_Dir) throws SQLException, Exception{
    	Connection			conn				= null;
    	PreparedStatement	pstmt				= null;
    	StringBuffer		strQuery			= new StringBuffer();
    	ConnectionContext   connectionContext	= new ConnectionResource();
    	int					rtn_cnt				= 0;

    	try{
    		conn = connectionContext.getConnection();

    		strQuery.setLength(0);
    		strQuery.append("delete cmd0300 where cd_syscd=? \n");//SysCd
    		strQuery.append(" and cd_userid=?  \n");//UserId
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(1, SysCd);
    		pstmt.setString(2, UserId);
    		rtn_cnt = pstmt.executeUpdate();
    		pstmt.close();

    		strQuery.setLength(0);
    		strQuery.append("insert into cmd0300 (CD_USERID,CD_SYSCD,CD_DEVHOME) values (\n");
    		strQuery.append("?, \n");//UserId
			strQuery.append("?, \n");//SysCd
			strQuery.append("?) \n");//Lbl_Dir
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, SysCd);
			pstmt.setString(3, Lbl_Dir);
			rtn_cnt = pstmt.executeUpdate();
			pstmt.close();

			conn.close();
			pstmt = null;
			conn = null;

			return rtn_cnt;


    	} catch (SQLException sqlexception){
    		sqlexception.printStackTrace();
    		throw sqlexception;
 		} catch (Exception exception) {
 			exception.printStackTrace();
 			throw exception;
 		}finally{
			if (strQuery != null)	strQuery = null;
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

    public int getTblDelete(String UserId,ArrayList<HashMap<String, String>> rvList) throws SQLException, Exception{
    	Connection			conn				= null;
    	PreparedStatement	pstmt				= null;
    	StringBuffer		strQuery			= new StringBuffer();
    	ConnectionContext   connectionContext	= new ConnectionResource();
    	int					rtn_cnt				= 0;

    	try{
    		conn = connectionContext.getConnection();

    		strQuery.setLength(0);
    		strQuery.append("delete cmd0300 where \n");//SysCd
    		strQuery.append(" cd_userid=?  \n");//UserId
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(1, UserId);
    		rtn_cnt = pstmt.executeUpdate();
    		pstmt.close();

    		for (int i=0 ; i<rvList.size() ; i++){
	    		strQuery.setLength(0);
	    		strQuery.append("insert into cmd0300 (CD_USERID,CD_SYSCD,CD_DEVHOME) values (\n");
	    		strQuery.append("?, \n");//UserId
				strQuery.append("?, \n");//SysCd
				strQuery.append("?) \n");//Lbl_Dir
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, UserId);
				pstmt.setString(2, rvList.get(i).get("cd_syscd"));
				pstmt.setString(3, rvList.get(i).get("cd_devhome"));
				rtn_cnt = pstmt.executeUpdate();
				pstmt.close();
    		}

    		conn.commit();
    		conn.close();
    		pstmt = null;
    		conn = null;

    		return rtn_cnt;

    	} catch (SQLException sqlexception){
    		sqlexception.printStackTrace();
    		throw sqlexception;
 		} catch (Exception exception) {
 			exception.printStackTrace();
 			throw exception;
 		}finally{
			if (strQuery != null)	strQuery = null;
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

    public int get_myGrid2_insert(String UserId,ArrayList<HashMap<String, String>> rtList,int Index,String SignName) throws SQLException, Exception {
		Connection        conn        			  = null;
		PreparedStatement pstmt       			  = null;
		StringBuffer      strQuery   			  = new StringBuffer();
		int               rtn_cnt				  = 0;
		ConnectionContext connectionContext 	  = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

		    if (Index ==1){
				strQuery.setLength(0);
				strQuery.append(" delete cmm0045 where cm_userid= ? 		\n");
				strQuery.append(" and CM_SIGNNM= ?							\n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, UserId);
	            pstmt.setString(2, SignName);
	            rtn_cnt = pstmt.executeUpdate();
	            pstmt.close();
		    }

			for (int i=0 ; i<rtList.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmm0045 (CM_USERID,CM_SIGNUSER,CM_LASTDT,CM_SIGNNM,CM_SIGNSTEP,CM_SIGNCD) values (\n");
				strQuery.append("?, \n");  //UserId
				strQuery.append("?, \n");//rtList.get(i).get("CM_USERID")
				strQuery.append("SYSDATE, \n");
				strQuery.append("?, \n");//Txt_SignNM or Cbo_SignNM.Text
				strQuery.append("?, \n");//리스트 순서(NO)
				strQuery.append("?) \n");//rtList.get(i).get("ITEM7")
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, UserId);
				pstmt.setString(2, rtList.get(i).get("CM_USERID"));
				pstmt.setString(3, SignName);
				pstmt.setString(4, Integer.toString(i+1));
				pstmt.setString(5, rtList.get(i).get("ITEM7"));
				rtn_cnt = pstmt.executeUpdate();
				pstmt.close();
			}

			conn.commit();
			conn.close();
			pstmt = null;
			conn = null;

			return rtn_cnt;

		} catch (SQLException sqlexception) {

			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}

	}//end of Txt_SignNM_insert() method statement


    public Object[] get_Txt_SignNM_delete(String USERID,String SIGNNM) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
		int               rtn_cnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append(" delete cmm0045 where cm_userid= ? 		\n");
			strQuery.append(" and CM_SIGNNM= ?							\n");

			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, USERID);
            pstmt.setString(2, SIGNNM);


			//System.out.print(((LoggableStatement)pstmt).getQueryString());
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

            rtList.clear();
			rst = new HashMap<String, String>();
			rst.put("ID","Txt_SignNM_delete");
			rst.put("result",Integer.toString(rtn_cnt));
			rst.put("TASK","DELETE CMM0045");
			rtList.add(rst);
			rst = null;

			conn.close();
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList = null;
			return rtObj;

		} catch (SQLException sqlexception) {

			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}

	}//end of Txt_SignNM_delete() method statement

    public Object[] getSql_Qry(String UserId) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		  rtObj		    = null;
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select b.cd_syscd,a.cm_sysmsg,b.cd_devhome \n");
			strQuery.append(" from cmd0300 b,cmm0030 a \n");
			strQuery.append(" where b.cd_userid=? \n");//UserId
			strQuery.append("   and b.cd_syscd=a.cm_syscd \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
			rs = pstmt.executeQuery();
			rtList.clear();
	        while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("NO", Integer.toString(rs.getRow()));
				if (rs.getString("cm_sysmsg") != null){
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				}else{
					continue;
					//rst.put("cm_sysmsg", "시스템명이 존재하지않습니다.");
				}
				rst.put("cd_syscd", rs.getString("cd_syscd"));
				rst.put("cd_devhome", rs.getString("cd_devhome"));
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
			rtList = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}

	}//end of getSql_Qry() method statement

    public Object[] dirOpenChk(ArrayList<String> fileList,String DevHome)	throws SQLException, Exception {
		CreateXml         ecmmtb      = new CreateXml();
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;

		try {
			int               i = 0;
			String            strPath1 = "";
			int               upSeq       = 0;
			int               maxSeq      = 100;
			rsval.clear();
			upSeq = 1;
			maxSeq = 1;
			upSeq = maxSeq;
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","isBranch");

			rst = new HashMap<String,String>();
			rst.put("cm_dirpath",DevHome);
			rst.put("cm_fullpath",DevHome);
			rst.put("cm_upseq",Integer.toString(upSeq));
			rst.put("cm_seqno",Integer.toString(maxSeq));
			rsval.add(maxSeq - 1, rst);

			rst = null;
			upSeq = maxSeq;
			for (i=0;fileList.size()>i;i++) {
				strPath1 = fileList.get(i).toString();
				maxSeq = maxSeq + 1;

				rst = new HashMap<String,String>();
				rst.put("cm_dirpath",strPath1);
				rst.put("cm_fullpath",DevHome+"/"+strPath1);
				rst.put("cm_upseq",Integer.toString(upSeq));
				rst.put("cm_seqno",Integer.toString(maxSeq));
				rsval.add(maxSeq - 1, rst);
				rst = null;
			}
			if (rsval.size() > 0) {

			    String strBran = "";
				for (i = 0;rsval.size() > i;i++) {
					strBran = "true";
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath").replace("/","\\\\"),
							strBran,rsval.get(i).get("cm_upseq"));
				}
			}
			list.add(ecmmtb.getDocument());
			returnObjectArray = list.toArray();
			list = null;
			ecmmtb = null;

			return returnObjectArray;

		}  catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.dirOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.dirOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  	rsval = null;
		}
	}//end of dirOpenChk() method statement


    public Object[] getMyPage(String UserId) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cd_pjid,a.cd_devno,a.cd_chgtit,a.cd_CHGDET,      \n");
		    strQuery.append("       a.CD_REQDEPT,a.CD_CHGDEPT,a.cd_chgcd,              \n");
		    strQuery.append("       c.cm_sysmsg,b.cd_syscd,d.cm_codename qrycd,        \n");
		    strQuery.append("       e.cm_codename sayucd,b.cd_sayucd,b.cd_qrycd        \n");
	        strQuery.append("  from CMD0210 b,cmd0110 a,cmm0030 c,cmm0020 d,cmm0020 e  \n");
	        strQuery.append("  where B.CD_USERID=?            \n ");
	        strQuery.append("    and b.cd_prjno=a.cd_pjid     \n ");
	        strQuery.append("    and b.cd_devno=a.cd_devno    \n ");
	        strQuery.append("    and decode(b.cd_syscd,'00000','99999',b.cd_syscd)=c.cm_syscd \n ");
	        strQuery.append("    and d.cm_macode='REQUEST'    \n ");
	        strQuery.append("    and d.cm_micode=decode(b.cd_qrycd,'00','****',b.cd_qrycd) \n");
	        strQuery.append("    and e.cm_macode='REQCD'      \n ");
	        strQuery.append("    and e.cm_micode=b.cd_sayucd  \n ");
    		strQuery.append("  order by a.cd_pjid desc,a.cd_devno \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserId);
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rtList.clear();
	        while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cd_syscd", rs.getString("cd_syscd"));
				rst.put("cd_qrycd", rs.getString("cd_qrycd"));
				rst.put("cd_sayucd", rs.getString("cd_sayucd"));
				rst.put("cd_pjid", rs.getString("cd_pjid"));
				rst.put("cd_devno", rs.getString("cd_devno"));
				rst.put("prjno", rs.getString("cd_pjid") + "-" + rs.getString("cd_devno"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("qryname", rs.getString("qrycd"));
				rst.put("sayuname", rs.getString("sayucd"));
				if (rs.getString("cd_chgcd").equals("1")){
	        		rst.put("chg_tyMsg", "프로젝트");
	        	} else if(rs.getString("cd_chgcd").equals("2")){
	        		rst.put("chg_tyMsg", "정상변경");
	        	} else if(rs.getString("cd_chgcd").equals("3")){
	        		rst.put("chg_tyMsg", "단순변경");
	        	} else if(rs.getString("cd_chgcd").equals("4")){
	        		rst.put("chg_tyMsg", "비상변경");
	        	}
				if (rs.getString("cd_syscd").equals("00000")) rst.put("cm_sysmsg", "전체");
				if (rs.getString("cd_qrycd").equals("00")) rst.put("qryname","전체");

				rst.put("reqdept", rs.getString("CD_REQDEPT"));
				rst.put("CHGdept", rs.getString("CD_CHGDEPT"));
				rst.put("Title", rs.getString("cd_chgtit"));
				rst.put("chg_cnt", rs.getString("cd_CHGDET"));
				rst.put("selected", "0");
				rtList.add(rst);
				rst = null;
	        }
	        rs.close();
	        pstmt.close();
	        conn.close();
	        rs = null;
	        pstmt = null;
	        conn = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1300.getMyPage() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1300.getMyPage() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.getMyPage() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.getMyPage() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1300.getMyPage() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getMyPage() method statement
    public String setMyPage(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> prjList) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			int parmCnt = 0;
			boolean insSw = false;
			for (int i=0;prjList.size()>i;i++) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("delete cmd0210                     \n");
		        strQuery.append("  where CD_USERID=?                \n ");
		        strQuery.append("    and cd_syscd=? and cd_qrycd=?  \n ");
		        strQuery.append("    and cd_sayucd=?                \n ");
		        strQuery.append("    and cd_prjno=? and cd_devno=?  \n ");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("userid"));
				pstmt.setString(++parmCnt, etcData.get("syscd"));
				pstmt.setString(++parmCnt, etcData.get("qrycd"));
				pstmt.setString(++parmCnt, etcData.get("sayucd"));
				pstmt.setString(++parmCnt, prjList.get(i).get("prjno"));
				pstmt.setString(++parmCnt, prjList.get(i).get("devno"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();

				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("insert into cmd0210               \n");
				strQuery.append("  (CD_USERID,CD_SYSCD,CD_QRYCD,CD_SAYUCD,CD_PRJNO,CD_DEVNO,CD_LASTDT) \n");
				strQuery.append("values (?, ?, ?, ?, ?, ?, SYSDATE) \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("userid"));
				pstmt.setString(++parmCnt, etcData.get("syscd"));
				pstmt.setString(++parmCnt, etcData.get("qrycd"));
				pstmt.setString(++parmCnt, etcData.get("sayucd"));
				pstmt.setString(++parmCnt, prjList.get(i).get("prjno"));
				pstmt.setString(++parmCnt, prjList.get(i).get("devno"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();

				insSw = false;
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmd0110 \n");
				strQuery.append(" where cd_pjid=? and cd_devno=?  \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(++parmCnt, prjList.get(i).get("prjno"));
				pstmt.setString(++parmCnt, prjList.get(i).get("devno"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if (rs.getInt("cnt") == 0) {
						insSw = true;
					}
				}
				rs.close();
				pstmt.close();

				if (insSw == true) {
					parmCnt = 0;
					strQuery.setLength(0);
	            	strQuery.append("insert into CMD0110 (CD_PJID,CD_DEVNO,CD_REQDEPT,CD_CHGDEPT,CD_CHGUPDPT,CD_CREATOR, \n");
	            	strQuery.append("                      CD_CHGCD,CD_CHGSAYU,CD_CHGTIT,CD_CHGDET,CD_CHGSTA) \n");
	            	strQuery.append("(select pj_no,dev_chg_no,wso_dmd_orgn_name,chg_dmd_orgn_name,chg_dmd_up_orgn_name, \n");
	            	strQuery.append("        mpr_no,chg_ty,chg_rsn,chg_name,chg_cnt,chg_stt from vi_pms_chg_pfmc \n");
	            	strQuery.append("  where pj_no=? and  dev_chg_no=?) \n");

	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	//pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(++parmCnt, prjList.get(i).get("prjno"));
					pstmt.setString(++parmCnt, prjList.get(i).get("devno"));
	            	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            	pstmt.executeUpdate();
	            	pstmt.close();
				}
			}
	        conn.close();

	        rs = null;
	        pstmt = null;
	        conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1300.setMyPage() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1300.setMyPage() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.setMyPage() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.setMyPage() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1300.setMyPage() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setMyPage() method statement
    public String delMyPage(String UserId,ArrayList<HashMap<String,String>> delPrjList) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			int parmCnt = 0;
			int retInt = 0;
			for (int i=0 ; delPrjList.size()>i ; i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmd0210                     \n");
		        strQuery.append("  where CD_USERID=?                \n ");
		        strQuery.append("    and cd_syscd=? and cd_qrycd=?  \n ");
		        strQuery.append("    and cd_sayucd=?                \n ");
		        strQuery.append("    and cd_prjno=? and cd_devno=?  \n ");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				parmCnt = 0;
				pstmt.setString(++parmCnt, UserId);
				pstmt.setString(++parmCnt, delPrjList.get(i).get("syscd"));
				pstmt.setString(++parmCnt, delPrjList.get(i).get("qrycd"));
				pstmt.setString(++parmCnt, delPrjList.get(i).get("sayucd"));
				pstmt.setString(++parmCnt, delPrjList.get(i).get("prjno"));
				pstmt.setString(++parmCnt, delPrjList.get(i).get("devno"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				retInt = pstmt.executeUpdate();
				pstmt.close();
			}
	        conn.close();

	        pstmt = null;
	        conn = null;

	        if (retInt>0){
	        	return "OK";
	        }else{
	        	return "denied";
	        }

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1300.delMyPage() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1300.delMyPage() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.delMyPage() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.delMyPage() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1300.delMyPage() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delMyPage() method statement

    public Object[] getMsg_notice(String UserId) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		HashMap<String, String>			  	rst	 	= null;
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_telno2, cm_email, cm_noticd  \n");
	        strQuery.append("  from cmm0040                         \n");
	        strQuery.append(" where cm_userid = ?                   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserId);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rtList.clear();
			if (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cm_telno2", rs.getString("cm_telno2"));
				rst.put("cm_email", rs.getString("cm_email"));
				rst.put("cm_noticd", rs.getString("cm_noticd"));
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

	        conn.close();

	        pstmt = null;
	        conn = null;

	        return rtList.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1300.getMsg_notice() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1300.getMsg_notice() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.getMsg_notice() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.getMsg_notice() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1300.getMsg_notice() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getMsg_notice() method statement

    public String setMsg_notice(String UserId, String noticd, String phone, String mail) throws SQLException, Exception {
		Connection        conn        	= null;
		PreparedStatement pstmt       	= null;
		ResultSet         rs          	= null;
		StringBuffer      strQuery   	= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		//conn = connectionContext.getConnection();
		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmm0040         \n");
	        strQuery.append("   set cm_telno2 = ?,  \n");
	        strQuery.append("       cm_email  = ?,  \n");
	        strQuery.append("       cm_noticd = ?   \n");
	        strQuery.append(" where cm_userid = ?   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, phone);
			pstmt.setString(2, mail);
			pstmt.setString(3, noticd);
			pstmt.setString(4, UserId);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

	        conn.close();

	        pstmt = null;
	        conn = null;

	        return "OK";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1300.setMsg_notice() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1300.setMsg_notice() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1300.setMsg_notice() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1300.setMsg_notice() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1300.setMsg_notice() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setMsg_notice() method statement

}//end of Cmd1300 class statement
