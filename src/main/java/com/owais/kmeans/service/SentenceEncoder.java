package com.owais.kmeans.service;


import org.springframework.stereotype.Component;
import com.owais.kmeans.service.Tokenizer;
import java.nio.LongBuffer;
import java.util.*;

@Component
public class SentenceEncoder {
    private static final int EMBEDDING_SIZE = 128;

    public double[] encode(String text) {
        double[] vector = new double[EMBEDDING_SIZE];
        Arrays.fill(vector, 0.0);

        if (text == null || text.isEmpty()) return vector;

        String[] tokens = text.toLowerCase().split("\\s+");

        for (String token : tokens) {
            int hash = Math.abs(token.hashCode());
            int index = hash % EMBEDDING_SIZE;
            vector[index] += 1.0;
        }

        // Optional: Normalize the vector
        double norm = 0.0;
        for (double val : vector) norm += val * val;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }

        return vector;
    }
}