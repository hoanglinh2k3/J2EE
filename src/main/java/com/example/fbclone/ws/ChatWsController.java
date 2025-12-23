package com.example.fbclone.ws;

import com.example.fbclone.dto.chat.MessageResponse;
import com.example.fbclone.dto.chat.SendMessageRequest;
import com.example.fbclone.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

  private final MessageService messageService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Client send to: /app/conversations/{conversationId}/send
   * Server broadcast: /topic/conversations/{conversationId}
   */
  @MessageMapping("/conversations/{conversationId}/send")
  public void send(@DestinationVariable Long conversationId, @Valid @Payload SendMessageRequest req, Principal principal) {
    if (!(principal instanceof Authentication auth)) {
      throw new RuntimeException("Unauthenticated websocket");
    }

    SecurityContextHolder.getContext().setAuthentication(auth);
    try {
      MessageResponse saved = messageService.send(conversationId, req);
      messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, saved);
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}
