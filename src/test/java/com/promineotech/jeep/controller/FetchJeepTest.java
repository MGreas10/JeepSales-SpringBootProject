package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;


import lombok.Getter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT )

@ActiveProfiles("test")
@Sql(scripts = {
	    "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
	    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
    config = @SqlConfig(encoding = "utf-8"))

 public class FetchJeepTest {

	@LocalServerPort
	private int serverPort;
	
	@Autowired
	@Getter
	private TestRestTemplate restTemplate;
	
	
	@Test 
	 public void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
		// Given a valid model, trim, URI
		
		JeepModel model = JeepModel.WRANGLER;
		String trim = "sport";
		String uri = String.format("http://localhost:%d/jeep?model=%s&trim=%s", 
				serverPort, model, trim);
		System.out.println(uri);
		
		// when a connection is made to the URI 
		
		ResponseEntity<List<Jeep>> response = getRestTemplate()
				.exchange(uri, HttpMethod.GET, null, 
						new ParameterizedTypeReference<List<Jeep>>() {});
	
		
		// Then a success (ok-200) status code is returned 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		// And assert that a jeep list is returned as expected
		List<Jeep> expected = buildExpected();
		System.out.println(expected);
		assertThat(response.getBody()).isEqualTo(expected);
		
	}
	

	protected List<Jeep> buildExpected(){
		List<Jeep> list = new LinkedList<Jeep>();
		list.add(Jeep.builder()
				.modelId(JeepModel.WRANGLER)
				.trimLevel("sport")
				.numDoors(2)
				.wheelSize(17)
				.basePrice(new BigDecimal(28475.00))
				.build());
		list.add(Jeep.builder()
				.modelId(JeepModel.WRANGLER)
				.trimLevel("sport")
				.numDoors(4)
				.wheelSize(17)
				.basePrice(new BigDecimal(31975.00))
				.build());
		
		return list;
	}
	
	//('WRANGLER', 'Sport', 2, 17, 28475.00);
	//('WRANGLER', 'Sport', 4, 17, 31975.00);
	
	
	
}
