/*****************************************************************************************
	1. program ID	: eCmd3100.java
	2. create date	: 2008.07. 10
	3. auth		    : No name
	4. update date	: 09 06 16
	5. auth		    : No name
	6. description	: eCmd3100(프로그램목록)
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.google.gson.Gson;

//import app.common.LoggableStatement;
import app.common.LoggableStatement;
import app.common.UserInfo;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd3100{
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

    public Object[] getJogun(int Index) throws Exception{
		ArrayList<HashMap<String, String>>  rsval = null;
		HashMap<String, String>			  rst		  = null;
		HashMap<String, Object>			  rst2		  = null;
		Map<String, String>			  rst3		  = null;
		Map<String, Object>			  rst4		  = null;
		Object[]		  rtObj		  = null;
		List<Map<String, String>> resultList = null;
		
		try {
			
			rsval = new ArrayList<HashMap<String, String>>();
            rsval.clear();
        	rst = new HashMap<String, String>();
			rst.put("cm_codename", "선택하세요");
			rst.put("cm_micode", "0");
			rst.put("Index", Integer.toString(Index));
			rsval.add(rst);
			rst = null;

            if (Index == 1){
            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "프로그램종류");
				rst.put("cm_micode", "1");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;

            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "프로그램상태");
				rst.put("cm_micode", "3");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;
			}else{
            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "프로그램명");
				rst.put("cm_micode", "1");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;

            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "프로그램설명");
				rst.put("cm_micode", "2");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;

            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "최종변경자");
				rst.put("cm_micode", "3");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;

            	rst = new HashMap<String, String>();
				rst.put("cm_codename", "프로그램경로");
				rst.put("cm_micode", "4");
				rst.put("Index", Integer.toString(Index));
				rsval.add(rst);
				rst = null;
			}

            rtObj = rsval.toArray();
            rsval.clear();
            rsval = null;

    		return rtObj;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd3100.getJogun() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd3100.getJogun() Exception END ##");
			throw exception;
		}
		finally{
			if (rtObj != null)	rtObj = null;
		}
	}//end of getJogun() method statement


    public Object[] getCode(String L_Syscd,int Index) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			if (!L_Syscd.equals("00000")){
				switch(Index){
	            case 1:
	              	strQuery.append("select distinct a.cm_micode,a.cm_codename from cmm0020 a,cmm0036 b \n");
	            	strQuery.append("where b.cm_syscd=? and \n");//L_Syscd
	                strQuery.append("a.cm_macode='JAWON' and \n");
	                strQuery.append("a.cm_micode=b.cm_rsrccd and \n");
	                break;
                case 2:
	            	strQuery.append("select distinct a.cm_micode,a.cm_codename from cmm0020 a \n");
	                strQuery.append("where a.cm_macode='CMR0020' and a.cm_micode in ('0','3','5','7','B') and\n");
	                break;
                case 3:
	            	strQuery.append("select distinct a.cm_micode,a.cm_codename from cmm0020 a,cmm0032 b \n");
	                strQuery.append("where b.cm_syscd=? and \n");//L_Syscd
	                strQuery.append("cm_macode='LANGUAGE' and \n");
	                strQuery.append("a.cm_micode=b.cm_langcd and \n");
	                break;
				}
				strQuery.append("a.cm_micode<>'****' and \n");
	            strQuery.append("a.cm_closedt is null \n");
	            strQuery.append("order by a.cm_micode \n");
			}else{
				strQuery.append("select cm_micode,cm_codename from cmm0020 where \n");
	            switch(Index){
				case 1:
					strQuery.append("cm_macode='JAWON' and \n");
					break;
				case 2:
					strQuery.append("cm_macode='CMR0020' and cm_micode in ('0','3','5','7','B') and \n");
					break;
				case 3:
					strQuery.append("cm_macode='LANGUAGE' and \n");
					break;
	            }
	            strQuery.append("cm_micode<>'****' and \n");
	            strQuery.append("cm_closedt is null \n");
	            strQuery.append("order by decode(cm_seqno,0,cm_micode) \n");
			}
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
			if (!L_Syscd.equals("00000")){
				switch(Index){
	            	case 1:	pstmt.setString(1, L_Syscd);break;
	            	case 3:	pstmt.setString(1, L_Syscd);break;
				}
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
			rst = new HashMap<String, String>();
			rst.put("cm_micode", "ALL");
			rst.put("cm_codename", "전체");
           	rsval.add(rst);
           	rst = null;
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmd3100.getCode() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd3100.getCode() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd3100.getCode() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd3100.getCode() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd3100.getCode() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getCode() method statement

    public Object[] Cmd3100_Cbo_SysCd(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			UserInfo userinfo = new UserInfo();
			Boolean tmp = userinfo.isAdmin(UserId);

			strQuery.setLength(0);
	        strQuery.append("select cm_syscd,cm_sysmsg \n");
	        strQuery.append("from cmm0030 where \n");
	        strQuery.append("cm_closedt is null and substr(cm_sysinfo,1,1)='0' \n");
            if (!tmp){
            	strQuery.append("and cm_syscd in(\n");
            	strQuery.append("select distinct cm_syscd from cmm0044 where \n");
            	strQuery.append("cm_userid=? and cm_closedt is null) \n");//UserId
			}
            strQuery.append("order by cm_sysmsg \n");

            pstmt = conn.prepareStatement(strQuery.toString());
	        if (!tmp){
	        	pstmt.setString(1, UserId);
	        }
		    rs = pstmt.executeQuery();

			rst = new HashMap<String, String>();
			rst.put("cm_sysmsg", "전체");
			rst.put("cm_syscd", "00000");
           	rsval.add(rst);
           	rst = null;

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_syscd", rs.getString("cm_syscd"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

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
	}//end of getCbo_SysCd() method statement


   	public Object[] getSql_Qry(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		String            WkCond2     = "";
		int 			  CNT 		  = 0;
		String            Txt_Cond    = "";
		String            Cbo_Cond2_code  = "";
		String			  strRsrcCd = "";
		String            strRsrc[] = null;
		int               i = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (etcData.get("Txt_Cond").length() > 0) Txt_Cond = etcData.get("Txt_Cond");
			else Txt_Cond = "";

			if (etcData.get("Cbo_Cond2_code").length() >0 ) Cbo_Cond2_code=etcData.get("Cbo_Cond2_code");
			else Cbo_Cond2_code="";

			WkCond2 = "";
		    if (etcData.get("Cbo_Cond11_code").equals("3") && Txt_Cond != "" ){
		    	strQuery.append("select cm_userid from cmm0040 where \n");
		    	strQuery.append(" cm_username = ? \n"); //Txt5
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, Txt_Cond);
	            rs = pstmt.executeQuery();
				while (rs.next()){
		             if (WkCond2 != "" ) {
		            	 WkCond2 = WkCond2 + ",";
		             }
		             WkCond2 = WkCond2 + rs.getString("cm_userid");
				}
				rs.close();
				pstmt.close();

				WkCond2 = WkCond2.replace(",", "','");
			}

		    if (Integer.parseInt(etcData.get("Cbo_Cond10_code")) > 0 && !Cbo_Cond2_code.equals("")) {
				if (Integer.parseInt(etcData.get("Cbo_Cond10_code")) == 1) {
					strRsrcCd = "Y";//프로그램종류 별 일때
				}
		    }

		    if (!strRsrcCd.equals("Y")) {
		    	strQuery.setLength(0);
				strQuery.append("select cm_rsrccd from cmm0036                     \n");
				strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
				strQuery.append("   and substr(cm_info, 2, 1)='1'                  \n");
				strQuery.append("   and substr(cm_info, 26, 1)='0'                 \n");
				strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
				strQuery.append("                           from cmm0037           \n");
				strQuery.append("                          where cm_syscd=?        \n");
				strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");

				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, etcData.get("L_SysCd"));
	            pstmt.setString(2, etcData.get("L_SysCd"));

	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	if (strRsrcCd != "") strRsrcCd = strRsrcCd + ",";
	            	strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd");
	            }
				rs.close();
				pstmt.close();

				strRsrc = strRsrcCd.split(",");
		    }
		    strQuery.setLength(0);
		    strQuery.append("select /*+ ALL ROWS */                                   \n");
		    strQuery.append("      a.cr_itemid,a.cr_syscd,a.cr_dsncd,a.cr_rsrcname,   \n");
		    strQuery.append("      a.cr_rsrccd,a.cr_lstver,a.cr_status,a.cr_jobcd,    \n");
		    strQuery.append("      a.cr_story,a.cr_editor,                            \n");
		    strQuery.append("      to_char(a.cr_lastdate,'yyyy/mm/dd') cr_lastdate,   \n");
    		strQuery.append("      a.cr_langcd,h.cm_sysmsg,d.cm_jobname job,          \n");
    		strQuery.append("      e.cm_codename rsrccd,g.cm_dirpath,                 \n");
			strQuery.append("      h.cm_sysgb,i.cm_codename sta,j.cm_Info,            \n");
			strQuery.append("      b.cm_username                \n");
		    strQuery.append(" from cmm0036 j,cmm0030 h,cmm0070 g,cmm0020 i,cmm0102 d, \n");
		    if (etcData.get("L_JobCd").equals("0000") && !etcData.get("SecuYn").equals("Y")) {
	    	   strQuery.append(" cmm0044 y, \n");
	        }
		    strQuery.append("      cmm0020 e,cmm0040 b,cmr0020 a where      \n");
		    if (!etcData.get("L_SysCd").equals("00000")){
		    	strQuery.append(" a.cr_syscd=? and \n"); //L_Syscd
		    }

	        if (etcData.get("L_JobCd").equals("0000") && !etcData.get("SecuYn").equals("Y")) {
 	    	   	strQuery.append("    y.cm_syscd=a.cr_syscd and y.cm_userid=? and  \n");
 	    	   	strQuery.append("    y.cm_closedt is null and                     \n");
 	    	   	strQuery.append("    y.cm_jobcd=a.cr_jobcd and                    \n");
		    }else if (!etcData.get("L_JobCd").equals("0000")){
	        	strQuery.append(" a.cr_jobcd=? and \n"); //L_Jobcd
	        }

	        if (Integer.parseInt(etcData.get("Cbo_Cond10_code")) > 0 && !Cbo_Cond2_code.equals("")) {//조건1콤보가 전체 아니고 하위콤보도 전체 아닐때
				switch(Integer.parseInt(etcData.get("Cbo_Cond10_code"))){
	            case 1:
	               strQuery.append(" a.cr_rsrccd=? and                               \n");
	               break;
	            case 2:
	            	strQuery.append(" a.cr_status=? and                               \n");
	               break;
	            case 3:
	            	strQuery.append(" a.cr_langcd=? and                               \n");
	               break;
				}
	        }

	        if (Integer.parseInt(etcData.get("Cbo_Cond11_code")) > 0 && Txt_Cond != ""){
				switch(Integer.parseInt(etcData.get("Cbo_Cond11_code"))){
	            case 1:
	            	strQuery.append(" upper(a.cr_rsrcname) like replace(upper(?),'_','\\_') escape '\\' and \n");
	            	break;
	            case 2:
	            	strQuery.append(" a.cr_story is not null and a.cr_story like replace(?,'_','\\_') escape '\\' and \n");
	            	break;
	            case 3:
	            	strQuery.append(" a.cr_editor is not null and rtrim(a.cr_editor) in (?) and \n");//WkCond2
	            	break;
	            case 4:
	            	strQuery.append(" g.cm_dirpath like replace(?,'_','\\_') escape '\\' and \n");
	            	break;
				}
	        }

	        if (etcData.get("Cbo_Option").equals("1")){//신규중 제외
	        	strQuery.append(" a.cr_status<>'3' and \n");
	        } else if (etcData.get("Cbo_Option").equals("2")){//신규중
	        	strQuery.append(" a.cr_status='3' and \n");
	        } else if (etcData.get("Cbo_Option").equals("3")){//폐기분 제외
	        	strQuery.append(" a.cr_clsdate is null and a.cr_status<>'9' and \n");
	        } else if (etcData.get("Cbo_Option").equals("4")){//폐기분
	        	strQuery.append(" (a.cr_clsdate is not null or a.cr_status='9') and \n");
	        }

	        strQuery.append(" a.cr_syscd=h.cm_syscd and \n");
	        strQuery.append(" a.cr_syscd=g.cm_syscd and a.cr_dsncd=g.cm_dsncd and \n");
	        strQuery.append(" a.cr_jobcd=d.cm_jobcd and \n");
	        strQuery.append(" a.cr_syscd=j.cm_syscd and \n");
	        strQuery.append(" j.cm_syscd=a.cr_syscd and \n");
	        strQuery.append(" a.cr_rsrccd=j.cm_rsrccd and \n");

	        if (!etcData.get("Chk_Aply").equals("true") && !strRsrcCd.equals("Y") && strRsrc.length>0){
	        	strQuery.append("a.cr_rsrccd in (");
				for (i=0 ; strRsrc.length>i ; i++) {
					if (i>0) strQuery.append(", ? ");
					else strQuery.append("? ");
				}
				strQuery.append(") and                                 \n");
		    }
	        strQuery.append(" e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode and \n");
	        strQuery.append(" i.cm_macode='CMR0020' and a.cr_status=i.cm_micode and \n");
	        strQuery.append(" a.cr_editor=b.cm_userid \n");

	        //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());

            if (!etcData.get("L_SysCd").equals("00000")){
            	pstmt.setString(++CNT, etcData.get("L_SysCd"));
            }
	        if (etcData.get("L_JobCd").equals("0000") && !etcData.get("SecuYn").equals("Y")) {
	        	pstmt.setString(++CNT, etcData.get("UserId"));
		    }else if (!etcData.get("L_JobCd").equals("0000")){
		    	pstmt.setString(++CNT, etcData.get("L_JobCd"));
		    }
	        if (Integer.parseInt(etcData.get("Cbo_Cond10_code")) > 0 && !Cbo_Cond2_code.equals("")) {
				pstmt.setString(++CNT, Cbo_Cond2_code);
	        }

	        if (Integer.parseInt(etcData.get("Cbo_Cond11_code")) > 0 && Txt_Cond != ""){
				switch(Integer.parseInt(etcData.get("Cbo_Cond11_code"))){
	            case 1:
	            	pstmt.setString(++CNT, "%"+Txt_Cond+"%");
	            	break;
	            case 2:
	            	pstmt.setString(++CNT, "%"+Txt_Cond+"%");
	            	break;
	            case 3:
	            	pstmt.setString(++CNT, WkCond2);
	            	break;
	            case 4:
	            	pstmt.setString(++CNT, "%"+Txt_Cond+"%");
	            	break;
				}
	        }
	        if (!etcData.get("Chk_Aply").equals("true") && !strRsrcCd.equals("Y") && strRsrc.length>0){
				for (i=0;strRsrc.length>i;i++) {
            		pstmt.setString(++CNT, strRsrc[i]);
            	}
	        }
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("NO",Integer.toString(rs.getRow()));
				rst.put("rsrccd",rs.getString("rsrccd"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("sta",rs.getString("sta"));
				rst.put("job",rs.getString("job"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_lastdate",rs.getString("cr_lastdate"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_lstver",Integer.toString(rs.getInt("cr_lstver")));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rtList.toArray();
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

}//end of Cmd3100 class statement
