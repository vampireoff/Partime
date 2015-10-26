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
 * �ӿ�ʵ����
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
		SoapObject request = new SoapObject(nameSpace, methodName);// ��һ����ʵ����SoapObject���󣬲�ָ�������ռ�ͷ�����
		if (params != null && !params.isEmpty()) {
			for (Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry<String, Object> e = it.next();
				if (e.getValue() instanceof byte[]) {
					byte[] d = (byte[]) e.getValue();
					String data = new String(Base64.encode(d));
					// request.addProperty(e.getKey(), new
					// SoapPrimitive(SoapEnvelope.ENC, "base64Binary", data));
					request.addProperty(e.getKey(), data);// �ڶ��������õ��ò��������������������ƺͲ���ֵ
				} else {
					request.addProperty(e.getKey().toString(), e.getValue());
				}
			}
		}
		// ������������SOAP������Ϣ����������ΪSOAPЭ��汾�ţ�����Ҫ���õ���WEBSERVICE�еİ汾��һ�£�
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
		HttpTransportSE ht = new HttpTransportSE(wsdl,MSG_TIMEOUT); // ���Ĳ�������������󣬲�ָ��wsdl�ĵ�url
		try {
			ht.call(SOAP_ACTION, envelope);// ���岽������webservice�����в���һSOAP_ACTIONΪ�����ռ�+��������������Ϊenvelope��

		} catch (IOException e) {
			Log.e("IOException:", e.getMessage());
			// androidHT.reset();
			throw e;
		} catch (XmlPullParserException e1) {
			Log.e("XmlPullParserException", e1.getMessage());
			throw e1;
		}
		try {
			resultObject = envelope.getResponse();// ��6����ʹ��getResponse�������WebService�����ķ��ؽ��
		} catch (SoapFault e) {
			Log.e("SoapFault", e.getMessage());
		}
		return resultObject;
	}

}
