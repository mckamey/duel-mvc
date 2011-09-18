package org.duelengine.duel.mvc;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.model.AbstractResourceMethod;

/**
 * JAX-RS glue for mapping unhandled exceptions through error filters 
 */
public abstract class UncaughtErrorHandler implements ExceptionMapper<Throwable> {

	private Provider<ExtendedUriInfo> factory;

	@Inject
	void init_ErrorFilterMapper(Provider<ExtendedUriInfo> factory) {
		this.factory = factory;
	}

	@Override
	public final Response toResponse(Throwable error) {

		ExtendedUriInfo uriInfo = factory.get();

		// find the intended result type
		MediaType resultType = getResultType(uriInfo);

		return Response
				.status(getHTTPStatus(uriInfo, error))
				.entity(getResult(uriInfo, error, resultType))
				.type(resultType)
				.build();
	}

	protected MediaType getResultType(ExtendedUriInfo uriInfo) {
		// can this matching be performed by JAX-RS?
		AbstractResourceMethod method = uriInfo.getMatchedMethod();
		
		if (method == null) {
			return getDefaultMediaType(uriInfo);
		}

		List<MediaType> supportedMediaTypes = getSupportedMediaTypes(uriInfo);
		
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

		return getDefaultMediaType(uriInfo);
	}

	protected abstract MediaType getDefaultMediaType(ExtendedUriInfo uriInfo);

	protected abstract List<MediaType> getSupportedMediaTypes(ExtendedUriInfo uriInfo);

	protected abstract Status getHTTPStatus(ExtendedUriInfo uriInfo, Throwable ex);

	protected abstract Object getResult(ExtendedUriInfo uriInfo, Throwable ex, MediaType resultType);
}
