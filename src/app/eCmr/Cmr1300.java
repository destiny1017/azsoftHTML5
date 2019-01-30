/*****************************************************************************************
	1. program ID	: eCmr1300.java
	2. create date	: 2008.08. 29
	3. auth		    : NO Name
	4. update date	: 
	5. auth		    : 
	6. description	: Cmr1300
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
import app.common.*;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr1300{		
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	/**
	 * @param  Cmr1300
	 * @return Cmr1300
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public Object[] Sql_Qry_All(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval 	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			 	rst		= null;
		
		Boolean			  	Sv_Admin    = false;
		String 				UserId		= null;
		String				ReqCd		= null;
		String				StepCd		= null;
		String				StartDate	= null;
		String				EndDate		= null;
		UserInfo 			userinfo 	= new UserInfo();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			UserId		= dataObj.get("UserId");
			ReqCd		= dataObj.get("ReqCd");
			StepCd		= dataObj.get("StepCd");
			StartDate 	= dataObj.get("StartDate");
            EndDate 	= dataObj.get("EndDate");            
			Sv_Admin 	= userinfo.isAdmin(UserId);
			userinfo = null;
			
            strQuery.setLength(0);
			strQuery.append("select a.cr_prjno,a.cr_prjname,a.cr_acptno,a.cr_status, ");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy-mm-dd hh24:mi') cr_acptdate, ");
			strQuery.append("a.cr_qrycd qry,a.cr_prcdate,a.cr_editor,a.cr_emgcd,a.cr_docno, ");
			strQuery.append("b.cm_codename reqcd,c.cm_codename stepcd ");
			strQuery.append("from cmr1000 a,cmm0020 b,cmm0020 c  ");
			strQuery.append("where (a.cr_qrycd like '3%' or a.cr_qrycd like '4%') ");
			if (!Sv_Admin){
				strQuery.append("and a.cr_editor= ?  \n");  //UserId
			}
			if (!ReqCd.equals("00")){
				strQuery.append("and a.cr_qrycd= ?  \n");  //ReqCd			
			}
			if (StepCd.equals("00")){  //전체
				strQuery.append("and ((a.cr_prcdate is not null  \n");
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>=?  \n");  //StartDate
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<=?) or a.cr_status='0')  \n");  //EndDate
			} 
			else if (StepCd.equals("1")){  //미완료
				strQuery.append("and a.cr_status='0'   \n");
			}
			else if (StepCd.equals("9")){  //완료
				strQuery.append("and a.cr_prcdate is not null  \n");
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>=?  \n");
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<=?  \n");
			}
			strQuery.append("and c.cm_macode = 'CMR1000' and a.cr_status = c.cm_micode  \n");
			strQuery.append("and b.cm_macode = 'REQUEST' and a.cr_qrycd = b.cm_micode   \n");
			strQuery.append("order by a.cr_acptdate desc  \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			int Cnt = 0;
			if (!Sv_Admin){
				pstmt.setString(++Cnt, UserId);
			}
			
			if (!ReqCd.equals("00")){
				pstmt.setString(++Cnt, ReqCd);
			}
			
            if (!StepCd.equals("1")) {
	            pstmt.setString(++Cnt, StartDate);   //yyyymmdd
	            pstmt.setString(++Cnt, EndDate);  //yyyymmdd
            }
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            
            while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("NO",Integer.toString(rs.getRow()));
				rst.put("cr_prjno", rs.getString("cr_prjno"));
				rst.put("prjnoname", "[" + rs.getString("cr_prjno") + "] " + rs.getString("cr_prjname"));//플젝
				rst.put("cr_acptdate",rs.getString("cr_acptdate"));//요청일
				rst.put("reqcd", rs.getString("reqcd"));//요청구분 네임
				String cr_acptno = rs.getString("cr_acptno");
				rst.put("cr_acptno", cr_acptno);   //요청번호
				rst.put("acptno", cr_acptno.substring(0,4)+"-"+
						cr_acptno.substring(4,6)+"-"+
						cr_acptno.substring(6,12));
				rst.put("stepcd", rs.getString("stepcd"));//진행상태 네임
				rst.put("cr_editor", rs.getString("cr_editor"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("qry", rs.getString("qry"));
				
			    if (rs.getString("cr_status").equals("0")){
			    	if (rs.getString("cr_editor").equals(UserId)){
			    		strQuery.setLength(0);
			    		strQuery.append("SELECT COUNT(*) AS CNT FROM CMR9900 \n");
			    		strQuery.append("WHERE CR_ACPTNO=? \n");//cr_acptno
			    		strQuery.append("  AND CR_STATUS<>'0' \n");
			    		strQuery.append("  AND CR_TEAM<>'SYSDUP' \n");
			            pstmt2 = conn.prepareStatement(strQuery.toString());
			           	pstmt2.setString(1, cr_acptno);
			            rs2 = pstmt2.executeQuery();
		                if (rs2.next()){
		                	if (rs2.getInt("CNT") == 0){		                		
		                		if (rs.getString("qry").equals("34")){
		                			rst.put("Mnu_Rinf0", "체크인취소");
		                		}
		                        else{
		                        	rst.put("Mnu_Rinf0", "체크아웃취소");
		                        }
		                   }else{
		                	   rst.put("Mnu_Rinf0", "");
		                   }
		                }
		                rs2.close();
		                pstmt2.close();
		                
			    	}
			    }
			    
				//관련반출요청번호
				strQuery.setLength(0);
				strQuery.append("select cr_number from cmr1002 where cr_acptno=? \n");//acptno
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	           	pstmt2.setString(1, cr_acptno);
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()) {
	            	if (rs2.getString("cr_number") != null & rs2.getString("cr_number") != ""){
	            		rst.put("cr_number", rs2.getString("cr_number").substring(0,4)+"-"+
	            			             	rs2.getString("cr_number").substring(4,6)+"-"+
	            			             	rs2.getString("cr_number").substring(6,12));
	            	}else rst.put("cr_number", "");
	            }
	            else rst.put("cr_number", "");
	            rs2.close();
	            pstmt2.close();
	            
	            
				//변경근거
				if (rs.getString("cr_emgcd") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename as emdcdname from cmm0020 	\n");
					strQuery.append("where cm_macode='REQGBN' and cm_micode= ?  \n");  //CR_EMGCD
		            pstmt2 = conn.prepareStatement(strQuery.toString());
		           	pstmt2.setString(1, rs.getString("cr_emgcd"));
		            rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						if (rs.getString("cr_docno") != null){//CR_DOCNO = 변경ID일련번호
							rst.put("emdcdname","["+rs2.getString("emdcdname")+"] " + rs.getString("cr_docno"));
						}
						else{
							rst.put("emdcdname","["+rs2.getString("emdcdname")+"]");
						}
					}
					else{
						rst.put("emdcdname", "");
					}
					rs2.close();
					pstmt2.close();
					
				}
				
		        //요청내용
				strQuery.setLength(0);
				strQuery.append("select b.cr_docfile from cmr1100 a,cmr0030 b 	\n");
				strQuery.append("where a.cr_acptno= ?    						\n");  //CR_ACPTNO
				strQuery.append("  and a.cr_docid=b.cr_docid   					\n");
				strQuery.append("order by a.cr_seRno  							\n");
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	           	pstmt2.setString(1, cr_acptno);
	            rs2 = pstmt2.executeQuery();
	            String	Docfile	= "";
	            Cnt = 0;
	            while (rs2.next()){
	            	if (rs2.getRow() == 1)
	            		Docfile = rs2.getString("cr_docfile");
	            	Cnt++;
	            }
				if (Cnt > 1){
					rst.put("Docfile", Docfile + "외 " + (Cnt-1) + "건");
				}
				else{
					rst.put("Docfile", Docfile);
				}
				rs2.close();
				pstmt2.close();
				
				
				//관련번호 존재 유무
				strQuery.setLength(0);
				strQuery.append("select count(*) as CNT from cmr1100  \n");
				strQuery.append("where cr_acptno= ?  				   \n");   //DbSet!CR_ACPTNO
				strQuery.append("  and cr_confno is null  			   \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, cr_acptno);
				rs2 = pstmt2.executeQuery();				
				if (rs2.next()){
					if (rs2.getInt("CNT") > 0){
						rst.put("Relationacpt", "1");
					}
					else{
						rst.put("Relationacpt", "0");
					}
				}
				
				rs2.close();
				pstmt2.close();
				
				
				//처리결과
				strQuery.setLength(0);
				strQuery.append("select count(*) as CNT from cmr1100  \n");
				strQuery.append("where cr_acptno= ?  				  \n");   //DbSet!CR_ACPTNO
				if (cr_acptno.substring(4,6).equals("46"))
					strQuery.append("  and cr_putcode is not null     \n");
				else
					strQuery.append("  and cr_errcd is not null       \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, cr_acptno);
				rs2 = pstmt2.executeQuery();				
				if (rs2.next()){
					if (rs2.getInt("CNT") > 0) {
						rst.put("Returnvalue", "1");
					}
					else{
						rst.put("Returnvalue", "0");
					}
				}
				
				rs2.close();
				pstmt2.close();
				
				
				if (rs.getString("cr_prcdate") == null){
					strQuery.setLength(0);
					//strQuery.append("select count(*) as cnt from cmr1100 where cr_acptno =? \n");
					//strQuery.append("and cr_putcode is not null and cr_errcd='WAIT' \n");
					strQuery.append("select count(*) as cnt from cmr1100 a,cmr9900 b \n");
					strQuery.append("where a.cr_acptno =? \n");
					strQuery.append("and a.cr_acptno=b.cr_acptno \n");
					if (cr_acptno.substring(4,6).equals("46"))
						strQuery.append("and nvl(a.cr_putcode,'0000')<>'0000' \n"); 
					else
						strQuery.append("and (a.cr_errcd is null or a.cr_errcd <>'0000') \n"); 
					strQuery.append("and (b.cr_confdate is not null or b.cr_locat ='00') \n");
					if (rs.getString("qry").equals("31")){
						strQuery.append("and b.cr_team='SYSDDN' \n");
					}else if (rs.getString("qry").equals("39")){
						strQuery.append("and b.cr_team='SYSDNC' \n");
					}else{
						strQuery.append("and b.cr_team='SYSDUP' \n");
					}
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, cr_acptno);
					rs2 = pstmt2.executeQuery();				
					if (rs2.next()){
						if (rs2.getInt("cnt") > 0)
							rst.put("colorsw", "5");
						else rst.put("colorsw", "0");
					}
					rs2.close();
					pstmt2.close();
					
				}else{
			        if (rs.getString("cr_status").equals("3")){
			        	rst.put("colorsw", "3");
			        }
			        else {
			        	rst.put("colorsw", "9");
			        }
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
    		return rsval.toArray();
    		
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr1300.Sql_Qry_All() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr1300.Sql_Qry_All() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1300.Sql_Qry_All() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr1300.Sql_Qry_All() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1300.Sql_Qry_All() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_All() method statement
	
	public Object[] Sql_Qry_Detail(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval 	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			 	rst		= null;		
		
		Boolean			  	Sv_Admin    = false;
		String 				UserId		= null;
		String				ReqCd		= null;
		String				StepCd		= null;
		String				StartDate	= null;
		String				EndDate		= null;
		String				Txt_Jawon	= null;
		UserInfo 			userinfo 	= new UserInfo();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			UserId		= dataObj.get("UserId");
			ReqCd		= dataObj.get("ReqCd");
			StepCd		= dataObj.get("StepCd");
			StartDate 	= dataObj.get("StartDate");
            EndDate 	= dataObj.get("EndDate");
            Txt_Jawon	= dataObj.get("Txt_Jawon");			
			Sv_Admin = userinfo.isAdmin(UserId);
			userinfo = null;
			
            strQuery.setLength(0);
            strQuery.append("select a.cr_acptno,a.cr_editor,a.cr_qrycd qry,a.cr_prjno, ");
			strQuery.append(" to_char(a.cr_acptdate,'yyyy-mm-dd hh24:mi') cr_acptdate, ");
            strQuery.append("a.cr_emgcd,a.cr_docno,b.cm_codename reqcdname,d.cm_codename stepcd,f.cr_serno, ");
            strQuery.append("f.cr_qrycd,f.cr_docid,f.cr_confno,f.cr_baseno,f.cr_prcdate, ");
            strQuery.append("f.cr_status,f.cr_errcd,g.cr_docfile,h.cd_prjname ");
            if (Sv_Admin){
	            strQuery.append("from cmd0300 h,cmr0030 g,cmr1100 f,cmm0020 d,cmm0020 b, cmr1000 a ");
	            strQuery.append("where a.cr_qrycd like '3%' ");
            }else{
	            strQuery.append("from cmd0304 i,cmd0300 h,cmr0030 g,cmr1100 f,cmm0020 d,cmm0020 b, cmr1000 a ");
	            strQuery.append("where a.cr_qrycd like '3%' ");
	            strQuery.append("  and a.cr_editor=? ");//UserId
	            strQuery.append("  and h.cd_prjno=i.cd_prjno and i.CD_PRJUSER=? ");//UserId
            }
            if (!ReqCd.equals("00")){
            	strQuery.append("and a.cr_qrycd=? ");//ReqCd
            }
            
            if (StepCd.equals("00")){  //전체
	            strQuery.append("and ((a.cr_prcdate is not null ");
	            strQuery.append("     and to_char(a.cr_acptdate,'yyyymmdd')>=? "); //StartDate
	            strQuery.append("     and to_char(a.cr_acptdate,'yyyymmdd')<=?) or a.cr_status='0') ");//EndDate
            } 
            else if (StepCd.equals("1")){  //미완료
            	strQuery.append("and a.cr_status='0' ");
            } 
            else if (StepCd.equals("9")){  //완료
            	strQuery.append("and a.cr_prcdate is not null ");
            	strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>=? ");//StartDate
            	strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<=? ");//EndDate
            }
            
            strQuery.append("and a.cr_acptno=f.cr_acptno ");
            if (Txt_Jawon != ""){
            	strQuery.append("and (g.cr_docfile like ?) ");//%Txt_Jawon%
            }
            strQuery.append("and f.cr_docid=g.cr_docid ");
            strQuery.append("and g.CR_PRJNO = h.CD_PRJNO ");
            strQuery.append("and d.cm_macode = 'CMR1000' and a.cr_status = d.cm_micode ");
            strQuery.append("and b.cm_macode = 'REQUEST' and a.cr_qrycd = b.cm_micode ");
			pstmt = conn.prepareStatement(strQuery.toString());
			
			int Cnt = 0;
			if (!Sv_Admin) {
				pstmt.setString(++Cnt, UserId);
				pstmt.setString(++Cnt, UserId);
			}
			if (!ReqCd.equals("00")){
				pstmt.setString(++Cnt, ReqCd);
			}
            if (!StepCd.equals("1")) {
	            pstmt.setString(++Cnt, StartDate);   //yyyymmdd
	            pstmt.setString(++Cnt, EndDate);  //yyyymmdd
            }
            if (Txt_Jawon != ""){
            	pstmt.setString(++Cnt,"%"+Txt_Jawon+"%");  //%Txt_Jawon%
            }
                        
            rs = pstmt.executeQuery();
            
            while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("NO",Integer.toString(rs.getRow()));
				if (rs.getString("cr_prjno") != null){
					rst.put("cr_prjno", rs.getString("cr_prjno"));
					rst.put("prjnoname", "[" + rs.getString("cr_prjno") + "] " + rs.getString("cd_prjname"));//플젝
            	}else{
            		rst.put("cr_prjno", "");
            		rst.put("prjnoname", "사용안함");
            	}
				rst.put("docname", rs.getString("cr_docfile"));//산출물명
				rst.put("cr_acptdate",rs.getString("cr_acptdate"));//요청일
			    if (!rs.getString("qry").equals("31")){
			    	strQuery.setLength(0);
			    	strQuery.append("select cm_codename from cmm0020 \n");
			    	strQuery.append("where cm_macode='CHECKIN' and cm_micode=? \n");//rs.getString("cr_qrycd")
		            pstmt2 = conn.prepareStatement(strQuery.toString());
		           	pstmt2.setString(1, rs.getString("cr_qrycd"));
		            rs2 = pstmt2.executeQuery();
	                if (rs2.next()){
	                	rst.put("reqcd", rs.getString("reqcdname") + "(" + rs2.getString("cm_codename") + ")");
	                }
	                rs2.close();
	                pstmt2.close();
		            
			    }
			    else{
			    	rst.put("reqcd", rs.getString("reqcdname"));//요청구분 네임			    
			    }
				String cr_acptno = rs.getString("cr_acptno");
				rst.put("cr_acptno", cr_acptno);   //요청번호
				rst.put("acptno", cr_acptno.substring(0,4)+"-"+cr_acptno.substring(4,6)+"-"+
						cr_acptno.substring(6,12));
				
				//관련반출요청번호
				strQuery.setLength(0);
				strQuery.append("select cr_number from cmr1002 where cr_acptno=? \n");//acptno
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	           	pstmt2.setString(1, cr_acptno);
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()){
	            	if (rs2.getString("cr_number") != null && rs2.getString("cr_number") != ""){
		            	rst.put("cr_number", rs2.getString("cr_number").substring(0,4)+"-"+
		            			             rs2.getString("cr_number").substring(4,6)+"-"+
		            			             rs2.getString("cr_number").substring(6,12));
	            	}
	            	else{
	            		rst.put("cr_number", "");
	            	}
	            }
	            else{
	            	rst.put("cr_number", "");
	            }
	            rs2.close();
	            pstmt2.close();
	            
	            
				//변경근거
				if (rs.getString("cr_emgcd") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename as emdcdname from cmm0020 	\n");
					strQuery.append("where cm_macode='REQGBN' and cm_micode= ?  \n");  //CR_EMGCD
		            pstmt2 = conn.prepareStatement(strQuery.toString());
		           	pstmt2.setString(1, rs.getString("cr_emgcd"));
		            rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						if (rs.getString("cr_docno") != null){//CR_DOCNO = 변경ID일련번호
							rst.put("emdcdname","["+rs2.getString("emdcdname")+"] " + rs.getString("cr_docno"));
						}
						else{
							rst.put("emdcdname","["+rs2.getString("emdcdname")+"]");
						}
					}
					else{
						rst.put("emdcdname", "");
					}
					rs2.close();
					pstmt2.close();
				}
				
				//진행상태 네임
				rst.put("stepcd", rs.getString("stepcd"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("cr_qrycd", rs.getString("cr_qrycd"));
				rst.put("qry", rs.getString("qry"));
				if (rs.getString("cr_baseno")!=null){
					rst.put("cr_baseno", rs.getString("cr_baseno"));
				}
				else{
					rst.put("cr_baseno", "");
				}
				rst.put("cr_editor", rs.getString("cr_editor"));				
				rst.put("cr_docid", rs.getString("cr_docid"));
				
				if (rs.getString("cr_prcdate") == null){
			        if (rs.getString("cr_errcd")!= null){
			        	if (rs.getString("cr_errcd") == null){
			        		rst.put("colorsw", "5");
			        	}
			        	else{
			        		rst.put("colorsw", "0");
			        	}
			        }
			        else{
			        	rst.put("colorsw", "0");
			        }
				}else{
			        if (rs.getString("cr_status").equals("3")){
			        	rst.put("colorsw", "3");
			        }
			        else{
			        	rst.put("colorsw", "9");
			        }
				}
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
			ecamsLogger.error("## Cmr1300.Sql_Qry_Detail() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr1300.Sql_Qry_Detail() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1300.Sql_Qry_Detail() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr1300.Sql_Qry_Detail() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1300.Sql_Qry_Detail() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Sql_Qry_Detail() method statement
	
	public int Cmr9900Str_Call(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?,?,?, '9', ?, '9' ); End;");//AcptNo,UserId,Sayu,SinCd
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, dataObj.get("AcptNo"));
        	pstmt.setString(2, dataObj.get("UserId"));
        	pstmt.setString(3, dataObj.get("Sayu"));
        	pstmt.setString(4, dataObj.get("SinCd"));
        	pstmt.executeUpdate();
        	pstmt.close();
        	
        	conn.close();
        	pstmt = null;
        	conn = null;
        	
        	return 0;
        	
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
			ecamsLogger.error("## Cmr1300.Cmr9900Str_Call() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1300.Cmr9900Str_Call() SQLException END ##");
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
			ecamsLogger.error("## Cmr1300.Cmr9900Str_Call() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1300.Cmr9900Str_Call() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1300.Cmr9900Str_Call() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}//end of eCmr1300DAO class statement