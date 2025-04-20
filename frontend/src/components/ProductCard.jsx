import React from 'react';
import FlashDiscountTimer from './FlashDiscountTimer';
import {
  Card,
  CardActions,
  CardContent,
  CardMedia,
  Button,
  Typography,
  Box,
  Chip,
} from '@mui/material';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';

const ProductCard = ({ product, onAddToCart }) => {
  const hasDiscount = product.discountPercentage && product.discountExpiry;
  
  const handleAddToCart = () => {
    // Call the provided callback
    onAddToCart(product, 1);
    
    // Request notification permission on first add
    if (Notification.permission === 'default') {
      try {
        Notification.requestPermission().then(permission => {
          // This would typically call an API to update user preferences
          console.log('Notification permission:', permission);
        });
      } catch (error) {
        console.error('Error requesting notification permission:', error);
      }
    }
  };
  
  // Calculate the discounted price if applicable
  const finalPrice = hasDiscount 
    ? product.price * (1 - (product.discountPercentage / 100)) 
    : product.price;

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="200"
          image={product.imageUrl || 'https://via.placeholder.com/300x200'}
          alt={product.name}
        />
        {hasDiscount && (
          <Chip
            label={`${product.discountPercentage}% OFF`}
            color="secondary"
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
              fontWeight: 'bold',
            }}
          />
        )}
      </Box>
      
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography gutterBottom variant="h6" component="div">
          {product.name}
        </Typography>
        
        <Typography 
          variant="body2" 
          color="text.secondary" 
          sx={{ 
            mb: 2, 
            height: '3em', 
            overflow: 'hidden',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical'
          }}
        >
          {product.description}
        </Typography>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Box>
            {hasDiscount ? (
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Typography 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ textDecoration: 'line-through', mr: 1 }}
                >
                  ${product.price.toFixed(2)}
                </Typography>
                <Typography variant="h6" color="secondary">
                  ${finalPrice.toFixed(2)}
                </Typography>
              </Box>
            ) : (
              <Typography variant="h6">${product.price.toFixed(2)}</Typography>
            )}
          </Box>
          
          <Chip 
            label={product.type === 'tariff' ? 'Plan' : 'Device'}
            size="small"
            variant="outlined"
          />
        </Box>
        
        {hasDiscount && (
          <Box sx={{ mb: 2 }}>
            <FlashDiscountTimer expiryTime={product.discountExpiry} />
          </Box>
        )}
      </CardContent>
      
      <CardActions>
        <Button 
          fullWidth 
          variant="contained" 
          color="secondary"
          startIcon={<AddShoppingCartIcon />}
          onClick={handleAddToCart}
        >
          Add to Cart
        </Button>
      </CardActions>
    </Card>
  );
};

export default ProductCard; 