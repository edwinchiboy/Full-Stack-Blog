// Comments API
const CommentsAPI = {
    async getComments(postId, page = 0, size = 10) {
        const response = await fetch(`/api/posts/${postId}/comments?page=${page}&size=${size}`);
        if (!response.ok) {
            throw new Error('Failed to fetch comments');
        }
        const data = await response.json();
        return data.data;
    },

    async createComment(postId, content) {
        const token = localStorage.getItem('jwt_token');
        if (!token) {
            throw new Error('You must be logged in to comment');
        }

        const response = await fetch(`/api/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ content })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create comment');
        }

        return await response.json();
    },

    async deleteComment(commentId) {
        const token = localStorage.getItem('jwt_token');
        if (!token) {
            throw new Error('You must be logged in to delete comments');
        }

        const response = await fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete comment');
        }

        return await response.json();
    }
};

// Comment state
let currentCommentPage = 0;
const commentsPerPage = 10;

// Format date for comments
function formatCommentDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);

    if (diffInSeconds < 60) {
        return 'Just now';
    } else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60);
        return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    } else if (diffInSeconds < 86400) {
        const hours = Math.floor(diffInSeconds / 3600);
        return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    } else if (diffInSeconds < 604800) {
        const days = Math.floor(diffInSeconds / 86400);
        return `${days} day${days > 1 ? 's' : ''} ago`;
    } else {
        return date.toLocaleDateString('en-US', { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric' 
        });
    }
}

// Load comments for a post
async function loadComments(postId, page = 0) {
    const commentsList = document.getElementById('comments-list');
    const loadingIndicator = document.getElementById('comments-loading');
    const noCommentsMessage = document.getElementById('no-comments-message');
    const commentCountElement = document.getElementById('comment-count');
    
    if (!commentsList) return;
    
    // Show loading
    loadingIndicator.style.display = 'block';
    commentsList.style.display = 'none';
    noCommentsMessage.style.display = 'none';
    
    try {
        const data = await CommentsAPI.getComments(postId, page, commentsPerPage);
        
        loadingIndicator.style.display = 'none';
        
        // Update comment count
        if (commentCountElement) {
            const count = data.totalElements || 0;
            commentCountElement.textContent = `Comments (${count})`;
        }
        
        if (!data.content || data.content.length === 0) {
            noCommentsMessage.style.display = 'block';
            commentsList.innerHTML = '';
            document.getElementById('comments-pagination').style.display = 'none';
            return;
        }
        
        // Get current user ID for delete button
        const currentUserId = getUserIdFromToken();
        
        // Display comments
        commentsList.style.display = 'block';
        commentsList.innerHTML = data.content.map(comment => `
            <div class="comment" data-comment-id="${comment.id}">
                <div class="comment__header">
                    <div class="comment__author">
                        <strong>${comment.authorName || 'Anonymous'}</strong>
                        <span class="comment__date">${formatCommentDate(comment.createdAt)}</span>
                    </div>
                    ${currentUserId === comment.authorId ? `
                        <button class="btn btn--ghost btn--small delete-comment-btn" data-comment-id="${comment.id}">
                            Delete
                        </button>
                    ` : ''}
                </div>
                <p class="comment__content">${escapeHtml(comment.content)}</p>
            </div>
        `).join('');
        
        // Add delete event listeners
        document.querySelectorAll('.delete-comment-btn').forEach(btn => {
            btn.addEventListener('click', handleDeleteComment);
        });
        
        // Render pagination
        renderCommentPagination(data);
    } catch (error) {
        console.error('Error loading comments:', error);
        loadingIndicator.style.display = 'none';
        commentsList.innerHTML = `
            <p style="text-align: center; color: var(--color-error);">
                Failed to load comments. Please try again.
            </p>
        `;
    }
}

// Render comment pagination
function renderCommentPagination(data) {
    const paginationContainer = document.getElementById('comments-pagination');
    if (!paginationContainer) return;

    if (data.totalPages <= 1) {
        paginationContainer.style.display = 'none';
        return;
    }

    paginationContainer.style.display = 'flex';

    let paginationHTML = '';

    // Previous button
    if (data.number > 0) {
        paginationHTML += `
            <button class="btn btn--ghost btn--small" onclick="loadComments('${window.currentPostId}', ${data.number - 1})">
                Previous
            </button>
        `;
    }

    // Page info
    paginationHTML += `
        <span style="margin: 0 1rem; color: var(--color-text-secondary);">
            Page ${data.number + 1} of ${data.totalPages}
        </span>
    `;

    // Next button
    if (data.number < data.totalPages - 1) {
        paginationHTML += `
            <button class="btn btn--ghost btn--small" onclick="loadComments('${window.currentPostId}', ${data.number + 1})">
                Next
            </button>
        `;
    }

    paginationContainer.innerHTML = paginationHTML;
}

// Handle comment form submission
async function handleCommentSubmit(event) {
    console.log('[Comments] handleCommentSubmit called');
    event.preventDefault();
    console.log('[Comments] Default form action prevented');

    const form = event.target;
    const textarea = form.querySelector('#comment');
    const submitButton = form.querySelector('button[type="submit"]');
    const content = textarea.value.trim();

    console.log('[Comments] Comment content:', content);
    console.log('[Comments] Current post ID:', window.currentPostId);

    if (!content) {
        alert('Please enter a comment');
        return;
    }

    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert('You must be logged in to comment');
        window.location.href = '/login';
        return;
    }

    // Disable form while submitting
    submitButton.disabled = true;
    submitButton.textContent = 'Posting...';

    try {
        console.log('[Comments] Sending comment to backend...');
        await CommentsAPI.createComment(window.currentPostId, content);
        console.log('[Comments] Comment posted successfully');

        // Clear form
        textarea.value = '';

        // Reload comments
        console.log('[Comments] Reloading comments...');
        await loadComments(window.currentPostId, 0);

        // Show success notification
        showCommentNotification('Comment posted successfully!', 'success');
    } catch (error) {
        console.error('Error posting comment:', error);
        alert(error.message || 'Failed to post comment. Please try again.');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Post Comment';
    }
}

// Handle comment deletion
async function handleDeleteComment(event) {
    const commentId = event.target.dataset.commentId;

    if (!confirm('Are you sure you want to delete this comment?')) {
        return;
    }

    try {
        await CommentsAPI.deleteComment(commentId);

        // Reload comments
        await loadComments(window.currentPostId, currentCommentPage);

        showCommentNotification('Comment deleted successfully!', 'success');
    } catch (error) {
        console.error('Error deleting comment:', error);
        alert(error.message || 'Failed to delete comment. Please try again.');
    }
}

// Show notification
function showCommentNotification(message, type = 'info') {
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
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Get user ID from token
function getUserIdFromToken() {
    const token = localStorage.getItem('jwt_token');
    if (!token) return null;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub;
    } catch (e) {
        return null;
    }
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize comments on page load
document.addEventListener('DOMContentLoaded', () => {
    console.log('[Comments] DOMContentLoaded fired');
    const commentForm = document.getElementById('comment-form');
    console.log('[Comments] Comment form found:', commentForm);

    if (commentForm) {
        console.log('[Comments] Waiting for window.currentPostId...');
        // Wait for post.js to load the post and set currentPostId
        const waitForPostId = setInterval(() => {
            console.log('[Comments] Polling for currentPostId, current value:', window.currentPostId);
            if (window.currentPostId) {
                console.log('[Comments] currentPostId detected:', window.currentPostId);
                clearInterval(waitForPostId);

                // Load comments
                console.log('[Comments] Loading comments for post:', window.currentPostId);
                loadComments(window.currentPostId);

                // Add form submit listener
                console.log('[Comments] Attaching submit event listener to form');
                commentForm.addEventListener('submit', handleCommentSubmit);
                console.log('[Comments] Submit event listener attached successfully');
            }
        }, 100);

        // Timeout after 5 seconds
        setTimeout(() => {
            console.log('[Comments] Timeout reached, clearing interval');
            clearInterval(waitForPostId);
        }, 5000);
    } else {
        console.log('[Comments] Comment form not found!');
    }
});
