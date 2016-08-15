package com.sinosoft.loaction.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import com.sinosoft.loaction.utils.HttpUtil.ProgressListener;




/**
 * 带进度的文件上传表单正文<br>
 * @author MSD_小谢
 *
 */
public class CustomMultipartEntity extends MultipartEntity {	
	private ProgressListener listener;	
	
	public void setProgressListener(ProgressListener listener){
		this.listener = listener;
	}
//	public CustomMultipartEntity(){
//		new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
//	               null, Charset.forName("UTF-8"));
//	}
	//继承父类构造  以免在上传文件时出现 文件名中文乱码
	public CustomMultipartEntity() {
		super();
	}
	public CustomMultipartEntity(HttpMultipartMode mode, String boundary,
			Charset charset) {
		super(mode, boundary, charset);
	}


	public CustomMultipartEntity(HttpMultipartMode mode) {
		super(mode);
	}


	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream));
	}	
	
	private class CountingOutputStream extends FilterOutputStream {
		private long transferred;
		
		public CountingOutputStream(OutputStream out) {
			super(out);
			this.transferred = 0;			
		}		
		
		@Override
		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			if(listener != null){
				listener.cumulative(this.transferred);			
				listener.progress((int) ((transferred / (float) getContentLength()) * 100));
			}
		}		
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			
			out.write(b, off, len);
			this.transferred += len;
			if(listener != null){
				listener.cumulative(this.transferred);
				listener.progress((int) ((transferred / (float) getContentLength()) * 100));
			}
		}
	}	
}
