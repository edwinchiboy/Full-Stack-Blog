/**
 * Authentication Module
 * Handles login, registration, JWT token management, and session handling
 */

const AUTH_CONFIG = {
    API_BASE_URL: window.location.origin,
    LOGIN_ENDPOINT: '/api/auth/signin',
    REGISTER_ENDPOINT: '/v1/registration',
    VALIDATE_OTP_ENDPOINT: '/v1/registration/validate-otp',
    COMPLETE_SIGNUP_ENDPOINT: '/v1/registration/complete-sign-up',
    TOKEN_KEY: 'jwt_token',
    USER_KEY: 'user_data'
};

/**
 * Auth utility functions
 */
const Auth = {
    /**
     * Store JWT token and user data in localStorage
     */
    saveAuth(token, userData) {
        localStorage.setItem(AUTH_CONFIG.TOKEN_KEY, token);
        localStorage.setItem(AUTH_CONFIG.USER_KEY, JSON.stringify(userData));
    },

    /**
     * Get JWT token from localStorage
     */
    getToken() {
        return localStorage.getItem(AUTH_CONFIG.TOKEN_KEY);
    },

    /**
     * Get user data from localStorage
     */
    getUser() {
        const userData = localStorage.getItem(AUTH_CONFIG.USER_KEY);
        return userData ? JSON.parse(userData) : null;
    },

    /**
     * Check if user is authenticated
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Check if user has admin role
     */
    isAdmin() {
        const user = this.getUser();
        return user && user.roles && user.roles.includes('ROLE_ADMIN');
    },

    /**
     * Clear authentication data (logout)
     */
    clearAuth() {
        localStorage.removeItem(AUTH_CONFIG.TOKEN_KEY);
        localStorage.removeItem(AUTH_CONFIG.USER_KEY);
    },

    /**
     * Get authorization header for API requests
     */
    getAuthHeader() {
        const token = this.getToken();
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }
};

/**
 * Login functionality
 */
async function handleLogin(event) {
    event.preventDefault();

    const form = event.target;
    const emailInput = form.querySelector('#email');
    const passwordInput = form.querySelector('#password');
    const submitButton = form.querySelector('button[type="submit"]');
    const errorContainer = document.getElementById('error-message');

    // Get form data
    const loginData = {
        username: emailInput.value.trim(),
        password: passwordInput.value
    };

    // Disable submit button
    submitButton.disabled = true;
    submitButton.textContent = 'Signing in...';

    // Hide previous errors
    if (errorContainer) {
        errorContainer.classList.add('hidden');
    }

    try {
        const response = await fetch(AUTH_CONFIG.API_BASE_URL + AUTH_CONFIG.LOGIN_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        const data = await response.json();

        if (response.ok) {
            // Save authentication data
            Auth.saveAuth(data.token, {
                id: data.id,
                username: data.username,
                email: data.email,
                roles: data.roles
            });

            // Show success message
            showNotification('Login successful! Redirecting...', 'success');

            // Redirect to dashboard
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1000);
        } else {
            // Show error message
            const errorMessage = data.message || 'Invalid email or password';
            showError(errorMessage, errorContainer);
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Network error. Please check your connection and try again.', errorContainer);
    } finally {
        // Re-enable submit button
        submitButton.disabled = false;
        submitButton.textContent = 'Sign In';
    }
}

/**
 * Registration functionality
 */
async function handleRegistration(event) {
    event.preventDefault();

    const form = event.target;
    const firstNameInput = form.querySelector('#username'); // Using username field as firstName
    const emailInput = form.querySelector('#email');
    const passwordInput = form.querySelector('#password');
    const confirmPasswordInput = form.querySelector('#confirm-password');
    const submitButton = form.querySelector('button[type="submit"]');

    // Validate passwords match
    if (passwordInput.value !== confirmPasswordInput.value) {
        showNotification('Passwords do not match', 'error');
        return;
    }

    // Get form data
    const registrationData = {
        firstName: firstNameInput.value.trim(),
        lastName: '', // You can add a lastName field if needed
        email: emailInput.value.trim()
    };

    // Store password temporarily for later use
    sessionStorage.setItem('temp_password', passwordInput.value);

    // Disable submit button
    submitButton.disabled = true;
    submitButton.textContent = 'Creating Account...';

    try {
        const response = await fetch(AUTH_CONFIG.API_BASE_URL + AUTH_CONFIG.REGISTER_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registrationData)
        });

        const result = await response.json();

        if (response.ok) {
            // Store registration ID for OTP verification
            sessionStorage.setItem('registration_id', result.data.registrationId);
            sessionStorage.setItem('user_email', emailInput.value.trim());

            // Show success and redirect to OTP verification page
            showNotification('Registration initiated! Please check your email for OTP.', 'success');

            // Create OTP verification UI
            setTimeout(() => {
                showOTPVerification();
            }, 1500);
        } else {
            const errorMessage = result.message || 'Registration failed. Please try again.';
            showNotification(errorMessage, 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showNotification('Network error. Please check your connection and try again.', 'error');
    } finally {
        // Re-enable submit button
        submitButton.disabled = false;
        submitButton.textContent = 'Create Account';
    }
}

/**
 * Show OTP verification form
 */
function showOTPVerification() {
    const email = sessionStorage.getItem('user_email');

    const otpHtml = `
        <div class="auth-container">
            <div class="auth-card">
                <div class="text-center mb-2xl">
                    <a href="/" class="header__logo" style="font-size: var(--font-size-3xl);">CRYPTOBLOG</a>
                </div>

                <h2 class="auth-card__title">Verify Your Email</h2>
                <p style="text-align: center; color: var(--color-text-secondary); margin-bottom: var(--space-xl);">
                    We've sent a verification code to <strong>${email}</strong>
                </p>

                <form id="otp-form">
                    <div class="form-group">
                        <label for="otp" class="form-label">Verification Code</label>
                        <input type="text" id="otp" class="form-input" placeholder="Enter 6-digit code" required maxlength="6">
                    </div>

                    <button type="submit" class="btn btn--primary w-full mb-lg">Verify Code</button>

                    <div class="text-center">
                        <button type="button" id="resend-otp" class="btn btn--ghost">Resend Code</button>
                    </div>
                </form>

                <div class="auth-card__footer">
                    <p><a href="/register">← Back to registration</a></p>
                </div>
            </div>
        </div>
    `;

    document.body.innerHTML = otpHtml;

    // Attach event handlers
    document.getElementById('otp-form').addEventListener('submit', handleOTPVerification);
    document.getElementById('resend-otp').addEventListener('click', handleResendOTP);
}

/**
 * Handle OTP verification
 */
async function handleOTPVerification(event) {
    event.preventDefault();

    const form = event.target;
    const otpInput = form.querySelector('#otp');
    const submitButton = form.querySelector('button[type="submit"]');

    const otpData = {
        registrationId: sessionStorage.getItem('registration_id'),
        otp: otpInput.value
    };

    submitButton.disabled = true;
    submitButton.textContent = 'Verifying...';

    try {
        const response = await fetch(AUTH_CONFIG.API_BASE_URL + AUTH_CONFIG.VALIDATE_OTP_ENDPOINT, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(otpData)
        });

        const result = await response.json();

        if (response.ok) {
            showNotification('Email verified! Completing registration...', 'success');

            // Complete signup with password
            setTimeout(() => {
                completeSignup();
            }, 1000);
        } else {
            showNotification(result.message || 'Invalid verification code', 'error');
        }
    } catch (error) {
        console.error('OTP verification error:', error);
        showNotification('Verification failed. Please try again.', 'error');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Verify Code';
    }
}

