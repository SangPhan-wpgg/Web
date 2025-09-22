// Main JavaScript for Video Platform

// Global error handler to catch unhandled errors
window.addEventListener('error', function(event) {
    // Filter out browser extension errors, Google Analytics, and WPS errors
    if (!event.filename || 
        (!event.filename.includes('extension') && 
         !event.filename.includes('google-analytics') &&
         !event.filename.includes('wps.com') &&
         !event.filename.includes('chrome-extension'))) {
        console.warn('JavaScript error caught:', event.error);
    }
});

// Handle unhandled promise rejections
window.addEventListener('unhandledrejection', function(event) {
    // Filter out browser extension errors, Google Analytics, and WPS errors
    if (!event.reason || 
        (!event.reason.toString().includes('extension') &&
         !event.reason.toString().includes('google-analytics') &&
         !event.reason.toString().includes('wps.com') &&
         !event.reason.toString().includes('chrome-extension'))) {
        console.warn('Unhandled promise rejection:', event.reason);
    }
});

// Filter out network errors from browser extensions
const originalFetch = window.fetch;
window.fetch = function(...args) {
    return originalFetch.apply(this, args).catch(error => {
        // Filter out browser extension network errors
        if (error.message && 
            (error.message.includes('google-analytics') ||
             error.message.includes('wps.com') ||
             error.message.includes('extension') ||
             error.message.includes('chrome-extension'))) {
            console.log('Filtered out browser extension network error:', error.message);
            return Promise.resolve(new Response('', { status: 200 }));
        }
        throw error;
    });
};

document.addEventListener('DOMContentLoaded', function() {
    try {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    } catch (error) {
        console.warn('Error initializing Bootstrap components:', error);
    }

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        try {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
        } catch (error) {
            console.warn('Error auto-hiding alerts:', error);
        }
    }, 5000);

    // Form validation for register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            try {
                // Run validation but don't block form submission
                const isValid = validateRegisterForm();
                console.log('Form validation result:', isValid);
                
                // Always allow form to submit - server will handle validation
                // Only show validation feedback, don't prevent submission
                
            } catch (error) {
                console.warn('Error in form validation:', error);
                // Always allow form to submit if validation fails
            }
        });
    }

    // Password strength indicator
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            const password = this.value;
            const strength = getPasswordStrength(password);
            updatePasswordStrength(strength);
        });
    }

    // Real-time validation for register form
    if (registerForm) {
        // Username validation
        const usernameInput = document.getElementById('userName');
        if (usernameInput) {
            usernameInput.addEventListener('input', function() {
                validateUsername(this);
            });
        }

        // Email validation
        const emailInput = document.getElementById('email');
        if (emailInput) {
            emailInput.addEventListener('input', function() {
                validateEmail(this);
            });
        }

        // Phone validation
        const phoneInput = document.getElementById('phone');
        if (phoneInput) {
            phoneInput.addEventListener('input', function() {
                validatePhone(this);
            });
        }

        // Full name validation
        const fullNameInput = document.getElementById('fullName');
        if (fullNameInput) {
            fullNameInput.addEventListener('input', function() {
                validateFullName(this);
            });
        }
    }

    // Confirm password validation
    const confirmPasswordInput = document.getElementById('confirmPassword');
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (password !== confirmPassword) {
                this.setCustomValidity('Mật khẩu không khớp');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    // Search functionality
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const searchInput = document.getElementById('searchInput');
            if (searchInput.value.trim() === '') {
                e.preventDefault();
                searchInput.focus();
            }
        });
    }

    // Loading states for buttons
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(button => {
        button.addEventListener('click', function() {
            console.log('Submit button clicked - allowing form submission');
            // Show loading state but don't disable immediately
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Đang xử lý...';
            // Don't disable button immediately - let form submit first
            
            // Force form submission if needed
            const form = this.closest('form');
            if (form) {
                console.log('Form found, allowing submission');
                // Remove any preventDefault that might be blocking
            setTimeout(() => {
                    if (!form.checkValidity || form.checkValidity()) {
                        form.submit();
                    }
                }, 100);
            }
        });
    });
});

