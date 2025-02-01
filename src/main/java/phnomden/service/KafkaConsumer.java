package phnomden.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	
	private final List<String> messages = new ArrayList<>();

	@KafkaListener(topics = "test-topic", groupId = "my-group")
	public void consume(ConsumerRecord<String, String> record) {
		messages.add(record.value());
		System.out.println("Received message: " + record.value());
	}

	public List<String> getMessages() {
		return new ArrayList<>(messages);
	}
}