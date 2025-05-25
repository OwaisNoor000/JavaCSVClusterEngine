package com.owais.kmeans.service;

import java.util.Arrays;

class Tokenizer {

    // Dummy BERT-style tokenizer for demonstration
    public Input tokenize(String sentence) {
        // Replace with a real tokenizer like HuggingFace tokenizer export
        long[] inputIds = new long[128];        // token IDs
        long[] attentionMask = new long[128];   // 1 where token is real

        Arrays.fill(inputIds, 101);  // [CLS] token id (dummy)
        Arrays.fill(attentionMask, 1);

        return new Input(new long[][]{inputIds}, new long[][]{attentionMask});
    }

    static class Input {
        long[][] inputIds;
        long[][] attentionMask;

        Input(long[][] inputIds, long[][] attentionMask) {
            this.inputIds = inputIds;
            this.attentionMask = attentionMask;
        }
    }
}
