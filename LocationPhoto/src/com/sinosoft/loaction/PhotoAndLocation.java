package com.sinosoft.loaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.sinosoft.loaction.bean.LocationBean;
import com.sinosoft.loaction.http.Protocol;
import com.sinosoft.loaction.service.LocationService;
import com.sinosoft.loaction.utils.FileUpdate;
import com.sinosoft.loaction.utils.HttpUtil.ProgressListener;
import com.sinosoft.loaction.utils.NeighPosition;

public class PhotoAndLocation extends Activity {
	private final int SDK_PERMISSION_REQUEST = 127;
	private String permissionInfo;
	private LocationService locationService;
	
	private ImageView showPhoto_ib;
	private Button location_bn;
	private Button camera_bn;
	private Button submit_bn;
	private List<String> poiList=new ArrayList<String>();
	private ListView showLoction_poi_list;
	private Button photolocation_back;
	
	private boolean isSuccessLocation=true;
	private String locationStr="";
	//拍照
	 private String path="";
	 private String img_name="/sdcard/sinosoft/img";
	 private String sdStatus = Environment.getExternalStorageState(); 
	 private String image_path="";
	//文件上传
	private List<File> imageFileList;
	private final String ENCODING = "UTF-8";
	private ProgressBar uploadProgress;
	private TextView progress;
	private RelativeLayout progressRL;
	
	private ArrayList<String> poidemo ;
	private boolean isFrist=true;
	private boolean isReStartLocation=true;
	private boolean isCloseLocation=true;
	private boolean isDialogLocation=false;
	private String location="";
	
	private ImageView location_list_load_iv;
	
//	MapView mMapView;
	TextureMapView mMapView;
    BaiduMap mBaiduMap;
    boolean isShowAndGoneMap=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoandloca_layout);
