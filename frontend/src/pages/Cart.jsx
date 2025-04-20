import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { fetchCart } from '../store/cartSlice';
import { notificationApi } from '../api/api';
import CartItem from '../components/CartItem';
import NotificationPrompt from '../components/NotificationPrompt';
import ExitIntentModal from '../components/ExitIntentModal';
import {
  Container,
  Typography,
  Grid,
  Paper,
  Button,
  Box,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

const Cart = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { items: cartItems, total: cartTotal, loading, error } = useSelector(state => state.cart);
  const { notificationPermission, email } = useSelector(state => state.user);
  
  const [showNotificationPrompt, setShowNotificationPrompt] = useState(false);
  const [showExitIntent, setShowExitIntent] = useState(false);
  const [exitIntentShown, setExitIntentShown] = useState(false);

  // Fetch cart on component mount
  useEffect(() => {
    dispatch(fetchCart());
  }, [dispatch]);

  // Check if notification prompt should be shown
  useEffect(() => {
    // Only show if permission is default and user hasn't explicitly declined
    if (
      notificationPermission === 'default' && 
      cartItems.length > 0 && 
      !sessionStorage.getItem('notificationPromptShown')
    ) {
      setShowNotificationPrompt(true);
      sessionStorage.setItem('notificationPromptShown', 'true');
    }
  }, [notificationPermission, cartItems]);

  // Set up exit intent detection
  useEffect(() => {
    if (cartItems.length === 0 || exitIntentShown) return;

    const handleMouseLeave = (event) => {
      // Only trigger if mouse leaves at the top of the page
      if (
        event.clientY <= 0 && 
        !exitIntentShown && 
        !sessionStorage.getItem('exitIntentShown')
      ) {
        setShowExitIntent(true);
        setExitIntentShown(true);
        sessionStorage.setItem('exitIntentShown', 'true');
      }
    };

    document.addEventListener('mouseleave', handleMouseLeave);

    return () => {
      document.removeEventListener('mouseleave', handleMouseLeave);
    };
  }, [cartItems, exitIntentShown]);

  const handleCloseNotificationPrompt = async (permit) => {
    setShowNotificationPrompt(false);
    
    if (permit && email) {
      try {
        await notificationApi.sendNotification({
          recipientEmail: email,
          subject: 'Notification Permission Update',
          content: 'You have successfully subscribed to notifications',
          type: 'SYSTEM'
        });
      } catch (error) {
        console.error('Failed to update notification permission:', error);
      }
    }
  };

  const handleCloseExitIntent = () => {
    setShowExitIntent(false);
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" fontWeight="bold" mb={4}>
        Your Cart
      </Typography>
      
      {loading ? (
        <Box display="flex" justifyContent="center" my={8}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error" sx={{ mb: 4 }}>
          {error}
        </Alert>
      ) : cartItems.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }} elevation={2}>
          <ShoppingCartIcon sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
          <Typography color="text.secondary" paragraph>
            Your cart is empty. Start shopping now!
          </Typography>
          <Button 
            component={RouterLink} 
            to="/"
            variant="contained" 
            color="primary"
            size="large"
          >
            Browse Products
          </Button>
        </Paper>
      ) : (
        <Grid container spacing={4}>
          {/* Cart items (left section) */}
          <Grid item xs={12} md={8}>
            <Paper sx={{ p: 3, mb: { xs: 4, md: 0 } }} elevation={2}>
              <Typography variant="h6" fontWeight="medium" mb={2}>
                Items ({cartItems.reduce((acc, item) => acc + item.quantity, 0)})
              </Typography>
              
              <Divider sx={{ mb: 2 }} />
              
              <Box>
                {cartItems.map(item => (
                  <Box key={item.id}>
                    <CartItem item={item} />
                    <Divider sx={{ my: 2 }} />
                  </Box>
                ))}
              </Box>
            </Paper>
          </Grid>
          
          {/* Order summary (right section) */}
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, position: 'sticky', top: 24 }} elevation={2}>
              <Typography variant="h6" fontWeight="medium" mb={2}>
                Order Summary
              </Typography>
              
              <Divider sx={{ mb: 2 }} />
              
              <Box mb={3}>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body1" color="text.secondary">
                    Subtotal:
                  </Typography>
                  <Typography variant="body1">
                    ${cartTotal.toFixed(2)}
                  </Typography>
                </Box>
                
                <Box display="flex" justifyContent="space-between" mt={2}>
                  <Typography variant="h6">
                    Total:
                  </Typography>
                  <Typography variant="h6" color="secondary" fontWeight="bold">
                    ${cartTotal.toFixed(2)}
                  </Typography>
                </Box>
              </Box>
              
              <Button
                variant="contained"
                color="secondary"
                size="large"
                fullWidth
                onClick={handleCheckout}
              >
                Proceed to Checkout
              </Button>
              
              <Typography variant="caption" color="text.secondary" align="center" display="block" mt={2}>
                Secure checkout powered by Stripe
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      )}
      
      {/* Notification permission prompt */}
      {showNotificationPrompt && (
        <NotificationPrompt onClose={handleCloseNotificationPrompt} />
      )}
      
      {/* Exit intent modal */}
      {showExitIntent && (
        <ExitIntentModal onClose={handleCloseExitIntent} />
      )}
    </Container>
  );
};

export default Cart; 