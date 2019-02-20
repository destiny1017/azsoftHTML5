
/*****************************************************************************************
	1. program ID	: CodeInfo.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: CodeInfo
*****************************************************************************************/

package app.common;

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


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class CodeInfo{

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

	public Object[] getCodeInfo(String MACODE,String SelMsg,String closeYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if(SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg   = "";
				}
			}

			String[] macode = MACODE.split(",");
			strQuery.append("select decode(cm_micode,'****',1,2) flag, cm_macode,cm_micode,cm_codename from cmm0020 where \n");
			strQuery.append("cm_macode in ( \n");
			if (macode.length == 1)
				strQuery.append(" ? ");
			else{
				for (int i=0;i<macode.length;i++){
					if (i == macode.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append(" ) \n");
			if (SelMsg == "" || SelMsg == null) {
	           strQuery.append("and cm_micode<>'****' \n");
			}
	        if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
	        	strQuery.append("and cm_closedt is null \n");
	        }
	        strQuery.append("order by cm_macode,flag, cm_micode \n");

            //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt =  new LoggableStatement(conn, strQuery.toString());

			for (int i=0;i<macode.length;i++){
				pstmt.setString(i+1, macode[i]);
			}

            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getString("cm_micode").equals("****")) {
					if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
						rst = new HashMap<String, String>();
						rst.put("ID", "0");
						rst.put("cm_codename", strSelMsg);
						rst.put("cm_micode", "00");
						rst.put("cm_macode", rs.getString("cm_macode"));
						
						rst.put("id", "00");
						rst.put("value", "00");
						rst.put("pid", "0");
						rst.put("text", strSelMsg);
						rst.put("order", Integer.toString(rs.getRow()));
						rst.put("link", "");
						
						rtList.add(rst);
						rst = null;
					}
				} else {

					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("cm_codename", rs.getString("cm_codename"));
					rst.put("cm_micode", rs.getString("cm_micode"));
					rst.put("cm_macode", rs.getString("cm_macode"));
					
					
					rst.put("id", rs.getString("cm_micode"));
					rst.put("value", rs.getString("cm_micode"));
					rst.put("pid", "0");
					rst.put("text", rs.getString("cm_codename"));
					rst.put("order", Integer.toString(rs.getRow()));
					rst.put("link", "");
					
					rtList.add(rst);
					rst = null;
				}
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
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getCodeInfo() method statement
	
	
	public HashMap<String, ArrayList<HashMap<String, String>>> getCodeInfoWithArray(ArrayList<HashMap<String, String>> codeInfoArr) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";
		HashMap<String, ArrayList<HashMap<String, String>>> returnCodeInfoArrayMap = new HashMap<String, ArrayList<HashMap<String, String>>>();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			
			for(HashMap<String, String> codeInfoMap: codeInfoArr) {
				//if(codeInfoMap.get("SelMsg").equals(anObject))
				if(codeInfoMap.containsKey("SelMsg")) {
					if (codeInfoMap.get("SelMsg").toUpperCase().equals("ALL")){
						strSelMsg = "전체";
					}
					else if(codeInfoMap.get("SelMsg").toUpperCase().equals("SEL")){
						strSelMsg = "선택하세요";
					}
					else{
						strSelMsg   = "";
					}
				}
				strQuery.setLength(0);
				strQuery.append("select decode(cm_micode,'****',1,2) flag, cm_macode,cm_micode,cm_codename from cmm0020 where 	\n");
				strQuery.append("cm_macode = ? 																					\n");
				
				if (!codeInfoMap.containsKey("SelMsg")) {
		           strQuery.append("and cm_micode<>'****' \n");
				}
				
				if(codeInfoMap.containsKey("closeYn")) {
					if (codeInfoMap.get("closeYn").toUpperCase().equals("N") || codeInfoMap.get("closeYn").toUpperCase().equals("0")){
			        	strQuery.append("and cm_closedt is null \n");
			        }
				}else {
					strQuery.append("and cm_closedt is null \n");
				}
		        
		        strQuery.append("order by cm_macode,flag, cm_micode \n");

	            //pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt =  new LoggableStatement(conn, strQuery.toString());

				pstmt.setString(1, codeInfoMap.get("MACODE"));
				
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

	            rs = pstmt.executeQuery();
	            rtList = new ArrayList<>();
				while (rs.next()){
					if (rs.getString("cm_micode").equals("****")) {
						if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
							rst = new HashMap<String, String>();
							rst.put("ID", "0");
							rst.put("cm_codename", strSelMsg);
							rst.put("cm_micode", "00");
							rst.put("cm_macode", rs.getString("cm_macode"));
							
							rst.put("id", "00");
							rst.put("value", "00");
							rst.put("pid", "0");
							rst.put("text", strSelMsg);
							rst.put("order", Integer.toString(rs.getRow()));
							rst.put("link", "");
							
							rtList.add(rst);
							rst = null;
						}
					} else {

						rst = new HashMap<String, String>();
						rst.put("ID", Integer.toString(rs.getRow()));
						rst.put("cm_codename", rs.getString("cm_codename"));
						rst.put("cm_micode", rs.getString("cm_micode"));
						rst.put("cm_macode", rs.getString("cm_macode"));
						
						
						rst.put("id", rs.getString("cm_micode"));
						rst.put("value", rs.getString("cm_micode"));
						rst.put("pid", "0");
						rst.put("text", rs.getString("cm_codename"));
						rst.put("order", Integer.toString(rs.getRow()));
						rst.put("link", "");
						
						rtList.add(rst);
						rst = null;
					}
				}//end of while-loop statement
				
				returnCodeInfoArrayMap.put(rtList.get(0).get("cm_macode"), rtList);
			}
			
			
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtList = null;

			return returnCodeInfoArrayMap;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getCodeInfo() method statement
	
	public Object[] getCodeInfo_Sort(String MACODE,String SelMsg,String closeYn,int sortCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if(SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg   = "";
				}
			}

			String[] macode = MACODE.split(",");
			strQuery.append("select decode(cm_micode,'****',1,2) flag, cm_macode,cm_micode,cm_codename,cm_seqno from cmm0020 where \n");
			strQuery.append("cm_macode in ( \n");
			if (macode.length == 1)
				strQuery.append(" ? ");
			else{
				for (int i=0;i<macode.length;i++){
					if (i == macode.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append(" ) \n");
			if (SelMsg == "" || SelMsg == null) {
	           strQuery.append("and cm_micode<>'****' \n");
			}
	        if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
	        	strQuery.append("and cm_closedt is null \n");
	        }
	        if (sortCd == 0) {
	        	strQuery.append("order by cm_macode, flag, cm_micode   \n");
	        } else if (sortCd == 9) {
	        	strQuery.append("order by cm_macode, flag, cm_seqno    \n");
	        } else {
	        	strQuery.append("order by cm_macode, flag, cm_codename \n");
	        }

            pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

			for (int i=0;i<macode.length;i++){
				pstmt.setString(i+1, macode[i]);
			}

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getString("cm_micode").equals("****")) {
					if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
						rst = new HashMap<String, String>();
						rst.put("ID", "0");
						rst.put("cm_codename", strSelMsg);
						rst.put("cm_micode", "00");
						rst.put("cm_macode", rs.getString("cm_macode"));
						rtList.add(rst);
						rst = null;
					}
				} else {
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("cm_codename", rs.getString("cm_codename"));
					rst.put("cm_micode", rs.getString("cm_micode"));
					rst.put("cm_macode", rs.getString("cm_macode"));
					rtList.add(rst);
					rst = null;
				}
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
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getCodeInfo() method statement
	public Object[] getJobCd(String SelMsg,String closeYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if(SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg   = "";
				}
			}

			strQuery.append("select cm_jobcd,cm_jobname from cmm0102 \n");
	        if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
	        	strQuery.append("where cm_useyn='Y'                  \n");
	        }
	        strQuery.append("order by cm_jobcd                       \n");

            pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
						rst = new HashMap<String, String>();
						rst.put("ID", "0");
						rst.put("cm_jobname", strSelMsg);
						rst.put("cm_jobcd", "00");
						rtList.add(rst);
						rst = null;
					}
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs=null;
			pstmt=null;
			conn=null;

			rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getJobCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getJobCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getJobCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getJobCd() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getJobCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getJobCd() method statement


	public Object[] getCodeInfo2(String MACODE,String SelMsg,String closeYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}

				else{
					strSelMsg   = "";
				}
			}

			String[] macode = MACODE.split(",");
			strQuery.append("select decode(cm_micode,'****',1,2) flag, cm_macode,cm_micode,cm_codename from cmm0020 where \n");
			strQuery.append("cm_macode in ( \n");
			if (macode.length == 1)
				strQuery.append(" ? ");
			else{
				for (int i=0;i<macode.length;i++){
					if (i == macode.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append(" ) \n");
			if (SelMsg == "" || SelMsg == null) {
	           strQuery.append("and cm_micode<>'****' \n");
			}
	        if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
	        	strQuery.append("and cm_closedt is null \n");
	        }
	        strQuery.append("order by cm_macode,flag, cm_micode \n");

            pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

			for (int i=0;i<macode.length;i++){
				pstmt.setString(i+1, macode[i]);
			}

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getString("cm_micode").equals("****")) {
					if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
						rst = new HashMap<String, String>();
						rst.put("ID", "0");
						rst.put("cm_codename", strSelMsg);
						rst.put("cm_micode", "00");
						rst.put("cm_macode", rs.getString("cm_macode"));
						rtList.add(rst);
						rst = null;
					}
				} else {
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("cm_codename", rs.getString("cm_codename"));
					rst.put("cm_micode", rs.getString("cm_micode"));
					rst.put("cm_macode", rs.getString("cm_macode"));
					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs=null;
			pstmt=null;
			conn=null;

			rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo2() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getCodeInfo2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCodeInfo() method statement


	public Object[] getUniCode(String macode) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.append("select cm_macode, cm_micode, cm_codename\n");//unistr(asciistr(cm_codename)) cm_codename \n");
			strQuery.append("  from CMM0020_LANG \n");
			strQuery.append(" where cm_macode = ? \n");
			strQuery.append("   and cm_micode <> '****' \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, macode);
            rs = pstmt.executeQuery();

            //String charSet[] = {"utf-8","euc-kr","8859_1","ISO-8859-1","MS949","KSC5601","CP949","latin1","UTF-16BE","UTF-16LE","ASCII","gb2312"};
            String charSet[] = {"utf-8","euc-kr","8859_1","ISO-8859-1","MS949","KSC5601","CP949","latin1","UTF-16BE","UTF-16LE","ASCII"};
            String charSet2[] = {"UTF-16LE"};

			while (rs.next()){


//				for(int i = 0; i < charSet.length; i++) {
//					for(int j = 0; j < charSet.length; j++) {
//						if (!charSet[i].equals(charSet[j])) {
//							rst = new HashMap<String, String>();
//							rst.put("cm_micode", rs.getString("cm_micode"));
//							rst.put("cm_macode", rs.getString("cm_macode"));
//							rst.put("cm_codename", new String(convertHexToString(rs.getString("cm_codename")).getBytes(charSet[i]),charSet[j])+" "+charSet[i]+","+charSet[j]);
//							rtList.add(rst);
//							rst = null;
//						}
// 	               	}
// 	           	}


				for(int i = 0; i < charSet.length; i++) {
					for(int j = 0; j < charSet2.length; j++) {
						if (!charSet[i].equals(charSet2[j])) {
							rst = new HashMap<String, String>();
							rst.put("cm_micode", rs.getString("cm_micode"));
							rst.put("cm_macode", rs.getString("cm_macode"));
							rst.put("cm_codename", new String(convertHexToString(rs.getString("cm_codename")).getBytes(charSet[i]),charSet2[j])+" "+charSet[i]+","+charSet2[j]);
							rtList.add(rst);
							rst = null;
						}
 	               	}
 	           	}

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs=null;
			pstmt=null;
			conn=null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo2() Exception END ##");
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
					ecamsLogger.error("## CodeInfo.getCodeInfo2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCodeInfo() method statement

	public String setUniCode(String macode, String micode, String codename) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.append("delete CMM0020_LANG\n");
			strQuery.append(" where cm_macode = ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, macode);
            pstmt.executeUpdate();
            pstmt.close();

            String cm_codename = "";
            strQuery.setLength(0);
            strQuery.append("SELECT ASCIISTR(?) cm_codename FROM DUAL \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, codename);
            rs = pstmt.executeQuery();
            if (rs.next()){
            	cm_codename = rs.getString("cm_codename");
            }
            rs.close();
            pstmt.close();

            //String charSet[] = {"utf-8","euc-kr","8859_1","ISO-8859-1","MS949","KSC5601","CP949","latin1","UTF-16BE","UTF-16LE","ASCII","gb2312"};
            String charSet[] = {"UTF-16LE"};
//            for(int i = 0; i < charSet.length; i++) {
//				for(int j = 0; j < charSet.length; j++) {
//					if (!charSet[i].equals(charSet[j])) {
//
//						strQuery.setLength(0);
//				       	strQuery.append("insert into CMM0020_LANG (cm_codename,cm_macode,cm_micode, \n");
//				        strQuery.append("cm_creatdt,cm_lastupdt) \n");
//				       	strQuery.append(" values (unistr(?), ?, ?, SYSDATE, SYSDATE) \n");
//						pstmt = conn.prepareStatement(strQuery.toString());
//						pstmt.setString(1, convertStringToHex(new String(cm_codename.getBytes(charSet[i]),charSet[j])));
//				    	pstmt.setString(2, macode);
//				    	pstmt.setString(3, charSet[i]+" "+charSet[j]);
//				    	pstmt.executeUpdate();
//				    	pstmt.close();
//
//					}
//	           	}
//            }

            for(int i = 0; i < charSet.length; i++) {

					strQuery.setLength(0);
			       	strQuery.append("insert into CMM0020_LANG (cm_codename,cm_macode,cm_micode, \n");
			        strQuery.append("cm_creatdt,cm_lastupdt) \n");
			       	strQuery.append(" values (unistr(?), ?, ?, SYSDATE, SYSDATE) \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, convertStringToHex(new String(cm_codename.getBytes(charSet[i]))));
			    	pstmt.setString(2, macode);
			    	pstmt.setString(3, charSet[i]);
			    	pstmt.executeUpdate();
			    	pstmt.close();

            }

			conn.close();

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CodeInfo.setUniCode() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.setUniCode() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.setUniCode() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.setUniCode() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## CodeInfo.setUniCode() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setUniCode() method statement

	private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    private static char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static String escape(String str) {
        // Modeled after the code in java.util.Properties.save()
        StringBuffer buf = new StringBuffer();
        int len = str.length();
        char ch;
        for (int i = 0; i < len; i++) {
            ch = str.charAt(i);
            switch (ch) {
            case '\\':
                buf.append("\\\\");
                break;
            case '\t':
                buf.append("\\t");
                break;
            case '\n':
                buf.append("\\n");
                break;
            case '\r':
                buf.append("\\r");
                break;
            default:
                if (ch >= ' ' && ch <= 127) {
                    buf.append(ch);
                } else {
                    buf.append('\\');
                    buf.append(toHex((ch >> 12) & 0xF));
                    buf.append(toHex((ch >> 8) & 0xF));
                    buf.append(toHex((ch >> 4) & 0xF));
                    buf.append(toHex((ch >> 0) & 0xF));
                }
            }
        }
        return buf.toString();
    }

    public String convertStringToHex(String str){

  	  char[] chars = str.toCharArray();

  	  StringBuffer hex = new StringBuffer();
  	  for(int i = 0; i < chars.length; i++){
  	    hex.append(Integer.toHexString((int)chars[i]));
  	  }

  	  return hex.toString();
    }

    public String convertHexToString(String hex){

  	  StringBuilder sb = new StringBuilder();
  	  StringBuilder temp = new StringBuilder();

  	  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
  	  for( int i=0; i<hex.length()-1; i+=2 ){

  	      //grab the hex in pairs
  	      String output = hex.substring(i, (i + 2));
  	      //convert hex to decimal
  	      int decimal = Integer.parseInt(output, 16);
  	      //convert the decimal to character
  	      sb.append((char)decimal);

  	      temp.append(decimal);
  	  }
  	  //System.out.println("Decimal : " + temp.toString());

  	  return sb.toString();
    }

    // hex to byte[]
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    // byte[] to hex
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }


    public Object[] getCodeInfo_lang(String MACODE,String SelMsg,String closeYn) throws SQLException, Exception {
    	  Connection        conn        = null;
    	  PreparedStatement pstmt       = null;
    	  ResultSet         rs          = null;
    	  StringBuffer      strQuery    = new StringBuffer();
    	  ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
    	  HashMap<String, String>       rst    = null;
    	  String            strSelMsg   = "";

    	  ConnectionContext connectionContext = new ConnectionResource();

    	  try {
    	   conn = connectionContext.getConnection();
    	   if (SelMsg != "" && SelMsg != null) {
    	    if (SelMsg.toUpperCase().equals("ALL")){
    	     strSelMsg = "전체";
    	    }
    	    else if(SelMsg.toUpperCase().equals("SEL")){
    	     strSelMsg = "선택하세요";
    	    }
    	    else{
    	     strSelMsg   = "";
    	    }
    	   }

    	   String[] macode = MACODE.split(",");
    	   strQuery.append("select cm_macode,cm_micode,cm_codename cm_codename from cmm0020_lang where \n");
    	   strQuery.append("cm_macode in ( \n");
    	   if (macode.length == 1)
    	    strQuery.append(" ? ");
    	   else{
    	    for (int i=0;i<macode.length;i++){
    	     if (i == macode.length-1)
    	      strQuery.append(" ? ");
    	     else
    	      strQuery.append(" ? ,");
    	    }
    	   }
    	   strQuery.append(" ) \n");
    	   if (SelMsg == "" || SelMsg == null) {
    	            strQuery.append("and cm_micode<>'****' \n");
    	   }
    	         if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
    	          strQuery.append("and cm_closedt is null \n");
    	         }
    	         strQuery.append("order by cm_micode \n");

    	            pstmt = conn.prepareStatement(strQuery.toString());
    	         //pstmt =  new LoggableStatement(conn, strQuery.toString());

    	   for (int i=0;i<macode.length;i++){
    	    pstmt.setString(i+1, macode[i]);
    	   }

    	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

    	            rs = pstmt.executeQuery();

    	            rtList.clear();
    	            String encoding = "";
    	   while (rs.next()){
    	    if (rs.getString("cm_micode").equals("****")) {
    	     if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
    	      rst = new HashMap<String, String>();
    	      rst.put("ID", "0");
    	      rst.put("cm_codename", strSelMsg);
    	      rst.put("cm_micode", "00");
    	      rst.put("cm_macode", rs.getString("cm_macode"));
    	      rtList.add(rst);
    	      rst = null;
    	     }
    	    } else {

    	     String charSet[] = {"utf-8","euc-kr","8859_1","ISO-8859-1","MS949","KSC5601","CP949","latin1","UTF-16BE","UTF-16LE","ASCII"};
    	           for(int i = 0; i < charSet.length; i++) {
    	            for(int j = 0; j < charSet.length; j++) {
    	             if (!charSet[i].equals(charSet[j])) {
    	              //ecamsLogger.error(charSet[i] + " to " + charSet[j] + " = " + new String(str.getBytes(charSet[i]),charSet[j]));
    	              rst = new HashMap<String, String>();
    	        rst.put("ID", Integer.toString(rs.getRow()));
    	              rst.put("cm_micode", rs.getString("cm_micode"));
    	        rst.put("cm_macode", rs.getString("cm_macode"));
    	        rst.put("cm_codename", new String(rs.getString("cm_codename").getBytes(charSet[i]),charSet[j])+" "+charSet[i]+","+charSet[j]);
    	        rtList.add(rst);
    	                    rst = null;
    	             }
    	               }
    	           }
    	           rst = new HashMap<String, String>();
    	     rst.put("ID", Integer.toString(rs.getRow()));
    	     rst.put("cm_codename", new String(rs.getString("cm_codename").getBytes(encoding),"UTF-8"));
    	     rst.put("cm_micode", rs.getString("cm_micode"));
    	     rst.put("cm_macode", rs.getString("cm_macode"));
    	     rtList.add(rst);
    	     rst = null;
    	    }
    	   }//end of while-loop statement
    	   rs.close();
    	   pstmt.close();
    	   conn.close();

    	         rs = null;
    	            pstmt = null;
    	            conn = null;

    	   return  rtList.toArray();

    	  } catch (SQLException sqlexception) {
    	   sqlexception.printStackTrace();
    	   ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException START ##");
    	   ecamsLogger.error("## Error DESC : ", sqlexception);
    	   ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException END ##");
    	   throw sqlexception;
    	  } catch (Exception exception) {
    	   exception.printStackTrace();
    	   ecamsLogger.error("## CodeInfo.getCodeInfo() Exception START ##");
    	   ecamsLogger.error("## Error DESC : ", exception);
    	   ecamsLogger.error("## CodeInfo.getCodeInfo() Exception END ##");
    	   throw exception;
    	  }finally{
    	   if (strQuery != null)  strQuery = null;
    	   if (rtList != null) rtList = null;
    	   if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
    	   if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
    	   if (conn != null){
    	    try{
    	     ConnectionResource.release(conn);
    	    }catch(Exception ex3){
    	     ecamsLogger.error("## CodeInfo.getCodeInfo() connection release exception ##");
    	     ex3.printStackTrace();
    	    }
    	   }
    	  }


    	 }//end of getCodeInfo() method statement

}//end of CodeInfo class statement
