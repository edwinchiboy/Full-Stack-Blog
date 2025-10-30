# Deployment Guide - Railway.app

## Prerequisites
- GitHub account
- Railway.app account (sign up at https://railway.app)
- Your code pushed to GitHub

## Step-by-Step Deployment

### 1. Push Code to GitHub
```bash
git add .
git commit -m "Prepare for Railway deployment"
git push origin main
```

### 2. Create Railway Project

1. Go to https://railway.app
2. Click "Start a New Project"
3. Select "Deploy from GitHub repo"
4. Authorize Railway to access your GitHub
5. Select your `cutom-blog` repository

### 3. Add PostgreSQL Database

1. In your Railway project, click "+ New"
2. Select "Database" → "PostgreSQL"
3. Railway will automatically create a PostgreSQL instance
4. Railway will automatically set the `DATABASE_URL` environment variable

### 4. Configure Environment Variables

Click on your application service → "Variables" tab and add:

**Required Variables:**
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<generate-a-secure-random-string-here>
```

**Optional Email Variables (if using email features):**
```
DEFAULT_FROM_EMAIL=noreply@yourdomain.com
EMAIL_PASSWORD=your-email-password
SMTP_HOST=smtp.gmail.com
EMAIL_PORT=587
SKIP_EMAIL_VERIFICATION=true
```

**Optional Mailgun Variables:**
```
MAILGUN_API_KEY=your-mailgun-api-key
MAILGUN_DOMAIN=your-mailgun-domain
```

**CORS Configuration:**
After deployment, add your Railway URL:
```
ALLOWED_ORIGINS=https://your-app-name.railway.app
```

### 5. Generate Secure JWT Secret

Run this command to generate a secure JWT secret:
```bash
openssl rand -base64 32
```
Copy the output and use it as your `JWT_SECRET` value in Railway.

### 6. Deploy

Railway will automatically:
1. Detect the Dockerfile
2. Build your application
3. Deploy it
4. Provide you with a public URL

### 7. Update CORS After First Deployment

1. Once deployed, copy your Railway URL (e.g., `https://your-app.railway.app`)
2. Go to Railway Variables
3. Update `ALLOWED_ORIGINS` with your actual Railway URL
4. Railway will automatically redeploy

## Accessing Your Application

Your application will be available at:
```
https://your-app-name.railway.app
```

## Database Migrations

Flyway will automatically run migrations on startup. All your migration files in `src/main/resources/db/migration/` will be executed.

## Monitoring

- View logs: Railway Dashboard → Your Service → "Deployments" tab
- View metrics: Railway Dashboard → Your Service → "Metrics" tab

## Troubleshooting

### Build Fails
- Check Railway logs for specific errors
- Ensure all environment variables are set correctly
- Verify Dockerfile syntax

### Database Connection Issues
- Ensure PostgreSQL service is running in Railway
- Check that `DATABASE_URL` is automatically set
- Verify Flyway migrations completed successfully

### Application Not Accessible
- Check if deployment succeeded in Railway dashboard
- Verify the application is listening on port 8080
- Check security group settings if using custom domain

## Cost
- Railway free tier: $5 credit per month
- Should be sufficient for small applications
- Monitor usage in Railway dashboard

## Custom Domain (Optional)

1. Go to your service settings
2. Click "Settings" → "Domains"
3. Add your custom domain
4. Update DNS records as instructed
5. Update `ALLOWED_ORIGINS` environment variable

## Useful Commands

Check deployment status:
```bash
# View in Railway dashboard or use Railway CLI
railway status
```

View logs:
```bash
railway logs
```

## Security Notes

1. Never commit `.env` files or secrets to Git
2. Use strong, unique JWT secret
3. Keep database credentials secure
4. Regularly update dependencies
5. Monitor access logs

## Additional Resources

- [Railway Documentation](https://docs.railway.app)
- [Spring Boot Production Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [PostgreSQL on Railway](https://docs.railway.app/databases/postgresql)