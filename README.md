# Web3 Blog - Full-Stack Application

A modern, full-stack blog platform built specifically for Web3 projects, featuring user authentication, content management, and subscription functionality.

## 🚀 Features

### Core Features
- **User Authentication & Authorization**
    - JWT-based authentication
    - Role-based access control (Admin/Reader)
    - Secure password hashing with BCrypt

- **Content Management**
    - Create, edit, and delete blog posts
    - Rich text content support
    - Draft and publish functionality
    - Categories and tags organization
    - Featured images support
    - SEO metadata (title, description, keywords)

- **Interactive Features**
    - Comment system for readers
    - Search and filtering capabilities
    - Pagination for better performance
    - Social sharing buttons (Twitter, LinkedIn, Telegram)
    - Email newsletter subscription

- **Admin Dashboard**
    - Post management interface
    - User and comment moderation
    - Analytics and statistics
    - Category management
    - Subscriber management with CSV export

- **Responsive Design**
    - Mobile-first approach
    - Modern UI with CSS Grid and Flexbox
    - Clean, professional design
    - Optimized for all screen sizes

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA with Hibernate
- **Language**: Java 17
- **Build Tool**: Maven

### Frontend
- **Languages**: HTML5, CSS3, Vanilla JavaScript
- **Design**: Responsive CSS Grid/Flexbox
- **No external frameworks** - Pure HTML/CSS/JS implementation

## 📁 Project Structure

```
web3-blog/
├── backend/
│   ├── src/main/java/com/web3blog/
│   │   ├── config/          # Security & JWT configuration
│   │   ├── controllers/     # REST API controllers
│   │   ├── services/        # Business logic services
│   │   ├── repositories/    # JPA repositories
│   │   ├── models/          # Entity classes
│   │   ├── dto/             # Data Transfer Objects
│   │   └── Web3BlogApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/
│   ├── index.html           # Home page with post listings
│   ├── post.html           # Single post view
│   ├── login.html          # Login form
│   ├── register.html       # Registration form
│   ├── dashboard.html      # Admin dashboard
│   ├── create-post.html    # Post creation/editing
│   ├── css/
│   │   └── styles.css      # Complete styling
│   └── assets/            # Images and static files
└── README.md
```

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6+
- Git

### Database Setup

1. **Install PostgreSQL** and create a database:
```sql
CREATE DATABASE web3blog;
CREATE USER web3blog_user WITH PASSWORD 'your_password_here';
GRANT ALL PRIVILEGES ON DATABASE web3blog TO web3blog_user;
```

2. **Create initial roles** (run after starting the application):
```sql
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_READER');
```

### Backend Setup

1. **Clone the repository**:
```bash
git clone <repository-url>
cd web3-blog/backend
```

2. **Configure database connection**:
    - Edit `src/main/resources/application.properties`
    - Update database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/web3blog
spring.datasource.username=web3blog_user
spring.datasource.password=your_password_here
```

3. **Configure JWT secret**:
    - Generate a secure random string (32+ characters)
    - Update in `application.properties`:
```properties
app.jwt.secret=your-secure-jwt-secret-key-here
```

4. **Build and run the application**:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**:
```bash
cd ../frontend
```

2. **Serve the frontend**:
    - **Option 1**: Use a simple HTTP server
   ```bash
   # Using Python 3
   python -m http.server 3000
   
   # Using Node.js
   npx http-server -p 3000
   
   # Using PHP
   php -S localhost:3000
   ```

    - **Option 2**: Use any web server (Apache, Nginx, etc.)

3. **Access the application**:
    - Frontend: `http://localhost:3000`
    - Backend API: `http://localhost:8080`

### Initial Admin Setup

1. **Register a new account** at `http://localhost:3000/register.html`

2. **Promote to admin** (run in database):
```sql
-- Find your user ID
SELECT id FROM users WHERE username = 'your-username';

-- Add admin role
INSERT INTO user_roles (user_id, role_id) 
VALUES (your_user_id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));
```

3. **Login and access dashboard** at `http://localhost:3000/dashboard.html`

## 📊 API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Posts
- `GET /api/posts` - Get all published posts (paginated)
- `GET /api/posts/{id}` - Get post by ID
- `GET /api/posts/slug/{slug}` - Get post by slug
- `POST /api/posts` - Create new post (Admin only)
- `PUT /api/posts/{id}` - Update post (Admin only)
- `DELETE /api/posts/{id}` - Delete post (Admin only)
- `GET /api/posts/search?keyword={keyword}` - Search posts
- `GET /api/posts/category/{categoryId}` - Get posts by category
- `GET /api/posts/tag/{tagId}` - Get posts by tag