//		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		initView();
		setOnclick();
		
		// after andrioid m,must request Permiision on runtime 对5.0以上的手机权限进行管理。
		getPersimmions();
		setMapView();
	}

	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}
	
	
	private void setOnclick() {
		location_bn.setOnClickListener(new View.OnClickListener() {
			//TODO 定位
			@Override
			public void onClick(View arg0) {
//				if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
//				chooesLoction();// 定位SDK
//				if(isShowAndGoneMap){
//					mMapView.setVisibility(View.GONE);
//					isShowAndGoneMap=false;
//				}else{
//					mMapView.setVisibility(View.VISIBLE);
//					isShowAndGoneMap=true;
//				}
				isDialogLocation=false;
				if(isDialogLocation){
//					isDialogLocation=false;
					locationService.stop();
					showLoction_poi_list.setVisibility(View.GONE);
					location_list_load_iv.setVisibility(View.GONE);
					//停止动画
					location_list_load_iv.clearAnimation();
				}else{
					isDialogLocation=true;
					locationService.start();
//					showLoction_poi_list.setVisibility(View.VISIBLE);
//					location_list_load_iv.setVisibility(View.VISIBLE);
					  
					//开始执行动画  
					location_list_load_iv.startAnimation(animation);
				}
				// start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
//					startLocation.setText(getString(R.string.stoplocation));
//				} else {
//					locationService.stop();
//					startLocation.setText(getString(R.string.startlocation));
//				}
			}
		});
		camera_bn.setOnClickListener(new View.OnClickListener() {
			//TODO 拍照
			@Override
			public void onClick(View arg0) {
				 cameraPhoto();
			}
		});
		submit_bn.setOnClickListener(new View.OnClickListener() {
			//TODO 提交
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View arg0) {
				Log.w("submit_bn", location_bn.getText().toString());
				Log.w("submit_bn.setOnClickL", bean.toString());
				locationStr=location_bn.getText().toString();
				String filename="";
				String title="提交信息详情";
				if(null!=imageFileList){
				for (int i = 0; i < imageFileList.size(); i++) {
					Log.w("submit_bn.setOnClickL", imageFileList.get(i).getName());
					filename+=imageFileList.get(i).getName();
				}
				
//				new MyAsyncTask().execute(imageFileList);
				}else{
					filename="没有文件";
				}
//				117.40037,39.164748
				
				String s="【当前位置】：\n"+locationStr
				+"\n【位置详情】：\n"+ bean.toString()+"\n"+"【上传文件】：\n"+filename;
				new AlertDialog.Builder(PhotoAndLocation.this)
				.setTitle(title)
//				.setMessage(setColorMsg(s))
				.setMessage("是否在("+NeighPosition.ONLY_LONGITUDE+","+NeighPosition.ONLY_LATITUDE+")500米范围内："
							+NeighPosition.findNeighPosition(bean.getLongitude(), bean.getLatitude())
							+NeighPosition.getDistance(bean.getLongitude(), bean.getLatitude())
							
						)
//				.setMessage("是否在范围内："+NeighPosition.findNeighPosition(116.403861, 39.915154))
				.setNegativeButton("确定", null)
				.show();
			}
		});
		showLoction_poi_list.setOnItemClickListener(new OnItemClickListener() {
			//TODO 重新获定位
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				location_bn.setText(location+" "+poiList.get(arg2));
				isDialogLocation=false;
				locationService.stop();
				showLoction_poi_list.setVisibility(View.GONE);
				location_list_load_iv.setVisibility(View.GONE);
				//停止动画
				location_list_load_iv.clearAnimation();
			}
		});
		photolocation_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isDialogLocation){
					isDialogLocation=false;
					locationService.stop();
					showLoction_poi_list.setVisibility(View.GONE);
					location_list_load_iv.setVisibility(View.GONE);
					//停止动画
					location_list_load_iv.clearAnimation();
				}
			}
		});
	}
	protected SpannableStringBuilder setColorMsg(String str) {
		
//		"【当前位置】："+locationStr+"\n"
//				+"【位置详情】："+ bean.toString()+"\n"+"【上传文件】"
	        Log.e("", str);
	        int i_loc1=str.indexOf("【当前位置】：");
	        int loc1=i_loc1+"【当前位置】：".length();
	        
	        int i_loc2=str.indexOf("【位置详情】：");
	        int loc2=i_loc2+"【当前位置】：".length();
	        
	        int i_loc3=str.indexOf("【上传文件】：");
	        int loc3=i_loc3+"【上传文件】：".length();
	        
	        SpannableStringBuilder style=new SpannableStringBuilder(str); 
	        
	        style.setSpan(new ForegroundColorSpan(Color.RED),i_loc1,loc1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);   
	        style.setSpan(new ForegroundColorSpan(Color.RED),i_loc2,loc2,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
	        style.setSpan(new ForegroundColorSpan(Color.RED),i_loc3,loc3,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
	        return style;
	}
	Animation animation ;
	private void initView() {
		showPhoto_ib=(ImageView) findViewById(R.id.photo_vb);
		location_bn=(Button) findViewById(R.id.photo_location_bn);
		camera_bn=(Button) findViewById(R.id.photo_camera_bn);
		submit_bn=(Button) findViewById(R.id.photo_submit_bn);
		showLoction_poi_list=(ListView) findViewById(R.id.poi_list);
		photolocation_back=(Button) findViewById(R.id.photolocation_back);
//		
		location_list_load_iv=(ImageView) findViewById(R.id.location_list_load_iv);
		//加载动画XML文件,生成动画指令  
		animation = AnimationUtils.loadAnimation(this, R.anim.waiting_progressbar_anim);
	}

	//以下为sdk版本要求最低为6.0实使用
	@TargetApi(23)
	private void getPersimmions() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			ArrayList<String> permissions = new ArrayList<String>();
//			/***
//			 * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
//			 */
//			// 定位精确位置
//			if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//				permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//			}
//			if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//				permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//			}
//			/*
//			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
//			 */
//			// 读写权限
//			if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//				permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
//			}
//			// 读取电话状态权限
//			if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
//				permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
//			}
//			
//			if (permissions.size() > 0) {
//				requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
//			}
//		}
	}

//	@TargetApi(23)
//	private boolean addPermission(ArrayList<String> permissionsList, String permission) {
//		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请	
//			if (shouldShowRequestPermissionRationale(permission)){
//				return true;
//			}else{
//				permissionsList.add(permission);
//				return false;
//			}
//				
//		}else{
//			return true;
//		}
//	}
	
//	@TargetApi(23)
//	@Override
//	public void onRequestPermissionsResult(int requestCode,
//			String[] permissions, int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// -----------location config ------------
		locationService = ((LocationApplication) getApplication()).locationService; 
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
//		startLocation.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		if(isStart){
			locationService.start();
			isStart=false;
		}
		
	}
	boolean isStart=true;
	 
	/*****
	 * @see copy funtion to you project
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	LocationBean bean;
	private BDLocationListener mListener = new BDLocationListener() {
		//TODO 定位
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				bean=new LocationBean();
				bean.setTime(location.getTime());
				bean.setLongitude(location.getLongitude());
				bean.setLatitude(location.getLatitude());
				bean.setRadius(location.getRadius()+"");
				bean.setCountryCode(location.getCountryCode());
				bean.setCountry(location.getCountry());
				bean.setCityCode(location.getCityCode());
				bean.setCity(location.getCity());
				bean.setDistrict(location.getDistrict());
				bean.setStreet(location.getStreet());
				bean.setAddrStr(location.getAddrStr());
				bean.setLocationDescribe(location.getLocationDescribe());
				bean.setDirection(location.getDirection()+"");
				StringBuffer sb = new StringBuffer(256);
//				sb.append("time : ");
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
//				sb.append(location.getTime());
//				sb.append("\nerror code : ");
//				sb.append(location.getLocType());
//				sb.append("\nlatitude : ");
//				sb.append(location.getLatitude());
//				sb.append("\nlontitude : ");
//				sb.append(location.getLongitude());
//				sb.append("\nradius : ");
//				sb.append(location.getRadius());
//				sb.append("\nCountryCode : ");
//				sb.append(location.getCountryCode());
//				sb.append("\nCountry : ");
//				sb.append(location.getCountry());
//				sb.append("\ncitycode : ");
//				sb.append(location.getCityCode());
//				sb.append("\ncity : ");
//				sb.append(location.getCity());
//				sb.append("\nDistrict : ");
//				sb.append(location.getDistrict());
//				sb.append("\nStreet : ");
//				sb.append(location.getStreet());
//				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
//				sb.append("\nDescribe: ");
//				sb.append(location.getLocationDescribe());//详细地址
//				sb.append("\nDirection(not all devices have value): ");
//				sb.append(location.getDirection());
				StringBuffer poiBuffer=new StringBuffer();
//				poiBuffer.append("\nPoi: ");
				if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
					poidemo = new ArrayList<String>();
					for (int i = 0; i < location.getPoiList().size(); i++) {
						Poi poi = (Poi) location.getPoiList().get(i);
						poiBuffer.append(poi.getName()+ ";");
						poidemo.add(poi.getName());
//						poiList.add(poi.getName());
					}
					System.out.println(poiBuffer.toString());
					bean.setPoi(poiBuffer.toString());
				}
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//					sb.append("\nspeed : ");
//					sb.append(location.getSpeed());// 单位：km/h
//					sb.append("\nsatellite : ");
//					sb.append(location.getSatelliteNumber());
//					sb.append("\nheight : ");
//					sb.append(location.getAltitude());// 单位：米
//					sb.append("\ndescribe : ");
//					sb.append("gps定位成功");
				} 
				else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
//					sb.append("\noperationers : ");
//					sb.append(location.getOperators());
//					sb.append("\ndescribe : ");
//					sb.append("网络定位成功");
					if(isSuccessLocation){
					Toast.makeText(PhotoAndLocation.this, "网络定位成功", Toast.LENGTH_SHORT).show();
					isSuccessLocation=false;
					}
				}
				else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//					sb.append("\ndescribe : ");
//					sb.append("离线定位成功，离线定位结果也是有效的");
				} 
				
				else if (location.getLocType() == BDLocation.TypeServerError) {
//					sb.append("\ndescribe : ");
//					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//					Toast.makeText(PhotoAndLocation.this, "网络定位失败", Toast.LENGTH_SHORT).show();
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//					sb.append("\ndescribe : ");
//					sb.append("网络不同导致定位失败，请检查网络是否通畅");
//					Toast.makeText(PhotoAndLocation.this, "网络定位失败", Toast.LENGTH_SHORT).show();
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//					sb.append("\ndescribe : ");
//					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//					Toast.makeText(PhotoAndLocation.this, "网络定位失败", Toast.LENGTH_SHORT).show();
				}
				logMsg(sb.toString(),bean.getLongitude(),bean.getLatitude());
			}
		}

	};
	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	
	public void logMsg(String str,double lng,double lat) {
		try {
			//TODO 定位服务设置
			//地图相关
			MyLocationData locData = new MyLocationData.Builder()
            .accuracy(Float.valueOf(bean.getRadius()))
                    // 此处设置开发者获取到的方向信息，顺时针0-360
            .direction(100).latitude(bean.getLatitude())
            .longitude(bean.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			//地图相关
	        LatLng ll = new LatLng(bean.getLatitude(),
	        		bean.getLongitude());
	        //设置地图初始缩放
	        MapStatus.Builder builder = new MapStatus.Builder();
	        builder.target(ll).zoom(15.0f);
	        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
			if(isFrist){
				
			 //定位相关
			if (location_bn != null){
				location=str;
//				location_bn.setText(str+" "+poidemo.get(0));
				
				if(NeighPosition.findNeighPosition(lng, lat)){
					location_bn.setText("当前位置：在范围内 ");
				}else{
					location_bn.setText("当前位置:不在范围内，相差"+NeighPosition.getDistance(bean.getLongitude(), bean.getLatitude()));
				}
			}
			isFrist=false;
			}
			if(isDialogLocation){
			chooesLoction();
			//需求更改 重定位后需要再次关闭定位
			isReStartLocation=true;
			}
			if(isReStartLocation){
				locationService.stop();
				isReStartLocation=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void chooesLoction(){
			for (int i = 0; i < poidemo.size(); i++) {
				poiList.add(poidemo.get(i));
			}
			List<String> listTemp= new ArrayList<String>();  
			 Iterator<String> it=poiList.iterator();  
			 while(it.hasNext()){  
			  String a=it.next();  
			  if(listTemp.contains(a)){  
			   it.remove();  
			  }  
			  else{  
			   listTemp.add(a);  
			  }  
			 }  
			
		System.out.println(poiList.size());
		PoiListAdapter adapter=new PoiListAdapter(poiList, PhotoAndLocation.this);
		showLoction_poi_list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
	}
	
	public class PoiListAdapter extends BaseAdapter{
		List<String> mList;
		Context mContext;
		public PoiListAdapter(List<String> mList,Context mContext){
			this.mContext=mContext;
			this.mList=mList;
		}
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			HolderView v;
			if(arg1==null){
				v=new HolderView();
				arg1=LayoutInflater.from(mContext).inflate(R.layout.poi_list_item, null);
				v.name=(TextView) arg1.findViewById(R.id.poi_list_name);
				arg1.setTag(v);
			}else{
				v=(HolderView) arg1.getTag();
			}
			v.name.setText(mList.get(arg0));
			return arg1;
		}
		class HolderView{
			TextView name;
		}
	}
	
	
	/////////
	
	/**
     * 打开系统相册
     */
    private void systemPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, SYS_INTENT_REQUEST);
    }
 
    /**
     * 调用相机拍照
     */
   
    private void cameraPhoto() {
//        String sdStatus = Environment.getExternalStorageState();
        /* 检测sd是否可用 */
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
       
        Intent intent = new Intent();
		// 指定开启系统相机的Action
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		// 根据文件地址创建文件
		String name = new DateFormat().format("yyyyMMdd_HHmmss",Calendar.getInstance(Locale.CHINA)) + ".png";  
		path=img_name+"/"+name;
		Log.v("", "intent path："+path);
		File dirs = new File(img_name);
		dirs.mkdirs();// 创建文件夹  
		File file= new File(path);
		if (dirs.exists()) {
			file.delete();
		}
		// 把文件地址转换成Uri格式
		Uri uri = Uri.fromFile(file);
		// 设置系统相机拍摄照片完成后图片文件的存放地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, 0);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode==0){
    		try {
    			Log.e("", "path:"+path);
        		File file = new File(path);
        		System.out.println("length:"+file.length());
        		if(file.length()>0){
        		imageFileList=new ArrayList<File>();
        		imageFileList.add(file);
        		image_path=path;
        		Log.e("", "file.getName():"+file.getName());
        		 System.out.println(image_path);
//    			Uri uri = Uri.fromFile(file);
//     			showPhoto_ib.setImageURI(uri);
        		Bitmap bitmap=BitmapFactory.decodeFile(image_path,getBitmapOption(2)); //将图片的长和宽缩小味原来的1/2
        		showPhoto_ib.setImageBitmap(bitmap);
        		}else{
        			Log.e("", "没有拍照");
        		}
    			
			} catch (Exception e) {
				Log.e("Exception", e.toString());
			}
    	    
			
    	}
    }
    private Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
    
    /// 提交发送
    
    /**
	 * 异步提交
	 * @author Tercel
	 *
	 */
	
	public class MyAsyncTask extends AsyncTask<List<File>, Long, String>{		
        @Override  
        protected void onPreExecute() {  
//        	upload.setClickable(false);
        	Toast.makeText(PhotoAndLocation.this, "开始上传附件...", Toast.LENGTH_SHORT).show();
        }
        
        
		@Override
		protected String doInBackground(List<File>... params) {
			ProgressListener listener =new ProgressListener() {

				//上传进度监听器			
				@Override
				public void cumulative(long num) {
					Log.i("uplpad-cumulative", String.valueOf(num));		//上传量
				}
				@Override
				public void progress(int progress) {
					Log.i("uplpad-progress", String.valueOf(progress));
					publishProgress((long)progress);			//进度
				}
			};
			try {				
				String url=Protocol.UPDATA_URL+"/MobileOAServer/uploadify!uploadify";
				return FileUpdate.post(url, params[0], "utf-8", listener,"userid",location_bn.getText().toString()
						,bean.toString());
			} catch (Exception e) {				
				e.printStackTrace();
				Log.e("notesend", "My AsyncTask doInBackground Exception");
				Toast.makeText(PhotoAndLocation.this, "上传失败！！！", Toast.LENGTH_SHORT).show();
				//异常自己处理
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			progressRL.setVisibility(View.VISIBLE);
			uploadProgress.setProgress((int)(long)values[0]);
			progress.setText(values[0] + "%");
		}
		//TODO 图片上传
		@Override
		protected void onPostExecute(String result) {
//			upload.setClickable(true);
			uploadProgress.setProgress(0);
			progress.setText("");
//			System.out.println(result);
			progressRL.setVisibility(View.GONE);
			if("error".equals(result)){
				Toast.makeText(PhotoAndLocation.this, "附件上传失败", Toast.LENGTH_LONG).show();
				return;
			}
			if("".equals(result)){
				Toast.makeText(PhotoAndLocation.this, "服务异常,请联系管理员", Toast.LENGTH_LONG).show();
				return;
			}if("[]".equals(result)){
				Toast.makeText(PhotoAndLocation.this, "服务异常,请联系管理员", Toast.LENGTH_LONG).show();
				return;
			}
			String attachment="";
			String msg="";
			try {
//				JSONResponse jsonResponse=JSONResponse.getResponse(result);
//				if(jsonResponse.isSuccess()){
//					JSONObject object=jsonResponse.getDataJSONObject();
//					attachment=object.getString("att_str");
//					msg=object.getString("result");
//				}else{
//					Toast.makeText(PhotoAndLocation.this, "附件上传失败", Toast.LENGTH_LONG).show();
//					return;
//				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
//			attachment=attachment.substring(0, attachment.length()-1);
			System.out.println(attachment);
			try {
		
//			sendNote(URLEncoder.encode(Base64Util.getFromBase64(attachment),"utf-8"));//上传成功后提交便签
			Toast.makeText(PhotoAndLocation.this, "附件上传成功...", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// 
				e.printStackTrace();
			}
		}
		@Override
		protected void onCancelled(String result) {
			try {
				System.out.println(result);
			} catch (Exception e) {
			}
			
		}
	}
	
    private void setMapView(){
    	//TODO 地图显示
//    	mCurrentMode = LocationMode.NORMAL;
    	// 地图初始化
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
//        mMapView.set
        
        //添加桩点
      //定义Maker坐标点  
        LatLng pointInit=new LatLng(NeighPosition.ONLY_LATITUDE, NeighPosition.ONLY_LONGITUDE);
        //构建Marker图标  
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.postion_icon);  
        //构建MarkerOption，用于在地图上添加Marker  
        OverlayOptions optionInit = new MarkerOptions().position(pointInit).icon(bitmap);  
        //在地图上添加Marker，并显示  
        mBaiduMap.addOverlay(optionInit);
        
        
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        
        //桩点范围
        CircleOptions circleOptions=new CircleOptions().radius(500).center(pointInit).stroke(new Stroke(5, 0x3c44cef6))
        		.fillColor(0x3c44cef6);
        mBaiduMap.addOverlay(circleOptions);
        
       Button b=(Button) findViewById(R.id.photo_gonemapview_bn);
       b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isShowAndGoneMap){
					mMapView.setVisibility(View.GONE);
					isShowAndGoneMap=false;
				}else{
					mMapView.setVisibility(View.VISIBLE);
					isShowAndGoneMap=true;
				}
			}
		});
        
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
    	// 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    	super.onDestroy();
    }
}
