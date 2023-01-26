package org.example;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class ServerApplication {
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final boolean HTTP2 = System.getProperty("http2") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "9090"));

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
