from flask import Flask, request, jsonify
from PIL import Image
import numpy as np
import torch
import torch.nn as nn
import torchvision.models as models
from torchvision import transforms
import os

app = Flask(__name__)

# Define a simple neural network to reduce image features to a 3-dimensional vector
class ImageEmbeddingNet(nn.Module):
    def __init__(self, input_size, output_size):
        super(ImageEmbeddingNet, self).__init__()
        self.fc = nn.Linear(input_size, output_size)
        self.relu = nn.ReLU()

    def forward(self, x):
        x = x.view(x.size(0), -1)  # Flatten the input
        x = self.relu(self.fc(x))
        return x

# Initialize and load pre-trained AlexNet
alexnet = models.alexnet(pretrained=True)

# Modify AlexNet for image feature extraction
alexnet.classifier[6] = nn.Linear(4096, 9)  # Output size changed to 9 for a 1D array

# Set AlexNet to evaluation mode
alexnet.eval()

# Define transformations for incoming images
transform = transforms.Compose([
    transforms.Resize((224, 224)),  # Resize images to match AlexNet input size
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])  # Normalize image pixels
])

@app.route('/get_embedding', methods=['POST'])
def get_embedding():
    # Check if an image file is present in the request
    if 'image' not in request.files:
        return jsonify({'error': 'No image file provided'})

    # Read the image file from the request
    image_file = request.files['image']

    # Open the image file using PIL
    image = Image.open(image_file).convert("RGB")

    # Apply transformations to the image
    image_tensor = transform(image).unsqueeze(0)  # Add batch dimension

    # Generate the embedding for the image
    with torch.no_grad():
        output = alexnet(image_tensor)
        embedding = output.numpy().flatten().tolist()

    # Return the embedding as JSON response
    return jsonify({'embedding': embedding})

if __name__ == '__main__':
    app.run(debug=True)