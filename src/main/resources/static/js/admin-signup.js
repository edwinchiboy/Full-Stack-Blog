// Admin Signup form handling
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('admin-signup-form');
    const errorMessageDiv = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    const successMessageDiv = document.getElementById('success-message');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Hide previous messages
        errorMessageDiv.classList.add('hidden');
        successMessageDiv.classList.add('hidden');

        // Get form values
        const firstName = document.getElementById('firstName').value.trim();
        const lastName = document.getElementById('lastName').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // Validate passwords match
        if (password !== confirmPassword) {
            errorText.textContent = 'Passwords do not match!';
            errorMessageDiv.classList.remove('hidden');
            return;
        }

        // Validate password length
        if (password.length < 8) {
            errorText.textContent = 'Password must be at least 8 characters long!';
            errorMessageDiv.classList.remove('hidden');
            return;
        }

        // Prepare request data
        const signupData = {
            firstName,
            lastName,
            email,
            password
        };

        try {
            // Make API call
            const response = await fetch('/api/auth/admin/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(signupData)
            });

            const data = await response.json();

            if (response.ok) {
                // Show success message
                successMessageDiv.classList.remove('hidden');

                // Redirect to login after 2 seconds
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            } else {
                // Show error message
                errorText.textContent = data.message || 'An error occurred during signup';
                errorMessageDiv.classList.remove('hidden');
            }
        } catch (error) {
            console.error('Signup error:', error);
            errorText.textContent = 'An unexpected error occurred. Please try again.';
            errorMessageDiv.classList.remove('hidden');
        }
    });
});