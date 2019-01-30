package html.app.dev;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import app.common.PrjInfo;
import app.common.SysInfo;
import app.common.SystemPath;
import app.eCmd.Cmd0100;
import app.eCmr.Cmr0100;
import app.eCmr.Cmr0200;
import app.eCmr.Confirm_select;
import html.app.common.ParsingCommon;

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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "SYSTEMCOMBO" :
					response.getWriter().write( getSysInfo(request) );
					break;
				case "SRIDCOMBO" :
					response.getWriter().write( getPrjList(request) );
					break;
				case "PRGCOMBO" :
					response.getWriter().write( getRsrcOpen(request) );
					break;
				case "FILETREE" :
					response.getWriter().write( getRsrcPath(request) );
					break;
				case "CHILDFILETREE" :
					response.getWriter().write( getChidrenTree(request) );
					break;
				case "GETFILEGRID":
					response.getWriter().write( getFileList(request) );
					break;
				case "GRIDDOWNFILE":
					response.getWriter().write( getDownFileList(request) );
					break;
				case "GETLOCALHOME":
					response.getWriter().write( getLocalHomeDir(request));
					break;
				case "CHECKCONFIRM":
					response.getWriter().write( checkConfirmInfo(request));
					break;
				case "GETCONFIRMINFO":
					response.getWriter().write( getConfirmInfo(request));
					break;
				case "REQUESTCHECKOUT":
					response.getWriter().write( requestCheckOut(request));
					break;	
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}//end of getSysInfo() method statement
	
	private String requestCheckOut(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>	confirmInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "confirmInfoData");
		ArrayList<HashMap<String, String>> requestFiles = ParsingCommon.parsingRequestJsonParamToArrayList(request, "requestFiles");
		ArrayList<HashMap<String, String>> requestConfirmData = ParsingCommon.parsingRequestJsonParamToArrayList(request, "requestConfirmData");
		
		
		
		return gson.toJson( confirm.Confirm_Info(confirmInfoMap) );
	}
	
	private String getConfirmInfo(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>	confirmInfoMap = null;
		confirmInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "confirmInfoData");
		return gson.toJson( confirm.Confirm_Info(confirmInfoMap) );
	}
	
	private String checkConfirmInfo(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>	confirmInfoMap = null;
		confirmInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "confirmInfoData");
		return gson.toJson( cmr0200.confSelect(	confirmInfoMap.get("sysCd"),
												confirmInfoMap.get("strReqCd"),
												confirmInfoMap.get("strRsrcCd"),
												confirmInfoMap.get("userId"),
												confirmInfoMap.get("strQry")) );
	}
	
	private String getLocalHomeDir(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>	localHomeMap = null;
		localHomeMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "devHomeData");
		return gson.toJson( systemPath.getDevHome(localHomeMap.get("userId"), localHomeMap.get("sysCd")) );
	}
	
	private String getChidrenTree(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 childrenMap = null;
		childrenMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "childFileTreeData");
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
	
	
	private String getRsrcPath(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 rsrcPathMap = null;
		rsrcPathMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "fileTreeData");
		return gson.toJson( systemPath.getRsrcPath(
								rsrcPathMap.get("UserId"),
								rsrcPathMap.get("SysCd") , 
								rsrcPathMap.get("SecuYn"), 
								rsrcPathMap.get("SinCd"), 
								rsrcPathMap.get("ReqCd")) );
	}
	
	
	
	private String getRsrcOpen(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 prgoMap = null;
		prgoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "progData");
		return gson.toJson( cmd0100.getRsrcOpen(prgoMap.get("SysCd"), prgoMap.get("SelMsg")));
	}
	
	private String getPrjList(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 srMap = null;
		srMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "srData");
		return gson.toJson( prjInfo.getPrjList(srMap));
	}
	
	
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 sysMap = null;
		sysMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "sysData");
		return gson.toJson( sysinfo.getSysInfo(
								sysMap.get("UserId"), 
								sysMap.get("SecuYn"), 
								sysMap.get("SelMsg"), 
								sysMap.get("CloseYn"), 
								sysMap.get("ReqCd")));
	}
	
	private String getFileList(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 downFileData = null;
		downFileData = ParsingCommon.parsingRequestJsonParamToHashMap(request, "getFileData");
		
		/*HashMap<String, Object> fileReturnMap = new HashMap<>();
		fileReturnMap.put("fileData", cmr0100.getFileList(downFileData));
		return  gson.toJson(fileReturnMap);*/
		return  gson.toJson(cmr0100.getFileList(downFileData));
	}
	
	private String getDownFileList(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 downFileData = null;
		ArrayList<HashMap<String, String>> 	 downFilelist = null;
		
		downFileData = ParsingCommon.parsingRequestJsonParamToHashMap(request, "downFileData");
		downFilelist = ParsingCommon.parsingRequestJsonParamToArrayList(request, "removedFileList");
		
		return gson.toJson(cmr0100.getDownFileList( 	downFileData.get("strUserId"), 
														downFilelist, 
														downFileData.get("strReqCD"), 
														downFileData.get("syscd"), 
														downFileData.get("sayu"),
														false)  );
	}
	

}
