package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is used to retrieve data from the REST Server
 */
public class RetrieveServerData {

	/**
	 * A default URL that is used to fetch data from the base address
	 * <a href="https://ilp-rest.azurewebsites.net/">default URL</a>
	 */
	private static final URL DEFAULT_URL;

	static {
		try {
			DEFAULT_URL = new URL("https://ilp-rest.azurewebsites.net/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The generic method used to retrieve data from the given extension of
	 * the default URL. The retrieved data returned is in the type of the given type reference.
	 * The following code is inspired and a heavily adapted version of the code in the link:
	 * <a href="https://stackoverflow.com/questions/67866342/">link to StackOverflow</a>
	 * @param extension the extension of the default URL whose data is to be retrieved.
	 * @param typeReference the type in which the retrieved data is supposed to be returned in.
	 * @param <T> the generic type that allows generic type invocation
	 * @return the data retrieved from the given extension of the REST Server with the default URL
	 */
	public static <T>T getExtensionDataFromDefaultURL(String extension, TypeReference<T> typeReference){
		return getExtensionDataFromURL(extension, DEFAULT_URL, typeReference);
	}

	/**
	 * The generic method used to retrieve data from the given extension of a given base address URL.
	 * The retrieved data returned is in the type of the given type reference.
	 * The following code is inspired and a heavily adapted version of the code in the link:
	 * <a href="https://stackoverflow.com/questions/67866342/">link to StackOverflow</a>
	 * @param extension the extension of the default URL whose data is to be retrieved.
	 * @param baseAddress the base address URL of the server that has to be accessed
	 * @param typeReference the type in which the retrieved data is supposed to be returned in.
	 * @param <T> the generic type that allows generic type invocation
	 * @return the data retrieved from the given extension of the REST Server with the default URL
	 */
	public static <T>T getExtensionDataFromURL(String extension, URL baseAddress, TypeReference<T> typeReference){
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue
					(new URL(baseAddress + extension), typeReference);
		}catch(MalformedURLException e){
			e.printStackTrace();
			throw new RuntimeException("URL is invalid : " + baseAddress + extension);
		}catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("There was an error retrieving the data for the extension: " + extension);
		}
	}

}