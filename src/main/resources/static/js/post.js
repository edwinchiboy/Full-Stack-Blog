// Get slug from URL parameter
const urlParams = new URLSearchParams(window.location.search);
const slug = urlParams.get('slug');

if (!slug) {
    window.location.href = '/';
}

let currentPostId = null;

// Fetch and display post
async function loadPost() {
    try {
        const response = await fetch(`/api/posts/slug/${slug}`);

        if (!response.ok) {
            if (response.status === 404) {
                document.querySelector('.container--narrow').innerHTML = `
                    <div style="text-align: center; padding: 4rem 0;">
                        <h1>Post Not Found</h1>
                        <p>The post you're looking for doesn't exist.</p>
                        <a href="/" class="btn btn--primary">Go Home</a>
                    </div>
                `;
                return;
            }
            throw new Error('Failed to load post');
        }

        const post = await response.json();
        currentPostId = post.id;
        displayPost(post);

        // Load comments and related posts
        loadComments(post.id);
        loadRelatedPosts(post.category);

    } catch (error) {
        console.error('Error loading post:', error);
        document.querySelector('.container--narrow').innerHTML = `
            <div style="text-align: center; padding: 4rem 0;">
                <h1>Error Loading Post</h1>
                <p>Sorry, there was an error loading this post.</p>
                <a href="/" class="btn btn--primary">Go Home</a>
            </div>
        `;
    }
}

function displayPost(post) {
    // Update page title
    document.title = `${post.title} - CryptoBlog`;

    // Update meta description
    const metaDescription = document.querySelector('meta[name="description"]');
    if (metaDescription) {
        metaDescription.content = post.excerpt || post.content.substring(0, 150);
    }

    // Update article header
    const articleHeader = document.querySelector('.article-header');
    if (articleHeader) {
        const categoryMap = {
            DEFI: 'DeFi',
            NFTS: 'NFTs',
            BLOCKCHAIN: 'Blockchain',
            TRADING: 'Trading',
            SECURITY: 'Security',
            WEB3: 'Web3'
        };

        articleHeader.innerHTML = `
            <div class="article-header__category">
                <span class="post-card__category">${categoryMap[post.category] || post.category}</span>
            </div>

            <h1 class="article-header__title">${post.title}</h1>

            ${post.excerpt ? `<p class="article-header__subtitle">${post.excerpt}</p>` : ''}

            <div class="article-header__meta">
                <span>By <strong>${post.author?.username || 'Anonymous'}</strong></span>
                <span>•</span>
                <span>${formatDate(post.publishedAt || post.createdAt)}</span>
            </div>
        `;
    }

    // Update article body
    const articleBody = document.querySelector('.article-body');
    if (articleBody) {
        articleBody.innerHTML = post.content;
    }

    // Remove tags section
    const tagList = document.querySelector('.tag-list');
    if (tagList) {
        tagList.remove();
    }
}

async function loadComments(postId) {
    try {
        const response = await fetch(`/api/comments/post/${postId}`);

        if (!response.ok) {
            throw new Error('Failed to load comments');
        }

        const comments = await response.json();
        displayComments(comments);

    } catch (error) {
        console.error('Error loading comments:', error);
        const commentsContainer = document.querySelector('.section:has(.comment-form) > .container');
        if (commentsContainer) {
            const commentsList = commentsContainer.querySelector('div:last-child');
            if (commentsList) {
                commentsList.innerHTML = '<p style="color: var(--color-text-secondary);">No comments yet. Be the first to comment!</p>';
            }
        }
    }
}

function displayComments(comments) {
    const commentsContainer = document.querySelector('.section:has(.comment-form) > .container');
    if (!commentsContainer) return;

    const commentsList = commentsContainer.querySelector('div:last-child');
    if (!commentsList) return;

    if (comments.length === 0) {
        commentsList.innerHTML = '<p style="color: var(--color-text-secondary);">No comments yet. Be the first to comment!</p>';
        return;
    }

    commentsList.innerHTML = comments.map(comment => `
        <div class="comment">
            <div class="comment__header">
                <div class="comment__author">
                    <span class="comment__author-name">${comment.author?.username || 'Anonymous'}</span>
                </div>
                <span class="comment__date">${formatTimeAgo(comment.createdAt)}</span>
            </div>
            <p class="comment__body">${comment.content}</p>
        </div>
    `).join('');
}

async function loadRelatedPosts(category) {
    try {
        const response = await fetch(`/api/posts?page=0&size=3`);

        if (!response.ok) {
            throw new Error('Failed to load related posts');
        }

        const data = await response.json();
        // Filter posts by same category and exclude current post
        let relatedPosts = data.content.filter(post =>
            post.category === category && post.id !== currentPostId
        ).slice(0, 3);

        // If not enough posts with same category, add other posts
        if (relatedPosts.length < 3) {
            const otherPosts = data.content.filter(post =>
                post.id !== currentPostId && !relatedPosts.includes(post)
            );
            relatedPosts = [...relatedPosts, ...otherPosts].slice(0, 3);
        }

        displayRelatedPosts(relatedPosts);

    } catch (error) {
        console.error('Error loading related posts:', error);
        const relatedSection = document.querySelector('.section:has(.grid--posts)');
        if (relatedSection) {
            relatedSection.style.display = 'none';
        }
    }
}

function displayRelatedPosts(posts) {
    const relatedGrid = document.querySelector('.section:has(.grid--posts) .grid--posts');
    if (!relatedGrid) return;

    const categoryMap = {
        DEFI: 'DeFi',
        NFTS: 'NFTs',
        BLOCKCHAIN: 'Blockchain',
        TRADING: 'Trading',
        SECURITY: 'Security',
        WEB3: 'Web3'
    };

    if (posts.length === 0) {
        const relatedSection = document.querySelector('.section:has(.grid--posts)');
        if (relatedSection) {
            relatedSection.style.display = 'none';
        }
        return;
    }

    relatedGrid.innerHTML = posts.map(post => `
        <article class="card post-card">
            <div class="post-card__content">
                <div class="post-card__header">
                    <span class="post-card__category">${categoryMap[post.category] || post.category}</span>
                    <span class="post-card__title"><a href="/post?slug=${post.slug}">${post.title}</a></span>
                </div>
                <p class="post-card__excerpt">
                    ${post.excerpt || post.content.substring(0, 100) + '...'}
                </p>
                <div class="post-card__meta">
                    <div class="post-card__author">
                        <span>By ${post.author?.username || 'Anonymous'}</span>
                    </div>
                    <span class="post-card__date">${formatDate(post.publishedAt || post.createdAt)}</span>
                </div>
            </div>
        </article>
    `).join('');
}

function formatTimeAgo(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    const intervals = {
        year: 31536000,
        month: 2592000,
        week: 604800,
        day: 86400,
        hour: 3600,
        minute: 60
    };

    for (const [unit, secondsInUnit] of Object.entries(intervals)) {
        const interval = Math.floor(seconds / secondsInUnit);
        if (interval >= 1) {
            return `${interval} ${unit}${interval > 1 ? 's' : ''} ago`;
        }
    }

    return 'just now';
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
}

// Load post when page loads
document.addEventListener('DOMContentLoaded', loadPost);