### Comments
- `GET /api/comments/post/{postId}` - Get comments for post
- `POST /api/comments/post/{postId}` - Add comment (Authenticated users)
- `DELETE /api/comments/{id}` - Delete comment (Admin only)

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category (Admin only)
- `PUT /api/categories/{id}` - Update category (Admin only)
- `DELETE /api/categories/{id}` - Delete category (Admin only)

### Subscribers
- `POST /api/subscribers/subscribe` - Subscribe to newsletter
- `POST /api/subscribers/unsubscribe` - Unsubscribe from newsletter
- `GET /api/subscribers/count` - Get subscriber count
- `GET /api/subscribers` - Get all subscribers (Admin only)

## 🔧 Configuration Options

### Application Properties
```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/web3blog
spring.datasource.username=web3blog_user
spring.datasource.password=your_password_here

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
app.jwt.secret=your-secure-jwt-secret-key
app.jwt.expiration-ms=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Mail Configuration (Optional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Frontend Configuration
- Update `API_BASE` constant in JavaScript files if backend URL changes
- Modify CSS variables in `styles.css` for theme customization

## 🎨 Customization

### Styling
- **Colors**: Modify CSS custom properties in `styles.css`
- **Fonts**: Update font families in the CSS
- **Layout**: Adjust grid and flexbox properties
- **Components**: Customize card styles, buttons, and forms

### Functionality
- **Categories**: Add default categories in the database
- **Email Templates**: Customize email notifications
- **Social Sharing**: Add more platforms in the frontend
- **Rich Text**: Enhance the content editor with more features

## 🔒 Security Features

- **Authentication**: JWT-based with secure token storage
- **Authorization**: Role-based access control
- **Password Security**: BCrypt hashing with salt
- **CORS Configuration**: Configurable cross-origin requests
- **Input Validation**: Both frontend and backend validation
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **XSS Protection**: Content sanitization

## 📱 Browser Compatibility

- **Modern Browsers**: Chrome 90+, Firefox 90+, Safari 14+, Edge 90+
- **Mobile**: iOS Safari, Chrome Mobile, Samsung Internet
- **Features Used**: CSS Grid, Flexbox, ES6+, Fetch API

## 🚀 Deployment

### Backend Deployment
1. **Build JAR file**:
```bash
mvn clean package -DskipTests
```

2. **Deploy to server** with Java 17 runtime
3. **Configure production database**
4. **Set environment variables** for sensitive data

### Frontend Deployment
1. **Upload files** to web server
2. **Configure HTTPS** for production
3. **Update API endpoints** to production URLs
4. **Set up CDN** for static assets (optional)

### Docker Deployment (Optional)
Create `Dockerfile` for backend:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/web3-blog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📈 Performance Considerations

- **Database Indexing**: Add indexes on frequently queried columns
- **Caching**: Implement Redis for session storage and caching
- **Pagination**: All list endpoints use pagination
- **Image Optimization**: Compress and resize images before upload
- **CDN**: Use CDN for static assets in production

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**:
    - Verify PostgreSQL is running
    - Check database credentials
    - Ensure database exists

2. **JWT Token Issues**:
    - Verify JWT secret is properly set
    - Check token expiration settings
    - Clear browser localStorage

3. **CORS Errors**:
    - Update allowed origins in backend
    - Check frontend API base URL

4. **Build Failures**:
    - Ensure Java 17 is installed
    - Check Maven configuration
    - Verify all dependencies are available

### Logs
- Backend logs: Console output shows SQL queries and errors
- Frontend errors: Check browser console for JavaScript errors

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
1. Check the troubleshooting section
2. Review application logs
3. Create an issue in the repository
4. Contact the development team

## 🚀 Future Enhancements

- **Advanced Editor**: Rich text editor with image upload
- **Email Notifications**: Automated email campaigns
- **Analytics Dashboard**: Detailed visitor and engagement metrics
- **Multi-language Support**: Internationalization
- **API Rate Limiting**: Prevent abuse with rate limiting
- **File Upload Service**: Integrated file storage
- **Advanced Search**: Elasticsearch integration
- **Social Login**: OAuth2 integration with major platforms
