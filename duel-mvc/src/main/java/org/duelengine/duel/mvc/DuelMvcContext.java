package org.duelengine.duel.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.duelengine.duel.DuelContext;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.sun.jersey.api.core.ExtendedUriInfo;

/**
 * DuelContext plus DUEL-MVC request context
 */
public class DuelMvcContext extends DuelContext {

	private final Injector injector;
	private final Stage stage;
	private final ExtendedUriInfo uriInfo;
	private final List<ActionFilter> actionFilters = new ArrayList<ActionFilter>();
	private final List<ResultFilter> resultFilters = new ArrayList<ResultFilter>();
	private final List<ExceptionFilter> errorFilters = new ArrayList<ExceptionFilter>();
	private boolean filtersPopulated;

	@Inject
	public DuelMvcContext(
			Injector injector,
			Stage stage,
			ExtendedUriInfo uriInfo) {

		if (injector == null) {
			throw new NullPointerException("injector");
		}
		if (uriInfo == null) {
			throw new NullPointerException("uriInfo");
		}
		if (stage == null) {
			throw new NullPointerException("stage");
		}

		this.injector = injector;
		this.stage = stage;
		this.uriInfo = uriInfo;
	}

	public Stage getStage() {
		return stage;
	}

	public ExtendedUriInfo uriInfo() {
		return uriInfo;
	}

	List<ActionFilter> getActionFilters() {
		return actionFilters;
	}

	List<ResultFilter> getResultFilters() {
		return resultFilters;
	}

	List<ExceptionFilter> getErrorFilters() {
		return errorFilters;
	}

	void ensureFilters(MethodInvocation invocation) {
		if (filtersPopulated) {
			return;
		}

		// class-level filters first
		Object controller = invocation.getThis();
		if (controller != null) {
			// NOTE: controller may be in multiple groups

			if (controller instanceof ActionFilter) {
				actionFilters.add((ActionFilter)controller);
			}

			if (controller instanceof ResultFilter) {
				resultFilters.add((ResultFilter)controller);
			}

			if (controller instanceof ExceptionFilter) {
				errorFilters.add((ExceptionFilter)controller);
			}

			Apply filters = controller.getClass().getAnnotation(Apply.class);
			if (filters != null) {
				addFilters(filters);
			}
		}

		// method-level filters follow
		Method method = invocation.getMethod();
		if (method != null) {
			Apply filters = method.getAnnotation(Apply.class);
			if (filters != null) {
				addFilters(filters);
			}
		}

		filtersPopulated = true;
	}

	private void addFilters(Apply annotation) {
		for (Class<? extends DuelMvcFilter> filterType : annotation.value()) {
			// NOTE: filter may be in multiple groups
			DuelMvcFilter filter = injector.getInstance(filterType);

			if (filter instanceof ActionFilter) {
				actionFilters.add((ActionFilter)filter);
			}

			if (filter instanceof ResultFilter) {
				resultFilters.add((ResultFilter)filter);
			}

			if (filter instanceof ExceptionFilter) {
				errorFilters.add((ExceptionFilter)filter);
			}
		}
	}
}
