package com.hack.user.infrastructure;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Service
public class AwsSnsService {
    private final SnsClient snsClient;

    public AwsSnsService(AwsConfig awsConfig, SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String createTopic(String email) {
        String topicName = "UserTopic_" + email.replace("@", "_").replace(".", "_");

        CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                .name(topicName)
                .build();

        CreateTopicResponse createTopicResponse = snsClient.createTopic(createTopicRequest);
        System.out.println("Tópico SNS criado com ARN: " + createTopicResponse.topicArn());
        return createTopicResponse.topicArn();
    }

    public void subscribeToTopic(String topicArn, String email) {
        SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build();

        snsClient.subscribe(subscribeRequest);
        System.out.println("Assinatura SNS criada para e-mail: " + email);
    }
}