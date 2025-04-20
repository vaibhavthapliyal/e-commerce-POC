import React, { useState } from 'react';

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

  const handlePriceChange = (e) => {
    const newFilters = { 
      ...filters, 
      priceRange: [
        filters.priceRange[0],
        parseInt(e.target.value, 10)
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

  return (
    <div className="bg-white p-4 rounded-lg shadow-md">
      <h2 className="text-lg font-semibold mb-4">Filters</h2>
      
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Category
        </label>
        <select
          value={filters.category}
          onChange={handleCategoryChange}
          className="block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary focus:border-primary"
        >
          <option value="all">All Products</option>
          <option value="tariff">Tariffs</option>
          <option value="device">Devices</option>
        </select>
      </div>
      
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Max Price: ${filters.priceRange[1]}
        </label>
        <input
          type="range"
          min="0"
          max="1000"
          step="50"
          value={filters.priceRange[1]}
          onChange={handlePriceChange}
          className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
        />
      </div>
      
      {filters.category === 'tariff' || filters.category === 'all' ? (
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Data Allowance
          </label>
          <select
            value={filters.dataAllowance}
            onChange={handleDataAllowanceChange}
            className="block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary focus:border-primary"
          >
            <option value="all">All Data Plans</option>
            <option value="1GB">1GB</option>
            <option value="5GB">5GB</option>
            <option value="10GB">10GB</option>
            <option value="Unlimited">Unlimited</option>
          </select>
        </div>
      ) : null}
      
      {filters.category === 'device' || filters.category === 'all' ? (
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Brand
          </label>
          <select
            value={filters.brand}
            onChange={handleBrandChange}
            className="block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary focus:border-primary"
          >
            <option value="all">All Brands</option>
            <option value="Apple">Apple</option>
            <option value="Samsung">Samsung</option>
            <option value="Google">Google</option>
            <option value="Huawei">Huawei</option>
          </select>
        </div>
      ) : null}
      
      <button
        onClick={handleReset}
        className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 px-4 rounded transition duration-300"
      >
        Reset Filters
      </button>
    </div>
  );
};

export default FilterPanel; 