package com.owais.kmeans.service;


import ai.onnxruntime.*;
import java.util.*;

public class SentenceEncoder {
    private OrtEnvironment env;
    private OrtSession session;

    public TextEncoder(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }

    public double[] encode(String text) throws OrtException {
        // Tokenize text using external tokenizer (must match training tokenizer)
        int[] inputIds = tokenize(text);
        long[] inputIdsLong = Arrays.stream(inputIds).asLongStream().toArray();
        long[] attentionMask = new long[inputIds.length];
        Arrays.fill(attentionMask, 1);

        // Prepare input
        OnnxTensor inputIdTensor = OnnxTensor.createTensor(env, new long[][]{inputIdsLong});
        OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(env, new long[][]{attentionMask});

        Map<String, OnnxTensor> inputs = Map.of(
            "input_ids", inputIdTensor,
            "attention_mask", attentionMaskTensor
        );

        // Run the model
        OrtSession.Result result = session.run(inputs);
        float[][] embeddings = (float[][]) result.get(0).getValue();

        // Convert float[] to double[]
        double[] doubleEmbeddings = new double[embeddings[0].length];
        for (int i = 0; i < embeddings[0].length; i++) {
            doubleEmbeddings[i] = embeddings[0][i];
        }

        return doubleEmbeddings;
    }

    private int[] tokenize(String text) {
        // Use the same tokenizer used to train the model (e.g., WordPiece or BPE)
        // You can call a Python microservice or use a Java tokenizer (like HuggingFace Tokenizers via JNI)
        throw new UnsupportedOperationException("Tokenizer not implemented");
    }
}
