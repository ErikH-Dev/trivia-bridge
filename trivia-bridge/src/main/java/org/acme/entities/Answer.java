package org.acme.entities;

import java.util.UUID;

public record Answer(
    UUID id,
    String option
) {}