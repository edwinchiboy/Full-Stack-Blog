/**
 * Posts Management Module
 * Handles fetching, displaying, and managing blog posts
 */

// Helper function to show notification
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification--${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === 'success' ? 'var(--color-success, #10b981)' : type === 'error' ? 'var(--color-error, #ef4444)' : 'var(--color-primary, #3b82f6)'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        z-index: 10000;
        max-width: 400px;
        animation: slideIn 0.3s ease-out;
    `;

    document.body.appendChild(notification);

    // Auto-remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add CSS animations if not already present
if (!document.querySelector('#notification-styles')) {
    const style = document.createElement('style');
    style.id = 'notification-styles';
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        @keyframes slideOut {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(400px);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

// Helper function to get category display name
function getCategoryDisplayName(category) {
    const categoryMap = {
        DEFI: 'DeFi',
        NFTS: 'NFTs',
        BLOCKCHAIN: 'Blockchain',
        TRADING: 'Trading',
        SECURITY: 'Security',
        WEB3: 'Web3'
    };
    return categoryMap[category] || category;
}

const PostsAPI = {
    BASE_URL: window.location.origin + '/api/posts',

    /**
     * Fetch all published posts with pagination
     */
    async getAllPosts(page = 0, size = 10) {
        try {
            const response = await fetch(`${this.BASE_URL}?page=${page}&size=${size}`);
            if (!response.ok) throw new Error('Failed to fetch posts');
            return await response.json();
        } catch (error) {
            console.error('Error fetching posts:', error);
            throw error;
        }
    },

    /**
     * Fetch a single post by ID
     */
    async getPostById(id) {
        try {
            const response = await fetch(`${this.BASE_URL}/${id}`);
            if (!response.ok) throw new Error('Post not found');
            return await response.json();
        } catch (error) {
            console.error('Error fetching post:', error);
            throw error;
        }
    },

    /**
     * Fetch a single post by slug
     */
    async getPostBySlug(slug) {
        try {
            const response = await fetch(`${this.BASE_URL}/slug/${slug}`);
            if (!response.ok) throw new Error('Post not found');
            return await response.json();
        } catch (error) {
            console.error('Error fetching post:', error);
            throw error;
        }
    },

    /**
     * Search posts by keyword
     */
    async searchPosts(keyword, page = 0, size = 10) {
        try {
            const response = await fetch(`${this.BASE_URL}/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
            if (!response.ok) throw new Error('Search failed');
            return await response.json();
        } catch (error) {
            console.error('Error searching posts:', error);
            throw error;
        }
    },

    /**
     * Create a new post (Admin only)
     */
    async createPost(postData) {
        try {
            console.log('=== API Request Debug ===');
            console.log('URL:', this.BASE_URL);
            console.log('Headers:', {
                'Content-Type': 'application/json',
                ...Auth.getAuthHeader()
            });
            console.log('Request body:', postData);

            const response = await fetch(this.BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...Auth.getAuthHeader()
                },
                body: JSON.stringify(postData)
            });

            console.log('Response status:', response.status);
            console.log('Response ok:', response.ok);

            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Authentication failed. Please login again.');
                } else if (response.status === 403) {
                    throw new Error('You do not have permission to create posts. Admin access required.');
                }
                const error = await response.json().catch(() => ({}));
                throw new Error(error.message || `Failed to create post (Status: ${response.status})`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error creating post:', error);
            throw error;
        }
    },

    /**
     * Update an existing post (Admin only)
     */
    async updatePost(id, postData) {
        try {
            const response = await fetch(`${this.BASE_URL}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    ...Auth.getAuthHeader()
                },
                body: JSON.stringify(postData)
            });
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Failed to update post');
            }
            return await response.json();
        } catch (error) {
            console.error('Error updating post:', error);
            throw error;
        }
    },

    /**
     * Delete a post (Admin only)
     */
    async deletePost(id) {
        try {
            const response = await fetch(`${this.BASE_URL}/${id}`, {
                method: 'DELETE',
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Failed to delete post');
            }
            return await response.json();
        } catch (error) {
            console.error('Error deleting post:', error);
            throw error;
        }
    },

    /**
     * Publish a draft post (Admin only)
     */
    async publishPost(id) {
        try {
            const response = await fetch(`${this.BASE_URL}/${id}/publish`, {
                method: 'PATCH',
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) throw new Error('Failed to publish post');
            return await response.json();
        } catch (error) {
            console.error('Error publishing post:', error);
            throw error;
        }
    },

    /**
     * Get posts by author (Admin only)
     */
    async getPostsByAuthor(username, page = 0, size = 10) {
        try {
            const response = await fetch(`${this.BASE_URL}/author/${username}?page=${page}&size=${size}`, {
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) throw new Error('Failed to fetch author posts');
            return await response.json();
        } catch (error) {
            console.error('Error fetching author posts:', error);
            throw error;
        }
    },

    /**
     * Get all posts by status (Admin only)
     * @param {string} status - PUBLISHED, DRAFT, or ARCHIVED
     */
    async getPostsByStatus(status, page = 0, size = 10) {
        try {
            const response = await fetch(`${this.BASE_URL}/status/${status}?page=${page}&size=${size}`, {
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) throw new Error('Failed to fetch posts by status');
            return await response.json();
        } catch (error) {
            console.error('Error fetching posts by status:', error);
            throw error;
        }
    },

    /**
     * Get all admin posts regardless of status (Admin only)
     */
    async getAllAdminPosts(page = 0, size = 10) {
        try {
            console.log('Fetching all admin posts...');

            // Fetch all statuses with a large page size to get all posts
            const largeSize = 1000; // Get a large number to fetch all posts

            const publishedPromise = this.getPostsByStatus('PUBLISHED', 0, largeSize)
                .catch(err => {
                    console.error('Error fetching published posts:', err);
                    return { content: [], totalElements: 0 };
                });
            const draftPromise = this.getPostsByStatus('DRAFT', 0, largeSize)
                .catch(err => {
                    console.error('Error fetching draft posts:', err);
                    return { content: [], totalElements: 0 };
                });

            const [published, draft] = await Promise.all([publishedPromise, draftPromise]);

            console.log('Published posts:', published.content?.length || 0);
            console.log('Draft posts:', draft.content?.length || 0);

            // Combine all posts and sort by createdAt (newest first)
            const allPosts = [
                ...(published.content || []),
                ...(draft.content || [])
            ];
            allPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

            console.log('Total combined posts:', allPosts.length);

            // Calculate pagination
            const totalElements = allPosts.length;
            const totalPages = Math.ceil(totalElements / size) || 1;
            const startIndex = page * size;
            const endIndex = startIndex + size;
            const paginatedContent = allPosts.slice(startIndex, endIndex);

            console.log('Paginated content:', paginatedContent.length);

            // Return in the same format as other endpoints
            return {
                content: paginatedContent,
                totalElements: totalElements,
                totalPages: totalPages,
                number: page,
                size: size,
                first: page === 0,
                last: page === totalPages - 1 || totalPages === 0
            };
        } catch (error) {
            console.error('Error fetching all admin posts:', error);
            throw error;
        }
    }
};

// Current page state
let currentPage = 0;
const pageSize = 6;
let currentCategory = null; // Track current category filter
let allPosts = []; // Cache all posts for client-side filtering

/**
 * Render posts on the homepage
 */
async function loadHomepagePosts(page = 0) {
    try {
        const postsContainer = document.querySelector('.grid--posts');
        if (!postsContainer) return;

        // Show loading state
        postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">Loading posts...</p>';

        const data = await PostsAPI.getAllPosts(page, pageSize);

        if (!data.content || data.content.length === 0) {
            postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">No posts available yet.</p>';
            return;
        }

        postsContainer.innerHTML = data.content.map(post => `
            <article class="card post-card">
                <div class="post-card__content">
                    <div class="post-card__header">
                        <span class="post-card__category">${getCategoryDisplayName(post.category?.category || post.category)}</span>
                        <span class="post-card__title"><a href="/post?slug=${post.slug}">${post.title}</a></span>
                    </div>
                    <p class="post-card__excerpt">
                        ${post.excerpt || post.content.substring(0, 150) + '...'}
                    </p>
                    <div class="post-card__meta">
                        <div class="post-card__author">
                            <span>By ${post.author?.username || 'Anonymous'}</span>
                        </div>
                        <span class="post-card__date">${formatDate(post.publishedAt || post.createdAt)}</span>
                    </div>
                </div>
            </article>
        `).join('');

        // Update pagination
        currentPage = page;
        renderPagination(data);
    } catch (error) {
        console.error('Error loading homepage posts:', error);
        const postsContainer = document.querySelector('.grid--posts');
        if (postsContainer) {
            postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-error);">Failed to load posts. Please try again later.</p>';
        }
    }
}

/**
 * Render pagination controls
 */
function renderPagination(data) {
    const paginationContainer = document.querySelector('.pagination');
    if (!paginationContainer) return;

    const { totalPages, number, first, last } = data;

    if (totalPages <= 1) {
        paginationContainer.innerHTML = '';
        return;
    }

    let paginationHTML = '';

    // Previous button
    paginationHTML += `
        <button class="pagination__button"
                ${first ? 'disabled' : ''}
                onclick="loadHomepagePosts(${number - 1})"
                aria-label="Previous page">← Prev</button>
    `;

    // Page numbers
    const maxVisiblePages = 5;
    let startPage = Math.max(0, number - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    // Adjust start if we're near the end
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <button class="pagination__button ${i === number ? 'pagination__button--active' : ''}"
                    onclick="loadHomepagePosts(${i})"
                    aria-label="Page ${i + 1}"
                    ${i === number ? 'aria-current="page"' : ''}>${i + 1}</button>
        `;
    }

    // Next button
    paginationHTML += `
        <button class="pagination__button"
                ${last ? 'disabled' : ''}
                onclick="loadHomepagePosts(${number + 1})"
                aria-label="Next page">Next →</button>
    `;

    paginationContainer.innerHTML = paginationHTML;
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Display search results on the current page
 */
async function displaySearchResults(keyword, page = 0) {
    const postsContainer = document.querySelector('.grid--posts');
    const headerElement = document.querySelector('.mb-2xl h2');

    if (!postsContainer) return;

    // Show loading state
    postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">Searching...</p>';

    try {
        const data = await PostsAPI.searchPosts(keyword, page, pageSize);

        // Update header to show search results
        if (headerElement) {
            headerElement.innerHTML = `Search Results for "${keyword}" <button onclick="clearSearch()" class="btn btn--ghost btn--small" style="margin-left: var(--space-md);">Clear Search</button>`;
        }

        if (!data.content || data.content.length === 0) {
            postsContainer.innerHTML = `
                <p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">
                    No posts found for "${keyword}".
                </p>`;
            return;
        }

        postsContainer.innerHTML = data.content.map(post => `
            <article class="card post-card">
                <div class="post-card__content">
                    <div class="post-card__header">
                        <span class="post-card__category">${getCategoryDisplayName(post.category?.category || post.category)}</span>
                        <span class="post-card__title"><a href="/post?slug=${post.slug}">${post.title}</a></span>
                    </div>
                    <p class="post-card__excerpt">
                        ${post.excerpt || post.content.substring(0, 150) + '...'}
                    </p>
                    <div class="post-card__meta">
                        <div class="post-card__author">
                            <span>By ${post.author?.username || 'Anonymous'}</span>
                        </div>
                        <span class="post-card__date">${formatDate(post.publishedAt || post.createdAt)}</span>
                    </div>
                </div>
            </article>
        `).join('');

        // Update pagination for search results
        currentPage = page;
        renderPagination(data);
    } catch (error) {
        console.error('Error displaying search results:', error);
        postsContainer.innerHTML = `
            <p style="grid-column: 1/-1; text-align: center; color: var(--color-error);">
                Failed to search posts. Please try again.
            </p>`;
    }
}

/**
 * Clear search and show all posts
 */
function clearSearch() {
    const searchInput = document.querySelector('.search-bar__input');
    const headerElement = document.querySelector('.mb-2xl h2');

    if (searchInput) {
        searchInput.value = '';
    }

    if (headerElement) {
        headerElement.textContent = 'Latest Articles';
    }

    loadHomepagePosts(0);
}

/**
 * Handle search functionality
 */
async function handleSearch(event) {
    event.preventDefault();
    const searchInput = document.querySelector('.search-bar__input');
    const keyword = searchInput?.value.trim();

    if (!keyword) {
        clearSearch();
        return;
    }

    displaySearchResults(keyword, 0);
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPosts);
} else {
    initPosts();
}

/**
 * Filter posts by category
 */
async function filterByCategory(category) {
    const postsContainer = document.querySelector('.grid--posts');
    const headerElement = document.querySelector('.mb-2xl h2');

    if (!postsContainer) return;

    // Set current category
    currentCategory = category;

    // Update active state on category buttons
    document.querySelectorAll('.tag[data-category]').forEach(btn => {
        if (btn.dataset.category === category) {
            btn.classList.add('tag--active');
        } else {
            btn.classList.remove('tag--active');
        }
    });

    // Show loading state
    postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">Filtering posts...</p>';

    try {
        // Fetch all posts (with large page size to get everything)
        const data = await PostsAPI.getAllPosts(0, 100);

        if (!data.content || data.content.length === 0) {
            postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">No posts available yet.</p>';
            return;
        }

        // Filter posts by category
        const filteredPosts = data.content.filter(post => {
            const postCategory = post.category?.category || post.category;
            return postCategory === category;
        });

        // Update header
        if (headerElement) {
            headerElement.innerHTML = `${getCategoryDisplayName(category)} Articles <button onclick="clearCategoryFilter()" class="btn btn--ghost btn--small" style="margin-left: var(--space-md);">Show All</button>`;
        }

        if (filteredPosts.length === 0) {
            postsContainer.innerHTML = `
                <p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">
                    No posts found in the ${getCategoryDisplayName(category)} category.
                </p>`;
            // Clear pagination
            const paginationContainer = document.querySelector('.pagination');
            if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }
            return;
        }

        // Display filtered posts
        postsContainer.innerHTML = filteredPosts.map(post => `
            <article class="card post-card">
                <div class="post-card__content">
                    <div class="post-card__header">
                        <span class="post-card__category">${getCategoryDisplayName(post.category?.category || post.category)}</span>
                        <span class="post-card__title"><a href="/post?slug=${post.slug}">${post.title}</a></span>
                    </div>
                    <p class="post-card__excerpt">
                        ${post.excerpt || post.content.substring(0, 150) + '...'}
                    </p>
                    <div class="post-card__meta">
                        <div class="post-card__author">
                            <span>By ${post.author?.username || 'Anonymous'}</span>
                        </div>
                        <span class="post-card__date">${formatDate(post.publishedAt || post.createdAt)}</span>
                    </div>
                </div>
            </article>
        `).join('');

        // Clear pagination for filtered results
        const paginationContainer = document.querySelector('.pagination');
        if (paginationContainer) {
            paginationContainer.innerHTML = '';
        }

        // Show notification
        showNotification(`Showing ${filteredPosts.length} ${getCategoryDisplayName(category)} posts`, 'success');

        // Scroll to posts section
        document.querySelector('#posts').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error filtering posts by category:', error);
        postsContainer.innerHTML = `
            <p style="grid-column: 1/-1; text-align: center; color: var(--color-error);">
                Failed to filter posts. Please try again.
                <button onclick="clearCategoryFilter()" class="btn btn--primary btn--small" style="margin-top: var(--space-md);">Show All Posts</button>
            </p>`;
        showNotification('Failed to filter posts', 'error');
    }
}

/**
 * Clear category filter and show all posts
 */
function clearCategoryFilter() {
    currentCategory = null;
    const headerElement = document.querySelector('.mb-2xl h2');
    const searchInput = document.querySelector('.search-bar__input');

    // Remove active state from all category buttons
    document.querySelectorAll('.tag[data-category]').forEach(btn => {
        btn.classList.remove('tag--active');
    });

    if (headerElement) {
        headerElement.textContent = 'Latest Articles';
    }

    if (searchInput) {
        searchInput.value = '';
    }

    loadHomepagePosts(0);
}

function initPosts() {
    // Load posts on homepage
    if (window.location.pathname.includes('index.html') || window.location.pathname === '/') {
        loadHomepagePosts();
    }

    // Attach search handler
    const searchForm = document.querySelector('.search-bar');
    if (searchForm) {
        searchForm.addEventListener('submit', handleSearch);
    }
}