/*****************************************************************************************
	1. program ID	: Cmp6000.java
	2. create date	: 2014. 08. 04
	3. auth		    : sykim
	4. update date	:
	5. auth		    :
	6. description	: 
*****************************************************************************************/

package app.eCmp;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import app.common.LoggableStatement;
import app.common.SystemPath;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmp6000{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * getRowList  부서별 의뢰 현황
	 * @param String selMonth,String dv
	 * @return List Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getRowList(String selMonth, String dv) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		long              rowCnt      = 0;
		double            rowCnt2     = 0.0;
		int 		      cnt 		  = 0;
        double 		      percent     = 0.0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		HashMap<String, String>			    rst2	  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		DecimalFormat df = new DecimalFormat("0.0");
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt 	 		  		  			\n");
			strQuery.append(" from ( SELECT 	cc_srid, min(cc_rate) as cc_rate	\n");
			strQuery.append("		 FROM		CMC0110								\n");
			strQuery.append("		 WHERE		cc_rate IS NOT NULL					\n");
			strQuery.append("		 GROUP BY	cc_srid ) a							\n");
			if( dv.equals("devDept") ){
				strQuery.append(" ,cmm0100 b,cmc0100 c      						\n");
			} else {
				strQuery.append(" ,cmc0100 c      		  							\n");
			}			
			strQuery.append(" where to_char(c.cc_compdate,'yyyymm')=?   \n");
			strQuery.append("  and a.cc_rate is not null				  \n");
			strQuery.append("  and a.cc_srid = c.cc_srid			   	  \n");
			strQuery.append("  and c.cc_status = '9'			  		  \n");
			if( dv.equals("devDept") ){
				strQuery.append("  and b.cm_updeptcd='HAND00028'		  \n");
				strQuery.append("  and c.cc_devdept = b.cm_deptcd		  \n");
			}
			strQuery.append("  and c.cc_reqdept!='000000201'		 	  \n");
			strQuery.append("  and c.cc_devdept!='000000201'		 	  \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, selMonth);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        if( rs.next() ){
	        	rowCnt = rs.getInt("cnt");
	        }
	        pstmt.close();
	        rs.close();
	        
	        parmCnt = 0;
	        if( dv.equals("reqDept") ){
				strQuery.setLength(0);
				strQuery.append("select d.cm_deptcd cd, d.cm_deptname name, count(*) as cnt 	 	\n");
				strQuery.append(" from ( SELECT 	cc_srid, min(cc_rate) as cc_rate				\n");
				strQuery.append("		 FROM		CMC0110											\n");
				strQuery.append("		 WHERE		cc_rate IS NOT NULL								\n");
				strQuery.append("		 GROUP BY	cc_srid ) a										\n");
				strQuery.append(" , cmm0100 b, cmc0100 c,     		 		  						\n");
				strQuery.append(" (select cm_deptcd,cm_deptname from cmm0100         		  		\n");
				strQuery.append("   where cm_useyn = 'Y' and cm_updeptcd is not null 		  		\n");
				strQuery.append("   start with cm_updeptcd = 						 		  		\n");
				strQuery.append("    (select cm_deptcd from cmm0100 where cm_updeptcd is null)		\n");
				strQuery.append("   connect by prior cm_updeptcd = cm_deptcd ) d     		  		\n");
				strQuery.append(" where to_char(c.cc_compdate,'yyyymm')=?          		  		\n");
				strQuery.append("  and a.cc_rate is not null						 		  		\n");
				strQuery.append("  and a.cc_srid = c.cc_srid			   		   	 		  		\n");
				strQuery.append("  and c.cc_reqdept = b.cm_deptcd					 		  		\n");
				strQuery.append("  and ( b.cm_updeptcd = d.cm_deptcd or b.cm_deptcd =d.cm_deptcd )	\n");
				strQuery.append("  and c.cc_status = '9'							 		  		\n");
				strQuery.append("  and c.cc_reqdept!='000000201'		  							\n");
				strQuery.append("  and c.cc_devdept!='000000201'		 	  						\n");
				strQuery.append("group by d.cm_deptcd, d.cm_deptname   				 		  		\n");
				strQuery.append("order by d.cm_deptcd, d.cm_deptname   				 		  		\n");
	        } else if( dv.equals("rate") ){
				strQuery.setLength(0);
				strQuery.append("select b.cm_micode cd, b.cm_codename name, count(*) as cnt 	 	\n");
				strQuery.append(" from ( SELECT 	cc_srid, min(cc_rate) as cc_rate				\n");
				strQuery.append("		 FROM		CMC0110											\n");
				strQuery.append("		 WHERE		cc_rate IS NOT NULL								\n");
				strQuery.append("		 GROUP BY	cc_srid ) a										\n");
				strQuery.append(" , cmm0020 b, cmc0100 c      		 		  						\n");
				strQuery.append(" where to_char(c.cc_compdate,'yyyymm')=?          		  		\n");
				strQuery.append("  and a.cc_rate is not null						 		  		\n");
				strQuery.append("  and a.cc_srid = c.cc_srid			   		   	 		  		\n");
				strQuery.append("  and b.cm_macode = 'DEVRATE'					 		  			\n");
				strQuery.append("  and a.cc_rate = b.cm_micode					 		  			\n");
				strQuery.append("  and c.cc_status = '9'							 		  		\n");
				strQuery.append("  and c.cc_reqdept!='000000201'									\n");
				strQuery.append("  and c.cc_devdept!='000000201'		 	  						\n");
				strQuery.append("group by b.cm_micode, b.cm_codename   				 		  		\n");
				strQuery.append("order by b.cm_micode, b.cm_codename   				 		  		\n");
	        } else if( dv.equals("devDept") ){
				strQuery.setLength(0);
				strQuery.append("select b.cm_deptcd cd, b.cm_deptname name, count(*) as cnt 	 	\n");
				strQuery.append(" from ( SELECT 	cc_srid, min(cc_rate) as cc_rate				\n");
				strQuery.append("		 FROM		CMC0110											\n");
				strQuery.append("		 WHERE		cc_rate IS NOT NULL								\n");
				strQuery.append("		 GROUP BY	cc_srid ) a										\n");
				strQuery.append(" , cmm0100 b, cmc0100 c      		 		  						\n");
				strQuery.append(" where to_char(c.cc_compdate,'yyyymm')=?          		  		\n");
				strQuery.append("  and a.cc_rate is not null						 		  		\n");
				strQuery.append("  and a.cc_srid = c.cc_srid			   		   	 		  		\n");
				strQuery.append("  and c.cc_devdept = b.cm_deptcd					 		  		\n");
				strQuery.append("  and c.cc_status = '9'							 		  		\n");
				strQuery.append("  and c.cc_reqdept!='000000201'									\n");
				strQuery.append("  and c.cc_devdept!='000000201'		 	  						\n");
				strQuery.append("  and b.cm_updeptcd='HAND00028'			  						\n");
				strQuery.append("group by b.cm_deptcd, b.cm_deptname   				 		  		\n");
				strQuery.append("order by b.cm_deptcd, b.cm_deptname   				 		  		\n");
	        }
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, selMonth);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rst = new HashMap<String, String>();
	        rst2 = new HashMap<String, String>();
        	if( dv.equals("reqDept") ){
        		rst.put("deptname","건수");
        	} else if ( dv.equals("rate") || dv.equals("devDept") ) {
        		rst.put("deptname", "처리건수");
        	}
        	rst2.put("deptname","점유율");
        	
	        while ( rs.next() ){
	        	cnt = rs.getInt("cnt");
				rst.put("col" + rs.getString("cd"), Integer.toString(cnt)); //각 부서에 대한 cnt 값 세팅							
				percent = (cnt/(double)rowCnt)*100;
				rowCnt2 = rowCnt2 + percent;
				percent = Double.parseDouble(df.format(percent));	
				rst2.put("col" + rs.getString("cd"), Double.toString(percent)+"%"); //각 부서에 대한  퍼센트 값 세팅
				
			}//end of while-loop statement
	        rowCnt2 = Double.parseDouble(df.format(rowCnt2));	
	        
	        rst.put("rowhap" , Long.toString(rowCnt));
	        rst2.put("rowhap" , Double.toString(rowCnt2)+"%");
			rsval.add(rst);
			rsval.add(rst2);
			rst = null;
			rst2 = null;
			
			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;
