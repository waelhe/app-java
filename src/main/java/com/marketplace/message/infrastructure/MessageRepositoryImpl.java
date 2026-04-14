package com.marketplace.message.infrastructure;

import com.marketplace.message.domain.Message;
import com.marketplace.message.domain.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final JpaMessageRepository jpaRepository;

    @Override
    public Message save(Message message) {
        return jpaRepository.save(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Message> findByConversationId(UUID conversationId, Pageable pageable) {
        return jpaRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    @Override
    public List<Message> findUnreadByRecipient(UUID recipientId) {
        return jpaRepository.findUnreadByRecipient(recipientId);
    }

    @Override
    public long countUnreadByRecipient(UUID recipientId) {
        return jpaRepository.countUnreadByRecipient(recipientId);
    }
}
