/*****************************************************************************************
	1. program ID	: Cmm0100.java
	2. create date	: 2008.12. 01
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [관리자] -> 코드정보
*****************************************************************************************/

package app.eCmm;

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
import org.w3c.dom.Document;

//import app.common.LoggableStatement;
import app.common.LoggableStatement;
import app.common.UserInfo;
import app.common.CreateXml;


public class Cmm0100{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 코드 정보를 조회합니다.
	 * @param  CboListIndex, 조회값
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getCodeList(HashMap<String,String> dataObj) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		Object[] returnObject = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			UserInfo userinfo = new UserInfo();
			strQuery.setLength(0);
			strQuery.append("select cm_macode,cm_micode,cm_codename,cm_seqno, \n");
			strQuery.append(" to_char(cm_creatdt,'yyyy-mm-dd') cm_creatdt, \n");
			strQuery.append(" to_char(cm_lastupdt,'yyyy-mm-dd') cm_lastupdt, \n");
			strQuery.append(" to_char(cm_closedt,'yyyy-mm-dd') cm_closedt \n");
			strQuery.append(" from cmm0020 where \n");
	        if (userinfo.isAdmin_conn(dataObj.get("UserId"),conn)){
	        	if (dataObj.get("CboMicode").equals("00")){
	        		if (dataObj.get("Txt_Code0") != "" && dataObj.get("Txt_Code0") != null){
	        			strQuery.append("cm_macode like ? and \n"); //'%" & Replace(Txt_Code(0), "*", "%") & "%'
	        		}
                    strQuery.append("cm_micode='****' \n");
	        	}else if (dataObj.get("CboMicode").equals("01")){
	               	strQuery.append("cm_codename like ? and \n");//'%" & Replace(Txt_Code(1), "*", "%") & "%'
	               	strQuery.append("cm_micode='****' \n");
	        	}else if (dataObj.get("CboMicode").equals("02")){
                    strQuery.append("cm_macode like ? and \n");//'%" & Replace(Txt_Code(0), "*", "%") & "%'
                    strQuery.append("cm_micode<>'****' \n");
                    strQuery.append("and cm_micode like ? \n");//'" & Replace(Txt_Code(2), "*", "%") & "'
	        	}else if (dataObj.get("CboMicode").equals("03")){
                    strQuery.append("cm_macode like ? and \n");//'%" & Replace(Txt_Code(0), "*", "%") & "%'
                    strQuery.append("cm_micode<>'****' \n");
                    strQuery.append("and cm_codename like ? \n");//'" & Replace(Txt_Code(3), "*", "%") & "'
	        	}
	        }else{
	        	strQuery.append("cm_macode in ('JAWON','LANGUAGE') and cm_micode<>'****' and cm_closedt is null \n");
	        }
	        strQuery.append("order by cm_macode,cm_micode \n");

            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt =  new LoggableStatement(conn, strQuery.toString());

