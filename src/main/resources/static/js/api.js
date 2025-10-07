
// API Service Layer for Crypto Blog
// Handles all HTTP requests to the backend API

const API_BASE_URL = 'http://localhost:8080/api';

// Token Management
const TokenManager = {
    getToken() {
        return localStorage.getItem('jwt_token');
    },

    setToken(token) {
        localStorage.setItem('jwt_token', token);
    },

    removeToken() {
        localStorage.removeItem('jwt_token');
    },

    getUserInfo() {
        const token = this.getToken();
        if (!token) return null;

        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return {
                username: payload.sub,
                roles: payload.roles,
                exp: payload.exp
            };
        } catch (e) {
            return null;
        }
    },

    isTokenExpired() {
        const userInfo = this.getUserInfo();
        if (!userInfo) return true;
        return Date.now() >= userInfo.exp * 1000;
    },

    isAdmin() {
        const userInfo = this.getUserInfo();
        return userInfo && userInfo.roles && userInfo.roles.includes('ROLE_ADMIN');
    }
};

// HTTP Client with automatic JWT injection
class ApiClient {
    async request(url, options = {}) {
        const token = TokenManager.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token && !TokenManager.isTokenExpired()) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(`${API_BASE_URL}${url}`, config);

            if (response.status === 401) {
                TokenManager.removeToken();
                window.location.href = '/login.html';
                throw new Error('Unauthorized');
            }

            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(data.message || 'Request failed');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    get(url) {
        return this.request(url, { method: 'GET' });
    }

    post(url, body) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }

    put(url, body) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    }

    patch(url, body) {
        return this.request(url, {
            method: 'PATCH',
            body: body ? JSON.stringify(body) : undefined
        });
    }

    delete(url) {
        return this.request(url, { method: 'DELETE' });
    }
}

const api = new ApiClient();

// Authentication API
const AuthAPI = {
    async login(username, password) {
        const response = await api.post('/auth/signin', { username, password });
        if (response.token) {
            TokenManager.setToken(response.token);
        }
        return response;
    },

    logout() {
        TokenManager.removeToken();
        window.location.href = '/login.html';
    },

    getCurrentUser() {
        return TokenManager.getUserInfo();
    },

    isAuthenticated() {
        return TokenManager.getToken() && !TokenManager.isTokenExpired();
    }
};

// Registration API
const RegistrationAPI = {
    async initiateRegistration(email, firstName, lastName) {
        return api.post('/v1/registration', { email, firstName, lastName });
    },

    async validateOtp(registrationId, otp) {
        return api.put('/v1/registration/validate-otp', { registrationId, otp });
    },

    async completeRegistration(registrationId, password) {
        return api.put('/v1/registration/complete-sign-up', { registrationId, password });
    },

    async resendOtp(registrationId) {
        return api.put(`/v1/registration/${registrationId}/resend-email-otp`);
    }
};

// Password Reset API
const PasswordResetAPI = {
    async initiateReset(email) {
        return api.post('/password-reset/initiate', { email });
    },

    async validateOtp(email, otp) {
        return api.post('/password-reset/validate-otp', { email, otp });
    },

    async resetPassword(email, otp, newPassword) {
        return api.post('/password-reset/reset', { email, otp, newPassword });
    },

    async resendOtp(email) {
        return api.post('/password-reset/resend-otp', { email });
    }
};

