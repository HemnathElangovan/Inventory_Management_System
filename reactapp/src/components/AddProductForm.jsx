import React, { useState } from 'react';

const AddProductForm = ({ onProductAdded }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    quantity: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: formData.name,
          description: formData.description,
          quantity: parseInt(formData.quantity)
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to add product');
      }

      const newProduct = await response.json();
      
      // Clear form
      setFormData({
        name: '',
        description: '',
        quantity: ''
      });

      if (onProductAdded) {
        onProductAdded(newProduct);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Add Product</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Name</label>
          <input
            type="text"
            id="name"
            name="name"
            data-testid="name-input"
            value={formData.name}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label htmlFor="description">Description</label>
          <input
            type="text"
            id="description"
            name="description"
            data-testid="description-input"
            value={formData.description}
            onChange={handleInputChange}
          />
        </div>
        <div>
          <label htmlFor="quantity">Quantity</label>
          <input
            type="number"
            id="quantity"
            name="quantity"
            data-testid="quantity-input"
            value={formData.quantity}
            onChange={handleInputChange}
            required
          />
        </div>
        <button 
          type="submit" 
          data-testid="add-button"
          disabled={loading}
        >
          {loading ? 'Adding...' : 'Add Product'}
        </button>
      </form>
      {error && <p>{error}</p>}
    </div>
  );
};

export default AddProductForm;