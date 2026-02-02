package org.example;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.pkitesting.CertificateBuilder;
import io.netty.pkitesting.X509Bundle;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;

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
					.protocol(HTTP2 ? HttpProtocol.H2 : HttpProtocol.HTTP11)
					.secure(spec -> spec.sslContext(
							HTTP2 ? Http2SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE)) :
									Http11SslContextSpec.forClient().configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE))
					));

	static void main(String[] args) throws Exception {
		X509Bundle cert = new CertificateBuilder().subject("CN=localhost").setIsCertificateAuthority(true).buildSelfSigned();
		SslProvider.ProtocolSslContextSpec sslContext =
				HTTP2 ? Http2SslContextSpec.forServer(cert.toTempCertChainPem(), cert.toTempPrivateKeyPem()) :
						Http11SslContextSpec.forServer(cert.toTempCertChainPem(), cert.toTempPrivateKeyPem());

		HttpServer.create()
				.host(HOST)
				.port(PORT)
				.protocol(HTTP2 ? HttpProtocol.H2 : HttpProtocol.HTTP11)
				.secure(spec -> spec.sslContext(sslContext))
				.route(RouterFunctionConfig.routesBuilder())
				.doOnBound(server -> System.out.println("Server is bound on " + server.address()))
				.bindNow()
				.onDispose()
				.block();
	}
}
