package com.promineotech.jeep.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
class DefaultJeepSalesService implements JeepSalesService {

	public List<Jeep> fetchJeeps(JeepModel model, String trim) {
		log.info("The fetchJeeps method was calld with was called with model}= model&trim{}=", model,trim);
		return null;
	}

}
