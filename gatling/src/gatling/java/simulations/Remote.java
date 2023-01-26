package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.incrementConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Remote extends Simulation {
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final boolean HTTP2 = System.getProperty("http2") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
	static final int USERS = Integer.parseInt(System.getProperty("users", "1024"));

	{
		HttpProtocolBuilder httpProtocolBuilder =
				http.baseUrl("https://" + HOST + ":" + PORT)
						.acceptHeader("text/plain")
						.acceptLanguageHeader("en-US,en;q=0.5")
						.userAgentHeader("Gatling");

		ScenarioBuilder scenarioBuilder =
				scenario("Receive from remote call")
						.forever().on(exec(http("remote_call").get("/remote")));

		if (HTTP2) {
			httpProtocolBuilder = httpProtocolBuilder.enableHttp2();
		}

		setUp(scenarioBuilder
				.injectClosed(
						incrementConcurrentUsers(USERS) //32x8 (256), 128x8 (1024), 512x8 (4096), 1024x8 (8192), 2048x8 (16_384)
								.times(8)
								.eachLevelLasting(Duration.ofSeconds(20))
								.separatedByRampsLasting(Duration.ofSeconds(10))
								.startingFrom(0)))
				.maxDuration(Duration.ofMinutes(5))
				.protocols(httpProtocolBuilder);
	}
}
