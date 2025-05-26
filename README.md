This component was originally designed for an App that some friends and I are working on called Gaya. It is basically a swipe based clothes shopping mobile app.
JavaCSVClusterEngine is the code I used for the recommendation facet. It is designed in Java Springboot and uses a special Kmeans++ algorithm I designed myself.

### Here is how it works:
1. A CSV is uploaded to the engine.
2. The products are converted to text embeddings using the all-MiniLM-L6-v2 model which is a wonderful memory efficient embedding network I stumbled upon.
3. According to the predefined number of Clusters, the most optimal centers are chosen using the kmeans++ algorithm.
4. Later on, KMeans is performed normally with a **Gower's distance** instead of the standard Euclidean distance.

### How to run the application:
1. Download and compile the application. My compiler was set to Java17 during development.
2. Using POST, upload a CSV to the **/upload**. Your CSV file must be in the format: prod_name,product_type_name,colour_group_name
5. Wait for the application to embed and cluster the products.
6. Your new CSV will be available for download in the format prod_name,product_type_name,colour_group_name,**cluster**