// Posts API
const PostsAPI = {
    async getAllPosts(page = 0, size = 10) {
        return api.get(`/posts?page=${page}&size=${size}`);
    },

    async getPostById(id) {
        return api.get(`/posts/${id}`);
    },

    async getPostBySlug(slug) {
        return api.get(`/posts/slug/${slug}`);
    },

    async getPostsByCategory(categoryId, page = 0, size = 10) {
        return api.get(`/posts/category/${categoryId}?page=${page}&size=${size}`);
    },

    async searchPosts(keyword, page = 0, size = 10) {
        return api.get(`/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    },

    async createPost(postData) {
        return api.post('/posts', postData);
    },

    async updatePost(id, postData) {
        return api.put(`/posts/${id}`, postData);
    },

    async deletePost(id) {
        return api.delete(`/posts/${id}`);
    },

    async publishPost(id) {
        return api.patch(`/posts/${id}/publish`);
    },

    async hidePost(id) {
        return api.patch(`/posts/${id}/hide`);
    },

    async draftPost(id) {
        return api.patch(`/posts/${id}/draft`);
    },

    async getPostsByStatus(status, page = 0, size = 10) {
        return api.get(`/posts/status/${status}?page=${page}&size=${size}`);
    }
};

// Categories API
const CategoriesAPI = {
    async getAllCategories() {
        return api.get('/categories');
    },

    async getCategoryById(id) {
        return api.get(`/categories/${id}`);
    },

    async createCategory(name, description) {
        return api.post('/categories', { name, description });
    },

    async updateCategory(id, name, description) {
        return api.put(`/categories/${id}`, { name, description });
    },

    async deleteCategory(id) {
        return api.delete(`/categories/${id}`);
    }
};

// Comments API
const CommentsAPI = {
    async getCommentsByPost(postId, page = 0, size = 10) {
        return api.get(`/comments/post/${postId}?page=${page}&size=${size}`);
    },

    async createComment(postId, content) {
        return api.post(`/comments/post/${postId}`, { content });
    },

    async deleteComment(commentId) {
        return api.delete(`/comments/${commentId}`);
    },

    async getCommentCount(postId) {
        return api.get(`/comments/post/${postId}/count`);
    }
};

// Subscribers API
const SubscribersAPI = {
    async subscribe(email) {
        return api.post('/subscribers/subscribe', { email });
    },

    async unsubscribe(email) {
        return api.post('/subscribers/unsubscribe', { email });
    },

    async getSubscriberCount() {
        return api.get('/subscribers/count');
    },

    async checkSubscription(email) {
        return api.get(`/subscribers/check/${encodeURIComponent(email)}`);
    },

    async getAllSubscribers() {
        return api.get('/subscribers');
    }
};

// Dashboard API
const DashboardAPI = {
    async getStats() {
        return api.get('/dashboard/stats');
    },

    async getPostStats() {
        return api.get('/dashboard/stats/posts');
    },

    async getSubscriberStats() {
        return api.get('/dashboard/stats/subscribers');
    },

    async getEngagementStats() {
        return api.get('/dashboard/stats/engagement');
    }
};

// Utility Functions
const Utils = {
    showLoading(element) {
        element.innerHTML = '<div class="spinner">Loading...</div>';
        element.disabled = true;
    },

    hideLoading(element, originalText) {
        element.innerHTML = originalText;
        element.disabled = false;
    },

    showError(message, containerId = 'error-message') {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `<div class="alert alert-error">${message}</div>`;
            container.style.display = 'block';
        } else {
            alert(message);
        }
    },

    showSuccess(message, containerId = 'success-message') {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `<div class="alert alert-success">${message}</div>`;
            container.style.display = 'block';
        } else {
            alert(message);
        }
    },

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    truncateText(text, maxLength = 150) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    },

    requireAuth() {
        if (!AuthAPI.isAuthenticated()) {
            window.location.href = '/login.html';
            return false;
        }
        return true;
    },

    requireAdmin() {
        if (!TokenManager.isAdmin()) {
            window.location.href = '/index.html';
            return false;
        }
        return true;
    }
};

// Export for use in HTML pages
window.API = {
    Auth: AuthAPI,
    Registration: RegistrationAPI,
    PasswordReset: PasswordResetAPI,
    Posts: PostsAPI,
    Categories: CategoriesAPI,
    Comments: CommentsAPI,
    Subscribers: SubscribersAPI,
    Dashboard: DashboardAPI,
    Utils: Utils,
    TokenManager: TokenManager
};