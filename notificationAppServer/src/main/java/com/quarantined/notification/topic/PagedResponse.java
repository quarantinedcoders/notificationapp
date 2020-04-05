package com.quarantined.notification.topic;

import java.util.List;

import lombok.Data;

@Data
public class PagedResponse<T> {

	private final List<T> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;
	private final boolean last;
}
