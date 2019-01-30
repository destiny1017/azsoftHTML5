package app.common;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.ecams.common.logger.EcamsLogger;

public class CreateXml implements Serializable {

	private static final long serialVersionUID = 1L;
	//TABLE CMM0200 CM_GBNCD
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	private ArrayList<String> colName = new ArrayList<String>();
	private Document doc = null;
	private Element rootCODE = null;
	
	

	/**
	 * toString method: creates a String representation of the object
	 * @return the String representation
	 * @author e.sale
	 */

	public void init_Xml(String ... args){
		try{
			for (String arg : args){
				colName.add(arg);
			}
			
			this.createXMLDocument();
			
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
	}
	
	private void createXMLDocument(){
		Element Code_list = null;
		
		DocumentBuilderFactory factory = null;
		factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			Code_list = doc.createElement("ITEMS");
			doc.appendChild(Code_list);
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		rootCODE = Code_list;
	}	

	public Document getDocument(){
		return this.doc;
	}
	
	public void addXML(String ... args){
		Element Code = doc.createElement("ITEM");

		Element ParentElement=null;
		
		try
		{		
			if (args[args.length-1] == null || args[args.length-1] == "" || args[args.length-1].equals("")){
				ParentElement = rootCODE;
			}
			else{
				NodeList nodeList = doc.getElementsByTagName("ITEM");
				for (int i=0;i<nodeList.getLength();i++){
					Element tmpNode = (Element)nodeList.item(i);
					if (tmpNode.getAttribute("ID").equals(args[args.length-1])){
						ParentElement = tmpNode;
						break;
					}
				}
				if (ParentElement == null){
					ParentElement = rootCODE;
				}
			}
			
			for (int i=0;i<args.length-1;i++){
				if (args[i] == null)
					args[i] = "";
				//ecamsLogger.debug(colName.get(i)+":1:"+args[i]);
				Code.setAttribute(colName.get(i), args[i]);
				//ecamsLogger.debug(colName.get(i)+":2:"+args[i]);
			}
			
			ParentElement.appendChild(Code);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		    ecamsLogger.error(e.getMessage());
		}			
	}	
	
	public String xml_toStr(){
		TransformerFactory tr_factory = TransformerFactory.newInstance();
		String xmlStr = "";     

		try
		{
			//tr_factory.setAttribute("indent-number", new Integer(4));
			Properties output = new Properties();
		    output.setProperty("version", "1.0");
		    output.setProperty("encoding", "utf-8");
		    output.setProperty("indent", "yes");
		    output.setProperty("standalone", "no");

			
		    StringWriter sw = new StringWriter();
		    Transformer transformer = tr_factory.newTransformer();
		    transformer.setOutputProperties(output);
		    transformer.transform(new DOMSource(doc), new StreamResult(sw));
		    xmlStr = sw.getBuffer().toString();

		    //xmlStr.trim();	    
    
		}
		catch (Exception e)
		{
			    e.printStackTrace();
		}
		return xmlStr;
	}
	
	public void xml_toFile(String filename){
		TransformerFactory tr_factory = TransformerFactory.newInstance();
		try
		{
			//tr_factory.setAttribute("indent-number", new Integer(4));
		    Properties output = new Properties();
		    output.setProperty("version", "1.0");
		    output.setProperty("encoding", "utf-8");
		    output.setProperty("indent", "yes");
		    output.setProperty("standalone", "no");


		    Transformer transformer = tr_factory.newTransformer();
		    transformer.setOutputProperties(output);
		    transformer.transform(new DOMSource(doc), new StreamResult(new FileWriter(filename)));
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
	}	
}//end of class MemberVO statement