package com.greatlearning.surabi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.greatlearning.surabi.dao.PaymentDao;
import com.greatlearning.surabi.domain.CreditCard;
import com.greatlearning.surabi.domain.Order;
import com.greatlearning.surabi.domain.Payment;
import com.greatlearning.surabi.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	PaymentDao paymentDao;
	
	
	private RabbitTemplate paymentCompletedTemplate;
	private RestTemplate rest;

	@Autowired
	public PaymentServiceImpl(RabbitTemplate rabbitTemplate, @LoadBalanced RestTemplate rest) {
		this.paymentCompletedTemplate = rabbitTemplate;
		this.rest = rest;
	}

	@Override
	public void save(Payment payment) {
		paymentDao.save(payment);

	}

	@Override
	public Payment update(Payment payment) {
		return paymentDao.update(payment);
	}

	@Override
	public List<Payment> findAll() {
		return paymentDao.findAll();
	}

	@Override
	public Payment findOne(Long id) {
		return paymentDao.findOne(id);
	}
	
	@Override
	public void publish(Payment payment) {
		
		paymentCompletedTemplate.convertAndSend(payment);
	}

	@Override
	public boolean checkCard(long customerId) {
		CreditCard creditCard = rest.getForObject("http://Customer-service/customers/check-card/"+customerId, CreditCard.class);
		return creditCard != null;
	}

	@Override
	public CreditCard getCard(long customerId) {
		CreditCard creditCard = rest.getForObject("http://Customer-service/customers/check-card/"+customerId, CreditCard.class);
		return creditCard;
	}

}
