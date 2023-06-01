package com.example.rsocket_demo;

import io.rsocket.Payload;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final Map<String, List<RSocketRequester>> participants = new ConcurrentHashMap<>();

    public void onConnect(RSocketRequester requester, String chattingAddress) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
//                    CLIENTS.add(requester);
                    if(participants.containsKey(chattingAddress))
                        participants.get(chattingAddress).add(requester);
                    else participants.put(chattingAddress, new ArrayList<>(Arrays.asList(requester)));

                    //추가 확인용 출력
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, List<RSocketRequester>> entry : participants.entrySet()) {
                        sb.append("key: " + entry.getKey() + "\n");
                        for (RSocketRequester rSocketRequester : entry.getValue()) {
                            sb.append("requester: " + rSocketRequester + "\n");
                        }
                    }
                    log.info(sb.toString());
                    //
                })
                .doOnError(error -> {
                    log.info(error.getMessage());
                })
                .doFinally(consumer -> {
//                    CLIENTS.remove(requester);
                    participants.get(chattingAddress).remove(requester);
                })
                .subscribe();
    }

    public Mono<ChatDto> message(ChatDto chatDto) {
        this.sendMessage(chatDto);
        return Mono.just(chatDto);
    }

    public void sendMessage(ChatDto chatDto) {
        Flux.fromIterable(participants.get(chatDto.getChattingAddress()))
                .doOnNext(ea -> {
                    ea.route("")
                            .data(chatDto)
                            .send()
                            .subscribe();
                })
                .subscribe();
    }
}