package org.duelengine.duel.mvc;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import net.sf.ehcache.constructs.web.filter.GzipFilter;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public abstract class DuelMvcModule extends JerseyServletModule {

	private final HttpServlet defaultServlet = new DefaultWrapperServlet();
	private final Filter neverExpireFilter = new NeverExpireFilter();
	private final Filter gzipFilter = new GzipFilter();

	/**
	 * Standard static file servlet
	 */
	protected HttpServlet getDefaultServlet() {
		return defaultServlet;
	}

	/**
	 * Servlet Filter which sets the cache control headers to never expire
	 */
	protected Filter getNeverExpireFilter() {
		return neverExpireFilter;
	}

	/**
	 * Servlet Filter which GZIP compresses response
	 */
	protected Filter getGzipFilter() {
		return gzipFilter;
	}

	/**
	 * Miscellaneous IoC configuration
	 */
	protected abstract void configureApp();

	/**
	 * Static URL route bindings
	 * 
	 * http://google-guice.googlecode.com/svn/trunk/javadoc/com/google/inject/servlet/ServletModule.html
	 */
	protected abstract void bindStaticRoutes();

	/**
	 * Gets the set of packages containing all MVC controllers.
	 * Return null for manually binding controllers.
	 */
	protected abstract Package[] getControllerPackages();

	/**
	 * MVC controller bindings
	 * 
	 * We have to explicitly bind each controller class to ensure Guice controls instantiation for AOP.
	 * Default implementation binds by convention entire controller packages using {@link #bindPackages(String...)}
	 */
	protected void bindControllers() {
		// ensure all controllers are built via Guice for AOP support
		bindPackages(this.getControllerPackages());
	}

	/**
	 * MVC view result bindings
	 * 
	 * We have to explicitly bind each View Result class to ensure Guice controls instantiation for AOP.
	 * This implementation binds a factory to expose via the base controller
	 */
	private void bindViewResults() {
		// ensure all view results are built via Guice for AOP support
		install(new FactoryModuleBuilder()
			.implement(ViewResult.class, ViewResult.class)
			.build(ViewResultFactory.class));
	}

	/**
	 * AOP interceptor bindings
	 */
	private void bindFilterChains() {

		// intercept all controller actions via filter chain
		install(new FactoryModuleBuilder()
			.implement(ActionFilterContext.class, ActionFilterContext.class)
			.build(ActionFilterContextFactory.class));

		ActionFilterInterceptor actionInterceptor = new ActionFilterInterceptor();
		requestInjection(actionInterceptor);

		bindInterceptor(
			Matchers.annotatedWith(Path.class),
			Matchers.annotatedWith(GET.class)
				.or(Matchers.annotatedWith(POST.class))
				.or(Matchers.annotatedWith(PUT.class))
				.or(Matchers.annotatedWith(DELETE.class))
				.or(Matchers.annotatedWith(HEAD.class))
				.or(Matchers.annotatedWith(OPTIONS.class)),
			actionInterceptor
		);

		// intercept all view results via filter chain
		install(new FactoryModuleBuilder()
			.implement(ResultFilterContext.class, ResultFilterContext.class)
			.build(ResultFilterContextFactory.class));

		ResultFilterInterceptor requestInterceptor = new ResultFilterInterceptor();
		requestInjection(requestInterceptor);

		bindInterceptor(
			Matchers.subclassesOf(ViewResult.class),
			new AbstractMatcher<Method>() {
				public boolean matches(Method m) {
					Class<?>[] paramTypes = m.getParameterTypes();
					return Void.TYPE.equals(m.getReturnType()) &&
							"write".equals(m.getName()) &&
							(paramTypes.length == 1) &&
							OutputStream.class.equals(paramTypes[0]);
				}
			},
			requestInterceptor);
	}

	/**
	 * Utility method which binds all concrete classes in the specified packages. Used for Guice-AOP.
	 * @param packages list of packages to bind
	 */
	private void bindPackages(Package... packages) {
		if (packages == null) {
			return;
		}

		for (Package pkg : packages) {
			try {
				Set<Class<?>> types = ClassEnumerator.getClasses(pkg.getName());

				for (Class<?> type : types) {
					if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
						// filter abstract or interfaces
						continue;
					}

					// scope is determined by Guice annotations
					bind(type);
				}

			} catch (Throwable ex) {
				addError(ex);
			}
		}
	}

	@Override
	protected final void configureServlets() {

		try {
			// bind any app-specific IoC
			configureApp();

		} catch (Throwable ex) {
			addError(ex);
		}

		try {
			// bind view results via factory
			bindViewResults();

		} catch (Throwable ex) {
			addError(ex);
		}

		try {
			// bind controllers by convention
			bindControllers();

		} catch (Throwable ex) {
			addError(ex);
		}

		try {
			// setup action / result filters
			bindFilterChains();

		} catch (Throwable ex) {
			addError(ex);
		}

		// ----------------------------------------------
		// the rest effectively replaces /WEB-INF/web.xml

		try {
			// map static routes
			bindStaticRoutes();

		} catch (Throwable ex) {
			addError(ex);
		}

		try {
			// map all other routes to Guice
			// http://jersey.java.net/nonav/apidocs/latest/contribs/jersey-guice/com/sun/jersey/guice/spi/container/servlet/package-summary.html
			serve("/*").with(GuiceContainer.class);

		} catch (Throwable ex) {
			addError(ex);
		}
	}
}
