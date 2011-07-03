#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.aspects;

import java.util.*;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import com.google.inject.*;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.model.AbstractResourceMethod;

import ${package}.controllers.ErrorController;

/**
 * JAX-RS Glue for mapping exceptions to error views 
 */
@Singleton
@javax.ws.rs.ext.Provider
public class ExceptionRouter implements ExceptionMapper<Throwable> {

	private final Logger log = Logger.getLogger(ExceptionRouter.class.getSimpleName());

	private static final MediaType DefaultMediaType = MediaType.TEXT_HTML_TYPE;
	private final List<MediaType> supportedMediaTypes = Arrays.asList(
			MediaType.TEXT_HTML_TYPE,
			MediaType.APPLICATION_XHTML_XML_TYPE,
			MediaType.APPLICATION_JSON_TYPE,
			MediaType.APPLICATION_XML_TYPE);

	private final ErrorController errorController;
	private final Provider<ExtendedUriInfo> extendedUriInfoProvider;

	@Inject
	public ExceptionRouter(
			ErrorController errorController,
			Provider<ExtendedUriInfo> extendedUriInfoProvider) {

		this.errorController = errorController;
		this.extendedUriInfoProvider = extendedUriInfoProvider;
	}

	@Override
	public Response toResponse(final Throwable ex) {

		// get request metadata
		ExtendedUriInfo uriInfo = this.extendedUriInfoProvider.get();

		// find the intended result type
		MediaType resultType = getResultType(uriInfo.getMatchedMethod());

		return Response
				.status(getHTTPStatus(ex, uriInfo))
				.type(resultType)
				.entity(getResult(ex, resultType))
				.build();
	}

	public Status getHTTPStatus(Throwable ex, ExtendedUriInfo uriInfo) {

		if (ex instanceof NotFoundException) {
			return Status.NOT_FOUND;
		}

		// TODO: filter exception severity for logging
		log.severe(uriInfo.getPath()+": "+ex.getMessage());

		// TODO: map more response codes
		return Status.INTERNAL_SERVER_ERROR;
	}

	private Object getResult(Throwable ex, MediaType resultType) {
		// can these mappings be performed by JAX-RS?
		// TODO: map more response views

		if (MediaType.APPLICATION_JSON_TYPE.equals(resultType) ||
			MediaType.APPLICATION_XML_TYPE.equals(resultType)) {
			return errorController.errorData(ex);
		}

		return errorController.errorView(ex);
	}

	private MediaType getResultType(AbstractResourceMethod method) {
		// can this matching be performed by JAX-RS?
		if (method == null) {
			return DefaultMediaType;
		}

		for (MediaType mediaType : method.getSupportedOutputTypes()) {
			if (supportedMediaTypes.contains(mediaType)) {
				return mediaType;
			}
		}

		for (MediaType mediaType : method.getSupportedInputTypes()) {
			if (supportedMediaTypes.contains(mediaType)) {
				return mediaType;
			}
		}

		return DefaultMediaType;
	}
}
