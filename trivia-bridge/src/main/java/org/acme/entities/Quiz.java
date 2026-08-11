package org.acme.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Quiz(
    UUID id,
    List<Question> questions,
    Instant expiresAt
) {}