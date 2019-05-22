import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class Calcu {
	
	public static void main(String[] args) {
		
		
		ArrayList<HashMap<String, Object>> testData = new ArrayList<>();
		HashMap<String, Object> testMap = new HashMap<>();
		
		testMap.put("cnt", 34);
		testMap.put("cm_codename", "체크아웃신청");
		testMap.put("cm_sysmsg", "형상관리시스템");
		testMap.put("cm_syscd", "90001");
		testMap.put("reqcd", "01");
		
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 2);
		testMap.put("cm_codename", "체크아웃신청");
		testMap.put("cm_sysmsg", "형상관리시스템_고병권");
		testMap.put("cm_syscd", "90033");
		testMap.put("reqcd", "01");
		
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 51);
		testMap.put("cm_codename", "테스트적용요청");
		testMap.put("cm_sysmsg", "형상관리시스템");
		testMap.put("cm_syscd", "90001");
		testMap.put("reqcd", "03");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 18);
		testMap.put("cm_codename", "운영적용요청");
		testMap.put("cm_sysmsg", "형상관리시스템");
		testMap.put("cm_syscd", "90001");
		testMap.put("reqcd", "04");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 80);
		testMap.put("cm_codename", "체크인");
		testMap.put("cm_sysmsg", "형상관리시스템");
		testMap.put("cm_syscd", "90001");
		testMap.put("reqcd", "07");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 2);
		testMap.put("cm_codename", "체크인");
		testMap.put("cm_sysmsg", "형상관리시스템_고병권");
		testMap.put("cm_syscd", "90033");
		testMap.put("reqcd", "07");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 15);
		testMap.put("cm_codename", "체크아웃취소신청");
		testMap.put("cm_sysmsg", "형상관리시스템");
		testMap.put("cm_syscd", "90001");
		testMap.put("reqcd", "11");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 1);
		testMap.put("cm_codename", "체크아웃취소신청");
		testMap.put("cm_sysmsg", "형상관리시스템_고병권");
		testMap.put("cm_syscd", "90033");
		testMap.put("reqcd", "11");
		testData.add(testMap);
		testMap = new HashMap<>();
		testMap.put("cnt", 1);
		testMap.put("cm_codename", "일괄등록");
		testMap.put("cm_sysmsg", "형상관리시스템_정호윤");
		testMap.put("cm_syscd", "90032");
		testMap.put("reqcd", "16");
		testData.add(testMap);
		
		
		ArrayList<Object>   categoriesArr			= new ArrayList<Object>();
		ArrayList<HashMap<String, Object>> 	seriesArr = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> series				= new HashMap<>();
		HashMap<String,  Object>	barData	= new HashMap<String, Object>();
		
	
		
		for(int i=0; i<testData.size(); i++) {
			if(!categoriesArr.contains(testData.get(i).get("cm_codename"))) {
				categoriesArr.add(testData.get(i).get("cm_codename"));
			}
			
			
			boolean insertSw = true;
			for(int k=0; k<seriesArr.size(); k++) {
				if(seriesArr.get(k).get("cm_syscd").equals(testData.get(i).get("cm_syscd"))) {
					insertSw = false;
				}
			}
			
			if(insertSw || seriesArr.size() == 0) {
				series = new HashMap<>();
				series.put("name", testData.get(i).get("cm_sysmsg"));
				series.put("stack",testData.get(i).get("cm_sysmsg"));
				series.put("cm_syscd",testData.get(i).get("cm_syscd"));
				seriesArr.add(series);
			}
			
			insertSw = true;
			
			for(int k=0; k<seriesArr.size(); k++) {
				if(seriesArr.get(k).get("cm_syscd").equals(testData.get(i).get("cm_syscd"))) {
					int seriesIndex = k;
					seriesArr.get(seriesIndex).put("data", 
													makeDataArr(categoriesArr, 
																seriesArr.get(seriesIndex), 
																testData.get(i).get("cnt").toString(), 
																testData.get(i).get("cm_codename").toString()));
					break;
				}
			}
		}
		barData.put("categories", categoriesArr);
		barData.put("series", seriesArr);
		
		System.out.println(barData);
	}
	
	public static ArrayList<Object> makeDataArr(ArrayList<Object> categoriesArr, HashMap<String, Object> series, String cnt , String codename){
		ArrayList<Object> dataArr = new ArrayList<>();
		
		if(series.containsKey("data")) dataArr = (ArrayList<Object>) series.get("data");
		
		if(dataArr.size() != categoriesArr.size()) {
			for(int i=0; i<categoriesArr.size(); i++) {
				if( (i+1) > dataArr.size()) {
					dataArr.add("0");
				}
			}
		}
		
		for(int i=0; i<categoriesArr.size(); i++) {
			if(categoriesArr.get(i).equals(codename)) {
				dataArr.remove(i);
				dataArr.add(i,cnt);
				break;
			}
		}
		
		return dataArr;
	}
	
	public static String printLine(){
		return "=============================================";
	}

}
