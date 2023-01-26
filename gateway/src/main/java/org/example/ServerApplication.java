package org.example;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;

public class ServerApplication {
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final boolean HTTP2 = System.getProperty("http2") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
	static final String REMOTE_SERVER_URL = System.getProperty("remoteServer", "https://127.0.0.1:9090");

	static final ConnectionProvider PROVIDER =
			ConnectionProvider.builder("gateway")
					.maxConnections(500)
					.pendingAcquireMaxCount(8 * 500)
					.build();

	static final HttpClient CLIENT =
			HttpClient.create(PROVIDER)
					.baseUrl(REMOTE_SERVER_URL)
					.secure(spec -> spec.sslContext(
							HTTP2 ? Http2SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE)) :
									Http11SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE))
					));

	public static void main(String[] args) throws Exception {
		SelfSignedCertificate cert = new SelfSignedCertificate();

		HttpServer.create()
				.host(HOST)
				.port(PORT)
				.protocol(HTTP2 ? HttpProtocol.H2 : HttpProtocol.HTTP11)
				.secure(spec -> spec.sslContext(
						HTTP2 ? Http2SslContextSpec.forServer(cert.certificate(), cert.privateKey()) :
								Http11SslContextSpec.forServer(cert.certificate(), cert.privateKey())))
				.route(RouterFunctionConfig.routesBuilder())
				.doOnBound(server -> System.out.println("Server is bound on " + server.address()))
				.bindNow()
				.onDispose()
				.block();
	}
}
