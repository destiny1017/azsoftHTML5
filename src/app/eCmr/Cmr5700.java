package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

//import app.common.AutoSeq;
//import app.common.LoggableStatement;
//import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr5700 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getPrjInfo(String txtPrjId,Boolean chkEnd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strSelMsg = "선택하세요";

			strQuery.setLength(0);
			if (chkEnd){
				strQuery.append("select pj_no cd_pjid,dev_chg_no cd_devno,chg_name cd_chgtit from vi_pms_chg_pfmc where \n");
			    if (txtPrjId != "" && txtPrjId != null){
			    	strQuery.append("PJ_NO like ? and \n");
			    }
			    strQuery.append("chg_ty='4' and \n");
			    strQuery.append("chg_stt in('1','2') \n");
			    strQuery.append("order by pj_no desc,dev_chg_no \n");
			}else{
				strQuery.append("select cd_pjid,cd_devno,cd_chgtit from CMD0110 \n");
			    if (txtPrjId != "" && txtPrjId != null){
			    	strQuery.append(" where cd_pjid like ? \n");
			    }
				strQuery.append("order by cd_pjid DESC,cd_devno \n");
			}

            pstmt = conn.prepareStatement(strQuery.toString());
   //     	pstmt = new LoggableStatement(conn, strQuery.toString());
            if (txtPrjId != "" && txtPrjId != null){
            	pstmt.setString(1, "%" + txtPrjId + "%");
            }
   //     	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
					   rst = new HashMap<String, String>();
					   rst.put("cd_pjid", "0000");
					   rst.put("cd_devno", "0");
					   rst.put("cd_chgtit", "");
					   rst.put("prjIdDevno", strSelMsg);
					   rst.put("cboPrjnoLabel", strSelMsg);
					   rtList.add(rst);
					   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cd_pjid", rs.getString("cd_pjid"));
				rst.put("cd_devno", rs.getString("cd_devno"));
				rst.put("cd_chgtit", rs.getString("cd_chgtit"));
				rst.put("prjIdDevno", rs.getString("cd_pjid") + "-" + rs.getString("cd_devno"));
				rst.put("cboPrjnoLabel", rs.getString("cd_pjid") + "-" + rs.getString("cd_devno") + " " + rs.getString("cd_chgtit"));
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

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getPrjInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5700.getPrjInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getPrjInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5700.getPrjInfo() Exception END ##");
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
					ecamsLogger.error("## Cmr5700.getPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getReqPrj(String txtPrjId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

		    if (txtPrjId != "" && txtPrjId != null){

				conn = connectionContext.getConnection();

		    	strQuery.setLength(0);
		        strQuery.append("select a.cd_chgcd,a.cd_chgsta,a.cd_reqdept,a.cd_chgdept,a.cd_chgtit,a.cd_chgdet,b.cm_codename \n");
		        strQuery.append("  from cmm0020 b,cmd0110 a \n");
		        strQuery.append(" where a.cd_pjid=? \n");
		        strQuery.append("   and a.cd_devno=? \n");
		        strQuery.append("   and b.cm_macode='REQSAYU' \n");
		        strQuery.append("   and a.cd_chgsayu=b.cm_micode \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
          //  	pstmt = new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, txtPrjId.substring(0,13));
	            pstmt.setString(2, txtPrjId.substring(14));
         //   	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				if (rs.next()){
					rst = new HashMap<String, String>();
					if (rs.getString("cd_chgcd").equals("1") || rs.getString("cd_chgcd").equals("2")){
						rst.put("cd_chgcd", "정상변경");
					}else if (rs.getString("cd_chgcd").equals("3")){
						rst.put("cd_chgcd", "단순변경");
					}else {
						rst.put("cd_chgcd", "비상변경");
					}

					if (rs.getString("cd_chgsta").equals("1")){
						rst.put("cd_chgsta", "승인전");
					}else if (rs.getString("cd_chgsta").equals("2")){
						rst.put("cd_chgsta", "승인");
					}else if (rs.getString("cd_chgsta").equals("3")){
						rst.put("cd_chgsta", "완료");
					}else rst.put("cd_chgsta", "");

					rst.put("cm_codename", rs.getString("cm_codename"));

					if (rs.getString("cd_reqdept") != null){
						rst.put("cd_reqdept", rs.getString("cd_reqdept"));
					}
					if (rs.getString("cd_chgdept") != null){
						rst.put("cd_chgdept", rs.getString("cd_chgdept"));
					}
					if (rs.getString("cd_chgtit") != null){
						rst.put("cd_chgtit", rs.getString("cd_chgtit"));
					}
					if (rs.getString("cd_chgdet") != null){
						rst.put("cd_chgdet", rs.getString("cd_chgdet"));
					}
					rtList.add(rst);
					rst = null;
				}else{
					rs.close();
					pstmt.close();

					strQuery.setLength(0);
			        strQuery.append("select a.chg_ty cd_chgcd,a.chg_stt cd_chgsta,a.wso_dmd_orgn_name cd_reqdept, \n");
			        strQuery.append("       a.chg_dmd_orgn_name cd_chgdept,a.chg_name cd_chgtit,a.chg_cnt cd_chgdet,b.cm_codename \n");
			        strQuery.append("  from cmm0020 b,vi_pms_chg_pfmc a \n");
		            strQuery.append(" where a.pj_no = ? \n");
		            strQuery.append("   and a.dev_chg_no = ? \n");
		            strQuery.append("   and b.cm_macode='REQSAYU' \n");
		            strQuery.append("   and a.chg_rsn=b.cm_micode \n");

		            pstmt = conn.prepareStatement(strQuery.toString());
       //         	pstmt = new LoggableStatement(conn, strQuery.toString());
		            pstmt.setString(1, txtPrjId.substring(0,13));
		            pstmt.setString(2, txtPrjId.substring(14));
         //       	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();

					if (rs.next()){
						rst = new HashMap<String, String>();
						if (rs.getString("cd_chgcd").equals("1") || rs.getString("cd_chgcd").equals("2")){
							rst.put("cd_chgcd", "정상변경");
						}else if (rs.getString("cd_chgcd").equals("3")){
							rst.put("cd_chgcd", "단순변경");
						}else {
							rst.put("cd_chgcd", "비상변경");
						}

						if (rs.getString("cd_chgsta").equals("1")){
							rst.put("cd_chgsta", "승인전");
						}else if (rs.getString("cd_chgsta").equals("2")){
							rst.put("cd_chgsta", "승인");
						}else if (rs.getString("cd_chgsta").equals("3")){
							rst.put("cd_chgsta", "완료");
						}else rst.put("cd_chgsta", "");

						rst.put("cm_codename", rs.getString("cm_codename"));

						if (rs.getString("cd_reqdept") != null){
							rst.put("cd_reqdept", rs.getString("cd_reqdept"));
						}else rst.put("cd_reqdept", "");

						if (rs.getString("cd_chgdept") != null){
							rst.put("cd_chgdept", rs.getString("cd_chgdept"));
						}else rst.put("cd_chgdept", "");

						if (rs.getString("cd_chgtit") != null){
							rst.put("cd_chgtit", rs.getString("cd_chgtit"));
						}else rst.put("cd_chgtit", "");

						if (rs.getString("cd_chgdet") != null){
							rst.put("cd_chgdet", rs.getString("cd_chgdet"));
						}else rst.put("cd_chgdet", "");

						rtList.add(rst);
						rst = null;
		            }
					rs.close();
					pstmt.close();
				}
				rs.close();
				pstmt.close();
	            conn.close();
	    	}
            rs = null;
            pstmt = null;
            conn = null;

    		return rtList.toArray();

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getReqPrj() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5700.getReqPrj() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getReqPrj() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5700.getReqPrj() Exception END ##");
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
					ecamsLogger.error("## Cmr5700.getReqPrj() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getPrjList(String txtPrjId,String AcptNo,Boolean chkEnd,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();


		    if (chkEnd){
		    	strQuery.setLength(0);
		    	strQuery.append("select g.cr_acPTno,c.cm_codename req,f.cm_codename sta, \n");
				strQuery.append("       to_char(g.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') cr_acptdate, \n");
				strQuery.append("       to_char(g.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') cr_prcdate, \n");
    			strQuery.append("       g.cr_sayu,e.cm_sysmsg,B.CM_USERNAME \n");
    			strQuery.append("from cmm0030 e,cmr1000 g,cmm0040 b, \n");
    			strQuery.append("(select cm_micode,cm_codename from cmm0020 where cm_macode='REQUEST') c, \n");
    			strQuery.append("(select cm_micode,cm_codename from cmm0020 where cm_macode='CMR1000') f \n");
    			if (AcptNo != "" && AcptNo != null){
    			   strQuery.append("where g.cr_acptno=? \n");
    			} else {
    			   strQuery.append("where g.cr_editor=? \n");
    			   strQuery.append("  and g.cr_qrycd='04' \n");
    			   strQuery.append("  and g.cr_status<>'3' \n");
    			   strQuery.append("  and g.cr_emgcd in ('03','04') \n");
    			   strQuery.append("  and g.cr_prjno is null \n");
    			}
    			strQuery.append("  and c.cm_micode=g.cr_qrycd \n");
    			strQuery.append("  and f.cm_micode=g.cr_status \n");
    			strQuery.append("  and g.cr_editor=b.cm_userid \n");
    			strQuery.append("  and g.cr_syscd=e.cm_syscd \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
    //        	pstmt = new LoggableStatement(conn, strQuery.toString());
    			if (AcptNo != "" && AcptNo != null){
    				pstmt.setString(1, AcptNo);
    			}else{
    				pstmt.setString(1, UserId);
    			}
         //   	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("cr_acptno", rs.getString("cr_acptno"));
					rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
					rst.put("req", rs.getString("req"));
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("sta", rs.getString("sta"));
					rst.put("cr_acptdate", rs.getString("cr_acptdate"));
					rst.put("cr_prcdate", rs.getString("cr_prcdate"));
					rst.put("cr_sayu", rs.getString("cr_sayu"));
					rst.put("selected", "0");
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
	    	}else{
	    		strQuery.append("select g.cr_acPTno,c.cm_codename req,d.cm_dirpath,g.cr_rsrcname,b.cm_username, \n");
				strQuery.append("f.cm_codename sta,h.cr_acptdate,g.cr_prcdate,e.cm_sysmsg \n");
				strQuery.append("from cmm0070 d,cmm0030 e,cmr1000 h,cmr1010 g,cmm0040 b,cmd0111 a,\n");
				strQuery.append("(select cm_micode,cm_codename from cmm0020 where cm_macode='REQUEST') c, \n");
				strQuery.append("(select cm_micode,cm_codename from cmm0020 where cm_macode='CMR1000') f \n");
				strQuery.append("where a.cd_pjid=? \n");
				strQuery.append("and a.cd_devno=? \n");
				strQuery.append("and a.cd_acptno=h.cr_acptno \n");
				strQuery.append("and a.cd_syscd=g.cr_syscd and a.cd_dsncd=g.cr_dsncd and a.cd_rsrcname=g.cr_rsrcname \n");
				strQuery.append("and g.cr_acptno=h.cr_acptno \n");
				strQuery.append("and c.cm_micode=h.cr_qrycd \n");
				strQuery.append("and f.cm_micode=h.cr_status \n");
				strQuery.append("and g.cr_syscd=d.cm_syscd and g.cr_dsncd=d.cm_dsncd \n");
				strQuery.append("and g.cr_editor=b.cm_userid \n");
				strQuery.append("and g.cr_syscd=e.cm_syscd \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
   //         	pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, txtPrjId.substring(0,13));
				pstmt.setString(2, txtPrjId.substring(14));
      //      	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("cr_acptno", rs.getString("cr_acptno"));
					rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
					rst.put("req", rs.getString("req"));
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("sta", rs.getString("sta"));
					rst.put("cr_acptdate", rs.getString("cr_acptdate"));
					rst.put("cr_prcdate", rs.getString("cr_prcdate"));
					rst.put("selected", "0");
					rtList.add(rst);
					rst = null;
				}
	    	}
			rs.close();
			pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

    		return rtList.toArray();

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getPrjList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5700.getPrjList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5700.getPrjList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5700.getPrjList() Exception END ##");
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
					ecamsLogger.error("## Cmr5700.getPrjList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	public String setPrjInfo(ArrayList<HashMap<String,String>> projectList,String txtPrjId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strPrjId    = txtPrjId.substring(0, 13);
		int               strDevNo    = Integer.parseInt(txtPrjId.substring(14));

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);


			strQuery.setLength(0);
			strQuery.append("select * from cmd0110 \n");
			strQuery.append(" where cd_pjid=? \n");
			strQuery.append("   and cd_devno=? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	//		pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, strPrjId);
			pstmt.setInt(2, strDevNo);
	//		ecamsLogger.error(new String((((LoggableStatement)pstmt).getQueryString())));
			rs = pstmt.executeQuery();
			if (rs.next()) {
			} else {
				rs.close();
				pstmt.close();

    			strQuery.setLength(0);
            	strQuery.append("insert into CMD0110 (CD_PJID,CD_DEVNO,CD_REQDEPT,CD_CHGDEPT,CD_CHGUPDPT,CD_CREATOR, \n");
            	strQuery.append("                      CD_CHGCD,CD_CHGSAYU,CD_CHGTIT,CD_CHGDET,CD_CHGSTA) \n");
            	strQuery.append("(select pj_no,dev_chg_no,wso_dmd_orgn_name,chg_dmd_orgn_name,chg_dmd_up_orgn_name, \n");
            	strQuery.append("        mpr_no,chg_ty,chg_rsn,chg_name,chg_cnt,chg_stt from vi_pms_chg_pfmc \n");
            	strQuery.append("  where pj_no=? and  dev_chg_no=?) \n");

            	pstmt = conn.prepareStatement(strQuery.toString());
      //      	pstmt = new LoggableStatement(conn, strQuery.toString());
            	pstmt.setString(1, strPrjId);
    			pstmt.setInt(2, strDevNo);
    //        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();

            	pstmt.close();
        	}
			rs.close();
			pstmt.close();

        	int i=0;
			for (i=0 ; i<projectList.size() ; i++){

        		strQuery.setLength(0);
            	strQuery.append("insert into cmd0111 (CD_PJID,CD_DEVNO,CD_ACPTNO,CD_SYSCD,CD_DSNCD,CD_RSRCNAME) ");
            	strQuery.append("(select '"+strPrjId+"', "+strDevNo+", \n");
            	strQuery.append("        CR_ACPTNO,CR_SYSCD,CR_DSNCD,CR_RSRCNAME \n");
            	strQuery.append("   from CMR1010 \n");
            	strQuery.append("  where CR_acptno=?) \n");

            	pstmt = conn.prepareStatement(strQuery.toString());
       //     	pstmt = new LoggableStatement(conn, strQuery.toString());
            	pstmt.setString(1, projectList.get(i).get("cr_acptno"));
      //      	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();


            	strQuery.setLength(0);
            	strQuery.append("update CMR1000 set CR_prjno='"+strPrjId+", \n");
            	strQuery.append("                   CR_devno="+strDevNo+", \n");
            	strQuery.append(" where cr_acptno=? \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
        //    	pstmt = new LoggableStatement(conn, strQuery.toString());
            	pstmt.setString(1, projectList.get(i).get("cr_acptno"));
        //    	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
        	}
        	conn.commit();
        	conn.close();

        	rs = null;
    		pstmt = null;
    		conn = null;


        	return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5700.setPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr5700.setPrjInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5700.setPrjInfo() SQLException END ##");
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
					ecamsLogger.error("## Cmr5700.setPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr5700.setPrjInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5700.setPrjInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5700.setPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}

