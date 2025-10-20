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
        console.log('Attempting to load dashboard stats...');
        const stats = await DashboardAPI.getStats();
        console.log('Dashboard stats loaded:', stats);

        // Update stat cards
        updateStatCard('totalPosts', stats.totalPosts || 0);
        updateStatCard('publishedPosts', stats.publishedPosts || 0);
        updateStatCard('totalViews', formatNumber(stats.totalViews || 0));
        updateStatCard('totalComments', stats.totalComments || 0);
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
        console.error('Error details:', error.message, error.stack);
        // Don't show notification, stats will just stay at 0
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

// Pagination state for dashboard
let dashboardCurrentPage = 0;
const postsPerPage = 10;

/**
 * Load and display recent posts in dashboard table
 */
async function loadDashboardPosts(page = 0) {
    try {
        console.log('Attempting to load dashboard posts, page:', page);
        // Use the PostsAPI.getAllAdminPosts method
        const data = await PostsAPI.getAllAdminPosts(page, postsPerPage);
        console.log('Dashboard posts loaded:', data);

        const tbody = document.querySelector('.table tbody');
        if (!tbody) {
            console.error('Table tbody not found');
            return;
        }

        if (!data.content || data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--color-text-secondary);">No posts yet. Create your first post!</td></tr>';
            renderPagination(0, 0);
            return;
        }

        // Category display name mapping
        const categoryMap = {
            DEFI: 'DeFi',
            NFTS: 'NFTs',
            BLOCKCHAIN: 'Blockchain',
            TRADING: 'Trading',
            SECURITY: 'Security',
            WEB3: 'Web3'
        };

        tbody.innerHTML = data.content.map(post => {
            const categoryValue = post.category?.category || post.category;
            const categoryDisplay = categoryMap[categoryValue] || categoryValue;

            return `
            <tr>
                <td><strong>${escapeHtml(post.title)}</strong></td>
                <td>${escapeHtml(categoryDisplay)}</td>
                <td>${renderStatusBadge(post.status)}</td>
                <td>${post.viewCount || 0}</td>
                <td>${formatDate(post.createdAt)}</td>
                <td>
                    <div class="table__actions">
                        <button class="btn btn--ghost btn--small" onclick="editPost('${post.id}')">Edit</button>
                        <button class="btn btn--ghost btn--small" style="color: var(--color-error);" onclick="confirmDeletePost('${post.id}', \`${escapeHtml(post.title)}\`)">Delete</button>
                    </div>
                </td>
            </tr>
        `;
        }).join('');

        // Update current page and render pagination
        dashboardCurrentPage = page;
        renderPagination(data.number, data.totalPages);
    } catch (error) {
        console.error('Error loading dashboard posts:', error);
        console.error('Error details:', error.message, error.stack);
        const tbody = document.querySelector('.table tbody');
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--color-error);">Failed to load posts. Please try again.</td></tr>';
        }
    }
}

/**
 * Render pagination controls
 */
function renderPagination(currentPage, totalPages) {
    const paginationContainer = document.getElementById('pagination-container');
    if (!paginationContainer) return;

    if (totalPages <= 1) {
        paginationContainer.innerHTML = '';
        return;
    }

    let paginationHTML = '<div class="pagination__controls">';

    // Previous button
    paginationHTML += `
        <button
            class="pagination__btn ${currentPage === 0 ? 'pagination__btn--disabled' : ''}"
            onclick="changePage(${currentPage - 1})"
            ${currentPage === 0 ? 'disabled' : ''}
        >
            Previous
        </button>
    `;

    // Page numbers
    paginationHTML += '<div class="pagination__pages">';

    // Always show first page
    if (currentPage > 2) {
        paginationHTML += `<button class="pagination__page" onclick="changePage(0)">1</button>`;
        if (currentPage > 3) {
            paginationHTML += '<span class="pagination__ellipsis">...</span>';
        }
    }

    // Show pages around current page
    for (let i = Math.max(0, currentPage - 2); i <= Math.min(totalPages - 1, currentPage + 2); i++) {
        paginationHTML += `
            <button
                class="pagination__page ${i === currentPage ? 'pagination__page--active' : ''}"
                onclick="changePage(${i})"
            >
                ${i + 1}
            </button>
        `;
    }

    // Always show last page
    if (currentPage < totalPages - 3) {
        if (currentPage < totalPages - 4) {
            paginationHTML += '<span class="pagination__ellipsis">...</span>';
        }
        paginationHTML += `<button class="pagination__page" onclick="changePage(${totalPages - 1})">${totalPages}</button>`;
    }

    paginationHTML += '</div>';

    // Next button
    paginationHTML += `
        <button
            class="pagination__btn ${currentPage === totalPages - 1 ? 'pagination__btn--disabled' : ''}"
            onclick="changePage(${currentPage + 1})"
            ${currentPage === totalPages - 1 ? 'disabled' : ''}
        >
            Next
        </button>
    `;

    paginationHTML += '</div>';
    paginationContainer.innerHTML = paginationHTML;
}

/**
 * Change to a different page
 */
function changePage(page) {
    if (page < 0) return;
    loadDashboardPosts(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

/**
 * Render status badge
 */
function renderStatusBadge(status) {
    const statusMap = {
        'PUBLISHED': 'success',
        'DRAFT': 'warning',
        'ARCHIVED': 'secondary'
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
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Navigate to edit post page
 */
function editPost(postId) {
    window.location.href = `/create-post?id=${postId}`;
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
    console.log('initDashboard called');
    console.log('Current pathname:', window.location.pathname);
    console.log('Current location:', window.location.href);

    // Check if we're on dashboard page
    const isDashboardPage = document.querySelector('.dashboard-header') !== null;
    console.log('Is dashboard page (has .dashboard-header):', isDashboardPage);

    if (!isDashboardPage) {
        console.log('Not on dashboard page, skipping');
        return;
    }

    // Always load on dashboard page
    console.log('Loading dashboard data...');
    console.log('PostsAPI available:', typeof PostsAPI !== 'undefined');
    console.log('Auth available:', typeof Auth !== 'undefined');

    try {
        loadDashboardStats();
    } catch (error) {
        console.error('Error calling loadDashboardStats:', error);
    }

    try {
        loadDashboardPosts();
    } catch (error) {
        console.error('Error calling loadDashboardPosts:', error);
    }
}