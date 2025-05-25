package com.owais.kmeans.service;


import ai.onnxruntime.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.LongBuffer;
import java.util.*;

@Component
public class SentenceEncoder {

    private OrtEnvironment env;
    private OrtSession session;

    @PostConstruct
    public void init() throws OrtException {
        env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        session = env.createSession("models/all-MiniLM-L6-v2.onnx", opts);
    }

    public float[] encode(String sentence) throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        Tokenizer.Input input = tokenizer.tokenize(sentence);

        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_ids", OnnxTensor.createTensor(env, input.inputIds));
        inputs.put("attention_mask", OnnxTensor.createTensor(env, input.attentionMask));

        OrtSession.Result result = session.run(inputs);
        float[][] tokenEmbeddings = (float[][]) result.get(0).getValue();

        // Compute mean pooling over token embeddings
        float[] avgEmbedding = meanPool(tokenEmbeddings, input.attentionMask[0]);

        result.close();
        return avgEmbedding;
    }

    private float[] meanPool(float[][] embeddings, long[] attentionMask) {
        int dim = embeddings[0].length;
        float[] sum = new float[dim];
        int count = 0;

        for (int i = 0; i < embeddings.length; i++) {
            if (attentionMask[i] == 1) {
                for (int j = 0; j < dim; j++) {
                    sum[j] += embeddings[i][j];
                }
                count++;
            }
        }

        for (int j = 0; j < dim; j++) {
            sum[j] /= count;
        }

        return sum;
    }
}
