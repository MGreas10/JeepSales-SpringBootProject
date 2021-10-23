package com.promineotech.jeep.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.promineotech.jeep.entity.Jeep;

@RestController

public class DefaultJeepSalesController implements JeepSalesController {

	public List<Jeep> fetchJeedp(String model, String trim) {
		
		return null;
	}

}
