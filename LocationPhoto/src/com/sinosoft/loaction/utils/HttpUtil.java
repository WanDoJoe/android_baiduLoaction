package com.sinosoft.loaction.utils;
/**
 * @author AirWizardWong
 * @since 2014-08-07
 */
/**
 * wan'dong'qiao'
 * 弃用 请勿使用
 * 产生连接超时异常
 * 2015-06-25
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	private static final int DEFAULT_SOCKET_TIMEOUT = 120 * 1000;
	public static HttpClient httpClient; //= new DefaultHttpClient();
	public static String postAsForm(final String url, final String data)
			throws ClientProtocolException, IOException, Exception {
		/**
		 * Callable的任务执行后可返回值 运行Callable任务可拿到一个Future对象
		 */
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					@Override
					public String call() throws Exception {
						List<NameValuePair> formparams = new ArrayList<NameValuePair>();
						formparams.add(new BasicNameValuePair("xmas-" + "json",
								data));
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
								formparams, "UTF-8");
						HttpPost httppost = new HttpPost(url);
						httppost.setEntity(entity);

						HttpParams httpParams = new BasicHttpParams();
					    // 超时设置
						/* 从连接池中取连接的超时时间 */
						ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
					    /* 连接超时 */
					    HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
					    /* 请求超时 */
					    HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
					    // 创建HttpClient对象
						httpClient = new DefaultHttpClient(httpParams);
		
						HttpResponse response = httpClient.execute(httppost);
						if (response.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(response
									.getEntity());
							// Log.e("result===>", result);
							return result;
						}

						return null;
					}
				});

		new Thread(task).start();
		return task.get();

	}
	
	
	///
	
	/**
	 * 连接超时
	 */
	public static final int CONNECTION_TIME_OUT = 1000 * 30;
	
	
	/**
	 * 响应超时
	 */
	public static final int SO_TIMEOUT = 1000 *30 ;
	
	
	/**
	 * 默认编码
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * 请进度监听
	 * @author Tercel
	 *
	 */
	public interface ProgressListener {
		public void cumulative(long num);
		
		public void progress(int progress);
	}
	
	
	
	/**
	 * 发起一个post请求,以表单文件上传,并返回服务器返回的字符串
	 * @param url		本次请求的URL路径
	 * @param map		请求的参数,该Map集合中Value值只会取两种类型,String & File<br>
	 * <B>注意:</B><br> 
	 * <li>1. 如果Value不是File类型,则会调用Value.toString(),如果你保存的是个POJO对象的话,请重写toString()<br>
	 * <li>2. 如果Value是File类型,并且文件不存在的话,会抛出 FileNotFoundException 异常<br>
	 * @param encoding	请求和接收字符串的编码 格式,如果因为编码不正确,则会默认使用UTF-8进行编码
	 * @param listener 请用进度监听器
	 * @return			返回请求的结果字符串
	 * @throws Exception	可能抛出多种网络或IO异常
	 */	
	public static String post(String url,Map<String,Object> map,String encoding,ProgressListener listener) throws Exception{
		HttpParams params = new BasicHttpParams();												//实例化Post参数对象
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);  				//设置请求超时
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT); 									//设置响应超时
        HttpClient client = new DefaultHttpClient(params);										//实例化一个连接对象
        HttpPost post = new HttpPost(url);														//根据Post参数,实例化一个Post对象
        
        CustomMultipartEntity entity = new CustomMultipartEntity();								//实例化请求实体,请求正文
        entity.setProgressListener(listener);													//设置进度回调接口
		if(map != null && !map.isEmpty()){															
			for(Map.Entry<String, Object> entry : map.entrySet()){								//迭代Map集合
				Object obj = entry.getValue();													//获取集合中的值
				ContentBody body = null;
				
				//*获取集合中的Value,如果当前的Value是File类型,则new 一个FileBody,如果是字符串类型,则new一个StringBody
				//*并将该对象保存到请求实体中 
				 
				if(obj!=null){
					if(obj instanceof File){														
						File file = (File) obj;
						if(file.exists()){
							body = new FileBody(file);										
						}else{
							throw new FileNotFoundException("File Not Found");
						}
					}else{													
						body = new StringBody(entry.getValue().toString(),Charset.forName(encoding));
					}
					entity.addPart(entry.getKey(),body);										//将正文保存到请求实体类中
				}
			}
		}
		post.setEntity(entity);																	//将请求实体保存到Post的实体参数中
		try {
			HttpResponse response = client.execute(post);										//执行Post方法	
			return EntityUtils.toString(response.getEntity(), encoding);						//根据字符编码返回字符串
		} catch (Exception e) {
			throw e;
		} finally{
			client.getConnectionManager().shutdown();											//释放连接所有资源
		}
	}	
	
	
	/**
	 * 发一起个Post请求,简单的Text方式
	 * @param url			请求URL
	 * @param parameters	请求参数
	 * @param encoding		字符编码
	 * @return
	 * @throws Exception
	 */
	public static String post(String url,List<NameValuePair> parameters,String encoding) throws Exception{
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SO_TIMEOUT);  
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		
		HttpPost post = new HttpPost(url);		
		HttpResponse response;
		try {
			UrlEncodedFormEntity encode = new UrlEncodedFormEntity(parameters, encoding);
			post.setEntity(encode);
			response = httpClient.execute(post);
			return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			throw e;
		}		
	}
	
	
	
	/**
	 * 发一起个Post请求,简单的Text方式,请求数据和返回数据均以UTF-8编码,
	 * @param url			请求URL
	 * @param parameters	请求参数
	 * @return	Json格式字符器
	 * @throws Exception
	 */
	public static String post(String url,List<NameValuePair> parameters)throws Exception{
		return post(url,parameters,DEFAULT_ENCODING);
	}	
	
	
	
	
	
	
	/**
	 * 发起一个post请求,以表单文件上传,并返回服务器返回的字符串
	 * @param url		本次请求的URL路径
	 * @param map		请求的参数,该Map集合中Value值只会取两种类型,String & File<br>
	 * <B>注意:</B><br> 
	 * <li>1. 如果Value不是File类型,则会调用Value.toString(),如果你保存的是个POJO对象的话,请重写toString()<br>
	 * <li>2. 如果Value是File类型,并且文件不存在的话,会抛出 FileNotFoundException 异常<br>
	 * @param listener 请用进度监听器
	 * @return			返回请求的结果字符串
	 * @throws Exception	可能抛出多种网络或IO异常
	 */	
	public static String post(String url, Map<String, Object> map, ProgressListener listener) throws Exception {
		return post(url, map, DEFAULT_ENCODING, listener);
	}
	
	
	/**
	 * 发起一个post请求,以表单文件上传,并返回服务器返回的字符串
	 * @param url		本次请求的URL路径
	 * @param map		请求的参数,该Map集合中Value值只会取两种类型,String & File<br>
	 * <B>注意:</B><br> 
	 * <li>1. 如果Value不是File类型,则会调用Value.toString(),如果你保存的是个POJO对象的话,请重写toString()<br>
	 * <li>2. 如果Value是File类型,并且文件不存在的话,会抛出 FileNotFoundException 异常<br>
	 * @return				返回请求的结果字符串
	 * @throws Exception	可能抛出多种网络或IO异常
	 */	
	public static String post(String url, Map<String, Object> map) throws Exception {
		return post(url, map, DEFAULT_ENCODING, null);
	}

}
