import React, { useState, useEffect } from 'react';
import {
  RSocketClient,
  JsonSerializer,
  IdentitySerializer,
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import { EchoResponder } from './responder';

const Chatting = () => {
  const [message, setMessage] = useState('');
  const [socket, setSocket] = useState(null);
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    connect();
  }, []);

  const messageReceiver = (payload) => {
    setMessages((prevMessages) => [...prevMessages, payload.data]);
  };
  const responder = new EchoResponder(messageReceiver);

  const send = () => {
    socket
      .requestResponse({
        data: {
          username: 'Superpil',
          message: message,
        },
        metadata: String.fromCharCode('message'.length) + 'message',
      })
      .subscribe({
        onComplete: (com) => {
          console.log('com : ', com);
        },
        onError: (error) => {
          console.log(error);
        },
        onNext: (payload) => {
          console.log(payload.data);
        },
        onSubscribe: (subscription) => {
          console.log('subscription', subscription);
        },
      });
  };

  const connect = () => {
    const client = new RSocketClient({
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer,
      },
      setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'message/x.rsocket.routing.v0',
      },
      responder: responder,
      transport: new RSocketWebSocketClient({
        url: 'ws://localhost:6565/rs', 
      }),
    });

    client.connect().subscribe({
      onComplete: (socket) => {
        setSocket(socket);
      },
      onError: (error) => {
        console.log(error);
      },
      onSubscribe: (cancel) => {
        console.log(cancel);
      },
    });
  };

  return (
    <div>
      <h1>Chatting</h1>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />
      <button onClick={send}>전송</button>
      <ul>
        {messages.map((item, index) => (
          <li key={index}>{item.username} : {item.message}</li>
        ))}
      </ul>
    </div>
  );
};

export default Chatting;