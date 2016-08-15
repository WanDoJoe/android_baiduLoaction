package com.sinosoft.loaction.utils;

import java.util.HashMap;
import java.util.Map;


//根据桩点经纬度 得到四个基本点
/**
 * 判断用户与目标点的距离
 * 
 * 根据桩点经纬度获取4个范围经纬度值
 * @author sinosoft_wan
 *
 */
public class NeighPosition {
//	119.747226,26.764138 

	public static double ONLY_LONGITUDE=119.747226;//桩点经度
	public static double ONLY_LATITUDE=26.764138;//桩点纬度
	
//	测试用数据117.401224,39.164762
//	public static double ONLY_LONGITUDE=117.401224;//桩点经度
//	public static double ONLY_LATITUDE=39.164762;//桩点纬度
	/**
	 * 是否在改点多少米范围内
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static boolean findNeighPosition(double longitude,double latitude){
		 //先计算查询点的经纬度范围  
        double r = 6371;//地球半径千米  
        double dis = 0.5;//0.5千米距离  
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(ONLY_LATITUDE*Math.PI/180));  
        dlng = dlng*180/Math.PI;//角度转为弧度  
        double dlat = dis/r;  
        dlat = dlat*180/Math.PI;  
        
        double minlat =ONLY_LATITUDE-dlat;  
        double maxlat = ONLY_LATITUDE+dlat;  
        double minlng = ONLY_LONGITUDE -dlng;  
        double maxlng = ONLY_LONGITUDE + dlng;  
        System.out.println("minlat:"+minlat
        		+"-maxlat:"+maxlat
        		+"-minlng:"+minlng
        		+"-maxlng:"+maxlng);
//        minlat:39.91067139197041-maxlat:39.91966460802959-minlng:116.39801237221822-maxlng:116.40973762778178
//       500m min-lat 0.004479 max0.004496    lng 0.005863   max 0.005862
        
        
        if((minlat<latitude&&maxlat>latitude)&&(minlng<longitude&&maxlng>longitude)){
        	return true;
        }else{
        	return false;
        }
	}
	//
	private final static double PI = 3.14159265358979323; // 圆周率
    private final static double R = 6371229; // 地球的半径
   /**
    * 根据两个位置的经纬度，来计算两地的距离（单位为M）
    */
//    public static double getDistance(double longt1, double lat1, double longt2,double lat2) 
    public static String getDistance( double longt2,double lat2) 
    {
        double x, y, distance;
        double longt1=ONLY_LONGITUDE;
        double lat1=ONLY_LATITUDE;
        x = (longt2 - longt1) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);
        float f=(float) distance;
//        if(distance-500>0){
//        	return "\n距离"+String.valueOf(Math.abs(distance-500))+"m";
        return "\n距离"+dis(f);
//        }else{
//        	return "已进入区域内";
//        }
//        return distance;
    }
    private static String dis(float f){
    	float ab=f-500;
    	if(ab>0){
    		if(ab>500){
    			return String.valueOf(Math.abs(ab/1000))+"Km";
    		}else{
    			return String.valueOf(Math.abs(ab))+"Km";
    		}
    		
    	}else{
    		return "";
    	}
    }
}
