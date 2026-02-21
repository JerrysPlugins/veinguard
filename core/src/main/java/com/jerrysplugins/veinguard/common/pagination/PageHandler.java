/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.pagination;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PageHandler<T> {

    private final List<T> entries;
    private final int pageSize;

    public PageHandler(List<T> entries, int pageSize) {
        this.entries = entries;
        this.pageSize = Math.max(1, pageSize);
    }

    public int getTotalPages() {
        return Math.max(1, (int) Math.ceil(entries.size() / (double) pageSize));
    }

    public void sendPage(int pageNumber, Function<T, String> lineMapper,
                         String header, String footer,
                         BiConsumer<Integer, String> lineConsumer) {

        int totalPages = getTotalPages();
        int page = Math.min(Math.max(1, pageNumber), totalPages);

        if (header != null) lineConsumer.accept(page, header);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, entries.size());

        for (int i = startIndex; i < endIndex; i++) {
            lineConsumer.accept(page, lineMapper.apply(entries.get(i)));
        }

        if (footer != null) lineConsumer.accept(page, footer);
    }
}