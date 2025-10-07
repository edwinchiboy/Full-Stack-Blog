/**
 * Newsletter Subscription Module
 * Handles email subscription functionality
 */

const NewsletterAPI = {
    BASE_URL: window.location.origin + '/api/subscribers',

    /**
     * Subscribe to newsletter
     */
    async subscribe(email) {
        try {
            const response = await fetch(`${this.BASE_URL}/subscribe`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Subscription failed');
            }

            return data;
        } catch (error) {
            console.error('Subscription error:', error);
            throw error;
        }
    },

    /**
     * Unsubscribe from newsletter
     */
    async unsubscribe(email) {
        try {
            const response = await fetch(`${this.BASE_URL}/unsubscribe`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Unsubscribe failed');
            }

            return data;
        } catch (error) {
            console.error('Unsubscribe error:', error);
            throw error;
        }
    },

    /**
     * Check subscription status
     */
    async checkSubscription(email) {
        try {
            const response = await fetch(`${this.BASE_URL}/check/${encodeURIComponent(email)}`);
            if (!response.ok) throw new Error('Failed to check subscription');
            return await response.json();
        } catch (error) {
            console.error('Check subscription error:', error);
            throw error;
        }
    }
};

/**
 * Handle newsletter subscription form
 */
async function handleNewsletterSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const emailInput = form.querySelector('input[type="email"]');
    const submitButton = form.querySelector('button[type="submit"]');

    if (!emailInput) return;

    const email = emailInput.value.trim();

    if (!email) {
        if (typeof showNotification === 'function') {
            showNotification('Please enter a valid email address', 'error');
        }
        return;
    }

    // Disable submit button
    submitButton.disabled = true;
    const originalText = submitButton.textContent;
    submitButton.textContent = 'Subscribing...';

    try {
        await NewsletterAPI.subscribe(email);

        if (typeof showNotification === 'function') {
            showNotification('Successfully subscribed to our newsletter!', 'success');
        }

        // Clear form
        form.reset();
    } catch (error) {
        console.error('Newsletter subscription error:', error);
        if (typeof showNotification === 'function') {
            showNotification(error.message || 'Subscription failed. Please try again.', 'error');
        }
    } finally {
        // Re-enable submit button
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    }
}

// Initialize newsletter subscriptions on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNewsletter);
} else {
    initNewsletter();
}

function initNewsletter() {
    // Find all newsletter forms on the page
    const newsletterForms = document.querySelectorAll('form');

    newsletterForms.forEach(form => {
        // Check if form has email input and subscribe button
        const hasEmailInput = form.querySelector('input[type="email"]');
        const hasSubscribeButton = Array.from(form.querySelectorAll('button'))
            .some(btn => btn.textContent.toLowerCase().includes('subscribe'));

        if (hasEmailInput && hasSubscribeButton) {
            form.addEventListener('submit', handleNewsletterSubmit);
        }
    });
}