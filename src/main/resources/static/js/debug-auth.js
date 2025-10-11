/**
 * Debug script to check authentication status
 * Run this in browser console to see current auth state
 */

console.log('=== AUTH DEBUG INFO ===');
console.log('JWT Token:', localStorage.getItem('jwt_token'));
console.log('User Data:', localStorage.getItem('user_data'));

const userData = localStorage.getItem('user_data');
if (userData) {
    const user = JSON.parse(userData);
    console.log('Parsed User:', user);
    console.log('User Roles:', user.roles);
}

// Decode JWT to see what's inside
const token = localStorage.getItem('jwt_token');
if (token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const decoded = JSON.parse(jsonPayload);
        console.log('Decoded JWT Token:', decoded);
        console.log('Token Roles:', decoded.roles);
        console.log('Token Issued At:', new Date(decoded.iat * 1000));
        console.log('Token Expires At:', new Date(decoded.exp * 1000));
        console.log('Token is expired:', Date.now() > decoded.exp * 1000);
    } catch (e) {
        console.error('Error decoding token:', e);
    }
}