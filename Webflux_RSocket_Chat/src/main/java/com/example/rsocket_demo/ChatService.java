package com.example.rsocket_demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    public void onConnect(RSocketRequester requester) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    CLIENTS.add(requester);
                    log.info("저장함");
                })
                .doOnError(error -> {
                })
                .doFinally(consumer -> {
                    CLIENTS.remove(requester);
                })
                .subscribe();
    }

    public Mono<ChatDto> message(ChatDto chatDto) {
        log.info(chatDto.getMessage() + "라고 옴!");
        this.sendMessage(chatDto);
        return Mono.just(chatDto);
    }

    public void sendMessage(ChatDto chatDto) {
        Flux.fromIterable(CLIENTS)
                .doOnNext(ea -> {
                    ea.route("")
                            .data(chatDto)
                            .send()
                            .subscribe();
                })
                .subscribe();
    }
}