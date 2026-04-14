package com.marketplace.message.infrastructure;

import com.marketplace.message.domain.Conversation;
import com.marketplace.message.domain.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {

    private final JpaConversationRepository jpaRepository;

    @Override
    public Conversation save(Conversation conversation) {
        return jpaRepository.save(conversation);
    }

    @Override
    public Optional<Conversation> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Conversation> findByParticipantIds(UUID participant1Id, UUID participant2Id) {
        return jpaRepository.findByParticipantIds(participant1Id, participant2Id);
    }

    @Override
    public List<Conversation> findByParticipantId(UUID participantId) {
        return jpaRepository.findByParticipantId(participantId);
    }
}
