import React, { useState } from 'react';
import {
  Typography,
  Box,
  Slider,
  FormControl,
  Select,
  MenuItem,
  Button,
  Divider,
  Chip,
  InputLabel,
  Paper
} from '@mui/material';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import DevicesIcon from '@mui/icons-material/Devices';
import PhoneIphoneIcon from '@mui/icons-material/PhoneIphone';
import DataUsageIcon from '@mui/icons-material/DataUsage';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';

const FilterPanel = ({ onFilterChange }) => {
  const [filters, setFilters] = useState({
    category: 'all',
    priceRange: [0, 1000],
    dataAllowance: 'all',
    brand: 'all',
  });

  const handleCategoryChange = (e) => {
    const newFilters = { 
      ...filters, 
      category: e.target.value 
    };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handlePriceChange = (event, newValue) => {
    const newFilters = { 
      ...filters, 
      priceRange: [
        filters.priceRange[0],
        newValue
      ] 
    };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleDataAllowanceChange = (e) => {
    const newFilters = { 
      ...filters, 
      dataAllowance: e.target.value 
    };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleBrandChange = (e) => {
    const newFilters = { 
      ...filters, 
      brand: e.target.value 
    };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const handleReset = () => {
    const resetFilters = {
      category: 'all',
      priceRange: [0, 1000],
      dataAllowance: 'all',
      brand: 'all',
    };
    setFilters(resetFilters);
    onFilterChange(resetFilters);
  };

  // Format price as currency
  const valueLabelFormat = (value) => {
    return `$${value}`;
  };

  return (
    <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <FilterAltIcon color="primary" sx={{ mr: 1.5 }} />
        <Typography variant="h6" fontWeight="medium">
          Filters
        </Typography>
      </Box>
      
      <Divider sx={{ mb: 3 }} />
      
      <Box sx={{ mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
          <DevicesIcon color="primary" fontSize="small" sx={{ mr: 1 }} />
          <Typography variant="subtitle1" fontWeight="medium">
            Category
          </Typography>
        </Box>
        <FormControl fullWidth size="small">
          <Select
            value={filters.category}
            onChange={handleCategoryChange}
            displayEmpty
            sx={{ borderRadius: 2 }}
          >
            <MenuItem value="all">All Products</MenuItem>
            <MenuItem value="tariff">Tariffs</MenuItem>
            <MenuItem value="device">Devices</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      <Box sx={{ mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
          <AttachMoneyIcon color="primary" fontSize="small" sx={{ mr: 1 }} />
          <Typography variant="subtitle1" fontWeight="medium">
            Maximum Price
          </Typography>
        </Box>
        <Box sx={{ px: 1 }}>
          <Typography variant="body1" color="primary" align="right" sx={{ fontWeight: 'medium', mb: 1 }}>
            {valueLabelFormat(filters.priceRange[1])}
          </Typography>
          <Slider
            value={filters.priceRange[1]}
            onChange={handlePriceChange}
            min={0}
            max={1000}
            step={50}
            valueLabelDisplay="auto"
            valueLabelFormat={valueLabelFormat}
            color="primary"
            sx={{ 
              '& .MuiSlider-thumb': {
                width: 16,
                height: 16,
              }
            }}
          />
        </Box>
      </Box>
      
      {(filters.category === 'tariff' || filters.category === 'all') && (
        <Box sx={{ mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
            <DataUsageIcon color="primary" fontSize="small" sx={{ mr: 1 }} />
            <Typography variant="subtitle1" fontWeight="medium">
              Data Allowance
            </Typography>
          </Box>
          <FormControl fullWidth size="small">
            <Select
              value={filters.dataAllowance}
              onChange={handleDataAllowanceChange}
              displayEmpty
              sx={{ borderRadius: 2 }}
            >
              <MenuItem value="all">All Data Plans</MenuItem>
              <MenuItem value="1GB">1GB</MenuItem>
              <MenuItem value="5GB">5GB</MenuItem>
              <MenuItem value="10GB">10GB</MenuItem>
              <MenuItem value="Unlimited">Unlimited</MenuItem>
            </Select>
          </FormControl>
        </Box>
      )}
      
      {(filters.category === 'device' || filters.category === 'all') && (
        <Box sx={{ mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1.5 }}>
            <PhoneIphoneIcon color="primary" fontSize="small" sx={{ mr: 1 }} />
            <Typography variant="subtitle1" fontWeight="medium">
              Brand
            </Typography>
          </Box>
          <FormControl fullWidth size="small">
            <Select
              value={filters.brand}
              onChange={handleBrandChange}
              displayEmpty
              sx={{ borderRadius: 2 }}
            >
              <MenuItem value="all">All Brands</MenuItem>
              <MenuItem value="Apple">Apple</MenuItem>
              <MenuItem value="Samsung">Samsung</MenuItem>
              <MenuItem value="Google">Google</MenuItem>
              <MenuItem value="Huawei">Huawei</MenuItem>
            </Select>
          </FormControl>
        </Box>
      )}
      
      <Box sx={{ mt: 4 }}>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 3 }}>
          {filters.category !== 'all' && (
            <Chip 
              label={`Category: ${filters.category === 'tariff' ? 'Tariffs' : 'Devices'}`}
              size="small"
              color="primary"
              onDelete={() => {
                const newFilters = { ...filters, category: 'all' };
                setFilters(newFilters);
                onFilterChange(newFilters);
              }}
            />
          )}
          {filters.priceRange[1] < 1000 && (
            <Chip 
              label={`Max Price: ${valueLabelFormat(filters.priceRange[1])}`}
              size="small"
              color="primary"
              onDelete={() => {
                const newFilters = { ...filters, priceRange: [0, 1000] };
                setFilters(newFilters);
                onFilterChange(newFilters);
              }}
            />
          )}
          {filters.dataAllowance !== 'all' && (
            <Chip 
              label={`Data: ${filters.dataAllowance}`}
              size="small"
              color="primary"
              onDelete={() => {
                const newFilters = { ...filters, dataAllowance: 'all' };
                setFilters(newFilters);
                onFilterChange(newFilters);
              }}
            />
          )}
          {filters.brand !== 'all' && (
            <Chip 
              label={`Brand: ${filters.brand}`}
              size="small"
              color="primary"
              onDelete={() => {
                const newFilters = { ...filters, brand: 'all' };
                setFilters(newFilters);
                onFilterChange(newFilters);
              }}
            />
          )}
        </Box>
        
        <Button
          variant="outlined"
          color="primary"
          fullWidth
          onClick={handleReset}
          sx={{ borderRadius: 2 }}
        >
          Reset All Filters
        </Button>
      </Box>
    </Paper>
  );
};

export default FilterPanel; 