/**
 * Complete signup with password
 */
async function completeSignup() {
    const password = sessionStorage.getItem('temp_password');
    const registrationId = sessionStorage.getItem('registration_id');

    const signupData = {
        registrationId: registrationId,
        password: password,
        username: sessionStorage.getItem('user_email') // Use email as username
    };

    try {
        const response = await fetch(AUTH_CONFIG.API_BASE_URL + AUTH_CONFIG.COMPLETE_SIGNUP_ENDPOINT, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(signupData)
        });

        const result = await response.json();

        if (response.ok) {
            // Clear temporary data
            sessionStorage.removeItem('temp_password');
            sessionStorage.removeItem('registration_id');
            sessionStorage.removeItem('user_email');

            showNotification('Registration complete! Redirecting to login...', 'success');

            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
        } else {
            showNotification(result.message || 'Failed to complete registration', 'error');
        }
    } catch (error) {
        console.error('Complete signup error:', error);
        showNotification('Failed to complete registration. Please try again.', 'error');
    }
}

/**
 * Resend OTP
 */
async function handleResendOTP() {
    const registrationId = sessionStorage.getItem('registration_id');
    const button = document.getElementById('resend-otp');

    button.disabled = true;
    button.textContent = 'Sending...';

    try {
        const response = await fetch(
            `${AUTH_CONFIG.API_BASE_URL}/v1/registration/${registrationId}/resend-email-otp`,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );

        if (response.ok) {
            showNotification('Verification code resent! Check your email.', 'success');
        } else {
            showNotification('Failed to resend code. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Resend OTP error:', error);
        showNotification('Failed to resend code. Please try again.', 'error');
    } finally {
        button.disabled = false;
        button.textContent = 'Resend Code';
    }
}

/**
 * Logout functionality
 */
function handleLogout() {
    Auth.clearAuth();
    showNotification('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = '/';
    }, 1000);
}

/**
 * Protect admin pages
 */
