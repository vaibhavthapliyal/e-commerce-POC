import axios from 'axios';

// Create axios instances for each microservice
const createApiClient = (servicePath) => {
  const client = axios.create({
    baseURL: servicePath,
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 second timeout
  });

  // Add response interceptor for better error handling
  client.interceptors.response.use(
    response => response,
    error => {
      console.error(`API Error (${servicePath}):`, error);
      
      // Enhance error object with additional information
      if (error.response) {
        // Server responded with a status code outside of 2xx range
        error.userMessage = `Server error: ${error.response.status} ${error.response.statusText}`;
        console.error('Error response data:', error.response.data);
      } else if (error.request) {
        // Request was made but no response received
        error.userMessage = 'No response from server. Check your connection and try again.';
      } else {
        // Error in setting up the request
        error.userMessage = 'Failed to make request. Please try again.';
      }
      
      return Promise.reject(error);
    }
  );

  return client;
};

// API clients for each microservice
const productClient = createApiClient('/api/products');
const productProxyClient = createApiClient('/api/product-proxy');
const cartClient = createApiClient('/api/cart');
const discountClient = createApiClient('/api/discounts');
const paymentClient = createApiClient('/api/payments');
const notificationClient = createApiClient('/api/notifications');
const orderClient = createApiClient('/api/orders');

