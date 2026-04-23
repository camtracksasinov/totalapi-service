// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	/**
	 * private ApiInfo apiInfo() { return new ApiInfo("Total CM REST API", "API to
	 * read total position Vehicle and Trip.", "Camtrack Ink", "Terms of service",
	 * new Contact("Camtrack SAS", "https://camtrack.net", "support@camtrack.net"),
	 * "License of API", "API license URL", (Collection)Collections.emptyList()); }
	 */

	private Info apiInfo() {
		return new Info().title("Total CM REST API").description("API to read total position Vehicle and Trip.")
				.version("2.0").contact(apiContact()).license(apiLicence());
	}

	private License apiLicence() {
		return new License().name("MIT Licence").url("https://opensource.org/licenses/mit-license.php");
	}

	private Contact apiContact() {
		return new Contact().name("Team Developpement").email("innovation@camtrack.net").url("https://camtrack.net");
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().servers(Arrays.asList(new Server().description("Default Ymane Server URL").url("/api")))
				.info(apiInfo()).paths(null);
	}

	/**
	 * @Bean public Docket apiUser() { return new
	 *       Docket(DocumentationType.SWAGGER_2).apiInfo(this.apiInfo()).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.regex("(?!/service/admin/*).+")).paths(PathSelectors.regex("(?!/error).+")).paths(PathSelectors.regex("(?!/api/error).+")).paths(PathSelectors.regex("(?!/actuator).+")).paths(PathSelectors.regex("(?!/api/actuator).+")).paths(PathSelectors.any()).build();
	 *       }
	 */
	/**
	 * @Bean UiConfiguration uiConfig() { return
	 *       UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false).defaultModelsExpandDepth(1).defaultModelExpandDepth(1).defaultModelRendering(ModelRendering.EXAMPLE).displayRequestDuration(false).docExpansion(DocExpansion.NONE).filter((Object)false).maxDisplayedTags((Integer)null).operationsSorter(OperationsSorter.ALPHA).showExtensions(false).tagsSorter(TagsSorter.ALPHA).supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS).validatorUrl((String)null).build();
	 *       }
	 */
}
