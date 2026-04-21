package src.core.config;

import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SecurityPropertiesValidator {

	@Value("${spring.datasource.password:}")
	private String dbPassword;

	@Value("${application.security.jwt.secret-key:}")
	private String jwtSecret;

	@Value("${cloudinary.cloud-name:}")
	private String cloudinaryCloudName;

	@Value("${cloudinary.api-key:}")
	private String cloudinaryApiKey;

	@Value("${cloudinary.api-secret:}")
	private String cloudinaryApiSecret;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Value("${spring.mail.password:}")
	private String mailPassword;

	@PostConstruct
	void validateSecrets() {
		requireNotBlank(dbPassword, "DB_PASSWORD");
		requireNotBlank(jwtSecret, "JWT_SECRET");
		validateJwtSecret(jwtSecret);
		requireNotBlank(cloudinaryCloudName, "CLOUDINARY_CLOUD_NAME");
		requireNotBlank(cloudinaryApiKey, "CLOUDINARY_API_KEY");
		requireNotBlank(cloudinaryApiSecret, "CLOUDINARY_API_SECRET");
		requireNotBlank(mailUsername, "MAIL_USERNAME");
		requireNotBlank(mailPassword, "MAIL_PASSWORD");
	}

	private void requireNotBlank(String value, String variableName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("Missing required security configuration: " + variableName);
		}
	}

	private void validateJwtSecret(String secret) {
		try {
			byte[] decoded = Decoders.BASE64.decode(secret);
			if (decoded.length < 32) {
				throw new IllegalStateException("JWT_SECRET must be base64 and at least 256 bits.");
			}
		} catch (IllegalArgumentException ex) {
			throw new IllegalStateException("JWT_SECRET must be a valid base64 string.", ex);
		}
	}
}

