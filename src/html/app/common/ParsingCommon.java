/**
 * JSON STRING 각종 JAVA OBJECT 형태로 변환
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-01-29
 */
package html.app.common;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParsingCommon {
	
	public static Gson gson = new Gson();
	
	/*
	 * request json string을 HasMap<String,String> 형태로 변환합니다.
	 */
	public static HashMap<String, String> parsingRequestJsonParamToHashMap(HttpServletRequest req, String paramKey) {
		String jsonData = null;
		jsonData 	= req.getParameter(paramKey);
		TypeToken<HashMap<String, String>> typeToken = new TypeToken<HashMap<String, String>>(){};
		HashMap<String, String> mapData 	=  gson.fromJson(jsonData, typeToken.getType());
		return mapData;
	}
	
	/*
	 * request json string을 String 형태로 변환합니다.
	 */
	public static String parsingRequestJsonParamToString(HttpServletRequest req, String dataTypeParam) {
		String jsonData = null;
		String returnString = null;
		
		jsonData 	= req.getParameter(dataTypeParam);
		returnString 	= (String) gson.fromJson(jsonData, String.class);
		return returnString;
	}
	
	/*
	 * request json string을 ArrayList<HashMap<String, String>> 형태로 변환합니다.
	 */
	public static ArrayList<HashMap<String, String>> parsingRequestJsonParamToArrayList(HttpServletRequest req, String paramKey) {
		String jsonData = null;
		jsonData 	= req.getParameter(paramKey);
		TypeToken<ArrayList<HashMap<String, String>>> typeToken = new TypeToken<ArrayList<HashMap<String, String>>>(){};
		ArrayList<HashMap<String, String>> dataList 	=  gson.fromJson(jsonData, typeToken.getType());
		return dataList;
	}
	
	/*
	 * request json string을 ArrayList<HashMap<String, Object>> 형태로 변환합니다.
	 */
	public static ArrayList<HashMap<String, Object>> parsingRequestJsonParamToArrayListHashMapObject(HttpServletRequest req, String paramKey) {
		String jsonData = null;
		jsonData 	= req.getParameter(paramKey);
		TypeToken<ArrayList<HashMap<String, Object>>> typeToken = new TypeToken<ArrayList<HashMap<String, Object>>>(){};
		ArrayList<HashMap<String, Object>> dataList 	=  gson.fromJson(jsonData, typeToken.getType());
		return dataList;
	}
	
	/*
	 * request json string을 ArrayList<HashMap<String, Object>> 형태로 변환합니다.
	 * 단 파라미터는 String문자열만 받아옵니다.
	 */
	public static ArrayList<HashMap<String, Object>> parsingJsonToArrayListHashMapObject( String jsonStr ) {
		TypeToken<ArrayList<HashMap<String, Object>>> typeToken = new TypeToken<ArrayList<HashMap<String, Object>>>(){};
		ArrayList<HashMap<String, Object>> dataList 	=  gson.fromJson(jsonStr, typeToken.getType());
		return dataList;
	}

}