            int paramIndex = 0;
	        if (userinfo.isAdmin_conn(dataObj.get("UserId"),conn)){
	        	if (dataObj.get("CboMicode").equals("00")){
	        		if (dataObj.get("Txt_Code0") != "" && dataObj.get("Txt_Code0") != null){
	        			pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code0").toUpperCase().replace("*","%")+"%");
	        		}
	        	}else if (dataObj.get("CboMicode").equals("01")){
	        		pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code1").replace("*","%")+"%");
	        	}else if (dataObj.get("CboMicode").equals("02")){
	        		pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code0").toUpperCase().replace("*","%")+"%");
	        		pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code2").replace("*","%")+"%");
	        	}else if (dataObj.get("CboMicode").equals("03")){
	        		pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code0").toUpperCase().replace("*","%")+"%");
	        		pstmt.setString(++paramIndex, "%"+dataObj.get("Txt_Code3").replace("*","%")+"%");
	        	}
	        }

	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_macode", rs.getString("cm_macode"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_creatdt", rs.getString("cm_creatdt"));
				rst.put("cm_lastupdt", rs.getString("cm_lastupdt"));
				if (rs.getString("cm_closedt") != null && rs.getString("cm_closedt") != ""){
					rst.put("cm_closedt", rs.getString("cm_closedt"));
					rst.put("closeYN", "미사용");
				}else{
					rst.put("cm_closedt", "");
					rst.put("closeYN", "사용");
				}
				rst.put("cm_seqno", rs.getString("cm_seqno"));
				rsval.add(rst);
				rst = null;
            }

            userinfo = null;
		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;
            returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getCodeList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getCodeList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObject != null)  	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getCodeList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public String getCodeName(String txtCode0) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		String				resultStr	= "";

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_codename from cmm0020 \n");
			strQuery.append("  Where cm_macode=? \n");
			strQuery.append("    and cm_micode='****' \n");

            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, txtCode0.toUpperCase());
            rs = pstmt.executeQuery();
            if(rs.next()){
            	resultStr = rs.getString("cm_codename");
            }
		    rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            strQuery = null;
			return resultStr;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeName() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getCodeName() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeName() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getCodeName() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getCodeName() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public String setCodeValue(HashMap<String,String> dataObj) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
		    strQuery.append("select cm_macode from cmm0020 where \n");
		    strQuery.append("cm_macode=? and \n");
		    strQuery.append("cm_micode='****' \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, dataObj.get("Txt_Code0").toUpperCase());
            rs = pstmt.executeQuery();

            strQuery.setLength(0);
            if(rs.next()){
            	strQuery.append("update cmm0020 set cm_codename= ?, \n");
            	strQuery.append("cm_lastupdt=SYSDATE, cm_closedt='' \n");
            	strQuery.append("where cm_macode= ? and cm_micode='****' \n");
            	pstmt2 = conn.prepareStatement(strQuery.toString());
            	pstmt2.setString(1, dataObj.get("Txt_Code1").replace("'", "''"));
            	pstmt2.setString(2, rs.getString("cm_macode"));
            }else{
 		       	strQuery.append("insert into cmm0020 (cm_macode,cm_micode,cm_codename, \n");
 		        strQuery.append("cm_creatdt,cm_lastupdt) \n");
 		       	strQuery.append(" values (?, '****',?,SYSDATE, SYSDATE) \n");
            	pstmt2 = conn.prepareStatement(strQuery.toString());
            	pstmt2.setString(1, dataObj.get("Txt_Code0").toUpperCase());
            	pstmt2.setString(2, dataObj.get("Txt_Code1").replace("'", "''"));
            }
        	pstmt2.executeUpdate();
        	pstmt2.close();
        	rs.close();
        	pstmt.close();


		    if (dataObj.get("Txt_Code2") != "" && dataObj.get("Txt_Code2") != null){
		    	strQuery.setLength(0);
		        strQuery.append("select cm_micode from cmm0020 where \n");
		        strQuery.append("cm_macode=? and \n");//'" & Txt_Code(0) & "'
		        strQuery.append("cm_micode=? \n");//'" & Txt_Code(2) & "'
		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, dataObj.get("Txt_Code0").toUpperCase());
		        pstmt.setString(2, dataObj.get("Txt_Code2"));
	            rs = pstmt.executeQuery();

	            strQuery.setLength(0);
	            if (rs.next()){
	            	strQuery.append("update cmm0020 set cm_codename=?, \n");//'" & Txt_Code(3) & "'
			        strQuery.append("cm_lastupdt=SYSDATE, \n");
			        if (dataObj.get("closeYN").equals("미사용")){
			        	strQuery.append("cm_closedt=SYSDATE, \n");
			        }else{
			        	strQuery.append("cm_closedt='', \n");
			        }
			        strQuery.append("cm_seqno=? \n");//" & Val(Txt_Code(4)) & "
			        strQuery.append("where cm_macode=? and \n");//'" & Txt_Code(0) & "'
			        strQuery.append("cm_micode=? \n");//'" & Txt_Code(2) & "'
	            }else{
			        strQuery.append("insert into cmm0020 (cm_codename,cm_seqno, \n");
			        strQuery.append("cm_creatdt,cm_lastupdt,cm_closedt,cm_macode,cm_micode) \n");
			        strQuery.append("values (?,?,SYSDATE,SYSDATE, \n");
			        if (dataObj.get("closeYN").equals("미사용")){
			        	strQuery.append("SYSDATE, \n");
			        }else{
			        	strQuery.append("'', \n");
			        }
			        strQuery.append("?,?) \n");
	            }
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	            pstmt2.setString(1, dataObj.get("Txt_Code3").replace("'", "''"));
	            pstmt2.setString(2, dataObj.get("Txt_Code4"));
	            pstmt2.setString(3, dataObj.get("Txt_Code0").toUpperCase());
	            pstmt2.setString(4, dataObj.get("Txt_Code2"));
	        	pstmt2.executeUpdate();
	        	pstmt2.close();
	        	rs.close();
	        	pstmt.close();
		    }

		    conn.commit();
		    conn.setAutoCommit(true);
            conn.close();
            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return dataObj.get("Txt_Code3");

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setCodeValue() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.setCodeValue() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setCodeValue() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.setCodeValue() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.setCodeValue() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public Object[] getLangList() throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("select cm_micode,cm_codename from cmm0020 \n");
    	    strQuery.append("where cm_macode='LANGUAGE' and cm_micode<>'****' and cm_closedt is null \n");
    	    strQuery.append("order by cm_micode \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            while(rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));

				strQuery.setLength(0);
  	          	strQuery.append("select cm_exename,cm_exeno from cmm0023 \n");
  	          	strQuery.append("where cm_langcd=? \n");

  	          	pstmt2 = conn.prepareStatement(strQuery.toString());
  	          	pstmt2.setString(1,rs.getString("cm_micode"));
  	          	rs2 = pstmt2.executeQuery();
  	          	if (rs2.next()){
  	          		if (rs2.getString("cm_exename") != null && rs2.getString("cm_exename") != ""){
  	          			String exename = rs2.getString("cm_exename");
  	          			if (exename.substring(exename.length()-1).equals(",")){
  	          				rst.put("cm_exename", exename.substring(0,exename.length()-1));
  	          			}else{
  	          				rst.put("cm_exename", exename);
  	          			}
  	          			rst.put("cm_exeno",rs2.getString("cm_exeno"));
  	          			exename = null;
  	          		}
  	          	}
  	          	rsval.add(rst);
  	          	rs2.close();
  	          	pstmt2.close();
  	          	rs2 = null;
  	          	pstmt2 = null;
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getLangList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getLangList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getLangList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getLangList() Exception END ##");
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
					ecamsLogger.error("## Cmm0100.getLangList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public String getExeName(String txtCode0) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		String				resultStr	= "";

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("select cm_exename from cmm0023 where \n");
    	    strQuery.append("cm_langcd=? \n");//Txt_Code0

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, txtCode0);
            rs = pstmt.executeQuery();

            if(rs.next()){
            	resultStr = rs.getString("cm_exename");
            	if (resultStr.substring(resultStr.length()-1).equals(",")){
            		resultStr = resultStr.substring(0,resultStr.length()-1);
            	}
            }
		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

    		return resultStr;

    	} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getExeName() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getExeName() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getExeName() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getExeName() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getExeName() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public String setLangInfo(HashMap<String,String> dataObj) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
		    strQuery.append("select cm_micode from cmm0020 where \n");
		    strQuery.append("cm_macode='LANGUAGE' and cm_micode=? \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, dataObj.get("Txt_Code0"));
            rs = pstmt.executeQuery();

            strQuery.setLength(0);
            if (rs.next()){
            	strQuery.append("update cmm0020 set \n");
            	if (dataObj.get("CodeDelYN").equals("N")){
            		strQuery.append("cm_closedt='', \n");
            	}else{
            		strQuery.append("cm_closedt=SYSDATE, \n");
            	}
            	strQuery.append("cm_codename=?, \n");
            	strQuery.append("cm_lastupdt=SYSDATE \n");
            	strQuery.append("where cm_macode='LANGUAGE' and cm_micode=? \n");
            }else if(dataObj.get("CodeDelYN").equals("Y")){
            	throw new Exception("Error : 폐기 할  코드값이 없습니다. 코드값를 확인해 주십시오.");
            }else{
 	           	strQuery.append("insert into cmm0020 (cm_macode,cm_codename,cm_micode,cm_creatdt, \n");
 	           	strQuery.append("cm_lastupdt) values ('LANGUAGE', ?,?, SYSDATE, SYSDATE) \n");
            }
        	pstmt2 = conn.prepareStatement(strQuery.toString());
        	pstmt2.setString(1, dataObj.get("Txt_Code1").replace("'", "''"));
        	pstmt2.setString(2, dataObj.get("Txt_Code0"));
        	pstmt2.executeUpdate();
        	pstmt2.close();
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
            strQuery.append("delete cmm0023 where cm_langcd=? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, dataObj.get("Txt_Code0"));
            pstmt.executeUpdate();
            pstmt.close();

            if (dataObj.get("CodeDelYN").equals("N")){
	            String txtCode2 = dataObj.get("Txt_Code2");
	            if (!txtCode2.substring(txtCode2.length()-1).equals(",")){
	            	txtCode2 = txtCode2 + ",";
	            }
	            strQuery.setLength(0);
	            strQuery.append("insert into cmm0023 (cm_langcd,cm_exename,cm_exeno) values (\n");
	            strQuery.append("?,?, \n");//'" & Txt_Code(0) & "' WkA
	            if (dataObj.get("Chk_ExeNo").equals("true")){
	            	strQuery.append("'Y')\n");
	            }else{
	            	strQuery.append("'N')\n");
	            }
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, dataObj.get("Txt_Code0"));
	            pstmt.setString(2, txtCode2);
	            pstmt.executeUpdate();
	            pstmt.close();
            }

		    conn.commit();
            conn.close();
            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return dataObj.get("Txt_Code1");

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setLangInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.setLangInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setLangInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.setLangInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.setLangInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public Object[] getJobList() throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("select cm_jobcd,cm_jobname from cmm0102 \n");
    	    strQuery.append("order by cm_jobcd \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            while(rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
  	          	rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getJobList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getJobList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getJobList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getJobList() Exception END ##");
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
					ecamsLogger.error("## Cmm0100.getJobList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public boolean setJobInfo(ArrayList<HashMap<String,String>> JobList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
		    strQuery.append("delete cmm0102 ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.executeUpdate();
            pstmt.close();

            int i=0;
            for (i=0 ; i<JobList.size() ; i++){
                strQuery.setLength(0);
                strQuery.append("insert into cmm0102 (cm_jobcd,cm_jobname,cm_useyn) ");
                strQuery.append("values (?,?,'Y') ");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1, JobList.get(i).get("cm_jobcd"));
                pstmt.setString(2, JobList.get(i).get("cm_jobname"));
                pstmt.executeUpdate();
                pstmt.close();
            }

		    conn.commit();
            conn.close();
            strQuery = null;
            pstmt = null;
            conn = null;

			return true;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setJobInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.setJobInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setJobInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.setJobInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.setJobInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public boolean setJobInfo_individual(String cm_jobcd,String cm_jobname) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
		    strQuery.append("delete cmm0102 ");
		    strQuery.append("where cm_jobcd = ? ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, cm_jobcd);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("insert into cmm0102 (cm_jobcd,cm_jobname,cm_useyn) ");
            strQuery.append("values (?,?,'Y') ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, cm_jobcd);
            pstmt.setString(2, cm_jobname);
            pstmt.executeUpdate();
            pstmt.close();

		    conn.commit();
            conn.close();
            strQuery = null;
            pstmt = null;
            conn = null;

			return true;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setJobInfo_individual() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.setJobInfo_individual() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.setJobInfo_individual() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.setJobInfo_individual() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.setJobInfo_individual() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public boolean delJobInfo(ArrayList<HashMap<String,String>> jobList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			ecamsLogger.error("size: " + jobList.size() + "\n");
			ecamsLogger.error("data: " + jobList.toString() + "\n");
			
			for (int i=0 ; i<jobList.size() ; i++)
			{
				strQuery.setLength(0);
			    strQuery.append("delete cmm0102 ");
			    strQuery.append("where cm_jobcd = ? ");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, jobList.get(i).get("cm_jobcd"));
	            pstmt.executeUpdate();
	            pstmt.close();
			}

		    conn.commit();
            conn.close();
            strQuery = null;
            pstmt = null;
            conn = null;

			return true;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.delJobInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.delJobInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.delJobInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.delJobInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.delJobInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

    public Document getProgInfoTree() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml		  myXml		  = new CreateXml();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select cm_prginfcd, cm_upprginfcd, cm_prginfname  \n");
			strQuery.append("from (Select * from cmm0104 where cm_useyn='Y')   \n");
			strQuery.append("start with cm_upprginfcd is null  				   \n");
			strQuery.append("connect by prior cm_prginfcd = cm_upprginfcd      \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            myXml.init_Xml("ID","cm_prginfcd","cm_prginfname","cm_upprginfcd","isBranch");

            rs = pstmt.executeQuery();

            while (rs.next()){
				myXml.addXML(rs.getString("cm_prginfcd"),
						rs.getString("cm_prginfcd"),
						rs.getString("cm_prginfname"),
						rs.getString("cm_upprginfcd"),"true",
						rs.getString("cm_upprginfcd"));

				strQuery.setLength(0);
				strQuery.append("select b.cm_micode,b.cm_codename from cmm0020 b,cmm0105 a \n");
				strQuery.append(" where a.cm_prginfcd=?                        \n");
				strQuery.append("   and b.cm_macode='RSCHKITEM'                \n");
				strQuery.append("   and b.cm_micode=a.cm_infcode               \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cm_prginfcd"));
				rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					myXml.addXML(rs.getString("cm_prginfcd")+rs2.getString("cm_micode"),
							rs.getString("cm_prginfcd")+rs2.getString("cm_micode"),
							rs2.getString("cm_codename"),
							rs.getString("cm_prginfcd"),"false",
							rs.getString("cm_prginfcd"));
				}
				rs2.close();
				pstmt2.close();

			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
            return myXml.getDocument();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getProgInfoTree() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getProgInfoTree() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getProgInfoTree() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getProgInfoTree() Exception END ##");
			throw exception;
		}finally{
			if (myXml != null)	myXml = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getProgInfoTree() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getProgInfoTree() method statement
    public String getProcInfo_Init() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            maxMicode   = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select rpad('0',max(cm_micode),'0') max  \n");
			strQuery.append("  from cmm0020                           \n");
			strQuery.append(" where cm_macode='RSCHKITEM' 		      \n");
			strQuery.append("   and cm_micode<>'****'                 \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            if (rs.next()){
            	maxMicode = rs.getString("max");
            }
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
            return maxMicode;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getProcInfo_Init() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getProcInfo_Init() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getProcInfo_Init() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getProcInfo_Init() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getProcInfo_Init() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getProcInfo_Init() method statement
    public Object[] getCodeInfo() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            rsInfo      = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

        	strQuery.setLength(0);
        	strQuery.append("select cm_infcode from cmm0105           \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	rs = pstmt.executeQuery();
        	while (rs.next()) {
        		rsInfo = rsInfo + "," + rs.getString("cm_infcode");
        	}
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
			strQuery.append("Select cm_micode,cm_codename from cmm0020         \n");
			strQuery.append(" where cm_macode='RSCHKITEM' and cm_micode<>'****'\n");
			strQuery.append(" order by cm_micode          				       \n");

            pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cm_micode", rs.getString("cm_micode"));
            	rst.put("cm_codename", rs.getString("cm_codename"));
            	if (rsInfo.indexOf(rs.getString("cm_micode"))>=0) {
            		rst.put("enabled", "0");
            		rst.put("checkbox", "1");
            	} else {
            		rst.put("enabled", "1");
            		rst.put("checkbox", "0");
            	}
    //        	rst.put("selected", "0");
            	rtList.add(rst);
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
			ecamsLogger.error("## Cmm0100.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getCodeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getProgInfoTree() method statement

    public Object[] getCodeSelInfo(String res) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            rsInfo1      = "";
		String            rsInfo2      = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
        	strQuery.append("select cm_infcode from cmm0105           \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	rs = pstmt.executeQuery();
        	while (rs.next()) {
        		rsInfo1 = rsInfo1 + "," + rs.getString("cm_infcode");
        	}
        	rs.close();
        	pstmt.close();

        	if (res != "" && res != null) {
            	strQuery.setLength(0);
            	strQuery.append("select * from cmm0105           \n");
            	strQuery.append("where cm_prginfcd = ?			           \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt =  new LoggableStatement(conn, strQuery.toString());
            	pstmt.setString(1, res);
            	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	rs = pstmt.executeQuery();

                while (rs.next()){
                	rsInfo2 = rsInfo2 + "," + rs.getString("cm_infcode");
                }
                rs.close();
                pstmt.close();
        	}
        	ecamsLogger.error("+++ rsInfo 1 : "+ rsInfo1);
        	ecamsLogger.error("+++ rsInfo 2 : "+ rsInfo2);
			strQuery.setLength(0);
			strQuery.append("Select cm_micode,cm_codename from cmm0020         \n");
			strQuery.append(" where cm_macode='RSCHKITEM' and cm_micode<>'****'\n");
			strQuery.append(" order by cm_micode          				       \n");

            pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

        	while (rs.next()) {
        		rst = new HashMap<String, String>();
            	rst.put("cm_micode", rs.getString("cm_micode"));
            	rst.put("cm_codename", rs.getString("cm_codename"));

            	if (rsInfo1.indexOf(rs.getString("cm_micode"))>=0) {
            		rst.put("enable", "0");
            		rst.put("checkbox", "1");

            		if (rsInfo2.indexOf(rs.getString("cm_micode"))>=0) {
                		rst.put("enable", "1");
                	}
            	} else {
            		rst.put("enable", "1");
            		rst.put("checkbox", "0");
            	}

                rtList.add(rst);
        	}
        	rs.close();
        	pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
            return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getCodeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getProgInfoTree() method statement

    public String subNewDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String DeptName = dataObj.get("prginfname");
			String DeptUpCd = dataObj.get("upprginfcd");
			String DeptCd 	=	"";
			strQuery.setLength(0);
            strQuery.append("select 'RSRC'||lpad(to_number(nvl(max(Substr(CM_PRGINFCD,5,5)),0)) + 1,5,'0') as prginfcd 	");
            strQuery.append("from cmm0104  	");
            pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		DeptCd = rs.getString("prginfcd");
        	}
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
	        strQuery.append("insert into cmm0104 (CM_PRGINFCD,CM_PRGINFNAME,CM_UPPRGINFCD,CM_USEYN) values ( ");
	        strQuery.append("?,?,?,'Y') ");//PrjNo,TKey,Dirname,Docseq
        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	pstmt.setString(1, DeptCd);
        	pstmt.setString(2, DeptName);
        	pstmt.setString(3, DeptUpCd);
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();
			conn = null;
			pstmt = null;
			rs = null;
        	return dataObj.get("gbncd") + DeptCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subNewDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.subNewDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subNewDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.subNewDir() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.subNewDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int subDelDir_Check(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int cnt = 0;
		try {
			conn = connectionContext.getConnection();
			String DeptCd = dataObj.get("upprginfcd");

			strQuery.setLength(0);
	        strQuery.append("select count(*) cnt from cmm0105          \n");
	        strQuery.append(" where cm_prginfcd in (select cm_prginfcd \n");
	        strQuery.append("                         from (select * from cmm0104)\n");
	        strQuery.append("                        start with cm_prginfcd=?     \n");
	        strQuery.append("                        connect by prior cm_prginfcd=cm_upprginfcd) \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, DeptCd);
	    	rs = pstmt.executeQuery();
	    	if (rs.next()) {
	    		cnt = rs.getInt("cnt");
	    	}
	    	rs.close();
	    	pstmt.close();

	    	conn.close();
			conn = null;
			pstmt = null;
			rs = null;
	    	return cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subDelDir_Check() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.subDelDir_Check() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subDelDir_Check() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.subDelDir_Check() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.subDelDir_Check() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String subDelDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String DeptCd = dataObj.get("upprginfcd");

			strQuery.setLength(0);
	        strQuery.append("delete cmm0104                                     \n");//PrjNo
	        strQuery.append(" where cm_prginfcd in (select cm_prginfcd          \n");
	        strQuery.append("                       from (select * from cmm0104)\n");
	        strQuery.append("                      start with cm_prginfcd=?     \n");
	        strQuery.append("                      connect by prior cm_prginfcd=cm_upprginfcd) \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, DeptCd);
	    	pstmt.executeUpdate();
	    	pstmt.close();
	    	conn.close();
	    	pstmt = null;
	    	conn = null;
	    	return DeptCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subDelDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.subDelDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subDelDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.subDelDir() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.subDelDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String subRename(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String DeptName = dataObj.get("prginfname");
			String DeptCd = dataObj.get("prginfcd");


			strQuery.setLength(0);
	        strQuery.append("update cmm0104 set CM_prginfname=?      \n");
	        strQuery.append(" where CM_prginfcd=?                  \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());

        	pstmt.setString(1, DeptName);
        	pstmt.setString(2, DeptCd);
        	pstmt.executeUpdate();
	    	pstmt.close();

	    	conn.close();
	    	pstmt = null;
	    	conn = null;
	    	return DeptCd;

		} catch (SQLException sqlexception) {
			ecamsLogger.error("## Cmm0100.subRename() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.subRename() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.subRename() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.subRename() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.subRename() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    public String updtProgInfo(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String PrgInfCd = dataObj.get("prginfcd");
			String infCd[] = dataObj.get("proginfo").split(",");

			strQuery.setLength(0);
            strQuery.append("delete cmm0105 where cm_prginfcd=?	\n");
            pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, PrgInfCd);
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

        	for (int i=0;infCd.length>i;i++) {
        		strQuery.setLength(0);
        		strQuery.append("insert into cmm0105 (CM_PRGINFCD,CM_INFCODE) values (?, ?) ");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt =  new LoggableStatement(conn, strQuery.toString());
            	pstmt.setString(1, PrgInfCd);
            	pstmt.setString(2, infCd[i]);
            	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
        	}

        	conn.close();

        	pstmt = null;
        	conn = null;

        	return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.updtProgInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.updtProgInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.updtProgInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.updtProgInfo() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.updtProgInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
	public Document getItemInfoTree(String cm_typecd) throws SQLException, Exception {//체크리스트 항목 정보 트리
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml		  myXml		  = new CreateXml();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			boolean searchAllSw = true; //전체 검색 여부
			
			if( (null != cm_typecd) && !"".equals(cm_typecd) && (!"00".equals(cm_typecd)) ) { //입력받은 typecd값이 null아니면서 00(전체)가 아니면
				searchAllSw = false;
			}
			
			//전체항목
			strQuery.setLength(0);
			strQuery.append("SELECT				cm_gbncd, cm_uppgbncd, cm_gbnname, cm_seq, cm_reqyn, cm_typecd		\n");
			strQuery.append("FROM				CMM0110																\n");
			strQuery.append("START WITH			cm_uppgbncd IS NULL													\n");
			strQuery.append("AND				cm_clsdate IS NULL													\n");
			if( !searchAllSw ){
				strQuery.append("AND				cm_typecd = ?													\n");
			}
			strQuery.append("CONNECT BY PRIOR	cm_gbncd = cm_uppgbncd												\n");
			strQuery.append("AND				cm_clsdate IS NULL													\n");
			if( !searchAllSw ){
				strQuery.append("AND				cm_typecd = ?													\n");
			}			
			strQuery.append("ORDER BY DECODE	(cm_uppgbncd,'',cm_gbncd,cm_uppgbncd) || DECODE(cm_uppgbncd,'','-1',cm_seq)		\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            
			if( !searchAllSw ){
				pstmt.setString(1, cm_typecd);
				pstmt.setString(2, cm_typecd);
			}
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            myXml.init_Xml("ID","cm_gbncd","cm_gbnname","cm_reqyn","cm_viewname","isBranch","cm_typecd","cm_uppgbncd");
            rs = pstmt.executeQuery();
            
            String viewName = null;
            String isBranch = null;
            
            while (rs.next()){
            	viewName = "";
            	isBranch = "";
            	
            	//항목별 자식여부 확인
            	strQuery.setLength(0);
            	strQuery.append("SELECT		COUNT(*) AS cnt													\n");
            	strQuery.append("FROM		CMM0110															\n");
            	strQuery.append("WHERE		cm_gbncd IN (	SELECT		cm_gbncd							\n");
            	strQuery.append("							FROM		CMM0110								\n");
            	strQuery.append("							START WITH	cm_gbncd = ?						\n");
            	strQuery.append("							AND			cm_clsdate IS NULL					\n");
            	strQuery.append("							CONNECT BY PRIOR	cm_gbncd = cm_uppgbncd )	\n");
            	strQuery.append("AND		cm_gbncd <> ?													\n");
            	strQuery.append("AND		cm_clsdate IS NULL												\n");
            	
            	pstmt2 = conn.prepareStatement(strQuery.toString());	
            	pstmt2.setString(1, rs.getString("cm_gbncd"));
            	pstmt2.setString(2, rs.getString("cm_gbncd"));
            	
            	rs2 = pstmt2.executeQuery();
            	if( rs2.next() ) {
            		if( rs2.getInt("cnt") > 0 ) {
            			isBranch = "true";
            		} else {
            			isBranch = "false";
            		}
            	}
            	
            	rs2.close();
            	pstmt2.close();
            	
            	
            	
            	if( rs.getString("cm_reqyn").equals("Y") ) {
            		viewName = "[필수]"+rs.getString("cm_gbnname");
            	} else {
            		viewName = rs.getString("cm_gbnname");
            	}
            	
				myXml.addXML(rs.getString("cm_gbncd"),
						rs.getString("cm_gbncd"), 
						rs.getString("cm_gbnname"),
						rs.getString("cm_reqyn"), 
						viewName,
						isBranch,
						rs.getString("cm_typecd"),
						rs.getString("cm_uppgbncd")
						);						
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			conn = null;
			
            return myXml.getDocument();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getItemInfoTree() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0100.getItemInfoTree() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getItemInfoTree() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0100.getItemInfoTree() Exception END ##");
			throw exception;
		}finally{
			if (myXml != null)	myXml = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.getItemInfoTree() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTeamInfoTreeCheckBox() method statement    
	
	
	
	public String newItemInfo(HashMap<String,String> dataObj) throws SQLException, Exception { //체크리스트 항목 등록 시
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		PreparedStatement pstmt3      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT		LPAD(NVL(MAX(cm_gbncd),'0')+1,4,0) AS cm_gbncd	\n");
			strQuery.append("FROM		CMM0110											\n");
			
			//ecamsLogger.error("===================="+dataObj.get("cm_gbncd"));
			
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		strQuery.setLength(0);
        		strQuery.append("SELECT		NVL(MAX(cm_seq),0)+1 AS cm_seq	\n");
        		strQuery.append("FROM		CMM0110							\n");
        		strQuery.append("WHERE		cm_uppgbncd = ?					\n");
        		
                pstmt2 = conn.prepareStatement(strQuery.toString());
                //pstmt2 =  new LoggableStatement(conn, strQuery.toString());
            	pstmt2.setString(1, dataObj.get("cm_uppgbncd"));
        		
            	
            	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
            	rs2 = pstmt2.executeQuery();
            	if (rs2.next()){
            		//ecamsLogger.error("=========="+rs2.getString("cm_seq"));
            		strQuery.setLength(0);
            		strQuery.append("INSERT INTO CMM0110 (cm_gbncd, cm_uppgbncd, cm_gbnname, cm_reqyn, cm_seq, cm_creatdt, cm_lstusr, cm_typecd) \n");
            		strQuery.append("VALUES(?,?,?,?,?,sysdate,?,?)\n");
            		
                    pstmt3 = conn.prepareStatement(strQuery.toString());
                    //pstmt3 =  new LoggableStatement(conn, strQuery.toString());
                	pstmt3.setString(1, rs.getString("cm_gbncd"));
                	pstmt3.setString(2, dataObj.get("cm_uppgbncd"));
                	pstmt3.setString(3, dataObj.get("cm_gbnname"));
                	pstmt3.setString(4, dataObj.get("cm_reqyn"));
                	pstmt3.setString(5, rs2.getString("cm_seq"));
                	pstmt3.setString(6, dataObj.get("cm_lstusr"));
                	pstmt3.setString(7, dataObj.get("cm_typecd"));
                	
                	
                	//ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
                	
                	pstmt3.executeUpdate();
        	    	pstmt3.close();
            	}
            	
            	rs2.close();
            	pstmt2.close();
        	}
        	
        	rs.close();
        	pstmt.close();
        	
        	rs = null;
        	rs2 = null;
        	pstmt = null;
        	pstmt2 = null;
        	pstmt3 = null;
        	
        	return "OK";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0100.newItemInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.newItemInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0100.newItemInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.newItemInfo() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.newItemInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}	
	
	
	public String updateItemInfo(HashMap<String,String> dataObj) throws SQLException, Exception {// 체크리스트 항목명 변경시
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("UPDATE  CMM0110					\n");
			strQuery.append("SET     cm_gbnname = ?,			\n");
			strQuery.append("        cm_reqyn = ?,				\n");
			strQuery.append("        cm_lastdate = sysdate,		\n");
			strQuery.append("        cm_lstusr = ?				\n");
			strQuery.append("WHERE   cm_gbncd = ?				\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, dataObj.get("cm_gbnname"));
        	pstmt.setString(2, dataObj.get("cm_reqyn"));
        	pstmt.setString(3, dataObj.get("cm_lstusr"));
        	pstmt.setString(4, dataObj.get("cm_gbncd"));
            
        	pstmt.executeUpdate();
        	pstmt.close();
        	pstmt = null;
        	
        	return "OK";
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
			ecamsLogger.error("## Cmm0100.updateItemInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.updateItemInfo() SQLException END ##");
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
			ecamsLogger.error("## Cmm0100.updateItemInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.updateItemInfo() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.updateItemInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
	public String delItemInfo(HashMap<String,String> dataObj) throws SQLException, Exception { //체크리스트 항목명 삭제 시
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE      CMM0110														\n");
			strQuery.append("SET         cm_clsdate = sysdate,											\n");
			strQuery.append("	         cm_seq 	= 0,												\n");
			strQuery.append("	         cm_lstusr 	= ?													\n");
			strQuery.append("WHERE       cm_gbncd in (   SELECT              cm_gbncd					\n");
			strQuery.append("                            FROM                CMM0110					\n");
			strQuery.append("                            START WITH          cm_gbncd = ?				\n");
			strQuery.append("                            CONNECT BY PRIOR    cm_gbncd = cm_uppgbncd )	\n");
			
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, dataObj.get("cm_lstusr"));
	    	pstmt.setString(2, dataObj.get("cm_gbncd"));
	    	pstmt.executeUpdate();
	    	
	    	pstmt.close();
	    	conn.close();
			conn = null;
			pstmt = null;

	    	return "OK";

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
			ecamsLogger.error("## Cmm0100.delItemInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.delItemInfo() SQLException END ##");
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
			ecamsLogger.error("## Cmm0100.delItemInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.delItemInfo() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.delItemInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}	
	
	public Object[] getItemInfoStepList(String cm_gbncd) throws SQLException, Exception{ //항목 하위 요소 순서 리스트
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("SELECT  	cm_gbncd, cm_gbnname, cm_uppgbncd, cm_reqyn, cm_seq		\n");
			strQuery.append("FROM    	CMM0110													\n");
			strQuery.append("WHERE   	cm_uppgbncd = ?											\n");
			strQuery.append("AND     	cm_clsdate IS NULL										\n");
			strQuery.append("ORDER BY	cm_seq													\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, cm_gbncd);
            rs = pstmt.executeQuery();

            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_gbncd", rs.getString("cm_gbncd"));
				rst.put("cm_uppgbncd", rs.getString("cm_uppgbncd"));
				rst.put("cm_seq", rs.getString("cm_seq"));
				rst.put("cm_reqyn", rs.getString("cm_reqyn"));
				rst.put("cm_gbnname", rs.getString("cm_gbnname"));
				
				rsval.add(rst);
				rst = null;
            }
		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getItemInfoStepList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.getItemInfoStepList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0100.getItemInfoStepList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.getItemInfoStepList() Exception END ##");
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
					ecamsLogger.error("## Cmm0100.getItemInfoStepList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}	
	
	public String updateItemInfoStep(ArrayList<HashMap<String,String>> chkListStep_dp) throws SQLException, Exception{ //체크리스트 항목 순서 변경
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			conn.setAutoCommit(false);
			
			int chkListStep_dp_Length = chkListStep_dp.size();
			
			for(int i=0; i<chkListStep_dp_Length; i++) {
				strQuery.setLength(0);
	    		strQuery.append("UPDATE		CMM0110			\n");
	    		strQuery.append("SET		cm_seq = ?		\n");
	    		strQuery.append("WHERE		cm_gbncd = ?	\n");
	    		
	    		
	    		pstmt = conn.prepareStatement(strQuery.toString());
//	    		pstmt.setString(1, chkListStep_dp.get(i).get("cm_seq"));
	    		pstmt.setString(1, Integer.toString(i+1));
	    		pstmt.setString(2, chkListStep_dp.get(i).get("cm_gbncd"));	    		
	    		
	    		pstmt.executeUpdate();
	    		pstmt.close();
			}

		    conn.commit();
		    conn.setAutoCommit(true);

            pstmt.close();
            conn.close();

            strQuery = null;
            pstmt = null;
            conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.updateItemInfoStep() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0100.updateItemInfoStep() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0100.updateItemInfoStep() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0100.updateItemInfoStep() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0100.updateItemInfoStep() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}