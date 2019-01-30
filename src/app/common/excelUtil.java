package app.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ecams.common.logger.EcamsLogger;


/** xls , xlsx 사용 가능
 * @author Administrator
 *
 */
public class excelUtil {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * <PRE>
	 * 1. MethodName	: getArrayCollection
	 * 2. ClassName		: excelUtil
	 * 3. Commnet			:
	 * 4. 작성자				: Administrator
	 * 5. 작성일				: 2010. 12. 29. 오후 7:13:33
	 * </PRE>
	 * 		@return Object[]
	 * 		@param filePath
	 * 		@param headerDef
	 * 		@return
	 * 		@throws IOException
	 * 		@throws Exception
	 */
	public Object[] getArrayCollection(String filePath,ArrayList<String> headerDef) throws IOException, Exception {
		Object[]		  rtObj		  = null;

		ArrayList<HashMap<String, String>>  rtList = null;
		HashMap<String, String>			    rst	   = null;

		Workbook wb;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;

		int firstRow;
		int lastRow;
		int rowIdx;

		short firstCell;
		short lastCell;
		short cellIdx;

		try {
			ecamsLogger.error("++++ filePath+++"+filePath);
			filePath = filePath.trim();
			filePath = filePath.replace("//", "/");
			filePath = filePath.replace("/", "//");
			//ecamsLogger.error("filePath[2] : " + filePath);

			//워크북 오브젝트의 취득
			//wb = getXlsWorkBook(filePath);//xls 과 xlsx의 구분위한 함수
			//xls와 xlsx 통합 사용
			wb = WorkbookFactory.create(new FileInputStream(filePath));

			// 총 워크시트수의 취득
			//int sheetcount = wb.getNumberOfSheets();
			if (wb == null){
				throw new Exception("엑셀 sheet 읽기 실패[excelUtil]");
			}
			sheet = wb.getSheetAt(0);

			// 워크시트에 있는 첫행과 마지막행의 인덱스를 취득
			firstRow = sheet.getFirstRowNum();
			lastRow = sheet.getLastRowNum();


			rtList = new ArrayList<HashMap<String, String>>();
			// 행 별로 데이터를 취득


			for (rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
				//행을 표시하는 오브젝트의 취득
				row = sheet.getRow(rowIdx);

				// 행에 데이터가 없는 경우
				if (row == null) break;


				// 행에서 첫셀과 마지막 셀의 인덱스를 취득
				firstCell = row.getFirstCellNum();
				lastCell = row.getLastCellNum();

				if ((lastCell-firstCell) < headerDef.size()){
					throw new Exception("엑셀파일의 열의 갯수가 지정한 해더의 갯수보다 적습니다.");
				}


				//셀 별로 데이터를 취득

				rst   = new HashMap<String, String>();
				for (cellIdx = firstCell ; cellIdx <= lastCell; cellIdx++) {
					String data = null;

					if (headerDef.size()-1 < cellIdx){
						break;
					}
					if (headerDef.get(cellIdx) == null){
						break;
					}

					// 셀을 표시하는 오브젝트를 취득
					cell = row.getCell(cellIdx);

					// 빈 셀인 경우
					if (cell == null) break;

					//cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// 셀에 있는 데이터의 종류를 취득
					int type = cell.getCellType();

					// 데이터 종류별로 데이터를 취득
					switch (type) {
						case Cell.CELL_TYPE_BOOLEAN:
							boolean bdata = cell.getBooleanCellValue();
							data = String.valueOf(bdata);
							break;
						case Cell.CELL_TYPE_NUMERIC:
							double ddata = cell.getNumericCellValue();
							data = String.valueOf(((int)ddata));
							break;
						case Cell.CELL_TYPE_STRING:
							data = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_BLANK:
						case Cell.CELL_TYPE_ERROR:
						case Cell.CELL_TYPE_FORMULA:
						default:
							continue;
					}
					rst.put(headerDef.get(cellIdx), data);
				}
				rtList.add(rst);
			}

			//excelFis.close();
			ecamsLogger.error("++++ rtList+++"+rtList.toString());
			rtObj =  rtList.toArray();
			//System.out.println("excelUtil[1]:"+rtList.toString());

			rtList = null;

			return rtObj;

		} catch (IOException exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception END ##");
			throw exception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getArrayCollection() Exception END ##");
			throw exception;
		} finally{
			if (rtObj != null)	rtObj = null;
		}
	}

