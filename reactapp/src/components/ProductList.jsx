import React, { useState, useEffect } from 'react';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await fetch('/products');
      
      if (!response.ok) {
        throw new Error('Failed to load products');
      }
      
      const data = await response.json();
      setProducts(data);
    } catch (err) {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>Loading products...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (products.length === 0) {
    return <div>No products available.</div>;
  }

  return (
    <div>
      <h2>Products</h2>
      <div>
        {products.map((product) => (
          <div key={product.id}>
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p>Quantity: {product.quantity}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProductList;