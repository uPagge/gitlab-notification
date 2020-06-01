package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;

public interface ReportService {

    String generateReport(@NonNull String login);

}