function protectAdminPage() {
    if (!Auth.isAuthenticated()) {
        showNotification('Please login to access this page', 'error');
        setTimeout(() => {
            window.location.href = '/login';
        }, 1500);
        return false;
    }

    // Update UI with user info
    updateUserUI();
    return true;
}

/**
 * Update UI with authenticated user info
 */
function updateUserUI() {
    const user = Auth.getUser();
    if (!user) return;

    // Update welcome message
    const welcomeElements = document.querySelectorAll('.header__user span');
    welcomeElements.forEach(el => {
        el.textContent = `Welcome, ${user.username}`;
    });
}

/**
 * Update navigation based on authentication status
 */
function updateNavigation() {
    const isAuthenticated = Auth.isAuthenticated();
    const isAdmin = Auth.isAdmin();

    // Get navigation elements
    const dashboardNavItem = document.getElementById('dashboard-nav-item');
    const guestActions = document.getElementById('guest-actions');
    const userActions = document.getElementById('user-actions');
    const userDisplayName = document.getElementById('user-display-name');
    const logoutBtn = document.getElementById('logout-btn');

    if (isAuthenticated) {
        // Show user actions, hide guest actions
        if (guestActions) guestActions.style.display = 'none';
        if (userActions) userActions.style.display = 'flex';

        // Show dashboard link if user is admin
        if (dashboardNavItem && isAdmin) {
            dashboardNavItem.style.display = 'block';
        }

        // Update user display name
        const user = Auth.getUser();
        if (userDisplayName && user) {
            userDisplayName.textContent = `Welcome, ${user.username}`;
        }

        // Attach logout handler
        if (logoutBtn) {
            logoutBtn.addEventListener('click', handleLogout);
        }
    } else {
        // Show guest actions, hide user actions
        if (guestActions) guestActions.style.display = 'flex';
        if (userActions) userActions.style.display = 'none';

        // Hide dashboard link
        if (dashboardNavItem) {
            dashboardNavItem.style.display = 'none';
        }
    }
}

/**
 * Show error message in container
 */
function showError(message, container) {
    if (container) {
        container.classList.remove('hidden');
        const alertDiv = container.querySelector('.alert');
        if (alertDiv) {
            alertDiv.textContent = message;
        }
    } else {
        showNotification(message, 'error');
    }
}

/**
 * Show notification toast
 */
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existing = document.querySelector('.notification-toast');
    if (existing) {
        existing.remove();
    }

    const notification = document.createElement('div');
    notification.className = `notification-toast notification-toast--${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: var(--color-bg-elevated);
        border: 1px solid var(--color-border);
        border-radius: var(--radius-md);
        color: var(--color-text-primary);
        box-shadow: var(--shadow-lg);
        z-index: 10000;
        animation: slideIn 0.3s ease-out;
        max-width: 400px;
    `;

    if (type === 'success') {
        notification.style.borderColor = 'var(--color-success)';
        notification.style.background = 'rgba(0, 255, 163, 0.1)';
    } else if (type === 'error') {
        notification.style.borderColor = 'var(--color-error)';
        notification.style.background = 'rgba(255, 56, 100, 0.1)';
    }

    document.body.appendChild(notification);

    // Auto-remove after 4 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 4000);
}

// Add animation styles
if (!document.getElementById('auth-animations')) {
    const style = document.createElement('style');
    style.id = 'auth-animations';
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
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
                transform: translateX(100%);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

/**
 * Initialize authentication on page load
 */
document.addEventListener('DOMContentLoaded', function() {
    // Check current page and attach appropriate handlers
    const currentPage = window.location.pathname;

    // Update navigation on all pages
    updateNavigation();

    // Login page
    if (currentPage.includes('login')) {
        const loginForm = document.querySelector('form');
        if (loginForm) {
            loginForm.addEventListener('submit', handleLogin);
        }

        // Redirect if already logged in
        if (Auth.isAuthenticated()) {
            window.location.href = '/dashboard';
        }
    }

    // Register page
    if (currentPage.includes('register')) {
        const registerForm = document.querySelector('form');
        if (registerForm) {
            registerForm.addEventListener('submit', handleRegistration);
        }

        // Redirect if already logged in
        if (Auth.isAuthenticated()) {
            window.location.href = '/dashboard';
        }
    }

    // Protected pages (dashboard, create-post)
    if (currentPage.includes('dashboard') || currentPage.includes('create-post')) {
        protectAdminPage();
    }

    // Attach logout handlers to all logout buttons
    const logoutButtons = document.querySelectorAll('.btn--secondary:contains("Logout"), button:contains("Logout")');
    logoutButtons.forEach(button => {
        if (button.textContent.includes('Logout')) {
            button.addEventListener('click', handleLogout);
        }
    });

    // Add click handler for logout buttons using querySelector
    document.querySelectorAll('button').forEach(button => {
        if (button.textContent.trim() === 'Logout') {
            button.addEventListener('click', handleLogout);
        }
    });
});