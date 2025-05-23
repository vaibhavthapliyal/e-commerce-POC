import React, { useState, useEffect, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { productApi, discountApi } from '../api/api';
import { addToCart, addCartItem } from '../store/cartSlice';
import ProductCard from '../components/ProductCard';
import FilterPanel from '../components/FilterPanel';
import SortDropdown from '../components/SortDropdown';
import {
  Container,
  Typography,
  Grid,
  Box,
  Paper,
  Button,
  Alert,
  CircularProgress,
  Pagination,
  Divider,
  Chip
} from '@mui/material';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';

const ProductListing = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    category: 'all',
    priceRange: [0, 1000],
    dataAllowance: 'all',
    brand: 'all',
  });
  const [sort, setSort] = useState('popularity');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // Function to retry API calls
  const retryApiCall = async (apiCall, maxRetries = 3) => {
    let lastError;
    for (let attempt = 0; attempt < maxRetries; attempt++) {
      try {
        return await apiCall();
      } catch (err) {
        console.log(`Attempt ${attempt + 1} failed:`, err);
        lastError = err;
        // Wait a bit before retrying (exponential backoff)
        await new Promise(resolve => setTimeout(resolve, 1000 * Math.pow(2, attempt)));
      }
    }
    throw lastError; // If all retries fail, throw the last error
  };

  // Memoized fetch function to avoid unnecessary re-renders
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      // Convert filters to API format
      const apiFilters = {
        type: filters.category !== 'all' ? filters.category : undefined,
        maxPrice: filters.priceRange[1],
        dataAllowance: filters.dataAllowance !== 'all' ? filters.dataAllowance : undefined,
        brand: filters.brand !== 'all' ? filters.brand : undefined,
      };
      
      // Use the retry function for product API call
      const response = await retryApiCall(() => 
        productApi.getProducts(apiFilters, currentPage - 1, sort)
      );
      console.log('API Response:', response);
      
      // Get active discounts with retry
      let discounts = [];
      try {
        discounts = await retryApiCall(() => discountApi.getDiscounts());
        console.log('Discounts:', discounts);
      } catch (discountErr) {
        console.error('Could not fetch discounts, proceeding without discount data:', discountErr);
      }
      
      // Apply discounts to products
      const productsWithDiscounts = response.products.map(product => {
        const discount = discounts.find(d => d.productId === product.id);
        if (discount) {
          return {
            ...product,
            discountPercentage: discount.percentage,
            discountExpiry: discount.expiryTime,
          };
        }
        return product;
      });
      
      setProducts(productsWithDiscounts);
      setTotalPages(response.totalPages || 1);
      setError(null);
    } catch (err) {
      console.error('Error fetching products:', err);
      setError('Failed to load products. Please try again later.');
      
      // Mock data for development - in production, you'd handle errors differently
      setProducts([
        {
          id: 1,
          name: 'Unlimited Data Plan',
          description: 'Unlimited data, calls and texts. Perfect for heavy users.',
          price: 49.99,
          type: 'tariff',
          dataAllowance: 'Unlimited',
          discountPercentage: 10,
          discountExpiry: new Date(Date.now() + 3600000).toISOString(), // 1 hour from now
        },
        {
          id: 2,
          name: 'iPhone 13 Pro',
          description: 'Latest iPhone with A15 Bionic chip and Pro camera system.',
          price: 999.99,
          type: 'device',
          brand: 'Apple',
        },
        {
          id: 3,
          name: '5G Basic Plan',
          description: '10GB data with 5G speeds, unlimited calls and texts.',
          price: 29.99,
          type: 'tariff',
          dataAllowance: '10GB',
        },
        {
          id: 4,
          name: 'Samsung Galaxy S21',
          description: 'Powerful Android phone with amazing camera and 5G.',
          price: 799.99,
          type: 'device',
          brand: 'Samsung',
          discountPercentage: 15,
          discountExpiry: new Date(Date.now() + 1800000).toISOString(), // 30 minutes from now
        },
      ]);
    } finally {
      setLoading(false);
    }
  }, [filters, sort, currentPage]);

  // Fetch products when dependencies change
  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setCurrentPage(1); // Reset to first page when filters change
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setCurrentPage(1); // Reset to first page when sort changes
  };

  const handlePageChange = (event, page) => {
    setCurrentPage(page);
    window.scrollTo(0, 0);
  };

  const handleAddToCart = (product, quantity = 1) => {
    // Local state update for immediate UI feedback
    dispatch(addToCart({ product, quantity }));
    
    // API call to update the cart on the server
    dispatch(addCartItem({ productId: product.id, quantity }));
    
    // Navigate to cart page
    navigate('/cart');
  };

  const handleRetry = () => {
    setError(null);
    fetchProducts();
  };

  return (
    <Box sx={{ backgroundColor: '#f8f9fa', minHeight: '100vh', py: 6 }}>
      <Container maxWidth="lg">
        <Box sx={{ mb: 5, textAlign: 'center' }}>
          <Typography 
            variant="h4" 
            component="h1" 
            sx={{ 
              fontWeight: 'bold',
              color: 'primary.main',
              mb: 2,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <ShoppingBagIcon sx={{ mr: 1, fontSize: 36 }} />
            Telecom Products
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 600, mx: 'auto' }}>
            Browse our selection of premium telecom products, plans, and devices
          </Typography>
        </Box>
        
        <Grid container spacing={4}>
          {/* Filters (left sidebar) */}
          <Grid item xs={12} md={3}>
            <FilterPanel onFilterChange={handleFilterChange} />
          </Grid>
          
          {/* Main content area */}
          <Grid item xs={12} md={9}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 2, mb: 3 }}>
              {/* Sort dropdown and product count */}
              <Box 
                sx={{ 
                  display: 'flex', 
                  flexDirection: { xs: 'column', sm: 'row' }, 
                  justifyContent: 'space-between',
                  alignItems: { xs: 'flex-start', sm: 'center' },
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', mb: { xs: 2, sm: 0 } }}>
                  <Typography variant="body1" fontWeight="medium" sx={{ mr: 1 }}>
                    Showing:
                  </Typography>
                  <Chip 
                    label={`${products.length} products`} 
                    color="primary" 
                    size="small" 
                    variant="outlined"
                  />
                </Box>
                <SortDropdown onSortChange={handleSortChange} currentSort={sort} />
              </Box>
            </Paper>
            
            {/* Error message */}
            {error && (
              <Alert 
                severity="error" 
                sx={{ mb: 3 }}
                action={
                  <Button color="inherit" size="small" onClick={handleRetry}>
                    Retry
                  </Button>
                }
              >
                {error}
              </Alert>
            )}
            
            {/* Loading indicator */}
            {loading && (
              <Box sx={{ display: 'flex', justifyContent: 'center', my: 8 }}>
                <CircularProgress color="primary" />
              </Box>
            )}
            
            {/* Products grid */}
            {!loading && products.length === 0 ? (
              <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 2 }}>
                <Typography variant="body1" color="text.secondary">
                  No products match your filters. Try adjusting your criteria.
                </Typography>
              </Paper>
            ) : (
              <Grid container spacing={3}>
                {products.map(product => (
                  <Grid item key={product.id} xs={12} sm={6} md={4}>
                    <ProductCard product={product} onAddToCart={() => handleAddToCart(product)} />
                  </Grid>
                ))}
              </Grid>
            )}
            
            {/* Pagination */}
            {totalPages > 1 && (
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <Pagination 
                  count={totalPages} 
                  page={currentPage}
                  onChange={handlePageChange}
                  color="primary"
                  size="large"
                  showFirstButton
                  showLastButton
                />
              </Box>
            )}
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default ProductListing; 