package org.beuk.service.calendar;

import java.io.*;
import java.util.*;

import org.apache.commons.configuration.*;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.extensions.java6.auth.oauth2.*;
import com.google.api.client.extensions.jetty.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.*;
import com.google.api.client.http.*;
import com.google.api.client.json.*;
import com.google.api.client.json.jackson2.*;
import com.google.api.client.util.store.*;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.Calendar;

public class CalendarController {

	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final String APPLICATION_NAME = "idro client";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	// private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY);
	private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

	/**
	 * Build and return an authorized Calendar client service.
	 *
	 * @return an authorized Calendar client service
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {

		final CalendarController calendarController = new CalendarController("/client_secret_hetidromeer.json", ".credentials/idro-pac-calendar-hetidromeer");
		calendarController.getCalendarService();
	}

	/** Global instance of the {@link FileDataStoreFactory}. */
	private final String accessType = "offline";
	// private final String accessType = "online";

	/** Directory to store user credentials for this application. */
	private final File dataStoreDir;
	private final String credentialFile;

	final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	public CalendarController(Configuration config) {
		credentialFile = config.getString("google.calendar.credentialfile");
		dataStoreDir = new File(System.getProperty("user.home"), config.getString("google.calendar.dataStoreDir"));
		initialize();
	}

	public CalendarController(final String credentialFile, final String dataStoreDirName) {
		this.credentialFile = credentialFile;
		dataStoreDir = new File(System.getProperty("user.home"), dataStoreDirName);
		initialize();
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @return an authorized Credential object.
	 * @throws Exception
	 */
	public Credential authorize() throws Exception {

		// Load client secrets.
		System.out.println("credentialFile: " + credentialFile);
		final InputStream in = CalendarController.class.getResourceAsStream(credentialFile);
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType(accessType).build();
		final Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + dataStoreDir.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Calendar client service.
	 *
	 * @return an authorized Calendar client service
	 * @throws Exception
	 */
	public Calendar getCalendarService() throws Exception {

		final Credential credential = authorize();
		return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	private void initialize() {

		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(dataStoreDir);
		} catch (final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}
}
