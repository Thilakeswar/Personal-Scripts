package zoho;

import Utils.HttpRequestUtil;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.naming.AuthenticationException;

import org.apache.http.HttpConnection;
import org.json.JSONObject;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class ImportCards
{
	public static void main(String args[]) throws Exception
	{
		try
		{
			//Test
			String filePath = "/Users/thilak-4083/Downloads/Cards.csv";
			String gateway = "stripe";
			CsvReader csvReader = new CsvReader(new FileReader(filePath));
			//csvReader.readHeaders();
			System.out.println("Header Count : " + csvReader.getHeaderCount());
			while(csvReader.readRecord())
			{
				System.out.println("CSV" + csvReader.getRawRecord());
				Card card = new Card();
				card.setEmail(csvReader.get("Email"));
				card.setDisplayName(csvReader.get("DisplayName"));
				card.setGatewayCardID(csvReader.get("GatewayCardID"));
				card.setGatewayCustomerID(csvReader.get("GatewayCustomerID"));
				card.setCardType(csvReader.get("CardType"));
				card.setFunding(csvReader.get("Funding"));
				card.setPaymentGateway(gateway);
				card.setLast4(csvReader.get("Last4"));
				card.setExpiryMonth(csvReader.get("ExpiryMonth"));
				card.setExpiryYear(csvReader.get("ExpiryYear"));
				card.setFirstName(csvReader.get("FirstName"));
				card.setLastName(csvReader.get("LastName"));
				card.setAddress(csvReader.get("Address"));
				card.setState(csvReader.get("State"));
				card.setCity(csvReader.get("City"));
				card.setCountry(csvReader.get("Country"));
				card.setPostalCode(csvReader.get("PostalCode"));
				importCard(card);
			}
			csvReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static boolean importCard(Card card) throws Exception
	{
		String email = card.getEmail();
		HttpURLConnection connection = HttpRequestUtil.createReadConnection("GET", "https://subscriptions.zoho.com/api/v1/customers","email_contains="+email);




		JSONObject jsonObject = new JSONObject();
		jsonObject.put("payment_gateway", card.getPaymentGateway());
		jsonObject.put("gateway_customer_id", card.getGatewayCustomerID());
		jsonObject.put("gateway_card_id", card.getGatewayCardID());
		jsonObject.put("expiry_month", card.getExpiryMonth());
		jsonObject.put("expiry_year", card.getExpiryYear());
		jsonObject.put("last_four_digits", card.getLast4());
		jsonObject.put("first_name", card.getFirstName());
		jsonObject.put("last_name", card.getLastName());
		jsonObject.put("address", card.getAddress());
		jsonObject.put("city", card.getCity());
		jsonObject.put("state", card.getState());
		jsonObject.put("country", card.getCountry());
		jsonObject.put("zip", card.getPostalCode());
		jsonObject.put("card_type", card.getCardType());
		jsonObject.put("funding", card.getFunding());



		return true;
	}

	public static void constructResponse(HttpURLConnection connection) throws Exception
	{
		InputStream is = null;
		int resCode = connection.getResponseCode();
		Object body;

		try
		{
			if(resCode == 401)
			{
				throw new AuthenticationException();
			}
			is = (resCode >= 200 && resCode < 300) ? connection.getInputStream() : connection.getErrorStream();
			if("gzip".equalsIgnoreCase(connection.getContentEncoding()))//No I18n
			{
				is = new GZIPInputStream(is);
			}

			InputStreamReader reader = new InputStreamReader(is, CHAR_SET);
			StringBuilder buffer = new StringBuilder();
			char[] bytes = new char[1024];
			int bytesRead;
			while((bytesRead = reader.read(bytes, 0, bytes.length)) > 0)
			{
				buffer.append(bytes, 0, bytesRead);
			}
			body = buffer.toString();
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}

	}
}