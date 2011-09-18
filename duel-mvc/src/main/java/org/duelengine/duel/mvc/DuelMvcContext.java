package org.duelengine.duel.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.duelengine.duel.DuelContext;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;

/**
 * DuelContext plus DUEL-MVC request context
 */
public class DuelMvcContext extends DuelContext {

	private final Injector injector;
	private final Stage stage;
	private final ExtendedUriInfo uriInfo;
	private final List<AuthFilter> authFilters = new ArrayList<AuthFilter>();
	private final List<ActionFilter> actionFilters = new ArrayList<ActionFilter>();
	private final List<ResultFilter> resultFilters = new ArrayList<ResultFilter>();
	private final List<ErrorFilter> errorFilters = new ArrayList<ErrorFilter>();
	private boolean filtersApplied;

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

	List<AuthFilter> getAuthFilters() {
		return authFilters;
	}

	List<ActionFilter> getActionFilters() {
		return actionFilters;
	}

	List<ResultFilter> getResultFilters() {
		return resultFilters;
	}

	List<ErrorFilter> getErrorFilters() {
		return errorFilters;
	}

	/**
	 * Ensures the filters were aggregated for request
	 */
	void ensureFilters() {
		if (filtersApplied) {
			return;
		}
		filtersApplied = true;

		AbstractResourceMethod method = uriInfo.getMatchedMethod();

		if (method == null) {
			return;
		}

		AbstractResource controller = method.getResource();

		// class-level filters applied first
		if (controller != null) {
			for (Annotation annotation : controller.getResourceClass().getAnnotations()) {
				if (annotation instanceof Apply) {
					addFilters((Apply)annotation);
				}
			}
		}

		// method-level filters applied last
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof Apply) {
				addFilters((Apply)annotation);
			}
		}
	}

	/**
	 * Aggregates the filters for request, gains access to controller instance 
	 * @param invocation
	 */
	void buildFilters(MethodInvocation invocation) {
		if (filtersApplied) {
			return;
		}
		filtersApplied = true;

		Method method = invocation.getMethod();

		if (method == null) {
			return;
		}

		Object controller = invocation.getThis();

		// class-level filters applied first
		if (controller != null) {
			addFilter(controller);

			for (Annotation annotation : controller.getClass().getAnnotations()) {
				if (annotation instanceof Apply) {
					addFilters((Apply)annotation);
				}
			}
		}

		// method-level filters applied last
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof Apply) {
				addFilters((Apply)annotation);
			}
		}
	}

	private void addFilters(Apply annotation) {
		Class<? extends DuelMvcFilter>[] filters = annotation.value();
		if (filters == null) {
			return;
		}

		for (Class<? extends DuelMvcFilter> filterType : filters) {
			addFilter(injector.getInstance(filterType));
		}
	}

	private void addFilter(Object value) {

		// NOTE: type may implement multiple filter interfaces at once

		if (value instanceof AuthFilter) {
			authFilters.add((AuthFilter)value);
		}

		if (value instanceof ActionFilter) {
			actionFilters.add((ActionFilter)value);
		}

		if (value instanceof ResultFilter) {
			resultFilters.add((ResultFilter)value);
		}

		if (value instanceof ErrorFilter) {
			errorFilters.add((ErrorFilter)value);
		}
	}
}
