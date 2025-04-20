import React from 'react';
import { useDispatch } from 'react-redux';
import { updateQuantity, removeFromCart, updateCartItem, removeCartItem } from '../store/cartSlice';
import FlashDiscountTimer from './FlashDiscountTimer';
import { 
  Box, 
  Typography, 
  Select, 
  MenuItem, 
  IconButton, 
  FormControl, 
  InputLabel,
  Grid,
  Button
} from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';

const CartItem = ({ item }) => {
  const dispatch = useDispatch();
  const hasDiscount = item.discountPercentage && item.discountExpiry;
  
  const finalPrice = hasDiscount 
    ? item.price * (1 - (item.discountPercentage / 100)) 
    : item.price;
  
  const subtotal = finalPrice * item.quantity;

  const handleQuantityChange = (e) => {
    const quantity = parseInt(e.target.value, 10);
    if (quantity >= 1) {
      // Local state update for immediate UI response
      dispatch(updateQuantity({ id: item.id, quantity }));
      
      // API call to update cart on the server
      dispatch(updateCartItem({ productId: item.id, quantity }));
    }
  };

  const handleRemove = () => {
    // Local state update for immediate UI response
    dispatch(removeFromCart(item.id));
    
    // API call to remove from cart on the server
    dispatch(removeCartItem(item.id));
  };

  return (
    <Grid container spacing={2} alignItems="center">
      {/* Product Image */}
      <Grid item xs={12} sm={3} md={2}>
        <Box 
          component="img"
          src={item.imageUrl || 'https://via.placeholder.com/96'}
          alt={item.name}
          sx={{ 
            width: '100%', 
            height: 80, 
            objectFit: 'cover', 
            borderRadius: 1,
            boxShadow: 1
          }}
        />
      </Grid>
      
      {/* Product Details */}
      <Grid item xs={12} sm={9} md={10}>
        <Box>
          {/* Product Name and Price */}
          <Box display="flex" justifyContent="space-between" mb={1} alignItems="flex-start">
            <Typography variant="subtitle1" fontWeight="medium">
              {item.name}
            </Typography>
            
            <Box textAlign="right">
              {hasDiscount ? (
                <Box>
                  <Typography 
                    variant="body2" 
                    color="text.secondary" 
                    sx={{ textDecoration: 'line-through' }}
                    component="span"
                    mr={1}
                  >
                    ${item.price.toFixed(2)}
                  </Typography>
                  <Typography variant="subtitle2" color="secondary" fontWeight="bold" component="span">
                    ${finalPrice.toFixed(2)}
                  </Typography>
                </Box>
              ) : (
                <Typography variant="subtitle2" fontWeight="bold">
                  ${item.price.toFixed(2)}
                </Typography>
              )}
            </Box>
          </Box>
          
          {/* Product Type */}
          <Typography variant="body2" color="text.secondary" mb={hasDiscount ? 1 : 2}>
            {item.type === 'tariff' ? 'Plan' : 'Device'}
          </Typography>
          
          {/* Discount Timer */}
          {hasDiscount && (
            <Box mb={2}>
              <FlashDiscountTimer expiryTime={item.discountExpiry} />
            </Box>
          )}
          
          {/* Quantity and Subtotal */}
          <Box 
            display="flex" 
            justifyContent="space-between" 
            alignItems="center"
          >
            <Box display="flex" alignItems="center">
              <FormControl size="small" sx={{ width: 80, mr: 2 }}>
                <InputLabel id={`quantity-label-${item.id}`}>Qty</InputLabel>
                <Select
                  labelId={`quantity-label-${item.id}`}
                  id={`quantity-${item.id}`}
                  value={item.quantity}
                  label="Qty"
                  onChange={handleQuantityChange}
                >
                  {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(num => (
                    <MenuItem key={num} value={num}>{num}</MenuItem>
                  ))}
                </Select>
              </FormControl>
              
              <Button
                startIcon={<DeleteOutlineIcon />}
                onClick={handleRemove}
                color="error"
                size="small"
                variant="text"
              >
                Remove
              </Button>
            </Box>
            
            <Typography variant="subtitle1" fontWeight="bold">
              ${subtotal.toFixed(2)}
            </Typography>
          </Box>
        </Box>
      </Grid>
    </Grid>
  );
};

export default CartItem; 