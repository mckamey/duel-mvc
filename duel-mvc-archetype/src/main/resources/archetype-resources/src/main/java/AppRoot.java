#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.lang.reflect.Modifier;
import java.util.*;
import javax.ws.rs.*;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.duelengine.duel.DuelContext;
import org.duelengine.duel.util.*;
import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import ${package}.aspects.*;
import ${package}.controllers.*;

public class AppRoot extends GuiceServletContextListener {

	@Override
	public Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {

			/**
			 * Configuration constant bindings
			 */
			private void bindConfigConstants() {
				// TODO: read environment var for DEV/STAGE/PROD
				// set debug in DEV
				// set CDN host in PROD

				// general debug mode (also controls compaction)
				bindConstant().annotatedWith(Names.named("DEBUG")).to(true);

				// CDN server hostname, e.g. "cdn.example.com"
				bindConstant().annotatedWith(Names.named("CDN_HOST")).to("");

				// name of .properties file containing CDN mappings
				bindConstant().annotatedWith(Names.named("CDN_MAP")).to("cdn");

				// action timing duration threshold in ms
				bindConstant().annotatedWith(Names.named("ACTION_THRESHOLD")).to(500.0);//ms

				// render timing duration threshold in ms
				bindConstant().annotatedWith(Names.named("RENDER_THRESHOLD")).to(100.0);//ms
			}

			/**
			 * JAX-RS serialization bindings
			 */
			private void bindSerializers() {
				// bind JAXB/JSON serialization
				bind(JacksonJaxbJsonProvider.class).in(Singleton.class);
			}

			/**
			 * AOP aspect bindings
			 */
			private void bindAspects() {
				// exception handling
				bind(ExceptionRouter.class);

				// view settings
				bind(DuelContext.class).toProvider(ViewContextProvider.class);

				// controller action timing
				ActionTimer actionTimer = new ActionTimer();
				this.requestInjection(actionTimer);

				bindInterceptor(
					Matchers.annotatedWith(Path.class),
					Matchers.annotatedWith(GET.class).or(Matchers.annotatedWith(POST.class)).or(Matchers.annotatedWith(PUT.class)).or(Matchers.annotatedWith(DELETE.class)).or(Matchers.annotatedWith(HEAD.class)),

					// aspects which intercept all controller actions
					actionTimer
				);
			}

			/**
			 * Static URL route bindings
			 */
			private void bindStaticRoutes() {
				// http://google-guice.googlecode.com/svn/trunk/javadoc/com/google/inject/servlet/ServletModule.html

				// this uses the standard static file servlet
				DefaultWrapperServlet defaultServlet = new DefaultWrapperServlet();

				serve(
					"/robots.txt",
					"/favicon.ico"
				).with(defaultServlet);

				serveRegex(
					"/cdn/.*",
					"/css/.*",
					"/js/.*",
					"/images/.*"
				).with(defaultServlet);

				// this sets the cache control headers to never expire
				NeverExpireFilter neverExpire = new NeverExpireFilter();

				filterRegex(
					"/cdn/.*"
				).through(neverExpire);
			}

			/**
			 * JAX-RS controller bindings
			 */
			private void bindControllers(String... packages) {
				// we have to explicitly bind each controller class
				// to ensure Guice controls instantiation for AOP
				// this will auto-register controllers in each package

				for (String packageName : packages) {
					try {
						Set<Class<?>> controllers = ClassEnumerator.getClasses(packageName);

						for (Class<?> controller : controllers) {
							if (controller.isInterface() || Modifier.isAbstract(controller.getModifiers())) {
								// filter abstract or interfaces
								continue;
							}

							// scope is determined by Guice annotations
							bind(controller);
						}
					} catch (Exception ex) {
						ex.printStackTrace();

						throw new IllegalArgumentException(packageName, ex);
					}
				}
			}

			@Override
			protected void configureServlets() {

				// setup Guice-style configuration values
				bindConfigConstants();

				// register JAX-RS serialization providers
				bindSerializers();

				// register JAX-RS controllers in package
				bindControllers(
					BaseController.class.getPackage().getName()
				);

				// register AOP aspects
				bindAspects();

				// the rest effectively replaces /WEB-INF/web.xml

				// map static routes
				bindStaticRoutes();

                // map all other routes to Guice
				// http://jersey.java.net/nonav/apidocs/latest/contribs/jersey-guice/com/sun/jersey/guice/spi/container/servlet/package-summary.html
				serve("/*").with(GuiceContainer.class);
			}
		});
	}
}
