/*****************************************************************************************
	1. program ID	: Cmp3300.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.BufferedReader;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

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
public class Cmp3200{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


    public Object[] getProgList(String UserId,String Gubun,String StDate,String EdDate,String Step1,String Step2,String Step3,String Step4,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            svStep1     = null;
		String            svStep2     = null;
		String            svStep3     = null;
		String            svStep4     = null;
		String            strStep1    = null;
		String            strStep2    = null;
		String            strStep3    = null;
		String            strStep4    = null;
		String            strQrycd     = "";
		long               stepCnt     = 0;
		boolean           rowSw       = false;
		int               i           = 0;
		long             rowCnt       = 0;
		long             subCnt       = 0;
		//long             totCnt       = 0;
		boolean          sysSw        = false; 
		boolean          jobSw        = false; 
		boolean          qrySw        = false;
		boolean          teamSw        = false; 
		int              chgSw        = 0;
		int              stepGbn      = 1;
		boolean          firstSw      = true;
		UserInfo         secuinfo     = new UserInfo();
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ArrayList<String>  rstot1 = new ArrayList<String>();
		ArrayList<Long>  rstot = new ArrayList<Long>();
		ArrayList<Long>  subtot1 = new ArrayList<Long>();
		ArrayList<Long>  subtot2 = new ArrayList<Long>();
		ArrayList<Long>  subtot3 = new ArrayList<Long>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			String strSecu = secuinfo.getSecuInfo(UserId);
			secuinfo = null;
			strQuery.setLength(0);
			strQuery.append("select ");
			
			if (Step1.equals("1")) {
				strQuery.append("c.cm_sysmsg, ");
				sysSw = true;
			}
			else if (Step1.equals("2")) {
				strQuery.append("a.cm_jobname, ");
				jobSw = true;
			}
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
				else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
			}
			else if (Step1.equals("5")) {
				strQuery.append("f.cm_deptname, ");
				teamSw = true;
			}
			else {
				strQuery.append("e.cm_codename, ");
				qrySw = true;
			}
			
			if (Step2 != null && Step2 != "") {
				stepGbn = 2;
				if (Step2.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step2.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				} 
				else if (Step2.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");	
					qrySw = true;			
				}
			}
			if (Step3 != null && Step3 != "") {
				stepGbn = 3;
				if (Step3.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step3.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				} 
				else if (Step3.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");	
					qrySw = true;			
				}
			}
			if (Step4 != null && Step4 != "") {
				stepGbn = 4;
				if (Step4.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step4.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				} 
				else if (Step4.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");	
					qrySw = true;			
				}
			}
			strQuery.append("  d.cr_passok,count(*) as cnt \n");
			strQuery.append("from ");
			if (sysSw == true) strQuery.append("cmm0030 c, ");
			if (jobSw == true) strQuery.append("cmm0102 a, "); 
			if (qrySw == true) strQuery.append("cmm0020 e, ");
			if (teamSw == true) strQuery.append("cmm0100 f, "); 
			strQuery.append("cmr1000 d,cmr1010 b                                  \n");
			if (Gubun.equals("01")) {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymm') between ? and ?           \n");
			} else {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymmdd') between ? and ?         \n");
			}
			strQuery.append("and d.cr_status in ('8','9') and d.cr_qrycd='04'                    \n");
			strQuery.append("and d.cr_acptno = b.cr_acptno                                       \n");
			if (strSecu.equals("0")) {
				strQuery.append("and b.cr_syscd in (select distinct cm_syscd from cmm0044        \n");
				strQuery.append("                    where cm_userid=? and cm_closedt is null)   \n");
				strQuery.append("and b.cr_jobcd in (select distinct cm_syscd from cmm0044        \n");
				strQuery.append("                    where cm_userid=? and cm_closedt is null    \n");
				strQuery.append("                      and cm_syscd=b.cr_syscd)                  \n");
			}
			strQuery.append("and b.cr_itemid=b.cr_baseitem                                       \n");
			if (sysSw == true) strQuery.append("and d.cr_syscd=c.cm_syscd                        \n");
			if (jobSw == true) strQuery.append("and b.cr_jobcd=a.cm_jobcd                        \n");
			if (teamSw == true) strQuery.append("and d.cr_teamcd=f.cm_deptcd                     \n");
			if (qrySw == true) 
				strQuery.append("and e.cm_macode='CHECKIN' and e.cm_micode=b.cr_qrycd            \n");
			if (JobCd != null && JobCd != "") {
				strQuery.append("and b.cr_syscd=? and b.cr_jobcd=?                               \n");
			} else if (SysCd != null && SysCd != "") {
				strQuery.append("and b.cr_syscd=?                                                \n");
			}
			
            strQuery.append("group by ");
			if (Step1.equals("1")) strQuery.append("c.cm_sysmsg, ");
			else if (Step1.equals("2")) strQuery.append("a.cm_jobname, ");
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
			} else if (Step1.equals("5")) strQuery.append("f.cm_deptname, ");
			else if (qrySw) strQuery.append("e.cm_codename, ");
			
			if (Step2 != null && Step2 != "") {
				if (Step2.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step2.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step2.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");	
			}
			
			if (Step3 != null && Step3 != "") {
				if (Step3.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step3.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step3.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}
			
			if (Step4 != null && Step4 != "") {
				if (Step4.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step4.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step4.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");	
			}
			
			strQuery.append("d.cr_passok \n");
			
            strQuery.append("order by ");
			if (Step1.equals("1")) strQuery.append("c.cm_sysmsg, ");
			else if (Step1.equals("2")) strQuery.append("a.cm_jobname, ");
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
			} else if (Step1.equals("5")) strQuery.append("f.cm_deptname, ");
			else if (qrySw) strQuery.append("e.cm_codename, ");
			
			if (Step2 != null && Step2 != "") {
				if (Step2.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step2.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step2.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");	
			}
			if (Step3 != null && Step3 != "") {
				if (Step3.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step3.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step3.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}
			if (Step4 != null && Step4 != "") {
				if (Step4.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step4.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step4.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");	
			}
			strQuery.append("d.cr_passok \n");
	        
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			if (strSecu.equals("0")) {
				pstmt.setString(++parmCnt, UserId);
				pstmt.setString(++parmCnt, UserId);
			}
			if (JobCd != null && JobCd != "") {
				pstmt.setString(++parmCnt, SysCd);
				pstmt.setString(++parmCnt, JobCd);
			} else if (SysCd != null && SysCd != "") {
				pstmt.setString(++parmCnt, SysCd);
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        
	        rsval.clear();
	        rstot1.clear();
	        rstot.clear();
	        subtot1.clear();
	        subtot2.clear();
	        subtot3.clear();
			while (rs.next()){
				rowSw = false;
				chgSw = 0;
				strStep1 = null;
				strStep2 = null;
				strStep3 = null;
				strStep4 = null;
				
				if (Step1.equals("1")) strStep1 = rs.getString("cm_sysmsg");
				else if (Step1.equals("2")) strStep1 = rs.getString("cm_jobname");
				else if (Step1.equals("3")) {
					if (Gubun.equals("02")) strStep1 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
					                            +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
					else strStep1 = rs.getString("ilja");
				}
				else if (Step1.equals("5")) strStep1 = rs.getString("cm_deptname");
				else strStep1 = rs.getString("cm_codename");
				
				if (Step2 != "" && Step2 != null) {
					if (Step2.equals("1")) strStep2 = rs.getString("cm_sysmsg");
					else if (Step2.equals("2")) strStep2 = rs.getString("cm_jobname");
					else if (Step2.equals("3")) {
						if (Gubun.equals("02")) strStep2 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep2 = rs.getString("ilja");
					}
					else if (Step2.equals("5")) strStep2 = rs.getString("cm_deptname");
					else strStep2 = rs.getString("cm_codename");
				}
				
				if (Step3 != "" && Step3 != null) {
					if (Step3.equals("1")) strStep3 = rs.getString("cm_sysmsg");
					else if (Step3.equals("2")) strStep3 = rs.getString("cm_jobname");
					else if (Step3.equals("3")) {
						if (Gubun.equals("02")) strStep3 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep3 = rs.getString("ilja");
					}
					else if (Step3.equals("5")) strStep3 = rs.getString("cm_deptname");
					else strStep3 = rs.getString("cm_codename");
				}
				
				if (Step4 != "" && Step4 != null) {
					if (Step4.equals("1")) strStep4 = rs.getString("cm_sysmsg");
					else if (Step4.equals("2")) strStep4 = rs.getString("cm_jobname");
					else if (Step4.equals("3")) {
						if (Gubun.equals("02")) strStep4 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep4 = rs.getString("ilja");
					}
					else if (Step4.equals("5")) strStep4 = rs.getString("cm_deptname");
					else strStep4 = rs.getString("cm_codename");
				}
				
				
				//System.out.println("svStep1 : "+svStep1+ " " + strStep1);
				if (svStep1 == null) rowSw = true;
				else if (!svStep1.equals(strStep1)) rowSw = true;
			    if (rowSw == true) {
					chgSw = 1;
			    }
			    
			    //System.out.println("svStep2 : "+svStep2+ " " + strStep2);
			    if (rowSw == false && Step2 != null && Step2 != "") {
			    	if (svStep2 == null) rowSw = true;
			    	else if (!svStep2.equals(strStep2)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 2;
			    	}
			    }
			    if (rowSw == false && Step3 != null && Step3 != "") {
			    	if (svStep3 == null) rowSw = true;
			    	else if (!svStep3.equals(strStep3)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 3;
			    	}
			    }
			    if (rowSw == false && Step4 != null && Step4 != "") {
			    	if (svStep4 == null) rowSw = true;
			    	else if (!svStep4.equals(strStep4)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 4;
			    	}
			    }
			    
			    //System.out.println("check : "+rowSw+ " " + chgSw);
				if (rowSw == true && firstSw == false) {
					rst.put("rowhap" , Long.toString(rowCnt));
					rsval.add(rst);	
                    rowCnt = 0;
                    
                    //System.out.println("+++++strStep1,strStep2,stepGbn,chgSw+++++"+
                    //		           strStep1+","+strStep2+","+Integer.toString(stepGbn)+","+Integer.toString(chgSw));
					if ((stepGbn > 1) && (stepGbn > chgSw)) {						
                        if (stepGbn == 4) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
    							rst.put(rstot1.get(i), Long.toString(subtot3.get(i))); 
    							subCnt = subCnt + subtot3.get(i);
    							subtot3.set(i, rowCnt);
    						}
                        	rst.put("step4name" , svStep3+" 합계");
                        	rst.put("rowhap" , Long.toString(subCnt));  
                        	rsval.add(rst);
                        	
                        	
                        	
                        	if (chgSw == 2) {
                            	rst = new HashMap<String, String>();
                            	subCnt = 0;
                            	for (i = 0;rstot1.size() > i;i++) {
        							rst.put(rstot1.get(i), Long.toString(subtot2.get(i))); 
        							subCnt = subCnt + subtot2.get(i);
        							subtot2.set(i, rowCnt);
        						}
                            	rst.put("step4name" , svStep2+" 합계");
                            	rst.put("rowhap" , Long.toString(subCnt)); 
                            	rsval.add(rst); 
                            	
                            	if (chgSw == 1) {
                                	rst = new HashMap<String, String>();
                                	subCnt = 0;
                                	for (i = 0;rstot1.size() > i;i++) {
            							rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
            							subCnt = subCnt + subtot1.get(i);
            							subtot1.set(i, rowCnt);
            						}
                                	rst.put("step4name" , svStep1+" 합계");
                                	rst.put("rowhap" , Long.toString(subCnt)); 
                                	rsval.add(rst);                            		
                            	}
                        	}
                        } else if (stepGbn == 3) {
	                    	rst = new HashMap<String, String>();
	                    	subCnt = 0;
	                    	for (i = 0;rstot1.size() > i;i++) {
								rst.put(rstot1.get(i), Long.toString(subtot2.get(i))); 
								subCnt = subCnt + subtot2.get(i);
								subtot2.set(i, rowCnt);
							}
                        	rst.put("step3name" , svStep2+" 합계");
	                    	rst.put("rowhap" , Long.toString(subCnt));  
	                    	rsval.add(rst);
	                    	
	                    	if (chgSw == 1) {
	                        	rst = new HashMap<String, String>();
	                        	subCnt = 0;
	                        	for (i = 0;rstot1.size() > i;i++) {
	    							rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
	    							subCnt = subCnt + subtot1.get(i);
	    							subtot1.set(i, rowCnt);
	    						}
	                        	rst.put("step3name" , svStep1+" 합계");
	                        	rst.put("rowhap" , Long.toString(subCnt));  
	                        	rsval.add(rst);                           		
	                    	}						
						} else if (stepGbn == 2) {
	                    	rst = new HashMap<String, String>();
	                    	subCnt = 0;
	                    	for (i = 0;rstot1.size() > i;i++) {
								rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
								subCnt = subCnt + subtot1.get(i);
								subtot1.set(i, rowCnt);
							}
                        	rst.put("step2name" , svStep1+" 합계");
	                    	rst.put("rowhap" , Long.toString(subCnt)); 
	                    	
	                    	//System.out.println("+++++++++++rowhap+++++++++"+Long.toString(subCnt));
	                    	rsval.add(rst);
						}
					}
					svStep1 = strStep1;
					svStep2 = strStep2;
					svStep3 = strStep3;
					svStep4 = strStep4;
					
					rst = new HashMap<String, String>();
					rst.put("step1name",strStep1);
					rst.put("step2name",strStep2);
					rst.put("step3name",strStep3);
					rst.put("step4name",strStep4);
				} 				
				
				//System.out.println("firstSw : "+firstSw);
				if (firstSw == true) {
					svStep1 = strStep1;
					svStep2 = strStep2;
					svStep3 = strStep3;
					svStep4 = strStep4;
					
					rst = new HashMap<String, String>();
					rst.put("step1name",strStep1);
					rst.put("step2name",strStep2);
					rst.put("step3name",strStep3);
					rst.put("step4name",strStep4);
					firstSw = false;					
				}
				
				strQrycd = rs.getString("cr_passok");
				rst.put("col" + strQrycd, Integer.toString(rs.getInt("cnt")));
				rowCnt = rowCnt + rs.getInt("cnt");
			    rowSw = false;
			    
		 		 for (i = 0;rstot1.size() > i;i++) {
				 	 if (rstot1.get(i).equals("col" + strQrycd)) {
				 		 rowSw = true;
				 		 rstot.set(i, rstot.get(i) + rs.getInt("cnt"));
				 		 
				 		 if (stepGbn > 1) subtot1.set(i, subtot1.get(i) + rs.getInt("cnt"));	 
				 		 if (stepGbn > 2) subtot2.set(i, subtot2.get(i) + rs.getInt("cnt"));	 
				 		 if (stepGbn > 3) subtot3.set(i, subtot3.get(i) + rs.getInt("cnt"));
				 		 break;
						 
					 }
				 }
		 		//System.out.println("rowSw2 : "+rowSw);
				 if (rowSw == false) {
					rstot1.add("col" + strQrycd);
					stepCnt = rs.getInt("cnt");
					rstot.add(stepCnt);
					
					if (stepGbn>1) subtot1.add(stepCnt);
					if (stepGbn>2) subtot2.add(stepCnt);
					if (stepGbn>3) subtot3.add(stepCnt);					
				}
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;
			if (firstSw == false) {
				rst.put("rowhap" , Long.toString(rowCnt));
				rsval.add(rst);	
				
				chgSw = stepGbn - 1;
				if ((stepGbn > 1) && (stepGbn > chgSw)) {						
                    if (stepGbn == 4) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							rst.put(rstot1.get(i), Long.toString(subtot3.get(i))); 
							subCnt = subCnt + subtot3.get(i);
							subtot3.set(i, rowCnt);
						}
                    	rst.put("step4name" , svStep3+" 합계");
                    	rst.put("rowhap" , Long.toString(subCnt));  
                    	rsval.add(rst);
                    	
                    	
                    	
                    	if (chgSw == 2) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
    							rst.put(rstot1.get(i), Long.toString(subtot2.get(i))); 
    							subCnt = subCnt + subtot2.get(i);
    							subtot2.set(i, rowCnt);
    						}
                        	rst.put("step4name" , svStep2+" 합계");
                        	rst.put("rowhap" , Long.toString(subCnt)); 
                        	rsval.add(rst); 
                        	
                        	if (chgSw == 1) {
                            	rst = new HashMap<String, String>();
                            	subCnt = 0;
                            	for (i = 0;rstot1.size() > i;i++) {
        							rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
        							subCnt = subCnt + subtot1.get(i);
        							subtot1.set(i, rowCnt);
        						}
                            	rst.put("step4name" , svStep1+" 합계");
                            	rst.put("rowhap" , Long.toString(subCnt)); 
                            	rsval.add(rst);                            		
                        	}
                    	}
                    } else if (stepGbn == 3) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							rst.put(rstot1.get(i), Long.toString(subtot2.get(i))); 
							subCnt = subCnt + subtot2.get(i);
							subtot2.set(i, rowCnt);
						}
                    	rst.put("step3name" , svStep2+" 합계");
                    	rst.put("rowhap" , Long.toString(subCnt));  
                    	rsval.add(rst);
                    	
                    	if (chgSw == 1) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
    							rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
    							subCnt = subCnt + subtot1.get(i);
    							subtot1.set(i, rowCnt);
    						}
                        	rst.put("step3name" , svStep1+" 합계");
                        	rst.put("rowhap" , Long.toString(subCnt));  
                        	rsval.add(rst);                           		
                    	}						
					} else if (stepGbn == 2) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							rst.put(rstot1.get(i), Long.toString(subtot1.get(i))); 
							subCnt = subCnt + subtot1.get(i);
							subtot1.set(i, rowCnt);
						}
                    	rst.put("step2name" , svStep1+" 합계");
                    	rst.put("rowhap" , Long.toString(subCnt)); 
                    	
                    	//System.out.println("+++++++++++rowhap+++++++++"+Long.toString(subCnt));
                    	rsval.add(rst);
					}
				}

            	rst = new HashMap<String, String>();
            	subCnt = 0;
            	for (i = 0;rstot1.size() > i;i++) {
					rst.put(rstot1.get(i), Long.toString(rstot.get(i))); 
					subCnt = subCnt + rstot.get(i);
				}
            	rst.put("step1name" , "총계");
            	rst.put("rowhap" , Long.toString(subCnt));  
            	rsval.add(rst);
			}
			
			returnObjectArray = rsval.toArray();
			//ecamsLogger.debug(rsval.toString());
			rsval = null;
			
			return returnObjectArray;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3200.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp3200.getProgList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3200.getProgList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp3200.getProgList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;				
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp3200.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement
    
}//end of Cmp3200 class statement
