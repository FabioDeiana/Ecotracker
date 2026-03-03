package com.ecotracker.ecotrackerbackend.payloads;

import java.time.LocalDateTime;

public record ErrorsDTO(String message, LocalDateTime timeStamp) {}
