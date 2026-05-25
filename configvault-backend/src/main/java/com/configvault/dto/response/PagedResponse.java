package com.configvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper for list-based API endpoints.
 *
 * <p>This DTO encapsulates a page of results along with pagination metadata,
 * enabling clients to navigate through large datasets efficiently. It mirrors
 * the structure of Spring Data's {@link org.springframework.data.domain.Page}
 * in a transport-friendly format.</p>
 *
 * @param <T> the type of elements contained in the page
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /** The list of items on the current page. */
    private List<T> content;

    /** The zero-indexed page number. */
    private int pageNumber;

    /** The number of items per page. */
    private int pageSize;

    /** The total number of items across all pages. */
    private long totalElements;

    /** The total number of pages available. */
    private int totalPages;

    /** Whether this is the last page of results. */
    private boolean last;
}
