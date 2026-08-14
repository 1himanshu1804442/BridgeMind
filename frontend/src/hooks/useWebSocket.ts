import { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';
import type { WebSocketMessage } from '../types';

export function useWebSocket(workspaceId: string | null, missionId: string | null) {
    const [isConnected, setIsConnected] = useState(false);
    const [lastMessage, setLastMessage] = useState<WebSocketMessage | null>(null);
    const clientRef = useRef<Client | null>(null);

    useEffect(() => {
        if (!workspaceId) return;

        const wsEndpoint = typeof window !== 'undefined' && window.location.port === '3000'
            ? '/ws'
            : 'http://localhost:8080/ws';

        const client = new Client({
            webSocketFactory: () => new SockJS(wsEndpoint),
            reconnectDelay: 5000,
            onConnect: () => {
                setIsConnected(true);
                client.subscribe(`/topic/workspace/${workspaceId}/missions`, (msg: IMessage) => {
                    setLastMessage(JSON.parse(msg.body));
                });
                if (missionId) {
                    client.subscribe(`/topic/mission/${missionId}/agents`, (msg: IMessage) => {
                        setLastMessage(JSON.parse(msg.body));
                    });
                }
            },
            onDisconnect: () => setIsConnected(false),
        });

        client.activate();
        clientRef.current = client;

        return () => {
            client.deactivate();
        };
    }, [workspaceId, missionId]);

    return { isConnected, lastMessage };
}
