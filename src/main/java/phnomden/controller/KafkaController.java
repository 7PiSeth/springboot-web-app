package phnomden.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import phnomden.service.KafkaConsumer;
import phnomden.service.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public KafkaController(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        kafkaProducer.sendMessage("test-topic", message);
        return "Message sent to Kafka: " + message;
    }
    
    @GetMapping("/consume")
    public List<String> consumeMessages() {
        return kafkaConsumer.getMessages();
    }
}