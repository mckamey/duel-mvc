package com.example.mvcapp;

import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.duelengine.duel.CDNLinkInterceptor;
import org.duelengine.duel.LinkInterceptor;
import org.duelengine.duel.mvc.DuelMvcContext;
import org.duelengine.duel.mvc.DuelMvcModule;

import com.example.mvcapp.aspects.ExceptionRouter;
import com.example.mvcapp.controllers.BaseController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;

public class AppRoot extends GuiceServletContextListener {

	@Override
	public Injector getInjector() {
		return Guice.createInjector(Stage.DEVELOPMENT, new DuelMvcModule() {

			/**
			 * Configuration constant bindings
			 */
			private void bindConstants() {

				// CDN server hostname in Stage.PRODUCTION, e.g. "cdn.example.com"
				bindConstant().annotatedWith(Names.named("CDN_HOST")).to(
						(currentStage() != Stage.PRODUCTION) ? "" : "cdn.example.com");

				// name of .properties file containing CDN mappings
				bindConstant().annotatedWith(Names.named("CDN_MAP")).to("cdn");

				// action timing duration threshold in ms
				bindConstant().annotatedWith(Names.named("ACTION_THRESHOLD")).to(250.0);//ms

				// render timing duration threshold in ms
				bindConstant().annotatedWith(Names.named("RENDER_THRESHOLD")).to(100.0);//ms

				// request timing duration threshold in ms
				bindConstant().annotatedWith(Names.named("LATENCY_THRESHOLD")).to(500.0);//ms
			}

			/**
			 * Static URL route bindings
			 */
			@Override
			protected void bindStaticRoutes(HttpServlet defaultServlet, Filter neverExpire) {
				// http://google-guice.googlecode.com/svn/trunk/javadoc/com/google/inject/servlet/ServletModule.html

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

				filterRegex(
					"/cdn/.*"
				).through(neverExpire);
			}

			@Provides
			@Singleton
			@SuppressWarnings("unused")
			protected LinkInterceptor linkInterceptorProvider(
					Stage stage,
					@Named("CDN_HOST") String cdnHost,
					@Named("CDN_MAP") String cdnMapName) {

				try {
					return new CDNLinkInterceptor(
							cdnHost,
							ResourceBundle.getBundle(cdnMapName),
							(stage == Stage.DEVELOPMENT));

				} catch (URISyntaxException ex) {
					addError(ex);
					return null;
				}
			}

			/**
			 * By convention everything in controllers package will be bound
			 */
			@Override
			protected String[] getControllerPackages() {
				return new String[] { BaseController.class.getPackage().getName() };
			}

			/**
			 * JAX-RS serialization bindings
			 */
			private void bindSerializers() {
				// bind JAXB/JSON serialization
				bind(JacksonJaxbJsonProvider.class).in(Singleton.class);
			}

			@Override
			protected void configureApp() {

				// context for each request
				bind(DuelMvcContext.class).to(AppContext.class);

				// intercept all exceptions
				bind(ExceptionRouter.class);

				// setup Guice-style configuration values
				bindConstants();

				// register JAX-RS serialization providers
				bindSerializers();
			}
		});
	}
}
