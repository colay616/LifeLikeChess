/**
 * 
 * Bmob移动后端云服务RestAPI工具类
 * 
 * 提供简单的RestAPI增删改查工具，可直接对表、云函数、支付订单、消息推送进行操作。
 * 使用方法：先初始化initBmob，后调用其他方法即可。
 * 具体使用方法及传参格式详见Bmob官网RestAPI开发文档。
 * http://docs.bmob.cn/restful/developdoc/index.html?menukey=develop_doc&key=develop_restful
 * 
 * @author 金鹰
 * @version V1.3.1
 * @since 2015-07-07
 * 
 */
package com.hyphenate.easeui.bmob;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Bmob {
	private static boolean IS_INIT = false;
	private static int TIME_OUT = 10000;

	private static String STRING_EMPTY = "";
	private static String APP_ID = STRING_EMPTY;
	private static String REST_API_KEY = STRING_EMPTY;
	private static String MASTER_KEY = STRING_EMPTY;
	
	private static final String BMOB_APP_ID_TAG = "X-Bmob-Application-Id";
	private static final String BMOB_REST_KEY_TAG = "X-Bmob-REST-API-Key";
	private static final String BMOB_MASTER_KEY_TAG = "X-Bmob-Master-Key";
	private static final String CONTENT_TYPE_TAG = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_DELETE = "DELETE";
	
	private static final String UTF8 = "UTF-8";
	private static final String CHAR_RISK = ":";

	public static final String MSG_NOT_FOUND = "Not Found";
	public static final String MSG_FILE_NOT_FOUND = "file Not Found";
	public static final String MSG_ERROR = "Error";
	public static final String MSG_UNREGISTERED = "Unregistered";
	/**
	 * 是否初始化Bmob
	 * @return 初始化结果
	 */
	public static boolean isInit(){
		return IS_INIT;
	}
	
	/**
	 * 初始化Bmob
	 * @param appId 填写 Application ID
	 * @param apiKey 填写 REST API Key
	 * @return 注册结果
	 */
	public static boolean initBmob(String appId, String apiKey){
		return initBmob(appId, apiKey, 10000);
	}
	
	/**
	 * 初始化Bmob
	 * @param appId 填写 Application ID
	 * @param apiKey 填写 REST API Key
	 * @param timeout 设置超时（1000~20000ms）
	 * @return 注册结果
	 */
	public static boolean initBmob(String appId, String apiKey, int timeout){
		APP_ID = appId;
		REST_API_KEY = apiKey;
		if(!APP_ID.equals(STRING_EMPTY) && !REST_API_KEY.equals(STRING_EMPTY)){
			IS_INIT = true;
		}
		if(timeout > 1000 && timeout < 20000){
			TIME_OUT = timeout;
		}
		try{
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}catch(Exception e){
			IS_INIT = false;
		}
		return isInit();
	}

	/**
	 * 初始化Bmob Master权限
	 * @param masterKey 填写 Master Key
	 */
	public static void initMaster(String masterKey){
		MASTER_KEY = masterKey;
	}
    
	/**
	 * 查询表全部记录(最多仅查询1000条记录)
	 * @param tableName 表名
	 * @return JSON格式结果
	 */
	public static String findAll(String tableName){
		return find(tableName,STRING_EMPTY);
	}

	/**
	 * 条件查询表全部记录(最多仅查询1000条记录)
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @return JSON格式结果
	 */
	public static String findAll(String tableName, String where){
		return find(tableName, where, STRING_EMPTY);
	}
	
	/**
	 * 查询表单条记录
	 * @param tableName 表名
	 * @param objectId objectId
	 * @return JSON格式结果
	 */
	public static String findOne(String tableName, String objectId){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName+"/"+objectId;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(findOne)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(findOne)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	

	/**
	 * 查询表限定数量记录
	 * @param tableName 表名
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int limit){
		return find(tableName, "{}", 0,limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int limit){
		return find(tableName, where, 0,limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录，返回指定列
	 * @param tableName 表名
	 * @param keys 返回列 （例：score,name）
	 * @param where 条件JOSN格式
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int limit){
		return findColumns(tableName, keys, where, 0,limit, STRING_EMPTY);
	}

	/**
	 * 查询表区间记录
	 * @param tableName 表名
	 * @param skip 跳过记录数
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int skip, int limit){
		return find(tableName, "{}", skip, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表区间记录
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @param skip 跳过记录数
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int skip, int limit){
		return find(tableName, where, skip, limit, STRING_EMPTY);
	}
	

	/**
	 * 条件查询表区间记录,返回指定列
	 * @param tableName 表名
	 * @param keys 返回列 （例：score,name）
	 * @param where 条件JOSN格式
	 * @param skip 跳过记录数
	 * @param limit 查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int skip, int limit){
		return findColumns(tableName, keys, where, skip, limit, STRING_EMPTY);
	}

	/**
	 * 排序查询表记录
	 * @param tableName 表名
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String order){
		return find(tableName, "{}", 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, String order){
		return find(tableName, where, 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录,返回指定列
	 * @param tableName 表名
	 * @param keys 返回列 （例：score,name）
	 * @param where 条件JOSN格式
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, String order){
		return findColumns(tableName, keys, where, 0, 1000, order);
	}
	
	/**
	 * 排序查询表限定数量记录
	 * @param tableName 表名
	 * @param limit 查询记录数（1~1000）
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int limit, String order){
		return find(tableName, "{}", 0, limit, order);
	}
	
	/**
	 * 条件排序查询表限定数量记录
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @param limit 查询记录数（1~1000）
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int limit, String order){
		return find(tableName, where, 0, limit, order);
	}

	/**
	 * 条件排序查询表限定数量记录,返回指定列
	 * @param tableName 表名
	 * @param keys 返回列 （例：score,name）
	 * @param where 条件JOSN格式
	 * @param limit 查询记录数（1~1000）
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int limit, String order){
		return findColumns(tableName, keys, where, 0, limit, order);
	}
	
	/**
	 * 条件排序查询表区间记录
	 * @param tableName 表名
	 * @param where 条件JOSN格式
	 * @param skip 跳过记录数
	 * @param limit 查询记录数（1~1000）
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int skip, int limit, String order){
		return findColumns(tableName, STRING_EMPTY, where, skip, limit, order);
	}
	

	/**
	 * 条件排序查询表区间记录,返回指定列
	 * @param tableName 表名
	 * @param keys 返回列 （例：score,name）
	 * @param where 条件JOSN格式
	 * @param skip 跳过记录数
	 * @param limit 查询记录数（1~1000）
	 * @param order 排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int skip, int limit, String order){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			skip = skip < 0 ? 0 : skip;
			limit = limit < 0 ? 0 : limit;
			limit = limit > 1000 ? 1000 : limit;
			where = where.equals(STRING_EMPTY) ? "{}" : where;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName+"?where="+urlEncoder(where)+"&limit="+limit + "&skip="+skip + "&order=" + order + "&keys=" + keys;

			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(findColumns)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(findColumns)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	/**
	 * BQL查询表记录
	 * @param BQL SQL语句。例如：select * from Student where name=\"张三\" limit 0,10 order by name
	 * @return JSON格式结果
	 */
	public static String findBQL(String BQL){
		return findBQL(BQL, STRING_EMPTY);
	}
	
	/**
	 * BQL查询表记录
	 * @param BQL SQL语句。例如：select * from Student where name=? limit ?,? order by name
	 * @param value 参数对应SQL中?以,为分隔符。例如"\"张三\",0,10"
	 * @return JSON格式结果
	 */
	public static String findBQL(String BQL, String value){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			BQL = urlEncoder(BQL) + "&values=[" + urlEncoder(value)+"]";
			String mURL = "https://api.bmob.cn/1/cloudQuery?bql="+BQL ;
			
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(findBQL)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(findBQL)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}


	/**
	 * 获取服务器时间
	 * @return 
	 */
	public static String getServerTime(){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/timestamp/";
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(getServerTime)" + e.getMessage();
			}catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(getServerTime)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	/**
	 * 查询表记录数
	 * @param tableName 表名
	 * @return 统计值
	 */
	public static int count(String tableName){
		return count(tableName, "{}");
	}
	
	/**
	 * 条件查询记录数
	 * @param tableName 表名
	 * @param where 查询条件(JSON格式)
	 * @return 统计值
	 */
	public static int count(String tableName, String where){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName+"?where="+urlEncoder(where)+"&count=1&limit=0";
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(count)" + e.getMessage();
			    System.err.println("Warn: "+ result);
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(count)" + e.getMessage();
			    System.err.println("Warn: "+ result);
			}
		}else{
			result = MSG_UNREGISTERED;
		    System.err.println("Warn: "+ result);
		}
		int count = 0;
		if(result.contains(MSG_NOT_FOUND) || result.contains(MSG_ERROR) || result.equals(MSG_UNREGISTERED)){
			return count;
		}else{
			if(result.contains("count")){
				count = Integer.valueOf(result.replaceAll("[^0-9]",STRING_EMPTY));
			}
		}
		return count;
	}
	
	/**
	 * 修改记录
	 * @param tableName 表名
	 * @param objectId objectId
	 * @param paramContent JSON格式参数
	 * @return JSON格式结果
	 */
	public static String update(String tableName, String objectId, String paramContent){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName+"/"+objectId;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_PUT);
			    conn.setDoOutput(true);
			    conn.connect();
			    printWriter(conn, paramContent);
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(update)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(update)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 插入记录
	 * @param tableName 表名
	 * @param paramContent JSON格式参数
	 * @return JSON格式结果
	 */
	public static String insert(String tableName, String paramContent){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
			    conn.setDoOutput(true);
			    conn.connect();
			    printWriter(conn, paramContent);
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(insert)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(insert)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}


	/**
	 * 删除记录
	 * @param tableName 表名
	 * @param objectId objectId
	 * @return JSON格式结果
	 */
	public static String delete(String tableName, String objectId){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/"+tableName+"/"+objectId;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_DELETE);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(delete)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(delete)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	/**
	 * 查询支付订单
	 * @param payId 交易编号
	 * @return JSON格式结果
	 */
	public static String findPayOrder(String payId){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/pay/"+payId;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
			    conn.connect();
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(findPayOrder)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(findPayOrder)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	

	/**
	 * 推送消息
	 * @param  data
	 * 详细使用方法参照 http://docs.bmob.cn/restful/developdoc/index.html?menukey=develop_doc&key=develop_restful#index_消息推送简介
	 * @return JSON格式结果
	 */
	public static String pushMsg(String data){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/push";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
			    printWriter(conn, data);
		        result = getResultFromConnection(conn);
			    conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(pushMsg)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(pushMsg)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
	    return result;
	}
	
	
	/**
	 * 调用云端代码
	 * @param funcName 云函数名
	 * @param paramContent JSON格式参数
	 * @return JSON格式结果
	 */
	public static String callFunction(String funcName, String paramContent){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/functions/"+funcName;
			try {
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
			    conn.setDoOutput(true);
			    conn.connect();
			    printWriter(conn, paramContent);
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(callFunction)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(callFunction)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	/**
	 * 发送短信
	 * @param mobileNum 电话号码
	 * @param content 短信内容
	 * @return JSON格式结果
	 */
	public static String requestSms(String mobileNum, String content){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/requestSms";
			try {
//				BSONObject bson = new BSONObject();
//				bson.put("mobilePhoneNumber", mobileNum);
//				bson.put("content", mobileNum);
//			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
//			    conn.setDoOutput(true);
//			    conn.connect();
//			    printWriter(conn, bson.toString());

				JSONObject json=new JSONObject();
				json.put("mobilePhoneNumber", mobileNum);
				json.put("content", mobileNum);
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, json.toString());

	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_NOT_FOUND + CHAR_RISK + "(callFunction)" + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + "(callFunction)" + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}	

	
	public static String uploadFile2(String file){
		String result = STRING_EMPTY;
		if(isInit()){
			HttpURLConnection conn = null;
			//获取文件名
	        String fileName = file.trim();  
	        fileName = fileName.substring(fileName.lastIndexOf("/")+1);  
	        
			String mURL = "https://api.bmob.cn/2/files/"+fileName;
			try {
				
				FileInputStream fis = new FileInputStream(file);
				
			    conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
			    conn.setDoOutput(true);
			    conn.connect();
			    
			    //一次读多个字节 
				byte[] tempbytes = new byte[1]; 
				int byteread = 0; 
				int i=0;
				ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
				OutputStream os = conn.getOutputStream();
				 while((byteread=fis.read(tempbytes))!=-1)  
			    {  
					 os.write(tempbytes);
			    }  
				
				os.flush();
				os.close();
				fis.close();			    
			    			    
	            result = getResultFromConnection(conn);
		        conn.disconnect();
			} catch (FileNotFoundException e){
			    result = MSG_FILE_NOT_FOUND + CHAR_RISK + e.getMessage();
			} catch (Exception e) {
			    result = MSG_ERROR + CHAR_RISK + e.getMessage();
			}
		}else{
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 复合查询-或
	 * @param where1 JSON格式条件一
	 * @param where2 JSON格式条件二
	 * @return 复合或字符串
	 */
	public static String whereOr(String where1, String where2){
		return "{\"$or\":["+where1+","+where2+"]}";
	}

	/**
	 * 复合查询-与
	 * @param where1 JSON格式条件一
	 * @param where2 JSON格式条件二
	 * @return 复合与字符串
	 */
	public static String whereAnd(String where1, String where2){
		return "{\"$and\":["+where1+","+where2+"]}";
	}
	


	/**
	 * 操作符-小于
	 * @param value 目标值
	 * @return 复合小于字符串
	 */
	public static String whereLess(int value){
		return "{\"$lt\":"+value+"}";
	}
	/**
	 * 操作符-小于
	 * @param value 目标值
	 * @return 复合小于字符串
	 */
	public static String whereLess(String value){
		return "{\"$lt\":"+value+"}";
	}

	/**
	 * 操作符-小于等于
	 * @param value 目标值
	 * @return 复合小于等于字符串
	 */
	public static String whereLessEqual(int value){
		return "{\"$lte\":"+value+"}";
	}
	/**
	 * 操作符-小于等于
	 * @param value 目标值
	 * @return 复合小于等于字符串
	 */
	public static String whereLessEqual(String value){
		return "{\"$lte\":"+value+"}";
	}

	/**
	 * 操作符-大于
	 * @param value 目标值
	 * @return 复合大于字符串
	 */
	public static String whereGreate(int value){
		return "{\"$gt\":"+value+"}";
	}
	/**
	 * 操作符-大于
	 * @param value 目标值
	 * @return 复合大于字符串
	 */
	public static String whereGreate(String value){
		return "{\"$gt\":"+value+"}";
	}

	/**
	 * 操作符-大于等于
	 * @param value 目标值
	 * @return 复合大于等于字符串
	 */
	public static String whereGreateEqual(int value){
		return "{\"$gte\":"+value+"}";
	}
	/**
	 * 操作符-大于等于
	 * @param value 目标值
	 * @return 复合大于等于字符串
	 */
	public static String whereGreateEqual(String value){
		return "{\"$gte\":"+value+"}";
	}

	/**
	 * 操作符-不等于
	 * @param value 目标值
	 * @return 复合不等于字符串
	 */
	public static String whereNotEqual(int value){
		return "{\"$ne\":"+value+"}";
	}
	/**
	 * 操作符-不等于
	 * @param value 目标值
	 * @return 复合不等于字符串
	 */
	public static String whereNotEqual(String value){
		return "{\"$ne\":"+value+"}";
	}

	/**
	 * 操作符-包含
	 * @param value 目标数组值(例：new int[]{1,3,5,7})
	 * @return 复合包含字符串
	 */
	public static String whereIn(int[] value){
		String result = STRING_EMPTY;
		for(int i=0; i<value.length; i++){
			result = i == value.length-1 ? String.valueOf(result + value[i]) : result + value[i]+",";
		}
		return "{\"$in\":["+result+"]}";
	}
	/**
	 * 操作符-包含
	 * @param value 目标数组值(例：new String[]{"张三","李四","王五"})
	 * @return 复合包含字符串
	 */
	public static String whereIn(String[] value){
		String result = STRING_EMPTY;
		for(int i=0; i<value.length; i++){
			result = i == value.length-1 ? result +  "\"" + value[i] +"\"" : result + "\"" + value[i]+"\",";
		}
		return "{\"$in\":["+result+"]}";
	}
	/**
	 * 操作符-包含
	 * @param value 目标数组值(例："1,3,5,7")
	 * @return 复合包含字符串
	 */
	public static String whereIn(String value){
		return "{\"$in\":["+value+"]}";
	}
	

	/**
	 * 操作符-不包含
	 * @param value 目标数组值(例：new int[]{1,3,5,7})
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(int[] value){
		String result = STRING_EMPTY;
		for(int i=0; i<value.length; i++){
			result = i == value.length-1 ? String.valueOf(result + value[i]) : result + value[i]+",";
		}
		return "{\"$nin\":["+result+"]}";
	}
	/**
	 * 操作符-不包含
	 * @param value 目标数组值(例：new String[]{"张三","李四","王五"})
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(String[] value){
		String result = STRING_EMPTY;
		for(int i=0; i<value.length; i++){
			result = i == value.length-1 ? result +  "\"" + value[i] +"\"" : result + "\"" + value[i]+"\",";
		}
		return "{\"$nin\":["+result+"]}";
	}
	/**
	 * 操作符-不包含
	 * @param value 目标数组值(例："\"张三\",\"李四\",\"王五\"")
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(String value){
		return "{\"$nin\":["+value+"]}";
	}

	/**
	 * 操作符-存在
	 * @param value 布尔值
	 * @return 复合存在字符串
	 */
	public static String whereExists(boolean value){
		return "{\"$exists\":"+value+"}";
	}

	/**
	 * 操作符-全包含
	 * @param value 目标值
	 * @return 复合全包含字符串
	 */
	public static String whereAll(String value){
		return "{\"$all\":["+value+"]}";
	}


	/**
	 * 操作符-区间包含
	 * @param greatEqual 是否大于包含等于
	 * @param greatValue 大于的目标值
	 * @param lessEqual 是否小于包含等于
	 * @param lessValue 小于的目标值
	 * @return 复合区间包含字符串
	 * 
	 * 例：查询[1000,3000), whereIncluded(true,1000,false,3000)
	 */
	public static String whereIncluded(boolean greatEqual, int greatValue, boolean lessEqual, int lessValue){
		return whereIncluded(greatEqual, String.valueOf(greatValue), lessEqual, String.valueOf(lessValue));
	}
	/**
	 * 操作符-区间包含
	 * @param greatEqual 是否大于包含等于
	 * @param greatValue 大于的目标值
	 * @param lessEqual 是否小于包含等于
	 * @param lessValue 小于的目标值
	 * @return 复合区间包含字符串
	 * 
	 * 例：查询[1000,3000), whereIncluded(true,"1000",false,"3000")
	 */
	public static String whereIncluded(boolean greatEqual, String greatValue, boolean lessEqual, String lessValue){
		String op1;
		String op2;
		op1 = greatEqual ? "\"$gte\"" : "\"$gt\"";
		op2 = lessEqual ? "\"$lte\"" : "\"$lt\"";	
		return "{"+op1+":"+greatValue+","+op2+":"+lessValue+"}";
	}
	

	/**
	 * 操作符-正则表达式
	 * @param regexValue 
	 * @return 复合正则表达式字符串
	 */
	public static String whereRegex(String regexValue){
		String op = "\"$regex\"";
		return "{"+op+":\""+regexValue+"\"}";
	}
	
	public static int getTimeout() {
		return TIME_OUT;
	}
	public static void setTimeout(int timeout) {
		TIME_OUT = timeout;
	}
	
	private static void printWriter(HttpURLConnection conn, String paramContent) throws UnsupportedEncodingException, IOException{
	    PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),UTF8));
        out.write(paramContent);
        out.flush();
        out.close();
	}
	private static String getResultFromConnection(HttpURLConnection conn) throws UnsupportedEncodingException, IOException{
		StringBuffer result = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),UTF8));
        String line;
        while ((line = reader.readLine()) != null){
            result.append(line);
        }
        reader.close();
		return result.toString();
	}
	private static HttpURLConnection connectionCommonSetting(HttpURLConnection conn, URL url, String method) throws IOException{
		conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod(method);
	    conn.setDoInput(true);
	    conn.setReadTimeout(TIME_OUT);

	    conn.setUseCaches(false);
	    conn.setInstanceFollowRedirects(true);
        
	    conn.setRequestProperty(BMOB_APP_ID_TAG, APP_ID);
	    conn.setRequestProperty(BMOB_REST_KEY_TAG, REST_API_KEY);
	    if(!MASTER_KEY.equals(STRING_EMPTY)){
		    conn.setRequestProperty(BMOB_MASTER_KEY_TAG, MASTER_KEY);
	    }
	    
	    conn.setRequestProperty(CONTENT_TYPE_TAG, CONTENT_TYPE_JSON);
	    return conn;
	}

	private static TrustManager[] trustAllCerts = new TrustManager[]{
		new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType)
					throws CertificateException {
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType)
					throws CertificateException {}
		}
	};

	private static String urlEncoder(String str) {
		try {
			return URLEncoder.encode(str, UTF8);
		} catch (UnsupportedEncodingException e1) {
			return str;
		}
	}
	
}
