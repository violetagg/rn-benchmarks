package org.example;

import io.netty.pkitesting.CertificateBuilder;
import io.netty.pkitesting.X509Bundle;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.SslProvider;

public class ServerApplication {
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final boolean HTTP2 = System.getProperty("http2") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "9090"));

	public static void main(String[] args) throws Exception {
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