// Product Service API
export const productApi = {
  getProducts: async (filters = {}, page = 0, sort = 'popularity') => {
    try {
      // Try the standard API route first
      const response = await productClient.get('/', { 
        params: { ...filters, page, sort } 
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching products through main route, trying fallback:', error);
      
      try {
        // If the standard route fails, try the proxy endpoint
        const proxyResponse = await productProxyClient.get('/', {
          params: { ...filters, page, sort }
        });
        console.log('Successfully retrieved products through proxy route');
        return proxyResponse.data;
      } catch (proxyError) {
        console.error('Both product API routes failed:', proxyError);
        throw proxyError;
      }
    }
  },
  
  getProductById: async (id) => {
    try {
      const response = await productClient.get(`/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching product ${id} through main route, trying fallback:`, error);
      
      try {
        const proxyResponse = await productProxyClient.get(`/${id}`);
        return proxyResponse.data;
      } catch (proxyError) {
        console.error(`Both product API routes failed for ID ${id}:`, proxyError);
        throw proxyError;
      }
    }
  },
};

// Helper function to get cart from localStorage
const getLocalCart = () => {
  try {
    const localCart = localStorage.getItem('cart');
    if (localCart) {
      return JSON.parse(localCart);
    }
  } catch (e) {
    console.error('Error retrieving cart from localStorage:', e);
  }
  return { items: [], total: 0 };
};

// Helper function to save cart to localStorage
const saveLocalCart = (cart) => {
  try {
    localStorage.setItem('cart', JSON.stringify(cart));
  } catch (e) {
    console.error('Error saving cart to localStorage:', e);
  }
};

// Cart Service API
export const cartApi = {
  addToCart: async (productId, quantity) => {
    try {
      const response = await cartClient.post('/add', { productId, quantity });
      return response.data;
    } catch (error) {
      console.error('Error adding to cart:', error);
      
      // Implement local fallback solution
      console.log('Using local cart fallback for add operation');
      
      // Get product details
      let product;
      try {
        product = await productApi.getProductById(productId);
      } catch (err) {
        // If we can't get the product, use a minimal version with just the ID
        product = { id: productId, name: "Product ID " + productId, price: 0 };
        console.warn('Using minimal product info due to API error:', err);
      }
      
      // Use local storage as fallback
      const localCart = getLocalCart();
      
      // Check if product already exists in cart
      const existingItemIndex = localCart.items.findIndex(item => item.id === productId);
      
      if (existingItemIndex >= 0) {
        // Update existing item
        localCart.items[existingItemIndex].quantity += quantity;
      } else {
        // Add new item
        localCart.items.push({
          ...product,
          quantity: quantity
        });
      }
      
      // Calculate new total
      localCart.total = localCart.items.reduce((sum, item) => {
        const price = item.discountPercentage 
          ? item.price * (1 - (item.discountPercentage / 100)) 
          : item.price;
        return sum + (price * item.quantity);
      }, 0);
      
      // Save updated cart
      saveLocalCart(localCart);
      
      // Return the local cart data in the same format as the API would
      return localCart;
    }
  },
  
  getCart: async () => {
    try {
      const response = await cartClient.get('/');
      return response.data;
    } catch (error) {
      console.error('Error fetching cart:', error);
      
      // Return local cart as fallback
      console.log('Using local cart fallback for get operation');
      return getLocalCart();
    }
  },
  
  updateCart: async (productId, quantity) => {
    try {
      const response = await cartClient.put('/update', { productId, quantity });
      return response.data;
    } catch (error) {
      console.error('Error updating cart:', error);
      
      // Implement local fallback
      console.log('Using local cart fallback for update operation');
      const localCart = getLocalCart();
      
      // Find and update the item
      const itemIndex = localCart.items.findIndex(item => item.id === productId);
      
      if (itemIndex >= 0) {
        if (quantity <= 0) {
          // Remove item if quantity is 0 or negative
          localCart.items.splice(itemIndex, 1);
        } else {
          // Update quantity
          localCart.items[itemIndex].quantity = quantity;
        }
        
        // Recalculate total
        localCart.total = localCart.items.reduce((sum, item) => {
          const price = item.discountPercentage 
            ? item.price * (1 - (item.discountPercentage / 100)) 
            : item.price;
          return sum + (price * item.quantity);
        }, 0);
        
        // Save updated cart
        saveLocalCart(localCart);
      }
      
      return localCart;
    }
  },
  
  removeFromCart: async (productId) => {
    try {
      const response = await cartClient.delete('/remove', { 
        data: { productId } 
      });
      return response.data;
    } catch (error) {
      console.error('Error removing from cart:', error);
      
      // Implement local fallback
      console.log('Using local cart fallback for remove operation');
      const localCart = getLocalCart();
      
      // Remove the item
      localCart.items = localCart.items.filter(item => item.id !== productId);
      
      // Recalculate total
      localCart.total = localCart.items.reduce((sum, item) => {
        const price = item.discountPercentage 
          ? item.price * (1 - (item.discountPercentage / 100)) 
          : item.price;
        return sum + (price * item.quantity);
      }, 0);
      
      // Save updated cart
      saveLocalCart(localCart);
      
      return localCart;
    }
  },

  clearCart: async () => {
    try {
      const response = await cartClient.delete('/');
      return response.data;
    } catch (error) {
      console.error('Error clearing cart:', error);
      
      // Implement local fallback
      console.log('Using local cart fallback for clear operation');
      localStorage.removeItem('cart');
      
      return { items: [], total: 0 };
    }
  },
};

// Discount Service API
export const discountApi = {
  getDiscounts: async () => {
    try {
      const response = await discountClient.get('/');
      return response.data;
    } catch (error) {
      console.error('Error fetching discounts:', error);
      throw error;
    }
  },
  
  getDiscountsByProductId: async (productId) => {
    try {
      const response = await discountClient.get(`/product/${productId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching discounts for product ${productId}:`, error);
      throw error;
    }
  },
};

// Payment Service API
export const paymentApi = {
  createPaymentIntent: async (paymentDetails) => {
    try {
      const response = await paymentClient.post('/', paymentDetails);
      return response.data;
    } catch (error) {
      console.error('Error creating payment intent:', error);
      throw error;
    }
  },
  
  confirmPayment: async (orderData) => {
    try {
      const response = await paymentClient.post('/', orderData);
      return response.data;
    } catch (error) {
      console.error('Error confirming payment:', error);
      throw error;
    }
  },
  
  getPaymentById: async (paymentId) => {
    try {
      const response = await paymentClient.get(`/${paymentId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching payment ${paymentId}:`, error);
      throw error;
    }
  },
};

// Order Service API (if this exists in the backend)
export const orderApi = {
  createOrder: async (orderData) => {
    try {
      const response = await orderClient.post('/', orderData);
      return response.data;
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  },
  
  getOrderById: async (orderId) => {
    try {
      const response = await orderClient.get(`/${orderId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching order ${orderId}:`, error);
      throw error;
    }
  },
  
  getOrdersByCustomerEmail: async (email) => {
    try {
      const response = await orderClient.get(`/customer/${email}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching orders for customer ${email}:`, error);
      throw error;
    }
  },
  
  updateOrderStatus: async (orderId, status) => {
    try {
      const response = await orderClient.put(`/${orderId}`, { status });
      return response.data;
    } catch (error) {
      console.error(`Error updating order ${orderId} status:`, error);
      throw error;
    }
  },
};

// Notification Service API
export const notificationApi = {
  sendNotification: async (notificationData) => {
    try {
      const response = await notificationClient.post('/', notificationData);
      return response.data;
    } catch (error) {
      console.error('Error sending notification:', error);
      throw error;
    }
  },
  
  getNotifications: async () => {
    try {
      const response = await notificationClient.get('/');
      return response.data;
    } catch (error) {
      console.error('Error fetching notifications:', error);
      throw error;
    }
  },
  
  getNotificationById: async (id) => {
    try {
      const response = await notificationClient.get(`/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching notification ${id}:`, error);
      throw error;
    }
  },
  
  getNotificationsByEmail: async (email) => {
    try {
      const response = await notificationClient.get(`/email/${email}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching notifications for ${email}:`, error);
      throw error;
    }
  },
};

export default {
  product: productClient,
  productProxy: productProxyClient,
  cart: cartClient,
  discount: discountClient,
  payment: paymentClient,
  order: orderClient,
  notification: notificationClient
}; 