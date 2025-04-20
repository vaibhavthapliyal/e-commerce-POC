import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { cartApi } from '../api/api';

const initialState = {
  items: [],
  total: 0,
  loading: false,
  error: null,
};

// Async thunks for cart operations
export const fetchCart = createAsyncThunk(
  'cart/fetchCart',
  async (_, { rejectWithValue }) => {
    try {
      const response = await cartApi.getCart();
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const addCartItem = createAsyncThunk(
  'cart/addItem',
  async ({ productId, quantity }, { rejectWithValue }) => {
    try {
      const response = await cartApi.addToCart(productId, quantity);
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const updateCartItem = createAsyncThunk(
  'cart/updateItem',
  async ({ productId, quantity }, { rejectWithValue }) => {
    try {
      const response = await cartApi.updateCart(productId, quantity);
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const removeCartItem = createAsyncThunk(
  'cart/removeItem',
  async (productId, { rejectWithValue }) => {
    try {
      await cartApi.removeFromCart(productId);
      return productId;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const clearCartItems = createAsyncThunk(
  'cart/clearCart',
  async (_, { rejectWithValue }) => {
    try {
      await cartApi.clearCart();
      return true;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    // Local reducers for offline functionality or optimistic updates
    addToCart: (state, action) => {
      const { product, quantity = 1 } = action.payload;
      const existingItem = state.items.find(item => item.id === product.id);
      
      if (existingItem) {
        existingItem.quantity += quantity;
      } else {
        state.items.push({ ...product, quantity });
      }
      
      state.total = calculateTotal(state.items);
    },
    updateQuantity: (state, action) => {
      const { id, quantity } = action.payload;
      const item = state.items.find(item => item.id === id);
      
      if (item) {
        item.quantity = quantity;
      }
      
      state.total = calculateTotal(state.items);
    },
    removeFromCart: (state, action) => {
      state.items = state.items.filter(item => item.id !== action.payload);
      state.total = calculateTotal(state.items);
    },
    clearCart: (state) => {
      state.items = [];
      state.total = 0;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch cart
      .addCase(fetchCart.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.items || [];
        state.total = action.payload.total || calculateTotal(action.payload.items);
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch cart';
        
        // If API fails, use local storage as fallback
        const storedCart = localStorage.getItem('cart');
        if (storedCart) {
          try {
            const parsedCart = JSON.parse(storedCart);
            state.items = parsedCart.items || [];
            state.total = parsedCart.total || calculateTotal(parsedCart.items);
          } catch (e) {
            console.error('Error parsing stored cart:', e);
          }
        }
      })
      
      // Add item
      .addCase(addCartItem.fulfilled, (state, action) => {
        state.items = action.payload.items || state.items;
        state.total = action.payload.total || calculateTotal(state.items);
        saveCartToLocalStorage(state);
      })
      
      // Update item
      .addCase(updateCartItem.fulfilled, (state, action) => {
        state.items = action.payload.items || state.items;
        state.total = action.payload.total || calculateTotal(state.items);
        saveCartToLocalStorage(state);
      })
      
      // Remove item
      .addCase(removeCartItem.fulfilled, (state, action) => {
        state.items = state.items.filter(item => item.id !== action.payload);
        state.total = calculateTotal(state.items);
        saveCartToLocalStorage(state);
      })
      
      // Clear cart
      .addCase(clearCartItems.fulfilled, (state) => {
        state.items = [];
        state.total = 0;
        localStorage.removeItem('cart');
      });
  },
});

// Helper function to calculate total
const calculateTotal = (items) => {
  return items.reduce((sum, item) => {
    const itemPrice = item.discountPercentage 
      ? item.price * (1 - (item.discountPercentage / 100))
      : item.price;
    return sum + (itemPrice * item.quantity);
  }, 0);
};

// Helper function to save cart to local storage for offline use
const saveCartToLocalStorage = (state) => {
  try {
    localStorage.setItem('cart', JSON.stringify({
      items: state.items,
      total: state.total
    }));
  } catch (e) {
    console.error('Error saving cart to local storage:', e);
  }
};

export const { addToCart, updateQuantity, removeFromCart, clearCart } = cartSlice.actions;

export default cartSlice.reducer; 