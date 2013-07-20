package com.moodletest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ConnHandler {
	
	public static Client test = new Client();
	public static JSONObject doGet(String url) {
	    JSONObject json = null;
	    HttpClient httpclient = new DefaultHttpClient();
	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url);
	    // Accept JSON
	    httpget.addHeader("accept", "application/json");
	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        System.out.println(response.toString());
	        System.out.println(response.getAllHeaders().toString());
	        Header[] head = response.getAllHeaders();
	        for(int i=0;i<head.length;i++){
	        System.out.println("header "+head[i].getName()+" : header value "+head[i].getValue()+" close ");
	        }
	        HttpEntity entity = response.getEntity();
	        
	        InputStream instream = entity.getContent();
	        
            String result= convertStreamToString(instream);
            System.out.println(result);
            // construct a JSON object with result
            json=new JSONObject(result);
            // Closing the input stream will trigger connection release
            instream.close();
	        // Get the response entity
            
            System.out.println(json.get("token"));
	        
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    // Return the json
	    return json;
	}
	
    public static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    byte[] bytes=new byte[1024];
    try {
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return sb.toString();
}
	public static HttpResponse doPost(String url, String c) throws ClientProtocolException, IOException 
    {
        HttpClient httpclient = test.httpClient;
		//HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
        StringEntity s = new StringEntity(c);
        System.out.println(s.getContent());
        System.out.println(s.toString());
        //s.setContentEncoding("UTF-8");
        s.setContentType("application/x-www-form-urlencoded");
        //request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(s);
        //request.addHeader("accept", "application/json");
        

        return httpclient.execute(request);
}
	
	public static HttpResponse doPost_json(String url, String c) throws ClientProtocolException, IOException 
    {
        HttpClient httpclient = test.httpClient;
		//HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
        StringEntity s = new StringEntity(c);
        System.out.println(s.getContent());
        System.out.println(s.toString());
        //s.setContentEncoding("UTF-8");
        s.setContentType("application/x-www-form-urlencoded");
        //request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(s);
        request.addHeader("accept", "application/json");
        //request.addHeader("accept", "application/json");
        

        return httpclient.execute(request);
}
	
	
	public String getValue(Element item, String str) {      
	    NodeList n = item.getElementsByTagName(str);        
	    return this.getElementValue(n.item(0));
	}
	 
	public final String getElementValue( Node elem ) {
	         Node child;
	         if( elem != null){
	             if (elem.hasChildNodes()){
	                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  }
	
	public Document returnDom(HttpResponse resp){
		Document dom = null;
		DocumentBuilder builder=null;
		DocumentBuilderFactory factory = null;
		HttpEntity enty = resp.getEntity();
		InputStream strem= null;
		try {
			strem = enty.getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        factory = DocumentBuilderFactory.newInstance();
        try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			dom = builder.parse(strem);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dom;
	}
	
	public String getKeyValue(String name,Document dom){
		XPath xpath = null;
		XPathExpression expr = null;
		XPathFactory xPathfactory = null;
		xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        try{
        expr = xpath.compile("//KEY[@name=\""+name+"\"]");
        NodeList nl = (NodeList) expr.evaluate(dom, XPathConstants.NODESET);
        NodeList valchildset = nl.item(0).getChildNodes();
        for(int i=0;i<valchildset.getLength();i++)
        {
        	//System.out.println(i+valchildset.item(i).getNodeName());
        	if(valchildset.item(i).getNodeName().equals("VALUE"))
        	{
        		//System.out.println("inside if condition");
        		Node value = valchildset.item(i).getFirstChild();
        		return value.getNodeValue();
        		
        	}
        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        
		return " ";
	}

	public String getKeyValue(String name,Document dom, int itemnum){
		XPath xpath = null;
		XPathExpression expr = null;
		XPathFactory xPathfactory = null;
		xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        try{
        expr = xpath.compile("//KEY[@name=\""+name+"\"]");
        NodeList nl = (NodeList) expr.evaluate(dom, XPathConstants.NODESET);
        NodeList valchildset = nl.item(itemnum).getChildNodes();
        for(int i=0;i<valchildset.getLength();i++)
        {
        	//System.out.println(i+valchildset.item(i).getNodeName());
        	if(valchildset.item(i).getNodeName().equals("VALUE"))
        	{
        		//System.out.println("inside if condition");
        		Node value = valchildset.item(i).getFirstChild();
        		return value.getNodeValue();
        		
        	}
        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        
		return "";
	}
	
	public void xmlparser(InputStream in){
		try
	     {
	         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	         factory.setNamespaceAware(true);
	         XmlPullParser xpp = factory.newPullParser();
	         //String myString = IOUtils.toString(in, "UTF-8");
	         InputStreamReader is = new InputStreamReader(in);
	         StringBuilder sb=new StringBuilder();
	         BufferedReader br = new BufferedReader(is);
	         String read = br.readLine();

	         while(read != null) {
	             //System.out.println(read);
	             sb.append(read);
	             read =br.readLine();

	         }
	         //new StringReader(sb.toString());
	         //xpp.setInput(in, null);
	         //System.out.println(sb.toString());
	         String str = sb.toString();
	         str.replaceAll(">\\s*<", "><");
	         //System.out.println(str);
	         xpp.setInput( new StringReader(sb.toString()) );
	         int eventType = xpp.getEventType();
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	          if(eventType == XmlPullParser.START_DOCUMENT) {
	              System.out.println("Start document");
	          } else if(eventType == XmlPullParser.START_TAG) {
	        	  if(xpp.getAttributeCount()>0){
	              System.out.println("Start tag "+xpp.getName()+" "+xpp.getAttributeName(0)+" : "+xpp.getAttributeValue(0));
	        	  }else{
	        		  System.out.println("Start tag "+xpp.getName());
	        	  }
	          } else if(eventType == XmlPullParser.END_TAG) {
	              System.out.println("End tag "+xpp.getName());
	          } else if(eventType == XmlPullParser.TEXT) {
	              System.out.println("Text "+xpp.getText());
	          }
	          eventType = xpp.next();
	         }
	         System.out.println("End document");
	     }catch(Exception e){
	    	 System.out.println("Error in xml parsing");
	    	 e.printStackTrace();
	     }
	}

}
