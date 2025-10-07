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
            const response = await fetch(this.BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...Auth.getAuthHeader()
                },
                body: JSON.stringify(postData)
            });
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Failed to create post');
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