import React, { useState } from 'react'
import AddProductForm from './components/AddProductForm'
import ProductList from './components/ProductList'

const App = () => {
  const [refreshKey, setRefreshKey] = useState(0);

  const handleProductAdded = () => {
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div>
        <AddProductForm onProductAdded={handleProductAdded} />
        <ProductList key={refreshKey} />
    </div>
  )
}

export default App