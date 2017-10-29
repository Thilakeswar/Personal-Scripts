package Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestUtil
{
	public static String zsid = "********";
	public static String authtoken = "*****************";
	public static String CHAR_SET = "UTF-8";

	public static HttpURLConnection createReadConnection(String method, String url, String qryStr) throws Exception
	{
		String reqUrl = String.format("%s?%s", url, qryStr);//No I18n
		return createConnection(method, reqUrl);
	}

	public static HttpURLConnection createWriteConnection(String method, String reqUrl, String qryStr) throws Exception
	{
		HttpURLConnection connection = createConnection(method, reqUrl);

		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHAR_SET);//No I18n
		connection.setDoOutput(true);

		writeQueryString(connection, qryStr);

		return connection;
	}

	public static HttpURLConnection createConnection(String method, String urlStr) throws Exception
	{
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);

		//Connection Params
		connection.setConnectTimeout(30000); // 30 seconds
		connection.setReadTimeout(300000); // 300 seconds
		connection.setUseCaches(false);

		connection.setRequestProperty("Accept-Charset", CHAR_SET);//No I18n
		connection.setRequestProperty("Accept", "application/json");//No I18n
		connection.setRequestProperty("User-Agent", "Dexter");//No I18n
		connection.setRequestProperty("Authorization", "Zoho-authtoken " + authtoken);//No I18n
		connection.setRequestProperty("X-com-zoho-subscriptions-organizationid", zsid);//No I18n
		return connection;
	}

	private static void writeQueryString(HttpURLConnection connection, String qryStr) throws IOException
	{
		OutputStream os = null;
		try
		{
			os = connection.getOutputStream();
			os.write(qryStr.getBytes(CHAR_SET));
		}
		finally
		{
			if (os != null)
			{
				os.close();
			}
		}
	}
}
