package cf.webex;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import cf.webex.rest.Dashboard_Auth_RestApi;
import cf.webex.tokenization.AuthTokens;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
@EnableAutoConfiguration(exclude={RedisAutoConfiguration.class})
public class WebExApplication {

	public static String applicationDir = "app/";

	public static void main(String[] args) throws IOException, InvalidConfigurationException {

		File credentialsFile = new File(applicationDir + "values/credentials.yml");

		if(!credentialsFile.exists()) {

			credentialsFile.createNewFile();
		}

		Dashboard_Auth_RestApi.yamlConfiguration.load(credentialsFile);

		if(!Dashboard_Auth_RestApi.yamlConfiguration.contains("auth.users.admin@localhost.password")) {

			Dashboard_Auth_RestApi.yamlConfiguration.set("auth.users.admin@localhost.password", Base64.getEncoder().encodeToString("password".getBytes()));
			Dashboard_Auth_RestApi.yamlConfiguration.set("auth.users.admin@localhost.rank", "Admin");
			Dashboard_Auth_RestApi.yamlConfiguration.set("auth.users.admin@localhost.name", "Test");
			Dashboard_Auth_RestApi.yamlConfiguration.save(credentialsFile);
		}

//		new Thread(() -> {
//
//			while (credentialsFile.exists()) {
//
//				try {
//
//					Thread.sleep(10000);
//				} catch (InterruptedException interruptedException) {
//
//					interruptedException.printStackTrace();
//				}
//
//				try {
//
//					RestAPI.yamlConfiguration.load(credentialsFile);
//				} catch (IOException | InvalidConfigurationException exceptions) {
//
//					exceptions.printStackTrace();
//				}
//			}
//
//		}).start();

		File tokensFile = new File(applicationDir + "values/applicationtokens.lTok");
		new File(applicationDir + "values/").mkdirs();
		if(!tokensFile.exists()) {
			tokensFile.createNewFile();
		}

		AuthTokens.loadTokens(tokensFile);

		SpringApplication.run(WebExApplication.class, args);
	}

}
