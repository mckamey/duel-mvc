#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.aspects;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.duelengine.duel.mvc.UncaughtErrorHandler;

import ${package}.controllers.ErrorController;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.ExtendedUriInfo;

/**
 * Uncaught error handling 
 */
@Singleton
@javax.ws.rs.ext.Provider
public class ErrorHandler extends UncaughtErrorHandler {

	private final ErrorController errorController;

	@Inject
	public ErrorHandler(ErrorController errorController) {
		if (errorController == null) {
			throw new NullPointerException("errorController");
		}

		this.errorController = errorController;
	}

	@Override
	protected Status getHTTPStatus(ExtendedUriInfo uriInfo, Throwable ex) {
		if (ex instanceof NotFoundException) {
			return Status.NOT_FOUND;
		}

		// TODO: map more response codes

		return Status.INTERNAL_SERVER_ERROR;
	}

	@Override
	protected Object getResult(ExtendedUriInfo uriInfo, Throwable ex, MediaType resultType) {
		// can these mappings be performed by JAX-RS?

		// TODO: map more response views

		if (MediaType.APPLICATION_JSON_TYPE.equals(resultType) ||
			MediaType.APPLICATION_XML_TYPE.equals(resultType)) {
			return errorController.errorData(ex);
		}

		if (MediaType.TEXT_PLAIN_TYPE.equals(resultType)) {
			return errorController.errorText(ex);
		}

		return errorController.errorView(ex);
	}

	@Override
	protected MediaType getDefaultMediaType(ExtendedUriInfo uriInfo) {
		return MediaType.TEXT_HTML_TYPE;
	}

	@Override
	protected List<MediaType> getSupportedMediaTypes(ExtendedUriInfo uriInfo) {
		return Arrays.asList(
			MediaType.TEXT_HTML_TYPE,
			MediaType.APPLICATION_XHTML_XML_TYPE,
			MediaType.APPLICATION_JSON_TYPE,
			MediaType.APPLICATION_XML_TYPE,
			MediaType.TEXT_PLAIN_TYPE);
	}
}
