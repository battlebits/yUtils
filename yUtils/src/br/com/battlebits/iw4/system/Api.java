package br.com.battlebits.iw4.system;

import static br.com.battlebits.iw4.IW4.getPlugin;
import static br.com.battlebits.iw4.IW4.getPluginConfig;
import static br.com.battlebits.iw4.IW4.getRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import br.com.battlebits.iw4.json.JSONException;
import br.com.battlebits.iw4.json.JSONObject;

public class Api {

	private String apiUrl;
	private String apiKey;
	private String apiToken;
	private String apiVersion;

	public void init() {
		this.apiUrl = getPluginConfig().getString("api_auth_url");
		this.apiKey = getPluginConfig().getString("api_auth_key");
		this.apiToken = "";
		this.apiVersion = "minecraft/" + getPluginConfig().getString("api_auth_version") + "/";
	}

	public JSONObject authLoginAction() {
		HashMap<Object, Object> apiCallParams = new HashMap<>();

		apiCallParams.put("key", this.apiKey);

		return call(apiCallParams, "login");
	}

	public JSONObject chkLoginAction() {
		HashMap<Object, Object> apiCallParams = new HashMap<>();

		return call(apiCallParams, "login/status");
	}

	public JSONObject getPlayerPendingOrders(HashMap<Object, Object> apiCallParams) {

		return call(apiCallParams, apiVersion + "order/sends");
	}

	public JSONObject getPlayerExpiredOrders(HashMap<Object, Object> apiCallParams) {

		return call(apiCallParams, apiVersion + "order/expired");
	}

	public JSONObject getPlayerPackages(HashMap<Object, Object> apiCallParams) {

		return call(apiCallParams, apiVersion + "order/get");
	}

	public JSONObject setOrders(HashMap<Object, Object> apiCallParams) {

		return call(apiCallParams, apiVersion + "order/set");
	}

	public void setToken(String newToken) {
		this.apiToken = newToken;
	}

	private JSONObject call(HashMap<Object, Object> apiCallParams, String route) {
		if (this.apiToken.length() > 0) {
			apiCallParams.put("token", this.apiToken);
		}
		String url = this.apiUrl + "/" + route + generateUrlQueryString(apiCallParams);

		if (url != null) {
			String HTTPResponse = HttpRequest(url);
			try {
				if (HTTPResponse != null) {
					return new JSONObject(HTTPResponse);
				}
				return null;
			} catch (JSONException e) {
				getRegistry().logger().logException("JSON parsing error.");
				getRegistry().logger().log(e);
			}
		}
		return null;
	}

	public static String HttpRequest(String url) {
		HttpURLConnection yc = null;
		try {
			if ("true".equals(getPluginConfig().getString("debug"))) {
				getRegistry().logger().logException("Request URL: " + url);
			}
			String content = "";

			URL conn = new URL(url);

			yc = (HttpURLConnection) conn.openConnection();

			yc.setRequestMethod("GET");
			yc.setConnectTimeout(15000);
			yc.setReadTimeout(15000);
			yc.setInstanceFollowRedirects(false);
			yc.setAllowUserInteraction(false);

			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content = content + inputLine;
			}
			in.close();

			if ("true".equals(getPluginConfig().getString("debug"))) {
				getRegistry().logger().log("JSON Response: " + content);
			}
			return content;
		} catch (ConnectException e) {
			getRegistry().logger().logException("HTTP request failed due to connection error.");
		} catch (SocketTimeoutException e) {
			getRegistry().logger().logException("HTTP request failed due to timeout error.");
		} catch (FileNotFoundException e) {
			getRegistry().logger().logException("HTTP request failed due to file not found.");
		} catch (UnknownHostException e) {
			getRegistry().logger().logException("HTTP request failed due to unknown host.");
		} catch (IOException e) {
			getRegistry().logger().logException(e.getMessage());
			try {
				String content = "";

				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getErrorStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					content = content + inputLine;
				}
				in.close();

				logToFile(url, content, yc.getResponseCode());
			} catch (IOException e1) {
				getRegistry().logger().log(e1);
			}
		} catch (Exception e) {
			getRegistry().logger().log(e);
		}
		return null;
	}

	private static String generateUrlQueryString(HashMap<Object, Object> map) {
		StringBuilder sb = new StringBuilder();

		sb.append("?");
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			if (sb.length() > 1) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", new Object[] { ((String) entry.getKey()).toString(), ((String) entry.getValue()).toString() }));
		}
		return sb.toString();
	}

	public static void logToFile(String url, String body, int responseCode) {
		FileWriter file = null;
		try {
			file = new FileWriter(new File(getPlugin().getDataFolder(), "http.log").getPath(), true);

			file.append("REQUEST URI: " + url + "\n");
			file.append("RESPONSE CODE: " + responseCode + "\n");
			file.append("CONTENT:\n" + body + "\n");
			file.append("--------------------------\n");
			file.flush();
		} catch (IOException e) {
			getRegistry().logger().log(e);
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
