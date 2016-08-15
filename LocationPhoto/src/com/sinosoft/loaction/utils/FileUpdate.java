package com.sinosoft.loaction.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.sinosoft.loaction.utils.HttpUtil.ProgressListener;


public class FileUpdate {
	
    /**
	 * 连接超时
	 */
	public static final int CONNECTION_TIME_OUT = 1000 * 120;
	
	
	/**
	 * 响应超时
	 */
	public static final int SO_TIMEOUT = 1000 *120 ;
	
	
	/**
	 * 默认编码
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	

	/**
	 * 
	 * @param url
	 * @param list
	 * @param encoding
	 * @param listener
	 * @return
	 * @throws Exception
	 */
	public static String post(String url,List<File> list,String encoding,ProgressListener listener,String userid,
			String location,String locationinfo){
//		HttpParams params = new BasicHttpParams();												//实例化Post参数对象
//		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);  				//设置请求超时
//        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT); 									//设置响应超时
        HttpClient client = new DefaultHttpClient();									//实例化一个连接对象
        HttpPost post = new HttpPost(url);														//根据Post参数,实例化一个Post对象
        try {
        HttpParams params=client.getParams();
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
    	HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);  				//设置请求超时
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT); 	
        	
        CustomMultipartEntity entity = new CustomMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
	               null, Charset.forName("UTF-8"));								//实例化请求实体,请求正文
        System.out.println(entity.getContentType());
        entity.setProgressListener(listener);													//设置进度回调接口
        for(int i=0; i<list.size(); i++){
        	System.out.println("post"+list.get(i).getName()+"--");
        	ContentBody body = new FileBody(list.get(i),"multipart/form-data;charset=utf-8",encoding);
        	//ContentBody body = new StringBody("属性",Charset.forName(encoding));
        	ContentBody body2 = new StringBody(location);
        	System.out.println("location="+location);
        	entity.addPart("file",body);	//表单字段名	
        	entity.addPart("location",body2);
        }
        ContentBody id = new StringBody(userid,Charset.forName(encoding));
        entity.addPart("userid",id);
        post.setEntity(entity);																	//将请求实体保存到Post的实体参数中
      
		try {
			HttpResponse response = client.execute(post);										//执行Post方法
			System.out.println("response="+response.getStatusLine().getStatusCode() );
			if(response.getStatusLine().getStatusCode()==200){
//				return EntityUtils.toString(response.getEntity(), encoding);						//根据字符编码返回字符串
				String result = EntityUtils.toString(response.getEntity(),DEFAULT_ENCODING);
				
//				result=getUTF8XMLString(result);
//				result=URLEncoder.encode(result, DEFAULT_ENCODING);//加密
//				result=URLDecoder.decode(result, DEFAULT_ENCODING);//解密并且消除 乱码		
				Log.e("post_file", "result="+result);
				return result;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally{
			client.getConnectionManager().shutdown();											//释放连接所有资源
		}
//		
        } catch (Exception e) {
			e.printStackTrace();
		}
        return "error";
	}	
	
	/** 
	    * Get XML String of utf-8 
	    *  
	    * @return XML-Formed string 
	    */  
	    public static String getUTF8XMLString(String xml) {  
	    // A StringBuffer Object  
	    StringBuffer sb = new StringBuffer();  
	    sb.append(xml);  
	    String xmString = "";  
	    String xmlUTF8="";  
	    try {  
	    xmString = new String(sb.toString().getBytes("UTF-8"));  
	    xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");  
	    System.out.println("utf-8 编码：" + xmlUTF8) ;  
	    } catch (UnsupportedEncodingException e) {  
	    e.printStackTrace();  
	    }  
	    // return to String Formed  
	    return xmlUTF8;  
	    }

}
