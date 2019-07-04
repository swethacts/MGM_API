package businesscomponents;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.ValidatableResponse;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cognizant.craft.ReusableLibrary;
import com.cognizant.craft.ScriptHelper;
import com.cognizant.framework.APIReusuableLibrary.SERVICEFORMAT;
import com.cognizant.framework.Status;
import com.cognizant.framework.APIReusuableLibrary.ASSERT_RESPONSE;
import com.cognizant.framework.APIReusuableLibrary.COMPARISON;
import com.cognizant.framework.APIReusuableLibrary.SERVICEMETHOD;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("unused")
public class APIComponents extends ReusableLibrary {

	static String customerId = null;
	static String itineraryId = null;
	static String mlifeNo = null;
	HeadersForAPI headers = new HeadersForAPI(scriptHelper);
	static Logger log = Logger.getLogger(APIComponents.class);

	public APIComponents(ScriptHelper scriptHelper) {
		super(scriptHelper);
	}

	/***
	 * 
	 * Function to validate Oauth Token API
	 * 
	 ***/

	public void validateOauthTokenAPI() {
		log.info("Testing Oauth Token API started . .");
		Map<String, String> headersMap = headers.getBasicAuthorization();
		ValidatableResponse response;

		String url = dataTable.getData("General_Data", "URL1");
		// String url="https://d76ee501-0415-44e8-a345-99ff342f64fe.mock.pstmn.io/oauth/to";

		try {
			String username = dataTable.getData("General_Data", "Username");
			String password = dataTable.getData("General_Data", "Password");

			response = apiDriver.sendNReceive2(url, username, password, 200);
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"access_token");
			response.assertThat().body(
					matchesJsonSchemaInClasspath("oauthToken_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	/**
	 * Function to validate Actuator Health API
	 * 
	 * 
	 */

	public void validateActuatorHealthAPI() {
		log.info("Testing Actuator Health API started . .");

		String expectedValue = getStatusValue();
		ValidatableResponse response;
		Map<String, String> headersMap = null;
		String url = dataTable.getData("General_Data", "URL1");

		try {
			response = apiDriver.sendNReceive(url, SERVICEMETHOD.GET,
					headersMap, 200);

			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG, "status",
					expectedValue, COMPARISON.IS_EQUALS);
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"status");
			// apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			// "application/json;", COMPARISON.IS_EXISTS);

			response.assertThat().body(
					matchesJsonSchemaInClasspath("actuator_schema .json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	private String getStatusValue() {
		String status = dataTable.getData("General_Data", "Status");
		return status;
	}

	/**
	 * Function to validate Customer Value API
	 * 
	 * 
	 */

	public void validateCustomerValueAPI() {

		log.info("Testing Customer Value API started . .");

		Object expectedList = new ArrayList<String>();
		expectedList = getPropertyList();

		ValidatableResponse response;
		Map<String, String> headersMap = null;

		String url = dataTable.getData("General_Data", "URL1");

		// String
		// url="https://5d017442-a822-4e7e-a802-8310422e64eb.mock.pstmn.io/pp/v1.0.1/customer/value/49622160";
		try {

			String expectedCustomerId = dataTable.getData("General_Data",
					"corporateCustomerId");
			String expectedMlifeNo = url.substring(url.lastIndexOf("/") + 1);

			response = apiDriver.sendNReceive(url, SERVICEMETHOD.GET,
					headersMap, 200);

			response.assertThat().body(
					matchesJsonSchemaInClasspath("customerValue_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.corporateCustomerId",
					expectedCustomerId, COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.mlifeNo", expectedMlifeNo,
					COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.LIST,
					"customer.customerValues.property", expectedList,
					COMPARISON.IS_EQUALS);
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.corporateCustomerId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.mlifeNo");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			// apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			// "application/json;", COMPARISON.IS_EXISTS);

			log.info("Response Generated");
		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	private List<String> getPropertyList() {

		List<String> customerPropertyList = new ArrayList<String>();
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property1"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property2"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property3"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property4"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property5"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property6"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property7"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property8"));
		customerPropertyList
				.add(dataTable.getData("General_Data", "Property9"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property10"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property11"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property12"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property13"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property14"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property15"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property16"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property17"));
		customerPropertyList.add(dataTable
				.getData("General_Data", "Property18"));

		return customerPropertyList;
	}

	/**
	 * Function to validate Customer Value API with some incorrect data
	 * 
	 * 
	 */

	public void validateCustomerValueAPI2() {

		log.info("Testing Customer Value API started . .");

		Object expectedList = new ArrayList<String>();
		expectedList = getPropertyList();

		ValidatableResponse response;
		Map<String, String> headersMap = null;

		String url = dataTable.getData("General_Data", "URL1");

		try {

			String expectedCustomerId = dataTable.getData("General_Data",
					"corporateCustomerId");
			String expectedMlifeNo = url.substring(url.lastIndexOf("/") + 1);

			response = apiDriver.sendNReceive(url, SERVICEMETHOD.GET,
					headersMap, 200);

			response.assertThat().body(
					matchesJsonSchemaInClasspath("customerValue_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.corporateCustomerId",
					expectedCustomerId, COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.identity.mlifeNo", expectedMlifeNo,
					COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.LIST,
					"customer.customerValues.property", expectedList,
					COMPARISON.IS_EQUALS);

			log.info("Response Generated");
		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	/**
	 * Function to validate Add Customer API
	 * 
	 * 
	 */

	public void validateAddCustomerAPI() {

		log.info("Testing Add Customer API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = headers.getHeaders3();

		String url = dataTable.getData("General_Data", "URL1");
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			String firstName = dataTable.getData("General_Data", "firstName");
			String lastName = dataTable.getData("General_Data", "lastName");
			
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_firstName", firstName);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_lastName", lastName);
			
			
			response = apiDriver.sendNReceive(url, SERVICEMETHOD.POST,
					SERVICEFORMAT.JSON, postBodyContent, headersMap, 200);

			response.assertThat()
					.body(matchesJsonSchemaInClasspath("addCustomerResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			customerId = response.extract().jsonPath().getString("customer.id");
			mlifeNo=response.extract().jsonPath().getString("customer.mlifeNo");

			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.firstName", firstName, COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"customer.lastName", lastName, COMPARISON.IS_EQUALS);
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customer.id");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customer.mlifeNo");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			/*apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			 "application/json", COMPARISON.IS_EXISTS);*/

			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	/**
	 * Function to validate create Itinerary API
	 * 
	 * 
	 */

	public void validatecreateItineraryAPI() {

		log.info("Testing create Itinerary API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = headers.getHeaders3();

		String url = dataTable.getData("General_Data", "URL1");
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_customerId", customerId);

			response = apiDriver.sendNReceive(url, SERVICEMETHOD.POST,
					SERVICEFORMAT.JSON, postBodyContent, headersMap, 200);

			response.assertThat()
					.body(matchesJsonSchemaInClasspath("createItineraryResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);

			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					customerId, "itinerary.customerId");

			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.customerId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.id");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.sourceId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.requestId");
			/*apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			 "application/json", COMPARISON.IS_EXISTS);*/

			itineraryId = response.extract().jsonPath()
					.getString("itinerary.id");
			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	/**
	 * Function to validate make room reservation API
	 * 
	 * 
	 */
	
	public void validateMakeRoomReservationAPI() {

		log.info("Testing make room reservation API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = headers.getHeaders3();

		String url = dataTable.getData("General_Data", "URL1");
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			String firstName = dataTable.getData("General_Data", "firstName");
			String lastName = dataTable.getData("General_Data", "lastName");
			
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_firstName", firstName);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_lastName", lastName);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_customerId", customerId);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_itineraryId", itineraryId);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_mlifeNo", mlifeNo);
			

			response = apiDriver.sendNReceive(url, SERVICEMETHOD.POST,
					SERVICEFORMAT.JSON, postBodyContent, headersMap, 200);

			response.assertThat()
					.body(matchesJsonSchemaInClasspath("makeRoomReservationResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);
			
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.profile.firstName", "["+firstName+"]", COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.profile.lastName", "["+lastName+"]", COMPARISON.IS_EQUALS);
			
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					customerId, "itinerary.customerId");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					itineraryId, "itinerary.id");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					"["+customerId+"]", "itinerary.roomReservations.profile.id");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					"["+mlifeNo+"]", "itinerary.roomReservations.profile.mlifeNo");
			
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.customerId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.id");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.sourceId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.requestId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.propertyId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.roomTypeId");
			
			
			// apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			// "application/json;", COMPARISON.IS_EXISTS);

			
			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}
	
	/**
	 * Function to validate update room reservation API
	 * 
	 * 
	 */
	
	public void validateUpdateRoomReservationAPI() {

		log.info("Testing update room reservation API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = headers.getHeaders3();

		String url = dataTable.getData("General_Data", "URL1");
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			String firstName = dataTable.getData("General_Data", "firstName");
			String lastName = dataTable.getData("General_Data", "lastName");
			
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_firstName", firstName);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_lastName", lastName);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_customerId", customerId);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_itineraryId", itineraryId);
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_mlifeNo", mlifeNo);
			

			response = apiDriver.sendNReceive(url, SERVICEMETHOD.POST,
					SERVICEFORMAT.JSON, postBodyContent, headersMap, 200);

			response.assertThat()
					.body(matchesJsonSchemaInClasspath("makeRoomReservationResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);
			
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.profile.firstName", "["+firstName+"]", COMPARISON.IS_EQUALS);
			apiDriver.assertIt(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.profile.lastName", "["+lastName+"]", COMPARISON.IS_EQUALS);
			
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					customerId, "itinerary.customerId");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					itineraryId, "itinerary.id");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					"["+customerId+"]", "itinerary.roomReservations.profile.id");
			apiDriver.assertEqual(url, response, ASSERT_RESPONSE.TAG,
					"["+mlifeNo+"]", "itinerary.roomReservations.profile.mlifeNo");
			
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.customerId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.id");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.sourceId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.requestId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.propertyId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"itinerary.roomReservations.roomTypeId");
			
			
			// apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "",
			// "application/json;", COMPARISON.IS_EXISTS);

			
			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		}
	}

	/**
	 * Function to validate Pricing and Availability API
	 * 
	 * 
	 */
	

	public void validatePricingandAvailabilityAPI() {
		log.info("Testing Pricing and Availability API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = null;
		String url = dataTable.getData("General_Data", "URL1");
		
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_customerId", customerId);
			
			
			response = given().relaxedHTTPSValidation().queryParam("request", postBodyContent).when().get(url)
				      .then().statusCode(200);
			    
			report.updateTestLog(url, "StatusCode: " + 200, "StatusCode: " + 200,
					Status.PASS);

			
			response.assertThat().body(
					matchesJsonSchemaInClasspath("pricingAndAvailabilityResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);
			
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.transactionId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.sourceId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"header.requestId");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"prices.propertyId");
			//apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "","application/json;", COMPARISON.IS_EXISTS);
			 
			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		} 
	}
	
	/**
	 * Function to validate Search Customer API
	 * 
	 * 
	 */
	

	public void validateSearchCustomerAPI() {
		log.info("Testing Search Customer API started . .");

		ValidatableResponse response;
		Map<String, String> headersMap = null;
		String url = dataTable.getData("General_Data", "URL1");
		
		try {

			String postBodyContent = dataTable.getData("General_Data",
					"InputJsonTemplate");
			postBodyContent = apiDriver.updateContent(postBodyContent,
					"update_mlifeId", customerId);
			
			
			response = given().relaxedHTTPSValidation().queryParam("request", postBodyContent).when().get(url)
				      .then().statusCode(200);
			    
			report.updateTestLog(url, "StatusCode: " + 200, "StatusCode: " + 200,
					Status.PASS);

			
			response.assertThat().body(
					matchesJsonSchemaInClasspath("searchCustomerResponse_schema.json"));
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema matched", Status.PASS);
			
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customers.id");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customers.mlifeNo");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customers.firstName");
			apiDriver.assertNotNull(url, response, ASSERT_RESPONSE.TAG,
					"customers.lastName");
			//apiDriver.assertIt(url, response, ASSERT_RESPONSE.HEADER, "","application/json;", COMPARISON.IS_EXISTS);
			 
			log.info("Response Generated");

		} catch (AssertionError error) {
			log.info("Error " + error.getMessage());
			report.updateTestLog("Validate the schema", "Schema should match",
					"Schema not matched", Status.FAIL);
		} 
	}
	
}
