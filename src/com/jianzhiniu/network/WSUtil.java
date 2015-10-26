package com.jianzhiniu.network;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

/**
 * 接口实现类
 * @author xzr
 *
 */
public class WSUtil {

	public static Object getSoapObjectByCallingWS(String nameSpace,
			String methodName, Map<String, Object> params, String wsdl)
			throws IOException, XmlPullParserException {
		return getObjectByCallingWS(nameSpace, methodName, params,
				wsdl);
	}

	public static Object getObjectByCallingWS(String nameSpace,
			String methodName, Map<String, Object> params, String wsdl)
			throws IOException, XmlPullParserException {

		final String SOAP_ACTION = nameSpace + methodName;
		Object soapPrimitiveResult = null;

		SoapSerializationEnvelope envelope = constructRequestObject2(nameSpace,
				methodName, params);
		soapPrimitiveResult = callWebservice(SOAP_ACTION, wsdl, envelope);

		return soapPrimitiveResult;
	}

	private static SoapSerializationEnvelope constructRequestObject2(
			String nameSpace, String methodName, Map<String, Object> params) {
		SoapObject request = new SoapObject(nameSpace, methodName);// 第一步，实例化SoapObject对象，并指定命名空间和方法名
		if (params != null && !params.isEmpty()) {
			for (Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry<String, Object> e = it.next();
				if (e.getValue() instanceof byte[]) {
					byte[] d = (byte[]) e.getValue();
					String data = new String(Base64.encode(d));
					// request.addProperty(e.getKey(), new
					// SoapPrimitive(SoapEnvelope.ENC, "base64Binary", data));
					request.addProperty(e.getKey(), data);// 第二步，设置调用参数方法，包括参数名称和参数值
				} else {
					request.addProperty(e.getKey().toString(), e.getValue());
				}
			}
		}
		// 第三步，设置SOAP请求信息（参数部分为SOAP协议版本号，与你要调用到的WEBSERVICE中的版本号一致）
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.setOutputSoapObject(request);

		// envelope.bodyOut = request;
		envelope.dotNet = true;
		envelope.encodingStyle = SoapSerializationEnvelope.ENC;
		return envelope;
	}

	public static Object callWebservice(String SOAP_ACTION, String wsdl,
			SoapSerializationEnvelope envelope) throws IOException,
			XmlPullParserException {

		// registerObjects(envelope);
		Object resultObject = null;
		int MSG_TIMEOUT = 15000;
		HttpTransportSE ht = new HttpTransportSE(wsdl,MSG_TIMEOUT); // 第四步，构建传输对象，并指明wsdl文档url
		try {
			ht.call(SOAP_ACTION, envelope);// 第五步，调用webservice（其中参数一SOAP_ACTION为命名空间+方法名，参数二为envelope）

		} catch (IOException e) {
			Log.e("IOException:", e.getMessage());
			// androidHT.reset();
			throw e;
		} catch (XmlPullParserException e1) {
			Log.e("XmlPullParserException", e1.getMessage());
			throw e1;
		}
		try {
			resultObject = envelope.getResponse();// 第6步：使用getResponse方法获得WebService方法的返回结果
		} catch (SoapFault e) {
			Log.e("SoapFault", e.getMessage());
		}
		return resultObject;
	}

}
