package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmm0403 {

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] all_sign_up(ArrayList<HashMap<String,String>> rtList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String,String>> rsval = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> rst = null;

		String imsiUser = ""; //엑셀에서 가져온 ID
		boolean check = false; // 중복체크 true면 동기화여부체크해서 skip 하거나 update , false면 insert
		String imsiPosition = ""; //직위
		String imsiDuty = ""; //직급
		String imsiCodename = ""; //담당직무
		String imsiJobcd = ""; //업무
		String imsiSyscd = ""; //시스템
		String imsiUpperProject = ""; //소속조직(상위/본부) 
		String imsiProject = ""; //소속조직
		String imsiHandrun = "";//동기화 여부
		String imsiEmail = ""; //이메일주소
		String imsiTelno1 = ""; //전화번호
		String imsiTelno2 = ""; //핸드폰번호
    	//String imsiAdmin = ""; //관리자 여부
    	//String imsiManid = ""; // 직원여부
		
    	int count = 0; //에러체크
    	int j = 0;
		int c = 0;
		int k =0;
    	int codenamecheck = 0;
    	String code= "";
		String[] result =  null;
		String er = "";
		StringBuffer error = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{

			conn = connectionContext.getConnection();
			//conn.setAutoCommit(false);

			for(int i=0; i<rtList.size(); i++)		//사용자리스트 만큼 for문
			{
				conn.setAutoCommit(false);
				
				imsiUser = ""; // ID
				check = false; // 중복체크 true면 동기화여부체크해서 skip 하거나 update , false면 insert
				imsiPosition = ""; //직위
				imsiDuty = ""; //직급
				imsiCodename = ""; //담당직무
				imsiJobcd = ""; //업무
				imsiSyscd = ""; //시스템
				imsiProject = ""; //소속조직
				imsiHandrun = "";//동기화 여부
		    	//imsiAdmin = ""; //관리자 여부
		    	//imsiManid = ""; // 직원여부
		    	count = 0;
		        j = 0;
				c = 0;
				k =0;
		    	codenamecheck = 0;
		    	//초기화작업 끝


		    	if(rtList.get(i).get("CM_EMAIL") == null || rtList.get(i).get("CM_EMAIL") == ""){
		    		imsiEmail = "";
		    	}else{
		    		imsiEmail = rtList.get(i).get("CM_EMAIL").toString().trim();
		    	}

		    	if(rtList.get(i).get("CM_TELNO1") == null || rtList.get(i).get("CM_TELNO1") == ""){
		    		imsiTelno1 = "";
		    	}else{
		    		imsiTelno1 = rtList.get(i).get("CM_TELNO1").toString().trim();
		    	}

		    	if(rtList.get(i).get("CM_TELNO2") == null || rtList.get(i).get("CM_TELNO2") == ""){
		    		imsiTelno2 = "";
		    	}else{
		    		imsiTelno2 = rtList.get(i).get("CM_TELNO2").toString().trim();
		    	}

		    	if( (rtList.get(i).get("CM_SYSCD") == null) || rtList.get(i).get("CM_SYSCD").equals("") ){
		    		imsiSyscd = "";
		    	}else{
		    		imsiSyscd = rtList.get(i).get("CM_SYSCD").toString().trim();
		    	}
		    	


				//USERID 존재하는지 40 테이블 조회 그리고 동기화제외 대상여부 조회
				strQuery.setLength(0);
				strQuery.append("select cm_userid,cm_handrun,cm_admin \n");
				strQuery.append(" from cmm0040 \n");
				strQuery.append("where cm_userid = ? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, rtList.get(i).get("CM_USERID").toString().trim());
				rs = pstmt.executeQuery();
				if(rs.next()){//기존사용자일 경우
					check = true;
					imsiUser = rs.getString("cm_userid");
					imsiHandrun = rs.getString("cm_handrun");

//					if (imsiHandrun.equals("N") && rtList.get(i).get("CM_ADMIN").toString().trim().equals("1")) {
//						imsiAdmin = "1";
//					} else {
//						imsiAdmin =  "0";
//					}
				}else{//존재하지 않을경우
					imsiUser = rtList.get(i).get("CM_USERID").toString().trim();
					imsiHandrun = "N";
					//imsiAdmin = rtList.get(i).get("CM_ADMIN").toString().trim();
				}


				rst = new HashMap<String, String>();
				//동기화제외 대상일 경우는 바로 skip
				if (imsiHandrun.equals("N")) {//사용자가 존재하지 않을경우 또는 동기화 대상 일때

					rst.put("CM_USERID", imsiUser);
					rst.put("CM_USERNAME", rtList.get(i).get("CM_USERNAME") == null ? "" : rtList.get(i).get("CM_USERNAME").toString().trim());
					rst.put("CM_TELNO1", imsiTelno1);
					rst.put("CM_TELNO2", imsiTelno2);
					rst.put("CM_EMAIL", imsiEmail);
					rst.put("CM_SYSCD", imsiSyscd);

					rs.close();
					pstmt.close();
					
					strQuery.setLength(0);
					strQuery.append("select cm_micode \n");
					strQuery.append("  from cmm0020 \n");
					strQuery.append(" where cm_macode='POSITION'   \n");
					strQuery.append("   and cm_codename = upper(?) \n");
					
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, rtList.get(i).get("CM_POSITION").toString().trim() );//엑셀 직위값
					
					rs = pstmt.executeQuery();
					
					if (rs.next()){
						imsiPosition = rs.getString("cm_micode");
						rst.put("CM_POSITION", rtList.get(i).get("CM_POSITION") == null ? "" : rtList.get(i).get("CM_POSITION").toString().trim());
					}else{
						rst.put("CM_POSITION", rtList.get(i).get("CM_POSITION") == null ? "" : rtList.get(i).get("CM_POSITION").toString().trim() + "[Error]");
						count++;
					}
					
					rs.close();
					pstmt.close();
					
					imsiDuty = "99"; //직원으로 고정

					/*					
					if(rtList.get(i).get("CM_CODENAME") != null && rtList.get(i).get("CM_CODENAME") != ""){//엑셀 직무값
						code = rtList.get(i).get("CM_CODENAME").toString().trim();
						result = code.split(",");

						for(j=0; j<result.length; j++){
//							if(result[j].equals("개발담당자") || result[j].equals("파트장") || result[j].equals("팀장")){
//								codenamecheck++;
//							}
							rs.close();
							pstmt.close();
							strQuery.setLength(0);
							strQuery.append("select cm_micode        \n");
							strQuery.append("  from cmm0020          \n");
							strQuery.append(" where cm_macode='DUTY' \n");
							strQuery.append("   and cm_codename = upper(?)  \n");
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, result[j]);
							rs = pstmt.executeQuery();
							if (rs.next()){
								codenamecheck++;
							}
						}
					} else{
						count ++;
					}

					if(codenamecheck == 0){
						count++;
					}else{//엑셀의 직무값이 유효할때
						rs.close();
						pstmt.close();
					    strQuery.setLength(0);
						strQuery.append("select min(cm_micode) as cm_micode  \n");
						strQuery.append("  from cmm0020 \n");
						strQuery.append(" where cm_macode='DUTY'   \n");

						strQuery.append(" and cm_codename in ( \n");
						if (result.length == 1)
							strQuery.append(" upper(?) ");
						else{
							for (j=0;j<result.length;j++){
								if (j == result.length-1)
									strQuery.append(" upper(?) ");
								else
									strQuery.append(" upper(?) ,");
							}
						}
						strQuery.append(" ) \n");

						//strQuery.append("   and cm_codename = ?     \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						for (j=0;j<result.length;j++){
							pstmt.setString(j+1, result[j]);
						}
						rs = pstmt.executeQuery();

						if (rs.next()){
							imsiDuty = rs.getString("cm_micode");
						}
					}

					rs.close();
					pstmt.close();
					*/
					
					//본부 및 소속 부서 조회
					if( (rtList.get(i).get("CM_UPPERPROJECT") != null) && !rtList.get(i).get("CM_UPPERPROJECT").equals("") ){//엑셀 본부값
						strQuery.setLength(0);
						strQuery.append("SELECT		cm_deptcd			\n");
						strQuery.append("FROM		cmm0100				\n");
						strQuery.append("WHERE		cm_deptname = ?		\n");
						
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, rtList.get(i).get("CM_UPPERPROJECT").toString().trim() );
						rs = pstmt.executeQuery();
						
						if ( rs.next() ){ //입력한 본부 값이 db에 정보가 있으면
							imsiUpperProject = rs.getString("cm_deptcd");
						}else{ //입력한 본부 값이 db에 정보가 없으면 신규 등록
							strQuery.setLength(0);
							strQuery.append("SELECT	'HAND' || LPAD(TO_NUMBER(NVL(MAX(SUBSTR(cm_deptcd,5,9)),0)) + 1,5,'0') AS cm_deptcd		\n");	
							strQuery.append("FROM	cmm0100									\n");
							strQuery.append("WHERE	SUBSTR(cm_deptcd,1,4) ='HAND'			\n");
							
							PreparedStatement pstmt2 = conn.prepareStatement(strQuery.toString());
							ResultSet rs2 = pstmt2.executeQuery();
							
							if( rs2.next() ) {
								imsiUpperProject = rs2.getString("cm_deptcd");
							}
							
							rs2.close();
							pstmt2.close();
							
				        	strQuery.setLength(0);
				        	strQuery.append("INSERT	INTO cmm0100 (CM_DEPTCD,CM_DEPTNAME,CM_UPDEPTCD,CM_USEYN,CM_HANDYN)		\n");
				        	strQuery.append("VALUES (?, ?, (SELECT MIN(cm_deptcd) FROM		cmm0100), 'Y', 'Y')				\n");
				        	pstmt2 = conn.prepareStatement(strQuery.toString());
				        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
				        	pstmt2.setString(1, imsiUpperProject);
				        	pstmt2.setString(2, rtList.get(i).get("CM_UPPERPROJECT").toString().trim());
				        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				        	pstmt2.executeUpdate();
				        	pstmt2.close();
						}
						
						rs.close();
						pstmt.close();
						
						//소속부서 조회
						if( (rtList.get(i).get("CM_PROJECT") != null) && !rtList.get(i).get("CM_PROJECT").equals("") ){
							strQuery.setLength(0);
							strQuery.append("SELECT	cm_deptcd				\n");
							strQuery.append("FROM	cmm0100					\n");
							strQuery.append("WHERE	cm_deptname = ?			\n");
							strQuery.append("AND	cm_updeptcd = ?			\n");
							
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, rtList.get(i).get("CM_PROJECT").toString().trim() );
							pstmt.setString(2, imsiUpperProject );
							rs = pstmt.executeQuery();
							
							if( rs.next() ) { //부서정보가 있으면
								imsiProject = rs.getString("cm_deptcd");
							} else { //부서정보가 없으면
								strQuery.setLength(0);
								strQuery.append("SELECT	'HAND' || LPAD(TO_NUMBER(NVL(MAX(SUBSTR(cm_deptcd,5,9)),0)) + 1,5,'0') AS cm_deptcd		\n");	
								strQuery.append("FROM	cmm0100									\n");
								strQuery.append("WHERE	SUBSTR(cm_deptcd,1,4) ='HAND'			\n");
								
								PreparedStatement pstmt2 = conn.prepareStatement(strQuery.toString());
								ResultSet rs2 = pstmt2.executeQuery();
								
								if( rs2.next() ) {
									imsiProject = rs2.getString("cm_deptcd");
								}
								
								rs2.close();
								pstmt2.close();
								
					        	strQuery.setLength(0);
					        	strQuery.append("INSERT	INTO cmm0100 (CM_DEPTCD,CM_DEPTNAME,CM_UPDEPTCD,CM_USEYN,CM_HANDYN)		\n");
					        	strQuery.append("VALUES (?, ?, ?, 'Y', 'Y')													\n");
					        	pstmt2 = conn.prepareStatement(strQuery.toString());
					        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
					        	pstmt2.setString(1, imsiProject);
					        	pstmt2.setString(2, rtList.get(i).get("CM_PROJECT").toString().trim());
					        	pstmt2.setString(3, imsiUpperProject);
					        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					        	pstmt2.executeUpdate();
					        	pstmt2.close();
							}
							
//							rst.put("CM_UPPERPROJECT", rtList.get(i).get("CM_UPPERPROJECT") == null ? "" : rtList.get(i).get("CM_UPPERPROJECT").toString().trim());
//							rst.put("CM_PROJECT", rtList.get(i).get("CM_PROJECT") == null ? "" : rtList.get(i).get("CM_PROJECT").toString().trim());
							
							rs.close();
							pstmt.close();
						} else {
							imsiProject = imsiUpperProject;
//							rst.put("CM_UPPERPROJECT", rtList.get(i).get("CM_UPPERPROJECT") == null ? "" : rtList.get(i).get("CM_UPPERPROJECT").toString().trim());
//							rst.put("CM_PROJECT", rtList.get(i).get("CM_PROJECT") == null ? "" : rtList.get(i).get("CM_PROJECT").toString().trim() + "[Error]");
//							count++;
						}
						rst.put("CM_UPPERPROJECT", rtList.get(i).get("CM_UPPERPROJECT") == null ? "" : rtList.get(i).get("CM_UPPERPROJECT").toString().trim());
						rst.put("CM_PROJECT", rtList.get(i).get("CM_PROJECT") == null ? "" : rtList.get(i).get("CM_PROJECT").toString().trim());
					} else { //엑셀에 본부값이 없으면 기본값을 가져옴.
						strQuery.setLength(0);
						strQuery.append("SELECT		MIN(cm_deptcd) AS cm_deptcd		\n");
						strQuery.append("FROM		cmm0100							\n");
						
						pstmt = conn.prepareStatement(strQuery.toString());
						
						rs = pstmt.executeQuery();
						
						if ( rs.next() ){
							imsiProject = rs.getString("cm_deptcd");
							rst.put("CM_UPPERPROJECT", rtList.get(i).get("CM_UPPERPROJECT") == null ? "" : rtList.get(i).get("CM_UPPERPROJECT").toString().trim());
							rst.put("CM_PROJECT", rtList.get(i).get("CM_PROJECT") == null ? "" : rtList.get(i).get("CM_PROJECT").toString().trim());
						}
						
						pstmt.close();
						rs.close();
					}

					/*
					strQuery.setLength(0);
					strQuery.append("select cm_deptcd  \n");
					strQuery.append("  from cmm0100  \n");
					strQuery.append(" where cm_deptname = ?   \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, rtList.get(i).get("CM_PROJECT").toString().trim() );
					rs = pstmt.executeQuery();
					if (rs.next()){
						imsiProject = rs.getString("cm_deptcd");
						rst.put("CM_PROJECT", rtList.get(i).get("CM_PROJECT").toString().trim());
					}else{
						rst.put("CM_PROJECT",  rtList.get(i).get("CM_PROJECT").toString().trim() + "[Error]");
						 count++;
					}
					*/
					
//					if(rtList.get(i).get("CM_MANID").toString().trim().equals("y") || rtList.get(i).get("CM_MANID").toString().trim().equals("Y") ){
//						imsiManid = "Y";
//						rst.put("CM_MANID", imsiManid);
//					}else if(rtList.get(i).get("CM_MANID").toString().trim().equals("n") || rtList.get(i).get("CM_MANID").toString().trim().equals("N")){
//						imsiManid = "N";
//						rst.put("CM_MANID", imsiManid);
//					}else{
//						rst.put("CM_MANID", rtList.get(i).get("CM_MANID").toString().trim() + "[Error]");
//						count++;
//					}
//
//					if(imsiAdmin.equals("1") || imsiAdmin.equals("0")){
//						rst.put("CM_ADMIN", imsiAdmin);
//					}else{
//						imsiAdmin = "0";
//						rst.put("CM_ADMIN", rtList.get(i).get("CM_ADMIN").toString().trim() + "[Error]");
//					    count++;
//					}
					rst.put("colorsw", "0");

					//count == 0 일 경우 시작/////////////////////////////////////////////////////////////////////////////////////////////////////
					if(count == 0){//엑셀 입력값이 오류가 없을때
						if(check){//userid 존재 할때 = 기존에 사용자가 DB에 저장되어 있는 경우
							pstmt.close();
							rs.close();
							strQuery.setLength(0);
							strQuery.append("update cmm0040 	\n");
							strQuery.append("set cm_username = ?, cm_project = ?, cm_position = ?, cm_duty = ?, cm_email = ?, cm_telno1 = ?, cm_telno2 = ?, cm_manid = 'Y', cm_admin = '0'  	\n");
							strQuery.append("where cm_userid = ?");
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, rtList.get(i).get("CM_USERNAME").toString().trim());
							pstmt.setString(2, imsiProject.trim());
							pstmt.setString(3, imsiPosition.trim());
							pstmt.setString(4, imsiDuty.trim());
							pstmt.setString(5, imsiEmail);
							pstmt.setString(6, imsiTelno1);
							pstmt.setString(7, imsiTelno2);
							pstmt.setString(8, imsiUser);
//							pstmt.setString(8, imsiManid);
//							pstmt.setString(9, imsiAdmin);
//							pstmt.setString(10, imsiUser);
							pstmt.executeUpdate();

						}
						else{//신규 사용자 DB insert
							pstmt.close();
							rs.close();
							strQuery.setLength(0);
							strQuery.append("insert into cmm0040(cm_userid, cm_username, cm_project, cm_position, cm_duty, cm_email, cm_telno1, cm_telno2, cm_manid, cm_admin, cm_dumypw, cm_juminnum, cm_handrun, cm_status)   	\n");
							strQuery.append(" values(?,?,?,?,?,?,?,?,'Y','0','1234','1234','N','0')");
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, imsiUser);
							pstmt.setString(2, rtList.get(i).get("CM_USERNAME").toString().trim());
							pstmt.setString(3, imsiProject.trim());
							pstmt.setString(4, imsiPosition.trim());
							pstmt.setString(5, imsiDuty.trim());
							pstmt.setString(6, imsiEmail);
							pstmt.setString(7, imsiTelno1);
							pstmt.setString(8, imsiTelno2);
//						    pstmt.setString(9, imsiManid);
//						    pstmt.setString(10, imsiAdmin);
						    pstmt.executeUpdate();
						}
						//40 수정 완료


						//43 수정 시작
					//////////////////////////////////////////////////////////////////////cmm0043
						//시스템 코드 가져오기
				    	if( (rtList.get(i).get("CM_SYSCD") == null) || rtList.get(i).get("CM_SYSCD").equals("") ){
				    		imsiSyscd = "";
				    	} else {
							strQuery.setLength(0);
							strQuery.append("SELECT	cm_syscd		\n");
							strQuery.append("FROM	cmm0030			\n");
							strQuery.append("WHERE	cm_sysmsg = ?	\n");
							
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, rtList.get(i).get("CM_SYSCD").toString().trim());
							rs = pstmt.executeQuery();
							if(rs.next()){
								imsiSyscd = rs.getString("cm_syscd");
							} else {
								count++;
								rst.put("colorsw", "5");
								rst.put("CM_SYSCD", imsiSyscd + "[Error]");
								imsiSyscd = "";
							}
							
							rs.close();
							pstmt.close();
				    	}						
						
						if(rtList.get(i).get("CM_CODENAME") != null && rtList.get(i).get("CM_CODENAME") != ""){
							code= rtList.get(i).get("CM_CODENAME").toString().trim();
							result = code.split(",");
							er = "";
							pstmt.close();
							rs.close();
							strQuery.setLength(0);
							strQuery.append("delete from cmm0043  	\n");
							strQuery.append("where cm_userid = ?");
							pstmt = conn.prepareStatement(strQuery.toString());
							pstmt.setString(1, imsiUser);
							pstmt.executeUpdate();

							for(j=0; j<result.length; j++){
								pstmt.close();
								rs.close();
								strQuery.setLength(0);
								strQuery.append("select cm_micode	 	  \n");
								strQuery.append("  from cmm0020 		  \n");
								strQuery.append(" where cm_macode='RGTCD' \n");
								strQuery.append("   and cm_codename = upper(?)\n");
								pstmt = conn.prepareStatement(strQuery.toString());
								pstmt.setString(1, result[j]);
								rs = pstmt.executeQuery();

								if (rs.next()){
									imsiCodename = rs.getString("cm_micode");
									if (er.length() == 0) er = result[j];
									else er = er + "," + result[j];
									rst.put("CM_CODENAME", er);
								}else{
									if (er.length() == 0) er = result[j]+"[Error]";
									else er = er + "," +  result[j]+"[Error]";
									count++;
									rst.put("CM_CODENAME", er);
								}

								if (count == 0){
									pstmt.close();
									rs.close();
									strQuery.setLength(0);
									strQuery.append("insert into cmm0043(cm_userid, cm_rgtcd, cm_creatdt, cm_lastdt)   	\n");
									strQuery.append(" values(?,?,sysdate,sysdate)");
									pstmt = conn.prepareStatement(strQuery.toString());
									pstmt.setString(1, imsiUser);
									pstmt.setString(2, imsiCodename);
									pstmt.executeUpdate();
								}else{
									rst.put("colorsw", "4");//43 테이블 입력 실패 green
								}
							}//result.length for end

	//							pstmt.close();
	//							rs.close();
	//							strQuery.setLength(0);
	//							strQuery.append("select cm_rgtcd 	 \n");
	//							strQuery.append("from cmm0043 	 \n");
	//							strQuery.append("where cm_userid = ? 	\n");
	//							strQuery.append("and cm_rgtcd = '74' 	\n");
	//							pstmt = conn.prepareStatement(strQuery.toString());
	//							pstmt.setString(1, imsiUser);
	//							rs = pstmt.executeQuery();
	//							if (rs.next()){
	//							}
	//							else if (imsiAdmin.equals("1")) {
	//								pstmt.close();
	//								rs.close();
	//								strQuery.setLength(0);
	//								strQuery.append("insert into cmm0043(cm_userid, cm_rgtcd, cm_creatdt, cm_lastdt)   	\n");
	//								strQuery.append("values(?, '74', sysdate, sysdate)");
	//								pstmt = conn.prepareStatement(strQuery.toString());
	//								pstmt.setString(1, imsiUser);
	//								pstmt.executeUpdate();
	//							}
							}// 43 수정완료


							count = 0;

						//44 수정 시작
						//////////////////////////////////////////////////////////////////// //cmm0044
						if(rtList.get(i).get("CM_JOBNAME") != null && rtList.get(i).get("CM_JOBNAME") != ""){
							result = rtList.get(i).get("CM_JOBNAME").toString().trim().split(",");
							error = new StringBuffer();
							for(k=0; k<result.length; k++){
								imsiJobcd = "";

								pstmt.close();
								rs.close();
								strQuery.setLength(0);
								strQuery.append("select cm_jobcd  \n");
								strQuery.append("  from cmm0102  \n");
								strQuery.append(" where (cm_jobcd = ? or cm_jobname = ?)   \n");
								pstmt = conn.prepareStatement(strQuery.toString());
								//pstmt = new LoggableStatement(conn,strQuery.toString());
								pstmt.setString(1, result[k].trim());
								pstmt.setString(2, result[k].trim());
								//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
								rs = pstmt.executeQuery();
								if (rs.next()){
									imsiJobcd = rs.getString("cm_jobcd");
									c = 1;
									if(k == 0){
										error.append(result[k]);
									}else{
										error.append(","+ result[k]);
									}
								}else{
									c = 2;
									if(k == 0){
										error.append(result[k] + "[Error]");

										if(rst.get("colorsw").equals("4") ) rst.put("colorsw", "6");//43 44 테이블 입력 실패 cyan
										else rst.put("colorsw", "5");//44 테이블 입력 실패 magenta

									} else{
										error.append(","+ result[k] + "[Error]");
									}
									count++;
								}
								if(c == 1){
									//cmm0034 조회해서 cm_syscd를 가져옴
									pstmt.close();
									rs.close();
									if( (imsiSyscd == null) || imsiSyscd.equals("") ) {
										strQuery.setLength(0);
										strQuery.append("select cm_syscd  \n");
										strQuery.append("  from cmm0034  \n");
										strQuery.append(" where cm_jobcd = ?   \n");
										pstmt = conn.prepareStatement(strQuery.toString());
										//pstmt = new LoggableStatement(conn,strQuery.toString());
										pstmt.setString(1, imsiJobcd);
										//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
										rs = pstmt.executeQuery();
										if (rs.next()){
											imsiSyscd = rs.getString("cm_syscd");
										}else{
											count++;
										}
									}

									if(k == 0){
										pstmt.close();
										rs.close();
										strQuery.setLength(0);
										strQuery.append("delete from cmm0044  	\n");
										strQuery.append("where cm_userid = ?");
										pstmt = conn.prepareStatement(strQuery.toString());
										//pstmt = new LoggableStatement(conn,strQuery.toString());
										pstmt.setString(1, imsiUser);
										//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
										pstmt.executeUpdate();
									}
									if(count == 0){
										if(rtList.get(i).get("CM_JOBNAME") != null && rtList.get(i).get("CM_JOBNAME") != ""){
											pstmt.close();
											rs.close();
											strQuery.setLength(0);
											strQuery.append("insert into cmm0044(cm_userid, cm_syscd, cm_jobcd) \n");
											strQuery.append(" values(?,?,?)");
											pstmt = conn.prepareStatement(strQuery.toString());
											//pstmt = new LoggableStatement(conn,strQuery.toString());
											pstmt.setString(1, imsiUser);
											pstmt.setString(2, imsiSyscd);
											pstmt.setString(3, imsiJobcd);
											//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
											pstmt.executeUpdate();
										}
									}
								}
								rst.put("CM_JOBNAME", error.toString());
							}
						}// 44 수정 완료
					}//count == 0 일 경우 끝
					else{//엑셀 저장 값이 오류가 있던지 아니면 동기화 제외 대상자 red
						rst.put("colorsw", "3");//red
						rst.put("CM_CODENAME", rtList.get(i).get("CM_CODENAME"));
						rst.put("CM_JOBNAME", rtList.get(i).get("CM_JOBNAME"));
					}
				}else{	//동기화제외 대상 경우 기존 값으로...
					rst = rtList.get(i);
					rst.put("colorsw", "7");//blue
				}
				rsval.add(rst);
//				if( !rst.get("colorsw").equals("3") && !rst.get("colorsw").equals("4") &&
//					!rst.get("colorsw").equals("5") && !rst.get("colorsw").equals("6") &&
//					!rst.get("colorsw").equals("7") ) {
				if( rst.get("colorsw").equals("0") ){
					conn.commit();
				} else {
					conn.rollback();
				}
			}

			rs.close();
			pstmt.close();
			//conn.commit();
			strQuery = null;
			rs = null;
			pstmt = null;
			conn = null;
			rst = null;

			return rsval.toArray();


		}catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				conn.rollback();
			}
			ecamsLogger.error("## Cmm0403.java.allSign_up() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## eCmm0403.java.allSign_up() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				conn.rollback();
			}
			ecamsLogger.error("## Cmm0403.allSign_up() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0403.allSign_up() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  	rtList = null;
			if (rsval != null) 	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0403.allSign_up() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//all_sing_up end
} //SingUp end
