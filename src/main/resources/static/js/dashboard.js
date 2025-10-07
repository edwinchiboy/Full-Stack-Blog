/**
 * Dashboard Module
 * Handles admin dashboard statistics and post management
 */

const DashboardAPI = {
    BASE_URL: window.location.origin + '/api/dashboard',

    /**
     * Fetch dashboard statistics
     */
    async getStats() {
        try {
            const response = await fetch(`${this.BASE_URL}/stats`, {
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) throw new Error('Failed to fetch dashboard stats');
            return await response.json();
        } catch (error) {
            console.error('Error fetching dashboard stats:', error);
            throw error;
        }
    },

    /**
     * Fetch post statistics
     */
    async getPostStats() {
        try {
            const response = await fetch(`${this.BASE_URL}/stats/posts`, {
                headers: Auth.getAuthHeader()
            });
            if (!response.ok) throw new Error('Failed to fetch post stats');
            return await response.json();
        } catch (error) {
            console.error('Error fetching post stats:', error);
            throw error;
        }
    }
};

/**
 * Load and display dashboard statistics
 */
async function loadDashboardStats() {
    try {
        const stats = await DashboardAPI.getStats();

        // Update stat cards
        updateStatCard('totalPosts', stats.totalPosts || 0);
        updateStatCard('publishedPosts', stats.publishedPosts || 0);
        updateStatCard('totalViews', formatNumber(stats.totalViews || 0));
        updateStatCard('totalComments', stats.totalComments || 0);
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
        if (typeof showNotification === 'function') {
            showNotification('Failed to load dashboard statistics', 'error');
        }
    }
}

/**
 * Update a stat card with new value
 */
function updateStatCard(type, value) {
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach(card => {
        const label = card.querySelector('.stat-card__label')?.textContent.toLowerCase();
        if (label?.includes('total posts') && type === 'totalPosts') {
            card.querySelector('.stat-card__number').textContent = value;
        } else if (label?.includes('published') && type === 'publishedPosts') {
            card.querySelector('.stat-card__number').textContent = value;
        } else if (label?.includes('views') && type === 'totalViews') {
            card.querySelector('.stat-card__number').textContent = value;
        } else if (label?.includes('comments') && type === 'totalComments') {
            card.querySelector('.stat-card__number').textContent = value;
        }
    });
}

/**
 * Load and display recent posts in dashboard table
 */
async function loadDashboardPosts() {
    try {
        const user = Auth.getUser();
        if (!user) return;

        const data = await PostsAPI.getPostsByAuthor(user.username, 0, 10);

        const tbody = document.querySelector('.table tbody');
        if (!tbody) return;

        if (!data.content || data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--color-text-secondary);">No posts yet. Create your first post!</td></tr>';
            return;
        }

        tbody.innerHTML = data.content.map(post => `
            <tr>
                <td><strong>${post.title}</strong></td>
                <td>${post.category?.name || 'Uncategorized'}</td>
                <td>${renderStatusBadge(post.status)}</td>
                <td>${post.viewCount || 0}</td>
                <td>${formatDate(post.createdAt)}</td>
                <td>
                    <div class="table__actions">
                        <button class="btn btn--ghost btn--small" onclick="editPost('${post.id}')">Edit</button>
                        <button class="btn btn--ghost btn--small" style="color: var(--color-error);" onclick="confirmDeletePost('${post.id}', '${post.title}')">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading dashboard posts:', error);
        if (typeof showNotification === 'function') {
            showNotification('Failed to load posts', 'error');
        }
    }
}

/**
 * Render status badge
 */
function renderStatusBadge(status) {
    const statusMap = {
        'PUBLISHED': 'success',
        'DRAFT': 'warning',
        'SCHEDULED': 'info'
    };
    const badgeClass = statusMap[status] || 'warning';
    return `<span class="badge badge--${badgeClass}">${status}</span>`;
}

/**
 * Format large numbers
 */
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    }
    if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
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
 * Navigate to edit post page
 */
function editPost(postId) {
    window.location.href = `create-post.html?id=${postId}`;
}

/**
 * Confirm and delete post
 */
function confirmDeletePost(postId, postTitle) {
    if (confirm(`Are you sure you want to delete "${postTitle}"? This action cannot be undone.`)) {
        deletePost(postId);
    }
}

/**
 * Delete a post
 */
async function deletePost(postId) {
    try {
        await PostsAPI.deletePost(postId);
        if (typeof showNotification === 'function') {
            showNotification('Post deleted successfully', 'success');
        }
        // Reload posts
        await loadDashboardPosts();
        // Reload stats
        await loadDashboardStats();
    } catch (error) {
        console.error('Error deleting post:', error);
        if (typeof showNotification === 'function') {
            showNotification(error.message || 'Failed to delete post', 'error');
        }
    }
}

// Initialize dashboard on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDashboard);
} else {
    initDashboard();
}

function initDashboard() {
    if (window.location.pathname.includes('dashboard.html')) {
        loadDashboardStats();
        loadDashboardPosts();
    }
}