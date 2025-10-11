/**
 * Posts Management Module
 * Handles fetching, displaying, and managing blog posts
 */

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
     * @param {string} status - PUBLISHED, DRAFT, or SCHEDULED
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
            const scheduledPromise = this.getPostsByStatus('SCHEDULED', 0, largeSize)
                .catch(err => {
                    console.error('Error fetching scheduled posts:', err);
                    return { content: [], totalElements: 0 };
                });

            const [published, draft, scheduled] = await Promise.all([publishedPromise, draftPromise, scheduledPromise]);

            console.log('Published posts:', published.content?.length || 0);
            console.log('Draft posts:', draft.content?.length || 0);
            console.log('Scheduled posts:', scheduled.content?.length || 0);

            // Combine all posts and sort by createdAt (newest first)
            const allPosts = [
                ...(published.content || []),
                ...(draft.content || []),
                ...(scheduled.content || [])
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

/**
 * Render posts on the homepage
 */
async function loadHomepagePosts() {
    try {
        const postsContainer = document.querySelector('.grid--posts');
        if (!postsContainer) return;

        // Show loading state
        postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">Loading posts...</p>';

        const data = await PostsAPI.getAllPosts(0, 6);

        if (!data.content || data.content.length === 0) {
            postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-text-secondary);">No posts available yet.</p>';
            return;
        }

        postsContainer.innerHTML = data.content.map(post => `
            <article class="card post-card">
                <span class="post-card__category">${post.category?.name || 'Uncategorized'}</span>
                <h3 class="post-card__title">
                    <a href="post.html?slug=${post.slug}">${post.title}</a>
                </h3>
                <p class="post-card__excerpt">
                    ${post.excerpt || post.content.substring(0, 150) + '...'}
                </p>
                <div class="post-card__meta">
                    <div class="post-card__author">
                        <span>By ${post.author?.username || 'Anonymous'}</span>
                    </div>
                    <span class="post-card__date">${formatDate(post.publishedAt || post.createdAt)}</span>
                </div>
            </article>
        `).join('');
    } catch (error) {
        console.error('Error loading homepage posts:', error);
        const postsContainer = document.querySelector('.grid--posts');
        if (postsContainer) {
            postsContainer.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--color-error);">Failed to load posts. Please try again later.</p>';
        }
    }
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
 * Handle search functionality
 */
async function handleSearch(event) {
    event.preventDefault();
    const searchInput = document.querySelector('.search-bar__input');
    const keyword = searchInput?.value.trim();

    if (!keyword) return;

    try {
        const data = await PostsAPI.searchPosts(keyword);
        // Redirect to search results page or update current page
        window.location.href = `search.html?q=${encodeURIComponent(keyword)}`;
    } catch (error) {
        console.error('Search error:', error);
        if (typeof showNotification === 'function') {
            showNotification('Search failed. Please try again.', 'error');
        }
    }
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPosts);
} else {
    initPosts();
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