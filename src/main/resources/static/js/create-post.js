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

    // Debug: Check authentication status
    console.log('=== Authentication Debug Info ===');
    console.log('Is Authenticated:', Auth.isAuthenticated());
    console.log('Is Admin:', Auth.isAdmin());
    console.log('Token exists:', !!Auth.getToken());
    console.log('Token preview:', Auth.getToken() ? Auth.getToken().substring(0, 30) + '...' : 'none');

    const user = Auth.getUser();
    console.log('User data:', user);
    console.log('User roles:', user?.roles);
    console.log('Auth header:', Auth.getAuthHeader());

    // Get form data - get content from TinyMCE if initialized
    const categoryValue = form.querySelector('#category').value;
    const content = window.tinymce && tinymce.get('content')
        ? tinymce.get('content').getContent()
        : form.querySelector('#content').value.trim();

    const postData = {
        title: form.querySelector('#title').value.trim(),
        subtitle: form.querySelector('#subtitle')?.value.trim() || null,
        content: content,
        excerpt: form.querySelector('#excerpt').value.trim(),
        category: categoryValue || null,
        featuredImage: form.querySelector('#featured-image')?.value.trim() || null,
        tags: form.querySelector('#tags')?.value.split(',').map(tag => tag.trim()).filter(tag => tag) || [],
        status: form.querySelector('#status').value.toUpperCase(),
        metaTitle: form.querySelector('#meta-title')?.value.trim() || null,
        metaDescription: form.querySelector('#meta-description')?.value.trim() || null
    };

    console.log('Category select value:', categoryValue);
    console.log('Post data to submit:', postData);

    // Validate required fields
    if (!postData.title || !postData.content || !postData.excerpt) {
        if (typeof showNotification === 'function') {
            showNotification('Please fill in all required fields', 'error');
        }
        return;
    }

    // Check authentication before proceeding
    if (!Auth.isAuthenticated()) {
        if (typeof showNotification === 'function') {
            showNotification('You must be logged in to create posts', 'error');
        }
        setTimeout(() => {
            window.location.href = '/login';
        }, 1500);
        return;
    }

    // Check admin role
    if (!Auth.isAdmin()) {
        if (typeof showNotification === 'function') {
            showNotification('You must be an admin to create posts', 'error');
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
            window.location.href = '/dashboard';
        }, 1500);
    } catch (error) {
        console.error('Error saving post:', error);
        console.error('Error details:', error.message);
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

        // Set content in TinyMCE if initialized, otherwise in textarea
        if (window.tinymce && tinymce.get('content')) {
            tinymce.get('content').setContent(post.content || '');
        } else {
            document.querySelector('#content').value = post.content || '';
        }

        document.querySelector('#excerpt').value = post.excerpt || '';
        // post.category.category contains the enum value (e.g., "DEFI")
        document.querySelector('#category').value = post.category?.category || '';
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
            window.location.href = '/dashboard';
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
    window.open('/preview-post', '_blank');
}

/**
 * Load categories for the category dropdown
 */
async function loadCategories() {
    const categorySelect = document.querySelector('#category');
    if (!categorySelect) return;

    // Categories are now enums, hardcode them in the frontend
    const categories = [
        { value: 'DEFI', name: 'DeFi' },
        { value: 'NFTS', name: 'NFTs' },
        { value: 'BLOCKCHAIN', name: 'Blockchain' },
        { value: 'TRADING', name: 'Trading' },
        { value: 'SECURITY', name: 'Security' },
        { value: 'WEB3', name: 'Web3' }
    ];

    // Keep the default "Select a category" option
    const defaultOption = categorySelect.querySelector('option[value=""]');
    categorySelect.innerHTML = '';
    if (defaultOption) {
        categorySelect.appendChild(defaultOption);
    }

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.value;
        option.textContent = category.name;
        categorySelect.appendChild(option);
    });
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCreatePost);
} else {
    initCreatePost();
}

/**
 * Handle image upload
 */
