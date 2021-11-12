package com.promineotech.jeep.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;
import com.promineotech.jeep.support.FetchJeepsTestSupport;




class FetchJeepsTest  {
	
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT )
	@ActiveProfiles("test")
	@Sql(scripts = {
		    "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
		    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
	    config = @SqlConfig(encoding = "utf-8"))
	@Nested
	class TestsThatDontPolluteTheApplicationContext extends FetchJeepsTestSupport{
		
		@Test
		void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
			// Given a valid model, trim, URI
			
					JeepModel model = JeepModel.WRANGLER;
					String trim = "Sport";
					String uri = String.format("%s?model=%s&trim=%s", 
							getBaseUri(), model, trim);
					
					// when a connection is made to the URI 
					
					ResponseEntity<List<Jeep>> response = getRestTemplate()
							.exchange(uri, HttpMethod.GET, null, 
									new ParameterizedTypeReference<>() {});
				
					// Then a success (ok-200) status code is returned 
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
					// And assert that a jeep list is returned as expected
					List<Jeep> expected = buildExpected();
					List<Jeep> actual = response.getBody();
					actual.forEach(jeep -> jeep.setModelPK(null));
					assertThat(actual).isEqualTo(expected);
					
				}
		
		@Test
		void testThatAnErrorMessageReturnedWhenUnkownModelAndTrimAreSupplied() {
			// Given a valid model, trim, URI
			
					JeepModel model = JeepModel.WRANGLER;
					String trim = "Unknown trim";
					String uri = String.format("%s?model=%s&trim=%s", 
							getBaseUri(), model, trim);
					
					// when a connection is made to the URI 
					
					ResponseEntity<Map<String, Object>> response = getRestTemplate()
							.exchange(uri, HttpMethod.GET, null, 
									new ParameterizedTypeReference<>() {});
				
					// Then an error message (not found-404) status code is returned 
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
					// And assert that an error message is returned 
				Map<String, Object> error = response.getBody();
				assertErrorValid(error, HttpStatus.NOT_FOUND);
				
				}

		@ParameterizedTest
		@MethodSource("com.promineotech.jeep.controller.FetchJeepsTest#paramertsForInvalidInput")
		void testThatAnErrorMessageReturnedWhenInvalidValueTrimAreSupplied( String model, String trim, String reason) {
			// Given a valid model, trim, URI
		
					String uri = String.format("%s?model=%s&trim=%s", 
							getBaseUri(), model, trim);
					
					// when a connection is made to the URI 
					
					ResponseEntity<Map<String, Object>> response = getRestTemplate()
							.exchange(uri, HttpMethod.GET, null, 
									new ParameterizedTypeReference<>() {});
				
					// Then an error message (not found-404) status code is returned 
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
					// And assert that an error message is returned 
				Map<String, Object> error = response.getBody();
				assertErrorValid(error, HttpStatus.BAD_REQUEST);
				
				}
		
		
	}
	
	static Stream<Arguments> paramertsForInvalidInput(){
		return Stream.of(
				arguments("WRANGLER", "inm%4567", "contains invalid value")
				,
				arguments("Invalid", "inm%4567", "contains invalid value")
				);
		
	
	}
	
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT )
	@ActiveProfiles("test")
	@Sql(scripts = {
		    "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
		    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
	    config = @SqlConfig(encoding = "utf-8"))
	@Nested
	class TestsThatPolluteTheApplicationContext extends FetchJeepsTestSupport{
		
	@MockBean
	private JeepSalesService jeepSalesService;
		
	@Test
	void testThatAnUnplannedErrorMessageReturned500ErrorMessage() {
		// Given a valid model, trim, URI
		
				JeepModel model = JeepModel.WRANGLER;
				String trim = "Sport";
				String uri = String.format("%s?model=%s&trim=%s", 
						getBaseUri(), model, trim);
				
				doThrow(new RuntimeException ("Outch!")). when(jeepSalesService).fetchJeeps(model, trim);
				
				// when a connection is made to the URI
				
				ResponseEntity<Map<String, Object>> response = getRestTemplate()
						.exchange(uri, HttpMethod.GET, null, 
								new ParameterizedTypeReference<>() {});
			
				// Then an internal server error (unplanned error-500) status code is returned 
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
				// And assert that an error message is returned 
			Map<String, Object> error = response.getBody();
			assertErrorValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
			
			}
	
	}
	
	
	
		
	}


