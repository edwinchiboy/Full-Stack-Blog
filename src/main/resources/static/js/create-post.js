/**
 * Create/Edit Post Module
 * Handles post creation and editing functionality
 */

let editingPostId = null;

/**
 * Handle post form submission
 */
async function handlePostSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const submitButton = form.querySelector('button[type="submit"]');

    // Get form data
    const postData = {
        title: form.querySelector('#title').value.trim(),
        subtitle: form.querySelector('#subtitle')?.value.trim() || null,
        content: form.querySelector('#content').value.trim(),
        excerpt: form.querySelector('#excerpt').value.trim(),
        categoryId: form.querySelector('#category').value,
        featuredImage: form.querySelector('#featured-image')?.value.trim() || null,
        tags: form.querySelector('#tags')?.value.split(',').map(tag => tag.trim()).filter(tag => tag) || [],
        status: form.querySelector('#status').value.toUpperCase(),
        metaTitle: form.querySelector('#meta-title')?.value.trim() || null,
        metaDescription: form.querySelector('#meta-description')?.value.trim() || null
    };

    // Validate required fields
    if (!postData.title || !postData.content || !postData.excerpt) {
        if (typeof showNotification === 'function') {
            showNotification('Please fill in all required fields', 'error');
        }
        return;
    }

    // Disable submit button
    submitButton.disabled = true;
    const originalText = submitButton.textContent;
    submitButton.textContent = editingPostId ? 'Updating...' : 'Creating...';

    try {
        let result;
        if (editingPostId) {
            result = await PostsAPI.updatePost(editingPostId, postData);
            if (typeof showNotification === 'function') {
                showNotification('Post updated successfully!', 'success');
            }
        } else {
            result = await PostsAPI.createPost(postData);
            if (typeof showNotification === 'function') {
                showNotification('Post created successfully!', 'success');
            }
        }

        // Redirect to dashboard after a short delay
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (error) {
        console.error('Error saving post:', error);
        if (typeof showNotification === 'function') {
            showNotification(error.message || 'Failed to save post', 'error');
        }
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    }
}

/**
 * Load post data for editing
 */
async function loadPostForEdit(postId) {
    try {
        const post = await PostsAPI.getPostById(postId);

        // Update page title
        const pageTitle = document.querySelector('h1');
        if (pageTitle) {
            pageTitle.textContent = 'Edit Post';
        }

        // Fill form with post data
        document.querySelector('#title').value = post.title || '';
        document.querySelector('#subtitle').value = post.subtitle || '';
        document.querySelector('#content').value = post.content || '';
        document.querySelector('#excerpt').value = post.excerpt || '';
        document.querySelector('#category').value = post.category?.id || '';
        document.querySelector('#featured-image').value = post.featuredImage || '';
        document.querySelector('#tags').value = post.tags?.join(', ') || '';
        document.querySelector('#status').value = post.status?.toLowerCase() || 'draft';
        document.querySelector('#meta-title').value = post.metaTitle || '';
        document.querySelector('#meta-description').value = post.metaDescription || '';

        // Update submit button text
        const submitButton = document.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.textContent = 'Update Post';
        }

        editingPostId = postId;
    } catch (error) {
        console.error('Error loading post:', error);
        if (typeof showNotification === 'function') {
            showNotification('Failed to load post data', 'error');
        }
        // Redirect to dashboard if post not found
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 2000);
    }
}

/**
 * Handle save as draft button
 */
function handleSaveAsDraft() {
    const statusSelect = document.querySelector('#status');
    if (statusSelect) {
        statusSelect.value = 'draft';
    }
    // Trigger form submission
    const form = document.querySelector('form');
    if (form) {
        form.dispatchEvent(new Event('submit'));
    }
}

/**
 * Handle preview button
 */
function handlePreview() {
    // Get current form data
    const title = document.querySelector('#title').value;
    const content = document.querySelector('#content').value;

    if (!title || !content) {
        if (typeof showNotification === 'function') {
            showNotification('Please fill in title and content to preview', 'error');
        }
        return;
    }

    // Store in session storage for preview page
    sessionStorage.setItem('preview_post', JSON.stringify({
        title,
        subtitle: document.querySelector('#subtitle').value,
        content,
        excerpt: document.querySelector('#excerpt').value,
        featuredImage: document.querySelector('#featured-image').value
    }));

    // Open preview in new tab
    window.open('preview-post.html', '_blank');
}

/**
 * Load categories for the category dropdown
 */
async function loadCategories() {
    try {
        const response = await fetch(window.location.origin + '/api/categories');
        if (!response.ok) throw new Error('Failed to fetch categories');

        const categories = await response.json();
        const categorySelect = document.querySelector('#category');

        if (categorySelect && categories.length > 0) {
            // Keep the default "Select a category" option
            const defaultOption = categorySelect.querySelector('option[value=""]');
            categorySelect.innerHTML = '';
            if (defaultOption) {
                categorySelect.appendChild(defaultOption);
            }

            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCreatePost);
} else {
    initCreatePost();
}

function initCreatePost() {
    if (!window.location.pathname.includes('create-post.html')) return;

    // Load categories
    loadCategories();

    // Check if editing existing post
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');
    if (postId) {
        loadPostForEdit(postId);
    }

    // Attach form submit handler
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', handlePostSubmit);
    }

    // Attach save as draft handler
    const draftButton = document.querySelector('button[type="button"].btn--secondary');
    if (draftButton) {
        draftButton.addEventListener('click', handleSaveAsDraft);
    }

    // Attach preview handler
    const previewButton = Array.from(document.querySelectorAll('button[type="button"]'))
        .find(btn => btn.textContent.trim() === 'Preview');
    if (previewButton) {
        previewButton.addEventListener('click', handlePreview);
    }
}