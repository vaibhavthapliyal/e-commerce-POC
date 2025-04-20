import React from 'react';
import { 
  FormControl, 
  Select, 
  MenuItem, 
  InputLabel, 
  Box, 
  Typography
} from '@mui/material';
import SortIcon from '@mui/icons-material/Sort';

const SortDropdown = ({ onSortChange, currentSort }) => {
  const handleChange = (e) => {
    onSortChange(e.target.value);
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mr: 1.5 }}>
        <SortIcon color="primary" fontSize="small" sx={{ mr: 0.75 }} />
        <Typography variant="body2" fontWeight="medium">
          Sort by:
        </Typography>
      </Box>
      <FormControl size="small" sx={{ minWidth: 170 }}>
        <Select
          value={currentSort}
          onChange={handleChange}
          displayEmpty
          sx={{ borderRadius: 2 }}
        >
          <MenuItem value="popularity">Popularity</MenuItem>
          <MenuItem value="price-low-high">Price: Low to High</MenuItem>
          <MenuItem value="price-high-low">Price: High to Low</MenuItem>
          <MenuItem value="newest">Newest</MenuItem>
        </Select>
      </FormControl>
    </Box>
  );
};

export default SortDropdown; 