package org.duelengine.duel.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.duelengine.duel.mvc.DuelMvcContext;
import org.duelengine.duel.DuelView;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Simple adapter for conveniently rendering DUEL views in JAX-RS
 */
public class ViewResult implements StreamingOutput {

	private final DuelMvcContext context;
	private final DuelView view;

	@AssistedInject
	public ViewResult(
		@Assisted Class<? extends DuelView> viewType,
		DuelMvcContext context) {

		if (viewType == null) {
			throw new NullPointerException("view");
		}
		if (context == null) {
			throw new NullPointerException("context");
		}

		try {
			this.view = viewType.newInstance();

		} catch (Exception ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		}

		this.context = context;
	}

	public ViewResult data(Object data) {
		this.context.setData(data);

		return this;
	}

	public ViewResult extras(Map<String, ?> extras) {
		this.context.putExtras(extras);

		return this;
	}

	public ViewResult extra(String ident, Object value) {
		this.context.putExtra(ident, value);

		return this;
	}

	public DuelMvcContext getContext() {
		return this.context;
	}

	public Class<? extends DuelView> getViewType() {
		return this.view.getClass();
	}

	@Override
	public void write(OutputStream stream)
		throws IOException, WebApplicationException {

		Writer output = new OutputStreamWriter(stream, this.context.getFormat().getEncoding());

		try {
			this.view.render(this.context.setOutput(output));
 			output.flush();

		} catch (Exception ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);

 		} finally {
			this.context.setOutput(null);
		}
	}
}
