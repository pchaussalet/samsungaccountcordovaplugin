package com.samsung.cordova.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.accounts.AccountManager;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

public class Account extends CordovaPlugin {
	private static final int REQUEST_LOGIN_CODE = 1;
	private static final int REQUEST_ACCESS_TOKEN_CODE = 2;
	private static final int SAMSUNG_RESULT_OK = -1;
	private static final int SAMSUNG_RESULT_CANCELED = 0;
	private static final int SAMSUNG_RESULT_FAILED = 1;
	private static final int SAMSUNG_RESULT_NETWORK_ERROR = 3;
	private CallbackContext callbackContext;
	private String clientId = "";
	private String clientSecret = "";

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		Log.d("tomtesting","Samsung Account plugin calling");
		this.callbackContext = callbackContext;
		if (action.equals("login")) {
			// String message = args.getString(0);
			if (args.length() > 1) {
				login(args.getString(0), args.getString(1));
				return true;
			} else {
				callbackContext
						.error("Expected ServiceId and ServiceKey two parameters.");
				return false;
			}
		}
		return false;
	}

	private void login(String clientId, String clientSecret) {
		Log.d("tomtesting","Samsung Account plugin login()");
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		final AccountManager manager = AccountManager.get(this.cordova
				.getActivity());
		final android.accounts.Account[] accountArr = manager
				.getAccountsByType("com.osp.app.signin");
		// Service app client secret
		if (accountArr.length > 0) {
			requestAccessToken();
		} else {

			String packageName = this.cordova.getActivity().getPackageName();
			Intent intent = new Intent(
					"com.osp.app.signin.action.ADD_SAMSUNG_ACCOUNT");
			intent.putExtra("client_id", clientId);
			intent.putExtra("client_secret", clientSecret);
			intent.putExtra("mypackage", packageName);
			intent.putExtra("MODE", "ADD_ACCOUNT");
			intent.putExtra("OSP_VER", "OSP_02");
			this.cordova.startActivityForResult(this, intent,
					REQUEST_LOGIN_CODE);
		}
	}

	private void requestAccessToken() {
		final String[] additional = new String[] { "api_server_url",
				"auth_server_url" };
		Intent intent = new Intent(
				"com.msc.action.samsungaccount.REQUEST_ACCESSTOKEN");
		intent.putExtra("client_id", clientId);
		intent.putExtra("client_secret", clientSecret);
		intent.putExtra("additional", additional);
		this.cordova.startActivityForResult(this, intent,
				REQUEST_ACCESS_TOKEN_CODE);
	}

	private void getAuthenticateUserID(final String api_server_url,
			final String accessToken) {
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				Account.this.getAuthenticateUserIDCore(api_server_url,
						accessToken);
			}
		});
	}

	private void getAuthenticateUserIDCore(String api_server_url,
			String accessToken) {
		try {
			URL url = new URL( "http://" + api_server_url
					+ "/v2/license/security/authorizeToken?authToken="
					+ accessToken);
			Log.d("tomtesting","api_server_url:" + api_server_url);
			Log.d("tomtesting","host:" + url.getHost());
			Log.d("tomtesting","Token:" + accessToken);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(
					(this.clientId + ":" + this.clientSecret)
							.getBytes(), Base64.NO_WRAP));
			conn.setRequestProperty("x-osp-appId", this.clientId);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream inputStr = conn.getInputStream();
				String encoding = conn.getContentEncoding() == null ? "UTF-8"
						: conn.getContentEncoding();
				String content = inputStreamTOString(inputStr, encoding);
				Log.d("tomtesting", content);
				String userID = parseAccessTokenResponseXml(new ByteArrayInputStream(content.getBytes()));
				this.callbackContext.success(userID);

			}else{
				throw  new Exception("HTTP status code: "+responseCode);
			}
		} catch (Exception e) {
			Log.d("tomtesting","validate access token exception: "+e.getMessage());
			this.callbackContext.error(e.getMessage());
		}

	}

	private String parseAccessTokenResponseXml(InputStream in) throws Exception {
		String userID = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(in);
		Element root = dom.getDocumentElement();

		NodeList items = root.getElementsByTagName("authenticateUserID");
		Element node = (Element) items.item(0);
		userID = node.getTextContent();
		return userID;
	}

	private String inputStreamTOString(InputStream in,String encoding) throws Exception {
		final int BUFFER_SIZE = 4096;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(),encoding);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == SAMSUNG_RESULT_OK) {
			if (requestCode == REQUEST_ACCESS_TOKEN_CODE) {
				String accessToken = intent.getStringExtra("access_token");
				String api_server_url = intent.getStringExtra("api_server_url");
				getAuthenticateUserID(api_server_url, accessToken);
			}else if(requestCode == REQUEST_LOGIN_CODE){
				requestAccessToken();
			}
		} else if (resultCode == SAMSUNG_RESULT_FAILED) {
			// String errorCode = intent.getStringExtra("error_code");
			String errorMessage = intent.getStringExtra("error_message");
			this.callbackContext.error(errorMessage);
		} else if (resultCode == SAMSUNG_RESULT_NETWORK_ERROR) {
			this.callbackContext.error("network error");
		} else {
			this.callbackContext.error("inner error");
		}
	}
}