// Password strength calculation
function getPasswordStrength(password) {
    let strength = 0;
    
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]/)) strength++;
    if (password.match(/[A-Z]/)) strength++;
    if (password.match(/[0-9]/)) strength++;
    if (password.match(/[^a-zA-Z0-9]/)) strength++;
    
    return strength;
}

// Update password strength indicator
function updatePasswordStrength(strength) {
    const indicator = document.getElementById('passwordStrength');
    if (!indicator) return;
    
    const strengthText = ['Rất yếu', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh'];
    const strengthColors = ['danger', 'warning', 'info', 'success', 'success'];
    
    indicator.textContent = strengthText[strength] || '';
    indicator.className = `badge bg-${strengthColors[strength] || 'secondary'}`;
}

// Show/hide password
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = document.querySelector(`[onclick="togglePassword('${inputId}')"] i`);
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'bi bi-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'bi bi-eye';
    }
}

// Copy to clipboard
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        showToast('Đã sao chép vào clipboard!', 'success');
    });
}

// Show toast notification
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    toast.addEventListener('hidden.bs.toast', function() {
        toast.remove();
    });
}

// Create toast container if it doesn't exist
function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '1055';
    document.body.appendChild(container);
    return container;
}

// Format file size
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Format duration
function formatDuration(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
        return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    } else {
        return `${minutes}:${secs.toString().padStart(2, '0')}`;
    }
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Real-time validation functions
function validateUsername(input) {
    try {
    const value = input.value.trim();
    const usernamePattern = /^[a-zA-Z0-9_]{3,20}$/;
    
    if (value.length === 0) {
        setFieldValid(input, false, 'Tên đăng nhập không được để trống');
    } else if (!usernamePattern.test(value)) {
        setFieldValid(input, false, 'Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (3-20 ký tự)');
    } else {
        setFieldValid(input, true, 'Tên đăng nhập hợp lệ!');
        }
    } catch (error) {
        console.warn('Error validating username:', error);
    }
}

function validateEmail(input) {
    try {
    const value = input.value.trim();
    const emailPattern = /^[A-Za-z0-9+_.-]+@(.+)$/;
    
    if (value.length === 0) {
        setFieldValid(input, false, 'Email không được để trống');
    } else if (!emailPattern.test(value)) {
        setFieldValid(input, false, 'Email không hợp lệ');
    } else {
        setFieldValid(input, true, 'Email hợp lệ!');
        }
    } catch (error) {
        console.warn('Error validating email:', error);
    }
}

function validatePhone(input) {
    try {
    const value = input.value.trim();
    const phonePattern = /^[0-9]{10,11}$/;
    
    if (value.length === 0) {
        setFieldValid(input, false, 'Số điện thoại không được để trống');
    } else if (!phonePattern.test(value)) {
        setFieldValid(input, false, 'Số điện thoại phải có 10-11 chữ số');
    } else {
        setFieldValid(input, true, 'Số điện thoại hợp lệ!');
        }
    } catch (error) {
        console.warn('Error validating phone:', error);
    }
}

function validateFullName(input) {
    try {
    const value = input.value.trim();
    
    if (value.length === 0) {
        setFieldValid(input, false, 'Họ và tên không được để trống');
    } else if (value.length < 2) {
        setFieldValid(input, false, 'Họ và tên phải có ít nhất 2 ký tự');
    } else {
        setFieldValid(input, true, 'Họ và tên hợp lệ!');
        }
    } catch (error) {
        console.warn('Error validating full name:', error);
    }
}

function setFieldValid(input, isValid, message) {
    try {
    const feedback = input.parentNode.querySelector('.valid-feedback, .invalid-feedback');
    
    if (isValid) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        if (feedback) {
            feedback.className = 'valid-feedback';
            feedback.textContent = message;
        }
    } else {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        if (feedback) {
            feedback.className = 'invalid-feedback';
            feedback.textContent = message;
        }
        }
    } catch (error) {
        console.warn('Error setting field validation:', error);
    }
}

