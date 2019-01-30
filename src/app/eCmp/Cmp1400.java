/*****************************************************************************************
	1. program ID	: Cmp1100.java
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

import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
//import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp1400{


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

	public Object[] getProgCnt(String UserId,String ReqCd, String admin,HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			int i=0;
			
			strQuery.setLength(0);
			
			if ("01".equals(ReqCd)) { 
				strQuery.append("SELECT OWNERDEPT, DEPT1, DEPTCD1, DEPT2, DEPTCD2, CM_SYSMSG, CHKSTR, COUNT(CR_ITSMID) CntSum					\n");
				strQuery.append("FROM(						\n");
			}
			
			strQuery.append(" SELECT TOPDEPTNAME(A.CC_REQDEPT) ownerdept                                                                 \n");
			strQuery.append("        ,B.CM_DEPTNAME dept1, b.cm_deptcd deptcd1                                              \n");
			strQuery.append("        ,C.CM_DEPTNAME dept2, c.cm_deptcd deptcd2                                              \n");
			strQuery.append("        ,E.CM_SYSMSG                                                                           \n");
			if("02".equals(ReqCd) || "04".equals(ReqCd)){
				strQuery.append("        ,H.CM_DEPTNAME dept3                                                               \n");
				strQuery.append("        ,A.CC_SRID                                                                 		\n");
				strQuery.append("        ,A.CC_REQTITLE                                                               		\n");
				strQuery.append("        ,to_char(to_date(A.CC_REQDATE,'yyyymmdd'),'yyyy-mm-dd') CC_REQDATE           		\n");
				strQuery.append("        ,I.CM_CODENAME cattype, I.cm_micode cattypecd                                		\n");
			}
			if ("01".equals(etcData.get("dateGbn"))) { 
				strQuery.append("        ,TO_CHAR(F.CR_ACPTDATE,'YYYY-MM') chkstr                                      		\n");
			}else if ("02".equals(etcData.get("dateGbn"))) { 
				strQuery.append("        ,TO_CHAR(MIN(F.CR_ACPTDATE),'YYYY-MM-DD') chkstr                                   \n");
				strQuery.append("        ,TO_CHAR(MAX(F.CR_ACPTDATE),'YYYY-MM-DD') chkend                                   \n");
			}else{
				strQuery.append("        ,TO_CHAR(F.CR_ACPTDATE,'YYYY-MM-DD') chkstr                                      	\n");
			}
			
			if ("01".equals(ReqCd)) { 
				strQuery.append("        ,F.CR_ITSMID                                      									\n");
			}else {
				strQuery.append("        ,COUNT(*) CntSum                                                                       \n");
			}
			strQuery.append("   FROM CMR1010 G,                                                                             \n");
			strQuery.append("        CMR1000 F,                                                                             \n");
			strQuery.append("        CMM0030 E,                                                                             \n");
			strQuery.append("        CMC0110 D,                                                                             \n");
			strQuery.append("        CMM0100 C,                                                                             \n");
			strQuery.append("        CMM0100 B,                                                                             \n");
			strQuery.append("        CMC0100 A                                                                              \n");
			if("02".equals(ReqCd) || "04".equals(ReqCd)){
				strQuery.append("    ,CMM0100 H                                                                             \n");
				strQuery.append("    ,CMM0020 I                                                                             \n");
			}
			strQuery.append("  WHERE A.CC_REQDEPT=B.CM_DEPTCD                                                               \n");
			strQuery.append("    AND A.CC_SRID=D.CC_SRID                                                                    \n");
			strQuery.append("    AND D.CC_DEPTCD=C.CM_DEPTCD                                                                \n");
			strQuery.append("    AND A.CC_SRID=F.CR_ITSMID                                                                  \n");
			strQuery.append("    AND F.CR_SYSCD=E.CM_SYSCD                                                                  \n");
			strQuery.append("    AND F.CR_QRYCD='07'                                                                        \n");
			strQuery.append("    AND F.CR_VERSION='Y'                                                                       \n");
			strQuery.append("    AND F.CR_STATUS<>'3'                                                                       \n");
			strQuery.append("    AND F.CR_PRCDATE IS NOT NULL                                                               \n");
			strQuery.append("    AND F.CR_ACPTNO=G.CR_ACPTNO                                                                \n");
			strQuery.append("    AND G.CR_ITEMID=G.CR_BASEITEM                                                              \n");
			if("02".equals(ReqCd) || "04".equals(ReqCd)){
				strQuery.append("    AND A.CC_DEVDEPT=H.CM_DEPTCD                                                           \n");
				strQuery.append("    AND I.CM_MACODE='CATTYPE' AND I.CM_MICODE=A.CC_CATTYPE                                 \n");
			}
			
			if(null != etcData.get("dept1")){
				strQuery.append("    AND A.CC_REQDEPT in (SELECT CM_DEPTCD         									\n");
				strQuery.append("                           FROM (SELECT * FROM CMM0100 WHERE CM_USEYN='Y') 		\n");
				strQuery.append("                          START WITH CM_DEPTCD=?  									\n");
				strQuery.append("                        CONNECT BY PRIOR CM_DEPTCD=CM_UPDEPTCD)  					\n");
			}
			
			if(null != etcData.get("dept2")){
				strQuery.append("    AND C.CM_DEPTCD = ?																	\n");
			}

			if("01".equals(ReqCd) || "02".equals(ReqCd)){
				if ("01".equals(etcData.get("dateGbn"))) { 
					strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymm') between ? and ?                                    \n");
				} else {
					strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymmdd') between ? and ?                                  \n");
				}
			}else{
				if ("01".equals(etcData.get("dateGbn"))) { 
					strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymm') between ? and ?                                  \n");
				} else {
					strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymmdd') between ? and ?                                \n");
				}
			}
			
			if(null != etcData.get("Sta")){
				strQuery.append("    AND A.CC_STATUS = ?																	\n");
			}

			if(null != etcData.get("srid")){
				strQuery.append("    AND (upper(A.CC_SRID) like upper(?) or upper(A.CC_REQTITLE) like upper(?))				\n");
			}
			
			strQuery.append(" GROUP BY A.CC_REQDEPT                                                                         \n");
			strQuery.append("         ,B.CM_DEPTNAME,b.cm_deptcd,C.CM_DEPTNAME,c.cm_deptcd,E.CM_SYSMSG                      \n");
			if("02".equals(ReqCd) || "04".equals(ReqCd)){
				strQuery.append("     ,H.CM_DEPTNAME      			                                                        \n");
				strQuery.append("     ,A.CC_SRID                                                                 			\n");
				strQuery.append("     ,A.CC_REQTITLE                                                               			\n");
				strQuery.append("     ,A.CC_REQDATE                                                                			\n");
				strQuery.append("     ,I.CM_CODENAME, I.cm_micode                                                 			\n");
			}
			
			if ("01".equals(etcData.get("dateGbn"))) { 
				strQuery.append("        ,TO_CHAR(F.CR_ACPTDATE,'YYYY-MM')                                      			\n");
			}else if ("03".equals(etcData.get("dateGbn"))) { 
				strQuery.append("        ,TO_CHAR(F.CR_ACPTDATE,'YYYY-MM-DD')		                                      	\n");
			}
			
			if("01".equals(ReqCd)){
				strQuery.append("        ,F.CR_ITSMID    )                                 									\n");
			}
			
			if ("01".equals(ReqCd)) { 
				strQuery.append("          GROUP BY  OWNERDEPT, DEPT1, DEPTCD1, DEPT2, DEPTCD2, CM_SYSMSG, CHKSTR			\n");
			}
			
			strQuery.append(" ORDER BY ownerdept,dept1,dept2,			                      								\n");
			
			if("02".equals(ReqCd) || "04".equals(ReqCd)){
				strQuery.append("      dept3,A.CC_SRID,	                      												\n");
			}
			if ("01".equals(ReqCd)) { 
				strQuery.append("          CM_SYSMSG,chkstr			                      									\n");
			}else {
				strQuery.append("          E.CM_SYSMSG,chkstr			                      									\n");
			}
			
			if ("02".equals(etcData.get("dateGbn"))) { 
				strQuery.append("      ,chkend                              												\n");
			}				
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());

			parmCnt = 0;
			
			if(null != etcData.get("dept1")){
				pstmt.setString(++parmCnt, etcData.get("dept1"));
			}
			
			if(null != etcData.get("dept2")){
				pstmt.setString(++parmCnt, etcData.get("dept2"));
			}

			pstmt.setString(++parmCnt, etcData.get("strDate"));
			pstmt.setString(++parmCnt, etcData.get("endDate"));
			
			if(null != etcData.get("Sta")){
				pstmt.setString(++parmCnt, etcData.get("Sta"));
			}

			if(null != etcData.get("srid")){
				pstmt.setString(++parmCnt, "%"+etcData.get("srid")+"%");
				pstmt.setString(++parmCnt, "%"+etcData.get("srid")+"%");
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        
	        String ownerdept = "";
	        String dept1 = "";
	        String dept2 = "";
	        String dept3 = "";
	        String sysmsg = "";
	        String srid = "";
	        int Sum = 0;
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("gbn", "C");
				
				if("".equals(ownerdept) || !rs.getString("ownerdept").equals(ownerdept)){
					ownerdept = rs.getString("ownerdept");
					rst.put("ownerdept", ownerdept);
				}else if(ownerdept.equals(rs.getString("ownerdept"))){
					rst.put("ownerdept", "");
				}

				rst.put("ownerdeptcd", rs.getString("ownerdept"));
				rst.put("dept1cd", rs.getString("deptcd1"));
				rst.put("dept2cd", rs.getString("deptcd2"));
				
				if(!"".equals(rst.get("ownerdept"))){
					dept1 = rs.getString("dept1");
					rst.put("dept1", dept1);
				}else{
					if("".equals(dept1) || !rs.getString("dept1").equals(dept1)){
						dept1 = rs.getString("dept1");
						rst.put("dept1", dept1);
					}else if(dept1.equals(rs.getString("dept1"))){
						rst.put("dept1", "");
					}
				}
				
				if(!"".equals(rst.get("dept1"))){
					dept2 = rs.getString("dept2");
					rst.put("dept2", dept2);
				}else{
					if("".equals(dept2) || !rs.getString("dept2").equals(dept2)){
						dept2 = rs.getString("dept2");
						rst.put("dept2", dept2);
					}else if(dept2.equals(rs.getString("dept2"))){
						rst.put("dept2", "");
					}
				}
				
				if("02".equals(ReqCd) || "04".equals(ReqCd)){
					if(!"".equals(rst.get("dept1"))){
						dept3 = rs.getString("dept3");
						rst.put("dept3", dept3);
					}else{
						if("".equals(dept3) || !rs.getString("dept3").equals(dept3)){
							dept3 = rs.getString("dept3");
							rst.put("dept3", dept3);
						}else if(dept3.equals(rs.getString("dept3"))){
							rst.put("dept3", "");
						}
					}
					
					rst.put("cc_srid", rs.getString("CC_SRID"));
					rst.put("cc_reqtitle", rs.getString("CC_REQTITLE"));
					rst.put("cattypecd", rs.getString("cattypecd"));
					
					if("".equals(srid) || !rs.getString("CC_SRID").equals(srid)){
						srid = rs.getString("CC_SRID");
						rst.put("srid", srid);
						rst.put("reqtitle", rs.getString("CC_REQTITLE"));
						rst.put("reqdate", rs.getString("CC_REQDATE"));
						rst.put("cattype", rs.getString("cattype"));
					}else if(srid.equals(rs.getString("CC_SRID"))){
						rst.put("srid", "");
						rst.put("reqtitle", "");
						rst.put("reqdate", "");
						rst.put("cattype", "");
					}
				}
				
				if(!"".equals(rst.get("dept2"))){
					sysmsg = rs.getString("CM_SYSMSG");
					rst.put("sysmsg", sysmsg);
				}else{
					if("".equals(sysmsg) || !rs.getString("CM_SYSMSG").equals(sysmsg)){
						sysmsg = rs.getString("CM_SYSMSG");
						rst.put("sysmsg", sysmsg);
					}else if(sysmsg.equals(rs.getString("CM_SYSMSG"))){
						rst.put("sysmsg", "");
					}
				}
				
				if ("02".equals(etcData.get("dateGbn"))) { 
					rst.put("chkDate", rs.getString("chkstr")+"~"+rs.getString("chkend"));
				}else{
					rst.put("chkDate", rs.getString("chkstr"));
				}
				rst.put("CntSum", rs.getString("CntSum"));
				
				Sum = Sum + rs.getInt("CntSum");
				
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			
			if(rsval.size()>0){
		        int rsvalCnt = rsval.size();
		        int deptSum = 0;
		        int total = 0;
		        int Srtotal = 0;
		        int currCnt = 0;
		        int totCnt = 0;
		        int SrtotalSum = 0;
		        String ownerdeptname = "";
		        String ownerdeptname2 = "";
		        String deptname = "";
		        String deptcd = "";
				for(i=0; i<rsvalCnt; i++){
					if(!"".equals(rsval.get(i).get("ownerdept"))){
						if(i==0){
							ownerdeptname = rsval.get(i).get("ownerdept");
						}else{
							if(!ownerdeptname.equals(rsval.get(i).get("ownerdept"))){
								int srCnt = 0;
								
								if("02".equals(ReqCd) || "04".equals(ReqCd)){
									strQuery.setLength(0);
									strQuery.append(" SELECT count(distinct A.CC_SRID) srCnt                                                    \n");
									strQuery.append("   FROM CMM0020 I,                                                                         \n");
									strQuery.append("        CMM0100 H,                                                                         \n");
									strQuery.append("        CMR1010 G,                                                                         \n");
									strQuery.append("        CMR1000 F,                                                                         \n");
									strQuery.append("        CMM0030 E,                                                                         \n");
									strQuery.append("        CMC0110 D,                                                                         \n");
									strQuery.append("        CMM0100 C,                                                                         \n");
									strQuery.append("        CMM0100 B,                                                                         \n");
									strQuery.append("        CMC0100 A                                                                          \n");
									strQuery.append("  WHERE A.CC_REQDEPT=B.CM_DEPTCD                                                           \n");
									strQuery.append("    AND A.CC_REQDEPT= ?                                                                    \n");
									strQuery.append("    AND B.CM_DEPTCD = ?                                                                    \n");
									strQuery.append("    AND A.CC_SRID=D.CC_SRID                                                                \n");
									strQuery.append("    AND A.CC_DEVDEPT=H.CM_DEPTCD                                                           \n");
									strQuery.append("    AND D.CC_DEPTCD=C.CM_DEPTCD                                                            \n");
									strQuery.append("    AND A.CC_SRID=F.CR_ITSMID                                                              \n");
									strQuery.append("    AND F.CR_SYSCD=E.CM_SYSCD                                                              \n");
									strQuery.append("    AND F.CR_QRYCD='07'                                                                    \n");
									strQuery.append("    AND F.CR_VERSION='Y'                                                                   \n");
									strQuery.append("    AND F.CR_STATUS<>'3'                                                                   \n");
									strQuery.append("    AND F.CR_PRCDATE IS NOT NULL                                                           \n");
									strQuery.append("    AND F.CR_ACPTNO=G.CR_ACPTNO                                                            \n");
									strQuery.append("    AND G.CR_ITEMID=G.CR_BASEITEM                                                          \n");
									strQuery.append("    AND I.CM_MACODE='CATTYPE' AND I.CM_MICODE=A.CC_CATTYPE                                 \n");
									
									if(null != etcData.get("dept1")){
										strQuery.append("    AND A.CC_REQDEPT in (SELECT CM_DEPTCD         									\n");
										strQuery.append("                           FROM (SELECT * FROM CMM0100 WHERE CM_USEYN='Y') 		\n");
										strQuery.append("                          START WITH CM_DEPTCD=?  									\n");
										strQuery.append("                        CONNECT BY PRIOR CM_DEPTCD=CM_UPDEPTCD)  					\n");
									}
									
									if(null != etcData.get("dept2")){
										strQuery.append("    AND C.CM_DEPTCD = ?																	\n");
									}

									if("01".equals(ReqCd) || "02".equals(ReqCd)){
										if ("01".equals(etcData.get("dateGbn"))) { 
											strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymm') between ? and ?                                    \n");
										} else {
											strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymmdd') between ? and ?                                  \n");
										}
									}else{
										if ("01".equals(etcData.get("dateGbn"))) { 
											strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymm') between ? and ?                                  \n");
										} else {
											strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymmdd') between ? and ?                                \n");
										}
									}
									
									if(null != etcData.get("Sta")){
										strQuery.append("    AND A.CC_STATUS = ?																	\n");
									}

									if(null != etcData.get("srid")){
										strQuery.append("    AND (upper(A.CC_SRID) like upper(?) or upper(A.CC_REQTITLE) like upper(?))				\n");
									}
									
									pstmt2 = conn.prepareStatement(strQuery.toString());
									//pstmt2 = new LoggableStatement(conn,strQuery.toString());

									parmCnt = 0;
									
									pstmt2.setString(++parmCnt, ownerdeptname);
									pstmt2.setString(++parmCnt, deptcd);
									
									if(null != etcData.get("dept1")){
										pstmt2.setString(++parmCnt, etcData.get("dept1"));
									}
									
									if(null != etcData.get("dept2")){
										pstmt2.setString(++parmCnt, etcData.get("dept2"));
									}

									pstmt2.setString(++parmCnt, etcData.get("strDate"));
									pstmt2.setString(++parmCnt, etcData.get("endDate"));
									
									if(null != etcData.get("Sta")){
										pstmt2.setString(++parmCnt, etcData.get("Sta"));
									}

									if(null != etcData.get("srid")){
										pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
										pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
									}
							        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
							        rs2 = pstmt2.executeQuery();
							        
									while (rs2.next()){
										srCnt = rs2.getInt("srCnt");
									}
									rs2.close();
									pstmt2.close();
								}

								//System.out.println("i=0>>>>>>>>ownerdeptname:"+ownerdeptname+",ownerdeptname2:"+ownerdeptname2);
								
								rst = new HashMap<String, String>();
								rst.put("gbn", "T");
								rst.put("ownerdept", ownerdeptname);
								rst.put("dept1", deptname);
					        	rst.put("dept2", "");
					        	
								if("02".equals(ReqCd) || "04".equals(ReqCd)){
									rst.put("dept3", "");
									rst.put("srid", Integer.toString(srCnt));
									rst.put("reqtitle", "");
									rst.put("reqdate", "");
									rst.put("cattype", "");
								}
								rst.put("sysmsg", "");
								rst.put("chkDate", "");
								rst.put("CntSum", Integer.toString(deptSum));
								
								total = total + deptSum;
								Srtotal = Srtotal + srCnt;
								
								rsval.add(rst);
								rst = null;
								currCnt++;
							}
							rst = new HashMap<String, String>();
							rst.put("gbn", "T");
							if("".equals(ownerdeptname2)) rst.put("ownerdept", ownerdeptname+" 소계");
							else rst.put("ownerdept", ownerdeptname2+" 소계");
							rst.put("dept1", "");
				        	rst.put("dept2", "");
							if("02".equals(ReqCd) || "04".equals(ReqCd)){
								rst.put("dept3", "");
								rst.put("srid", Integer.toString(Srtotal));
								rst.put("reqtitle", "");
								rst.put("reqdate", "");
								rst.put("cattype", "");
							}
							rst.put("sysmsg", "");
							rst.put("chkDate", "");
							rst.put("CntSum", Integer.toString(total));
							rsval.add(rst);
							rst = null;
							totCnt++;
							
							SrtotalSum = SrtotalSum + Srtotal;
							
							if(!"".equals(rsval.get(i).get("ownerdept"))) ownerdeptname2 = rsval.get(i).get("ownerdept");
						}
						deptname = rsval.get(i).get("dept1");
						deptcd = rsval.get(i).get("dept1cd");
						
						total = 0;
						Srtotal = 0;
						deptSum = Integer.parseInt(rsval.get(i).get("CntSum"));
					}else{
						if(!"".equals(rsval.get(i).get("dept1"))){
							int srCnt = 0;
							
							if("02".equals(ReqCd) || "04".equals(ReqCd)){
								strQuery.setLength(0);
								strQuery.append(" SELECT count(distinct A.CC_SRID) srCnt                                                    \n");
								strQuery.append("   FROM CMM0020 I,                                                                         \n");
								strQuery.append("        CMM0100 H,                                                                         \n");
								strQuery.append("        CMR1010 G,                                                                         \n");
								strQuery.append("        CMR1000 F,                                                                         \n");
								strQuery.append("        CMM0030 E,                                                                         \n");
								strQuery.append("        CMC0110 D,                                                                         \n");
								strQuery.append("        CMM0100 C,                                                                         \n");
								strQuery.append("        CMM0100 B,                                                                         \n");
								strQuery.append("        CMC0100 A                                                                          \n");
								strQuery.append("  WHERE A.CC_REQDEPT=B.CM_DEPTCD                                                           \n");
								strQuery.append("    AND A.CC_REQDEPT= ?                                                                    \n");
								strQuery.append("    AND B.CM_DEPTCD = ?                                                                    \n");
								strQuery.append("    AND A.CC_SRID=D.CC_SRID                                                                \n");
								strQuery.append("    AND A.CC_DEVDEPT=H.CM_DEPTCD                                                           \n");
								strQuery.append("    AND D.CC_DEPTCD=C.CM_DEPTCD                                                            \n");
								strQuery.append("    AND A.CC_SRID=F.CR_ITSMID                                                              \n");
								strQuery.append("    AND F.CR_SYSCD=E.CM_SYSCD                                                              \n");
								strQuery.append("    AND F.CR_QRYCD='07'                                                                    \n");
								strQuery.append("    AND F.CR_VERSION='Y'                                                                   \n");
								strQuery.append("    AND F.CR_STATUS<>'3'                                                                   \n");
								strQuery.append("    AND F.CR_PRCDATE IS NOT NULL                                                           \n");
								strQuery.append("    AND F.CR_ACPTNO=G.CR_ACPTNO                                                            \n");
								strQuery.append("    AND G.CR_ITEMID=G.CR_BASEITEM                                                          \n");
								strQuery.append("    AND I.CM_MACODE='CATTYPE' AND I.CM_MICODE=A.CC_CATTYPE                                 \n");
								
								if(null != etcData.get("dept1")){
									strQuery.append("    AND A.CC_REQDEPT in (SELECT CM_DEPTCD         									\n");
									strQuery.append("                           FROM (SELECT * FROM CMM0100 WHERE CM_USEYN='Y') 		\n");
									strQuery.append("                          START WITH CM_DEPTCD=?  									\n");
									strQuery.append("                        CONNECT BY PRIOR CM_DEPTCD=CM_UPDEPTCD)  					\n");
								}
								
								if(null != etcData.get("dept2")){
									strQuery.append("    AND C.CM_DEPTCD = ?																	\n");
								}

								if("01".equals(ReqCd) || "02".equals(ReqCd)){
									if ("01".equals(etcData.get("dateGbn"))) { 
										strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymm') between ? and ?                                    \n");
									} else {
										strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymmdd') between ? and ?                                  \n");
									}
								}else{
									if ("01".equals(etcData.get("dateGbn"))) { 
										strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymm') between ? and ?                                  \n");
									} else {
										strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymmdd') between ? and ?                                \n");
									}
								}
								
								if(null != etcData.get("Sta")){
									strQuery.append("    AND A.CC_STATUS = ?																	\n");
								}

								if(null != etcData.get("srid")){
									strQuery.append("    AND (upper(A.CC_SRID) like upper(?) or upper(A.CC_REQTITLE) like upper(?))				\n");
								}
								
								pstmt2 = conn.prepareStatement(strQuery.toString());
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());

								parmCnt = 0;
								
								//pstmt2.setString(++parmCnt, ownerdeptname);
								
								//ecamsLogger.error("++++++++++++++++++++++++ownerdeptname:"+ownerdeptname+",ownerdeptname2:"+ownerdeptname2);
								
								if(currCnt == 0) pstmt2.setString(++parmCnt, ownerdeptname);
								else {
									if(!"".equals(ownerdeptname2)){
										if (totCnt>0) pstmt2.setString(++parmCnt, ownerdeptname2);
										else if (!"".equals(rsval.get(i).get("ownerdept")) && !ownerdeptname2.equals(rsval.get(i).get("ownerdept"))) pstmt2.setString(++parmCnt, ownerdeptname2);
										else pstmt2.setString(++parmCnt, ownerdeptname2);
									}else{
										pstmt2.setString(++parmCnt, ownerdeptname);
									}
								}
								pstmt2.setString(++parmCnt, deptcd);
								
								if(null != etcData.get("dept1")){
									pstmt2.setString(++parmCnt, etcData.get("dept1"));
								}
								
								if(null != etcData.get("dept2")){
									pstmt2.setString(++parmCnt, etcData.get("dept2"));
								}

								pstmt2.setString(++parmCnt, etcData.get("strDate"));
								pstmt2.setString(++parmCnt, etcData.get("endDate"));
								
								if(null != etcData.get("Sta")){
									pstmt2.setString(++parmCnt, etcData.get("Sta"));
								}

								if(null != etcData.get("srid")){
									pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
									pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
								}
						        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
						        rs2 = pstmt2.executeQuery();
						        
								while (rs2.next()){
									srCnt = rs2.getInt("srCnt");
								}
								rs2.close();
								pstmt2.close();
							}
							
							//System.out.println(">>>>>>>ownerdeptname:"+ownerdeptname+",ownerdeptname2:"+ownerdeptname2);
							rst = new HashMap<String, String>();
							rst.put("gbn", "T");
							if(currCnt == 0) rst.put("ownerdept", ownerdeptname);
							else if (totCnt>0) rst.put("ownerdept", ownerdeptname2);
							else if (!"".equals(rsval.get(i).get("ownerdept")) && !ownerdeptname2.equals(rsval.get(i).get("ownerdept"))) rst.put("ownerdept", ownerdeptname2);
							else rst.put("ownerdept", "");
							rst.put("dept1", deptname);
				        	rst.put("dept2", "");
				        	
							if("02".equals(ReqCd) || "04".equals(ReqCd)){
								rst.put("dept3", "");
								rst.put("srid", Integer.toString(srCnt));
								rst.put("reqtitle", "");
								rst.put("reqdate", "");
								rst.put("cattype", "");
							}
							rst.put("sysmsg", "");
							rst.put("chkDate", "");
							rst.put("CntSum", Integer.toString(deptSum));
							
							total = total + deptSum;
							Srtotal = Srtotal + srCnt;
							rsval.add(rst);
							rst = null;
							currCnt++;
							totCnt = 0;
							
							deptSum = Integer.parseInt(rsval.get(i).get("CntSum"));
							deptname = rsval.get(i).get("dept1");
							deptcd = rsval.get(i).get("dept1cd");
						}else{
							deptSum = deptSum + Integer.parseInt(rsval.get(i).get("CntSum"));
						}
					}
				}
				
				if(rsvalCnt<=rsval.size()){
					int srCnt = 0;
					
					if("02".equals(ReqCd) || "04".equals(ReqCd)){
						strQuery.setLength(0);
						strQuery.append(" SELECT count(distinct A.CC_SRID) srCnt                                                    \n");
						strQuery.append("   FROM CMM0020 I,                                                                         \n");
						strQuery.append("        CMM0100 H,                                                                         \n");
						strQuery.append("        CMR1010 G,                                                                         \n");
						strQuery.append("        CMR1000 F,                                                                         \n");
						strQuery.append("        CMM0030 E,                                                                         \n");
						strQuery.append("        CMC0110 D,                                                                         \n");
						strQuery.append("        CMM0100 C,                                                                         \n");
						strQuery.append("        CMM0100 B,                                                                         \n");
						strQuery.append("        CMC0100 A                                                                          \n");
						strQuery.append("  WHERE A.CC_REQDEPT=B.CM_DEPTCD                                                           \n");
						strQuery.append("    AND A.CC_REQDEPT= ?                                                                    \n");
						strQuery.append("    AND B.CM_DEPTCD = ?                                                                    \n");
						strQuery.append("    AND A.CC_SRID=D.CC_SRID                                                                \n");
						strQuery.append("    AND A.CC_DEVDEPT=H.CM_DEPTCD                                                           \n");
						strQuery.append("    AND D.CC_DEPTCD=C.CM_DEPTCD                                                            \n");
						strQuery.append("    AND A.CC_SRID=F.CR_ITSMID                                                              \n");
						strQuery.append("    AND F.CR_SYSCD=E.CM_SYSCD                                                              \n");
						strQuery.append("    AND F.CR_QRYCD='07'                                                                    \n");
						strQuery.append("    AND F.CR_VERSION='Y'                                                                   \n");
						strQuery.append("    AND F.CR_STATUS<>'3'                                                                   \n");
						strQuery.append("    AND F.CR_PRCDATE IS NOT NULL                                                           \n");
						strQuery.append("    AND F.CR_ACPTNO=G.CR_ACPTNO                                                            \n");
						strQuery.append("    AND G.CR_ITEMID=G.CR_BASEITEM                                                          \n");
						strQuery.append("    AND I.CM_MACODE='CATTYPE' AND I.CM_MICODE=A.CC_CATTYPE                                 \n");
						
						if (null != etcData.get("dept1")){
							strQuery.append("    AND A.CC_REQDEPT in (SELECT CM_DEPTCD         									\n");
							strQuery.append("                           FROM (SELECT * FROM CMM0100 WHERE CM_USEYN='Y') 		\n");
							strQuery.append("                          START WITH CM_DEPTCD=?  									\n");
							strQuery.append("                        CONNECT BY PRIOR CM_DEPTCD=CM_UPDEPTCD)  					\n");
						}
						
						if(null != etcData.get("dept2")){
							strQuery.append("    AND C.CM_DEPTCD = ?																	\n");
						}

						if("01".equals(ReqCd) || "02".equals(ReqCd)){
							if ("01".equals(etcData.get("dateGbn"))) { 
								strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymm') between ? and ?                                    \n");
							} else {
								strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymmdd') between ? and ?                                  \n");
							}
						}else{
							if ("01".equals(etcData.get("dateGbn"))) { 
								strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymm') between ? and ?                                  \n");
							} else {
								strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymmdd') between ? and ?                                \n");
							}
						}
						
						if(null != etcData.get("Sta")){
							strQuery.append("    AND A.CC_STATUS = ?																	\n");
						}

						if(null != etcData.get("srid")){
							strQuery.append("    AND (upper(A.CC_SRID) like upper(?) or upper(A.CC_REQTITLE) like upper(?))				\n");
						}
						
						pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());

						parmCnt = 0;
						
						//pstmt2.setString(++parmCnt, ownerdeptname);
						if("".equals(ownerdeptname2)) pstmt2.setString(++parmCnt, ownerdeptname);
						else pstmt2.setString(++parmCnt, ownerdeptname2);
						pstmt2.setString(++parmCnt, deptcd);
						
						if(null != etcData.get("dept1")){
							pstmt2.setString(++parmCnt, etcData.get("dept1"));
						}
						
						if(null != etcData.get("dept2")){
							pstmt2.setString(++parmCnt, etcData.get("dept2"));
						}

						pstmt2.setString(++parmCnt, etcData.get("strDate"));
						pstmt2.setString(++parmCnt, etcData.get("endDate"));
						
						if(null != etcData.get("Sta")){
							pstmt2.setString(++parmCnt, etcData.get("Sta"));
						}

						if(null != etcData.get("srid")){
							pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
							pstmt2.setString(++parmCnt, "%"+etcData.get("srid")+"%");
						}
				        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				        rs2 = pstmt2.executeQuery();
				        
						while (rs2.next()){
							srCnt = rs2.getInt("srCnt");
						}
						rs2.close();
						pstmt2.close();
					}
					
					//System.out.println("LAST>>>>>>>ownerdeptname:"+ownerdeptname+",ownerdeptname2:"+ownerdeptname2);
					
					rst = new HashMap<String, String>();
					rst.put("gbn", "T");
					
					if(rsvalCnt==rsval.size()) {
						if("".equals(ownerdeptname2)) rst.put("ownerdept", ownerdeptname);
						else rst.put("ownerdept", ownerdeptname2);
					}else{
						if("".equals(ownerdeptname2)) rst.put("ownerdept", "");
						else {
							boolean sameflg = false;
							for(int k=(rsval.size()-1); k>rsvalCnt; k--){
								//System.out.println(k+">>>>>>>"+rsval.get(k).get("ownerdept")+","+ownerdeptname2);
								if(rsval.get(k).get("ownerdept").equals(ownerdeptname2)){
									sameflg = true;
									break;
								}
							}
							if(sameflg){
								rst.put("ownerdept", "");
							}else{
								rst.put("ownerdept", ownerdeptname2);
							}
						}
					}
					
					/*
					if(rsvalCnt==rsval.size()) {
						if("".equals(ownerdeptname2)) rst.put("ownerdept", ownerdeptname);
						else rst.put("ownerdept", ownerdeptname2);
					}else{
						if(!"".equals(ownerdeptname2)) rst.put("ownerdept", ownerdeptname2);
						else rst.put("ownerdept", "");
					}
					*/
					//else rst.put("ownerdept", "");
					rst.put("dept1", deptname);
		        	rst.put("dept2", "");
					if("02".equals(ReqCd) || "04".equals(ReqCd)){
						rst.put("dept3", "");
						rst.put("srid", Integer.toString(srCnt));
						rst.put("reqtitle", "");
						rst.put("reqdate", "");
						rst.put("cattype", "");
					}
					rst.put("sysmsg", "");
					rst.put("chkDate", "");
					rst.put("CntSum", Integer.toString(deptSum));
					
					total = total + deptSum;
					Srtotal = Srtotal + srCnt;
					rsval.add(rst);
					rst = null;

					rst = new HashMap<String, String>();
					rst.put("gbn", "T");
					if("".equals(ownerdeptname2)) rst.put("ownerdept", ownerdeptname+" 소계");
					else rst.put("ownerdept", ownerdeptname2+" 소계");
					rst.put("dept1", "");
		        	rst.put("dept2", "");
					if("02".equals(ReqCd) || "04".equals(ReqCd)){
						rst.put("dept3", "");
						rst.put("srid", Integer.toString(Srtotal));
						rst.put("reqtitle", "");
						rst.put("reqdate", "");
						rst.put("cattype", "");
					}
					rst.put("sysmsg", "");
					rst.put("chkDate", "");
					rst.put("CntSum", Integer.toString(total));
					rsval.add(rst);
					rst = null;
					
					SrtotalSum = SrtotalSum + Srtotal;
				}
				
				strQuery.setLength(0);
				strQuery.append(" SELECT distinct c.cm_deptcd, C.CM_DEPTNAME                                                    \n");
				strQuery.append("   FROM CMR1010 G,                                                                             \n");
				strQuery.append("        CMR1000 F,                                                                             \n");
				strQuery.append("        CMM0030 E,                                                                             \n");
				strQuery.append("        CMC0110 D,                                                                             \n");
				strQuery.append("        CMM0100 C,                                                                             \n");
				strQuery.append("        CMM0100 B,                                                                             \n");
				strQuery.append("        CMC0100 A                                                                              \n");
				if("02".equals(ReqCd) || "04".equals(ReqCd)){
					strQuery.append("    ,CMM0100 H                                                                             \n");
					strQuery.append("    ,CMM0020 I                                                                             \n");
				}
				strQuery.append("  WHERE A.CC_REQDEPT=B.CM_DEPTCD                                                               \n");
				strQuery.append("    AND A.CC_SRID=D.CC_SRID                                                                    \n");
				strQuery.append("    AND D.CC_DEPTCD=C.CM_DEPTCD                                                                \n");
				strQuery.append("    AND A.CC_SRID=F.CR_ITSMID                                                                  \n");
				strQuery.append("    AND F.CR_SYSCD=E.CM_SYSCD                                                                  \n");
				strQuery.append("    AND F.CR_QRYCD='07'                                                                        \n");
				strQuery.append("    AND F.CR_VERSION='Y'                                                                       \n");
				strQuery.append("    AND F.CR_STATUS<>'3'                                                                       \n");
				strQuery.append("    AND F.CR_PRCDATE IS NOT NULL                                                               \n");
				strQuery.append("    AND F.CR_ACPTNO=G.CR_ACPTNO                                                                \n");
				strQuery.append("    AND G.CR_ITEMID=G.CR_BASEITEM                                                              \n");
				if("02".equals(ReqCd) || "04".equals(ReqCd)){
					strQuery.append("    AND A.CC_DEVDEPT=H.CM_DEPTCD                                                           \n");
					strQuery.append("    AND I.CM_MACODE='CATTYPE' AND I.CM_MICODE=A.CC_CATTYPE                                 \n");
				}

				if(null != etcData.get("dept1")){
					strQuery.append("    AND A.CC_REQDEPT in (SELECT CM_DEPTCD         									\n");
					strQuery.append("                           FROM (SELECT * FROM CMM0100 WHERE CM_USEYN='Y') 		\n");
					strQuery.append("                          START WITH CM_DEPTCD=?  									\n");
					strQuery.append("                        CONNECT BY PRIOR CM_DEPTCD=CM_UPDEPTCD)  					\n");
				}
				
				if(null != etcData.get("dept2")){
					strQuery.append("    AND C.CM_DEPTCD = ?																	\n");
				}

				if("01".equals(ReqCd) || "02".equals(ReqCd)){
					if ("01".equals(etcData.get("dateGbn"))) { 
						strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymm') between ? and ?                                    \n");
					} else {
						strQuery.append("    AND TO_CHAR(F.CR_ACPTDATE,'yyyymmdd') between ? and ?                                  \n");
					}
				}else{
					if ("01".equals(etcData.get("dateGbn"))) { 
						strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymm') between ? and ?                                  \n");
					} else {
						strQuery.append("    AND TO_CHAR(A.CC_CREATEDATE,'yyyymmdd') between ? and ?                                \n");
					}
				}
				
				if(null != etcData.get("Sta")){
					strQuery.append("    AND A.CC_STATUS = ?																	\n");
				}
	
				if(null != etcData.get("srid")){
					strQuery.append("    AND (upper(A.CC_SRID) like upper(?) or upper(A.CC_REQTITLE) like upper(?))				\n");
				}
				
				strQuery.append(" ORDER BY C.CM_DEPTNAME					                      								\n");
				
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	
				parmCnt = 0;

				if(null != etcData.get("dept1")){
					pstmt.setString(++parmCnt, etcData.get("dept1"));
				}
				
				if(null != etcData.get("dept2")){
					pstmt.setString(++parmCnt, etcData.get("dept2"));
				}
	
				pstmt.setString(++parmCnt, etcData.get("strDate"));
				pstmt.setString(++parmCnt, etcData.get("endDate"));
				
				if(null != etcData.get("Sta")){
					pstmt.setString(++parmCnt, etcData.get("Sta"));
				}
	
				if(null != etcData.get("srid")){
					pstmt.setString(++parmCnt, "%"+etcData.get("srid")+"%");
					pstmt.setString(++parmCnt, "%"+etcData.get("srid")+"%");
				}
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
		        
		        deptSum = 0;
		        while(rs.next()){
		        	deptSum = 0;
					//ecamsLogger.error("+++++++++++++++++++++++++++++++++++++++"+rsvalCnt);
					for(i=0; i<rsvalCnt; i++){
						if(rsval.get(i).get("dept2cd").equals(rs.getString("cm_deptcd"))){
							deptSum = deptSum + Integer.parseInt(rsval.get(i).get("CntSum"));
						}
					}
					
					if(deptSum>0){
						rst = new HashMap<String, String>();
						rst.put("gbn", "T");
						rst.put("ownerdept", "");
						rst.put("dept1", "");
			        	rst.put("dept2", rs.getString("CM_DEPTNAME"));
						if("02".equals(ReqCd) || "04".equals(ReqCd)){
							rst.put("dept3", "");
							rst.put("srid", "");
							rst.put("reqtitle", "");
							rst.put("reqdate", "");
							rst.put("cattype", "");
						}
						rst.put("sysmsg", "");
						rst.put("chkDate", "");
						rst.put("CntSum", Integer.toString(deptSum));
						rsval.add(rst);
						rst = null;
					}
		        }
		        rs.close();
		        pstmt.close();
		        
				rst = new HashMap<String, String>();
				rst.put("gbn", "S");
				rst.put("ownerdept", "총계");
				rst.put("dept1", "");
				rst.put("dept2", "");
				if("02".equals(ReqCd) || "04".equals(ReqCd)){
					rst.put("dept3", "");
					rst.put("srid", Integer.toString(SrtotalSum));
					rst.put("reqtitle", "");
					rst.put("reqdate", "");
					rst.put("cattype", "");
				}
				rst.put("sysmsg", "");
				
				if ("01".equals(etcData.get("dateGbn"))) { 
					rst.put("chkDate", etcData.get("strDate").substring(0, 4)+"-"+etcData.get("strDate").substring(4, 6)+"~"+etcData.get("endDate").substring(0, 4)+"-"+etcData.get("endDate").substring(4, 6));
				}else { 
					rst.put("chkDate", etcData.get("strDate").substring(0, 4)+"-"+etcData.get("strDate").substring(4, 6)+"-"+etcData.get("strDate").substring(6, 8)+"~"+etcData.get("endDate").substring(0, 4)+"-"+etcData.get("endDate").substring(4, 6)+"-"+etcData.get("endDate").substring(6, 8));
				}
				
				//rst.put("chkDate", "");
				rst.put("CntSum", Integer.toString(Sum));
				rsval.add(rst);
				rst = null;
			}
			
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1400.getProgCnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1400.getProgCnt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1400.getProgCnt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1400.getProgCnt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1400.getProgCnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgCnt() method statement

	
	public Object getTestDetail(ArrayList<HashMap<String, String>> isridList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;	
		
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			int i = 0;
			for(i=0; i<isridList.size(); i++){
	            int caseseq = 0;
	            
				strQuery.setLength(0);
				strQuery.append(" select distinct 'U' testgbn, a.cc_srid, b.cc_casename, b.cc_etc, b.cc_exprst, 				 \n");
				strQuery.append("        c.cm_username, to_char(to_date(b.cc_testday,'yyyymmdd'),'yyyy/mm/dd') testday,          \n");
				strQuery.append("        decode(b.cc_testrst,'P','PASS','FAIL') testrst, b.cc_caseseq                            \n");
				strQuery.append("   from cmc0100 a, cmc0200 b, cmm0040 c                                                         \n");
				strQuery.append("  where a.cc_srid = ?                                                                           \n");
				strQuery.append("    and a.cc_srid = b.cc_srid                                                                   \n");
				strQuery.append("    and a.cc_createuser = c.cm_userid                                                           \n");
				strQuery.append("  order by c.cm_username                                                                        \n");
				pstmt = conn.prepareStatement(strQuery.toString());
		        //pstmt = new LoggableStatement(conn,strQuery.toString());     
		        pstmt.setString(1, isridList.get(i).get("isrid"));    
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
	            rs = pstmt.executeQuery();
	            while (rs.next()){
	            	rst = new HashMap<String, String>();
	            	rst.put("cc_srid", rs.getString("cc_srid"));
            		rst.put("testgbn", "단위테스트");
	            	
	        		rst.put("cc_casename", rs.getString("cc_casename"));
	        		rst.put("cc_etc", rs.getString("cc_etc"));
	        		rst.put("cc_exprst", rs.getString("cc_exprst"));
            		rst.put("cm_username", rs.getString("cm_username"));
	        		
	        		rst.put("testday", rs.getString("testday"));
	        		rst.put("testrst", rs.getString("testrst"));
	        		rst.put("cc_worktime", "");
	            	
	        		if(caseseq == 0){
	            		caseseq = rs.getInt("cc_caseseq");
	            		
		        		strQuery.setLength(0);
		    			strQuery.append(" select count(*) cnt from cmc0101 where cc_srid = ? and cc_caseseq = ? and cc_division in ('54','55')  \n");
		    			pstmt2 = conn.prepareStatement(strQuery.toString());
		    			pstmt2.setString(1, isridList.get(i).get("isrid"));
		    			pstmt2.setString(2, rs.getString("cc_caseseq"));
		                rs2 = pstmt2.executeQuery();
		                if (rs2.next()){
		                	if(rs2.getInt("cnt")>0){
		                		rst.put("file", "있음");
		                	}else{
		                		rst.put("file", "없음");
		                	}
		                }
		                rs2.close();
		                pstmt2.close();
	        		}else{
	        			if(caseseq == rs.getInt("cc_caseseq")){
		        			rst.put("file", "");
	        			}else{
	                		caseseq = rs.getInt("cc_caseseq");
	                		
    		        		strQuery.setLength(0);
    		    			strQuery.append(" select count(*) cnt from cmc0101 where cc_srid = ? and cc_caseseq = ? and cc_division = '43'  \n");
    		    			pstmt2 = conn.prepareStatement(strQuery.toString());
    		    			pstmt2.setString(1, isridList.get(i).get("isrid"));
    		    			pstmt2.setString(2, rs.getString("cc_caseseq"));
    		                rs2 = pstmt2.executeQuery();
    		                if (rs2.next()){
    		                	if(rs2.getInt("cnt")>0){
    		                		rst.put("file", "있음");
    		                	}else{
    		                		rst.put("file", "없음");
    		                	}
    		                }
    		                rs2.close();
    		                pstmt2.close();
	        			}
	        		}
	        		
		        	rtList.add(rst);
	        		rst = null;
	        		
				}
	            rs.close();
	            pstmt.close();
	            
	            strQuery.setLength(0);
				strQuery.append(" select distinct 'T' testgbn, a.cc_srid, b.cc_casename, 						                 \n");
				strQuery.append("        c.cc_itemmsg cc_etc, d.cc_itemmsg cc_exprst,                                            \n");
				strQuery.append("        f.cm_username, to_char(to_date(e.cc_testday,'yyyymmdd'),'yyyy/mm/dd') testday,          \n");
				strQuery.append("        e.cc_testrst testrst, e.cc_worktime, b.cc_caseseq caseseq,                              \n");
				strQuery.append("        c.cc_seqno, d.cc_seqno, e.cc_testgbn    								                 \n");
				strQuery.append("   from cmc0100 a, cmc0210 b, cmc0212 c, cmc0212 d, cmc0211 e, cmm0040 f                        \n");
				strQuery.append("  where a.cc_srid = ?                                                                           \n");
				strQuery.append("    and a.cc_srid = b.cc_srid                                                                   \n");
				strQuery.append("    and b.cc_caseseq = c.cc_caseseq                                                             \n");
				strQuery.append("    and b.cc_caseseq = d.cc_caseseq                                                             \n");
				strQuery.append("    and a.cc_srid = c.cc_srid                                                                   \n");
				strQuery.append("    and a.cc_srid = d.cc_srid                                                                   \n");
				strQuery.append("    and c.cc_gbncd = 'C' and c.cc_status <> '3'                                                 \n");
				strQuery.append("    and d.cc_gbncd = 'R' and d.cc_status <> '3'                                                 \n");
				strQuery.append("    and a.cc_srid = e.cc_srid and b.cc_caseseq = e.cc_caseseq                                   \n");
				strQuery.append("    and e.cc_status <> '3'                                                                      \n");
				strQuery.append("    and e.cc_testuser = f.cm_userid                                                             \n");
				strQuery.append("  order by cm_username, caseseq, e.cc_testgbn desc, c.cc_seqno, d.cc_seqno                      \n");
				
	            pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt = new LoggableStatement(conn,strQuery.toString());     
	            pstmt.setString(1, isridList.get(i).get("isrid"));
	    		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
	            rs = pstmt.executeQuery();
	            
	            caseseq = 0;
	            String cc_etc = "";
	            String cc_exprst = "";
	            String testgbn = "";
	            while (rs.next()){
	            	rst = new HashMap<String, String>();
	            	rst.put("cc_srid", rs.getString("cc_srid"));
	            	rst.put("testgbn", "통합테스트");
            		rst.put("cc_casename", rs.getString("cc_casename"));
            		
            		rst.put("cm_username", rs.getString("cm_username"));
            		rst.put("testday", rs.getString("testday"));
            		rst.put("testrst", rs.getString("testrst"));
	            	
            		
	        		if(caseseq == 0){
	            		rst.put("cc_etc", rs.getString("cc_etc"));
	            		rst.put("cc_exprst", rs.getString("cc_exprst"));
	            		cc_etc = rs.getString("cc_etc");
	            		cc_exprst = rs.getString("cc_exprst");
	            		
	            		if (rs.getBigDecimal("cc_worktime") != null){
	            			rst.put("cc_worktime", rs.getBigDecimal("cc_worktime").toString());
	            		}else{
	            			rst.put("cc_worktime", "");
	            		}
	            		caseseq = rs.getInt("caseseq");
	            		testgbn = rs.getString("cc_testgbn");
	            		
		        		strQuery.setLength(0);
		    			strQuery.append(" select count(*) cnt from cmc0101 where cc_srid = ? and cc_caseseq = ? and cc_division in ('54','55')  \n");
		    			pstmt2 = conn.prepareStatement(strQuery.toString());
		    			pstmt2.setString(1, isridList.get(i).get("isrid"));
		    			pstmt2.setString(2, rs.getString("caseseq"));
		                rs2 = pstmt2.executeQuery();
		                if (rs2.next()){
		                	if(rs2.getInt("cnt")>0){
		                		rst.put("file", "있음");
		                	}else{
		                		rst.put("file", "없음");
		                	}
		                }
		                rs2.close();
		                pstmt2.close();
	        		}else{
	        			if(caseseq == rs.getInt("caseseq") && testgbn.equals(rs.getString("cc_testgbn"))){
	                		rst.put("cc_worktime", "");
		        			rst.put("file", "");

		            		if("".equals(cc_etc)){
			            		rst.put("cc_etc", rs.getString("cc_etc"));
			            		cc_etc = rs.getString("cc_etc");
			        		}else{
			        			if(cc_etc.equals(rs.getString("cc_etc"))){
			                		rst.put("cc_etc", "");
			        			}else{
			                		rst.put("cc_etc", rs.getString("cc_etc"));
			                		cc_etc = rs.getString("cc_etc");
			        			}
			        		}
			        		if("".equals(cc_exprst)){
			            		rst.put("cc_exprst", rs.getString("cc_exprst"));
			            		cc_exprst = rs.getString("cc_exprst");
			        		}else{
			        			if(cc_exprst.equals(rs.getString("cc_exprst"))){
			                		rst.put("cc_exprst", "");
			        			}else{
			                		rst.put("cc_exprst", rs.getString("cc_exprst"));
			                		cc_exprst = rs.getString("cc_exprst");
			        			}
			        		}
		            		
	        			}else{
		            		rst.put("cc_etc", rs.getString("cc_etc"));
		            		cc_etc = rs.getString("cc_etc");
	                		rst.put("cc_exprst", rs.getString("cc_exprst"));
	                		cc_exprst = rs.getString("cc_exprst");
	                		
	                		if (rs.getBigDecimal("cc_worktime") != null){
	                			rst.put("cc_worktime", rs.getBigDecimal("cc_worktime").toString());
	                		}else{
	                			rst.put("cc_worktime", "");
	                		}
	                		caseseq = rs.getInt("caseseq");
	                		
    		        		strQuery.setLength(0);
    		    			strQuery.append(" select count(*) cnt from cmc0101 where cc_srid = ? and cc_caseseq = ? and cc_division in ('54','55')  \n");
    		    			pstmt2 = conn.prepareStatement(strQuery.toString());
    		    			pstmt2.setString(1, isridList.get(i).get("isrid"));
    		    			pstmt2.setString(2, rs.getString("caseseq"));
    		                rs2 = pstmt2.executeQuery();
    		                if (rs2.next()){
    		                	if(rs2.getInt("cnt")>0){
    		                		rst.put("file", "있음");
    		                	}else{
    		                		rst.put("file", "없음");
    		                	}
    		                }
    		                rs2.close();
    		                pstmt2.close();
	        			}
	        		}
	        		
		        	rtList.add(rst);
	        		rst = null;
	        		
				}
	            rs.close();
	            pstmt.close();
			}
			
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

            return rtList.toArray();

	    } catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1400.getTestDetail() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1400.getTestDetail() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1400.getTestDetail() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1400.getTestDetail() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1400.getTestDetail() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTestDetail() method statement
	
}//end of Cmp1400 class statement
