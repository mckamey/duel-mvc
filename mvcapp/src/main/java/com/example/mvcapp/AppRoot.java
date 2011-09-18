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

import com.example.mvcapp.aspects.ErrorHandler;
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
				if (currentStage() == Stage.PRODUCTION) {
					bindConstant().annotatedWith(Names.named(Globals.CDN_HOST)).to("cdn.example.com");
				} else {
					bindConstant().annotatedWith(Names.named(Globals.CDN_HOST)).to("");
				}

				// name of .properties file containing CDN mappings
				bindConstant().annotatedWith(Names.named(Globals.CDN_MAP_NAME)).to("cdn");

				// action timing duration threshold in ms
				bindConstant().annotatedWith(Names.named(Globals.ACTION_THRESHOLD)).to(250.0);//ms

				// render timing duration threshold in ms
				bindConstant().annotatedWith(Names.named(Globals.RENDER_THRESHOLD)).to(100.0);//ms

				// request timing duration threshold in ms
				bindConstant().annotatedWith(Names.named(Globals.LATENCY_THRESHOLD)).to(500.0);//ms
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

			@Override
			protected void configureApp() {
				// setup Guice-style configuration values
				bindConstants();

				// bind JAXB/JSON serialization
				bind(JacksonJaxbJsonProvider.class).in(Singleton.class);

				// context for each request
				bind(DuelMvcContext.class).to(AppContext.class);

				// unhandled exception mapping
				bind(ErrorHandler.class);
			}

			/**
			 * By convention, every concrete class in the controller packages will be available
			 */
			@Override
			protected Package[] getControllerPackages() {
				return new Package[] {
					BaseController.class.getPackage()
				};
			}

			/**
			 * LinkInterceptor is used by the merge tool to manage references to static resources.
			 * Configured for build-time optimization via pom.xml
			 * @param stage
			 * @param cdnHost
			 * @param cdnMapName
			 * @return
			 */
			@Provides
			@Singleton
			@SuppressWarnings("unused")
			protected LinkInterceptor linkInterceptorSingleton(
					Stage stage,
					@Named(Globals.CDN_HOST) String cdnHost,
					@Named(Globals.CDN_MAP_NAME) String cdnMapName) {

				LinkInterceptor linkInterceptor = null;

				try {
					linkInterceptor = new CDNLinkInterceptor(
						cdnHost,
						ResourceBundle.getBundle(cdnMapName),
						(stage == Stage.DEVELOPMENT));

				} catch (URISyntaxException ex) {
					// Guice providers cannot throw exceptions
					addError(ex);
				}

				return linkInterceptor;
			}
		});
	}
}
