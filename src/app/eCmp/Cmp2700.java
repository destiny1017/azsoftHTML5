/*****************************************************************************************
	1. program ID	: Cmp2700.java
	2. create date	: 2011. 05. 11
	3. auth		    : jm.seo
	4. update date	: 
	5. auth		    : 
	6. description	: 1. User Deploy
*****************************************************************************************/
 
package app.eCmp;

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
import app.common.*;

/**
 * @author bigeyes	
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp2700{
	/**
     * Logger Class Instance Creation
     * logger
     */
	
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */ 
    public Object[] getResult(String UserId,String diffdt,String diffSeq,String svrIp,String SysCd,String diffRst,String portNo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		int               wkCnt       = 0;
		String            NEWDEV      = "";
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		UserInfo         secuinfo     = new UserInfo();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			boolean adminSw = secuinfo.isAdmin_conn(UserId, conn);
			if (adminSw == false) {
				adminSw = secuinfo.getSecuInfo_conn(UserId, "112", conn);
			}
			strQuery.setLength(0);
			strQuery.append("	 select count(*) cnt	                           \n");
			strQuery.append("	   from cmd0027 a,cmd0026 c                        \n");
			strQuery.append("	  where c.cd_diffdt=?  and c.cd_diffseq=?          \n");
			if (!svrIp.equals("ALL")) strQuery.append("and c.cd_svrip=?            \n");			
			if (!SysCd.equals("ALL")) strQuery.append("and a.cd_syscd=?            \n");			
			if (!diffRst.equals("ALL")) strQuery.append("and a.cd_diffrst=?        \n");	
			if (!portNo.equals("ALL")) strQuery.append("and a.cd_portno=?          \n");	
			if (adminSw == false) {
				strQuery.append("and a.cd_syscd in (select a.cm_syscd from cmm0044 a \n");
				strQuery.append("                    where a.cm_userid=?             \n");
				strQuery.append("                      and a.cm_closedt is null      \n");
				strQuery.append("                    group by a.cm_syscd)            \n");
			}
			strQuery.append("	    and c.cd_diffdt=a.cd_diffdt                    \n");
			strQuery.append("	    and c.cd_jobseq=a.cd_jobseq                    \n");

			//strQuery.append("	    and EXE_CHECK(a.cd_rsrcname)='Y'               \n");			
			strQuery.append("	    and a.cd_viewyn='Y'                            \n");			
			
	
			pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			
			pstmt.setString(++parmCnt, diffdt);
			pstmt.setInt(++parmCnt, Integer.parseInt(diffSeq));
			if (!svrIp.equals("ALL")) pstmt.setString(++parmCnt, svrIp);			
			if (!SysCd.equals("ALL")) pstmt.setString(++parmCnt, SysCd);	
			if (!diffRst.equals("ALL")) pstmt.setString(++parmCnt, diffRst);
			if (!portNo.equals("ALL")) pstmt.setString(++parmCnt, portNo);
			if (adminSw == false) {
				pstmt.setString(++parmCnt, UserId);
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
	        
	        if(rs.next()){
	        	wkCnt = rs.getInt("cnt");
			}//end of while-loop statement
	        
			rs.close();
			pstmt.close();	
			if (wkCnt > 65000){
				rst = new HashMap<String, String>();
				rst.put("ERROR","데이타가 65,000건이 초과하여 조회하실수 없습니다. 대사기록상세 오른쪽마우스클릭하여 대사내용합계표에서 시스템별로 조회하여 주십시요.");
				rsval.add(rst);
				rst = null;

				rs.close();
				pstmt.close();
				conn.close();
				conn = null;
				pstmt = null;
				rs = null;

				return rsval.toArray();
			}
			
			parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("	 select b.cm_syscd,b.cm_sysmsg,a.cd_dirpath,a.cd_syscd,a.cd_dsncd,a.cd_rsrcname,c.cd_svrname,c.cd_svrip,	\n");
			strQuery.append("	        e.cm_micode,e.cm_codename,a.cd_ecamssize,a.cd_ecamsdate,a.cd_ecamsmd5,     	\n");
			strQuery.append("	        a.cd_svrsize,a.cd_svrdate,a.cd_svrmd5,a.cd_rsrccd,a.cd_portno,f.cm_username	\n");
			strQuery.append("	   from cmm0040 f,cmm0020 e,cmm0030 b,cmd0027 a,cmd0026 c                          	\n");
			strQuery.append("	  where c.cd_diffdt=?  and c.cd_diffseq=?                 		 	                \n");
			if (!svrIp.equals("ALL")) strQuery.append("and c.cd_svrip=?                                             \n");			
			if (!SysCd.equals("ALL")) strQuery.append("and a.cd_syscd=?                                             \n");			
			if (!diffRst.equals("ALL")) strQuery.append("and a.cd_diffrst=?                                         \n");
			if (!portNo.equals("ALL")) strQuery.append("and a.cd_portno=?                                           \n");	
			strQuery.append("	    and c.cd_diffdt=a.cd_diffdt and c.cd_jobseq=a.cd_jobseq                        	\n");
			strQuery.append("	    and a.cd_syscd=b.cm_syscd                                                   	\n");
			strQuery.append("	    and e.cm_macode='DIFFRST'                                                   	\n");
			strQuery.append("	    and e.cm_micode=a.cd_diffrst				                                 	\n");
			//strQuery.append("	    and EXE_CHECK(a.cd_rsrcname)='Y'               \n");
			strQuery.append("	    and a.cd_viewyn='Y'                            \n");
			strQuery.append("	    and a.cd_lstusr=f.cm_userid(+)                 \n");
			if (SysCd.equals("ALL") || SysCd.equals("999")){			
				strQuery.append("	  union                                                                         	\n");
				strQuery.append("	 select nvl(a.cd_syscd,'999') cd_syscd ,'전 업무 확인필요',a.cd_dirpath,a.cd_syscd,a.cd_dsncd,a.cd_rsrcname,c.cd_svrname,c.cd_svrip,	\n");
				strQuery.append("	        e.cm_micode,e.cm_codename,a.cd_ecamssize,a.cd_ecamsdate,a.cd_ecamsmd5,     	\n");
				strQuery.append("	        a.cd_svrsize,a.cd_svrdate,a.cd_svrmd5,a.cd_rsrccd,a.cd_portno,f.cm_username	\n");
				strQuery.append("	   from cmm0040 f,cmm0020 e,                                                       	\n");
				strQuery.append("	        cmd0027 a,cmd0026 c                                                        	\n");
				strQuery.append("	  where c.cd_diffdt=? and c.cd_diffseq=?                 		              	    \n");
				if (!svrIp.equals("ALL")) strQuery.append("and c.cd_svrip=?                                             \n");			
				if (!portNo.equals("ALL")) strQuery.append("and c.cd_portno=?                                           \n");	
				strQuery.append("	    and c.cd_diffdt=a.cd_diffdt and c.cd_jobseq=a.cd_jobseq                        	\n");
				strQuery.append("	    and a.cd_dsncd='99999'                                                       	\n");
				strQuery.append("	    and e.cm_macode='DIFFRST'                                                    	\n");
				strQuery.append("	    and e.cm_micode=a.cd_diffrst				                                 	\n");
			}			
			strQuery.append("	    and a.cd_viewyn='Y'                            \n");
			strQuery.append("	    and a.cd_lstusr=f.cm_userid(+)                 \n");
			//strQuery.append("	    and EXE_CHECK(a.cd_rsrcname)='Y'               \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			
			pstmt.setString(++parmCnt, diffdt);
			pstmt.setInt(++parmCnt, Integer.parseInt(diffSeq));
			if (!svrIp.equals("ALL")) pstmt.setString(++parmCnt, svrIp);
			if (!SysCd.equals("ALL")) pstmt.setString(++parmCnt, SysCd);
			if (!diffRst.equals("ALL")) pstmt.setString(++parmCnt, diffRst);
			if (!portNo.equals("ALL")) pstmt.setString(++parmCnt, portNo);
			if (SysCd.equals("ALL") || SysCd.equals("999")){			
				pstmt.setString(++parmCnt, diffdt);
				pstmt.setInt(++parmCnt, Integer.parseInt(diffSeq));
				if (!svrIp.equals("ALL")) pstmt.setString(++parmCnt, svrIp);
				if (!portNo.equals("ALL")) pstmt.setString(++parmCnt, portNo);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery(); 
	        
	        while (rs.next()){
				rst = new HashMap<String, String>();
				
				strQuery.setLength(0);
				strQuery.append("select decode(substr(a.cm_info,10,1),1,'Y','N') binyn  from cmm0036 a  \n");   
				strQuery.append(" where a.cm_syscd=?                                  \n");
				strQuery.append("   and a.cm_rsrccd=?                                 \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());	
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, rs.getString("cd_syscd"));
				pstmt2.setString(2, rs.getString("cd_rsrccd"));				
		        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());           
		        rs2 = pstmt2.executeQuery();
		        if(rs2.next()){
					rst.put("binyn",rs2.getString("binyn"));
		        }
		        rs2.close(); 
				pstmt2.close();				
				
				//rst.put("acptno",rs.getString("acptno"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cd_dirpath",rs.getString("cd_dirpath"));
				rst.put("cd_syscd",rs.getString("cd_syscd"));
				rst.put("cd_dsncd",rs.getString("cd_dsncd"));
				rst.put("cd_rsrcname",rs.getString("cd_rsrcname"));
				rst.put("cd_portno",rs.getString("cd_portno"));
				rst.put("cm_svrname",rs.getString("cd_svrname"));
				rst.put("cm_svrip",rs.getString("cd_svrip"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("svrname",rs.getString("cd_svrname") + "(" + rs.getString("cd_svrip") + ")");
				rst.put("cm_micode",rs.getString("cm_micode"));
                if(NEWDEV.equals("Y")){
    				rst.put("cm_codename","시스템구축중");                	
                }else{
    				rst.put("cm_codename",rs.getString("cm_codename"));                	
                }

				rst.put("cd_ecamssize",rs.getString("cd_ecamssize"));
				
				if (rs.getString("cd_ecamsdate") != null){ 
					if (rs.getString("cd_ecamsdate").length()>=14 && rs.getString("cd_ecamsdate").indexOf("-") < 1 ) {
						rst.put("cd_ecamsdate",rs.getString("cd_ecamsdate").substring(0, 4) + "/" + 
						rs.getString("cd_ecamsdate").substring(4, 6) + "/" +
						rs.getString("cd_ecamsdate").substring(6, 8) + " " +
						rs.getString("cd_ecamsdate").substring(8, 10) + ":" +
						rs.getString("cd_ecamsdate").substring(10, 12) + ":" +
				        rs.getString("cd_ecamsdate").substring(12, 14));
					} else {
						rst.put("cd_ecamsdate",rs.getString("cd_ecamsdate"));
					}
				}
				rst.put("cd_ecamsmd5",rs.getString("cd_ecamsmd5"));
				rst.put("cd_svrsize",rs.getString("cd_svrsize"));
				if (rs.getString("cd_svrdate") != null){ 
					if (rs.getString("cd_svrdate").length()>=14) {
						rst.put("cd_svrdate",rs.getString("cd_svrdate").substring(0, 4) + "/" + 
						rs.getString("cd_svrdate").substring(4, 6) + "/" +
						rs.getString("cd_svrdate").substring(6, 8) + " " +
						rs.getString("cd_svrdate").substring(8, 10) + ":" +
						rs.getString("cd_svrdate").substring(10, 12) + ":" +
				        rs.getString("cd_svrdate").substring(12, 14));
					} else {
						rst.put("cd_svrdate",rs.getString("cd_svrdate"));
					}
				}
				rst.put("cd_svrmd5",rs.getString("cd_svrmd5"));
				rst.put("cm_micode",rs.getString("cm_micode"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
	        
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			//ecamsLogger.error(rsval.toString());
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getResult() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp2700.getResult() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getResult() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp2700.getResult() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2700.getResult() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
    }
    
    
    public Object[] getDaesa(String datStD,String datEdD) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("	 select a.cd_diffdt,a.cd_diffseq,FILEDIFF_SVRIP(a.cd_diffdt,a.cd_diffseq) SVRIP, \n");
			strQuery.append("	        a.cd_syscd,c.cm_sysmsg, \n");
			strQuery.append("	        to_char(min(A.cd_stdate),'yyyy/mm/dd hh24:mi:ss') as stdate,	\n");
			strQuery.append("	        to_char(max(A.cd_eddate),'yyyy/mm/dd hh24:mi:ss') as eddate		\n");
			strQuery.append("	   from (select cd_diffdt,cd_diffseq from cmd0026         				\n");
			strQuery.append("	          where cd_diffdt>=?           			          				\n");
			strQuery.append("	            and cd_diffdt<=?             		     				    \n");
			strQuery.append("	            and cd_eddate is not null	                 				\n");
			strQuery.append("	          group by cd_diffdt,cd_diffseq) b,               				\n");
			strQuery.append("	        cmd0026 a, cmm0030 c                                         				\n");
			strQuery.append("	  where b.cd_diffdt=a.cd_diffdt                           				\n");
			strQuery.append("	    and b.cd_diffseq=a.cd_diffseq                         				\n");
			strQuery.append("	    and a.cd_syscd=c.cm_syscd(+)                         				\n");
			strQuery.append("	  GROUP BY a.cd_diffdt,a.cd_diffseq,a.cd_syscd,c.cm_sysmsg                       				\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			 
			pstmt.setString(++parmCnt, datStD);
			pstmt.setString(++parmCnt, datEdD);
			
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        
	        while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cd_diffdt",rs.getString("cd_diffdt"));
				rst.put("diffdt",rs.getString("cd_diffdt").substring(0,4)+"/"+rs.getString("cd_diffdt").substring(4,6)+"/"+
						rs.getString("cd_diffdt").substring(6,8));
				rst.put("cd_jobseq",rs.getString("cd_jobseq"));
				if (rs.getString("SVRIP") != null) {
					if (rs.getString("SVRIP").equals("ALL")) {
						rst.put("CD_SVRIP", "ALL");
						rst.put("SVRIP","전체서버");
						rst.put("CD_SVRNAME","ALL");
					} else {
						rst.put("CD_SVRIP", rs.getString("SVRIP").substring(0,rs.getString("SVRIP").indexOf(",")));
						rst.put("SVRIP",rs.getString("SVRIP").substring(0,rs.getString("SVRIP").indexOf(",")));
						rst.put("CD_SVRNAME",rs.getString("SVRIP").substring(rs.getString("SVRIP").indexOf(",")+1));
					}
				}
				rst.put("cd_syscd",rs.getString("cd_syscd"));		
				if(rs.getString("cm_sysmsg") == null){
					rst.put("cm_sysmsg","ALL");
				}else{
					rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				}
				rst.put("stdate",rs.getString("stdate"));
				rst.put("eddate",rs.getString("eddate"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			//ecamsLogger.debug(rsval.toString());		
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp2700.getDaesa() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp2700.getDaesa() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2700.getDaesa() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    
    public Object[] getDaesa2(String UserId,String datStD,String datEdD) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		UserInfo         secuinfo     = new UserInfo();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			boolean adminSw = secuinfo.isAdmin_conn(UserId, conn);
			
			if (adminSw == false) {
				adminSw = secuinfo.getSecuInfo_conn(UserId, "112", conn);
			}
			strQuery.setLength(0);
			strQuery.append("	 SELECT A.CD_DIFFDT,A.CD_DIFFSEQ,A.CD_SVRIP,A.CD_SVRNAME,A.CD_PORTNO,\n");
			strQuery.append("	        to_char(min(A.cd_stdate),'yyyy/mm/dd hh24:mi:ss') as stdate, \n");
			strQuery.append("	        to_char(max(A.cd_eddate),'yyyy/mm/dd hh24:mi:ss') as eddate, \n");
			strQuery.append("	        b.cm_codename,nvl(a.cd_diffrst,'00') cd_diffrst,             \n");
			strQuery.append("	        (select count(*) from cmd0027                                \n");
			strQuery.append("	          where cd_diffdt=a.cd_diffdt and cd_svrip=a.cd_svrip        \n");
			strQuery.append("	            and cd_diffseq=a.cd_diffseq                              \n");
			strQuery.append("	            and cd_portno=a.cd_portno) diffcnt                       \n");
			strQuery.append("	   FROM CMD0026 A,cmm0020 b                               			 \n");
			strQuery.append("	  WHERE A.CD_DIFFDT>=?			                          			 \n");
			strQuery.append("	    AND A.CD_DIFFDT<=?          		                 			 \n");
			strQuery.append("	    AND A.CD_EDDATE is not null   		                 			 \n");
			strQuery.append("	    AND b.cm_macode='DIFFRST' and b.cm_micode=nvl(a.cd_diffrst,'00') \n");
			if (!adminSw) {
				strQuery.append("and a.cd_svrip in (select a.cm_svrip from cmm0031 a,cmm0044 b       \n");
				strQuery.append("                    where b.cm_userid=? and b.cm_closedt is null    \n");
				strQuery.append("                      and b.cm_syscd=a.cm_syscd                     \n");
				strQuery.append("                      and a.cm_svrcd='05'                           \n");
				strQuery.append("                      and a.cm_closedt is null                      \n");
				strQuery.append("                    group by a.cm_svrip)                            \n");
			}
			strQuery.append("	  group BY a.CD_DIFFDT,a.cd_diffseq,a.cd_svrip,a.cd_svrname,a.cd_portno,b.cm_codename,nvl(a.cd_diffrst,'00')   \n");
			strQuery.append("	  order BY a.CD_DIFFDT,a.cd_svrip              	   		            \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			
			pstmt.setString(++parmCnt, datStD);
			pstmt.setString(++parmCnt, datEdD);
			if (adminSw == false) {
				pstmt.setString(++parmCnt, UserId);
			}
			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();

	        while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("diffdt",rs.getString("cd_diffdt").substring(0,4)+"/"+rs.getString("cd_diffdt").substring(4,6)+"/"+
						rs.getString("cd_diffdt").substring(6,8));				
				rst.put("cd_diffdt",rs.getString("cd_diffdt"));
				rst.put("cd_diffseq",rs.getString("cd_diffseq"));
				rst.put("cd_svrip",rs.getString("CD_SVRIP"));
				rst.put("cd_portno",rs.getString("CD_PORTNO"));
				rst.put("SVRIP",rs.getString("CD_SVRNAME"));
				rst.put("stdate",rs.getString("stdate"));
				rst.put("eddate",rs.getString("eddate"));
				rst.put("diffrst",rs.getString("cm_codename"));
				rst.put("cd_syscd","ALL");				
				rst.put("cd_diffrst", rs.getString("cd_diffrst"));
				if (rs.getString("cd_diffrst").equals("00")) {
					rst.put("diffcnt", rs.getString("diffcnt"));
				}
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			//ecamsLogger.debug(rsval.toString());		
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp2700.getDaesa2() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa2() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp2700.getDaesa2() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2700.getDaesa2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    public Object[] DaesaResult(String UserId,String diffdt,String diffseq,String svrip,String detail,String portNo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		UserInfo         secuinfo     = new UserInfo();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			boolean adminSw = secuinfo.isAdmin_conn(UserId, conn);
			if (adminSw == false) {
				adminSw = secuinfo.getSecuInfo_conn(UserId, "112", conn);
			}
			
			strQuery.setLength(0);
			strQuery.append(" select nvl(c.cm_syscd,'99999') cm_syscd,NVL(c.cm_sysmsg,'전 업무 확인필요') cm_sysmsg, \n");  
			strQuery.append("        a.cd_svrip,a.cd_svrname,a.cd_portno,               \n");  
			strQuery.append("        a.cd_diffrst,b.cm_codename,count(*) ResultCnt      \n");
			strQuery.append("   from cmm0030 c,cmm0020 b,cmd0027 a,cmd0026 d            \n");
			strQuery.append("  where d.cd_diffdt=?                                      \n");
			strQuery.append("    and d.cd_diffseq=?                                     \n");
			if(detail.equals("Y")){
				strQuery.append("    and d.cd_svrip=?                                   \n");
				strQuery.append("    and d.cd_portno=?                                  \n");
			}
			if (adminSw == false) {
				strQuery.append("and (    nvl(a.cd_syscd, '99999') = '99999'                \n");
				strQuery.append("     or  a.cd_syscd in (select a.cm_syscd from cmm0044 a   \n");
				strQuery.append("                    where a.cm_userid=?                	\n");
				strQuery.append("                      and a.cm_closedt is null         	\n");
				strQuery.append("                    group by a.cm_syscd)               	\n");
				strQuery.append("    )                                                   	\n");
			}
			strQuery.append("	    and a.cd_viewyn='Y'                            \n");
			//strQuery.append("	    and EXE_CHECK(a.cd_rsrcname)='Y'               \n");			

			strQuery.append("    and d.cd_diffdt=a.cd_diffdt                            \n");
			strQuery.append("    and d.cd_jobseq=a.cd_jobseq                            \n");
			strQuery.append("    and b.cm_macode='DIFFRST'                              \n");
			strQuery.append("    and b.cm_micode=a.cd_diffrst                           \n");
			strQuery.append("    and a.cd_syscd=c.cm_syscd(+)                           \n");
			strQuery.append("  group by c.cm_syscd,c.cm_sysmsg,a.cd_svrip,a.cd_svrname, \n");
			strQuery.append("       a.cd_portno,a.cd_diffrst,b.cm_codename              \n");
			//strQuery.append("  order by cm_syscd,c.cm_sysmsg,a.cd_svrip                          \n");
			strQuery.append("	    order by cm_syscd  desc             \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			
			pstmt.setString(++parmCnt, diffdt);
			pstmt.setString(++parmCnt, diffseq);
			if(detail.equals("Y")){
				pstmt.setString(++parmCnt, svrip);
				pstmt.setString(++parmCnt, portNo);
			} 
			if (adminSw == false) pstmt.setString(++parmCnt, UserId);

			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();

	        while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cd_svrip",rs.getString("cd_svrip"));
				rst.put("cd_portno",rs.getString("cd_portno"));
				rst.put("cd_svrname",rs.getString("cd_svrname"));
				rst.put("cd_diffrst",rs.getString("cd_diffrst"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("ResultCnt",rs.getString("ResultCnt"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			//ecamsLogger.debug(rsval.toString());		
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp2700.getDaesa2() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2700.getDaesa2() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp2700.getDaesa2() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2700.getDaesa2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
 }