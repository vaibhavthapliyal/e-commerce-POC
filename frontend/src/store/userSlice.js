import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { notificationApi } from '../api/api';

const initialState = {
  notificationPermission: 'default',
  email: '',
  loading: false,
  error: null,
  checkoutInfo: {
    name: '',
    email: '',
    phone: '',
    address: '',
  },
};

// Async thunk for updating notification permission
export const updateNotificationPermission = createAsyncThunk(
  'user/updateNotificationPermission',
  async ({ email, status }, { rejectWithValue }) => {
    try {
      const response = await notificationApi.updatePermission(email, status);
      return { email, status, response };
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

// Async thunk for sending a test notification
export const sendTestNotification = createAsyncThunk(
  'user/sendTestNotification',
  async (email, { rejectWithValue }) => {
    try {
      const response = await notificationApi.sendTestNotification(email);
      return response;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setNotificationPermission: (state, action) => {
      state.notificationPermission = action.payload;
    },
    setUserEmail: (state, action) => {
      state.email = action.payload;
    },
    updateCheckoutInfo: (state, action) => {
      state.checkoutInfo = {
        ...state.checkoutInfo,
        ...action.payload,
      };
      
      // Update email in the user state if it's updated in checkout
      if (action.payload.email) {
        state.email = action.payload.email;
      }
    },
    clearCheckoutInfo: (state) => {
      state.checkoutInfo = initialState.checkoutInfo;
    },
  },
  extraReducers: (builder) => {
    builder
      // Update notification permission
      .addCase(updateNotificationPermission.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateNotificationPermission.fulfilled, (state, action) => {
        state.loading = false;
        state.notificationPermission = action.payload.status;
        state.email = action.payload.email;
      })
      .addCase(updateNotificationPermission.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update notification settings';
      })
      
      // Send test notification
      .addCase(sendTestNotification.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(sendTestNotification.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(sendTestNotification.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to send test notification';
      });
  },
});

export const { 
  setNotificationPermission, 
  setUserEmail,
  updateCheckoutInfo, 
  clearCheckoutInfo 
} = userSlice.actions;

export default userSlice.reducer; 