	/**
	 * <PRE>
	 * 1. MethodName	: getExcelArrayCollection
	 * 2. ClassName		: excelUtil
	 * 3. Commnet			: 단위테스트/통합테스트 테스트케이스 [엑셀로드]시 사용하는 함수
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 29. 오후 7:09:04
	 * </PRE>
	 * 		@return Object[]
	 * 		@param filePath
	 * 		@param headerDef
	 * 		@return
	 * 		@throws IOException
	 * 		@throws Exception
	 */
	public Object[] getExcelArrayCollection(String filePath,ArrayList<String> headerDef) throws IOException, Exception
	{
		Object[]		  rtObj		  = null;

		ArrayList<HashMap<String, String>>  rtList = null;
		HashMap<String, String>			    rst	   = null;

		Workbook wb;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;

		int firstRow;
		int lastRow;
		int rowIdx;

		short firstCell;
		short lastCell;
		short cellIdx;

		try {
			//ecamsLogger.error("filePath 111 : " + "["+filePath+"]");
			//filePath ="//ecams//hanabank//bin//tmp//9812370_excel.xls";
			filePath = filePath.trim();
			filePath = filePath.replace("//", "/");
			filePath = filePath.replace("/", "//");
			//ecamsLogger.error("filePath 222 : " + filePath);

			//워크북 오브젝트의 취득
//			wb = getXlsWorkBook(filePath);
			//xls와 xlsx 통합 사용
			wb = WorkbookFactory.create(new FileInputStream(filePath));

			// 총 워크시트수의 취득
			//int sheetcount = wb.getNumberOfSheets();


			sheet = wb.getSheetAt(0);

			// 워크시트에 있는 첫행과 마지막행의 인덱스를 취득
			firstRow = sheet.getFirstRowNum()+1; //헤더부분 제외하기 위한 조치
			lastRow = sheet.getLastRowNum();


			rtList = new ArrayList<HashMap<String, String>>();
			// 행 별로 데이터를 취득


			for (rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
				//행을 표시하는 오브젝트의 취득
				row = sheet.getRow(rowIdx);

				// 행에 데이터가 없는 경우
				if (row == null) break;


				// 행에서 첫셀과 마지막 셀의 인덱스를 취득
				firstCell = row.getFirstCellNum();
				lastCell = row.getLastCellNum();
//
//				if ((lastCell-firstCell) < headerDef.size()){
//					//(kicc)header size보다 작은경우 존재해서 주석처리.
//					//throw new Exception("엑셀파일의 열의 갯수가 지정한 해더의 갯수보다 적습니다.");
//				}
//

				//셀 별로 데이터를 취득
				rst   = new HashMap<String, String>();
				for (cellIdx = firstCell ; cellIdx <= lastCell; cellIdx++) {
					String data = null;

					if (headerDef.size()-1 < cellIdx){
						break;
					}
					/*//해당셀 null 이여두 값을 빈칸으로 넘겨줘야해서 주석처리 - 호윤
					if (headerDef.get(cellIdx) == null){
						break;
					}
					*/
					
					//첫번째셀이면서 null 이면 braek;
					if (headerDef.get(cellIdx) == null && cellIdx == 1){
						break;
					}


					// 셀을 표시하는 오브젝트를 취득
					cell = row.getCell(cellIdx);

					// 빈 셀인 경우
					if (cell == null){
						//기존 빈셀인경우 break; 했는데
						//break;
						rst.put(headerDef.get(cellIdx), "");
					}else{

						//cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// 셀에 있는 데이터의 종류를 취득
						int type = cell.getCellType();

						// 데이터 종류별로 데이터를 취득


						switch (type) {
							case Cell.CELL_TYPE_BOOLEAN:
								boolean bdata = cell.getBooleanCellValue();
								data = String.valueOf(bdata);
								data.trim();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								double ddata = cell.getNumericCellValue();
								data = String.valueOf(((int)ddata));
								data.trim();
								break;
							case Cell.CELL_TYPE_STRING:
								data = cell.getStringCellValue();
								data.trim();
								break;
							case Cell.CELL_TYPE_BLANK:
							case Cell.CELL_TYPE_ERROR:
							case Cell.CELL_TYPE_FORMULA:
							default:
								continue;
						}
						rst.put(headerDef.get(cellIdx), data);
					}
				}
				rtList.add(rst);
			}

			//excelFis.close();

			rtObj =  rtList.toArray();
			//System.out.println("excelUtil[2]:"+rtList.toString());
			rtList = null;

			return rtObj;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## excelUtil.getExcelArrayCollection() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## excelUtil.getExcelArrayCollection() Exception END ##");
			throw exception;
		} finally{
			if (rtObj != null)	rtObj = null;
		}
	}

	/** 서버에 엑셀 파일 작성하기
	 * <PRE>
	 * 1. MethodName	: setExcel
	 * 2. ClassName		: excelUtil
	 * 3. Commnet			:
	 * 4. 작성자				: Administrator
	 * 5. 작성일				: 2010. 12. 29. 오후 7:13:25
	 * </PRE>
	 * 		@return String
	 * 		@param filePath
	 * 		@param headerDef
	 * 		@param aryData
	 * 		@return
	 * 		@throws Exception
	 */
	public String setExcel(String filePath,ArrayList<String> headerDef,ArrayList<HashMap<String,String>> aryData) throws Exception
	{
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

					header = sheet.getHeader();
					header.setCenter("Center Header");

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

    					if (wkRow ==0){
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

					if (wkRow ==0){
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

	/** xls 와 xlsx 구분해서 workbook 작성
	 * @param fileName
	 * @return XSSFWorkbook or HSSFWorkbook 리턴
	 * @throws Exception
	 */
//	private Workbook getXlsWorkBook(String fileName) throws Exception
//	{
//		Workbook     wb = null;
//
//		try {
//
//	        if( fileName.lastIndexOf(".xlsx")>0){
//	            FileInputStream fis =new FileInputStream(fileName);
//	            wb = new XSSFWorkbook(fis);
//	            fis.close();
//	        } else if( fileName.matches(".*.xls")) {
//	            FileInputStream fis =new FileInputStream(fileName);
//	            POIFSFileSystem filein =new POIFSFileSystem( fis);
//	            wb =new HSSFWorkbook(filein);
//	            filein = null;
//	            fis.close();
//	        }
//
//	        return wb;
//
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## excelUtil.getXlsWorkBook() Exception START ##");
//			ecamsLogger.error("## Error DESC : ", exception);
//			ecamsLogger.error("## excelUtil.getXlsWorkBook() Exception END ##");
//			throw exception;
//		} finally{
//			if (wb != null)	wb = null;
//		}
//    }
}
