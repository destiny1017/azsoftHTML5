/**
 * 체크아웃 화면 서블릿 
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */

package html.app.dev;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import app.common.PrjInfo;
import app.common.SysInfo;
import app.common.SystemPath;
import app.eCmd.Cmd0100;
import app.eCmr.Cmr0100;
import app.eCmr.Cmr0200;
import app.eCmr.Confirm_select;
import html.app.common.ParsingCommon;
import sun.security.jca.GetInstance.Instance;

@WebServlet("/webPage/dev/CheckOut")
public class CheckOut extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmr0100 cmr0100  = new Cmr0100();
	SysInfo sysinfo = new SysInfo();
	PrjInfo prjInfo = new PrjInfo();
	Cmd0100 cmd0100 = new Cmd0100();
	SystemPath systemPath = new SystemPath();
	Cmr0200 cmr0200 = new Cmr0200();
	Confirm_select confirm = new Confirm_select();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "SYSTEMCOMBO" :
					response.getWriter().write( getSysInfo(jsonElement) );
					break;
				case "SRIDCOMBO" :
					response.getWriter().write( getPrjList(jsonElement) );
					break;
				case "PRGCOMBO" :
					response.getWriter().write( getRsrcOpen(jsonElement) );
					break;
				case "FILETREE" :
					response.getWriter().write( getRsrcPath(jsonElement) );
					break;
				case "CHILDFILETREE" :
					response.getWriter().write( getChidrenTree(jsonElement) );
					break;
				case "GETFILEGRID":
					response.getWriter().write( getFileList(jsonElement) );
					break;
				case "GRIDDOWNFILE":
					response.getWriter().write( getDownFileList(jsonElement) );
					break;
				case "GETLOCALHOME":
					response.getWriter().write( getLocalHomeDir(jsonElement));
					break;
				case "CHECKCONFIRM":
					response.getWriter().write( checkConfirmInfo(jsonElement));
					break;
				case "GETCONFIRMINFO":
					response.getWriter().write( getConfirmInfo(jsonElement));
					break;
				case "REQUESTCHECKOUT":
					response.getWriter().write( requestCheckOut(jsonElement));
					break;
				case "SVRFILEMAKE":
					response.getWriter().write( svrFileMake(jsonElement));
					break;	
				case "GETFILELIST":
					response.getWriter().write( getProgFileList(jsonElement));
					break;	
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}//end of getSysInfo() method statement
	
	private String getProgFileList(JsonElement jsonElement) throws SQLException, Exception {
		String acptNo = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "ACPTNO") );
		return gson.toJson( cmr0100.getProgFileList(acptNo, "G"));
	}
	
	private String svrFileMake(JsonElement jsonElement) throws SQLException, Exception {
		String acptNo = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "ACPTNO") );
		return gson.toJson( cmr0100.svrFileMake(acptNo));
	}
	
	private String requestCheckOut(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>	requestMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "requestData"));
		ArrayList<HashMap<String, String>> requestFiles = ParsingCommon.jsonArrToArr(ParsingCommon.jsonEtoStr(jsonElement, "requestFiles"));
		ArrayList<HashMap<String, Object>> requestConfirmData = changeLinkedTreeMapToMap(ParsingCommon.jsonStrToArrObj(ParsingCommon.jsonEtoStr(jsonElement, "requestConfirmData")));
		return gson.toJson( cmr0100.request_Check_Out(requestFiles, requestMap, requestConfirmData) );
	}
	
	private ArrayList<HashMap<String, Object>> changeLinkedTreeMapToMap(ArrayList<HashMap<String, Object>> changeTargetArr) {
		
		for(int i=0; i<changeTargetArr.size(); i++) {
			String jsonStr = changeTargetArr.get(i).get("arysv").toString();
			ArrayList<HashMap<String, Object>> arrayList = ParsingCommon.jsonStrToArrObj(jsonStr);
			changeTargetArr.get(i).put("arysv",arrayList);
		}
		
		return changeTargetArr;
	}
	
	private String getConfirmInfo(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>	confirmInfoMap = null;
		confirmInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "confirmInfoData"));
		return gson.toJson( confirm.Confirm_Info(confirmInfoMap) );
	}
	
	private String checkConfirmInfo(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>	confirmInfoMap = null;
		confirmInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "confirmInfoData"));
		return gson.toJson( cmr0200.confSelect(	confirmInfoMap.get("sysCd"),
												confirmInfoMap.get("strReqCd"),
												confirmInfoMap.get("strRsrcCd"),
												confirmInfoMap.get("userId"),
												confirmInfoMap.get("strQry")) );
	}
	
	private String getLocalHomeDir(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>	localHomeMap = null;
		localHomeMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "devHomeData"));
		return gson.toJson( systemPath.getDevHome(localHomeMap.get("userId"), localHomeMap.get("sysCd")) );
	}
	
	private String getChidrenTree(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 childrenMap = null;
		childrenMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "childFileTreeData"));
		return makeChildrenTree(
					childrenMap.get("Rsrccd"), 
					childrenMap.get("FileInfo"), 
					childrenMap.get("FileId"), 
					childrenMap.get("UserId"), 
					childrenMap.get("SysCd"));
	}
	
	private String makeChildrenTree(String Rsrccd, String FileInfo, String FileId, String UserId, String SysCd) {
		
		ArrayList<HashMap<String, String>> rtRsrcPathList = null;
		ArrayList<HashMap<String, String>> mergePathList  =  new ArrayList<HashMap<String, String>>();
		try {
				
			rtRsrcPathList = (ArrayList<HashMap<String, String>>) systemPath.getDirPath3(	UserId, 
																							SysCd, 
																							Rsrccd, 
																							FileInfo, 
																							FileId);
			
			for(HashMap<String, String> pathMap: rtRsrcPathList) {
				
				pathMap.put("id", 	pathMap.get("cm_seqno"));
				pathMap.put("pid", 	pathMap.get("cm_upseq"));
				pathMap.put("order","1");
				pathMap.put("text", pathMap.get("cm_dirpath"));
				pathMap.put("value", pathMap.get("cr_rsrccd"));
				pathMap.put("fullpath", pathMap.get("cm_fullpath"));
				pathMap.put("dsncd", pathMap.get("cr_dsncd"));
				
			}
			
			mergePathList.addAll(mergePathList.size(), rtRsrcPathList);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return gson.toJson(mergePathList);
		}
		
	}
	
	private String getRsrcPath(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 rsrcPathMap = null;
		rsrcPathMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "fileTreeData"));
		return gson.toJson( systemPath.getRsrcPath(
								rsrcPathMap.get("UserId"),
								rsrcPathMap.get("SysCd") , 
								rsrcPathMap.get("SecuYn"), 
								rsrcPathMap.get("SinCd"), 
								rsrcPathMap.get("ReqCd")) );
	}
	
	
	
	private String getRsrcOpen(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 prgoMap = null;
		prgoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "progData"));
		return gson.toJson( cmd0100.getRsrcOpen(prgoMap.get("SysCd"), prgoMap.get("SelMsg")));
	}
	
	private String getPrjList(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 srMap = null;
		srMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "srData"));
		return gson.toJson( prjInfo.getPrjList(srMap));
	}
	
	
	private String getSysInfo(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 sysMap = null;
		sysMap =  ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "sysData"));
		return gson.toJson( sysinfo.getSysInfo(
								sysMap.get("UserId"), 
								sysMap.get("SecuYn"), 
								sysMap.get("SelMsg"), 
								sysMap.get("CloseYn"), 
								sysMap.get("ReqCd")));
	}
	
	private String getFileList(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 downFileData = null;
		downFileData =  ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "getFileData"));
		
		/*HashMap<String, Object> fileReturnMap = new HashMap<>();
		fileReturnMap.put("fileData", cmr0100.getFileList(downFileData));
		return  gson.toJson(fileReturnMap);*/
		return  gson.toJson(cmr0100.getFileList(downFileData));
	}
	
	private String getDownFileList(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 downFileData = null;
		ArrayList<HashMap<String, String>> 	 downFilelist = null;
		
		downFileData = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "downFileData"));
		downFilelist = ParsingCommon.jsonArrToArr(ParsingCommon.jsonEtoStr(jsonElement, "removedFileList"));
		
		return gson.toJson(cmr0100.getDownFileList( 	downFileData.get("strUserId"), 
														downFilelist, 
														downFileData.get("strReqCD"), 
														downFileData.get("syscd"), 
														downFileData.get("sayu"),
														false)  );
	}
	

}