//			ecamsLogger.error(rsval.toString());
			
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getRowList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp6000.getRowList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getRowList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.getRowList() Exception END ##");
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
					ecamsLogger.error("## Cmp6000.getRowList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRowList() method statement
	

	public String excelDataMakeDetail(
			ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep, //gridLst에 대한 값
			ArrayList<HashMap<String,String>> fileList2,ArrayList<HashMap<String,String>> prjStep2, //gridLst2에 대한 값
			ArrayList<HashMap<String,String>> fileList3,ArrayList<HashMap<String,String>> prjStep3, //gridLst3에 대한 값
			String UserId,String exlName,String qryDate) throws SQLException, Exception {
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList2	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList3	  = new ArrayList<HashMap<String, String>>();
		
		ArrayList<String> headerDef   = new ArrayList<String>();	
		ArrayList<String> headerDef2  = new ArrayList<String>();	
		ArrayList<String> headerDef3  = new ArrayList<String>();
		
		SystemPath systempath = new SystemPath();
		int j     = 0;
		String retMsg         = "";

		try {
			headerDef.add("deptname");
			headerDef.add("rowhap");
			for (j=0;prjStep.size()>j;j++) {
				headerDef.add("col"+prjStep.get(j).get("code"));
			}
			
			rtList.clear();
			rtList = excelDataFunc(fileList, prjStep);
			
			headerDef2.add("deptname");
			headerDef2.add("rowhap");
			for (j=0;prjStep2.size()>j;j++) {
				headerDef2.add("col"+prjStep2.get(j).get("code"));
			}
			
			rtList2.clear();
			rtList2 = excelDataFunc(fileList2, prjStep2);
			
			headerDef3.add("deptname");
			headerDef3.add("rowhap");
			for (j=0;prjStep3.size()>j;j++) {
				headerDef3.add("col"+prjStep3.get(j).get("code"));
			}
			
			rtList3.clear();
			rtList3 = excelDataFunc(fileList3, prjStep3);
			
			String strPath = systempath.getTmpDir("99");
            //ecamsLogger.debug("++++++excel++"+rtList.toString());
			retMsg = setExcel(strPath+"/"+exlName,qryDate,headerDef, rtList, headerDef2, rtList2, headerDef3, rtList3);
			systempath = null;
			rtList = null;
			rtList2 = null;
			rtList3 = null;

			return retMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.excelDataMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.excelDataMake() Exception END ##");
			throw exception;
		}finally{

		}
	}//end of excelDataMakeDetail() method statement
	
	public ArrayList<HashMap<String, String>> excelDataFunc(ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep) throws Exception{
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int j     = 0;
		
		try {
			rtList.clear();
			rst = new HashMap<String,String>();
			rst.put("deptname", "구분");
			rst.put("rowhap", "합계");

			for (j=0;prjStep.size()>j;j++) {
				rst.put("col"+prjStep.get(j).get("code"), prjStep.get(j).get("codename"));
			}
			rtList.add(rst);
			rst = null;

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("deptname", fileList.get(i).get("deptname"));
				rst.put("rowhap", fileList.get(i).get("rowhap"));
				for (j=0;prjStep.size()>j;j++) {
					rst.put("col"+prjStep.get(j).get("code"), fileList.get(i).get("col"+prjStep.get(j).get("code")));
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			
			return rtList;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.excelDataFunc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.excelDataFunc() Exception END ##");
			throw exception;
		}finally{

		}
	}//end of excelDataFunc() method statement
	
	public String setExcel(String filePath,String qryDate,
			ArrayList<String> headerDef,ArrayList<HashMap<String,String>> aryData,
			ArrayList<String> headerDef2,ArrayList<HashMap<String,String>> aryData2,
			ArrayList<String> headerDef3,ArrayList<HashMap<String,String>> aryData3
			)throws Exception {
		HashMap<String, String>			    rst	   = null;

		FileOutputStream excelFis = null;

		Workbook     wb;

		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		Header header = null;
		CellStyle cs = null;
		DataFormat df = null;
		Font f = null;
		
		qryDate = qryDate.substring(0,4)+"년 "+qryDate.substring(4, 6)+"월"; 

		try {
			boolean rowSw = false;
			int     wkCnt = 0;
			int     wkRow = 0;

	        if( filePath.matches(".*.xls")) {
	            wb = new HSSFWorkbook();
	        } else {
	            wb = new XSSFWorkbook();
	        }

			for (int i = 0;i<aryData.size();i++){

				if (i == 0) rowSw = true;
				else {
					if ((i%60000) == 0) rowSw = true;
					else rowSw = false;
				}
				if (rowSw == true) {
					if (i > 0) wkCnt = i/60000;
					++wkCnt;
					wkRow = 0;
					
					
					sheet = wb.createSheet("sheet"+Integer.toString(wkCnt));
					
					row = sheet.createRow(wkRow);
					row.createCell(0).setCellValue(qryDate+" 개발실적등급별 보고서");
					++wkRow;
					
					row = sheet.createRow(wkRow);
					row.createCell(0).setCellValue("부서별 의뢰 현황");
					++wkRow;

					header = sheet.getHeader();
					header.setCenter(qryDate+" 개발실적등급별 보고서");

					cs = wb.createCellStyle();
					df = wb.createDataFormat();

					f = wb.createFont();
					f.setFontHeightInPoints((short) 10);
					f.setBoldweight(Font.BOLDWEIGHT_BOLD);

					cs.setFont(f);
					cs.setDataFormat(df.getFormat("#,##0.0"));
				}

                if (rowSw == true && i > 0) {
                	row = sheet.createRow(wkRow);
                	rst = aryData.get(0);

                	for (short j=0;j<rst.size();j++){
    					row.createCell(j).setCellValue(rst.get(headerDef.get((int)j)));
    					
    					if (wkRow ==2){
    						// 셀을 표시하는 오브젝트를 취득
    						cell = row.getCell(j);

    						// 빈 셀인 경우
    						if (cell == null) break;

    						cell.setCellStyle(cs);
    					}
    				}
                	++wkRow;
                }
                row = sheet.createRow(wkRow);
                rst = aryData.get(i);

				for (short j=0;j<rst.size();j++){
					row.createCell(j).setCellValue(rst.get(headerDef.get((int)j)));
					

					if (wkRow ==2){
						// 셀을 표시하는 오브젝트를 취득
						cell = row.getCell(j);

						// 빈 셀인 경우
						if (cell == null) break;

						cell.setCellStyle(cs);
					}
				}
				++wkRow;
			}
			
			rst = null;
			rowSw = false;
			int wkRowTmp1 = wkRow+1;
			
			for (int i = 0;i<aryData2.size();i++){
				if (i == 0) rowSw = true;
				else {
					if ((i%60000) == 0) rowSw = true;
					else rowSw = false;
				}
				if (rowSw == true) {

					row = sheet.createRow(wkRow);
					row.createCell(0).setCellValue("업무 등급별 처리현황(S:4주~, A:3~4주, B:2~3주, C:1주, D:단순업무)");
					++wkRow;
					
					//header = sheet.getHeader();
					//header.setCenter("Center Header");

					cs = wb.createCellStyle();
					df = wb.createDataFormat();

					f = wb.createFont();
					f.setFontHeightInPoints((short) 10);
					f.setBoldweight(Font.BOLDWEIGHT_BOLD);

					cs.setFont(f);
					cs.setDataFormat(df.getFormat("#,##0.0"));
				}

                if (rowSw == true && i > 0) {
                	row = sheet.createRow(wkRow);
                	rst = aryData2.get(0);

                	for (short j=0;j<rst.size();j++){
    					row.createCell(j).setCellValue(rst.get(headerDef2.get((int)j)));
    					
    					
    					if (wkRow ==wkRowTmp1){
    						// 셀을 표시하는 오브젝트를 취득
    						cell = row.getCell(j);

    						// 빈 셀인 경우
    						if (cell == null) break;

    						cell.setCellStyle(cs);
    					}
    				}
                	++wkRow;
                }
                row = sheet.createRow(wkRow);
                rst = aryData2.get(i);

				for (short j=0;j<rst.size();j++){
					row.createCell(j).setCellValue(rst.get(headerDef2.get((int)j)));
					
					
					if (wkRow == wkRowTmp1){
						// 셀을 표시하는 오브젝트를 취득
						cell = row.getCell(j);

						// 빈 셀인 경우
						if (cell == null) break;

						cell.setCellStyle(cs);
					}
				}
				++wkRow;
			}
			
			rst = null;
			rowSw = false;
			int wkRowTmp2 = wkRow+1;
			
			for (int i = 0;i<aryData3.size();i++){
				if (i == 0) rowSw = true;
				else {
					if ((i%60000) == 0) rowSw = true;
					else rowSw = false;
				}
				if (rowSw == true) {

					row = sheet.createRow(wkRow);
					row.createCell(0).setCellValue("연구소 실 별 처리 현황");
					++wkRow;
					//header = sheet.getHeader();
					//header.setCenter("Center Header");

					cs = wb.createCellStyle();
					df = wb.createDataFormat();

					f = wb.createFont();
					f.setFontHeightInPoints((short) 10);
					f.setBoldweight(Font.BOLDWEIGHT_BOLD);

					cs.setFont(f);
					cs.setDataFormat(df.getFormat("#,##0.0"));
				}

                if (rowSw == true && i > 0) {
                	row = sheet.createRow(wkRow);
                	rst = aryData3.get(0);

                	for (short j=0;j<rst.size();j++){
    					row.createCell(j).setCellValue(rst.get(headerDef3.get((int)j)));
    					
    					
    					if (wkRow ==wkRowTmp2){
    						// 셀을 표시하는 오브젝트를 취득
    						cell = row.getCell(j);

    						// 빈 셀인 경우
    						if (cell == null) break;

    						cell.setCellStyle(cs);
    					}
    				}
                	++wkRow;
                }
                row = sheet.createRow(wkRow);
                rst = aryData3.get(i);

				for (short j=0;j<rst.size();j++){
					row.createCell(j).setCellValue(rst.get(headerDef3.get((int)j)));
					
					
					if (wkRow ==wkRowTmp2){
						// 셀을 표시하는 오브젝트를 취득
						cell = row.getCell(j);

						// 빈 셀인 경우
						if (cell == null) break;

						cell.setCellStyle(cs);
					}
				}
				++wkRow;
			}

			excelFis = new FileOutputStream(filePath);
		    wb.write(excelFis);
		    excelFis.close();
		    //ecamsLogger.error("#######   setExcel  end   #######");
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception END ##");
			throw exception;
		}
		return filePath;
	}
	
	public Object[] getDivisionInfo() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select cm_micode, cm_codename    \n");
			strQuery.append(" from cmm0020					  \n");
			strQuery.append(" where cm_micode<>'****'		  \n");
			strQuery.append("  and cm_closedt is null	  	  \n");
			strQuery.append("  and cm_macode = 'REPRATE'	  \n");
			strQuery.append("order by cm_micode			  	  \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());

	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        
	        rst = new HashMap<String, String>();
			rst.put("cm_micode","00");
			rst.put("cm_codename","선택하세요");
			rsval.add(rst);

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmp6000.getDivisionInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp6000.getDivisionInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getDivisionInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.getDivisionInfo() Exception END ##");
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
					ecamsLogger.error("## Cmp6000.getDivisionInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDivisionInfo() method statement
	
	
	public Object[] getColHeader(String dv) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			
			if( dv.equals("reqDept") ){
				strQuery.setLength(0);
				strQuery.append("select cm_deptcd cd, cm_deptname name   \n");
				strQuery.append(" from cmm0100					  		 \n");
				strQuery.append(" where cm_updeptcd='000000100'	  		 \n");
				strQuery.append(" and cm_deptcd !='000000201'	  		 \n");
			} else if ( dv.equals("rate") ) {
				strQuery.setLength(0);
				strQuery.append("select cm_micode cd, cm_codename name   \n");
				strQuery.append(" from cmm0020					  		 \n");
				strQuery.append(" where cm_micode<>'****'	  		 	 \n");
				strQuery.append("  and cm_closedt is null	  	  		 \n");
				strQuery.append("  and cm_macode = 'DEVRATE'	  	  	 \n");
				strQuery.append("order by cm_micode			  	  		 \n");
			} else if ( dv.equals("devDept") ) {
				strQuery.setLength(0);
				strQuery.append("select cm_deptcd cd, cm_deptname name   \n");
				strQuery.append(" from cmm0100					  		 \n");
				strQuery.append(" where cm_updeptcd='HAND00028'	  		 \n");
			}
			

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());

	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        
	        while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("code",rs.getString("cd"));
				rst.put("codename",rs.getString("name"));

				
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			rst = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getReqDeptCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp6000.getReqDeptCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getReqDeptCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.getReqDeptCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp6000.getReqDeptCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getColHeader() method statement
	

}//end of Cmp6000 class statement
