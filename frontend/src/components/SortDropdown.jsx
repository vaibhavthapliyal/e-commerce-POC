import React from 'react';

const SortDropdown = ({ onSortChange, currentSort }) => {
  const handleChange = (e) => {
    onSortChange(e.target.value);
  };

  return (
    <div className="flex items-center">
      <label htmlFor="sort-select" className="mr-2 text-sm font-medium text-gray-700">
        Sort by:
      </label>
      <select
        id="sort-select"
        value={currentSort}
        onChange={handleChange}
        className="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-primary focus:border-primary"
      >
        <option value="popularity">Popularity</option>
        <option value="price-low-high">Price: Low to High</option>
        <option value="price-high-low">Price: High to Low</option>
        <option value="newest">Newest</option>
      </select>
    </div>
  );
};

export default SortDropdown; 