async function handleImageUpload() {
    const fileInput = document.querySelector('#featured-image-file');
    const uploadButton = document.querySelector('#upload-image-btn');
    const imageUrlInput = document.querySelector('#featured-image');
    const imagePreview = document.querySelector('#image-preview');
    const imagePreviewImg = document.querySelector('#image-preview-img');

    const file = fileInput.files[0];
    if (!file) {
        if (typeof showNotification === 'function') {
            showNotification('Please select an image file', 'error');
        }
        return;
    }

    // Validate file type
    if (!file.type.startsWith('image/')) {
        if (typeof showNotification === 'function') {
            showNotification('Please select a valid image file', 'error');
        }
        return;
    }

    // Disable button during upload
    uploadButton.disabled = true;
    const originalText = uploadButton.textContent;
    uploadButton.textContent = 'Uploading...';

    try {
        const formData = new FormData();
        formData.append('file', file);

        const token = Auth.getToken();
        console.log('Token exists:', !!token);
        console.log('Token preview:', token ? token.substring(0, 20) + '...' : 'none');

        if (!token) {
            throw new Error('You must be logged in to upload images');
        }

        const response = await fetch(window.location.origin + '/api/upload/image', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Upload failed');
        }

        const result = await response.json();

        // Set the image URL
        imageUrlInput.value = window.location.origin + result.url;

        // Show preview
        imagePreviewImg.src = window.location.origin + result.url;
        imagePreview.style.display = 'block';

        if (typeof showNotification === 'function') {
            showNotification('Image uploaded successfully!', 'success');
        }

    } catch (error) {
        console.error('Error uploading image:', error);
        if (typeof showNotification === 'function') {
            showNotification(error.message || 'Failed to upload image', 'error');
        }
    } finally {
        uploadButton.disabled = false;
        uploadButton.textContent = originalText;
    }
}

function initCreatePost() {
    if (!window.location.pathname.includes('create-post')) return;

    // Load categories
    loadCategories();

    // Initialize TinyMCE editor
    initTinyMCE();

    // Check if editing existing post - delay to ensure TinyMCE is loaded
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');
    if (postId) {
        // Wait for TinyMCE to initialize before loading post data
        setTimeout(() => loadPostForEdit(postId), 500);
    }

    // Attach form submit handler
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', handlePostSubmit);
    }

    // Attach preview handler
    const previewButton = Array.from(document.querySelectorAll('button[type="button"]'))
        .find(btn => btn.textContent.trim() === 'Preview');
    if (previewButton) {
        previewButton.addEventListener('click', handlePreview);
    }

    // Attach upload handler
    const uploadButton = document.querySelector('#upload-image-btn');
    if (uploadButton) {
        uploadButton.addEventListener('click', handleImageUpload);
    }

    // Show preview when image URL is manually entered
    const imageUrlInput = document.querySelector('#featured-image');
    if (imageUrlInput) {
        imageUrlInput.addEventListener('input', function() {
            const imagePreview = document.querySelector('#image-preview');
            const imagePreviewImg = document.querySelector('#image-preview-img');
            if (this.value) {
                imagePreviewImg.src = this.value;
                imagePreview.style.display = 'block';
            } else {
                imagePreview.style.display = 'none';
            }
        });
    }
}

/**
 * Initialize TinyMCE Rich Text Editor
 */
function initTinyMCE() {
    if (!window.tinymce) {
        console.error('TinyMCE not loaded');
        return;
    }

    tinymce.init({
        selector: '#content',
        height: 500,
        menubar: false,
        plugins: [
            'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
            'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
            'insertdatetime', 'media', 'table', 'help', 'wordcount'
        ],
        toolbar: 'undo redo | blocks | bold italic forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat | image | code | help',
        content_style: 'body { font-family:Inter,Helvetica,Arial,sans-serif; font-size:16px }',
        skin: 'oxide-dark',
        content_css: 'dark',

        // Image upload handler
        images_upload_handler: async function (blobInfo, progress) {
            return new Promise(async (resolve, reject) => {
                try {
                    const formData = new FormData();
                    formData.append('file', blobInfo.blob(), blobInfo.filename());

                    const token = Auth.getToken();
                    if (!token) {
                        reject('You must be logged in to upload images');
                        return;
                    }

                    const response = await fetch(window.location.origin + '/api/upload/image', {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`
                        },
                        body: formData
                    });

                    if (!response.ok) {
                        const error = await response.json();
                        reject(error.error || 'Upload failed');
                        return;
                    }

                    const result = await response.json();
                    resolve(window.location.origin + result.url);
                } catch (error) {
                    console.error('Error uploading image:', error);
                    reject(error.message || 'Failed to upload image');
                }
            });
        },

        // Auto resize
        automatic_uploads: true,
        file_picker_types: 'image',

        // Paste images
        paste_data_images: true
    });
}