// Form validation function for register form
function validateRegisterForm() {
    try {
        console.log('Validating register form...');
    
    const form = document.getElementById('registerForm');
    if (!form) {
        console.log('Form not found');
        return true;
    }
    
        let isValid = true;
    
    // Get form elements
    const fullName = document.getElementById('fullName');
    const userName = document.getElementById('userName');
    const email = document.getElementById('email');
    const phone = document.getElementById('phone');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const agreeTerms = document.getElementById('agreeTerms');
    
    // Validate full name
    if (!fullName.value.trim()) {
        setFieldValid(fullName, false, 'Họ và tên không được để trống');
        isValid = false;
    } else if (fullName.value.trim().length < 2) {
        setFieldValid(fullName, false, 'Họ và tên phải có ít nhất 2 ký tự');
        isValid = false;
    } else {
        setFieldValid(fullName, true, 'Họ và tên hợp lệ!');
    }
    
    // Validate username
    if (!userName.value.trim()) {
        setFieldValid(userName, false, 'Tên đăng nhập không được để trống');
        isValid = false;
    } else if (!/^[a-zA-Z0-9_]{3,20}$/.test(userName.value)) {
        setFieldValid(userName, false, 'Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (3-20 ký tự)');
        isValid = false;
    } else {
        setFieldValid(userName, true, 'Tên đăng nhập hợp lệ!');
    }
    
    // Validate email
    if (!email.value.trim()) {
        setFieldValid(email, false, 'Email không được để trống');
        isValid = false;
    } else if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(email.value)) {
        setFieldValid(email, false, 'Email không hợp lệ');
        isValid = false;
    } else {
        setFieldValid(email, true, 'Email hợp lệ!');
    }
    
    // Validate phone
    if (!phone.value.trim()) {
        setFieldValid(phone, false, 'Số điện thoại không được để trống');
        isValid = false;
    } else if (!/^[0-9]{10,11}$/.test(phone.value)) {
        setFieldValid(phone, false, 'Số điện thoại phải có 10-11 chữ số');
        isValid = false;
    } else {
        setFieldValid(phone, true, 'Số điện thoại hợp lệ!');
    }
    
    // Validate password
    if (!password.value) {
        setFieldValid(password, false, 'Mật khẩu không được để trống');
        isValid = false;
    } else if (password.value.length < 8) {
        setFieldValid(password, false, 'Mật khẩu phải có ít nhất 8 ký tự');
        isValid = false;
    } else {
        setFieldValid(password, true, 'Mật khẩu hợp lệ!');
    }
    
    // Validate confirm password
    if (!confirmPassword.value) {
        setFieldValid(confirmPassword, false, 'Vui lòng xác nhận mật khẩu');
        isValid = false;
    } else if (password.value !== confirmPassword.value) {
        setFieldValid(confirmPassword, false, 'Mật khẩu xác nhận không khớp');
        isValid = false;
    } else {
        setFieldValid(confirmPassword, true, 'Mật khẩu xác nhận khớp!');
    }
    
    // Validate terms agreement
    if (!agreeTerms.checked) {
        // Show error for terms checkbox
        const termsError = document.createElement('div');
        termsError.className = 'invalid-feedback d-block';
        termsError.textContent = 'Vui lòng đồng ý với điều khoản sử dụng';
        
        // Remove existing error if any
        const existingError = agreeTerms.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }
        
        agreeTerms.parentNode.appendChild(termsError);
        isValid = false;
    } else {
        // Remove error if terms is checked
        const existingError = agreeTerms.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }
    }
    
        if (isValid) {
    console.log('Form validation passed');
        } else {
            console.log('Form validation failed - but allowing submission');
        }
        
        // Always return true to allow form submission
        // Server-side validation will handle the actual validation
    return true;
    } catch (error) {
        console.warn('Error in form validation:', error);
        return true; // Allow form to submit if validation fails
    }
}

// Search function for search page
function searchForKeyword(keyword) {
    const searchInput = document.getElementById('searchInput');
    const searchForm = document.getElementById('searchForm');
    
    if (searchInput && searchForm) {
        searchInput.value = keyword;
        searchForm.submit();
    }
}
